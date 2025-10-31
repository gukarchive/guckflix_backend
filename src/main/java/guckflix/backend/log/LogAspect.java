package guckflix.backend.log;

import guckflix.backend.entity.Member;
import guckflix.backend.exception.BusinessException;
import guckflix.backend.security.authen.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 요구사항 :
 *
 * 기능1: API가 요청된 횟수 집계할 것임
 * 매 정시 API 불린 횟수를 집계하여 매일 1회 batch 작업으로 db에 INSERT
 * 일자, URI, API 호출 횟수만 수집
 *
 * q. API 호출 횟수를 어디에 저장해서 가지고 있어야 하는가? 만약 블루-그린 배포면 그 내용이 공유되어야 한다. 따라서 db 중에 골라야 한다.
 * API 호출 때마다 횟수를 증가시켜야 하므로 속도도 중요하다
 * a. 빠른 속도인 redis를 사용한다.
 *
 */
@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class LogAspect {

    private final LogRedisRepository inMemoryService;
    private final SlackAlarm slackAlarm;

    // 이미지 컨트롤러는 제외. 관리해야 하는 키가 너무 많음
    @Pointcut("execution(* guckflix.backend.controller.*.*(..)) && !execution(* guckflix.backend.controller.ImageController.*(..))")
    public void allControllerNotImage() {
    }

    // 집계 AOP
    @Before("allControllerNotImage()")
    public void apiCountLogging() throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String requestURI = request.getRequestURI();
        inMemoryService.addCount(requestURI);
    }

    // 익셉션 로깅 AOP
    @AfterThrowing(pointcut = "execution(* guckflix.backend.controller.*.*(..))", throwing = "e")
    public void exceptionLogging(JoinPoint joinPoint, Exception e) {

        // 예외 분류. 사용자 오입력, ID 중복같은 비즈니스 익셉션이면 로그를 남기지 않음
        // 그 이외 모든 예외 로깅
        if(e instanceof BusinessException) {
            return;
        }

        // 사용자 정보
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        StringBuffer userInfo = new StringBuffer();

        if(authentication.getPrincipal() == "anonymousUser") {
            userInfo.append("anonymous");
        } else {
            Member member = ((PrincipalDetails) authentication.getPrincipal()).getMember();
            String username = member.getUsername();
            String userRole = member.getRole().toString();
            userInfo.append(username).append(" ").append(userRole);
        }

        // 파라미터 출력
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Enumeration<String> parameterNames = request.getParameterNames();
        StringBuffer params = new StringBuffer();
        parameterNames.asIterator().forEachRemaining(paramName ->
                params.append(paramName).append(" = ").append(request.getParameter(paramName)).append(" "));

        String requestURL = request.getMethod() + " " + request.getRequestURL();
        String IP = request.getRemoteAddr();
        String occurredClass = joinPoint.getSignature().toString();

        /* 로깅 형식
        요청 URL : GET http://localhost:8081/test
        요청 IP : 0:0:0:0:0:0:0:1
        사용자 정보 : anonymous
        실행 클래스 : String guckflix.backend.controller.TestController.test()
        리퀘스트 파라미터 : page = 2255 page4 = 336
        예외 클래스 : class guckflix.backend.exception.NotAllowedIdException
        다음 예외 발생
        guckflix.backend.exception.NotAllowedIdException: Not allowed ID
        이하 스택트레이스...
         */
        log.warn("\n 요청 URL : {} \n 요청 IP : {} \n 사용자 정보 : {} \n 실행 클래스 : {} \n 리퀘스트 파라미터 : {} \n 예외 클래스 : {} \n 다음 예외 발생 \n",
                requestURL,
                IP,
                userInfo,
                occurredClass,
                params,
                e.getClass(),
                e);

        String requestId = MDC.get("request_id");

        // slack 알림 전송
        slackAlarm.sendAlert(requestId, requestURL, occurredClass, e);
    }
}
