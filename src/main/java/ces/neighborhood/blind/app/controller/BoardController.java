package ces.neighborhood.blind.app.controller;

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
import ces.neighborhood.blind.app.dto.PostDto;
import ces.neighborhood.blind.app.entity.Board;
import ces.neighborhood.blind.app.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    @GetMapping("/board/write")
    public String write(Model model) {
        return "/board/write";
    }

    /**
     * 게시글 상세 페이지
     * 조회수 증가 서비스 프로세스 : 세션당 일회 증가 가능
     */
    @GetMapping("/board/post/{postNo}")
    public String posts(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable Long postNo) throws Exception {
        HttpSession session = request.getSession();
        // session 에서 조회한 게시물 목록 받아오기
        List<Long> viewedPosts = (List<Long>) session.getAttribute("viewedPosts");
        if (viewedPosts == null) {
            viewedPosts = new ArrayList<>();
        }

        // 해당 게시물을 이번 세션에서 조회한 적이 없으면 조회수 증가
        if (!viewedPosts.contains(postNo)) {
            boardService.increaseViewCount(postNo);
            viewedPosts.add(postNo);
        }

        session.setAttribute("viewedPosts", viewedPosts);
        Optional<PostDto> postDto = boardService.getPost(postNo);
        if (postDto.isEmpty()) {
            response.sendRedirect("/error");
        }
        model.addAttribute("board", postDto.get());
        return "/board/post";
    }

    /**
     * 게시글 등록
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @ResponseBody
    @PostMapping("/board/write/post")
    public ResponseEntity post(@RequestBody Board board, Principal principal) {
//        return ApiResponse.success(boardService.saveMbrBoard(board, principal));
        // img Base64 버전
        return ApiResponse.success(boardService.saveBoard(board, principal));
    }

    /**
     * 게시글 편집 페이지
     */
    @GetMapping("/board/edit/post/{postNo}")
    public String edit(Model model, @PathVariable Long postNo) {
        model.addAttribute("board", boardService.getPost(postNo));
        return "/board/edit";
    }
}
