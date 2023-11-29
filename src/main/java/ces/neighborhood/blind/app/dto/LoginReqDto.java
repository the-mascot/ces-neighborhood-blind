package ces.neighborhood.blind.app.dto;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginReqDto {

    @NotBlank
    @Email(message = "Not in email format")
    private String userId;

    @NotBlank
    // 회원 비밀번호
    private String password;
}
