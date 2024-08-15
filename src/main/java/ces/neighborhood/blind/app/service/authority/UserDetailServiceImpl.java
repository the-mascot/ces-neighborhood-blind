package ces.neighborhood.blind.app.service.authority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ces.neighborhood.blind.app.repository.MemberRepository;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * Spring Security 로그인 UserDetailsService 구현체
 * </pre>
 *
 * @see UserDetailsService
 * @see ces.neighborhood.blind.app.provider.AuthenticationProviderImpl
 * @version 1.0
 * @author mascot
 * @since 2023.12.07
 */
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * username으로 DB에서 user 정보 가져오기
     * @param mbrId
     * @return UserDetails
     * @throws
     */
    @Override
    public UserDetails loadUserByUsername(String mbrId) throws BizException {
        return memberRepository.findById(mbrId)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.CODE_1001.getMessage()));
    }
}
