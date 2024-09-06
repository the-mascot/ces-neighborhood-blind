package ces.neighborhood.blind.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import ces.neighborhood.blind.app.dto.PostTestDto;
import ces.neighborhood.blind.app.entity.Likes;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.entity.Post;
import ces.neighborhood.blind.app.entity.QLikes;
import ces.neighborhood.blind.app.entity.QPost;
import ces.neighborhood.blind.app.repository.LikesRepository;
import ces.neighborhood.blind.app.repository.MemberRepository;
import ces.neighborhood.blind.app.repository.PostRepository;
import ces.neighborhood.blind.common.TestQueryDslConfig;
import ces.neighborhood.blind.common.code.ComCode;
import ces.neighborhood.blind.common.code.Constant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DataJpaTest    // JPA 슬라이싱 테스트. JPA 관련 Component 들만 로드 시키고 테스트 떄 넣은 Data 도 롤백 된다.
@ActiveProfiles("test-h2") // 테스트 프로파일
@TestPropertySource(locations = "classpath:application-test-h2.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)    // Replace.NONE 실제 데이터 베이스 사용.
@Import(TestQueryDslConfig.class) /*QueryDsl 추가로 JpaQueryFactory Bean 등록을 위해 추가.
JpaQueryFactory 는 persistenceLayer가 아니라서 추가 bean등록이 필요하다.*/
public class JpaTestWithH2 {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikesRepository likesRepository;

    private Long postNo = Long.valueOf(1);

    @BeforeEach
    public void save() {
        MbrInfo mbrInfo = MbrInfo.builder()
                .mbrId("tester@naver.com")
                .mbrNickname("테스터")
                .mbrStd(ComCode.MBR_STD_ACTIVE.getCode())
                .role("ROLE_MEMBER")
                .build();

        Post post = Post.builder()
                .postNo(postNo)
                .mbrInfo(mbrInfo)
                .title("TEST 1 번 타이틀입니다.")
                .content("TEST 1번 Content 입니다.")
                .delYn("N")
                .build();

        Post post2 = Post.builder()
                .mbrInfo(mbrInfo)
                .title("TEST 2 번 타이틀입니다.")
                .content("TEST 2번 Content 입니다.")
                .delYn("N")
                .build();

        Likes likes = Likes.builder()
                .likesId(Likes.LikesId.builder()
                        .mbrId("tester@naver.com")
                        .postNo(postNo)
                        .postType("POST").build())
                .post(Post.builder().postNo(postNo).build())
                .build();

        memberRepository.save(mbrInfo);
        memberRepository.flush();
        postRepository.save(post);
        postRepository.save(post2);
        likesRepository.save(likes);
    }

    @Test
    @DisplayName("select() 테스트")
    public void selectTest() {
        QPost post = QPost.post;
        // select는 단일 조회시 해당 필드타입으로 return, 여러개의 경우 Tuple 타입 return
        List<Tuple> results = jpaQueryFactory.select(post.postNo, post.title)
                .from(post)
                .fetch();

        log.info("[selectTest] results : {}", results);
        assertTrue(results.size() > 0);
    }

    @Test
    @DisplayName("selectDistinct() 테스트")
    public void selectDistinctTest() {
        QPost post = QPost.post;

        List<String> results = jpaQueryFactory.selectDistinct(post.mbrInfo.mbrId)
                .from(post)
                .where(post.mbrInfo.mbrId.eq("tester@naver.com"))
                .fetch();

        log.info("[selectDistinctTest] results.get(0) : {}", results.get(0));
        assertTrue(results.size() == 1);
    }

    @Test
    @DisplayName("selectOne() 테스트")
    public void selectOneTest() {
        QPost post = QPost.post;

        Integer results = jpaQueryFactory.selectOne()
                .from(post)
                .where(post.mbrInfo.mbrId.eq("tester@naver.com"))
                .fetchOne();

        log.info("[selectOneTest] results : {}", results);
        assertEquals(1, results);
    }

    @Test
    @DisplayName("exists 테스트")
    public void selectExistsTest() {
        QPost post = QPost.post;
        QLikes likes = QLikes.likes;

        List<Boolean> result = jpaQueryFactory.select(
                JPAExpressions.selectOne()
                        .from(likes)
                        .where(likes.likesId.mbrId.eq("tester@naver.com")
                                .and(likes.likesId.postNo.eq(post.postNo)))
                        .exists())
                .from(post)
                .fetch();

        log.info("[selectExistsTest] results : {}", result);
        assertTrue(result.get(0));
        assertFalse(result.get(1));
    }

    @Test
    @DisplayName("Projections 생성자 테스트")
    public void selectProjectionsConstructor() {
        QPost qPost = QPost.post;
        QLikes qLikes = QLikes.likes;

        List<PostTestDto> posts = jpaQueryFactory
                .select(Projections.constructor(PostTestDto.class,
                        qPost.postNo,
                        qPost.title,
                        qPost.content,
                        JPAExpressions
                                .select(qLikes.likesId.mbrId.count())
                                .from(qLikes)
                                .where(qLikes.likesId.postNo.eq(qPost.postNo)
                                        .and(qLikes.likesId.postType.eq(Constant.REF_TYPE_POST)))
                ))
                .from(qPost)
                .orderBy(new OrderSpecifier<>(Order.DESC, qPost.createDate))
                .fetch();

        log.info("[selectProjectionsConstructor] posts : {}", posts);
        assertTrue(posts.size() > 0);
    }

    @Test
    @DisplayName("CASE 문법 테스트")
    public void selectCaseTest() {
        QPost post = QPost.post;

        List<Tuple> results = jpaQueryFactory.select(
                    post.postNo,
                    new CaseBuilder()
                            .when(post.delYn.eq("N")).then("Active")
                            .when(post.delYn.eq("Y")).then("Deleted")
                            .otherwise("Undefined").as("postStatus")
                )
                .from(post)
                .fetch();
        // alias 를 지정한 튜플 접근은 StringPath 를 사용해야한다.
        StringPath postStatusPath = Expressions.stringPath("postStatus");

        log.info("[selectCaseTest] results : {}", results);
        assertEquals("Active", results.get(0).get(postStatusPath));
    }

    @Test
    @DisplayName("BooleanBuilder 테스트")
    public void booleanBuilderTest() {
        QPost qPost = QPost.post;

        // selectFrom은 자동으로 전체 필드 select
        Post post = jpaQueryFactory.selectFrom(qPost)
                .from(qPost)
                .where(buildPredicate(postNo, "N"))
                .fetchOne();
        log.info("[booleanBuilderTest] post : {}", post.toString());
        assertEquals(post.getPostNo(), postNo);
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
}
