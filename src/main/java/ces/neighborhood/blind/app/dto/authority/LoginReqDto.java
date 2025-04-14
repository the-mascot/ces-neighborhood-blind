package ces.neighborhood.blind.app.dto.authority;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.ToString;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Getter
@ToString
public class LoginReqDto {
    @NotBlank
    @Email(message = "이메일 형식이 아닙니다.")
    String userId;

    @NotBlank
    String password;

}
