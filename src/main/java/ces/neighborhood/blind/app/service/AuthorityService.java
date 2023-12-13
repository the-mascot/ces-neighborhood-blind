package ces.neighborhood.blind.app.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import ces.neighborhood.blind.app.dto.LoginReqDto;
import ces.neighborhood.blind.app.dto.Role;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.repository.MemberRepository;
import ces.neighborhood.blind.common.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
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

    public void joinMember(LoginReqDto loginReqDto) {
        memberRepository.save(convertToMbrInfo(loginReqDto));
    }

    private MbrInfo convertToMbrInfo(LoginReqDto loginReqDto) {
        return MbrInfo.builder()
                .mbrId(loginReqDto.getUserId())
                .mbrPw(passwordEncoder.encode(loginReqDto.getPassword()))
                .role(Role.ROLE_MEMBER.getRoleName())
                .build();
    }

}
