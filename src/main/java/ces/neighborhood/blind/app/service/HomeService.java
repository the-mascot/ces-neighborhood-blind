package ces.neighborhood.blind.app.service;

import org.springframework.stereotype.Service;

import ces.neighborhood.blind.app.entity.Menu;
import ces.neighborhood.blind.app.record.MenuRecord;
import ces.neighborhood.blind.app.repository.HomeDslRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeDslRepository homeDslRepository;

    /**
     * 계층구조 메뉴 가져오기
     * @param
     * @return
     * @throws
     */
    public List<MenuRecord> getMenu() {
        List<Menu> menus = homeDslRepository.findAllMenuWithHierarchy();
        return convertToMenuRecord(menus);
    }

    protected List<MenuRecord> convertToMenuRecord(List<Menu> menus) {
        return null;
    }

}
