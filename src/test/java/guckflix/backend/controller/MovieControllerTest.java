package guckflix.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guckflix.backend.dto.CreditDto;
import guckflix.backend.dto.GenreDto;
import guckflix.backend.dto.MovieDto;
import guckflix.backend.entity.Actor;
import guckflix.backend.repository.ActorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired ActorRepository actorRepository;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Rollback(value = false)
    @Transactional
    void post() throws Exception {

        MovieDto.Post post = new MovieDto.Post();
        post.setGenres(List.of(new GenreDto(1L, "액션")));
        post.setTitle("테스트 영화 제목");
        post.setReleaseDate(LocalDate.now());
        post.setOverview("zzzzzzzzzzzzzzz");

        actorRepository.save(Actor.builder()
                        .name("김씨")
                        .biography("전기")
                        .birthDay(LocalDate.now())
                        .deathDay(LocalDate.now())
                        .credits(new ArrayList<>())
                        .build());

        CreditDto.Post credit = new CreditDto.Post();
        credit.setActorId(1L);
        credit.setCasting("판토마임");

        post.setCredits(List.of(credit));

        MockMultipartFile form = new MockMultipartFile("form", null, "application/json", objectMapper.writeValueAsString(post).getBytes(StandardCharsets.UTF_8));
        MockMultipartFile w500File = new MockMultipartFile("w500File", new byte[1]);
        MockMultipartFile originFile = new MockMultipartFile("originFile", new byte[1]);

        mockMvc.perform(
                multipart(HttpMethod.POST, "/movies")
                        .file(form)
                        .file(w500File)
                        .file(originFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("location"));

    }
}