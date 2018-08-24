package mblog.core.persist.dao;

import mblog.core.persist.entity.SearchTaskPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface SearchTaskDao extends JpaRepository<SearchTaskPO, Long>, JpaSpecificationExecutor<SearchTaskPO> {
    SearchTaskDao findByJobKey(String jobKey);

    Page<SearchTaskDao> findAllByOrderByIdDesc(Pageable pageable);

    List<SearchTaskDao> findAllByIdIn(Set<Long> ids);

    @Override
    Page<SearchTaskPO> findAll(Pageable pageable);

    @Override
    List<SearchTaskPO> findAll();

    int deleteAllByIdIn(Collection<Long> ids);

    SearchTaskPO findById(Long id);

}

