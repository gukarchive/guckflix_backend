package guckflix.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import guckflix.backend.entity.Credit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;

public class CreditDto {

    @Getter
    @Setter
    @Schema(name = "CreditDto-Response")
    public static class Response{

        @JsonProperty("actor_id")
        private Long id;

        private String name;

        @JsonProperty("movie_id")
        private Long movieId;

        @JsonProperty("character")
        private String casting;

        @JsonProperty("profile_path")
        private String profilePath;

        private int order;

        public Response (Credit entity){
            this.id = entity.getActor().getId();
            this.movieId = entity.getMovie().getId();
            this.name = entity.getActor().getName();
            this.casting = entity.getCasting();
            this.order = entity.getOrder();
            this.profilePath = entity.getActor().getProfilePath();
        }
    }

    @Getter
    @Setter
    @Schema(name = "CreditDto-Post")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Post {

        @NotNull
        @JsonProperty("actor_id")
        private Long actorId;

        @NotEmpty
        @Length(min = 1, max = 30)
        @JsonProperty("character")
        private String casting;

    }

    @Getter
    @Setter
    @Schema(name = "CreditDto-Delete")
    public static class Delete {

        @JsonProperty("credit_id")
        private Long id;

    }

    @Getter
    @Setter
    @Schema(name = "CreditDto-Patch")
    public static class Patch {

        @JsonProperty("character")
        private String casting;

    }

}