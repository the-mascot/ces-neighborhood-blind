package ces.neighborhood.blind.app.repository;

import ces.neighborhood.blind.app.dto.BoardDto;
import java.util.List;

public interface BoardDslRepository {

    List<BoardDto> findAllByPostNoAndMbrId(String mbrId);

}
