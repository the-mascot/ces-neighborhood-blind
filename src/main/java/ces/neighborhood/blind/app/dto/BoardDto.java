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
public class BoardDto {

    private Long postNo;

    private String boardType;

    private String nickName;

    private String title;

    private String content;

    private String delYn;

    private Integer viewCnt;

    private Integer likeCnt;

    private Long commCnt;

    private Timestamp createDate;

    private String postUrl;

    public String getCreateDateStr() {
        return ComUtils.calculateTimeDifference(createDate);
    }

    public String getPostUrl() {
        return "/board/post/" + postNo;
    }

    public BoardDto(Long postNo, String boardType, String nickName,
                    String title, String content, String delYn,
                    Integer viewCnt,
                    Integer likeCnt, Long commCnt, Timestamp createDate) {
        this.postNo = postNo;
        this.boardType = boardType;
        this.nickName = nickName;
        this.title = title;
        this.content = content;
        this.delYn = delYn;
        this.viewCnt = viewCnt;
        this.likeCnt = likeCnt;
        this.commCnt = commCnt;
        this.createDate = createDate;
    }
}
