package ces.neighborhood.blind.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ces.neighborhood.blind.app.dto.ApiResponse;
import ces.neighborhood.blind.app.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * image upload
     */
    @PostMapping("/upload/image")
    public ResponseEntity uploadImage(@RequestBody MultipartFile image) {
        return ApiResponse.success(fileService.uploadImage(image));
    }

}
