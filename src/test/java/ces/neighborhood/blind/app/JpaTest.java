package ces.neighborhood.blind.app;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ces.neighborhood.blind.app.entity.Board;
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
        List<Board> boardList = boardRepository.findAllByBoardTypeOrderByCreateDate("ALL");
        System.out.println("TOTAL SIZE: " + boardList.size());
    }

}
