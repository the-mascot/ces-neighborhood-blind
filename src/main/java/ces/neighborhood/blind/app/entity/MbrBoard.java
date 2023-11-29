package ces.neighborhood.blind.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
    @GeneratedValue
    private String boardNo;
    private String boardType;
    private String title;
    private String content;
    private String delYn;
    private String mbrId;
    private String updateDate;
    private String likeCnt;

}
