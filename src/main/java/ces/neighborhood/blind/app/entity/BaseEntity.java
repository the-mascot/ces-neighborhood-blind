package ces.neighborhood.blind.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Timestamp;
import lombok.Getter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
@Getter
public abstract class BaseEntity {

    // 마지막수정자
    private String modifyUser;

    // 최초생성자
    private String createUser;

    // 마지막수정일자
    @UpdateTimestamp
    @Column(updatable = false)
    private Timestamp modifyDate;

    // 최초생성일자
    @CreationTimestamp
    @Column(insertable = false)
    private Timestamp createDate;

}
