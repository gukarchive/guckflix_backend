package guckflix.backend.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public List<LogDto> findByKeys(String inputKey) {
        Set<String> keys = redisTemplate.keys(inputKey);

        List<LogDto> list = new ArrayList<>();
        for (String findKey : keys) {
            list.add(new LogDto(keyFormatToDbmsFormat(findKey),
                    LocalDate.now().minusDays(1),
                    Integer.parseInt(redisTemplate.opsForValue().get(findKey))));
        }
        return list;
    }

    public void addCount(String key) {
        String cacheKey = createCacheKey(key);
        String currentValue = redisTemplate.opsForValue().get(cacheKey);

        if (currentValue != null) { // 값이 존재하면 ++;
            redisTemplate.opsForValue().set(cacheKey, String.valueOf(Integer.valueOf(currentValue) + 1));
        } else { // 값이 없을 경우 set 1
            redisTemplate.opsForValue().set(cacheKey, String.valueOf(1));
        }
    }

    /**
     * url /popular/33을 받을 때 /popular:/33:2024-10-14 형식으로 redis key 생성
     */
    private String createCacheKey(String uri) {
        String[] parts = uri.split("/");
        String key = "";

        for(int i = 1; i < parts.length; i++){
            key = key + "/" + parts[i] + ":";

            if(i == parts.length - 1) // 마지막 것은 날짜를 붙임
                key = key + LocalDate.now();
        }
        return key;
    }

    /**
     * RDBMS에 insert 하기 위해서는 /popular:/33:2024-10-14 와 같은 key 형식을 /popular/33 처럼 변환해야 함
     */
    private String keyFormatToDbmsFormat(String key) {
        String result = null;

        // 날짜 제거
        int lastColonIndex = key.lastIndexOf(':');
        if (lastColonIndex != -1) {
            // 0부터 마지막 콜론 이전까지의 문자열 추출
            result = key.substring(0, lastColonIndex);
        }
        // 콜론 제거
        result = result.replace(":", "");
        return result;
    }
}
