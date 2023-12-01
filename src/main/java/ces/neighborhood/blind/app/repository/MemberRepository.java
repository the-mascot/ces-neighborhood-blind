package ces.neighborhood.blind.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ces.neighborhood.blind.app.entity.MbrInfo;

public interface MemberRepository extends JpaRepository<MbrInfo, String> {
}
