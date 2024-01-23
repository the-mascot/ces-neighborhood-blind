package ces.neighborhood.blind.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public abstract class BaseEntity {

    // 마지막수정자
    protected String modifyUser;

    // 마지막수정일자
    @UpdateTimestamp
    @Column(insertable = false)
    protected Timestamp modifyDate;

    // 최초생성자
    protected String createUser;

    // 최초생성일자
    @CreationTimestamp
    @Column(updatable = false)
    protected Timestamp createDate;

}
