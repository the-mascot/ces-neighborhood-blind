package ces.neighborhood.blind.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ces.neighborhood.blind.app.entity.Attachment;
import ces.neighborhood.blind.app.repository.AttachmentRepository;
import ces.neighborhood.blind.common.code.Constant;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${file.upload.image.server}")
    private String imageUploadServer;

    private static final List<String>
            ALLOWED_IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp", "heic", "heif");

    private static final String IMAGE_FOLDER_PATTERN = "yyyyMMdd";

    private final AttachmentRepository attachmentRepository;

    private String imageSrc;

    public String uploadImage(MultipartFile image, Principal principal) {
        if (image.isEmpty()) {
            throw new BizException(ErrorCode.CODE_8000);
        }
        // 파일명
        String fileName = image.getOriginalFilename();
        // 확장자
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        // 확장자 검사
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(fileExt)) {
            throw new BizException(ErrorCode.CODE_8001);
        }
        Map<String, Object> result = new HashMap<>();
        try {
            // base64 encoding 한 yyyyMMdd 형태의 폴더이름
            String folderPath = Base64.getEncoder().encodeToString(
                    new SimpleDateFormat(IMAGE_FOLDER_PATTERN)
                            .format(new Date()).getBytes());
            String absolutePath = new File("").getAbsolutePath() + "\\src\\main\\resources\\static\\images";
            Path uploadFolderPath = Paths.get(absolutePath, folderPath).toAbsolutePath();
            // 경로 폴더 유무 확인 및 생성
            if (!Files.exists(uploadFolderPath)) {
                Files.createDirectories(uploadFolderPath);
            }
            // 파일명 UUID 사용
            String uuid = UUID.randomUUID().toString();
            StringBuilder stringBuilder = new StringBuilder(uuid);
            stringBuilder.append(".");
            stringBuilder.append(fileExt);
            String storedFileName = stringBuilder.toString();
            Path uploadPath = Paths.get(uploadFolderPath.toString(), storedFileName);
            // 이미지 저장
            image.transferTo(uploadPath);
            // DB 첨부파일 저장
            attachmentRepository.save(Attachment.builder().
                    fileName(fileName)
                    .storedFileName(storedFileName)
                    .fileExt(fileExt)
                    .fileSize(image.getSize())
                    .delYn(Constant.N)
                    .createUser(principal.getName())
                    .build());
            imageSrc = imageUploadServer + folderPath + "/" + storedFileName;
        } catch (IOException e) {
            throw new BizException(ErrorCode.CODE_8002);
        }

        return imageSrc;
    }
}
