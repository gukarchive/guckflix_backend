package guckflix.backend.controller;

import guckflix.backend.dto.ActorDto;
import guckflix.backend.dto.CreditDto;
import guckflix.backend.dto.GenreDto;
import guckflix.backend.dto.MovieDto;
import guckflix.backend.entity.Genre;
import guckflix.backend.repository.GenreRepository;
import guckflix.backend.service.ActorService;
import guckflix.backend.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
class ActorControllerTest {

    @Autowired MockMvc mockMvc;

    @Autowired ActorService actorService;
    @Autowired MovieService movieService;

    @Autowired
    GenreRepository genreRepository;

    @Test
    @WithMockUser
    @Transactional
    public void actorController_get_test() throws Exception{

        String genreName = "Fantasy";
        Genre genre = Genre.builder().genreName(genreName).build();
        Long savedGenreId = genreRepository.save(genre);

        ActorDto.Post actorPost = new ActorDto.Post();
        actorPost.setName("국진");
        actorPost.setCredits(new ArrayList<>());
        Long savedId = actorService.save(actorPost);

        MovieDto.Post movie = new MovieDto.Post();
        movie.setTitle("쇼생크탈출");
        movie.setOverview("감옥에 억울하게 ...");
        movie.setReleaseDate(LocalDate.of(1945,11,26));
        movie.setGenres(List.of(new GenreDto(savedGenreId, genreName)));
        movie.setCredits(List.of(new CreditDto.Post(savedId, "수감자")));

        movieService.save(movie);


        mockMvc.perform(
                MockMvcRequestBuilders.get("/actors/"+savedId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedId))
                .andExpect(jsonPath("$.name").value("국진"));

    }
}