package ces.neighborhood.blind.app.service;

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
import java.io.InputStream;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.buket}")
    private String buket;

    private static final List<String>
            ALLOWED_IMAGE_EXTENSIONS = Arrays.asList( "jpg", "jfif", "pjpeg", "jpeg", "pjp", "png", "gif", "bmp", "dib", "webp", "heic", "heif");

    private static final String IMAGE_FOLDER_PATTERN = "yyyyMMdd";

    private final AttachmentRepository attachmentRepository;

    private static final int limitImageSize = 10485760;

    public String uploadFileToS3(MultipartFile multipartFile, Principal principal) {
        if (multipartFile.isEmpty()) {
            throw new BizException(ErrorCode.CODE_8000);
        }

        String fileName = multipartFile.getOriginalFilename();
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(fileExt)) {
            throw new BizException(ErrorCode.CODE_8001);
        }
        if (multipartFile.getSize() > limitImageSize) {
            throw new BizException(ErrorCode.CODE_8003);
        }

        String folderPath = new SimpleDateFormat(IMAGE_FOLDER_PATTERN).format(new Date());
        String uuid = UUID.randomUUID().toString();
        String storedFileName = new StringBuilder(uuid).append(".").append(fileExt).toString();
        String fullFileName = new StringBuilder(folderPath).append("/").append(storedFileName).toString();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            amazonS3Client.putObject(buket, fullFileName, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            throw new BizException(ErrorCode.CODE_8002);
        }

        attachmentRepository.save(Attachment.builder()
                .folderPath(folderPath)
                .fileName(fileName)
                .storedFileName(storedFileName)
                .fileExt(fileExt)
                .fileSize(multipartFile.getSize())
                .delYn(Constant.N)
                .createUser(principal.getName())
                .build());

        return amazonS3Client.getUrl(buket, fullFileName).toString();
    }
}
