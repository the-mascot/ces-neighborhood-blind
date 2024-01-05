package ces.neighborhood.blind.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import ces.neighborhood.blind.app.dto.BoardListDto;
import ces.neighborhood.blind.app.repository.BoardRepository;
import java.util.List;

@ActiveProfiles("test")
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JpaTest {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void joinTest() {
    }

    @Test
    public void convertDate() {
        List<BoardListDto> boardListDto = boardRepository.getBoardList();
        boardListDto.forEach(s -> {
            System.out.println("title : " + s.getTitle());
            System.out.println("content : " + s.getContent());
            System.out.println("nickname : " + s.getMbrNm());
        });
    }


}
