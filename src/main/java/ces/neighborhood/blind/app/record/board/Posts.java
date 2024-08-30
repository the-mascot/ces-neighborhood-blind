package ces.neighborhood.blind.app.record.board;

import ces.neighborhood.blind.common.utils.ComUtils;
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
        String createElapsedTime,
        String fileUrl,
        String fileName,
        Long fileCnt
) {

    public Posts(Long postNo, String nickName, String title, String content,
                 Integer viewCnt, Long likeCnt, Boolean isLiked,
                 Long commentCnt,
                 Timestamp createDate, String fileUrl, String fileName, Long fileCnt) {
        this(postNo, nickName, title, content, viewCnt, likeCnt, isLiked,
                commentCnt, createDate, null, fileUrl, fileName, fileCnt);
    }

    @Override
    public String createElapsedTime() {
        return ComUtils.calculateTimeDifference(createDate);
    }
}
