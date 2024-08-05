package ces.neighborhood.blind.common.handler;

import ces.neighborhood.blind.common.exception.ApiExceptionResponse;
import ces.neighborhood.blind.common.exception.ErrorCode;
import ces.neighborhood.blind.common.utils.ComUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * 인증 되었지만, 권한이 없는 요청에 대한 처리 Handler
 * </pre>
 *
 * @version 1.0
 * @author the-mascot
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        ApiExceptionResponse error = ApiExceptionResponse.builder()
                .errCode(ErrorCode.CODE_1122.getCode())
                .errMsg(ErrorCode.CODE_1122.getMessage())
                .build();
        response.getWriter().write(ComUtils.convertObjectToJson(error));
    }
}
