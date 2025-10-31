package guckflix.backend.service;

import guckflix.backend.config.GenreCached;
import guckflix.backend.dto.CreditDto;
import guckflix.backend.dto.GenreDto;
import guckflix.backend.dto.MovieDto;
import guckflix.backend.entity.*;
import guckflix.backend.repository.ActorRepository;
import guckflix.backend.repository.CreditRepository;
import guckflix.backend.repository.GenreRepository;
import guckflix.backend.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MovieServiceTest {


    @Autowired
    EntityManager em;

    @Autowired
    MovieService movieService;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    ActorRepository actorRepository;

    @Autowired CreditRepository creditRepository;

    @Autowired
    GenreRepository genreRepository;


    @Test
    @Transactional
    public void updateTest() throws Exception {

        Actor actor1 = Actor.builder().name("김씨").biography("잘생겼다").credits(new ArrayList<>()).build();
        Actor actor2 = Actor.builder().name("박씨").biography("못생겼다").credits(new ArrayList<>()).build();
        Actor actor3 = Actor.builder().name("황씨").biography("그럭저럭 생겼다").credits(new ArrayList<>()).build();

        GenreDto dto = new GenreDto(1L, "Action");

        List<Genre> genres = genreRepository.findAll();
        List<MovieGenre> movieGenres = new ArrayList<>();

        Movie movie = Movie.builder().title("Test Movie")
                .overview("테스트 영화")
                .movieGenres(movieGenres)
                .backdropPath("uuid1.jpg")
                .posterPath("uuid2.jpg")
                .credits(new ArrayList<>())
                .releaseDate(LocalDate.now())
                .build();

        for (Genre genre : genres) {
            if(genre.getGenreName().equals(dto.getGenre()) && genre.getId().equals(dto.getId())){
                MovieGenre movieGenre = MovieGenre.builder().genre(genre).movie(null).build();
                movieGenre.changeMovie(movie);
                movieGenres.add(movieGenre);
            }
        }

        Credit credit = Credit.builder()
                .movie(movie)
                .actor(actor1)
                .casting("쓰레기통")
                .order(0)
                .build();
        actor1.getCredits().add(credit);
        movie.getCredits().add(credit);

        movieRepository.save(movie);
        actorRepository.save(actor1);
        actorRepository.save(actor2);
        actorRepository.save(actor3);
        creditRepository.save(credit);

        List<CreditDto.Post> creditPostForm = Arrays.asList(
                new CreditDto.Post(actor1.getId(), "더덕"),
                new CreditDto.Post(actor2.getId(), "감자"),
                new CreditDto.Post(actor3.getId(), "고구마")
        );

        MovieDto.Update movieUpdateForm = new MovieDto.Update();
        movieUpdateForm.setPosterPath("포스터바뀌었나요??.jpg");
        movieUpdateForm.setBackdropPath("백그라운드바뀌었나요??.jpg");
        movieUpdateForm.setGenres(Arrays.asList(new GenreDto(2L, "Genre Changed")));
        movieUpdateForm.setReleaseDate(LocalDate.of(1982,7,13));
        movieUpdateForm.setCredits(creditPostForm);

        movieService.update(movieUpdateForm, movie.getId());

        em.flush();
        em.clear();

        Movie findMovie = movieRepository.findById(movie.getId());

        assertThat(findMovie.getCredits().size()).isEqualTo(3);
        assertThat(findMovie.getReleaseDate()).isEqualTo(LocalDate.of(1982, 7, 13));

    }

    @Test
    @Transactional
    public void delete_test() throws Exception{
        Actor actor1 = Actor.builder().name("김씨").biography("잘생겼다").credits(new ArrayList<>()).build();
        Actor actor2 = Actor.builder().name("박씨").biography("못생겼다").credits(new ArrayList<>()).build();

        Long savedActor1Id = actorRepository.save(actor1);
        Long savedActor2Id = actorRepository.save(actor2);

        MovieDto.Post movieDto = new MovieDto.Post();
        movieDto.setTitle("테스트 영화");
        movieDto.setOverview("재미있음");
        movieDto.setGenres(Arrays.asList(new GenreDto(1L, "Adventure")));
        movieDto.setCredits(new ArrayList<>(List.of(new CreditDto.Post(savedActor1Id, "김"))));
        movieDto.setCredits(new ArrayList<>(List.of(new CreditDto.Post(savedActor2Id, "박"))));

        Long savedId = movieService.save(movieDto);
        Movie movie = movieRepository.findById(savedId);

        Credit credit = Credit.builder()
                .movie(movie)
                .actor(actor1)
                .casting("김씨 역")
                .order(0)
                .build();
        Credit credit2 = Credit.builder()
                .movie(movie)
                .actor(actor2)
                .casting("박씨 역")
                .order(1)
                .build();

        movie.getCredits().add(credit);
        movie.getCredits().add(credit2);

        creditRepository.save(credit);
        creditRepository.save(credit2);

        movieService.delete(movie.getId());

        assertThatThrownBy(()-> movieService.findById(movie.getId()))
                .isInstanceOf(RuntimeException.class);

    }


}