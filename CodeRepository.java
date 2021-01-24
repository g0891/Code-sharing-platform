package platform;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface CodeRepository extends CrudRepository<Code, Long> {
    ArrayList<Code> findTop10ByOrderByDateDesc();
    ArrayList<Code> findTop10ByTimeLessThanEqualAndViewsLessThanEqualOrderByDateDesc(long time, int views);
    public Optional<Code> findByUuid(String uuid);
}
