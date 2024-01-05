package ces.neighborhood.blind.app.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import ces.neighborhood.blind.app.dto.BoardListDto;
import ces.neighborhood.blind.app.entity.Board;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.repository.BoardRepository;
import ces.neighborhood.blind.common.code.Constant;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public List<BoardListDto> getBoardList(Model model) {
        String boardType = String.valueOf(model.getAttribute("boardType"));
        List<BoardListDto> boardList;
        if (StringUtils.equals(boardType, Constant.BOARD_TYPE_ALL) || StringUtils.equals(boardType, Constant.NULL.toLowerCase())) {
            boardList = boardRepository.getBoardList();
        } else {
            boardList = boardRepository.getBoardList(boardType);
        }
        return boardList;
    }

    public long saveMbrBoard(Board board, Principal principal) {
        board.setMbrInfo(MbrInfo.builder().mbrId(principal.getName()).build());
        board.setCreateUser(principal.getName());
        return boardRepository.save(board).getPostNo();
    }

    public Board getPost(Long postNo) {
        return boardRepository.findById(postNo).get();
    }
}
