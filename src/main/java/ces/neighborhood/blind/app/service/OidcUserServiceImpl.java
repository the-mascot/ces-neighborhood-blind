package ces.neighborhood.blind.app.service;

import static org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService.createDefaultClaimTypeConverters;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.converter.ClaimTypeConverter;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import ces.neighborhood.blind.app.dto.Role;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.repository.MemberRepository;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * Oidc(OpenID Connect) UserService 구현체
 * </pre>
 *
 * @see OAuth2UserService
 * @see org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
 * @version 1.0
 * @author mascot
 * @since 2023.12.15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OidcUserServiceImpl implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";

    private static final Converter<Map<String, Object>, Map<String, Object>> DEFAULT_CLAIM_TYPE_CONVERTER = new ClaimTypeConverter(
            createDefaultClaimTypeConverters());

    private Function<ClientRegistration, Converter<Map<String, Object>, Map<String, Object>>> claimTypeConverterFactory = (
            clientRegistration) -> DEFAULT_CLAIM_TYPE_CONVERTER;

    private final MemberRepository memberRepository;

    /**
     * Resource 서버에서 받아온 userInfo DB 저장
     * @see org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService#loadUser(OidcUserRequest)
     * @param userRequest
     * @return OAuth2User
     * @throws
     */
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest)
            throws OAuth2AuthenticationException {
        Assert.notNull(userRequest, "userRequest cannot be null");
        OidcUserInfo userInfo = null;
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> claims = getClaims(userRequest, oauth2User);
        userInfo = new OidcUserInfo(claims);

        if (userInfo.getSubject() == null) {
            throw new OAuth2AuthenticationException(INVALID_USER_INFO_RESPONSE_ERROR_CODE);
        }

        if (!userInfo.getSubject().equals(userRequest.getIdToken().getSubject())) {
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String attributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        Map<String, Object> attributes = StringUtils.equals(registrationId, "naver") ? oauth2User.getAttribute(attributeName) : oauth2User.getAttributes();

        if (attributes == null || attributes.get("email") == null) {
            throw new BizException(ErrorCode.CODE_1005);
        }

        memberRepository.save(convertToMbrInfo(attributes));

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.add(new OidcUserAuthority(userRequest.getIdToken(), userInfo));
        return getUser(userRequest, userInfo, authorities);
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
                .build();
    }

    /**
     * ID Token claims 가져오기
     * @param userRequest, oauth2User
     * @return ID Token claims에 있는 userInfo
     * @throws
     */
    private Map<String, Object> getClaims(OidcUserRequest userRequest, OAuth2User oauth2User) {
        Converter<Map<String, Object>, Map<String, Object>> converter = this.claimTypeConverterFactory
                .apply(userRequest.getClientRegistration());
        if (converter != null) {
            return converter.convert(oauth2User.getAttributes());
        }
        return DEFAULT_CLAIM_TYPE_CONVERTER.convert(oauth2User.getAttributes());
    }

    /**
     * Default OidcUser 생성
     * @param userRequest, userInfo, authorities
     * @return OidcUser
     * @throws
     */
    private OidcUser getUser(OidcUserRequest userRequest, OidcUserInfo userInfo, Set<GrantedAuthority> authorities) {
        ClientRegistration.ProviderDetails providerDetails = userRequest.getClientRegistration().getProviderDetails();
        String userNameAttributeName = providerDetails.getUserInfoEndpoint().getUserNameAttributeName();
        if (StringUtils.isEmpty(userNameAttributeName)) {
            return new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo, userNameAttributeName);
        }
        return new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo);
    }

}
