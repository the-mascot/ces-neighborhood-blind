package ces.neighborhood.blind.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Attachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FILE_NO_SEQ")
    @SequenceGenerator(name = "FILE_NO_SEQ", sequenceName = "FILE_NO_SEQ", allocationSize = 50)
    private Long fileNo;

    private String folderPath;

    private String fileName;

    private String storedFileName;

    private String fileExt;

    private Long fileSize;

    private String refType;

    private Long refNo;

    private String delYn;

    public void setRefNo(Long refNo) {
        this.refNo = refNo;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }
}
