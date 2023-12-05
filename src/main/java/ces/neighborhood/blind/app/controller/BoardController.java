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
import ces.neighborhood.blind.app.entity.MbrBoard;
import ces.neighborhood.blind.app.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @RequestMapping("/board")
    public String board(Model model, HttpServletRequest request, HttpServletResponse response) {
        return "/board/board";
    }

    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @RequestMapping("/write")
    public String write(Model model) {
        model.addAttribute("mbrBoard", new MbrBoard());
        return "/board/write";
    }

    @ResponseBody
    @PostMapping("/board/post")
    public ResponseEntity post(@RequestBody MbrBoard mbrBoard) {
        return ApiResponse.success(boardService.saveMbrBoard(mbrBoard));
    }
}
