package info.logbat_meta.common.payload;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiCommonResponse<T> {

    private int statusCode;
    private String message;
    private T data;

    private ApiCommonResponse(int status, String message, T data) {
        this.statusCode = status;
        this.message = message;
        this.data = data;
    }

    private ApiCommonResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    private ApiCommonResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    public static <T> ApiCommonResponse<T> createApiResponse(HttpStatus httpStatus, String message,
                                                             T data) {
        return new ApiCommonResponse<>(httpStatus.value(), message, data);
    }

    public static ApiCommonResponse<Void> createFailResponse(HttpStatus httpStatus,
        String message) {
        return new ApiCommonResponse<>(httpStatus.value(), message);
    }

    public static ApiCommonResponse<Void> createSuccessResponse() {
        return new ApiCommonResponse<>(HttpStatus.OK.value());
    }

    public static <T> ApiCommonResponse<T> createSuccessResponse(T data) {
        return new ApiCommonResponse<>(HttpStatus.OK.value(), "Success", data);
    }
}
