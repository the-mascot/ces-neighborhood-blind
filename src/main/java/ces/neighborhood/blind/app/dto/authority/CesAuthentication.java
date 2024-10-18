package ces.neighborhood.blind.app.dto.authority;

import ces.neighborhood.blind.app.record.authority.LoginRes;
import ces.neighborhood.blind.app.record.authority.OAuthLoginRes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class CesAuthentication {

    LoginRes loginRes;

    OAuthLoginRes oAuthLoginRes;

    TokenDto tokenDto;

}
