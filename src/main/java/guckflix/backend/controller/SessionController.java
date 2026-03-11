package guckflix.backend.controller;

import guckflix.backend.dto.MemberDto;
import guckflix.backend.exception.ResponseDto;
import guckflix.backend.security.authen.PrincipalDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/session")
public class SessionController {

    /**
     * 클라이언트의 SESSION 검사 및 만료 연장
     */
    @GetMapping("/validation")
    public ResponseEntity<?> checkSession(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession(false);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (session == null || authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, "유효하지 않은 세션"));
        }

        PrincipalDetails user = (PrincipalDetails) authentication.getPrincipal();

        return ResponseEntity.ok().body(new ResponseDto<>(HttpStatus.OK.value(), HttpStatus.OK, "유효 세션 성공", new MemberDto.User(user.getId(), user.getRole().name())));
    }

    // 블루-그린 배포 시 헬스체크
    @GetMapping("/healthcheck")
    public ResponseEntity<Void> healthcheck() {
        return ResponseEntity.ok().build();
    }

}
