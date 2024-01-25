package ces.neighborhood.blind.app.provider;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * OAuth2 Authorization Code 발급 요청시 추가 파라미터를 위한 Request Resolver
 * Google OAuth 리프레시 토큰 발급 테스트를 위해 추가됨. 현재 사용 x
 * </pre>
 *
 * @deprecated
 * @version 1.0
 * @author mascot
 * @since 2023.12.18
 */
@Component
public class CustomOauth2AuthorizationRequestResolver implements
        OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    public static final String CUSTOM_AUTHORIZATION_REQUEST_BASE_URI = "/oauth2/authorization";

    public CustomOauth2AuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
                CUSTOM_AUTHORIZATION_REQUEST_BASE_URI);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest  = this.defaultResolver.resolve(request);

        return authorizationRequest != null ?
                customAuthorizationRequest(authorizationRequest) :
                null;

    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request,
                                              String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = this.defaultResolver.resolve(request, clientRegistrationId);

        return authorizationRequest != null ?
                customAuthorizationRequest(authorizationRequest) :
                null;
    }

    /**
     * Authorization Code 요청 추가 파라미터 설정
     * @param authorizationRequest
     * @return OAuth2AuthorizationRequest
     * @throws
     */
    private OAuth2AuthorizationRequest customAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest) {
        Map<String, Object> additionalParameters = new LinkedHashMap<>(authorizationRequest.getAdditionalParameters());
        // Google OAuth는 정보제공 동의화면이 나오는 첫 요청에 대해서만 Refresh Token 을 제공한다.
        // 테스트 용으로 access_type 을 offline 으로 하면 동의화면이 뜨면서 refreshToken 을 받을 수 있다.
        //additionalParameters.put("access_type", "offline");

        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .additionalParameters(additionalParameters)
                .build();
    }
}
