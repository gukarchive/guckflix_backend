package guckflix.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import guckflix.backend.annotation.DateRange;
import guckflix.backend.config.GenreCached;
import guckflix.backend.entity.Movie;
import guckflix.backend.entity.MovieGenre;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class MovieDto {


    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "MovieDto-Post")
    public static class Post implements DataTransferable {

        @NotBlank
        @Length(max = 100)
        private String title;

        @NotBlank
        @Length(max = 1000)
        private String overview;

        @Valid
        @NotNull
        @Size(min = 1, max = 10)
        private List<GenreDto> genres;

        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @DateRange(minYear = 1900, daysFromToday = 30)
        @JsonProperty("release_date")
        private LocalDate releaseDate;

        @JsonProperty("backdrop_path")
        private String backdropPath;

        @JsonProperty("poster_path")
        private String posterPath;

        @Valid
        @NotNull
        @Size(min = 1)
        private List<CreditDto.Post> credits;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "MovieDto-Response")
    public static class Response implements DataTransferable {

        private Long id;

        private String title;

        private String overview;

        private float popularity;

        @JsonProperty("vote_count")
        private int voteCount;

        @JsonProperty("vote_average")
        private float voteAverage;

        private List<GenreDto> genres;

        @JsonProperty("release_date")
        private LocalDate releaseDate;

        @JsonProperty("backdrop_path")
        private String backdropPath;

        @JsonProperty("poster_path")
        private String posterPath;

        @JsonIgnore
        public Response(Movie entity){
            this.setId(entity.getId());

            List<MovieGenre> movieGenres = entity.getMovieGenres();
            List<GenreDto> genreDtos = new ArrayList<>();
            for (MovieGenre movieGenre : movieGenres) {
                genreDtos.add(new GenreDto(movieGenre.getGenre().getId(), movieGenre.getGenre().getGenreName()));
            }

            this.setGenres(genreDtos);
            this.setOverview(entity.getOverview());
            this.setPopularity(entity.getPopularity());
            this.setTitle(entity.getTitle());
            this.setPosterPath(entity.getPosterPath());
            this.setBackdropPath(entity.getBackdropPath());
            this.setVoteAverage(entity.getVoteAverage());
            this.setVoteCount(entity.getVoteCount());
            this.setReleaseDate(entity.getReleaseDate());
        }


    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "MovieDto-Update")
    public static class Update implements DataTransferable {

        @NotBlank
        @Length(max = 100)
        private String title;

        @NotBlank
        @Length(max = 1000)
        private String overview;

        @Valid
        @NotNull
        @Size(min = 1, max = 10)
        private List<GenreDto> genres;

        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @DateRange(minYear = 1900, daysFromToday = 30)
        @JsonProperty("release_date")
        private LocalDate releaseDate;

        @JsonProperty("backdrop_path")
        private String backdropPath;

        @JsonProperty("poster_path")
        private String posterPath;

        @Valid
        @NotNull
        @Size(min = 1)
        private List<CreditDto.Post> credits;

    }





}
