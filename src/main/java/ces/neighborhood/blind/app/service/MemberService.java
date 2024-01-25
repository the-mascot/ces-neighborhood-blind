package ces.neighborhood.blind.app.service;

import org.springframework.stereotype.Service;

import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.repository.MemberRepository;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * 회원정보 변경 및 회원 관련 Service
 * </pre>
 *
 * @version 1.0
 * @author mascot
 * @since 2023.11.24
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원정보 상세 가져오기
     * @param mbrId
     * @return MbrInfo
     * @throws
     */
    public MbrInfo getMbrInfo(String mbrId) {
        return memberRepository.findById(mbrId)
                .orElseThrow(() -> new RuntimeException());
    }
}
