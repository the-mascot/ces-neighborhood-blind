package ces.neighborhood.blind.app.entity;

import jakarta.persistence.Entity;
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
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOARD_NO_SEQ")
    @SequenceGenerator(name = "BOARD_NO_SEQ", sequenceName = "BOARD_NO_SEQ", allocationSize = 50)
    private Long boardNo;

    private String boardType;

    private String title;

    private String content;

    private String delYn;

    private Integer likeCnt;

    @ManyToOne
    @JoinColumn(name = "mbr_Id")
    private MbrInfo mbrInfo;

}
