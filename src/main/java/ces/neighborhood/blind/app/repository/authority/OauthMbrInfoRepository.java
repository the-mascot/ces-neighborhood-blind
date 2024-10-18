package ces.neighborhood.blind.app.repository.authority;

import org.springframework.data.jpa.repository.JpaRepository;

import ces.neighborhood.blind.app.entity.OauthMbrInfo;

public interface OauthMbrInfoRepository extends JpaRepository<OauthMbrInfo, OauthMbrInfo.OauthMbrInfoKey> {
}
