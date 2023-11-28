package ces.neighborhood.blind.app.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.ToString;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@Getter
@ToString
public class LoginReqDto {

    @NotBlank
    @Email(message = "Not in email format")
    private String userId;

    @NotBlank
    // 회원 비밀번호
    private String password;
}
