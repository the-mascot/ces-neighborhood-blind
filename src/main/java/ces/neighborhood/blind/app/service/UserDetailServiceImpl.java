package ces.neighborhood.blind.app.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.repository.MemberRepository;
import ces.neighborhood.blind.common.exception.BizException;
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
                .map(this::)
    }

    private UserDetails createUserDetails(MbrInfo mbrInfo) {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(member.getMbrRole());
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);
        return new User(
                member.getMbrId(),
                member.getMbrPw(),
                authorities
        );
    }
}
