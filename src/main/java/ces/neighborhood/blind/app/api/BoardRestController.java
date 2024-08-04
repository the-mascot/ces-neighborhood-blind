package ces.neighborhood.blind.app.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ces.neighborhood.blind.app.dto.ApiResponse;
import ces.neighborhood.blind.app.dto.LikeDto;
import ces.neighborhood.blind.app.service.BoardService;
import ces.neighborhood.blind.common.code.Constant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(Constant.BASE_API_URL + "/board")
@RequiredArgsConstructor
public class BoardRestController {

    private final BoardService boardService;

    /**
     * 게시판 메인
     */
    @GetMapping("/main")
    public ResponseEntity getBoardMain() {
        return ApiResponse.success(boardService.getBoardList());
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
                                       @RequestPart(value = "image", required = false) MultipartFile image) {
        boardService.writeComment(postNo, content, image);
        return ApiResponse.success();
    }

}
