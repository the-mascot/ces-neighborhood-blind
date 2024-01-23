package ces.neighborhood.blind.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Attachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ATTACHMENT_NO_SEQ")
    @SequenceGenerator(name = "ATTACHMENT_NO_SEQ", sequenceName = "ATTACHMENT_NO_SEQ", allocationSize = 50)
    private Long fileNo;

    private String fileName;

    private String storedFileName;

    private String fileExt;

    private Long fileSize;

    private String refType;

    private Long refNo;

    private String delYn;

}
