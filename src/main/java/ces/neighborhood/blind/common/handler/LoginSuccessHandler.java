package ces.neighborhood.blind.common.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 로그인 성공 처리 Handler
 * </pre>
 *
 * @version 1.0
 * @author the-mascot
 * @since 2023.12.05
 */
@Slf4j
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain chain,
                                        Authentication authentication)
            throws IOException, ServletException {
        log.info("[LoginSuccessHandler] {} 님 login", authentication.getName());
        response.sendRedirect("/");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        log.info("[LoginSuccessHandler] {} 님 login", authentication.getName());
        response.sendRedirect("/");
    }
}
