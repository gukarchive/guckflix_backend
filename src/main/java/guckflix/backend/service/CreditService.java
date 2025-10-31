package guckflix.backend.service;

import guckflix.backend.dto.ActorDto;
import guckflix.backend.dto.CreditDto;
import guckflix.backend.dto.CreditDto.Response;
import guckflix.backend.entity.Actor;
import guckflix.backend.entity.Credit;
import guckflix.backend.entity.Movie;
import guckflix.backend.exception.NotAllowedIdException;
import guckflix.backend.repository.ActorRepository;
import guckflix.backend.repository.CreditRepository;
import guckflix.backend.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;
    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;

    public List<Response> findActors(Long movieId) {
        return creditRepository.findByMovieId(movieId).stream()
                .map((entity)-> new Response(entity))
                .collect(Collectors.toList());
    }

    @Transactional
    public ActorDto.Response.CreditWithMovieInfo addCredit(Long movieId, CreditDto.Post form) {

        Actor actor = actorRepository.findById(form.getActorId());
        Movie movie = movieRepository.findById(movieId);

        List<Credit> movieCredits = creditRepository.findByMovieId(movie.getId());
        int maxOrder = 0;
        for (Credit credit : movieCredits) {
            int order = credit.getOrder();
            if (order > maxOrder) {
                maxOrder = order;
            }
        }
        maxOrder = ++maxOrder;

        Credit credit = Credit.builder().order(maxOrder).casting(form.getCasting()).build();

        credit.changeActor(actor);
        credit.changeMovie(movie);

        creditRepository.save(credit);

        return new ActorDto.Response.CreditWithMovieInfo(credit);
    }

    @Transactional
    public void deleteCredit(Long movieId, Long creditId) {
        Credit credit = creditRepository.findById(creditId);

        if(credit.getMovie().getId().equals(movieId)) {
            creditRepository.remove(credit);
            credit.changeMovie(null);
            credit.changeActor(null);
        } else {
            throw new NotAllowedIdException("requested others resource. not allowed");
        }
    }

    @Transactional
    public void updateCredit(Long movieId, Long creditId, CreditDto.Patch form) {
        Credit credit = creditRepository.findById(creditId);

        if(credit.getMovie().getId().equals(movieId)) {
            credit.update(form.getCasting());
        } else {
            throw new NotAllowedIdException("requested others resource. not allowed");
        }
    }

//    public void save(List<CreditDto.Post> cForm) {
//        cForm.stream().forEach((form)->
//                        creditRepository.save(
//            Credit.builder()
//                    .actor(actorRepository.findById(form.getActorId()))
//                    .movie(movieRepository.findById(form.getMovieId()))
//                    .casting(form.getCasting())
//                    .order(form.getOrder()).build()
//                        );
//        );
//    }
//
//    public void update(List<CreditDto.Patch> uForm){
//
//        for (CreditDto.Patch form : uForm) {
//            Credit credit = creditRepository.findById(form.getActorId());
//            credit.update(form.getOrder(), form.getCasting());
//        }
//    }
//
//    public void delete(Long creditId) {
//        Credit credit = creditRepository.findById(creditId);
//        creditRepository.remove(credit);
//    }
}
