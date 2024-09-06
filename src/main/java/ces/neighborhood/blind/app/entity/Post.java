package ces.neighborhood.blind.app.entity;

import ces.neighborhood.blind.common.utils.ComUtils;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POST_NO_SEQ")
    @SequenceGenerator(name = "POST_NO_SEQ", sequenceName = "POST_NO_SEQ", allocationSize = 50)
    private Long postNo;

     @Access(AccessType.PROPERTY)
    private String title;

    private String content;

    private String delYn;

    @Builder.Default
    private Integer viewCnt = 0;

    @Transient
    private String createDateStr;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mbr_id")
    private MbrInfo mbrInfo;

    // N:1 관계 금지!! 실무에서 사용 X
//    @Builder.Default
//    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
//    private List<Comment> comment = new ArrayList<>();

    public Post(Long postNo) {
        this.postNo = postNo;
    }

    public String getCreateDateStr() {
        return ComUtils.calculateTimeDifference(createDate);
    }
}
