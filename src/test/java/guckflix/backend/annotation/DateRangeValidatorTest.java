package guckflix.backend.annotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import guckflix.backend.dto.CreditDto;
import guckflix.backend.dto.GenreDto;
import guckflix.backend.dto.MovieDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
class DateRangeValidatorTest {

    @Autowired private Validator validator;

    MovieDto.Post post;

    @Autowired MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach(){
        post = new MovieDto.Post();
        post.setGenres(List.of(new GenreDto(1L, "액션"), new GenreDto(2L, "판타지")));
        post.setOverview("대략적인 영화 소개 정보");
        post.setTitle("테스트 영화 제목");
        post.setCredits(List.of(new CreditDto.Post(1L, "김씨"), new CreditDto.Post(2L, "박씨")));
    }

    @Test
    @DisplayName("MovieDto.Post.releaseDate의 @DateRange 값 범위보다 높으므로 실패 예상")
    void expectFailByOverDate() {

        post.setReleaseDate(LocalDate.of(2555,1,1));
        Set<ConstraintViolation<MovieDto.Post>> validate = validator.validate(post);

        List<String> errorCodes = validate.stream()
                .map(violation -> violation.getMessageTemplate())
                .collect(Collectors.toList());

        assertThat(errorCodes).contains("{DateRange}");

    }

    @Test
    @DisplayName("정상 범위 값")
    void expectSuccess() {

        post.setReleaseDate(LocalDate.of(1985,11,11));
        Set<ConstraintViolation<MovieDto.Post>> validate = validator.validate(post);

        List<String> errorCodes = validate.stream()
                .map(violation -> violation.getMessageTemplate())
                .collect(Collectors.toList());

        assertThat(errorCodes).doesNotContain("{DateRange}");
    }

    @Test
    @DisplayName("MovieDto.Post.releaseDate의 @DateRange 값 범위보다 낮으므로 실패 예상")
    void expectFailByUnderDate() {

        MovieDto.Post post = new MovieDto.Post();
        post.setReleaseDate(LocalDate.of(1899,1,1));
        Set<ConstraintViolation<MovieDto.Post>> validate = validator.validate(post);

        List<String> errorCodes = validate.stream()
                .map(violation -> violation.getMessageTemplate())
                .collect(Collectors.toList());

        assertThat(errorCodes).contains("{DateRange}");
    }

    @Test
    @DisplayName("컨트롤러 성공 검증")
    @WithMockUser(roles = {"ADMIN"})
    void expectSuccessController() throws Exception {

        post.setReleaseDate(LocalDate.of(1994, 11, 26));

        MockMultipartFile file = new MockMultipartFile("originFile", new byte[1]);
        MockMultipartFile file2 = new MockMultipartFile("w500File", new byte[1]);
        MockMultipartFile form = new MockMultipartFile("form", null, MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(post).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
                multipart("/movies", HttpMethod.POST)
                        .file(file2)
                        .file(form)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

    }

    @Test
    @DisplayName("컨트롤러 실패 검증")
    @WithMockUser(roles = {"ADMIN"})
    void expectFailController() throws Exception {

        post.setReleaseDate(LocalDate.of(2505, 11, 26));

        MockMultipartFile file = new MockMultipartFile("originFile", new byte[1]);
        MockMultipartFile file2 = new MockMultipartFile("w500File", new byte[1]);
        MockMultipartFile form = new MockMultipartFile("form", null, MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(post).getBytes(StandardCharsets.UTF_8));

        MvcResult mvcResult = mockMvc.perform(
                multipart("/movies", HttpMethod.POST)
                        .file(file2)
                        .file(form)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        String content = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("content = " + content);
        assertThat(content).contains("\"field\":\"releaseDate\"");

    }

}