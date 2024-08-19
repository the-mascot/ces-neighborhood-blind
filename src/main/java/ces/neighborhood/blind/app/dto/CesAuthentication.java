package ces.neighborhood.blind.app.dto;

import ces.neighborhood.blind.app.record.authority.LoginRes;
import ces.neighborhood.blind.app.record.authority.OAuthLoginRes;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CesAuthentication {

    LoginRes loginRes;

    OAuthLoginRes oAuthLoginRes;

    TokenDto tokenDto;

}
