package ces.neighborhood.blind.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
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

    private String mbrId;

    private String content;

    private String delYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_no")
    private Board board;
}
