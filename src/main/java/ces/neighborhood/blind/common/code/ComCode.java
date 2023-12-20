package ces.neighborhood.blind.common.code;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ComCode {
    MBR_STD_ACTIVE("1000"),
    MBR_STD_LOCKED("1001"),
    MBR_STD_WITHDRAW("1002"),
    MBR_STD_EXPIRED("1003")
    ;

    String code;

    ComCode(String code) {
        this.code = code;
    }

}
