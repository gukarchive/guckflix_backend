package guckflix.backend.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<DateRange, LocalDate> {

    private int minYear;
    private int daysFromToday;

    @Override
    public void initialize(DateRange constraintAnnotation) {
        minYear = constraintAnnotation.minYear();
        daysFromToday = constraintAnnotation.daysFromToday();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {

        // null 허용 X
        if (value == null) {
            return false;
        }

        LocalDate past = LocalDate.of(minYear, 1, 1);
        LocalDate now = LocalDate.now();
        LocalDate limitDate = now.plusDays(daysFromToday);

        return value.isAfter(past) && value.isBefore(limitDate) ;
    }
}
