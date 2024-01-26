package ces.neighborhood.blind.app.dto;

import ces.neighborhood.blind.common.utils.ComUtils;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
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

    private Long likeCnt;

    private Boolean isLiked;

    private Integer commCnt;

    private Timestamp createDate;

    private String folderPath;

    private String fileName;

    private String postUrl;

    private String imgSrc;

    public String getCreateDateStr() {
        return ComUtils.calculateTimeDifference(createDate);
    }

    public String getImgSrc() {
        return StringUtils.isEmpty(folderPath) || StringUtils.isEmpty(fileName) ? null :
        "https://ces-neighborhood.s3.ap-northeast-2.amazonaws.com/" + folderPath + "/" + fileName;
    }

    public String getPostUrl() {
        return "/board/post/" + postNo;
    }

    public BoardDto(Long postNo, String boardType, String nickName,
                    String title, String content, String delYn,
                    Integer viewCnt, Long likeCnt, Boolean isLiked,
                    Integer commCnt, Timestamp createDate, String folderPath, String fileName) {
        this.postNo = postNo;
        this.boardType = boardType;
        this.nickName = nickName;
        this.title = title;
        this.content = content;
        this.delYn = delYn;
        this.viewCnt = viewCnt;
        this.likeCnt = likeCnt;
        this.isLiked = isLiked;
        this.commCnt = commCnt;
        this.createDate = createDate;
        this.folderPath = folderPath;
        this.fileName = fileName;
    }

    public BoardDto(Long postNo, String boardType, String nickName,
                    String title, String content, String delYn,
                    Integer viewCnt, Long likeCnt, Boolean isLiked,
                    Long commCnt, Timestamp createDate, String folderPath, String fileName) {
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
        this.folderPath = folderPath;
        this.fileName = fileName;
    }
}
