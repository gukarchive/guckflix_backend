package guckflix.backend.event;

import guckflix.backend.dto.CreditDto;
import guckflix.backend.dto.GenreDto;
import guckflix.backend.dto.MovieDto;
import guckflix.backend.entity.Actor;
import guckflix.backend.entity.Genre;
import guckflix.backend.repository.ActorRepository;
import guckflix.backend.repository.CreditRepository;
import guckflix.backend.repository.GenreRepository;
import guckflix.backend.repository.MovieRepository;
import guckflix.backend.service.AiChatService;
import guckflix.backend.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.transaction.TestTransaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;

@DataJpaTest
@Import({
        MovieService.class,
        MovieRepository.class,
        CreditRepository.class,
        ActorRepository.class,
        GenreRepository.class
})
class MovieServiceEventListnerIntegrationTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @MockBean
    private AiChatService aiChatService;

    @MockBean
    private MovieServiceEventListner movieServiceEventListner;

    @Test
    @Transactional
    void save_커밋후_이벤트리스너가_upsert를_호출한다() {
        Long actorId = actorRepository.save(
                Actor.builder()
                        .name("테스트 배우")
                        .biography("테스트")
                        .credits(new ArrayList<>())
                        .build()
        );
        Long genreId = genreRepository.save(Genre.builder().genreName("Action").build());

        MovieDto.Post post = new MovieDto.Post();
        post.setTitle("이벤트 리스너 테스트 영화");
        post.setOverview("테스트 개요");
        post.setBackdropPath("origin.jpg");
        post.setPosterPath("w500.jpg");
        post.setReleaseDate(LocalDate.of(2026, 2, 25));
        post.setGenres(List.of(new GenreDto(genreId, "Action")));
        post.setCredits(List.of(new CreditDto.Post(actorId, "주연")));

        Long movieId = movieService.save(post);
        TestTransaction.flagForCommit();
        TestTransaction.end();

        verify(movieServiceEventListner).onMovieSavedOrUpdated(new MovieServiceEventListner.MovieSavedOrUpdatedEvent(movieId));
    }

    @Test
    @Transactional
    void update_커밋후_이벤트리스너가_onMovieUpdated를_호출한다() {
        Long actorId = actorRepository.save(
                Actor.builder()
                        .name("업데이트 테스트 배우1")
                        .biography("테스트")
                        .credits(new ArrayList<>())
                        .build()
        );
        Long actorId2 = actorRepository.save(
                Actor.builder()
                        .name("업데이트 테스트 배우2")
                        .biography("테스트")
                        .credits(new ArrayList<>())
                        .build()
        );
        Long genreId = genreRepository.save(Genre.builder().genreName("Action").build());
        Long genreId2 = genreRepository.save(Genre.builder().genreName("Drama").build());

        MovieDto.Post post = new MovieDto.Post();
        post.setTitle("업데이트 이벤트 리스너 테스트 영화");
        post.setOverview("기존 개요");
        post.setBackdropPath("origin_u.jpg");
        post.setPosterPath("w500_u.jpg");
        post.setReleaseDate(LocalDate.of(2026, 2, 25));
        post.setGenres(List.of(new GenreDto(genreId, "Action")));
        post.setCredits(List.of(new CreditDto.Post(actorId, "주연")));

        Long movieId = movieService.save(post);
        TestTransaction.flagForCommit();
        TestTransaction.end();
        clearInvocations(movieServiceEventListner);

        MovieDto.Update update = new MovieDto.Update();
        update.setTitle("업데이트된 제목");
        update.setOverview("업데이트된 개요");
        update.setBackdropPath("origin_u2.jpg");
        update.setPosterPath("w500_u2.jpg");
        update.setReleaseDate(LocalDate.of(2026, 2, 24));
        update.setGenres(List.of(new GenreDto(genreId2, "Drama")));
        update.setCredits(List.of(new CreditDto.Post(actorId2, "조연")));

        movieService.update(update, movieId);

        verify(movieServiceEventListner).onMovieSavedOrUpdated(new MovieServiceEventListner.MovieSavedOrUpdatedEvent(movieId));
    }

    @Test
    @Transactional
    void delete_커밋후_이벤트리스너가_delete를_호출한다() {
        Long actorId = actorRepository.save(
                Actor.builder()
                        .name("삭제 테스트 배우")
                        .biography("테스트")
                        .credits(new ArrayList<>())
                        .build()
        );
        Long genreId = genreRepository.save(Genre.builder().genreName("Thriller").build());

        MovieDto.Post post = new MovieDto.Post();
        post.setTitle("삭제 이벤트 리스너 테스트 영화");
        post.setOverview("테스트 개요");
        post.setBackdropPath("origin2.jpg");
        post.setPosterPath("w500_2.jpg");
        post.setReleaseDate(LocalDate.of(2026, 2, 25));
        post.setGenres(List.of(new GenreDto(genreId, "Thriller")));
        post.setCredits(List.of(new CreditDto.Post(actorId, "조연")));

        Long movieId = movieService.save(post);
        TestTransaction.flagForCommit();
        TestTransaction.end();
        clearInvocations(movieServiceEventListner);

        movieService.delete(movieId);

        verify(movieServiceEventListner).onMovieDeleted(new MovieServiceEventListner.MovieDeletedEvent(movieId));
    }
}
