package guckflix.backend.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import guckflix.backend.exception.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        HttpStatus status = HttpStatus.FORBIDDEN;
        ResponseDto erroDto = new ResponseDto(status.value(), status, "권한이 없습니다");
        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");
        String json = objectMapper.writeValueAsString(erroDto);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }
}
