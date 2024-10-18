package ces.neighborhood.blind.app.repository.board;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import ces.neighborhood.blind.app.entity.Comment;
import ces.neighborhood.blind.app.entity.QAttachment;
import ces.neighborhood.blind.app.entity.QComment;
import ces.neighborhood.blind.app.entity.QLikes;
import ces.neighborhood.blind.app.entity.QPost;
import ces.neighborhood.blind.app.entity.QReply;
import ces.neighborhood.blind.app.record.board.PostRes;
import ces.neighborhood.blind.app.record.board.PostsRes;
import ces.neighborhood.blind.common.constant.Constant;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostDslRepositoryImpl implements PostDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 게시물 상세 가져오기
     */
    @Override
    public PostRes findPostById(Long postNo, String mbrId) {
        QPost post = QPost.post;
        QComment comment = QComment.comment;
        QReply reply = QReply.reply;
        QLikes likes = QLikes.likes;
        QAttachment attachment = QAttachment.attachment;

        Tuple posts = jpaQueryFactory.select(
                    post.postNo,
                    post.mbrInfo.mbrNickname,
                    post.title,
                    post.content,
                    post.viewCnt,
                    getLikeCount(post, likes, Constant.REF_TYPE_POST),
                    getIsLiked(post, likes, mbrId),
                    post.createDate
                )
                .where(post.postNo.eq(postNo)
                    .and(post.delYn.eq("N")))
                .fetchOne();

        List<Comment> comments = jpaQueryFactory.selectFrom(comment)
                .from(comment)
                .where(comment.post.postNo.eq(postNo)
                        .and(comment.delYn.eq("N")))
                .leftJoin(reply).fetchJoin()
                .orderBy(comment.createDate.desc())
                .fetch();

        for (Comment c : comments) {
            if (c.getReply() != null) {
                c.getReply().sort((r1, r2) -> r2.getCreateDate().compareTo(r1.getCreateDate()));
            }
        }

        return new PostRes(posts, comments);
    }

    /**
     * 게시물 목록 가져오기
     */
    @Override
    public List<PostsRes> findAllPostsDto(String mbrId) {
        QPost post = QPost.post;
        QComment comment = QComment.comment;
        QReply reply = QReply.reply;
        QLikes likes = QLikes.likes;
        QAttachment attachment = QAttachment.attachment;

        // 첨부 파일 개수를 서브쿼리로 계산
        JPQLQuery<Long> attachmentCount = JPAExpressions
                .select(attachment.count())
                .from(attachment)
                .where(attachmentConditions(attachment, post));

        // 처음 올린 대표 이미지 가져오는 쿼리
        JPQLQuery<Long> minFileNo = JPAExpressions
                .select(attachment.fileNo.min())
                .from(attachment)
                .where(attachmentConditions(attachment, post));

        return jpaQueryFactory
                .select(Projections.constructor(PostsRes.class,
                        post.postNo,
                        post.mbrInfo.mbrNickname,
                        post.title,
                        post.content,
                        post.viewCnt,
                        getLikeCount(post, likes, Constant.REF_TYPE_POST),
                        getIsLiked(post, likes, mbrId),
                        comment.count().add(reply.count()),
                        post.createDate,
                        attachment.fileUrl,
                        attachment.originalFileName,
                        attachmentCount
                ))
                .from(post)
                .leftJoin(comment).fetchJoin()
                    .on(comment.post.postNo.eq(post.postNo)
                    .and(comment.delYn.eq("N")))
                .leftJoin(reply).fetchJoin()
                    .on(reply.comment.commentNo.eq(comment.commentNo)
                    .and(reply.delYn.eq("N")))
                .leftJoin(attachment).fetchJoin()
                    .on(attachment.fileNo.eq(minFileNo))
                .where(post.delYn.eq("N"))
                .groupBy(
                        post.postNo,
                        post.mbrInfo.mbrNickname,
                        post.title,
                        post.content,
                        post.viewCnt,
                        post.createDate,
                        attachment.fileUrl,
                        attachment.originalFileName
                )
                .orderBy(new OrderSpecifier<>(Order.DESC, post.createDate))
                .fetch();
    }

    /**
     * Attachment where 조건 절
     * refType = 'POST'
     * refNo = postNo
     * delYn = 'N'
     */
    private BooleanExpression attachmentConditions(QAttachment attachment, QPost post) {
        return  attachment.refType.eq(Constant.REF_TYPE_POST)
                .and(attachment.refNo.eq(post.postNo))
                .and(attachment.delYn.eq("N"));
    }

    private Expression<Long> getLikeCount(QPost post, QLikes likes, String postType) {
        return JPAExpressions
                .select(likes.likesId.mbrId.count())
                .from(likes)
                .where(likes.likesId.postNo.eq(post.postNo)
                        .and(likes.likesId.postType.eq(postType)));
    }

    private BooleanExpression getIsLiked(QPost post, QLikes likes, String mbrId) {
        return JPAExpressions
                .selectOne()
                .from(likes)
                .where(likes.likesId.mbrId.eq(mbrId)
                        .and(likes.likesId.postNo.eq(post.postNo))
                        .and(likes.likesId.postType.eq(Constant.REF_TYPE_POST)))
                .exists();
    }
}
