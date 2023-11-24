package ces.neighborhood.blind.common.entity;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ces.neighborhood.blind.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final String code;
    private final T data;
    private final String message;

    public ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return headers;
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>(ErrorCode.CODE_0000.getCode(), ErrorCode.CODE_0000.getMessage(), data);
        return new ResponseEntity<>(apiResponse, apiResponse.getHeaders(), HttpStatus.OK);
    }
}
