package ces.neighborhood.blind.app.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import ces.neighborhood.blind.app.entity.Board;
import ces.neighborhood.blind.app.repository.BoardRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public List<Board> getBoardList(Model model) {
        String boardType = String.valueOf(model.getAttribute("boardType"));
        return boardRepository.findByBoardType();
    }

    public long saveMbrBoard(Board board) {
        return boardRepository.save(board).getBoardNo();
    }
}
