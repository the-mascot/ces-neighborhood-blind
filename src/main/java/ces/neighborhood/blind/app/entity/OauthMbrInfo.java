package ces.neighborhood.blind.app.entity;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OauthMbrInfo {

    @EmbeddedId
    private OauthMbrInfoKey oauthMbrInfoKey;

    @CreationTimestamp
    @Column(updatable = false)
    private String snsContDate;

    @ManyToOne
    @JoinColumn(name = "mbr_id", referencedColumnName = "mbrId")
    private MbrInfo mbrInfo;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class OauthMbrInfoKey implements Serializable {

        private String oauthId;

        private String provider;

    }
}
