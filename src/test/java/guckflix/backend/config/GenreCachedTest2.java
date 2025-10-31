package guckflix.backend.config;

import guckflix.backend.entity.Genre;
import guckflix.backend.repository.GenreRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;


@ExtendWith(SpringExtension.class)
@Import({GenreCached.class, GenreRepository.class})
class GenreCachedTest2 {

    @MockBean GenreRepository genreRepository;

    @Autowired GenreCached genreCached;

    @BeforeEach
    public void init() {
        Mockito.when(genreRepository.findAll()).thenReturn(List.of(Genre.builder().genreName("Action").id(0L).build()));
    }

    @Test
    public void immutableTest() throws Exception {

        List<Genre> all = genreRepository.findAll();
        Map<Long, Genre> genres = GenreCached.getGenres();
        System.out.println("genres = " + all.size());
        System.out.println("genres = " + genres);
        Assertions.assertThatThrownBy(()->genres.put(100L, Genre.builder().genreName("테스트용장르").id(100L).build()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void immutableTestAndCannotInsert() throws Exception {
        Map<Long, Genre> genres = GenreCached.getGenres();
        Assertions.assertThatThrownBy(()->genres.put(100L, Genre.builder().genreName("테스트용장르").id(100L).build()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

}