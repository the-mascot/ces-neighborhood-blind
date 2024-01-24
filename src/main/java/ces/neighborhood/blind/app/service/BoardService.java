package ces.neighborhood.blind.app.service;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import ces.neighborhood.blind.app.dto.BoardDto;
import ces.neighborhood.blind.app.dto.PostDto;
import ces.neighborhood.blind.app.entity.Attachment;
import ces.neighborhood.blind.app.entity.Board;
import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.repository.AttachmentRepository;
import ces.neighborhood.blind.app.repository.BoardRepository;
import ces.neighborhood.blind.common.code.Constant;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final S3Service s3Service;

    private final AttachmentRepository attachmentRepository;

    public List<BoardDto> getBoardList(Model model) {
        String boardType = String.valueOf(model.getAttribute("boardType"));
        List<BoardDto> boardList;
        if (StringUtils.equals(boardType, Constant.BOARD_TYPE_ALL) || StringUtils.equals(boardType, Constant.NULL.toLowerCase())) {
            boardList = boardRepository.getBoardList();
        } else {
            boardList = boardRepository.getBoardList(boardType);
        }
        boardList.forEach(boardDto -> {
            boardDto.setContent(Jsoup.parse(boardDto.getContent()).text());
        });
        return boardList;
    }

    public long saveMbrBoard(Board board, Principal principal) {
        board.setMbrInfo(MbrInfo.builder().mbrId(principal.getName()).build());
        board.setCreateUser(principal.getName());
        board.setDelYn(Constant.N);
        return boardRepository.save(board).getPostNo();
    }

    public long saveBoard(Board board, Principal principal) {
        board.setMbrInfo(MbrInfo.builder().mbrId(principal.getName()).build());
        board.setCreateUser(principal.getName());
        board.setDelYn(Constant.N);
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

    public PostDto getPost(Long postNo) {
        return boardRepository.getPost(postNo).get();
    }
}
