package ces.neighborhood.blind.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ces.neighborhood.blind.app.entity.Likes;
import java.util.List;

public interface LikesRepository extends JpaRepository<Likes, Likes.LikesId> {

    @Query("select count(l.likesId.postNo) from Likes l where l.likesId.postNo = :postNo ")
    Integer getLikeCount(@Param("postNo") Long postNo);

    List<Likes> findAllByLikesId_PostNo(Long postNo);

}
