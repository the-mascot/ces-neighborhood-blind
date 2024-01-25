package ces.neighborhood.blind.app.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ces.neighborhood.blind.app.dto.LoginReqDto;
import ces.neighborhood.blind.app.dto.Role;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.repository.MemberRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 회원가입, 로그인 및 권한 관련 Service
 * </pre>
 *
 * @version 1.0
 * @author mascot
 * @since 2023.11.27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    private RememberMeServices rememberMeServices = new NullRememberMeServices();


    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    private SecurityContextRepository
            securityContextRepository = new RequestAttributeSecurityContextRepository();

    /**
     * 회원가입 Service
     * @param loginReqDto
     * @return
     * @throws
     */
    public void joinMember(LoginReqDto loginReqDto) {
        memberRepository.save(convertToMbrInfo(loginReqDto));
    }

    /**
     * LoginReqDto -> MbrInfo convert
     * @param loginReqDto
     * @return LoginReqDto -> MbrInfo entity로 변환
     * @throws
     */
    private MbrInfo convertToMbrInfo(LoginReqDto loginReqDto) {
        return MbrInfo.builder()
                .mbrId(loginReqDto.getUserId())
                .mbrPw(passwordEncoder.encode(loginReqDto.getPassword()))
                .role(Role.ROLE_MEMBER.getRoleName())
                .build();
    }

    /**
     * 네이버 로그인 Authorization Code 요청
     * Spring OAuth 가 아닌 직접 요청 구현을 위해 작성. 현재 Spring OAuth 사용으로 사용 x
     * @param param
     * @return Authorization Code 요청 response
     * @throws
     */
    public String authenticate(Map<String, Object> param) {
        WebClient webClient = WebClient.builder().baseUrl("https://nid.naver.com/oauth2.0/authorize").build();
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("response_type", "code")
                        .queryParam("client_id", "UpEJjnGzwwLj_hk4mbB6")
                        .queryParam("redirect_uri", "http%3a%2f%2flocalhost%3a8010%2flogin%2foauth2%2fcode%2fnaver")
                        .queryParam("state", "1234")
                        .build()
                ).retrieve()
                .bodyToMono(String.class)
                .block();
                //.queryParam("redirect_uri", "http://localhost:8010/login/oauth2/code/naver")
        log.error(response);
        return response;
    }
}
