package ces.neighborhood.blind.app.entity;

import ces.neighborhood.blind.app.dto.ComCode;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
public class MbrInfo implements UserDetails {

    @Id
    // 회원 ID (이메일 형식)
    private String mbrId;

    // 비밀번호
    private String mbrPw;

    // 권한
    private String role;

    // 닉네임
    private String mbrNickname;

    private String mbrNm;

    private String mbrEmail;

    private String mbrStd;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp joinDate;

    private Timestamp lastLoginDate;

    private Timestamp withdrawDate;

    @UpdateTimestamp
    @Column(insertable = false)
    private Timestamp modifyDate;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return mbrPw;
    }

    @Override
    public String getUsername() {
        return mbrId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
