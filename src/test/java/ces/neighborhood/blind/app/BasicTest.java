package ces.neighborhood.blind.app;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import ces.neighborhood.blind.app.dto.AccessTokenResponseDto;
import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicTest {

    @Test
    public void toStringTest() throws Exception {
        String response = "{\"access_token\":\"1234\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        AccessTokenResponseDto accessTokenResponseDto = objectMapper.readValue(response, AccessTokenResponseDto.class);
    }

    @Test
    public void escapeTest() throws Exception {
        String html = "<p>ㅁㅇ</p><p>ㄹ</p><p>ㅇ</p><p><strong>ㄹㄹㄹㄹ</strong></p>";
        System.out.println(Jsoup.parse(html).text());
    }

    @Test
    public void absolutePathTest() {
        //Path path = Paths.get("C:", "attachment");
        //String absolutePath = path.toAbsolutePath().toString();
        String folderPath = Base64.getEncoder().encodeToString(
                new SimpleDateFormat("yyyyMMdd")
                        .format(new Date()).getBytes());
        String absolutePath = new File("").getAbsolutePath() + "\\src\\main\\resources\\upload\\images";
        Path uploadFolderPath = Paths.get(absolutePath, folderPath).toAbsolutePath();
        System.out.println("절대경로 : " + absolutePath);
        System.out.println("uploadFolderPath : " + uploadFolderPath);
    }

    @Test
    public void makeDir() {
        Path path = Paths.get("C:", "neighborhood", "attachment", "images", "dd.png");
        String absolutePath = path.toAbsolutePath().toString();
        File file = new File(absolutePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Test
    public void bigIntegerTest() {
        SecureRandom secureRandom = new SecureRandom();
        log.info(secureRandom.toString());
        String state = new BigInteger(130, secureRandom).toString();
        log.info("state : {}", state);
    }

}
