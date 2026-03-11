package guckflix.backend.security.authen;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// 메서드의 결과값들은 직렬화/역직렬화할 필요가 없음
@JsonIgnoreProperties({
        "password",
        "authorities",
        "accountNonExpired",
        "accountNonLocked",
        "credentialsNonExpired",
        "enabled",
        "name"
})
public abstract class PrincipalDetailsMixin {
}
