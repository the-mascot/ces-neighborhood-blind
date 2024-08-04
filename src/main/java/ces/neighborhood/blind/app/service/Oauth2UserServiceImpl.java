package ces.neighborhood.blind.app.service;


import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import ces.neighborhood.blind.app.dto.Role;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.entity.SnsMbrInfo;
import ces.neighborhood.blind.app.repository.MemberRepository;
import ces.neighborhood.blind.app.repository.Oauth2UserRepository;
import ces.neighborhood.blind.common.code.ComCode;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * Oauth2 UserService 구현체
 * </pre>
 *
 * @see OAuth2UserService
 * @see DefaultOAuth2UserService
 * @version 1.0
 * @author mascot
 * @since 2023.12.07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String NAVER = "naver";

    private static final String MISSING_USER_INFO_URI_ERROR_CODE = "missing_user_info_uri";

    private static final String MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE = "missing_user_name_attribute";

    private final MemberRepository memberRepository;

    private final Oauth2UserRepository oauth2UserRepository;

    /**
     * Resource 서버에서 받아온 userInfo DB 저장
     * @see DefaultOAuth2UserService#loadUser(OAuth2UserRequest)
     * @param userRequest
     * @return OAuth2User
     * @throws
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {
        // userRequest Validation
        Assert.notNull(userRequest, "userRequest cannot be null");
        // userInfoEndpoint URI Validation
        if (StringUtils.isEmpty(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())) {
            OAuth2Error oauth2Error =
                    new OAuth2Error(MISSING_USER_INFO_URI_ERROR_CODE,
                            "Missing required UserInfo Uri in UserInfoEndpoint for Client Registration: "
                                    + userRequest.getClientRegistration()
                                    .getRegistrationId(),
                            null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        // 사용자 식별 이름 google : sub, naver : response
        String userNameAttributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // userNameAttributeName Validation
        if (StringUtils.isEmpty(userNameAttributeName)) {
            OAuth2Error oauth2Error = new OAuth2Error(MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE,
                    "Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: "
                            + userRequest.getClientRegistration().getRegistrationId(),
                    null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        // 공급자 ID ex) google, naver. SNS_TYPE으로 사용
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // userInfo 요청 entity 생성
        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<MultiValueMap<String, String>> requestEntity = this.getRequestEntity(userRequest);
        // resource 서버에 userInfo 요청
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<>() {});
        Map<String, Object> userAttributes = StringUtils.equals(registrationId, NAVER) ? (Map<String, Object>) response.getBody().get(userNameAttributeName) : response.getBody();

        if (userAttributes == null || userAttributes.get("email") == null) {
            throw new BizException(ErrorCode.CODE_1101);
        }

        // mbrInfo 저장
        memberRepository.save(convertToMbrInfo(userAttributes));
        // snsMbrInfo 저장
        oauth2UserRepository.save(convertToSnsMbrInfo(userAttributes, registrationId));
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.add(new SimpleGrantedAuthority(Role.ROLE_MEMBER.getRoleName()));

        return new DefaultOAuth2User(authorities, userAttributes, "email");
    }

    /**
     * RefreshToken 저장
     * 현재는 (Session 을 이용한 로그인 사용 + OAuth2 유저정보 DB 저장) 형태라 refreshToken 을 사용하지 않으나,
     * JWT 로그인 사용 할 경우 Access Token 을 재활성화 하기 위해 refresh Token 저장.
     * @param oAuth2User, refreshToken
     * @return
     * @throws
     */
    public void saveRefreshToken(OAuth2User oAuth2User, OAuth2RefreshToken refreshToken) {
        oauth2UserRepository.save(SnsMbrInfo
                .builder()
                .snsMbrInfoKey(
                        SnsMbrInfo.SnsMbrInfoKey.builder()
                                .snsId((String) oAuth2User.getAttribute("email"))
                                .snsType((String) oAuth2User.getAttribute("snsType"))
                                .build()
                )
                .refreshToken((String) refreshToken.getTokenValue())
                .build());
    }

    /**
     * attributes -> MbrInfo convert
     * @param attributes
     * @return attributes -> MbrInfo entity로 변환
     * @throws
     */
    private MbrInfo convertToMbrInfo(Map<String, Object> attributes) {
        return MbrInfo.builder()
                .mbrId(attributes.get("email").toString())
                .role(Role.ROLE_MEMBER.getRoleName())
                .mbrNm(String.valueOf(attributes.get("name")))
                .mbrEmail(String.valueOf(attributes.get("email")))
                .mbrStd(ComCode.MBR_STD_ACTIVE.getCode())
                .build();
    }

    /**
     * attributes -> SnsMbrInfo convert
     * @param attributes, registrationId
     * @return attributes -> SnsMbrInfo entity로 변환
     * @throws
     */
    private SnsMbrInfo convertToSnsMbrInfo(Map<String, Object> attributes, String registrationId) {
        return SnsMbrInfo.builder()
                .snsMbrInfoKey(SnsMbrInfo.SnsMbrInfoKey.builder()
                        .snsId(String.valueOf(attributes.get(StringUtils.equals(NAVER, registrationId) ? "id" : "sub")))
                        .snsType(registrationId)
                        .build())
                .snsName(String.valueOf(attributes.get("name")))
                .mbrId(String.valueOf(attributes.get("email")))
                .build();
    }

    /**
     * userInfo 요청 entity 생성
     * @see org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter#convert(OAuth2UserRequest)
     * @param userRequest
     * @return Resource 서버에 userInfo 를 요청하기 위한 Request Entity
     * @throws
     */
    private RequestEntity<MultiValueMap<String, String>> getRequestEntity(OAuth2UserRequest userRequest) {
        // userInfo 요청 entity body 생성
        MultiValueMap<String, String> parameter = this.convertParameter(userRequest);
        // userInfo 요청 entity header 생성
        HttpHeaders headers = this.convertHeaders();

        URI uri = UriComponentsBuilder
                .fromUriString(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())
                .build()
                .toUri();
        return new RequestEntity<>(parameter, headers, HttpMethod.POST, uri);
    }

    /**
     * userInfo 요청 entity body 생성
     */
    private MultiValueMap<String, String> convertParameter(OAuth2UserRequest userRequest) {
        MultiValueMap<String, String> parameter = new LinkedMultiValueMap<>();
        parameter.add(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue());
        return parameter;
    }

    /**
     * userInfo 요청 entity header 생성
     */
    private HttpHeaders convertHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setAcceptCharset(List.of(Charset.forName("UTF-8")));
        final MediaType contentType = MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
        headers.setContentType(contentType);
        return headers;
    }
}
