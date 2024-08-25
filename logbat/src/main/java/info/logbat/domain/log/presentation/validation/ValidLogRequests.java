package info.logbat.domain.log.presentation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LogRequestsValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLogRequests {

    String message() default "유효하지 않은 로그 요청입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
