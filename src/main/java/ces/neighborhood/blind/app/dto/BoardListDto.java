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

    private String nickName;

    private String title;

    private String content;

    private String delYn;

    private Integer viewCnt;

    private Integer likeCnt;

    private Long commCnt;

    private Timestamp createDate;

    public String getCreateDateStr() {
        return ComUtils.calculateTimeDifference(createDate);
    }
}
