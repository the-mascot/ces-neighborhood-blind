package ces.neighborhood.blind.app;

import ces.neighborhood.blind.common.exception.ErrorCode;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

public class BasicTest {

    public BasicTest() {
        this.toStringTest();
    }

    @Test
    public void toStringTest() {

        System.out.println(ErrorCode.CODE_1001.toString());
    }

}
