package ces.neighborhood.blind.app.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import ces.neighborhood.blind.app.entity.QAttachment;
import ces.neighborhood.blind.app.entity.QComment;
import ces.neighborhood.blind.app.entity.QLikes;
import ces.neighborhood.blind.app.entity.QPost;
import ces.neighborhood.blind.app.entity.QReply;
import ces.neighborhood.blind.app.record.board.Posts;
import ces.neighborhood.blind.common.code.Constant;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostDslRepositoryImpl implements PostDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Posts> findAllPostsDto(String mbrId) {
        QPost post = QPost.post;
        QAttachment attachment = QAttachment.attachment;
        QComment comment = QComment.comment;
        QReply reply = QReply.reply;
        QLikes likes = QLikes.likes;
        return jpaQueryFactory
                .select(Projections.constructor(Posts.class,
                        post.postNo,
                        post.mbrInfo.mbrNickname,
                        post.title,
                        post.content,
                        post.viewCnt,
                        JPAExpressions
                            .select(likes.likesId.count())
                            .from(likes)
                            .where(likes.likesId.postNo.eq(post.postNo)
                            .and(likes.likesId.postType.eq(Constant.REF_TYPE_POST))),
                        JPAExpressions
                            .selectOne()
                            .from(likes)
                            .where(
                                likes.likesId.mbrId.eq(mbrId)
                                .and(likes.likesId.postNo.eq(post.postNo))
                                .and(likes.likesId.postType.eq(Constant.REF_TYPE_POST)))
                                .exists(),
                        comment.count().add(reply.count()),
                        post.createDate,
                        attachment.fileUrl
                ))
                .from(post)
                .leftJoin(comment).fetchJoin()
                    .on(comment.post.postNo.eq(post.postNo)
                    .and(comment.delYn.eq("N")))
                .leftJoin(reply).fetchJoin()
                    .on(reply.comment.commentNo.eq(comment.commentNo)
                    .and(reply.delYn.eq("N")))
                .leftJoin(attachment).fetchJoin()
                    .on(attachment.refType.eq(Constant.REF_TYPE_POST)
                    .and(attachment.refNo.eq(post.postNo))
                    .and(attachment.delYn.eq("N")))
                .where(post.delYn.eq("N"))
                .groupBy(
                        post.postNo,
                        post.mbrInfo.mbrNickname,
                        post.title,
                        post.content,
                        post.viewCnt,
                        post.createDate,
                        attachment.fileUrl
                )
                .fetch();
    }
}
