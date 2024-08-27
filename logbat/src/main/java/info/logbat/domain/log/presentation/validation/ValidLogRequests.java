package info.logbat.domain.log.presentation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 로그 요청 목록의 유효성을 검사하는 어노테이션입니다.
 *
 * @deprecated 사용되지 않는 어노테이션입니다.
 */
@Constraint(validatedBy = LogRequestsValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Deprecated(forRemoval = true)
public @interface ValidLogRequests {

    String message() default "유효하지 않은 로그 요청입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
