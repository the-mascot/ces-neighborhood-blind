package ces.neighborhood.blind.app.entity;

import ces.neighborhood.blind.common.utils.ComUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMM_NO_SEQ")
    @SequenceGenerator(name = "COMM_NO_SEQ", sequenceName = "COMM_NO_SEQ", allocationSize = 50)
    private Long commentNo;

    private String content;

    private String delYn;

    @Transient
    private String createDateStr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbr_id")
    private MbrInfo mbrInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_no")
    private Board board;

    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY)
    private List<Reply> reply;

    public String getCreateDateStr() {
        return ComUtils.calculateTimeDifference(createDate);
    }

    public void setMbrInfo(MbrInfo mbrInfo) {
        this.mbrInfo = mbrInfo;
    }
}
