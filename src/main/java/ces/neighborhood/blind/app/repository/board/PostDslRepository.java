package ces.neighborhood.blind.app.repository.board;

import ces.neighborhood.blind.app.record.board.PostRes;
import ces.neighborhood.blind.app.record.board.PostsRes;
import java.util.List;

public interface PostDslRepository {

    PostRes findPostById(Long postNo, String mbrId);

    List<PostsRes> findAllPostsDto(String mbrId);

}
