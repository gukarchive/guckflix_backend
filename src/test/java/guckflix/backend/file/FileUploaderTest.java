package guckflix.backend.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import guckflix.backend.dto.CreditDto;
import guckflix.backend.dto.GenreDto;
import guckflix.backend.dto.MovieDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
public class FileUploaderTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testFormDataWithImage() throws Exception {

        MovieDto.Post postForm = new MovieDto.Post();
        postForm.setTitle("Test Movie");
        postForm.setOverview("This is a test movie");
        postForm.setGenres(Arrays.asList(new GenreDto(1L, "Action"), new GenreDto(2L, "Action")));
        postForm.setReleaseDate(LocalDate.now());
        postForm.setCredits(List.of(new CreditDto.Post(1L, "Test casting")));
        String dtoJson = objectMapper.writeValueAsString(postForm);

        // Multipart 데이터 생성
        MockMultipartFile originFile = new MockMultipartFile("originFile", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test data".getBytes());
        MockMultipartFile w500File = new MockMultipartFile("w500File", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test data".getBytes());
        MockMultipartFile json = new MockMultipartFile("form", "form", "application/json", dtoJson.getBytes(StandardCharsets.UTF_8));

        File beforeDir = new File(FileConst.IMAGE_DIRECTORY_ROOT + "/" + FileConst.DIRECTORY_ORIGINAL);
        File[] beforeFiles = beforeDir.listFiles();
        int beforeFileLength = beforeFiles.length;

        // MockMvc로 요청 전송
        mockMvc.perform(MockMvcRequestBuilders.multipart("/movies")
                        .file(originFile)
                        .file(w500File)
                        .file(json)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        File afterDir = new File(FileConst.IMAGE_DIRECTORY_ROOT + "/" + FileConst.DIRECTORY_ORIGINAL);
        File[] afterFiles = afterDir.listFiles();
        int afterFileLength = afterFiles.length;

        System.out.println("beforeFileLength = " + beforeFileLength);
        System.out.println("afterFileLength = " + afterFileLength);
        Assertions.assertThat(beforeFileLength).isEqualTo(afterFileLength-1);
    }
}