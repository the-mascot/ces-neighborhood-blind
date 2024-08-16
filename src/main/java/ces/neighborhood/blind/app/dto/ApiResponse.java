package ces.neighborhood.blind.app.dto;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import ces.neighborhood.blind.common.exception.ErrorCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiResponse<T> {

    private final String code;
    private final T data;
    private final String message;

    public ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private static HttpHeaders getTokenHeaders(TokenDto tokenDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(tokenDto.getAccessTokenHeaderName(), tokenDto.getAccessToken());
        headers.add(tokenDto.getRefreshTokenHeaderName(), tokenDto.getRefreshToken());
        return headers;
    }

    public static ResponseEntity<ApiResponse<Void>> success() {
        ApiResponse<Void> apiResponse = new ApiResponse<>(ErrorCode.CODE_0000.getCode(), ErrorCode.CODE_0000.getMessage(), null);
        return new ResponseEntity<>(apiResponse, apiResponse.getHeaders(), HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>(ErrorCode.CODE_0000.getCode(), ErrorCode.CODE_0000.getMessage(), data);
        return new ResponseEntity<>(apiResponse, apiResponse.getHeaders(), HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(TokenDto tokenDto) {
        ApiResponse<T> apiResponse = new ApiResponse<>(ErrorCode.CODE_0000.getCode(), ErrorCode.CODE_0000.getMessage(), null);
        return new ResponseEntity<>(apiResponse, getTokenHeaders(tokenDto), HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(TokenDto tokenDto, T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>(ErrorCode.CODE_0000.getCode(), ErrorCode.CODE_0000.getMessage(), data);
        return new ResponseEntity<>(apiResponse, getTokenHeaders(tokenDto), HttpStatus.OK);
    }
}
