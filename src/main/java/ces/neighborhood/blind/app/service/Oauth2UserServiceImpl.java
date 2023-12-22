package ces.neighborhood.blind.app.service;


import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import ces.neighborhood.blind.common.code.ComCode;
import ces.neighborhood.blind.app.dto.Role;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.entity.SnsMbrInfo;
import ces.neighborhood.blind.app.entity.SnsMbrInfoKey;
import ces.neighborhood.blind.app.repository.MemberRepository;
import ces.neighborhood.blind.app.repository.Oauth2UserRepository;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String NAVER = "naver";

    private final MemberRepository memberRepository;

    private final Oauth2UserRepository oauth2UserRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {
        // delegate로 DefaultOAuth2UserService의 loadUser를 호출해 Oauth2User를 받아온다.
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        // 공급자 ID ex) google, naver. SNS_TYPE으로 사용
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 사용자 식별 이름 google : sub, naver : response
        String userNameAttributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        if (StringUtils.isEmpty(userNameAttributeName)) {
            throw new BizException(ErrorCode.CODE_1100);
        }

        Map<String, Object> attributes = StringUtils.equals(registrationId, NAVER) ? oauth2User.getAttribute(userNameAttributeName) : oauth2User.getAttributes();

        if (attributes == null || attributes.get("email") == null) {
            throw new BizException(ErrorCode.CODE_1101);
        }

        // mbrInfo 저장
        memberRepository.save(convertToMbrInfo(attributes));
        // snsMbrInfo 저장
        oauth2UserRepository.save(convertToSnsMbrInfo(attributes, registrationId));
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.add(new SimpleGrantedAuthority(Role.ROLE_MEMBER.getRoleName()));

        return new DefaultOAuth2User(authorities, attributes, "email");
    }

    public void saveRefreshToken(OAuth2User oAuth2User, OAuth2RefreshToken refreshToken) {
        oauth2UserRepository.save(SnsMbrInfo
                .builder()
                .snsMbrInfoKey(
                        SnsMbrInfoKey.builder()
                                .snsId((String) oAuth2User.getAttribute("email"))
                                .snsType((String) oAuth2User.getAttribute("snsType"))
                                .build()
                )
                .refreshToken((String) refreshToken.getTokenValue())
                .build());
    }

    private MbrInfo convertToMbrInfo(Map<String, Object> attributes) {
        return MbrInfo.builder()
                .mbrId(String.valueOf(attributes.get("email")))
                .role(Role.ROLE_MEMBER.getRoleName())
                .mbrNm(String.valueOf(attributes.get("name")))
                .mbrEmail(String.valueOf(attributes.get("email")))
                .mbrStd(ComCode.MBR_STD_ACTIVE.getCode())
                .build();
    }

    private SnsMbrInfo convertToSnsMbrInfo(Map<String, Object> attributes, String registrationId) {
        return SnsMbrInfo.builder()
                .snsMbrInfoKey(SnsMbrInfoKey.builder()
                        .snsId(String.valueOf(attributes.get(StringUtils.equals(NAVER, registrationId) ? "id" : "sub")))
                        .snsType(registrationId)
                        .build())
                .snsName(String.valueOf(attributes.get("name")))
                .mbrId(String.valueOf(attributes.get("email")))
                .build();
    }
}
