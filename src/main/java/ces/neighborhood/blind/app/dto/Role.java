package ces.neighborhood.blind.app.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Role {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_MEMBER("ROLE_MEMBER"),
    ROLE_GUEST("ROLE_GUEST")
    ;

    String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }
}
