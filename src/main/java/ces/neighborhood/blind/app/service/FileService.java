package ces.neighborhood.blind.app.service;

import ces.neighborhood.blind.common.exception.BizException;
import ces.neighborhood.blind.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileService {

    public String uploadImage(MultipartFile image) {
        if (image.isEmpty()) {
            throw new BizException(ErrorCode.CODE_8000);
        }

        return "";
    }

}
