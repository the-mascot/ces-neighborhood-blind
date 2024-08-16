package ces.neighborhood.blind.app.dto;

import ces.neighborhood.blind.app.record.authority.OAuthLoginRes;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Oauth2Authentication {

    OAuthLoginRes oAuthLoginRes;

    TokenDto tokenDto;

}
