package guckflix.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

public class AiDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ChatRequest {

        @NotBlank
        private String message;

    }

    @Getter
    @AllArgsConstructor
    public static class ChatResponse {
        private String answer;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SearchCondition {
        private Boolean isMovieRecommendation;
        private Boolean isSimilaritySearch;
        private LocalDate releaseDateFrom;
        private LocalDate releaseDateTo;
        private List<String> genres;
        private List<String> actors;
        private Integer limit = 10;
    }
}
