package ces.neighborhood.blind.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ces.neighborhood.blind.app.dto.ApiResponse;
import ces.neighborhood.blind.app.dto.LikeDto;
import ces.neighborhood.blind.app.entity.Post;
import ces.neighborhood.blind.app.service.board.BoardService;
import ces.neighborhood.blind.common.code.Constant;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(Constant.BASE_API_URL + "/board")
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시판 리스트
     */
    @GetMapping("/list")
    public ResponseEntity getBoardMain() {
        return ApiResponse.success(boardService.getBoardList());
    }

    /**
     * 게시글 등록
     */
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    @ResponseBody
    @PostMapping("/board/post")
    public ResponseEntity post(@RequestBody Post post, Principal principal) {
//        return ApiResponse.success(boardService.saveMbrBoard(board, principal));
        // img Base64 버전
        return ApiResponse.success(boardService.saveBoard(post, principal));
    }

    /**
     * 게시글 편집 페이지
     */
    @PutMapping("/board/post/{postNo}")
    public ResponseEntity edit(Model model, @PathVariable Long postNo) {
        model.addAttribute("board", boardService.getPost(postNo));
        return ApiResponse.success();
    }

    /**
     * 게시글 좋아요
     */
    @PutMapping("/like")
    public ResponseEntity like(@Valid @RequestBody LikeDto likeDto) {
        return ApiResponse.success(boardService.like(likeDto));
    }

    /**
     * 댓글 등록
     */
    @PostMapping("/comment")
    public ResponseEntity writeComment(@RequestPart(value = "content", required = false) String content ,
                                       @RequestPart(value = "postNo", required = false) String postNo,
                                       @RequestPart(value = "image", required = false)
                                       MultipartFile image) {
        boardService.writeComment(postNo, content, image);
        return ApiResponse.success();
    }
}
