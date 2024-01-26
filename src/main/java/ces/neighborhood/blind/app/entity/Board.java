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
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POST_NO_SEQ")
    @SequenceGenerator(name = "POST_NO_SEQ", sequenceName = "POST_NO_SEQ", allocationSize = 50)
    private Long postNo;

    private String boardType;

    private String title;

    private String content;

    private String delYn;

    @Builder.Default
    private Integer viewCnt = 0;

    @Transient
    private String createDateStr;

    @ManyToOne
    @JoinColumn(name = "mbr_id")
    private MbrInfo mbrInfo;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    private List<Comment> comment;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    private List<Reply> Reply;

    public String getCreateDateStr() {
        return ComUtils.calculateTimeDifference(createDate);
    }

    public void setDelYn(String delYn) {
        this.delYn = delYn;
    }

    public void setMbrInfo(MbrInfo mbrInfo) {
        this.mbrInfo = mbrInfo;
    }
}
