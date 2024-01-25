package ces.neighborhood.blind.common.config;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpClientTransport;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpField;
import org.springframework.stereotype.Component;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * WebClient 요청 시 Logging 을 위한 커스텀 HttpClient
 * TODO: WebClient 요청 사용 시 정상동작 테스트 해봐야함.
 * </pre>
 *
 * @see WebClientConfig
 * @version 1.0
 * @author the-mascot
 * @since 2023.12.26
 */
@Slf4j
@Component
public class CustomHttpClient extends HttpClient {

    public CustomHttpClient() {
        super();
    }

    public CustomHttpClient(HttpClientTransport transport) {
        super(transport);
    }

    @Override
    public Request newRequest(URI uri) {
        Request request = super.newRequest(uri);
        return enhance(request);
    }

    public Request enhance(Request request) {
        StringBuilder builder = new StringBuilder();
        request.onRequestBegin(theRequest -> {
            builder.append("[Http Request Client] method : ");
            builder.append(theRequest.getMethod());
            builder.append(", uri: ");
            builder.append(theRequest.getURI());
        });
        request.onRequestHeaders(theRequest -> {
            builder.append("\n headers: ");
            for (HttpField header : theRequest.getHeaders()) {
                builder.append(header.getName());
                builder.append(":");
                builder.append(header.getValues());
                builder.append(", ");
            }
            builder.setLength(builder.length() - 2);
        });
        request.onRequestContent((theRequest, content) -> {
            builder.append("\n body: ");
            builder.append(content.toString());
        });
        request.onRequestSuccess(theRequest -> {
            log.error(builder.toString());
            builder.delete(0, builder.length());
        });
        builder.append("\n");
        request.onResponseBegin(theResponse -> {
            // append response status to group
        });
        request.onResponseHeaders(theResponse -> {
            for (HttpField header : theResponse.getHeaders()) {
                // append response headers to group
            }
        });
        request.onResponseContent((theResponse, content) -> {
            // append content to group
        });
        request.onResponseSuccess(theResponse -> {
            log.debug(builder.toString());
        });
        return request;
    }
}
