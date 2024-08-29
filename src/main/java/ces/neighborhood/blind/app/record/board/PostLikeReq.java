package ces.neighborhood.blind.app.record.board;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PostLikeReq(
    @NotNull
    @Pattern(regexp = "POST|COMMENT|REPLY", message = "postType Validation Fail.")
    String postType,

    @NotNull
     Long postNo
) {
}
