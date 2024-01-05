package ces.neighborhood.blind.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ces.neighborhood.blind.app.dto.BoardListDto;
import ces.neighborhood.blind.app.entity.Board;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select new ces.neighborhood.blind.app.dto.BoardListDto(" +
            "b.postNo, b.boardType, b.mbrInfo.mbrNm ,b.title, " +
            "substring(b.content, 1, 300), b.delYn, b.viewCnt, b.likeCnt, count(c), b.createDate) " +
            "from Board b left join b.comment c on c.delYn = 'N' " +
            "where b.boardType = :boardType " +
            "group by b.postNo, b.boardType, b.mbrInfo.mbrNm ,b.title, " +
            "substring(b.content, 1, 300), b.delYn, b.viewCnt, b.likeCnt, b.createDate " +
            "order by b.createDate desc")
    List<BoardListDto> getBoardList(@Param("boardType") String boardType);

    @Query("select new ces.neighborhood.blind.app.dto.BoardListDto(" +
            "b.postNo, b.boardType, b.mbrInfo.mbrNm ,b.title, " +
            "substring(b.content, 1, 300), b.delYn, b.viewCnt, b.likeCnt, count(c), b.createDate) " +
            "from Board b left join b.comment c on c.delYn = 'N' " +
            "where b.boardType is null " +
            "group by b.postNo, b.boardType, b.mbrInfo.mbrNm ,b.title, " +
            "substring(b.content, 1, 300), b.delYn, b.viewCnt, b.likeCnt, b.createDate " +
            "order by b.createDate desc")
    List<BoardListDto> getBoardList();
}
