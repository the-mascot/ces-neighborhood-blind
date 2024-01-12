package ces.neighborhood.blind.app.controller;

import org.eclipse.jetty.client.HttpResponse;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import ces.neighborhood.blind.app.dto.ApiResponse;
import ces.neighborhood.blind.app.entity.Board;
import ces.neighborhood.blind.app.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시판 목록 페이지
     */
    @GetMapping("/board")
    public String board(Model model) {
        model.addAttribute("boardList", boardService.getBoardList(model));
        return "/board/board";
    }

    /**
     * 게시글 작성 페이지
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @GetMapping("/board/write")
    public String write(Model model) {
        return "/board/write";
    }

    /**
     * 게시글 상세 페이지
     */
    @GetMapping("/board/post/{postNo}")
    public String posts(Model model, @PathVariable Long postNo) {
        model.addAttribute("board", boardService.getPost(postNo));
        return "/board/post";
    }

    /**
     * 게시글 등록
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @ResponseBody
    @PostMapping("/board/write/post")
    public ResponseEntity post(@RequestBody Board board, Principal principal) {
        return ApiResponse.success(boardService.saveMbrBoard(board, principal));
    }
}
