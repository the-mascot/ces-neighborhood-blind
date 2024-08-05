package ces.neighborhood.blind.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {

    private String authorizationType;

    private String accessTokenHeaderName;

    private String refreshTokenHeaderName;

    private String accessToken;

    private String refreshToken;

    public String getAccessToken() {
        return authorizationType + accessToken;
    }

    public String getRefreshToken() {
        return authorizationType + refreshToken;
    }
}
