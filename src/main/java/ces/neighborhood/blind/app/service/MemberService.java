package ces.neighborhood.blind.app.service;

import org.springframework.stereotype.Service;

import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 회원정보 변경 및 회원 관련 Service
 * </pre>
 *
 * @version 1.0
 * @author mascot
 * @since 2023.11.24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원정보 상세 조회
     * @param mbrId
     * @return MbrInfo
     * @throws
     */
    public MbrInfo getMbrInfo(String mbrId) {
        return memberRepository.findById(mbrId)
                .orElseThrow(() -> new RuntimeException());
    }

    /**
     * ID 중복체크
     * @param mbrId
     * @return 해당 아이디가 존재하면 true / 존재하지 않으면 false
     * @throws
     */
    public Boolean checkIdDuplicate(String mbrId) {
        Boolean isDuplicate = memberRepository.existsById(mbrId);
        log.info("[MemberService - checkDuplicate] mbrId: {}, isDuplicate: {}", mbrId, isDuplicate);
        return isDuplicate;
    }
}
