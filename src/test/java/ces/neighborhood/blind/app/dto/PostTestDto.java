package ces.neighborhood.blind.app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.querydsl.core.annotations.QueryProjection;

@Getter
@NoArgsConstructor
public class PostTestDto {

    private Long postNo;

    private String title;

    private String content;

    private Boolean isLiked;

    @QueryProjection
    public PostTestDto(Long postNo, String title, String content,
                       Boolean isLiked) {
        this.postNo = postNo;
        this.title = title;
        this.content = content;
        this.isLiked = isLiked;
    }
}
