package guckflix.backend.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class AiTimingAspect {

    @Around("execution(* guckflix.backend.service.AiChatService.analyze(..))")
    public Object measureAnalyze(ProceedingJoinPoint joinPoint) throws Throwable {
        long startedAt = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            log.debug("AI timing - analyze(ms): {}", elapsedMillis(startedAt));
        }
    }

    @Around("execution(* guckflix.backend.service.AiChatService.ask(..))")
    public Object measureAsk(ProceedingJoinPoint joinPoint) throws Throwable {
        long startedAt = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            log.debug("AI timing - ask(ms): {}", elapsedMillis(startedAt));
        }
    }

    @Around("execution(* org.springframework.ai.vectorstore.VectorStore.similaritySearch(..))")
    public Object measureSimilaritySearch(ProceedingJoinPoint joinPoint) throws Throwable {
        long startedAt = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            log.debug("AI timing - similaritySearch(ms): {}", elapsedMillis(startedAt));
        }
    }

    private long elapsedMillis(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
