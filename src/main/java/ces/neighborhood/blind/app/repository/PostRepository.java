package ces.neighborhood.blind.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ces.neighborhood.blind.app.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostDslRepository {

}
