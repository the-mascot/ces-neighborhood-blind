package ces.neighborhood.blind.app.service.board;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ces.neighborhood.blind.app.dto.PostDto;
import ces.neighborhood.blind.app.entity.Attachment;
import ces.neighborhood.blind.app.entity.Comment;
import ces.neighborhood.blind.app.entity.Likes;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.entity.Post;
import ces.neighborhood.blind.app.record.board.PostLikeReq;
import ces.neighborhood.blind.app.record.board.Posts;
import ces.neighborhood.blind.app.repository.AttachmentRepository;
import ces.neighborhood.blind.app.repository.CommentRepository;
import ces.neighborhood.blind.app.repository.LikesRepository;
import ces.neighborhood.blind.app.repository.PostRepository;
import ces.neighborhood.blind.app.service.file.S3Service;
import ces.neighborhood.blind.common.code.Constant;
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

    private final PostRepository postRepository;

    private final AttachmentRepository attachmentRepository;

    private final LikesRepository likesRepository;

    private final CommentRepository commentRepository;

    private final S3Service s3Service;

    /**
     * 게시판 목록 가져오기
     * @param
     * @return List<BoardDto>
     * @throws
     */
    public List<Posts> getPosts() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        List<Posts> posts = postRepository.findAllPostsDto(authentication.getName());

        return posts;
    }

    /**
     * 게시글 저장 (이미지 파일 없는 버전)
     * @param post, principal
     * @return postNo
     * @throws
     */
    public long saveBoardWithoutFile(Post post, Principal principal) {
        post.setMbrInfo(MbrInfo.builder().mbrId(principal.getName()).build());
        post.setCreateUser(principal.getName());
        post.setDelYn(Constant.N);
        return postRepository.save(post).getPostNo();
    }

    /**
     * 게시글 저장 (이미지 파일 refNo 업데이트)
     *
     * 이미지 업로드 서비스 프로세스
     * 에디터 이미지 업로드 즉시 : S3 파일 저장 -> DB Attachment 저장
     * 게시글 등록 후 : 게시글 저장 -> 본문에 img 태그 불러와서 최종 저장된 img만 Attachment 참조번호(refNo) 업데이트
     * -> S3에 업로드 되었으나, 최종 저장하지 않은(refNo가 없는) 파일은 batch로 일정 기간 후 삭제 예정.
     *
     * @param post, principal
     * @return postNo
     * @throws
     */
    public long saveBoard(Post post, Principal principal) {
        post.setMbrInfo(MbrInfo.builder().mbrId(principal.getName()).build());
        post.setCreateUser(principal.getName());
        post.setDelYn(Constant.N);
        // 게시글 저장
        Long postNo = postRepository.save(post).getPostNo();

        // 첨부 이미지 refNo 업데이트
        Document doc = Jsoup.parse(post.getContent());
        Elements imgTags = doc.select("img");
        for (Element imgTag : imgTags) {
            String src =  imgTag.attr("src");
            String[] parts = src.split("/");
            String fileName = parts[parts.length - 1];
           Attachment attachment = attachmentRepository.findByFileName(fileName);
            if (attachment != null) {
                attachment.setRefNo(postNo);
                attachment.setModifyUser(principal.getName());
                attachment.setRefType(Constant.REF_TYPE_POST);
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
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        //return postRepository.getPost(postNo, authentication.getName());
        return null;
    }

    /**
     * 게시글 조회수 증가
     * @param postNo
     * @return
     * @throws
     */
    @Transactional
    public void increaseViewCount(Long postNo) {
        //postRepository.updateViewCount(postNo);
    }

    /**
     * 게시글 좋아요 기능
     * @param postLikeReq
     * @return
     * @throws
     */
    public void updatePostLike(PostLikeReq postLikeReq) {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        Likes.LikesId likesId = Likes.LikesId.builder()
                .postType(postLikeReq.postType())
                .postNo(postLikeReq.postNo())
                .mbrId(authentication.getName())
                .build();
        // 해당 게시물에 좋아요를 한적 있는지 확인
        Optional<Likes> likes = likesRepository.findById(likesId);
        if (likes.isPresent()) {
            // 좋아요 상태면 좋아요 해제
            likesRepository.delete(likes.get());
        } else {
            // 좋아요 추가
            likesRepository.save(Likes.builder()
                    .likesId(likesId)
                    .post(
                        Post.builder()
                        .postNo(postLikeReq.postNo())
                        .build()
                    )
                    .build());
        }
    }

    @Transactional
    public void writeComment(String postNo, String content, MultipartFile image) {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        commentRepository.save(Comment.builder()
                .post(new Post(Long.valueOf(postNo)))
                .content(content)
                .build());
        if (!image.isEmpty()) {
            s3Service.uploadFileToS3(image, authentication);
        }
    }
}
