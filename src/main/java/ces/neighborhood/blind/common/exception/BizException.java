package ces.neighborhood.blind.common.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BizException extends RuntimeException {

    private ErrorCode errorCode;

    public BizException() { super(); }

    public BizException(String message) { super(message); }

    public BizException(Exception e) { super(e); }

    public BizException(String message, Exception e) {
        super(message, e);
    }

    public BizException(ErrorCode errorCode) {
        super(errorCode.toString());
        this.errorCode = errorCode;
    }

    public BizException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BizException(String message, Exception e, ErrorCode errorCode) {
        super(message, e);
        this.errorCode = errorCode;
    }
}
