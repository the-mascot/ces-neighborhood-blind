package ces.neighborhood.blind.app.repository;

import ces.neighborhood.blind.app.record.board.PostRes;
import ces.neighborhood.blind.app.record.board.Posts;
import java.util.List;

public interface PostDslRepository {

    PostRes findPostById(Long postNo);

    List<Posts> findAllPostsDto(String mbrId);

}
