package guckflix.backend.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.Locale;

@Getter
public class BindErrorDetail {

    private String message;
    private String field;

    /**
     * 필드 바인딩 에러용 생성자
     */
    public BindErrorDetail(String message, String field) {
        this.message = message;
        this.field = field;
    }

    /**
     * 오브젝트 바인딩 에러용 생성자
     */
    public BindErrorDetail(String message) {
        this.message = message;
        this.field = "Overall";
    }

    @JsonIgnore
    public static BindErrorDetail createFieldError(FieldError error, MessageSource messageSource, Locale locale) {
        return new BindErrorDetail(
                messageSource.getMessage(error, locale),
                error.getField());
    }

    @JsonIgnore
    public static BindErrorDetail createObjectError(ObjectError error, MessageSource messageSource, Locale locale) {
        return new BindErrorDetail(
                messageSource.getMessage(error, locale)
                );
    }
}
