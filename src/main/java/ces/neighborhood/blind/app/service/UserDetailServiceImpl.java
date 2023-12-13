package ces.neighborhood.blind.app.service;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.repository.MemberRepository;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String mbrId) throws BizException {
        return memberRepository.findById(mbrId)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.CODE_1001.getMessage()));
    }

    // UserDetails 사용 버전
//    @Override
//    public UserDetails loadUserByUsername(String mbrId) throws BizException {
//        return memberRepository.findById(mbrId)
//                .map(this::createUserDetails)
//                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.CODE_1001.getMessage()));
//    }

    private UserDetails createUserDetails(MbrInfo mbrInfo) {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(mbrInfo.getRole());
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);
        return new User(
                mbrInfo.getMbrId(),
                mbrInfo.getMbrPw(),
                authorities
        );
    }
}
