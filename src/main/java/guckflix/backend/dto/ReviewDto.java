package guckflix.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import guckflix.backend.entity.Review;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ReviewDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "ReviewDto-Post")
    public static class Post {

        @NotBlank
        private String content;

        @NotBlank
        @JsonProperty("vote_rating")
        private float voteRating;

        @JsonIgnore
        private Long movieId;

        @JsonIgnore
        private Long userId;

        @JsonIgnore
        private Long reviewId;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "ReviewDto-Response")
    public static class Response implements DataTransferable{

        @JsonProperty("review_id")
        private Long reviewId;

        @JsonProperty("user_id")
        private Long userId;

        private String content;

        @JsonProperty("vote_rating")
        private float voteRating;

        @JsonProperty("last_modified_at")
        private LocalDateTime lastModifiedAt;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        @JsonIgnore
        private Long movieId;

        public Response (Review entity){
            this.setReviewId(entity.getId());
            this.setContent(entity.getContent());
            this.setCreatedAt(entity.getCreatedAt());
            this.setLastModifiedAt(entity.getLastModifiedAt());
            this.setVoteRating(entity.getVoteRating());
        }

    }

}
