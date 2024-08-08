package ces.neighborhood.blind.app.record;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import jakarta.validation.constraints.Email;

public record JoinReq(
        @NotBlank
        @Email(message = "이메일 형식이 아닙니다.")
        String userId,

        @NotBlank
        String password,

        @NotBlank
        String nickname
) {}
