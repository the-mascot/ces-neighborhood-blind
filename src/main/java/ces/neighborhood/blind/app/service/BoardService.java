package ces.neighborhood.blind.app.service;

import org.springframework.stereotype.Service;

import ces.neighborhood.blind.app.entity.MbrBoard;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.repository.BoardRepository;
import ces.neighborhood.blind.app.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public List<MbrBoard> getBoardList(String boardType) {
        return boardRepository.findByBoardType(boardType);
    }

    public long saveMbrBoard(MbrBoard mbrBoard) {
        return boardRepository.save(mbrBoard).getBoardNo();
    }
}
