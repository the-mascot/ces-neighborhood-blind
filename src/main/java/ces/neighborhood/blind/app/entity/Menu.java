package ces.neighborhood.blind.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;
import java.sql.Timestamp;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MENU_SEQ")
    @SequenceGenerator(name = "MENU_SEQ", sequenceName = "MENU_SEQ")
    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "parent_menu_id")
    private String parentMenuId;

    @Transient
    private Integer depth;

    @Column(name = "sort_no")
    private Integer sortNo;

    @Column(name = "menu_nm")
    private String menuNm;

    @Column(name = "menu_desc")
    private String menuDesc;

    @Column(name = "page_url")
    private String pageUrl;

    @Column(name = "use_yn")
    private String useYn;

    @Column(name = "del_yn")
    private String delYn;

    @Column(name = "modify_user")
    private String modifyUser;

    @Column(name = "modify_date")
    private Timestamp modifyDate;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "create_date")
    private Timestamp createDate;
}
