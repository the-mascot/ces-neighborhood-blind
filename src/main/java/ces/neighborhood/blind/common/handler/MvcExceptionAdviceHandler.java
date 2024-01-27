package ces.neighborhood.blind.common.handler;

import ces.neighborhood.blind.common.exception.ApiExceptionResponse;
import ces.neighborhood.blind.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * <pre>
 * MvcException 처리 Handler
 * </pre>
 *
 * @version 1.0
 * @author the-mascot
 * @since 2024.01.26
 */
@Slf4j
@ControllerAdvice
public class MvcExceptionAdviceHandler {

    /**
     * 최상위 Exception 처리
     */
    @ExceptionHandler({Exception.class})
    public String exceptionHandler(Model model, Exception e) {
        log.error("[MvcExceptionHandler] errMsg: {}", e.getMessage());
        log.error("[MvcExceptionHandler] err 호출지점: {}", e.getStackTrace()[0]);
        e.printStackTrace();
        model.addAttribute("error", ErrorCode.CODE_9998.getMessage());
        return "error";
    }

    /**
     * Validation Exception 처리
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiExceptionResponse> exceptionHandler(
            HttpServletRequest request, final MethodArgumentNotValidException e) {
        log.error("[MvcExceptionHandler] errMsg: {}", e.getMessage());
        log.error("[MvcExceptionHandler] err 호출지점: {}", e.getStackTrace()[0]);
        e.printStackTrace();
        return new ResponseEntity<>(ApiExceptionResponse.builder()
                .errCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .errMsg(e.getBindingResult().getFieldError().getDefaultMessage())
                .build(), HttpStatus.BAD_REQUEST);
    }

}
