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
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POST_NO_SEQ")
    @SequenceGenerator(name = "POST_NO_SEQ", sequenceName = "POST_NO_SEQ", allocationSize = 50)
    private Long postNo;

    @Transient
    private String nickname;

    private String title;

    private String content;

    private String delYn;

    @Builder.Default
    private Integer viewCnt = 0;

    @Transient  // 테이블에 없는 필드
    private Long likeCnt;

    @Transient
    private Boolean isLiked;

    @Transient
    private String createDateStr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbr_id")
    private MbrInfo mbrInfo;

    @Builder.Default    // 생성자 이용시 초기값 지정을 위해 사용
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Comment> comment = new ArrayList<>();

    public Post(Long postNo) {
        this.postNo = postNo;
    }

    public String getCreateDateStr() {
        return ComUtils.calculateTimeDifference(createDate);
    }
}
