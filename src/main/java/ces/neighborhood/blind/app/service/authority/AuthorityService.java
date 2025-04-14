package ces.neighborhood.blind.app.service.authority;

import ces.neighborhood.blind.app.dto.authority.CesAuthentication;
import ces.neighborhood.blind.app.dto.authority.JoinReqDto;
import ces.neighborhood.blind.app.dto.authority.LoginReqDto;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.provider.JwtTokenProvider;
import ces.neighborhood.blind.app.record.authority.LoginRes;
import ces.neighborhood.blind.app.repository.member.MemberRepository;
import ces.neighborhood.blind.common.constant.ComCode;
import ces.neighborhood.blind.common.constant.Role;
import ces.neighborhood.blind.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

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
    private final UserDetailServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private RememberMeServices rememberMeServices = new NullRememberMeServices();
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();
    private SecurityContextRepository
            securityContextRepository = new RequestAttributeSecurityContextRepository();

    /**
     * 회원가입 Service
     * @param joinReq
     */
    public void joinMember(JoinReqDto joinReq) {
        MbrInfo mbrInfo = MbrInfo.builder()
                .mbrId(joinReq.getUserId())
                .mbrPw(passwordEncoder.encode(joinReq.getPassword()))
                .role(Role.ROLE_MEMBER.getRoleName())
                .mbrNickname(joinReq.getNickname())
                .mbrStd(ComCode.MBR_STD_ACTIVE.getCode())
                .build();
        log.debug("[AuthorityService - joinMember] mbrInfo : {}", mbrInfo);
        memberRepository.save(mbrInfo);
    }

    /**
     * JWT 로그인 인증
     * @param loginReq
     * @return
     * @throws
     */
    public CesAuthentication authenticate(LoginReqDto loginReq) {
        // ID 값으로 회원정보조회
        MbrInfo mbrInfo = userDetailsService.loadUserByUsername(loginReq.getUserId());
        // 비밀번호 확인
        this.credentialChecks(loginReq, mbrInfo);
        Authentication authentication = new UsernamePasswordAuthenticationToken(mbrInfo.getUsername(), mbrInfo.getAuthorities());

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
        LoginRes loginRes = new LoginRes(mbrInfo.getMbrNickname(),
                mbrInfo.getMbrProfileImageUrl());

        return new CesAuthentication(loginRes, null, jwtTokenProvider.createTokenDTO(accessToken, refreshToken));
    }

    /**
     * Bcrypt 비밀번호 확인
     * authentication: 입력된 비밀번호, userDetails: DB 저장된 비밀번호
     * @param loginReq, userDetails
     */
    protected void credentialChecks(LoginReqDto loginReq, UserDetails userDetails) {
        if (!this.passwordEncoder.matches(loginReq.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException(ErrorCode.CODE_1002.getMessage());
        }
    }
}
