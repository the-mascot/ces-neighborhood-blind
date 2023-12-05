package ces.neighborhood.blind.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain chain,
                                        Authentication authentication)
            throws IOException, ServletException {
        //SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("[LoginSuccessHandler] {} 님 login" + authentication.getName());
        response.sendRedirect("/");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        log.debug("[LoginSuccessHandler] {} 님 login" + authentication.getName());
        response.sendRedirect("/");
    }
}
