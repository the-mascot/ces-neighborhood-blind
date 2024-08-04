package ces.neighborhood.blind.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ces.neighborhood.blind.app.dto.ApiResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class PwaController {

    @PostMapping("/message")
    public ResponseEntity saveMessage(
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestPart(value = "videoFile", required = false) MultipartFile videoFile,
            @RequestPart(value = "attachFiles", required = false) List<MultipartFile> attachFiles,
            @RequestPart(value = "voiceFile", required = false) MultipartFile voiceFile,
            @RequestPart(value = "content") String content,
            @RequestPart(value = "receivers", required = false) String receivers) {

      log.error("hello");
      log.error(content);
      return ApiResponse.success("D");
    }

}
