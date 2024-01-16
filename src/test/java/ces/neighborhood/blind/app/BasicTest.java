package ces.neighborhood.blind.app;

import ces.neighborhood.blind.app.dto.AccessTokenResponseDto;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

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

}
