package ces.neighborhood.blind.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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
public class MbrBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOARD_NO_SEQ")
    @SequenceGenerator(name = "BOARD_NO_SEQ", sequenceName = "BOARD_NO_SEQ", allocationSize = 50)
    private Long boardNo;
    private String boardType;
    private String title;
    private String content;
    private String delYn;
    private String mbrId;
    private Timestamp updateDate;
    private Integer likeCnt;

}
