package ces.neighborhood.blind.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
public class MbrInfo {

    @Id
    private String mbrId;
    private String mbrPw;
    private String mbrNickname;
    private String mbrNm;
    private String mbrEmail;
    private Timestamp joinDate;
    private String mbrStd;
    private Timestamp lastLoginDate;
    private Timestamp withdrawDate;
}
