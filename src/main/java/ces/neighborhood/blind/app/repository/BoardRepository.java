package ces.neighborhood.blind.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ces.neighborhood.blind.app.entity.MbrBoard;
import ces.neighborhood.blind.app.entity.MbrInfo;
import java.util.List;

public interface BoardRepository extends JpaRepository<MbrBoard, Long> {

    List<MbrBoard> findByBoardType(String boardType);

}
