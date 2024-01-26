package ces.neighborhood.blind.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ces.neighborhood.blind.app.entity.SnsMbrInfo;

public interface Oauth2UserRepository extends JpaRepository<SnsMbrInfo, SnsMbrInfo.SnsMbrInfoKey> {
}
