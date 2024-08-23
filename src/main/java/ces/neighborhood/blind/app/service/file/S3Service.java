package ces.neighborhood.blind.app.service.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

import ces.neighborhood.blind.app.entity.Attachment;
import ces.neighborhood.blind.app.repository.AttachmentRepository;
import ces.neighborhood.blind.common.code.Constant;
import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * AWS S3 업로드 관련 Service
 * </pre>
 *
 * @version 1.0
 * @author mascot
 * @since 2024.01.18
 */
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.buket}")
    private String buket;

    private static final List<String>
            ALLOWED_IMAGE_EXTENSIONS = Arrays.asList("jpg", "jfif", "pjpeg", "jpeg", "pjp", "png", "gif", "bmp", "dib", "webp", "heic", "heif");

    private static final String IMAGE_FOLDER_PATTERN = "yyyyMMdd";

    private final AttachmentRepository attachmentRepository;

    // 파일사이즈 제한 10MB
    private static final int limitImageSize = 10 * 1024 * 1024;

    /**
     * AWS S3에 File Upload
     * @param multipartFile, principal
     * @return 업로드된 파일 url
     * @throws
     */
    public String uploadFileToS3(MultipartFile multipartFile, Principal principal) {
        if (multipartFile.isEmpty()) {
            throw new BizException(ErrorCode.CODE_8000);
        }

        // 파일명
        String originalFileName = multipartFile.getOriginalFilename();
        // 확장자
        String fileExt = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();

        // 확장자 검사
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(fileExt)) {
            throw new BizException(ErrorCode.CODE_8001);
        }

        // 파일 사이즈 검사
        if (multipartFile.getSize() > limitImageSize) {
            throw new BizException(ErrorCode.CODE_8003);
        }

        // base64 encoding 한 yyyyMMdd 형태의 폴더이름
        String folderPath = new SimpleDateFormat(IMAGE_FOLDER_PATTERN).format(new Date());
        // 저장 파일명 UUID 사용
        String uuid = UUID.randomUUID().toString();
        String fileName = new StringBuilder(uuid).append(".").append(fileExt).toString();
        String fullFileName = new StringBuilder(folderPath).append("/").append(fileName).toString();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            // 이미지 저장
            amazonS3Client.putObject(buket, fullFileName, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            throw new BizException(ErrorCode.CODE_8002);
        }

        // Attachment 저장
        attachmentRepository.save(Attachment.builder()
                .folderPath(folderPath)
                .fileName(fileName)
                .originalFileName(originalFileName)
                .fileExt(fileExt)
                .fileSize(multipartFile.getSize())
                .delYn(Constant.N)
                .createUser(principal.getName())
                .build());

        return amazonS3Client.getUrl(buket, fullFileName).toString();
    }
}
