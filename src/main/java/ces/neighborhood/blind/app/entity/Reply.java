package ces.neighborhood.blind.app.entity;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Reply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPLY_NO_SEQ")
    @SequenceGenerator(name = "REPLY_NO_SEQ", sequenceName = "REPLY_NO_SEQ", allocationSize = 50)
    private Long replyNo;

    @Transient
    private String nickname;

    private String content;

    @Transient
    private Long likeCnt;

    @Transient
    private Boolean isLiked;

    private String delYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbr_id")
    private MbrInfo mbrInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_no")
    private Comment comment;
}
