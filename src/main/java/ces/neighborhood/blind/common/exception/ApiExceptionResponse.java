package ces.neighborhood.blind.common.exception;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ApiExceptionResponse {

    private String errCode;
    private String errMsg;
    private LocalDateTime responseTime;

    @Builder
    public ApiExceptionResponse(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.responseTime = LocalDateTime.now();
    }

}
