package ces.neighborhood.blind.app.service.file;

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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * File Upload 관련 Service
 * </pre>
 *
 * @version 1.0
 * @author mascot
 * @since 2024.01.12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${file.upload.image.server}")
    private String imageUploadServer;

    private static final List<String>
            ALLOWED_IMAGE_EXTENSIONS = Arrays.asList( "jpg", "jfif", "pjpeg", "jpeg", "pjp", "png", "gif", "bmp", "dib", "webp", "heic", "heif");

    private static final String IMAGE_FOLDER_PATTERN = "yyyyMMdd";

    private final AttachmentRepository attachmentRepository;

    // 파일사이즈 제한 10MB
    private static final int limitImageSize = 10 * 1024 * 1024;

    private String imageSrc;

    /**
     * 이미지 업로드
     * @param image, principal
     * @return 이미지 업로드 경로 (upload server/Base64Enc(yyyyMMdd)/UUID.확장자)
     * @throws
     */
    public String uploadImage(MultipartFile image, Principal principal) {
        if (image.isEmpty()) {
            throw new BizException(ErrorCode.CODE_8000);
        }
        // 파일명
        String originalFileName = image.getOriginalFilename();
        // 확장자
        String fileExt = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();

        // 확장자 검사
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(fileExt)) {
            throw new BizException(ErrorCode.CODE_8001);
        }

        // 파일 사이즈 검사
        if (image.getSize() > limitImageSize) {
            throw new BizException(ErrorCode.CODE_8003);
        }

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
            String fileName = stringBuilder.toString();
            Path uploadPath = Paths.get(uploadFolderPath.toString(), fileName);
            // 이미지 저장
            image.transferTo(uploadPath);
            // Attachment 저장
            attachmentRepository.save(Attachment.builder()
                    .folderPath(folderPath)
                    .fileName(fileName)
                    .originalFileName(originalFileName)
                    .fileExt(fileExt)
                    .fileSize(image.getSize())
                    .delYn(Constant.N)
                    .createUser(principal.getName())
                    .build());
            imageSrc = imageUploadServer + folderPath + "/" + fileName;
        } catch (IOException e) {
            throw new BizException(ErrorCode.CODE_8002);
        }

        return imageSrc;
    }
}
