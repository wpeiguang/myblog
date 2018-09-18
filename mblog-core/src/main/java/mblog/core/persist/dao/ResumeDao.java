package mblog.core.persist.dao;

import mblog.core.persist.entity.ResumePO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.web.PageableDefault;

import java.util.Collection;

public interface ResumeDao extends JpaRepository<ResumePO, Long>, JpaSpecificationExecutor<ResumePO> {
    @Override
    Page<ResumePO> findAll(@PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable);

    ResumePO findById(long id);

    ResumePO findByMobile(String mobile);

    ResumePO findByResumeId(String resumeId);

    int deleteAllByIdIn(Collection<Long> ids);

    @Override
    void delete(Long aLong);
}
