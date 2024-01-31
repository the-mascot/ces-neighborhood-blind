package ces.neighborhood.blind.app.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import ces.neighborhood.blind.app.dto.BoardDto;
import ces.neighborhood.blind.app.entity.QAttachment;
import ces.neighborhood.blind.app.entity.QBoard;
import ces.neighborhood.blind.app.entity.QComment;
import ces.neighborhood.blind.app.entity.QLikes;
import ces.neighborhood.blind.app.entity.QReply;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<BoardDto> findAllByPostNoAndMbrId(String mbrId) {
        QBoard board = QBoard.board;
        QAttachment attachment = QAttachment.attachment;
        QComment comment = QComment.comment;
        QReply reply = QReply.reply;
        QLikes likes = QLikes.likes;
        QLikes i = new QLikes("i");

        return jpaQueryFactory
                .select(Projections.constructor(BoardDto.class,
                        board.postNo,
                        board.boardType,
                        board.mbrInfo.mbrNickname,
                        board.title,
                        board.content,
                        board.viewCnt,
                        likes.likesId.postNo.count(),
                        new CaseBuilder().when(i.likesId.postNo.isNull()).then(false).otherwise(true),
                        comment.commentNo.count().add(reply.replyNo.count()),
                        board.createDate,
                        attachment.folderPath,
                        attachment.storedFileName
                ))
                .from(board)
                .leftJoin(board.comment, comment).fetchJoin()
                .on(comment.delYn.eq("N"))
                .leftJoin(board.reply, reply).fetchJoin()
                .on(reply.delYn.eq("N"))
                .leftJoin(board.attachment, attachment).fetchJoin()
                .on(attachment.refType.eq("BOARD")
                        .and(attachment.delYn.eq("N")))
                .leftJoin(board.likes, likes).fetchJoin()
                .on(likes.likesId.postType.eq("POST"))
                .leftJoin(board.likes, i).fetchJoin()
                .on(i.likesId.postType.eq("POST")
                        .and(i.likesId.mbrId.eq(mbrId)))
                .where(board.boardType.isNull()
                        .and(attachment.fileNo.eq(jpaQueryFactory.select(attachment.fileNo.min()).from(attachment))))
                .fetch();
    }
}
