package ces.neighborhood.blind.common.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ces.neighborhood.blind.common.exception.ApiExceptionResponse;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * RestC
 * </pre>
 *
 * @version 1.0
 * @author the-mascot
 * @since 2023.12.05
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionAdviceHandler {

    @ExceptionHandler({BizException.class})
    public ResponseEntity<ApiExceptionResponse> exceptionHandler(HttpServletRequest request, final BizException e) {
        log.error("[BizExceptionHandler] errCode: {}, errMsg: {}", e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(ApiExceptionResponse.builder()
                        .errCode(e.getErrorCode().getCode())
                        .errMsg(e.getErrorCode().getMessage())
                        .build());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiExceptionResponse> exceptionHandler(HttpServletRequest request, final Exception e) {
        log.error("[RestExceptionHandler] errMsg: {}", e.getMessage());
        log.error("[RestExceptionHandler] err 호출지점: {}", e.getStackTrace()[0]);
        e.printStackTrace();
        return ResponseEntity
                .status(ErrorCode.CODE_9999.getHttpStatus())
                .body(ApiExceptionResponse.builder()
                        .errCode(ErrorCode.CODE_9999.getCode())
                        .errMsg(ErrorCode.CODE_9999.getMessage())
                        .build());
    }
}
