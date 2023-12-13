package ces.neighborhood.blind.app.provider;

import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.service.UserDetailServiceImpl;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final UserDetailServiceImpl userDetailService;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = userDetailService.loadUserByUsername(authentication.getName());
        this.credentialChecks(authentication, userDetails);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
        usernamePasswordAuthenticationToken.setDetails(authentication.getDetails());
        return usernamePasswordAuthenticationToken;
    }

    protected void credentialChecks(Authentication authentication, UserDetails userDetails) {
        if (!this.passwordEncoder.matches(String.valueOf(authentication.getCredentials()), userDetails.getPassword())) {
            throw new BadCredentialsException(ErrorCode.CODE_1002.getMessage());
        }
    }

    // Spring Security에게 UsernamePassword 방식을 지원한다는 것을 알려주는 역할
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
