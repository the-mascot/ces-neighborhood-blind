package ces.neighborhood.blind.app.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;

import ces.neighborhood.blind.app.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {


}
