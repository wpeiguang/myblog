package mblog.core.persist.dao;

import mblog.core.persist.entity.GeneralizeListPO;
import mblog.core.persist.entity.GeneralizeTaskPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface GeneralizeListDao extends JpaRepository<GeneralizeListPO, Long>, JpaSpecificationExecutor<GeneralizeListPO> {

    @Override
    Page<GeneralizeListPO> findAll(Pageable pageable);

    @Override
    List<GeneralizeListPO> findAll();

    int deleteAllByIdIn(Collection<Long> ids);

    GeneralizeListPO findById(Long id);
}
