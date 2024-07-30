package ces.neighborhood.blind.app.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import ces.neighborhood.blind.app.entity.Menu;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HomeRepositoryImpl implements HomeDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Menu> findAllMenuWithHierarchy() {
        return null;
    }
}
