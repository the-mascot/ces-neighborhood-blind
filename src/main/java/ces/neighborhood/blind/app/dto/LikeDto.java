package ces.neighborhood.blind.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {

    @NotNull
    @Pattern(regexp = "POST|COMMENT|REPLY", message = "postType Validation Fail.")
    private String postType;

    @NotNull
    private Long postNo;
}
