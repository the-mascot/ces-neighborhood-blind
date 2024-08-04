package ces.neighborhood.blind.app.service;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ces.neighborhood.blind.app.dto.BoardDto;
import ces.neighborhood.blind.app.dto.LikeDto;
import ces.neighborhood.blind.app.dto.PostDto;
import ces.neighborhood.blind.app.entity.Attachment;
import ces.neighborhood.blind.app.entity.Board;
import ces.neighborhood.blind.app.entity.Comment;
import ces.neighborhood.blind.app.entity.Likes;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.repository.AttachmentRepository;
import ces.neighborhood.blind.app.repository.BoardRepository;
import ces.neighborhood.blind.app.repository.CommentRepository;
import ces.neighborhood.blind.app.repository.LikesRepository;
import ces.neighborhood.blind.common.code.Constant;
import ces.neighborhood.blind.common.utils.ComUtils;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 블라인드 게시판 관련 Service
 * </pre>
 *
 * @version 1.0
 * @author mascot
 * @since 2023.12.01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final AttachmentRepository attachmentRepository;

    private final LikesRepository likesRepository;

    private final CommentRepository commentRepository;

    private final S3Service s3Service;

    public List<Board> getBoardList() {
        return boardRepository.findAll();
    }

    /**
     * 게시판 목록 가져오기
     * @param boardType
     * @return List<BoardDto>
     * @throws
     */
    public List<BoardDto> getBoardList(String boardType) {
        Authentication authentication = ComUtils.getAuthentication();
        List<BoardDto> boardList;
        // TODO: 게시판 타입 결정 후 코드 변경 요
        if (StringUtils.equals(boardType, Constant.BOARD_TYPE_ALL) || StringUtils.equals(boardType, Constant.NULL.toLowerCase())) {
            boardList = boardRepository.getBoardList(authentication.getName());
        } else {
            boardList = boardRepository.getBoardList(boardType, authentication.getName());
        }
        boardList.forEach(boardDto -> {
            // 본문 미리보기를 위해 HTML -> 평문으로 변환
            boardDto.setContent(Jsoup.parse(boardDto.getContent()).text());
        });
        return boardList;
    }

    /**
     * 게시글 저장 (이미지 파일 없는 버전)
     * @param board, principal
     * @return postNo
     * @throws
     */
    public long saveBoardWithoutFile(Board board, Principal principal) {
        board.setMbrInfo(MbrInfo.builder().mbrId(principal.getName()).build());
        board.setCreateUser(principal.getName());
        board.setDelYn(Constant.N);
        return boardRepository.save(board).getPostNo();
    }

    /**
     * 게시글 저장 (이미지 파일 refNo 업데이트)
     *
     * 이미지 업로드 서비스 프로세스
     * 에디터 이미지 업로드 즉시 : S3 파일 저장 -> DB Attachment 저장
     * 게시글 등록 후 : 게시글 저장 -> 본문에 img 태그 불러와서 최종 저장된 img만 Attachment 참조번호(refNo) 업데이트
     * -> S3에 업로드 되었으나, 최종 저장하지 않은(refNo가 없는) 파일은 batch로 일정 기간 후 삭제 예정.
     *
     * @param board, principal
     * @return postNo
     * @throws
     */
    public long saveBoard(Board board, Principal principal) {
        board.setMbrInfo(MbrInfo.builder().mbrId(principal.getName()).build());
        board.setCreateUser(principal.getName());
        board.setDelYn(Constant.N);
        // 게시글 저장
        Long postNo = boardRepository.save(board).getPostNo();

        // 첨부 이미지 refNo 업데이트
        Document doc = Jsoup.parse(board.getContent());
        Elements imgTags = doc.select("img");
        for (Element imgTag : imgTags) {
            String src =  imgTag.attr("src");
            String[] parts = src.split("/");
            String storedFileName = parts[parts.length - 1];
           Attachment attachment = attachmentRepository.findByStoredFileName(storedFileName);
            if (attachment != null) {
                attachment.setRefNo(postNo);
                attachment.setModifyUser(principal.getName());
                attachment.setRefType(Constant.BOARD);
                attachmentRepository.save(attachment);
            }
        }
        return postNo;
    }

    /**
     * 게시글 상세 가져오기
     * @param postNo
     * @return PostDto
     * @throws
     */
    public Optional<PostDto> getPost(Long postNo) {
        Authentication authentication = ComUtils.getAuthentication();
        return boardRepository.getPost(postNo, authentication.getName());
    }

    /**
     * 게시글 조회수 증가
     * @param postNo
     * @return
     * @throws
     */
    @Transactional
    public void increaseViewCount(Long postNo) {
        boardRepository.updateViewCount(postNo);
    }

    /**
     * 게시글 좋아요 기능
     * @param likeDto
     * @return LikeDto(좋아요 여부, 좋아요 개수)
     * @throws
     */
    public LikeDto like(LikeDto likeDto) {
        Authentication authentication = ComUtils.getAuthentication();
        Likes.LikesId likesId = Likes.LikesId.builder()
                .postType(likeDto.getPostType())
                .postNo(likeDto.getPostNo())
                .mbrId(authentication.getName())
                .build();
        // 해당 게시물에 좋아요를 한적 있는지 확인
        Optional<Likes> likes = likesRepository.findById(likesId);
        boolean isLiked = likes.isPresent();
        if (isLiked) {
            // 좋아요 상태면 좋아요 해제
            likesRepository.delete(likes.get());
        } else {
            // 좋아요 추가
            likesRepository.save(Likes.builder()
                    .likesId(likesId)
                    .build());
        }

        return LikeDto.builder()
                .isLiked(!isLiked)
                .likeCnt(likesRepository.getLikeCount(likeDto.getPostNo()))
                .build();
    }

    @Transactional
    public void writeComment(String postNo, String content, MultipartFile image) {
        Authentication authentication = ComUtils.getAuthentication();
        commentRepository.save(Comment.builder()
                .board(new Board(Long.valueOf(postNo)))
                .content(content)
                .build());
        if (!image.isEmpty()) {
            s3Service.uploadFileToS3(image, authentication);
        }
    }
}
