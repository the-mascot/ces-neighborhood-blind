package ces.neighborhood.blind.app.repository.board;

import ces.neighborhood.blind.app.entity.QAttachment;
import ces.neighborhood.blind.app.entity.QComment;
import ces.neighborhood.blind.app.entity.QLikes;
import ces.neighborhood.blind.app.entity.QMbrInfo;
import ces.neighborhood.blind.app.entity.QPost;
import ces.neighborhood.blind.app.record.board.CommentRes;
import ces.neighborhood.blind.app.record.board.PostsRes;
import ces.neighborhood.blind.common.constant.Constant;
import java.util.List;
import lombok.RequiredArgsConstructor;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

@RequiredArgsConstructor
public class PostDslRepositoryImpl implements PostDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 게시물 상세 가져오기
     */
    @Override
    public PostsRes findPostById(Long postNo, String mbrId) {
        QPost post = QPost.post;
        QComment comment = QComment.comment;
        QLikes likes = QLikes.likes;
        QAttachment attachment = QAttachment.attachment;
        QMbrInfo mbrInfo = QMbrInfo.mbrInfo;

        Tuple posts = jpaQueryFactory.select(
                        post.postNo,
                        post.mbrInfo.mbrNickname,
                        post.title,
                        post.content,
                        post.viewCnt,
                        getLikeCount(post.postNo, Constant.REF_TYPE_POST),
                        getIsLiked(post.postNo, mbrId, Constant.REF_TYPE_POST),
                        post.createDate
                )
                .where(post.postNo.eq(postNo)
                        .and(post.delYn.eq(Constant.N)))
                .fetchOne();

        List<CommentRes> comments = jpaQueryFactory
                .select(Projections.constructor(CommentRes.class,
                        Expressions.constant(Constant.REF_TYPE_COMMENT),
                        comment.commentNo,
                        mbrInfo.mbrNickname,
                        comment.content,
                        getLikeCount(comment.commentNo, Constant.REF_TYPE_COMMENT),
                        getIsLiked(comment.commentNo, mbrId, Constant.REF_TYPE_COMMENT),
                        comment.createDate,
                        JPAExpressions
                                .select(Projections.constructor(CommentRes.class,
                                    Expressions.constant(Constant.REF_TYPE_REPLY),
                                    comment.commentNo,
                                    mbrInfo.mbrNickname,
                                    comment.content,
                                    getLikeCount(comment.commentNo, Constant.REF_TYPE_REPLY),
                                    getIsLiked(comment.commentNo, mbrId, Constant.REF_TYPE_REPLY),
                                        comment.createDate
                                ))
                                .from(comment)
                                .where(comment.commentNo.eq(comment.commentNo)
                                .and(comment.delYn.eq("N")))
                                .orderBy(comment.createDate.desc())
                ))
                .from(comment)
                .where(comment.post.postNo.eq(postNo)
                        .and(comment.delYn.eq("N")))
                .orderBy(comment.createDate.desc())
                .fetch();


        return null;
    }

    /**
     * 게시물 목록 가져오기
     */
    @Override
    public List<PostsRes> findAllPostsDto(String mbrId) {
        QPost post = QPost.post;
        QComment comment = QComment.comment;
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
                        getLikeCount(post.postNo, Constant.REF_TYPE_POST),
                        getIsLiked(post.postNo, mbrId, Constant.REF_TYPE_POST),
                        getCommentCount(post.postNo),
                        post.createDate,
                        attachment.fileUrl,
                        attachment.originalFileName,
                        attachmentCount
                ))
                .from(post)
                .leftJoin(attachment).fetchJoin()
                    .on(attachment.fileNo.eq(minFileNo))
                .where(post.delYn.eq(Constant.N))
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
                .and(attachment.delYn.eq("N"));
    }

    private Expression<Long> getLikeCount(NumberPath<Long> id, String postType) {
        QLikes likes = QLikes.likes;
        return JPAExpressions
                .select(likes.likesId.postNo.count())
                .from(likes)
                .where(likes.likesId.postNo.eq(id)
                        .and(likes.likesId.postType.eq(postType)));
    }

    private BooleanExpression getIsLiked(NumberPath<Long> id, String mbrId, String postType) {
        QLikes likes = QLikes.likes;
        return JPAExpressions
                .selectOne()
                .from(likes)
                .where(likes.likesId.mbrId.eq(mbrId)
                        .and(likes.likesId.postNo.eq(id))
                        .and(likes.likesId.postType.eq(postType)))
                .exists();
    }

    private Expression<Long> getCommentCount(NumberPath<Long> postNo) {
        QComment comment = QComment.comment;
        QComment reply = new QComment("reply");
        return JPAExpressions
                .select(comment.count().add(reply.count()))
                .from(comment)
                .leftJoin(reply)
                .on(comment.commentNo.eq(reply.parentCommentNo)
                .and(reply.delYn.eq("N")))
                .where(comment.post.postNo.eq(postNo)
                        .and(comment.delYn.eq(Constant.N)));
    }
}
