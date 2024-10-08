package ces.neighborhood.blind.common.handler;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import ces.neighborhood.blind.common.exception.ApiExceptionResponse;
import ces.neighborhood.blind.common.exception.ErrorCode;
import ces.neighborhood.blind.common.utils.ComUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <pre>
 * 인증되지 않은 요청에 대한 처리 Handler
 * </pre>
 *
 * @version 1.0
 * @author the-mascot
 */
@Component
public class CustomAuthenticationEntryPoint implements
        AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        ApiExceptionResponse error = ApiExceptionResponse.builder()
                .errCode(ErrorCode.CODE_1121.getCode())
                .errMsg(ErrorCode.CODE_1121.getMessage())
                .build();
        response.getWriter().write(ComUtils.convertObjectToJson(error));

    }
}
