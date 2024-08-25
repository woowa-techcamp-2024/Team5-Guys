package info.logbat.domain.log.presentation.validation;

import info.logbat.domain.log.presentation.payload.request.CreateLogRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 로그 요청 목록의 유효성을 검사하는 밸리데이터 클래스입니다. 빈 요청, 개별 요청의 유효성, 전체 요청의 유효성을 검사합니다.
 */
@Component
@RequiredArgsConstructor
public class LogRequestsValidator implements
    ConstraintValidator<ValidLogRequests, List<CreateLogRequest>> {

    private final Validator validator;

    /**
     * 로그 요청 목록의 유효성을 검사합니다.
     *
     * @param requests 검사할 로그 요청 목록
     * @param context  제약 조건 컨텍스트
     * @return 유효성 검사 결과 (true: 유효, false: 무효)
     */
    @Override
    public boolean isValid(List<CreateLogRequest> requests, ConstraintValidatorContext context) {
        if (isEmptyRequest(requests, context)) {
            return false;
        }

        List<String> errorMessages = new ArrayList<>();
        List<CreateLogRequest> validRequests = filterValidRequests(requests, errorMessages);

        updateRequestsList(requests, validRequests);

        if (requests.isEmpty()) {
            addErrorMessage(context, String.join("\n", errorMessages));
            return false;
        }

        return true;
    }

    /**
     * 요청 목록이 비어있는지 확인합니다.
     *
     * @param requests 검사할 요청 목록
     * @param context  제약 조건 컨텍스트
     * @return 비어있으면 true, 그렇지 않으면 false
     */
    private boolean isEmptyRequest(List<CreateLogRequest> requests,
        ConstraintValidatorContext context) {
        if (requests == null || requests.isEmpty()) {
            addErrorMessage(context, "빈 요청이 전달되었습니다.");
            return true;
        }
        return false;
    }

    /**
     * 유효한 요청만 필터링합니다.
     *
     * @param requests      전체 요청 목록
     * @param errorMessages 오류 메시지를 저장할 리스트
     * @return 유효한 요청 목록
     */
    private List<CreateLogRequest> filterValidRequests(List<CreateLogRequest> requests,
        List<String> errorMessages) {

        return IntStream.range(0, requests.size())
            .filter(index -> isValidRequest(requests.get(index), index + 1, errorMessages))
            .mapToObj(requests::get)
            .toList();
    }

    /**
     * 개별 요청의 유효성을 검사합니다.
     *
     * @param request       검사할 요청
     * @param index         요청의 인덱스
     * @param errorMessages 오류 메시지를 저장할 리스트
     * @return 유효하면 true, 그렇지 않으면 false
     */
    private boolean isValidRequest(CreateLogRequest request, int index,
        List<String> errorMessages) {
        Set<ConstraintViolation<CreateLogRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
            errorMessages.add("Request " + index + ": " + message);
            return false;
        }
        return true;
    }

    /**
     * 요청 목록을 유효한 요청만으로 갱신합니다.
     *
     * @param requests      원본 요청
     * @param validRequests 유효한 요청 목록
     */
    private void updateRequestsList(List<CreateLogRequest> requests,
        List<CreateLogRequest> validRequests) {
        requests.clear();
        requests.addAll(validRequests);
    }

    /**
     * 사용자 정의 제약 조건 위반을 추가합니다.
     *
     * @param context 제약 조건 컨텍스트
     * @param message 제약 조건 위반 메시지
     */
    private void addErrorMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
            .addConstraintViolation();
    }
}