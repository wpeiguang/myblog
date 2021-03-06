package mblog.core.persist.service;

import mblog.core.data.GeneralizeTask;
import mblog.core.data.Resume;
import mblog.core.data.SearchTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GeneralizeService {

    void addTask(GeneralizeTask task);

    Page<GeneralizeTask> list(Pageable pageable);

    void delete(List<Long> ids);

    Page<Resume> getResumeList(Pageable pageable);

    String startTaskById(Long id);

    String stopTask(Long id);

    void initTask();
}
