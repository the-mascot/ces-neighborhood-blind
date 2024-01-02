package ces.neighborhood.blind.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ces.neighborhood.blind.app.dto.ApiResponse;
import ces.neighborhood.blind.app.entity.Board;
import ces.neighborhood.blind.app.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @RequestMapping("/board")
    public String board(Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("boardList", boardService.getBoardList(model));
        return "/board/board";
    }

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @RequestMapping("/write")
    public String write(Model model) {
        model.addAttribute("mbrBoard");
        return "/board/write";
    }

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @ResponseBody
    @PostMapping("/board/post")
    public ResponseEntity post(@RequestBody Board board, Principal principal) {
        board.setCreateUser(principal.getName());
        return ApiResponse.success(boardService.saveMbrBoard(board, principal));
    }
}
