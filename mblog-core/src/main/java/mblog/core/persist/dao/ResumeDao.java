package mblog.core.persist.dao;

import mblog.core.persist.entity.ResumePO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;

public interface ResumeDao extends JpaRepository<ResumePO, Long>, JpaSpecificationExecutor<ResumePO> {
    @Override
    Page<ResumePO> findAll(Pageable pageable);

    ResumePO findById(long id);

    int deleteAllByIdIn(Collection<Long> ids);
}
