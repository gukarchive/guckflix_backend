package guckflix.backend.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import guckflix.backend.exception.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * AuthenticationEntryPoint를 따로 등록해주지 않으면 인증, 인가 실패 전부 403 코드로 응답하므로 401 코드를 받을 수 없다.
 * https://velog.io/@park2348190/Spring-Security%EC%9D%98-Unauthorized-Forbidden-%EC%B2%98%EB%A6%AC
 */
@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseDto fail = new ResponseDto(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, "You need to log in"); // Custom error response.
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        String json = objectMapper.writeValueAsString(fail);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }
}
