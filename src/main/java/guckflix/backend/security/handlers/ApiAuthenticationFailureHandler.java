package guckflix.backend.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import guckflix.backend.exception.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 로그인 실패 시 메세지를 보낼 핸들러
 {
 "status": "BAD_REQUEST",
 "message": "로그인에 실패하였습니다",
 "status_code": 400
 }
 */
@Component
@Slf4j
public class ApiAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        log.error("인증 실패: " + exception.getMessage());

        ObjectMapper objectMapper = new ObjectMapper();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ResponseDto fail = new ResponseDto(status.value(), status, "로그인에 실패하였습니다");
        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");
        String json = objectMapper.writeValueAsString(fail);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }
}
