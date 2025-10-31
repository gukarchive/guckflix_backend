package guckflix.backend.service;

import guckflix.backend.dto.ReviewDto.Post;
import guckflix.backend.dto.ReviewDto.Response;
import guckflix.backend.dto.paging.PagingFactory;
import guckflix.backend.dto.paging.PagingRequest;
import guckflix.backend.dto.paging.Paging;
import guckflix.backend.entity.Movie;
import guckflix.backend.entity.Review;
import guckflix.backend.exception.NotAllowedIdException;
import guckflix.backend.repository.MovieRepository;
import guckflix.backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;

    public Paging<Response> findAllById(Long movieId, PagingRequest pagingRequest) {
        Paging<Review> reviews = reviewRepository.findByMovieId(movieId, pagingRequest);
        List<Response> dtos = reviews.getList().stream().map((entity) -> new Response(entity)).collect(Collectors.toList());
        return PagingFactory.newPaging(reviews, dtos);
    }

    public Response findById(Long reviewId){
        Review entity = reviewRepository.findById(reviewId);
        return new Response(entity);
    }

    @Transactional
    public Long save(Post dto){
        Long reviewId = reviewRepository.save(dtoToEntity(dto));
        Movie movie = movieRepository.findById(dto.getMovieId());
        movie.updateVoteAdd(dto.getVoteRating());
        return reviewId;
    }

    @Transactional
    public void delete(Long reviewId, Long movieId, Long userId){
        Review review = reviewRepository.findById(reviewId);
        if(!review.getUserId().equals(userId)) throw new NotAllowedIdException("Id doesn't matches");
        reviewRepository.remove(review);
        Movie movie = movieRepository.findById(movieId);
        movie.updateVoteDelete(review.getVoteRating());

    }

    private Review dtoToEntity(Post dto){
        Review entity = Review.builder()
                .userId(dto.getUserId())
                .content(dto.getContent())
                .voteRating(dto.getVoteRating())
                .movieId(dto.getMovieId())
                .build();
        return entity;
    }

}