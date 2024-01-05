package ces.neighborhood.blind.app;

import ces.neighborhood.blind.app.dto.AccessTokenResponseDto;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BasicTest {

    @Test
    public void toStringTest() throws Exception {
        String response = "{\"access_token\":\"1234\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        AccessTokenResponseDto accessTokenResponseDto = objectMapper.readValue(response, AccessTokenResponseDto.class);
    }

}
