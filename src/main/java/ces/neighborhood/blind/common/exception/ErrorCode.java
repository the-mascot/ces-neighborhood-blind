package ces.neighborhood.blind.common.exception;

import lombok.Getter;
import lombok.ToString;

import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    CODE_0000("0000", "Successful Response", HttpStatus.OK),

    /*CODE 1000 ~ 1999 : 회원관련*/
    CODE_1000("1000", "No Authority"),
    CODE_1001("1001", "Not Found Member"),
    CODE_1002("1002", "Wrong Password"),
    CODE_1003("1003", "Please enter a Password"),
    CODE_1005("1005", "아이디 또는 비밀번호를 잘못 입력했습니다.\n입력하신 내용을 다시 확인해주세요."),
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[BizError] CODE: ");
        stringBuilder.append(code);
        stringBuilder.append(", message: ");
        stringBuilder.append(message);
        return stringBuilder.toString();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus == null ? HttpStatus.INTERNAL_SERVER_ERROR : httpStatus;
    }
}
