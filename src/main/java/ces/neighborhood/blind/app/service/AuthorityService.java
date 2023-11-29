package ces.neighborhood.blind.app.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ces.neighborhood.blind.app.dto.LoginReqDto;
import ces.neighborhood.blind.app.dto.Role;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

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
