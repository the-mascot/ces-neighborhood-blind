package ces.neighborhood.blind.app.provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import ces.neighborhood.blind.app.dto.AccessTokenRequestDto;
import ces.neighborhood.blind.app.dto.AccessTokenResponseDto;
import ces.neighborhood.blind.common.config.WebClientConfig;
import ces.neighborhood.blind.common.utils.ComUtils;
import jdk.jfr.ContentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationProviderImpl implements
        AuthenticationProvider {

    private static final String INVALID_STATE_PARAMETER_ERROR_CODE = "invalid_state_parameter";

    private GrantedAuthoritiesMapper authoritiesMapper = ((authorities) -> authorities);

    private final WebClientConfig webClientConfig;

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
        AccessTokenRequestDto accessTokenRequestDto = AccessTokenRequestDto.builder()
                .grantType(clientRegistration.getAuthorizationGrantType().getValue())
                .clientId(clientRegistration.getClientId())
                .clientSecret(clientRegistration.getClientSecret())
                .code(authorizationResponse.getCode())
                .state(authorizationRequest.getState())
                .build();

        ResponseEntity<AccessTokenResponseDto> response = webClientConfig.webClient()
                .post()
                .uri(clientRegistration.getProviderDetails().getTokenUri())
                .body(BodyInserters.fromValue(accessTokenRequestDto))
                .retrieve()
                .toEntity(AccessTokenResponseDto.class)
                .block();

        //OAuth2AccessTokenResponse accessTokenResponse = response.getBody();

//        OAuth2AuthorizationCodeAuthenticationToken authenticationResult = new OAuth2AuthorizationCodeAuthenticationToken(
//                authorizationCodeAuthentication.getClientRegistration(),
//                authorizationCodeAuthentication.getAuthorizationExchange(), accessTokenResponse.getAccessToken(),
//                accessTokenResponse.getRefreshToken(), accessTokenResponse.getAdditionalParameters());
//        authenticationResult.setDetails(authorizationCodeAuthentication.getDetails());

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private String getNaverTokenReqUri(ClientRegistration clientRegistration,OAuth2AuthorizationRequest authorizationRequest, OAuth2AuthorizationResponse authorizationResponse) {
        return UriComponentsBuilder
                .fromUriString(clientRegistration.getProviderDetails().getTokenUri())
                .queryParam("grant_type", clientRegistration.getAuthorizationGrantType().getValue())
                .queryParam("client_id", clientRegistration.getClientId())
                .queryParam("client_secret", clientRegistration.getClientSecret())
                .queryParam("code", authorizationResponse.getCode())
                .queryParam("state", authorizationRequest.getState())
                .toUriString();
    }

}
