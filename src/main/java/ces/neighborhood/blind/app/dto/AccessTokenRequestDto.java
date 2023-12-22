package ces.neighborhood.blind.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@Builder
public class AccessTokenRequestDto {

    private String grantType;

    private String clientId;

    private String clientSecret;

    private String code;

    private String state;

    private String AccessToken;

    private String refreshToken;

    private String serviceProvider;
}
