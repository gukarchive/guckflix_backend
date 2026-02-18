package guckflix.backend.controller;

import guckflix.backend.dto.MemberDto;
import guckflix.backend.entity.Member;
import guckflix.backend.exception.ResponseDto;
import guckflix.backend.security.authen.PrincipalDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/session")
public class SessionController {

    /**
     * 클라이언트의 JSESSIONID가 유효한 지 검사
     */
    @GetMapping("/validation")
    public ResponseEntity checkSession(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession(false);

        String jsessionIdFromCookie = null;
        Authentication authentication = null;
        Long userId = null;
        String role = null;

        // 세션이 있으면
        if (session != null) {

            // 이용자가 전송한 JSESSIONID 값
            Cookie[] requestCookies = request.getCookies();
            for (Cookie cookie : requestCookies)
                if(cookie.getName().equals("JSESSIONID")) jsessionIdFromCookie = cookie.getValue();

            // 스프링 시큐리티 인증 확인
            authentication = SecurityContextHolder.getContext().getAuthentication();
            Member member = ((PrincipalDetails) authentication.getPrincipal()).getMember();
            userId = member.getId();
            role = member.getRole().toString();

            if (authentication != null && authentication.isAuthenticated()) {

                // 이용자가 전송한 JSESSIONID가 세션 값과 일치하는 경우
                if (jsessionIdFromCookie.equals(session.getId())) {
                    return ResponseEntity.ok().body(new ResponseDto(HttpStatus.OK.value(), HttpStatus.OK, "유효 세션 성공", new MemberDto.User(userId, role)));

                }
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value()).body(new ResponseDto(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, "유효하지 않은 세션"));

    }

}