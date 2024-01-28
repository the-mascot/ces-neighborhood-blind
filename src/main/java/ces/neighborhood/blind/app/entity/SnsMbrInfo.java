package ces.neighborhood.blind.app.entity;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SnsMbrInfo {

    @EmbeddedId
    private SnsMbrInfoKey snsMbrInfoKey;

    private String snsName;

    @CreationTimestamp
    @Column(updatable = false)
    private String snsContDate;

    private String refreshToken;

    private String mbrId;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class SnsMbrInfoKey implements Serializable {

        private String snsId;

        private String snsType;

    }
}
