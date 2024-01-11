package ces.neighborhood.blind.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ces.neighborhood.blind.app.dto.BoardDto;
import ces.neighborhood.blind.app.entity.Board;
import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select new ces.neighborhood.blind.app.dto.BoardDto(" +
            "b.postNo, b.boardType, b.mbrInfo.mbrNickname ,b.title, " +
            "substring(b.content, 1, 300), b.delYn, b.viewCnt, b.likeCnt, (count(c) + count(r)), b.createDate) " +
            "from Board b left join b.comment c on c.delYn = 'N' left join Reply r on c.delYn = 'N' " +
            "where b.boardType = :boardType and b.delYn = 'N' " +
            "group by b.postNo, b.boardType, b.mbrInfo.mbrNickname ,b.title, " +
            "substring(b.content, 1, 300), b.delYn, b.viewCnt, b.likeCnt, b.createDate " +
            "order by b.createDate desc")
    List<BoardDto> getBoardList(@Param("boardType") String boardType);

    @Query("select new ces.neighborhood.blind.app.dto.BoardDto(" +
            "b.postNo, b.boardType, b.mbrInfo.mbrNickname ,b.title, " +
            "substring(b.content, 1, 300), b.delYn, b.viewCnt, b.likeCnt, (count(c) + count(r)), b.createDate) " +
            "from Board b left join b.comment c on c.delYn = 'N' left join Reply r on c.delYn = 'N' " +
            "where b.boardType is null and b.delYn = 'N' " +
            "group by b.postNo, b.boardType, b.mbrInfo.mbrNickname ,b.title, " +
            "substring(b.content, 1, 300), b.delYn, b.viewCnt, b.likeCnt, b.createDate " +
            "order by b.createDate desc")
    List<BoardDto> getBoardList();

    @Query("select new ces.neighborhood.blind.app.dto.BoardDto(" +
            "b.postNo, b.boardType, b.mbrInfo.mbrNickname ,b.title, " +
            "b.content, b.delYn, b.viewCnt, b.likeCnt, (count(c) + count(r)), b.createDate) " +
            "from Board b left join b.comment c on c.delYn = 'N' left join Reply r on c.delYn = 'N' " +
            "where b.boardType is null and b.postNo = :postNo and b.delYn = 'N' " +
            "group by b.postNo, b.boardType, b.mbrInfo.mbrNickname ,b.title, " +
            "substring(b.content, 1, 300), b.delYn, b.viewCnt, b.likeCnt, b.createDate ")
    Optional<BoardDto> getBoard(@Param("postNo") Long postNo);
}
