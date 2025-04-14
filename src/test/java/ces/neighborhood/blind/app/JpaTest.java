package ces.neighborhood.blind.app;

import ces.neighborhood.blind.app.entity.Comment;
import ces.neighborhood.blind.app.entity.Likes;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.entity.Post;
import ces.neighborhood.blind.app.entity.QLikes;
import ces.neighborhood.blind.app.entity.QPost;
import ces.neighborhood.blind.app.repository.board.LikesRepository;
import ces.neighborhood.blind.app.repository.board.PostRepository;
import ces.neighborhood.blind.common.TestQueryDslConfig;
import ces.neighborhood.blind.common.config.P6SpyConfig;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Slf4j
@DataJpaTest    // JPA 슬라이싱 테스트. JPA 관련 Component 들만 로드 시키고 테스트 떄 넣은 Data 도 롤백 된다.
@ActiveProfiles("test") // 테스트 프로파일
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)    // Replace.NONE 실제 데이터 베이스 사용.
@Import({ TestQueryDslConfig.class, P6SpyConfig.class}) /*QueryDsl 추가로 JpaQueryFactory Bean 등록을 위해 추가.
JpaQueryFactory 는 persistenceLayer가 아니라서 추가 bean등록이 필요하다.*/
public class JpaTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikesRepository likesRepository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Test
    public void convertDate() {
        long startTime = System.currentTimeMillis();
        //List<BoardDto> boardDto = boardRepository.getBoardList("dmstn1812@naver.com");
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("실행시간 : " + duration + "ms");

        // 2차 테스트 서브쿼리
        // 실행시간 : 117ms

        //1차 테스트 join
        // 실행시간 : 134ms
        // 실행시간 : 114ms
        //실행시간 : 118ms
//        boardDto.forEach(s -> {
//            System.out.println("title : " + s.getTitle());
//            System.out.println("content : " + s.getContent());
//        });

    }

    @Test
    public void complexKeyTest() {
        Likes likes = Likes.builder()
                .likesId(Likes.LikesId.builder()
                        .postNo(Long.valueOf(1))
                        .mbrId("dmstn1812@naver.com").build())
                .build();
        System.out.println(likesRepository.save(likes).getLikesId());
    }

    @Test
    public void getComplexKeyTest() {
        List<Likes> likes = likesRepository.findAllByLikesId_PostNo(Long.valueOf(503));
    }

    @Test
    public void nPlusOneTest() {
        // JPA 의 N + 1 문제 테스트
        Long id = Long.valueOf(1);
        Optional<Post> post = postRepository.findById(id);
        // 부모 Board의 개수 만큼 +1 의 Comment select 쿼리 발생.
        if (post.isPresent() && post.get().getComment() != null) {
            List<Comment> comments = post.get().getComment();
            comments.forEach(s -> log.info(s.toString()));
        }
    }


//    @Test
//    @DisplayName("EntityGraph 테스트")
//    public void entityGraphTest() {
//        List<Post> postList = postRepository.findAll();
//        System.out.println("EntityGraph 종료");
//        //boardList.forEach(s -> System.out.println("postNo : " + s.getPostNo() + ", comment size : " + s.getComment().size()));
//        postList.forEach(s -> System.out.println("nickname : " + s.getMbrInfo().getMbrNickname()));
//    }
//
//    @Test
//    @DisplayName("queryDsl 테스트")
//    public void simpleQueryDslTest() {
//        List<BoardDto> boardList = postRepository.findAllByPostNoAndMbrId("dmstn1812@naver.com");
//        boardList.forEach(s -> System.out.println(s.getPostNo()));
//    }

    @Test
    @DisplayName("save 테스트")
    public void save() {
        String title = "2년6개월 짧지만 길게 다닌 회사 퇴사합니다.";
        String content = "회사 차량 잠그지 말고 퇴근하라는 상사의 지시로 잠그지 않고 퇴근했다가 차량 도난을 당했고 렌트차량이었던 회사 차를 저에게 인수해서 책임져라라는 부서장의 말에 퇴사를 말하게 됐어요. 퇴사하고 내가 퇴직금으로 처리하겠다고 하면서요. 사람이 없어서 힘들어하던 부서에선 뒤늦게 달래주고 책임 없게 해주겠다고 하는 와중에 결국 오늘 차를 찾았네요.\n" +
                "야근, 주말출근 밥 먹듯 하는 회사에서 연장 근무 수당 못 받고 좋은 게 좋은 거다라고 생각하며 다녔는데 이렇게 퇴사를 말하니 참 싱숭생숭하네요..ㅎ";

        Post post = Post.builder()
                .mbrInfo(MbrInfo.builder().mbrId("dmstn1812@naver.com").build())
                .title(title)
                .content(content)
                .delYn("N")
                .build();

        Post savedPost = postRepository.save(post);
        assertNotNull(savedPost.getPostNo());
        assertEquals(title, savedPost.getTitle());
    }

    @Test
    @DisplayName("BooleanBuilder 테스트")
    public void booleanBuilderTest() {
        QPost qPost = QPost.post;
        Long postNo = Long.valueOf(1);
        List<Post> posts = jpaQueryFactory.select(qPost)
                .from(qPost)
                .where(buildPredicate(postNo, "N"))
                .fetch();

        if (posts != null)
            log.info("[booleanBuilderTest] post : {}", posts);
        assertTrue(posts.size() > 0);
    }

    BooleanBuilder buildPredicate(Long postNo, String delYn) {
        QPost post = QPost.post;
        BooleanBuilder builder = new BooleanBuilder();

        if (postNo != null) {
            builder.and(post.postNo.eq(postNo));
        }
        if (delYn != null) {
            builder.and(post.delYn.eq(delYn));
        }
        return builder;
    }

//    @Test
//    @DisplayName("댓글, 대댓글 튜플 조인 테스트")
//    void commentTupleJoinTest() {
//        Long postNo = Long.valueOf(1);
//
//        QComment comment = QComment.comment;
//        QReply reply = QReply.reply;
//        QMbrInfo mbrInfo = QMbrInfo.mbrInfo;
//
//        // tuple join으로 가져올 경우 comment 하나당 reply 하나가 select 됨
//        List<Tuple> data = jpaQueryFactory.select(comment, reply)
//                .from(comment)
//                .leftJoin(reply)
//                .on(comment.eq(reply.comment))
//                .where(comment.post.postNo.eq(postNo))
//                .fetch();
//
//        log.info("[commentJoinTest] data : {}", data);
//    }

//    @Test
//    @DisplayName("댓글, 대댓글 조인 테스트")
//    void commentJoinTest() {
//        Long postNo = Long.valueOf(1);
//        String mbrId = "dmstn1812@naver.com";
//
//        QPost post = QPost.post;
//        QComment comment = QComment.comment;
//        QReply reply = QReply.reply;
//        QMbrInfo mbrInfo = QMbrInfo.mbrInfo;
//        QLikes likes = QLikes.likes;
//        QAttachment attachment = QAttachment.attachment;
//
//        List<CommentRes> comments = jpaQueryFactory.select(Projections.constructor(CommentRes.class,
//                        Expressions.constant(Constant.REF_TYPE_COMMENT),
//                        comment.commentNo,
//                        mbrInfo.mbrNickname,
//                        comment.content,
//                        getLikeCount(comment.commentNo, likes, Constant.REF_TYPE_COMMENT),
//                        getIsLiked(comment.commentNo, likes, mbrId, Constant.REF_TYPE_COMMENT),
//                        comment.createDate,
//                        attachment
//                ))
//                .from(comment)
//                .leftJoin(comment.reply, reply)
//                .where(comment.post.postNo.eq(postNo))
//                .fetch();
//
//        log.info("[commentJoinTest] comments : {}", comments);
////        log.info("[commentJoinTest] comments : {}", comments);
//
//    }

    private Expression<Long> getLikeCount(NumberPath<Long> id, QLikes likes, String postType) {
        return JPAExpressions
                .select(likes.likesId.postNo.count())
                .from(likes)
                .where(likes.likesId.postNo.eq(id)
                        .and(likes.likesId.postType.eq(postType)));
    }

    private BooleanExpression getIsLiked(NumberPath<Long> id, QLikes likes, String mbrId, String postType) {
        return JPAExpressions
                .selectOne()
                .from(likes)
                .where(likes.likesId.mbrId.eq(mbrId)
                        .and(likes.likesId.postNo.eq(id))
                        .and(likes.likesId.postType.eq(postType)))
                .exists();
    }
}
