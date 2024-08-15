package ces.neighborhood.blind.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

    CODE_0000("0000", "Successful Response", HttpStatus.OK),

    /*CODE 1000 ~ 1999 : 회원관련*/
    CODE_1000("1000", "No Authority"),
    CODE_1001("1001", "존재하지 않는 회원입니다."),
    CODE_1002("1002", "Wrong Password"),
    CODE_1003("1003", "Please enter a Password"),
    CODE_1004("1004", "신규회원 닉네임 설정 필요."),
    CODE_1005("1005", "아이디 또는 비밀번호를 잘못 입력했습니다.\n입력하신 내용을 다시 확인해주세요."),
    CODE_1100("1100", "OAuth Login - Missing attribute name in UserInfoEndpoint"),
    CODE_1101("1101", "OAuth Login - Missing attribute or email in UserInfoEndpoint"),
    CODE_1120("1120", "토큰 유효기간 만료"),
    CODE_1121("1121", "유효하지 않은 토큰입니다."),
    CODE_1122("1122", "권한이 낮습니다."),
    CODE_8000("8001", "업로드 이미지 파일이 없습니다."),
    CODE_8001("8001", "지원하지않는 파일 형식입니다."),
    CODE_8002("8002", "이미지 업로드 실패"),
    CODE_8003("8002", "첨부할 수 있는 이미지 용량은 최대 10MB 까지입니다."),
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
