package ces.neighborhood.blind.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
    @ResponseBody
    @PostMapping("/board/like")
    public ResponseEntity like(@Valid @RequestBody LikeDto likeDto) {
        return ApiResponse.success(boardService.like(likeDto));
    }

}
