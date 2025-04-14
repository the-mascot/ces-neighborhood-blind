package ces.neighborhood.blind.app.dto.authority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResDto {
    String nickname;

    String profileImage;
}
