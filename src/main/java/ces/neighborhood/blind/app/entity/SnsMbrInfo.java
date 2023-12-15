package ces.neighborhood.blind.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

public class SnsMbrInfo {

    @Id
    private String snsId;
    @Id
    private String snsType;

    private String snsName;

    @CreationTimestamp
    @Column(updatable = false)
    private String snsContDate;

    private String refreshToken;

    private String mbrId;
}
