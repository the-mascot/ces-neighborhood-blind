package ces.neighborhood.blind.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ces.neighborhood.blind.app.dto.ApiResponse;
import ces.neighborhood.blind.app.dto.LikeDto;
import ces.neighborhood.blind.app.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BoardRestController {

    private final BoardService boardService;

    /**
     * 게시글 좋아요
     */
    @PostMapping("/board/like")
    public ResponseEntity like(@Valid @RequestBody LikeDto likeDto) {
        return ApiResponse.success(boardService.like(likeDto));
    }

    /**
     * 댓글 등록
     */
    @PostMapping("/board/write/comment")
    public ResponseEntity writeComment(@RequestPart(value = "content", required = false) String content ,
                                       @RequestPart(value = "postNo", required = false) String postNo,
                                       @RequestPart(value = "image", required = false) MultipartFile image) {
        boardService.writeComment(postNo, content, image);
        return ApiResponse.success();
    }

}
