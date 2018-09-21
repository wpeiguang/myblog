package mblog.core.persist.dao;

import mblog.core.persist.entity.GeneralizeTaskPO;
import mblog.core.persist.entity.SearchTaskPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface GeneralizeTaskDao extends JpaRepository<GeneralizeTaskPO, Long>, JpaSpecificationExecutor<GeneralizeTaskPO> {

    @Override
    Page<GeneralizeTaskPO> findAll(Pageable pageable);

    @Override
    List<GeneralizeTaskPO> findAll();

    int deleteAllByIdIn(Collection<Long> ids);

    GeneralizeTaskPO findById(Long id);

}
