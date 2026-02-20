package guckflix.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guckflix.backend.dto.AiDto;
import guckflix.backend.entity.Credit;
import guckflix.backend.entity.Movie;
import guckflix.backend.entity.MovieGenre;
import guckflix.backend.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionTextParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiChatService {

    private static final int INDEX_PAGE_SIZE = 200;

    private final ChatModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;
    private final MovieRepository movieRepository;
    private final ObjectMapper objectMapper;
    private final FilterExpressionTextParser filterExpressionTextParser = new FilterExpressionTextParser();

    /**
     * 텍스트를 임베딩 벡터로 변환한다.
     *
     * @param text 임베딩할 텍스트
     * @return 임베딩 벡터. 입력이 비어 있으면 길이 0 배열
     */
    public float[] embed(String text) {
        if (text == null || text.isBlank()) {
            return new float[0];
        }
        return embeddingModel.embed(text);
    }

    /**
     * 인기 영화 전체 페이지를 순회하며 벡터 문서를 저장한다.
     *
     * @return 저장된 문서 총 개수
     */
    @Transactional(readOnly = true)
    public int embedAllMovies() {
        int offset = 0;
        int totalIndexed = 0;

        while (true) {
            List<Movie> movies = movieRepository.findPopular(offset, INDEX_PAGE_SIZE);
            if (movies.isEmpty()) {
                break;
            }

            List<Document> documents = movies.stream()
                    .map(this::toMovieDocument)
                    .toList();

            if (!documents.isEmpty()) {
                vectorStore.add(documents);
                totalIndexed += documents.size();
            }

            if (movies.size() < INDEX_PAGE_SIZE) {
                break;
            }
            offset += INDEX_PAGE_SIZE;
        }

        return totalIndexed;
    }

    /**
     * 사용자 요청을 분석해 검색 조건(SearchCondition)으로 변환한다.
     *
     * @param request 사용자 입력 요청
     * @return 파싱된 SearchCondition, 파싱 실패 시 null
     */
    public AiDto.SearchCondition analyze(AiDto.ChatRequest request) {
        String message = request.getMessage();
        if (message == null || message.isBlank()) {
            return null;
        }

        Prompt prompt = new Prompt(List.of(
                new SystemMessage("""
                        You are a classifier and extractor.
                        Determine whether the user's message is a movie recommendation request
                        and extract search conditions when possible.

                        Return JSON only. Do not include markdown, code fences, or extra text.
                        Use this exact schema:
                        {
                          "isMovieRecommendation": true,
                          "isSimilaritySearch": true,
                          "releaseDateFrom": "2020-01-01",
                          "releaseDateTo": "2026-02-19",
                          "genres": ["Crime", "Drama"],
                          "actors": ["Ryan Reynolds"],
                          "limit": 10
                        }

                        Rules:
                        - If it is not a movie recommendation request, set isMovieRecommendation to false.
                        - Keep only fields defined in the schema.
                        - genres must use only values from this allowed list (exact spelling):
                          ["Adventure","Fantasy","Animation","Drama","Horror","Action","Comedy","History","Western","Thriller","Crime","Documentary","Science Fiction","Mystery","Music","Romance","Family","War","TV Movie"]
                        - If the user genre is in Korean or another language, translate/map it to one value from the allowed list.
                        - Do not create new genre names outside the allowed list.
                        - Do not infer fields unless the user explicitly states them.
                        - If a field is not explicitly mentioned or unknown, return null (or [] for list fields).
                        - Decide that ambiguous words such as "recent" are within 30 days of the current date.
                        - limit must be an integer between 1 and 10.
                        """),
                new UserMessage(message)
        ));

        ChatResponse response = chatModel.call(prompt);
        String text = (response == null || response.getResult() == null || response.getResult().getOutput() == null)
                ? null
                : response.getResult().getOutput().getText();

        if (text == null) {
            return null;
        }

        return parseAnalyzeResult(text);
    }

    /**
     * 사용자 질문과 검색 조건을 기반으로 벡터 검색을 수행하고 최종 답변을 생성한다.
     *
     * @param message 사용자 질문 원문
     * @param condition 검색 조건 DTO
     * @return 생성된 답변 문자열, 모델 응답이 비정상이면 빈 문자열
     */
    public String ask(String message, AiDto.SearchCondition condition) {
        List<Document> references = searchReferences(message, condition);
        String context = references.isEmpty()
                ? "No relevant documents found."
                : references.stream()
                .map(Document::getText)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n\n---\n\n"));

        Prompt prompt = new Prompt(List.of(
                new SystemMessage("You are a movie recommendation assistant. Use references first and answer concisely."),
                new UserMessage("Question:\n" + message + "\n\nReferences:\n" + context)
        ));

        ChatResponse response = chatModel.call(prompt);
        if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
            return "";
        }

        String text = response.getResult().getOutput().getText();
        return text == null ? "" : text;
    }

    /**
     * VectorStore 유사도 검색을 수행한다.
     *
     * @param query 검색 질의 텍스트
     * @param condition 검색 필터 DTO
     * @return 유사 문서 목록(없으면 빈 리스트)
     */
    private List<Document> searchReferences(String query, AiDto.SearchCondition condition) {
        SearchRequest.Builder builder = SearchRequest.builder()
                .query(query)
                .topK(resolveTopK(condition));

        Filter.Expression filterExpression = toFilterExpression(condition);
        if (filterExpression != null) {
            builder.filterExpression(filterExpression);
        }

        List<Document> documents = vectorStore.similaritySearch(builder.build());
        return documents == null ? List.of() : documents;
    }

    /**
     * 검색 개수(topK)를 안전 범위로 보정한다.
     *
     * @param condition 검색 필터 DTO
     * @return 보정된 topK 값(최소 1, 최대 20)
     */
    private int resolveTopK(AiDto.SearchCondition condition) {
        if (condition == null || condition.getLimit() == null) {
            return 10;
        }
        return Math.max(1, Math.min(20, condition.getLimit()));
    }

    /**
     * SearchCondition을 VectorStore 필터 표현식으로 변환한다.
     *
     * @param condition 검색 필터 DTO
     * @return 필터가 없으면 null, 있으면 파싱된 Filter.Expression
     */
    private Filter.Expression toFilterExpression(AiDto.SearchCondition condition) {
        if (condition == null) {
            return null;
        }

        List<String> clauses = new ArrayList<>();
        if (condition.getReleaseDateFrom() != null) {
            clauses.add("releaseDate >= " + toDateNumber(condition.getReleaseDateFrom()));
        }
        if (condition.getReleaseDateTo() != null) {
            clauses.add("releaseDate <= " + toDateNumber(condition.getReleaseDateTo()));
        }
        if (condition.getActors() != null && !condition.getActors().isEmpty()) {
            String joinedActors = condition.getActors().stream()
                    .filter(actor -> actor != null)
                    .map(actor -> actor.trim())
                    .filter(actor -> !actor.isEmpty())
                    .map(actor -> escape(actor))
                    .map(actor -> "actors == '" + actor + "'")
                    .collect(Collectors.joining(" || "));
            if (!joinedActors.isBlank()) {
                clauses.add("(" + joinedActors + ")");
            }
        }
        if (condition.getGenres() != null && !condition.getGenres().isEmpty()) {
            String joinedGenres = condition.getGenres().stream()
                    .filter(genre -> genre != null)
                    .map(genre -> genre.trim())
                    .filter(genre -> !genre.isEmpty())
                    .map(genre -> escape(genre))
                    .map(genre -> "genres == '" + genre + "'")
                    .collect(Collectors.joining(" || "));
            if (!joinedGenres.isBlank()) {
                clauses.add("(" + joinedGenres + ")");
            }
        }

        if (clauses.isEmpty()) {
            return null;
        }

        String expressionText = String.join(" && ", clauses);
        return filterExpressionTextParser.parse(expressionText);
    }

    /**
     * 필터 문자열에서 특수문자를 이스케이프한다.
     *
     * @param value 원본 문자열
     * @return 이스케이프된 문자열
     */
    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }

    /**
     * LLM JSON 응답 텍스트를 SearchCondition으로 파싱한다.
     *
     * 예시 입력:
     * {
     *   "isMovieRecommendation": true,
     *   "isSimilaritySearch": false,
     *   "releaseDateFrom": "2026-01-01",
     *   "releaseDateTo": "2026-02-01",
     *   "genres": ["Drama"],
     *   "actors": ["Ryan Reynolds"],
     *   "limit": 10
     * }
     *
     * 예시 결과:
     * SearchCondition(
     *   isMovieRecommendation=true,
     *   isSimilaritySearch=false,
     *   releaseDateFrom=2026-01-01,
     *   releaseDateTo=2026-02-01,
     *   genres=[Drama],
     *   actors=[Ryan Reynolds],
     *   limit=10
     * )
     *
     * @param text LLM 원본 응답 텍스트
     * @return 파싱된 SearchCondition, 파싱 실패 시 null
     */
    private AiDto.SearchCondition parseAnalyzeResult(String text) {
        try {
            return objectMapper.readValue(stripCodeFence(text), AiDto.SearchCondition.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * LLM 응답에 코드펜스가 포함된 경우 JSON 본문만 추출한다.
     *
     * @param text LLM 원본 응답 텍스트
     * @return 코드펜스를 제거한 텍스트
     */
    private String stripCodeFence(String text) {
        String trimmed = text == null ? "" : text.trim();
        if (!trimmed.startsWith("```")) {
            return trimmed;
        }

        int firstNewLine = trimmed.indexOf('\n');
        int lastFence = trimmed.lastIndexOf("```");
        if (firstNewLine < 0 || lastFence <= firstNewLine) {
            return trimmed;
        }
        return trimmed.substring(firstNewLine + 1, lastFence).trim();
    }

    private Document toMovieDocument(Movie movie) {
        List<String> genres = movie.getMovieGenres().stream()
                .map(MovieGenre::getGenre)
                .filter(Objects::nonNull)
                .map(genre -> genre.getGenreName())
                .filter(Objects::nonNull)
                .filter(name -> !name.isBlank())
                .distinct()
                .toList();

        List<String> actors = movie.getCredits().stream()
                .map(Credit::getActor)
                .filter(Objects::nonNull)
                .map(actor -> actor.getName())
                .filter(Objects::nonNull)
                .filter(name -> !name.isBlank())
                .distinct()
                .toList();

        String text = buildEmbeddingText(movie, genres, actors);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("movieId", movie.getId());
        if (movie.getReleaseDate() != null) {
            metadata.put("releaseDate", toDateNumber(movie.getReleaseDate()));
        }
        metadata.put("genres", genres);
        metadata.put("actors", actors);

        return Document.builder()
                .text(text)
                .metadata(metadata)
                .build();
    }

    private String buildEmbeddingText(Movie movie, List<String> genres, List<String> actors) {
        String title = movie.getTitle() == null ? "" : movie.getTitle();
        String overview = movie.getOverview() == null ? "" : movie.getOverview();
        String releaseDate = movie.getReleaseDate() == null ? "" : movie.getReleaseDate().toString();

        return """
                Title: %s
                Overview: %s
                Genres: %s
                Actors: %s
                Release Date: %s
                """.formatted(
                title,
                overview,
                String.join(", ", genres),
                String.join(", ", actors),
                releaseDate
        );
    }

    private int toDateNumber(LocalDate date) {
        return date.getYear() * 10000 + date.getMonthValue() * 100 + date.getDayOfMonth();
    }
}
