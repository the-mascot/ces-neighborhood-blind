package ces.neighborhood.blind.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
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
}
