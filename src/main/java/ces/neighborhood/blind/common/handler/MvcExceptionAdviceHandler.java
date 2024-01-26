package ces.neighborhood.blind.common.handler;

import ces.neighborhood.blind.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class MvcExceptionAdviceHandler {

    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Model model, Exception e) {
        log.error("[MvcExceptionHandler] errMsg: {}", e.getMessage());
        log.error("[MvcExceptionHandler] err 호출지점: {}", e.getStackTrace()[0]);
        e.printStackTrace();
        model.addAttribute("error", ErrorCode.CODE_9998.getMessage());
        return "error";
    }

}
