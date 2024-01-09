package ces.neighborhood.blind.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ces.neighborhood.blind.app.dto.BoardDto;
import ces.neighborhood.blind.app.entity.Board;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select new ces.neighborhood.blind.app.dto.BoardDto(" +
            "b.postNo, b.boardType, b.mbrInfo.mbrNm ,b.title, " +
            "substring(b.content, 1, 300), b.delYn, b.viewCnt, b.likeCnt, count(c.c), b.createDate) " +
            "from Board b left join b.comment c on c.delYn = 'N' " +
            "where b.boardType = :boardType and b.delYn = 'N' " +
            "group by b.postNo, b.boardType, b.mbrInfo.mbrNm ,b.title, " +
            "substring(b.content, 1, 300), b.delYn, b.viewCnt, b.likeCnt, b.createDate " +
            "order by b.createDate desc")
    List<BoardDto> getBoardList(@Param("boardType") String boardType);

    @Query("select new ces.neighborhood.blind.app.dto.BoardDto(" +
            "b.postNo, b.boardType, b.mbrInfo.mbrNm ,b.title, " +
            "substring(b.content, 1, 300), b.delYn, b.viewCnt, b.likeCnt, sum(count(c.commNo), count(c.)), b.createDate) " +
            "from Board b left join b.comment c on c.delYn = 'N' " +
            "where b.boardType is null and b.delYn = 'N' " +
            "group by b.postNo, b.boardType, b.mbrInfo.mbrNm ,b.title, " +
            "substring(b.content, 1, 300), b.delYn, b.viewCnt, b.likeCnt, b.createDate " +
            "order by b.createDate desc")
    List<BoardDto> getBoardList();
}
