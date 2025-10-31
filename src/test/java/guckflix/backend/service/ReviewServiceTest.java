package guckflix.backend.service;

import guckflix.backend.config.GenreCached;
import guckflix.backend.dto.GenreDto;
import guckflix.backend.dto.MovieDto;
import guckflix.backend.dto.ReviewDto;
import guckflix.backend.entity.Genre;
import guckflix.backend.exception.NotAllowedIdException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReviewServiceTest {

    @Autowired ReviewService reviewService;
    @Autowired MovieService movieService;

    @Autowired EntityManager em;

    @Test
    @Transactional
    public void review_add() throws Exception{
        MovieDto.Post movieDto = new MovieDto.Post();
        movieDto.setTitle("test");
        movieDto.setReleaseDate(LocalDate.parse("2023-11-23"));
        movieDto.setGenres(List.of(new GenreDto(14L, "Fantasy")));
        movieDto.setCredits(new ArrayList<>());

        Long savedMovieId = movieService.save(movieDto);

        ReviewDto.Post reviewDto = new ReviewDto.Post();
        reviewDto.setMovieId(savedMovieId);
        reviewDto.setUserId(115L);
        reviewDto.setVoteRating(5);
        reviewService.save(reviewDto);

        MovieDto.Response findMovie = movieService.findById(savedMovieId);
        assertThat(findMovie.getVoteAverage()).isEqualTo(5f);

        ReviewDto.Post reviewDto2 = new ReviewDto.Post();
        reviewDto2.setMovieId(savedMovieId);
        reviewDto2.setUserId(115L);
        reviewDto2.setVoteRating(4);
        reviewService.save(reviewDto2);

        MovieDto.Response findMovie2 = movieService.findById(savedMovieId);
        assertThat(findMovie2.getVoteCount()).isEqualTo(2);
        assertThat(findMovie2.getVoteAverage()).isEqualTo(4.5f);

    }

    @Test
    @Transactional
    public void review_minus() throws Exception{

        MovieDto.Post movieDto = new MovieDto.Post();
        movieDto.setTitle("test");
        movieDto.setGenres(List.of(new GenreDto(14L, "Fantasy")));
        movieDto.setCredits(new ArrayList<>());
        Long savedMovieId = movieService.save(movieDto);

        ReviewDto.Post reviewDto = new ReviewDto.Post();
        reviewDto.setMovieId(savedMovieId);
        reviewDto.setVoteRating(5);
        reviewDto.setUserId(115L);
        Long reviewId = reviewService.save(reviewDto);

        ReviewDto.Post reviewDto2 = new ReviewDto.Post();
        reviewDto2.setMovieId(savedMovieId);
        reviewDto2.setVoteRating(4);
        reviewDto2.setUserId(115L);
        Long reviewId2 = reviewService.save(reviewDto2);

        MovieDto.Response findMovie1 = movieService.findById(savedMovieId);

        assertThat(findMovie1.getVoteAverage()).isEqualTo(4.5f);
        assertThat(findMovie1.getVoteCount()).isEqualTo(2);

        reviewService.delete(reviewId2, savedMovieId, 115L);

        MovieDto.Response findMovie2 = movieService.findById(savedMovieId);

        assertThat(findMovie2.getVoteAverage()).isEqualTo(5f);
        assertThat(findMovie2.getVoteCount()).isEqualTo(1);

    }

    @Test
    @Transactional
    public void review_minus_notAllowedId() throws Exception{

        MovieDto.Post movieDto = new MovieDto.Post();
        movieDto.setTitle("test");
        movieDto.setGenres(List.of(new GenreDto(14L, "Fantasy")));
        movieDto.setCredits(new ArrayList<>());
        Long savedMovieId = movieService.save(movieDto);

        ReviewDto.Post reviewDto = new ReviewDto.Post();
        reviewDto.setMovieId(savedMovieId);
        reviewDto.setVoteRating(4);
        reviewDto.setUserId(115L);
        Long reviewId = reviewService.save(reviewDto);

        Assertions.assertThatThrownBy(()-> reviewService.delete(reviewId, savedMovieId, 119L))
                .isInstanceOf(NotAllowedIdException.class);

    }
}