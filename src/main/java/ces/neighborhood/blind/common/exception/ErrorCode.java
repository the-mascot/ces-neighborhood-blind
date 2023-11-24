package ces.neighborhood.blind.common.exception;

public enum ErrorCode {

    CODE_0000("0000", "Successful Response"),

    /*CODE 1000 ~ 1999 : 회원관련*/
    NO_AUTHORITY("1000", "No Authority"),
    NOT_FOUND_PASSWORD("1001", "Not Found Password"),
    WRONG_PASSWORD("1002", "Wrong Password"),
    CODE_9999("9999", "Internal Server Error")
    ;
    private String code;
    private String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }

    public String getMessage() { return message; }
}
