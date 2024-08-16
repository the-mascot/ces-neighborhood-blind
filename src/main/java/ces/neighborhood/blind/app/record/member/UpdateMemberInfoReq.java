package ces.neighborhood.blind.app.record.member;

import jakarta.validation.constraints.Size;

public record UpdateMemberInfoReq(
        @Size(min = 2, max = 10)
        String nickname,
        String profileImage
) {
}
