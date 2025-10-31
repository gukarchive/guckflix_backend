package guckflix.backend.service;

import guckflix.backend.dto.ActorDto;
import guckflix.backend.dto.GenreDto;
import guckflix.backend.dto.MovieDto;
import guckflix.backend.entity.Genre;
import guckflix.backend.exception.NotFoundException;
import guckflix.backend.repository.GenreRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ActorServiceTest {

    @Autowired ActorService actorService;
    @Autowired MovieService movieService;

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    EntityManager entityManager;

    Long savedMovieId1;
    Long savedMovieId2;
    Long savedMovieId3;
    Long savedMovieId4;


    @BeforeEach
    public void beforeEach() {

        MovieDto.Post movie = new MovieDto.Post();
        movie.setTitle("쇼생크탈출");
        movie.setOverview("감옥에 억울하게 ...");
        movie.setReleaseDate(LocalDate.of(1945,11,26));
        movie.setGenres(List.of(new GenreDto(14L, "Fantasy")));
        movie.setCredits(new ArrayList<>());
        MovieDto.Post movie2 = new MovieDto.Post();
        movie2.setTitle("어벤저스");
        movie2.setOverview("히어로들이 ...");
        movie2.setReleaseDate(LocalDate.of(2021,11,26));
        movie2.setGenres(List.of(new GenreDto(14L, "Fantasy")));
        movie2.setCredits(new ArrayList<>());
        MovieDto.Post movie3 = new MovieDto.Post();
        movie3.setTitle("대부");
        movie3.setOverview("신참 마피아 ...");
        movie3.setReleaseDate(LocalDate.of(1966,11,26));
        movie3.setGenres(List.of(new GenreDto(14L, "Fantasy")));
        movie3.setCredits(new ArrayList<>());
        MovieDto.Post movie4 = new MovieDto.Post();
        movie4.setTitle("이퀼리브리엄");
        movie4.setOverview("불법 색출 ...");
        movie4.setReleaseDate(LocalDate.of(2006,5,26));
        movie4.setGenres(List.of(new GenreDto(14L, "Fantasy")));
        movie4.setCredits(new ArrayList<>());


        savedMovieId1 = movieService.save(movie);
        savedMovieId2 = movieService.save(movie2);
        savedMovieId3 = movieService.save(movie3);
        savedMovieId4 = movieService.save(movie4);
    }

    @AfterEach
    public void afterEach() {
        movieService.delete(savedMovieId1);
        movieService.delete(savedMovieId2);
        movieService.delete(savedMovieId3);
        movieService.delete(savedMovieId4);
    }

    @Test
    public void saveTest() throws Exception{

        ActorDto.Post form = new ActorDto.Post();
        form.setBirthDay(LocalDate.of(1994,11,26));
        form.setName("gukjin");
        form.setBiography("1994년 대구에서 출생한 ....");
        form.setCredits(Arrays.asList(
                new ActorDto.Post.ActorPostCredit(savedMovieId1, "수감자1"),
                new ActorDto.Post.ActorPostCredit(savedMovieId2, "국장"),
                new ActorDto.Post.ActorPostCredit(savedMovieId3, "주인공")
        ));

        ActorDto.Post form2 = new ActorDto.Post();
        form2.setBirthDay(LocalDate.of(1954,7,30));
        form2.setName("hwan");
        form2.setBiography("1954년 서울에서 출생한 ....");
        form2.setCredits(Arrays.asList(
                new ActorDto.Post.ActorPostCredit(savedMovieId1, "수감자1")
        ));

        Long savedId = actorService.save(form);
        entityManager.flush();
        entityManager.clear();
        Long savedId2 = actorService.save(form2);
        entityManager.flush();
        entityManager.clear();

        ActorDto.Response gukjin = actorService.findDetail(savedId);
        ActorDto.Response hwan = actorService.findDetail(savedId2);

        assertThat(gukjin.getName()).isEqualTo("gukjin");
        assertThat(hwan.getCredits().get(0).getOrder()).isEqualTo(1);
        System.out.println("hwan = " + hwan);
        System.out.println("hwan.getCredits() = " + hwan.getCredits());

    }

    @Test
    public void delete_test() throws Exception{

        ActorDto.Post form = new ActorDto.Post();
        form.setBirthDay(LocalDate.of(1994,11,26));
        form.setName("gukjin");
        form.setBiography("1994년 대구에서 출생한 ....");
        form.setCredits(Arrays.asList(
                new ActorDto.Post.ActorPostCredit(savedMovieId1, "수감자1"),
                new ActorDto.Post.ActorPostCredit(savedMovieId2, "국장"),
                new ActorDto.Post.ActorPostCredit(savedMovieId3, "주인공")
        ));

        Long savedId1 = actorService.save(form);

        entityManager.flush();
        entityManager.clear();

        actorService.delete(savedId1);

        assertThatThrownBy(()->actorService.findDetail(savedId1))
                .isInstanceOf(NotFoundException.class);

    }


    @Test
    public void updateTest() throws Exception{
        //given


        //when

        //then

    }
}