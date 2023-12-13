package ces.neighborhood.blind.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.sql.Timestamp;
import lombok.Getter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
@Getter
public abstract class BaseEntity {

    // 마지막수정일자
    @UpdateTimestamp
    @Column(updatable = false)
    private Timestamp modifyDate;

    // 최초생성일자
    @CreationTimestamp
    @Column(insertable = false)
    private Timestamp createDate;

}
