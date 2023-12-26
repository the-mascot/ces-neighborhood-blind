package ces.neighborhood.blind.common.utils;

import ces.neighborhood.blind.common.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;

@Slf4j
@RequiredArgsConstructor
public class WebClientUtils {

    private final WebClientConfig webClientConfig;

//    public ResponseEntity<?> postBlocking() {
//        webClientConfig.webClient()
//                .post()
//                .bodyValue()
//        return null;
//    }

}
