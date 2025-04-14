package ces.neighborhood.blind.app.record.board;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import ces.neighborhood.blind.common.utils.ComUtils;

public record PostsRes(
        Long postNo,            // 글 번호
        String nickName,        // 닉네임
        String title,           // 제목
        String content,         // 내용
        Integer viewCnt,        // 조회수
        Long likeCnt,           // 좋아요 수
        Boolean isLiked,        // 종아요 여부
        Integer commentCnt,     // 총 댓글(댓글 + 대댓글 총 합) 갯수
        Timestamp createDate,   // 작성시간
        String elapsedTime,     // n 시간 전 작성
        String fileUrl,         // 대표파일 url
        String fileName,        // 대표파일 명
        Integer fileCount       // 총 파일 개수
) {
    /**
     * 경과 시간 계산 메소드
     * @return 경과 시간 문자열 (예: "1시간 전", "2일 전")
     */
    @JsonProperty("elapsedTime")
    public String getElapsedTime() {
        return ComUtils.calculateTimeDifference(createDate);
    }
}
