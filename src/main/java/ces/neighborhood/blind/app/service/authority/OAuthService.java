package ces.neighborhood.blind.app.service.authority;

import org.apache.commons.lang3.ObjectUtils;
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
import ces.neighborhood.blind.app.dto.CesAuthentication;
import ces.neighborhood.blind.app.dto.Role;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.entity.OauthMbrInfo;
import ces.neighborhood.blind.app.provider.JwtTokenProvider;
import ces.neighborhood.blind.app.record.authority.OAuthLoginRes;
import ces.neighborhood.blind.app.repository.MemberRepository;
import ces.neighborhood.blind.app.repository.OauthMbrInfoRepository;
import ces.neighborhood.blind.app.service.member.MemberService;
import ces.neighborhood.blind.common.code.ComCode;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    private static final String NAVER = "naver";

    private static final String NAVER_PROFILE_IMAGE = "profile_image";

    private static final String GOOGLE_PROFILE_IMAGE = "picture";

    private final MemberRepository memberRepository;

    private final OauthMbrInfoRepository oauthMbrInfoRepository;

    private final MemberService memberService;

    /**
     * JWT 로그인 인증
     * 1. 인증서버에 토큰 요청
     * 2. resource 서버에 userInfo 요청
     * 3. 기존 회원 확인 / 신규회원 가입
     * @param code, state
     * @return
     * @throws
     */
    public CesAuthentication authenticate(String registrationId, String code, String state) throws
            AuthenticationException {
        // authorization_code 응답 객체 생성
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        log.info("[OauthService - authenticate] clientRegistration : {}", clientRegistration);
        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponse
                .success(code)
                .state(state)
                .redirectUri(clientRegistration.getRedirectUri())
                .build();

        // 1. 인증서버에 토큰 요청
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<MultiValueMap<String, String>> requestEntity = this.getRequestEntity(clientRegistration, authorizationResponse);
        log.info("[OauthService - authenticate] requestEntity : {}", requestEntity);
        ResponseEntity<AccessTokenResponseDto> response = restTemplate.exchange(requestEntity, AccessTokenResponseDto.class);
        log.info("[OauthService - authenticate] response : {}", response);
        AccessTokenResponseDto accessTokenResponseDto = response.getBody();

        // Access Token
        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessTokenResponseDto.getAccessToken(), Instant.now(), Instant.now().plusSeconds(30));
        // Refresh Token
        OAuth2RefreshToken oAuth2RefreshToken = StringUtils.equals(clientRegistration.getRegistrationId(), "google") ? null : new OAuth2RefreshToken(accessTokenResponseDto.getRefreshToken(), Instant.now(), null);

        // 2. resource 서버에 userInfo 요청
        Map<String, Object> additionalParameters = new HashMap<>();
        OAuth2User oauth2User = this.userService.loadUser(new OAuth2UserRequest(
                clientRegistration, oAuth2AccessToken, additionalParameters
        ));
        // 응답 userAttributes -> mbrInfo 컨버팅
        MbrInfo responseMbrInfo = convertToMbrInfo(oauth2User.getAttributes(), registrationId);

        // 3. 기존 회원 확인 / 신규회원 가입
        Optional<MbrInfo> optionalMbrInfo = memberRepository.findById(responseMbrInfo.getMbrId());

        if(optionalMbrInfo.isPresent()) {
            /*기존 회원의 경우 마지막 로그인 날짜 업데이트.
            기존에 프로필 사진 없고, oAuth에서 제공하는 프로필 사진 있는 경우 회원 프로필 사진 업데이트.*/
            MbrInfo mbrInfo = optionalMbrInfo.get();
            mbrInfo.setLastLoginDate(Timestamp.valueOf(LocalDateTime.now()));
            if (StringUtils.isBlank(mbrInfo.getMbrProfileImageUrl()) && responseMbrInfo.getMbrProfileImageUrl() != null) {
                mbrInfo.setMbrProfileImageUrl(responseMbrInfo.getMbrProfileImageUrl());
            }
        } else {
            // unique 닉네임 생성
            String nickname = memberService.generateRandomNickname();
            MbrInfo mbrInfo = MbrInfo.builder()
                    .mbrId(responseMbrInfo.getMbrId())
                    .mbrNickname(nickname)
                    .mbrStd(ComCode.MBR_STD_ACTIVE.getCode())
                    .mbrProfileImageUrl(responseMbrInfo.getMbrProfileImageUrl())
                    .role(Role.ROLE_MEMBER.getRoleName())
                    .build();

            // mbrInfo 저장
            memberRepository.save(mbrInfo);
        }
        // oauthMbrInfo 저장
        oauthMbrInfoRepository.save(convertToOauthMbrInfo(oauth2User.getAttributes(), registrationId));
        MbrInfo mbrInfo = memberRepository.findById(responseMbrInfo.getMbrId()).get();

        Authentication authentication = new UsernamePasswordAuthenticationToken(mbrInfo.getMbrId(), null, mbrInfo.getAuthorities());
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        OAuthLoginRes oAuthLoginRes = new OAuthLoginRes(optionalMbrInfo.isEmpty(), mbrInfo.getMbrNickname(), mbrInfo.getMbrProfileImageUrl());
        CesAuthentication authenticate = new CesAuthentication(null, oAuthLoginRes, jwtTokenProvider.createTokenDTO(accessToken, refreshToken));
        return authenticate;
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

    /**
     * attributes -> MbrInfo convert
     * @param userAttributes
     * @return attributes -> MbrInfo entity로 변환
     * @throws
     */
    private MbrInfo convertToMbrInfo(Map<String, Object> userAttributes, String registrationId) {
        if (userAttributes == null || userAttributes.get("email") == null) {
            throw new BizException(ErrorCode.CODE_1101);
        }
        String mbrId = (String) userAttributes.get("email");
        String profileImage = ObjectUtils.defaultIfNull(
                (String) userAttributes.get(registrationId.equals(NAVER) ?
                        NAVER_PROFILE_IMAGE : GOOGLE_PROFILE_IMAGE), null);
        String nickname = ObjectUtils.defaultIfNull((String) userAttributes.get("nickname"), null);

        return MbrInfo.builder()
                .mbrId(mbrId)
                .role(Role.ROLE_MEMBER.getRoleName())
                .mbrNickname(nickname)
                .mbrProfileImageUrl(profileImage)
                .mbrStd(ComCode.MBR_STD_ACTIVE.getCode())
                .build();
    }

    /**
     * attributes -> OauthMbrInfo convert
     * @param attributes, registrationId
     * @return attributes -> OauthMbrInfo entity로 변환
     * @throws
     */
    private OauthMbrInfo convertToOauthMbrInfo(Map<String, Object> attributes, String registrationId) {
        return OauthMbrInfo.builder()
                .oauthMbrInfoKey(OauthMbrInfo.OauthMbrInfoKey.builder()
                        .oauthId(String.valueOf(attributes.get(StringUtils.equals(NAVER, registrationId) ? "id" : "sub")))
                        .provider(registrationId)
                        .build())
                .mbrInfo(new MbrInfo(String.valueOf(attributes.get("email"))))
                .build();
    }
}
