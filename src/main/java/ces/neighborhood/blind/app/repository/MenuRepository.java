package ces.neighborhood.blind.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ces.neighborhood.blind.app.entity.Menu;
import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query(value = """
        WITH RECURSIVE MENUHIERARCHY AS (
            SELECT
                M.MENU_ID,
                M.PARENT_MENU_ID,
                1 AS DEPTH,
                M.SORT_NO,
                M.MENU_NM,
                M.MENU_DESC,
                M.PAGE_URL
            FROM MENU M
            WHERE M.PARENT_MENU_ID IS NULL
              AND M.USE_YN = 'Y'
              AND M.DEL_YN = 'N'
            UNION ALL
            SELECT
                M.MENU.ID,
                M.PARENT_MENU_ID,
                MH.DEPTH + 1 AS DEPTH,
                M.SORT_NO,
                M.MENU_NM,
                M.MENU_DESC,
                M.PAGE_URL
            FROM MENU M
            INNER JOIN MENUHIERARCHY MH
            ON M.PARENT_MENU_ID = MH.MENU_ID
            UNION ALL
            SELECT
                M.MENU.ID,
                M.PARENT_MENU_ID,
                MH.DEPTH + 1 AS DEPTH,
                M.SORT_NO,
                M.MENU_NM,
                M.MENU_DESC,
                M.PAGE_URL
            FROM MENU M
            INNER JOIN MENUHIERARCHY MH
            ON M.PARENT_MENU_ID = MH.MENU_ID
        )
        SELECT * FROM MENUHIERARCHY
    """, nativeQuery = true)
    List<Menu> findAllMenuWithHierarchy();

}
