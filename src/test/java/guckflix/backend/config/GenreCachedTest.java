package guckflix.backend.config;

import guckflix.backend.entity.Genre;
import guckflix.backend.repository.GenreRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import java.util.*;


class GenreCachedTest {

    private static ApplicationContext context;
    private static GenreCached genreCached;

    @BeforeAll
    public static void init() {
        context = new AnnotationConfigApplicationContext(TestConfig.class);
        genreCached = context.getBean("genreCached", GenreCached.class);
    }

    @Configuration
    static class TestConfig {
        @Bean
        public GenreRepository genreRepository() {
            GenreRepository mock = Mockito.mock(GenreRepository.class);
            Mockito.when(mock.findAll()).thenReturn(List.of(Genre.builder().genreName("Action").id(0L).build()));
            return mock;
        }

        @Bean GenreCached genreCached() {
            return new GenreCached(genreRepository());
        }

        @Bean
        public EntityManager entityManager() {
            return Mockito.mock(EntityManager.class);
        }
    }

    @Test
    public void immutableTest() throws Exception {
        Map<Long, Genre> genres = genreCached.getGenres();
        System.out.println("genres = " + genres.get(0L).getGenreName());
        Assertions.assertThatThrownBy(()->genres.put(100L, Genre.builder().genreName("테스트용장르").id(100L).build()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void immutableTestAndCannotInsert() throws Exception {
        Map<Long, Genre> genres = genreCached.getGenres();
        List<Object> list = new ArrayList<>(genres.values());
        Assertions.assertThatThrownBy(()->genres.put(100L, Genre.builder().genreName("테스트용장르").id(100L).build()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

}