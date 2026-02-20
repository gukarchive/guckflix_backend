package guckflix.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import guckflix.backend.dto.AiDto;
import guckflix.backend.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vectorstore.VectorStore;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiChatServiceTest {

    @Mock
    private ChatModel chatModel;

    @Mock
    private EmbeddingModel embeddingModel;

    @Mock
    private VectorStore vectorStore;

    @Mock
    private MovieRepository movieRepository;

    private AiChatService aiChatService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        aiChatService = new AiChatService(chatModel, embeddingModel, vectorStore, movieRepository, objectMapper);
    }

    @Test
    void analyze_parse_success() {
        // given
        String json = """
                {
                  "isMovieRecommendation": true,
                  "isSimilaritySearch": false,
                  "releaseDateFrom": "2026-01-01",
                  "releaseDateTo": "2026-02-01",
                  "genres": ["Drama", "Crime"],
                  "actors": ["Ryan Reynolds"],
                  "limit": 10
                }
                """;

        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse(json));

        AiDto.ChatRequest request = new AiDto.ChatRequest();
        request.setMessage("라라랜드 같은 영화 추천해줘");

        // when
        AiDto.SearchCondition condition = aiChatService.analyze(request);

        // then
        assertThat(condition).isNotNull();
        assertThat(condition.getIsMovieRecommendation()).isTrue();
        assertThat(condition.getIsSimilaritySearch()).isFalse();
        assertThat(condition.getReleaseDateFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(condition.getReleaseDateTo()).isEqualTo(LocalDate.of(2026, 2, 1));
        assertThat(condition.getGenres()).isEqualTo(List.of("Drama", "Crime"));
        assertThat(condition.getActors()).isEqualTo(List.of("Ryan Reynolds"));
        assertThat(condition.getLimit()).isEqualTo(10);
    }

    @Test
    void analyze_parse_fail_returns_null() {
        // given
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse("true"));

        AiDto.ChatRequest request = new AiDto.ChatRequest();
        request.setMessage("라라랜드 같은 영화 추천해줘");

        // when
        AiDto.SearchCondition condition = aiChatService.analyze(request);

        // then
        assertThat(condition).isNull();
    }

    @Test
    void embed_blank_returns_empty_vector() {
        // when
        float[] vector = aiChatService.embed("   ");

        // then
        assertThat(vector).isEmpty();
    }

    private ChatResponse mockChatResponse(String text) {
        AssistantMessage output = new AssistantMessage(text);
        Generation generation = new Generation(output);
        return new ChatResponse(List.of(generation));
    }
}
