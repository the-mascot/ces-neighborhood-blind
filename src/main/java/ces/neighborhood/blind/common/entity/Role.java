package ces.neighborhood.blind.common.entity;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Role {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_MEMBER("ROLE_MEMBER"),
    ROLE_GUEST("ROLE_GUEST")
    ;

    String description;

    Role(String description) {
        this.description = description;
    }
}
