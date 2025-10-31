package guckflix.backend.dto.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guckflix.backend.dto.ActorDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // 값이 null이면 출력되지 않음
public class ResponseWrapper<T> {

    @JsonProperty("movie_id")
    private Long movieId;

    @JsonProperty("actor_id")
    private Long actorId;

    private List<T> results;

    public static <T> ResponseWrapper withMovieId(Long movieId, List<T> results){
        ResponseWrapper responseWrapper = new ResponseWrapper<>();
        responseWrapper.movieId = movieId;
        responseWrapper.results = results;
        return responseWrapper;
    }

    public static <T> ResponseWrapper withActorId(Long actorId, List<T> results){
        ResponseWrapper responseWrapper = new ResponseWrapper<>();
        responseWrapper.actorId = actorId;
        responseWrapper.results = results;
        return responseWrapper;
    }

}