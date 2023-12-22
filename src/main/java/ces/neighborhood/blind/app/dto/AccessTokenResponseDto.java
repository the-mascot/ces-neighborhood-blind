package ces.neighborhood.blind.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@Builder
public class AccessTokenResponseDto {

    private String AccessToken;

    private String refreshToken;

    private String tokenType;

    private Integer expiresIn;

    private String error;

    private String errorDescription;
}
