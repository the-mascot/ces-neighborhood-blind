package ces.neighborhood.blind.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ces.neighborhood.blind.app.dto.ApiResponse;
import ces.neighborhood.blind.app.service.file.FileService;
import ces.neighborhood.blind.app.service.file.S3Service;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    private final S3Service s3Service;

    /**
     * image upload
     */
    @PostMapping("/upload/image")
    public ResponseEntity uploadImage(@RequestBody MultipartFile image, Principal principal) {
        return ApiResponse.success(fileService.uploadImage(image, principal));
    }

    /**
     * image upload to S3
     */
    @PostMapping("/s3/upload/image")
    public ResponseEntity uploadImageToS3(@RequestBody MultipartFile image, Principal principal) {
        return ApiResponse.success(s3Service.uploadFileToS3(image, principal));
    }
}
