package ces.neighborhood.blind.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import ces.neighborhood.blind.app.dto.BoardDto;
import ces.neighborhood.blind.app.entity.Board;
import ces.neighborhood.blind.app.entity.Likes;
import ces.neighborhood.blind.app.repository.BoardRepository;
import ces.neighborhood.blind.app.repository.LikesRepository;
import java.util.List;

@ActiveProfiles("test")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JpaTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private LikesRepository likesRepository;

    @Test
    public void convertDate() {
        long startTime = System.currentTimeMillis();
        List<BoardDto> boardDto = boardRepository.getBoardList("dmstn1812@naver.com");
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
        // Board 와 즉지 로딩인 MbrInfo select 발생
        List<Board> boardList = boardRepository.findAll();
        System.out.println("findAll 종료");
        // 지연 로딩인 Comment select 발생
        // 부모 Board의 개수 만큼 +1 의 Comment select 쿼리 발생.
        boardList.forEach(s -> System.out.println("postNo : " + s.getPostNo() + ", comment size : " + s.getComment().size()));
    }

    @Test
    public void joinTest() {
        // 일반 Join 테스트
        List<Board> boardList = boardRepository.findAllWithJustJoin();
        System.out.println("findAllWithJustJoin 종료");
        boardList.forEach(s -> System.out.println("postNo : " + s.getPostNo() + ", comment size : " + s.getComment().size()));
    }

    @Test
    public void fetchJoinTest() {
        // Fetch Join 테스트
        List<Board> boardList = boardRepository.findAllWithFetchJoin();
        System.out.println("findAllWithFetchJoin 종료");
        boardList.forEach(s -> System.out.println("postNo : " + s.getPostNo() + ", comment size : " + s.getComment().size()));
    }

    @Test
    @DisplayName("EntityGraph 테스트")
    public void entityGraphTest() {
        List<Board> boardList = boardRepository.findAll();
        System.out.println("EntityGraph 종료");
        boardList.forEach(s -> System.out.println("postNo : " + s.getPostNo() + ", comment size : " + s.getComment().size()));
        boardList.forEach(s -> System.out.println("nickname : " + s.getMbrInfo().getMbrNickname()));
    }
}
