package ces.neighborhood.blind.app.service.member;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.record.authority.LoginRes;
import ces.neighborhood.blind.app.record.member.UpdateMemberInfoReq;
import ces.neighborhood.blind.app.repository.MemberRepository;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import ces.neighborhood.blind.common.utils.ComUtils;
import java.util.Optional;
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
    public MbrInfo getMbrInfoById(String mbrId) {
        return memberRepository.findById(mbrId)
                .orElseThrow(() -> new BizException(ErrorCode.CODE_1001));
    }

    /**
     * ID 중복체크
     * @param mbrId
     * @return 해당 아이디가 존재하면 true / 존재하지 않으면 false
     * @throws
     */
    public Boolean checkIdDuplicate(String mbrId) {
        Boolean isDuplicate = memberRepository.existsById(mbrId);
        log.info("[MemberService - checkIdDuplicate] mbrId: {}, isDuplicate: {}", mbrId, isDuplicate);
        return isDuplicate;
    }

    /**
     * nickname 중복체크
     * @param nickname
     * @return 해당 닉네임이 존재하면 true / 존재하지 않으면 false
     * @throws
     */
    public Boolean checkNicknameDuplicate(String nickname) {
        Boolean isDuplicate = memberRepository.existsByMbrNickname(nickname);
        log.info("[MemberService - checkNicknameDuplicate] nickname: {}, isDuplicate: {}", nickname, isDuplicate);
        return isDuplicate;
    }

    /**
     * 회원정보변경
     * @param updateMemberInfoReq
     * @return
     * @throws
     */
    @Transactional
    public void updateMbrInfo(UpdateMemberInfoReq updateMemberInfoReq) {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        MbrInfo mbrInfo = this.getMbrInfoById(authentication.getName());
        Optional.ofNullable(updateMemberInfoReq.nickname()).ifPresent(mbrInfo::setMbrNickname);
        Optional.ofNullable(updateMemberInfoReq.profileImage()).ifPresent(mbrInfo::setMbrProfileImageUrl);
    }

    /**
     * unique nickname 생성
     * @param
     * @return unique nickname
     * @throws
     */
    public String generateRandomNickname() {
        String randomNickname;
        boolean exist;
        // random 닉네임 생성
        do {
            randomNickname = ComUtils.generateRandomNickname();
            exist = memberRepository.existsByMbrNickname(randomNickname);
        } while (exist);
        return randomNickname;
    }

    /**
     * 로그인정보 가져오기
     * @param
     * @return LoginRes(닉네임, 프로필사진 url)
     * @throws
     */
    public LoginRes getProfileInfo() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        MbrInfo mbrInfo = this.getMbrInfoById(authentication.getName());
        return new LoginRes(mbrInfo.getMbrNickname(), mbrInfo.getMbrProfileImageUrl());
    }
}
