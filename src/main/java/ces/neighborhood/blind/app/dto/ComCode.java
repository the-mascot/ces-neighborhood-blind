package ces.neighborhood.blind.app.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ComCode {
    MBR_STD_ACTIVE("1000")
    ;

    String code;

    ComCode(String code) {
        this.code = code;
    }

}
