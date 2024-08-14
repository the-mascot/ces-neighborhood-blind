package ces.neighborhood.blind.app.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import ces.neighborhood.blind.app.dto.AccessTokenResponseDto;
import ces.neighborhood.blind.app.dto.TokenDto;
import ces.neighborhood.blind.app.provider.JwtTokenProvider;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final ClientRegistrationRepository clientRegistrationRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = ((authorities) -> authorities);

    private final Oauth2UserServiceImpl userService;

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * JWT 로그인 인증
     * @param code, state
     * @return
     * @throws
     */
    public TokenDto authenticate(String registrationId, String code, String state) throws
            AuthenticationException {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponse
                .success(code)
                .state(state)
                .redirectUri(clientRegistration.getRedirectUri())
                .build();

        // 인증서버에 Access Token 요청
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<MultiValueMap<String, String>> requestEntity = this.getRequestEntity(clientRegistration, authorizationResponse);
        ResponseEntity<AccessTokenResponseDto> response = restTemplate.exchange(requestEntity, AccessTokenResponseDto.class);
        log.info("[OauthService - authenticate] response : {}", response);
        AccessTokenResponseDto accessTokenResponseDto = response.getBody();

        // Access Token
        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessTokenResponseDto.getAccessToken(), Instant.now(), Instant.now().plusSeconds(30));
        // Refresh Token
        OAuth2RefreshToken oAuth2RefreshToken = StringUtils.equals(clientRegistration.getRegistrationId(), "google") ? null : new OAuth2RefreshToken(accessTokenResponseDto.getRefreshToken(), Instant.now(), null);

        Map<String, Object> additionalParameters = new HashMap<>();
        // resource 서버에 userInfo 요청
        OAuth2User oauth2User = this.userService.loadUser(new OAuth2UserRequest(
                clientRegistration, oAuth2AccessToken, additionalParameters
        ));

        Authentication authentication = new UsernamePasswordAuthenticationToken(oauth2User.getName(), oauth2User.getAuthorities());
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        return jwtTokenProvider.createTokenDTO(accessToken, refreshToken);
    }

    /**
     * Access Token 요청 entity 생성
     * @param clientRegistration, authorizationResponse
     * @return Access Token 요청하기 위한 RequestEntity
     * @throws
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
        parameter.add(OAuth2ParameterNames.REDIRECT_URI, authorizationResponse.getRedirectUri());
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
        // client_secret_basic 인 경우 clientId, clientSecret URL Encoder 로 encoding 해서 Basic Auth 로 header 에 보내야함.
        // google, naver 둘다 현재 Basic 방식.
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.equals(clientRegistration.getClientAuthenticationMethod())) {
            String clientId = encodeClientCredential(clientRegistration.getClientId());
            String clientSecret = encodeClientCredential(clientRegistration.getClientSecret());
            headers.setBasicAuth(clientId, clientSecret);
        }
        return headers;
    }

    /**
     * client 정보 URL encoding
     */
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
