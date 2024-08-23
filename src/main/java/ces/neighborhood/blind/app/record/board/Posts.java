package ces.neighborhood.blind.app.record.board;

import java.sql.Timestamp;

public record Posts(
        Long postNo,
        String nickName,
        String title,
        String content,
        Integer viewCnt,
        Long likeCnt,
        Boolean isLiked,
        Long commentCnt,
        Timestamp createDate,
        String fileUrl
) {
}
