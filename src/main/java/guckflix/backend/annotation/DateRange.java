package guckflix.backend.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * 이 클래스는 LocalDate 유효성 검사 어노테이션
 *
 * 어드민이 영화 개봉일자를 입력할 시 1500년, 2100년은 잘못된 입력 데이터임
 * 그러나 오늘로부터 5일 뒤 개봉은 충분히 입력 가능한 데이터라고 할 수 있음
 * 따라서 날짜를 과거년도(minYear)로부터 오늘 날짜 이후 n일(LocalDate.now + daysFromToday) 안의 데이터인지 검사
 *
 * (minYear = 1900, daysFromToday = 30) 현재 시간 2023.10.01일 때, 1900.1.1 ~ 2023.11.01까지의 데이터만 허용
 */
@Target({FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface DateRange {

    String message() default "{DateRange}";

    int minYear() default 1896;
    int daysFromToday() default 0;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
