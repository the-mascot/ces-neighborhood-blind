package ces.neighborhood.blind.app.repository;

import ces.neighborhood.blind.app.entity.MbrInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MbrInfo, String> {
}
