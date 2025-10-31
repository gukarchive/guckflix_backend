package guckflix.backend.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogScheduler {

    private final LogRedisRepository inMemoryRepository;

    private final LogJdbcRepository logRepository;

    @Scheduled(cron = "0 0 4 * * *") // 매일 오전 4시에 실행
    public void countInsertToDb() {
        log.info("[스케줄러 동작] 로깅 데이터 INSERT from redis to mysql using jdbcTemplate...");
        List<LogDto> logList = inMemoryRepository.findByKeys("*:" + LocalDate.now().minusDays(1).toString());
        int updateCounts = logRepository.saveAll(logList);
        log.info("{}건 삽입됨", updateCounts);
    }
}
