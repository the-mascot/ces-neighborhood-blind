package ces.neighborhood.blind.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ces.neighborhood.blind.app.entity.MbrInfo;
import ces.neighborhood.blind.app.entity.SnsMbrInfo;

public interface MemberRepository extends JpaRepository<MbrInfo, String> {
}
