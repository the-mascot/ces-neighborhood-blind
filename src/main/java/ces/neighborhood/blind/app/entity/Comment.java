package ces.neighborhood.blind.app.entity;

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
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMM_NO_SEQ")
    @SequenceGenerator(name = "COMM_NO_SEQ", sequenceName = "COMM_NO_SEQ", allocationSize = 50)
    private Long commentNo;

    private String content;

    @Transient
    private Long likeCnt;

    @Transient
    private Boolean isLiked;

    private String delYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbr_id")
    private MbrInfo mbrInfo;

    @ManyToOne(fetch = FetchType.LAZY)  // @ManyToOne 는 fetch = FetchType.EAGER 가 기본
    @JoinColumn(name = "post_no")
    private Post post;

    @OneToMany(mappedBy = "comment")    // @OneToMany 는 fetch = FetchType.LAZY 가 기본
    private List<Reply> reply;
}
