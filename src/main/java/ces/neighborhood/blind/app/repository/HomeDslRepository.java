package ces.neighborhood.blind.app.repository;

import ces.neighborhood.blind.app.entity.Menu;
import java.util.List;

public interface HomeDslRepository {

    List<Menu> findAllMenuWithHierarchy();

}
