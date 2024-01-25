package ces.neighborhood.blind.app.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Component;

import ces.neighborhood.blind.app.service.UserDetailServiceImpl;
import ces.neighborhood.blind.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * Spring Security 로그인 인증 Provider, AuthenticationProvider 구현체
 * </pre>
 *
 * @see AuthenticationProvider
 * @see org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
 * @see org.springframework.security.authentication.dao.DaoAuthenticationProvider
 * @version 1.0
 * @author mascot
 * @since 2023.12.07
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final UserDetailServiceImpl userDetailService;

    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Spring Security 로그인 인증
     * @see org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider#authenticate(Authentication)
     * @param authentication
     * @return Authentication
     * @throws
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // DB User 정보 조회
        UserDetails userDetails = userDetailService.loadUserByUsername(authentication.getName());
        // 비밀번호 체크
        this.credentialChecks(authentication, userDetails);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
        // Details : remoteAddress(ip), sessionID
        usernamePasswordAuthenticationToken.setDetails(authentication.getDetails());
        return usernamePasswordAuthenticationToken;
    }

    /**
     * Bcrypt 비밀번호 확인
     * authentication: 입력된 비밀번호, userDetails: DB 저장된 비밀번호
     * @param authentication, userDetails
     * @return
     * @throws
     */
    protected void credentialChecks(Authentication authentication, UserDetails userDetails) {
        if (!this.passwordEncoder.matches(String.valueOf(authentication.getCredentials()), userDetails.getPassword())) {
            throw new BadCredentialsException(ErrorCode.CODE_1002.getMessage());
        }
    }

    // Spring Security에게 UsernamePassword 방식을 지원한다는 것을 알려주는 역할
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
