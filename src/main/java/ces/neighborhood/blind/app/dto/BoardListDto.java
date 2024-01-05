package ces.neighborhood.blind.app.dto;

import ces.neighborhood.blind.common.utils.ComUtils;
import java.sql.Timestamp;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardListDto {

    private Long postNo;

    private String boardType;

    private String mbrNm;

    private String title;

    private String content;

    private String delYn;

    private Integer viewCnt;

    private Integer likeCnt;

    private Long commCnt;

    private Timestamp createDate;

    private String url;

    public String getCreateDateStr() {
        return ComUtils.calculateTimeDifference(createDate);
    }

    public String getUrl() {
        return "/board/posts/" + this.postNo;
    }

    public BoardListDto(Long postNo, String boardType, String mbrNm,
                        String title,
                        String content, String delYn, Integer viewCnt,
                        Integer likeCnt, Long commCnt, Timestamp createDate) {
        this.postNo = postNo;
        this.boardType = boardType;
        this.mbrNm = mbrNm;
        this.title = title;
        this.content = content;
        this.delYn = delYn;
        this.viewCnt = viewCnt;
        this.likeCnt = likeCnt;
        this.commCnt = commCnt;
        this.createDate = createDate;
    }
}
