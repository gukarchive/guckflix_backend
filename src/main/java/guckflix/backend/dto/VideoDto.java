package guckflix.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import guckflix.backend.entity.Video;
import guckflix.backend.entity.enums.ISO3166;
import guckflix.backend.entity.enums.ISO639;
import guckflix.backend.entity.enums.VideoProvider;
import guckflix.backend.entity.enums.VideoType;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


public class VideoDto {
    @Getter
    @Setter
    @ApiModel(value = "VideoDto-Response")
    public static class Response {
        @JsonProperty("movie_id")
        private Long movieId;

        @JsonProperty("video_name")
        private String name;

        @JsonProperty("iso_639")
        private ISO639 iso639;

        @JsonProperty("iso_3166")
        private ISO3166 iso3166;

        private String key;

        private Boolean official;

        private VideoProvider site;

        private VideoType type;

        private LocalDateTime publishedAt;

        public Response(Video entity){
            this.movieId = entity.getMovie().getId();
            this.name = entity.getName();
            this.key = entity.getKey();
            this.site = entity.getSite();
            this.official = entity.getOfficial();
            this.iso639 = entity.getIso639();
            this.iso3166 = entity.getIso3166();
            this.type = entity.getType();
            this.publishedAt = entity.getPublishedAt();
        }

    }

}
