package ces.neighborhood.blind.app.dto.authority;

import jakarta.validation.constraints.Email;
import lombok.Getter;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Getter
public class JoinReqDto {
    @NotBlank
    @Email(message = "이메일 형식이 아닙니다.")
    String userId;

    @NotBlank
    String password;

    @NotBlank
    String nickname;

}
