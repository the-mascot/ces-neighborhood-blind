package ces.neighborhood.blind.common.exception;

import lombok.Getter;
import lombok.ToString;

import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ErrorCode {

    CODE_0000("0000", "Successful Response", HttpStatus.OK),

    /*CODE 1000 ~ 1999 : 회원관련*/
    CODE_1000("1000", "No Authority"),
    CODE_1001("1001", "Not Found Member"),
    CODE_1002("1002", "Wrong Password"),
    CODE_1003("1003", "Please enter a Password"),
    CODE_9999("9999", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR)
    ;
    private String code;
    private String message;
    private HttpStatus httpStatus;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() { return code; }

    public String getMessage() { return message; }

    public HttpStatus getHttpStatus() {
        return httpStatus == null ? HttpStatus.INTERNAL_SERVER_ERROR : httpStatus;
    }
}
