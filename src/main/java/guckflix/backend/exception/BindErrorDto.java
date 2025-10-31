package guckflix.backend.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Getter
public class BindErrorDto {

    @JsonProperty("status_code")
    private int statusCode;

    private HttpStatus status;

    private String message;

    @JsonProperty("bind_errors")
    private List<BindErrorDetail> fieldErrorList;

    @JsonProperty("object_errors")
    private List<BindErrorDetail> objectErrorList;

    /**
     *
     * @param statusCode : 400 상태코드
     * @param status : BAD_REQUEST
     * @param message :
     * @param paramFieldErrors
     * @param paramObjectErrors
     * @param messageSource
     * @param locale
     */
    public BindErrorDto(int statusCode, HttpStatus status, String message,
                        List<FieldError> paramFieldErrors, List<ObjectError> paramObjectErrors,
                        MessageSource messageSource, Locale locale) {
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
        this.fieldErrorList = paramFieldErrors.stream().map((fieldError)-> BindErrorDetail.createFieldError(fieldError, messageSource, locale))
                .collect(Collectors.toList());
        this.objectErrorList = paramObjectErrors.stream().filter((objectError) -> !(objectError instanceof FieldError))
                .map((objectError)-> BindErrorDetail.createObjectError(objectError, messageSource, locale))
                .collect(Collectors.toList());
    }
}
