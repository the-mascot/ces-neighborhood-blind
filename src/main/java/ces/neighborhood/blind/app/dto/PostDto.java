package ces.neighborhood.blind.app.dto;

import ces.neighborhood.blind.common.utils.ComUtils;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private Long postNo;

    private String boardType;

    private String nickName;

    private String title;

    private String content;

    private String delYn;

    private Integer viewCnt;

    private Long likeCnt;

    private Boolean isLiked;

    private Integer commCnt;

    private Timestamp createDate;

    private String postUrl;

    public String getCreateDateStr() {
        return ComUtils.calculateTimeDifference(createDate);
    }

    public String getPostUrl() {
        return "/board/post/" + postNo;
    }

    public PostDto(Long postNo, String boardType, String nickName,
                   String title, String content, String delYn,
                   Integer viewCnt, Long likeCnt, Boolean isLiked,
                   Integer commCnt, Timestamp createDate) {
        this.postNo = postNo;
        this.boardType = boardType;
        this.nickName = nickName;
        this.title = title;
        this.content = content;
        this.delYn = delYn;
        this.viewCnt = viewCnt;
        this.likeCnt = likeCnt;
        this.likeCnt = likeCnt;
        this.commCnt = commCnt;
        this.createDate = createDate;
    }

    public PostDto(Long postNo, String boardType, String nickName,
                   String title, String content, String delYn,
                   Integer viewCnt, Long likeCnt, Boolean isLiked,
                   Long commCnt, Timestamp createDate) {
        this.postNo = postNo;
        this.boardType = boardType;
        this.nickName = nickName;
        this.title = title;
        this.content = content;
        this.delYn = delYn;
        this.viewCnt = viewCnt;
        this.likeCnt = likeCnt;
        this.isLiked = isLiked;
        this.commCnt = commCnt.intValue();
        this.createDate = createDate;
    }
}
