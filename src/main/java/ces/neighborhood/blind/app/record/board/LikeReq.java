package ces.neighborhood.blind.app.record.board;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record LikeReq(
    @NotNull
    @Pattern(regexp = "POST|COMMENT|REPLY", message = "postType Validation Fail.")
    String postType,

    @NotNull
     Long postNo
) {
}
