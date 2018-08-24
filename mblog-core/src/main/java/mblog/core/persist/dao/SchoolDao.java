package mblog.core.persist.dao;

import mblog.core.persist.entity.SchoolPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SchoolDao extends JpaRepository<SchoolPO, Long>, JpaSpecificationExecutor<SchoolPO> {
    SchoolPO findByName(String name);

    @Override
    List<SchoolPO> findAll();
}
