package ces.neighborhood.blind.app.provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import ces.neighborhood.blind.app.dto.AccessTokenResponseDto;
import ces.neighborhood.blind.app.service.Oauth2UserServiceImpl;
import ces.neighborhood.blind.common.config.WebClientConfig;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationProviderImpl implements
        AuthenticationProvider {

    private static final String INVALID_STATE_PARAMETER_ERROR_CODE = "invalid_state_parameter";

    private GrantedAuthoritiesMapper authoritiesMapper = ((authorities) -> authorities);

    private final Oauth2UserServiceImpl userService;

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
        // Access Token 요청
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<MultiValueMap<String, String>> requestEntity = this.getRequestEntity(clientRegistration, authorizationResponse);
        ResponseEntity<AccessTokenResponseDto> response = restTemplate.exchange(requestEntity, AccessTokenResponseDto.class);
        AccessTokenResponseDto accessTokenResponseDto = response.getBody();
        // Access Token
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessTokenResponseDto.getAccessToken(), Instant.now(), Instant.now().plusSeconds(30));
        // Refresh Token
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(accessTokenResponseDto.getRefreshToken(), Instant.now(), null);
        Map<String, Object> additionalParameters = new HashMap<>();
        OAuth2User oauth2User = this.userService.loadUser(new OAuth2UserRequest(
                clientRegistration, accessToken, additionalParameters
        ));

        Collection<? extends GrantedAuthority> mappedAuthorities = this.authoritiesMapper.mapAuthorities(oauth2User.getAuthorities());

        OAuth2LoginAuthenticationToken authenticationResult = new OAuth2LoginAuthenticationToken(
                authorizationCodeAuthentication.getClientRegistration(),
                authorizationCodeAuthentication.getAuthorizationExchange(),
                oauth2User,
                mappedAuthorities,
                accessToken,
                refreshToken);
        authenticationResult.setDetails(authorizationCodeAuthentication.getDetails());

        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * Access Token 요청 entity 생성
     */
    private RequestEntity<MultiValueMap<String, String>> getRequestEntity(ClientRegistration clientRegistration, OAuth2AuthorizationResponse authorizationResponse) {
        MultiValueMap<String, String> parameter = this.convertParameter(clientRegistration, authorizationResponse);
        HttpHeaders headers = this.convertHeaders(clientRegistration);

        URI uri = UriComponentsBuilder
                .fromUriString(clientRegistration.getProviderDetails().getTokenUri())
                .build()
                .toUri();
        return new RequestEntity<>(parameter, headers, HttpMethod.POST, uri);
    }

    /**
     * Access Token 요청 body 변환
     */
    private MultiValueMap<String, String> convertParameter(ClientRegistration clientRegistration, OAuth2AuthorizationResponse authorizationResponse) {
        MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        parameter.add(OAuth2ParameterNames.GRANT_TYPE, clientRegistration.getAuthorizationGrantType().getValue());
        parameter.add(OAuth2ParameterNames.CODE, authorizationResponse.getCode());
        return parameter;
    }

    /**
     * Access Token 요청 header 변환
     */
    private HttpHeaders convertHeaders(ClientRegistration clientRegistration) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setAcceptCharset(List.of(Charset.forName("UTF-8")));
        final MediaType contentType = MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
        headers.setContentType(contentType);
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.equals(clientRegistration.getClientAuthenticationMethod())) {
            String clientId = encodeClientCredential(clientRegistration.getClientId());
            String clientSecret = encodeClientCredential(clientRegistration.getClientSecret());
            headers.setBasicAuth(clientId, clientSecret);
        }
        return headers;
    }

    public static String encodeClientCredential(String clientCredential) {
        try {
            return URLEncoder.encode(clientCredential, StandardCharsets.UTF_8.toString());
        }
        catch (UnsupportedEncodingException ex) {
            // Will not happen since UTF-8 is a standard charset
            throw new IllegalArgumentException(ex);
        }
    }

}
