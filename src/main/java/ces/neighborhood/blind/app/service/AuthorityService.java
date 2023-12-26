package ces.neighborhood.blind.app.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    private RememberMeServices rememberMeServices = new NullRememberMeServices();


    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();

    private SecurityContextRepository
            securityContextRepository = new RequestAttributeSecurityContextRepository();

    public void login(LoginReqDto loginReqDto, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken unauthenticatedToken = new UsernamePasswordAuthenticationToken(
                loginReqDto.getUserId(),
                loginReqDto.getPassword()
        );
        Authentication authentication = authenticationManager.authenticate(unauthenticatedToken);

        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);
        this.rememberMeServices.loginSuccess(request, response, authentication);
    }

    /**
     * 회원가입 Service
     */
    public void joinMember(LoginReqDto loginReqDto) {
        memberRepository.save(convertToMbrInfo(loginReqDto));
    }

    /**
     * LoginReqDto -> MbrInfo convert
     */
    private MbrInfo convertToMbrInfo(LoginReqDto loginReqDto) {
        return MbrInfo.builder()
                .mbrId(loginReqDto.getUserId())
                .mbrPw(passwordEncoder.encode(loginReqDto.getPassword()))
                .role(Role.ROLE_MEMBER.getRoleName())
                .build();
    }

    public String authenticate(Map<String, Object> param) throws Exception {
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
