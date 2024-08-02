package ces.neighborhood.blind.app.record;

import jakarta.validation.constraints.Email;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

public record LoginReq(
        @NotBlank
        @Email(message = "이메일 형식이 아닙니다.")
        String userId,

        @NotBlank
        String password
) {}
