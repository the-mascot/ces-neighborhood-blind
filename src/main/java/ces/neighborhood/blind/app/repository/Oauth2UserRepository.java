package ces.neighborhood.blind.app.repository;

import ces.neighborhood.blind.app.entity.SnsMbrInfo;
import ces.neighborhood.blind.app.entity.SnsMbrInfoKey;

import org.springframework.data.jpa.repository.JpaRepository;

public interface Oauth2UserRepository extends JpaRepository<SnsMbrInfo, SnsMbrInfoKey> {
}
