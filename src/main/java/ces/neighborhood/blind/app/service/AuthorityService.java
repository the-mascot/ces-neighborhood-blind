package ces.neighborhood.blind.app.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import ces.neighborhood.blind.app.dto.LoginReqDto;
import ces.neighborhood.blind.app.dto.Role;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.repository.MemberRepository;
import ces.neighborhood.blind.common.exception.BizException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    public String login(LoginReqDto loginReqDto, Model model) {
        String redirectUrl;
        UsernamePasswordAuthenticationToken unauthenticatedToken = new UsernamePasswordAuthenticationToken(
                loginReqDto.getUserId(),
                loginReqDto.getPassword()
        );
        Authentication authentication = authenticationManager.authenticate(unauthenticatedToken);



        return redirectUrl;
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
