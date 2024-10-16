package ces.neighborhood.blind.app.record.board;

import ces.neighborhood.blind.app.entity.Attachment;
import ces.neighborhood.blind.app.entity.Comment;
import ces.neighborhood.blind.app.entity.Post;
import ces.neighborhood.blind.common.utils.ComUtils;
import java.sql.Timestamp;
import java.util.List;

public record PostRes(
        Long postNo,
        String nickName,
        String title,
        String content,
        Integer viewCnt,
        Long likeCnt,
        Boolean isLiked,
        Timestamp createDate,
        String createElapsedTime,
        List<Comment> comments,
        List<Attachment> attachments
) {
    public PostRes(Post post, List<Comment> comments, List<Attachment> attachments) {
        this.postNo = post.getPostNo();
        this.nickName = post.getMbrInfo().getMbrId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.viewCnt = post.getViewCnt();

    }

    @Override
    public String createElapsedTime() {
        return ComUtils.calculateTimeDifference(createDate);
    }
}
