package ces.neighborhood.blind.common.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import ces.neighborhood.blind.app.provider.JwtTokenProvider;
import ces.neighborhood.blind.common.code.Constant;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * JwtFilter - 토큰 검사 필터 OncePerRequestFilter 상속으로 디스패처 포워딩 되도 한번만 실행됨
 * </pre>
 *
 * @version 1.0
 * @author the-mascot
 */
@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private static final List<String> SKIP_URLS = Arrays.asList(
            "/api/v1/auth/",
            "/static"
            );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if (isSkipUrl(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // access 토큰 resolve
        String accessToken = jwtTokenProvider.resolveToken(request, Constant.ACCESS_TOKEN_HEADER_NAME);
        log.debug("[JwtTokenFilter] token : {}", accessToken);

        // 토큰 validation
        int result = jwtTokenProvider.validateToken(accessToken);

        if (result == 1) {  // 토큰 유효
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (result == 2) {   // 엑세스 토큰 만료
            // refresh 토큰 resolve
            String refreshToken = jwtTokenProvider.resolveToken(request, Constant.REFRESH_TOKEN_HEADER_NAME);
            // refresh 토큰으로 access 토큰 재발급
            String freshAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken);

            response.addHeader(Constant.ACCESS_TOKEN_HEADER_NAME, freshAccessToken);
        } else {
            // 유효하지 않은 토큰
            throw new BizException(ErrorCode.CODE_1121);
        }
        filterChain.doFilter(request, response);
    }

    private boolean isSkipUrl(String requestUri) {
        return SKIP_URLS.stream().anyMatch(requestUri::startsWith);
    }
}
