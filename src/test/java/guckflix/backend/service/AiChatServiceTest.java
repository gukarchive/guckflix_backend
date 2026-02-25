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
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiChatServiceTest {

    @Mock
    private ChatModel chatModel;

    @Mock
    private VectorStore vectorStore;

    @Mock
    private MovieRepository movieRepository;

    private AiChatService aiChatService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        aiChatService = new AiChatService(chatModel, vectorStore, movieRepository, objectMapper);
    }

    @Test
    void analyze_파싱_성공() {
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
        request.setMessage("데드풀 같은 영화 추천해줘");

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
    void analyze_파싱_실패시_null_반환() {
        // given
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse("true"));

        AiDto.ChatRequest request = new AiDto.ChatRequest();
        request.setMessage("데드풀 같은 영화 추천해줘");

        // when
        AiDto.SearchCondition condition = aiChatService.analyze(request);

        // then
        assertThat(condition).isNull();
    }

    @Test
    void ask_참조문서_없으면_null_반환() {
        // given
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());

        // when
        String answer = aiChatService.ask("액션 영화 추천해줘", new AiDto.SearchCondition());

        // then
        assertThat(answer).isNull();
        verify(chatModel, never()).call(any(Prompt.class));
    }

    @Test
    void ask_참조문서_있으면_채팅응답_반환() {
        // given
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(document(101L, "샘플 영화 설명")));
        when(chatModel.call(any(Prompt.class))).thenReturn(mockChatResponse("- [101] Sample: reason"));

        // when
        String answer = aiChatService.ask("액션 영화 추천해줘", new AiDto.SearchCondition());

        // then
        assertThat(answer).isEqualTo("- [101] Sample: reason");
    }

    @Test
    void ask_모델응답_비정상이면_빈문자열_반환() {
        // given
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(document(10L, "샘플")));
        when(chatModel.call(any(Prompt.class))).thenReturn(null);

        // when
        String answer = aiChatService.ask("액션 영화 추천해줘", new AiDto.SearchCondition());

        // then
        assertThat(answer).isEmpty();
    }

    @Test
    void ask_limit_최대초과시_topK_20으로_보정() {
        // given
        AiDto.SearchCondition condition = new AiDto.SearchCondition();
        condition.setLimit(100);

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenAnswer(invocation -> {
            SearchRequest request = invocation.getArgument(0);
            assertThat(extractTopK(request)).isEqualTo(20);
            return List.of();
        });

        aiChatService.ask("액션 영화 추천해줘", condition);

    }

    @Test
    void ask_limit_최소미만시_topK_1로_보정() {
        // given
        AiDto.SearchCondition condition = new AiDto.SearchCondition();
        condition.setLimit(0);

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenAnswer(invocation -> {
            SearchRequest request = invocation.getArgument(0);
            assertThat(extractTopK(request)).isEqualTo(1);
            return List.of();
        });

        aiChatService.ask("액션 영화 추천해줘", condition);

    }

    private Document document(Long movieId, String text) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("movieId", movieId);
        return Document.builder()
                .text(text)
                .metadata(metadata)
                .build();
    }

    private int extractTopK(SearchRequest request) {
        try {
            Method method = request.getClass().getMethod("getTopK");
            return (Integer) method.invoke(request);
        } catch (Exception ignored) {
            // no-op
        }

        try {
            Method method = request.getClass().getMethod("topK");
            return (Integer) method.invoke(request);
        } catch (Exception ignored) {
            // no-op
        }

        try {
            Field field = request.getClass().getDeclaredField("topK");
            field.setAccessible(true);
            return (Integer) field.get(request);
        } catch (Exception e) {
            throw new AssertionError("Cannot read SearchRequest.topK", e);
        }
    }

    private ChatResponse mockChatResponse(String text) {
        AssistantMessage output = new AssistantMessage(text);
        Generation generation = new Generation(output);
        return new ChatResponse(List.of(generation));
    }
}
