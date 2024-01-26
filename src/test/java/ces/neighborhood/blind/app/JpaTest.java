package ces.neighborhood.blind.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import ces.neighborhood.blind.app.dto.BoardDto;
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
    public void joinTest() {
    }

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

}
