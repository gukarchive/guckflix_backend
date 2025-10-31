package guckflix.backend.exception;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

/**
 * ErrorDto : 상태코드(400), 상태코드 메세지(BAD_REQUEST), 메세지("No movie of given id")를 출력하기 위한 클래스
 * BindErrorDto : ErrorDto에 더해 바인딩 에러를 locale(ko or en) 따라 출력하기 위해서 만든 클래스
 * BindingErrorDetail : 필드 에러와 오브젝트 에러 관계없이 받아서 일반적인 메세지로 만드는 클래스
 * 에러 형태를 스프링 기본 값 대신 원하는 대로 만들기 위해 사용
 */

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ApiExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ResponseDto> notFound(NotFoundException e) {
        ResponseDto errorResponse = new ResponseDto(HttpStatus.BAD_REQUEST.value(),HttpStatus.NOT_FOUND, e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(value = NotAllowedIdException.class)
    public ResponseEntity<ResponseDto> notAllowedId(NotAllowedIdException e) {
        ResponseDto errorResponse = new ResponseDto(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(value = DuplicateException.class)
    public ResponseEntity<ResponseDto> memberDuplicate(DuplicateException e) {
        ResponseDto errorResponse = new ResponseDto(HttpStatus.CONFLICT.value(),HttpStatus.CONFLICT, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = RuntimeIOException.class)
    public ResponseEntity<ResponseDto> notFound(RuntimeIOException e) {
        ResponseDto errorResponse = new ResponseDto(HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST, e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 요청 Locale에 따라 BindingResult 응답 메세지 변경
     * https://meetup.nhncloud.com/posts/147
     */
    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<BindErrorDto> bindException(BindException e, Locale locale) {
        String message = locale.getLanguage().equals("en") ? "validation fail" : "잘못된 요청입니다";
        BindErrorDto bindErrorDto = new BindErrorDto(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, message, e.getFieldErrors(), e.getAllErrors(), messageSource, locale);
        return ResponseEntity.badRequest().body(bindErrorDto);
    }


}
