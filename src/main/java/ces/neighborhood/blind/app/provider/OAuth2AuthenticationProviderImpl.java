package ces.neighborhood.blind.app.provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import ces.neighborhood.blind.app.dto.AccessTokenRequestDto;
import ces.neighborhood.blind.app.dto.AccessTokenResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationProviderImpl implements
        AuthenticationProvider {

    private static final String INVALID_STATE_PARAMETER_ERROR_CODE = "invalid_state_parameter";

    private GrantedAuthoritiesMapper authoritiesMapper = ((authorities) -> authorities);

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        OAuth2LoginAuthenticationToken authorizationCodeAuthentication = (OAuth2LoginAuthenticationToken) authentication;

        OAuth2AuthorizationRequest authorizationRequest = authorizationCodeAuthentication.getAuthorizationExchange().getAuthorizationRequest();
        // scope에 openid가 있으면 OIDC Provider를 태우기 위해 return null 처리
        if (authorizationCodeAuthentication.getAuthorizationExchange()
                .getAuthorizationRequest()
                .getScopes()
                .contains("openid")) {
            return null;
        }
        // authorizaton code 응답 에러확인
        OAuth2AuthorizationResponse authorizationResponse = authorizationCodeAuthentication.getAuthorizationExchange().getAuthorizationResponse();
        if (authorizationResponse.statusError()) {
            throw new OAuth2AuthorizationException(authorizationResponse.getError());
        }
        // 로그인요청시 state와 redirect 응답의 state가 같은지 확인
        if (!StringUtils.equals(authorizationRequest.getState(), authorizationResponse.getState())) {
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_STATE_PARAMETER_ERROR_CODE);
            throw new OAuth2AuthorizationException(oauth2Error);
        }

        ClientRegistration clientRegistration = authorizationCodeAuthentication.getClientRegistration();
        WebClient webClient = WebClient.builder()
                .baseUrl(clientRegistration.getProviderDetails().getTokenUri())
                .build();
        AccessTokenRequestDto accessTokenRequestDto = AccessTokenRequestDto.builder()
                .grantType("authorization_code")
                .clientId(clientRegistration.getClientId())
                .clientSecret(clientRegistration.getClientSecret())
                .code(authorizationResponse.getCode())
                .state(authorizationRequest.getState())
                .build();
        String response = webClient.post()
                .body(BodyInserters.fromValue(accessTokenRequestDto))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
