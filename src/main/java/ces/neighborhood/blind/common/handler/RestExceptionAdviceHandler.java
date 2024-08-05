package ces.neighborhood.blind.common.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import ces.neighborhood.blind.common.exception.ApiExceptionResponse;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * RestException 처리 Handler
 * </pre>
 *
 * @version 1.0
 * @author the-mascot
 * @since 2023.11.27
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionAdviceHandler {

    /**
     * BizException Exception 처리
     */
    @ExceptionHandler({BizException.class})
    public ResponseEntity<ApiExceptionResponse> exceptionHandler(HttpServletRequest request, final BizException e) {
        log.error("[BizExceptionHandler] errCode: {}, errMsg: {}", e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        return new ResponseEntity<>(ApiExceptionResponse.builder()
                .errCode(e.getErrorCode().getCode())
                .errMsg(e.getErrorCode().getMessage())
                .build(), e.getErrorCode().getHttpStatus());
    }

    /**
     * 최상위 Exception 처리
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiExceptionResponse> exceptionHandler(HttpServletRequest request, final Exception e) {
        log.error("[RestExceptionHandler] errMsg: {}", e.getMessage());
        log.error("[RestExceptionHandler] err 호출지점: {}", e.getStackTrace()[0]);
        e.printStackTrace();
        return new ResponseEntity<>(ApiExceptionResponse.builder()
                .errCode(ErrorCode.CODE_9999.getCode())
                .errMsg(ErrorCode.CODE_9999.getMessage())
                .build(), ErrorCode.CODE_9999.getHttpStatus());
    }

    /**
     * mapping 되는 컨트롤러 없는 경우 (404) 에러 handler
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NoHandlerFoundException ex) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
