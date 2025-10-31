package guckflix.backend.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResponseDto<T> {

    @JsonProperty("status_code")
    private int statusCode;

    private HttpStatus status;

    private String message;

    @JsonProperty("data")
    private T bodyDto;

    public ResponseDto(int statusCode, HttpStatus status, String message) {
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
    }

    public ResponseDto(int statusCode, HttpStatus status, String message, T bodyDto) {
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
        this.bodyDto = bodyDto;
    }
}
