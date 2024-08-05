package ces.neighborhood.blind.common.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * Request, Response logging 을 위한 필터
 * </pre>
 *
 * @version 1.0
 * @author mascot
 * @since 2023.12.27
 */
@Slf4j
public class ReqResLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;

            HttpServletRequest requestToCache = new ContentCachingRequestWrapper(httpServletRequest);
            HttpServletResponse responseToCache = new ContentCachingResponseWrapper(httpServletResponse);

            chain.doFilter(requestToCache, responseToCache);

            log.info("[ReqResLoggingFilter] method: {}, uri: {}", requestToCache.getMethod(), requestToCache.getRequestURI());
            log.info("[ReqResLoggingFilter] headers: {}", this.getHeaders(requestToCache));
            if (!StringUtils.equals(requestToCache.getMethod(), HttpMethod.GET.toString())) {
                log.info("[ReqResLoggingFilter] body: {}", this.getBody((ContentCachingRequestWrapper) requestToCache));
            }
            chain.doFilter(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    private String getHeaders(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            builder.append(headerNames);
            builder.append("=");
            builder.append(request.getHeader(headerNames.nextElement()));
            builder.append(", ");
        }
        builder.setLength(builder.length() - 2);
        return builder.toString();
    }

    private String getBody(HttpServletRequest request) {
        ContentCachingRequestWrapper requestWrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (requestWrapper != null) {
            byte[] buf = requestWrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    return new String(buf, 0, buf.length, requestWrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    return " - ";
                }
            }
        }
        return " - ";
    }

}
