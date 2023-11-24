package ces.neighborhood.blind.app.service;

import org.springframework.stereotype.Service;

import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MbrInfo getMbrInfo(String mbrId) {
        return memberRepository.findById(mbrId).orElse(null);
    }
}
