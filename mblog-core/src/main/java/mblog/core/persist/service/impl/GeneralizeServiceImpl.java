package mblog.core.persist.service.impl;

import mblog.base.lang.Common;
import mblog.base.lang.EnumTaskStatus;
import mblog.core.data.GeneralizeTask;
import mblog.core.data.Resume;
import mblog.core.persist.dao.GeneralizeTaskDao;
import mblog.core.persist.entity.GeneralizeTaskPO;
import mblog.core.persist.service.GeneralizeService;
import mblog.core.persist.utils.BeanMapUtils;
import mblog.core.task.Bitfish;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GeneralizeServiceImpl implements GeneralizeService {

    @Autowired
    private GeneralizeTaskDao generalizeTaskDao;

    @Override
    public void addTask(GeneralizeTask task) {
        GeneralizeTaskPO po = new GeneralizeTaskPO();

        BeanUtils.copyProperties(task, po);
        po.setStatus(EnumTaskStatus.STOPED.getName());
        generalizeTaskDao.save(po);
    }

    @Override
    public Page<GeneralizeTask> list(Pageable pageable) {
        Page<GeneralizeTaskPO> page = generalizeTaskDao.findAll(pageable);
        List<GeneralizeTask> rets = new ArrayList<>();

        page.getContent().forEach(po -> {
            GeneralizeTask ret = BeanMapUtils.copy(po);
            rets.add(ret);
        });

        return new PageImpl<>(rets, pageable, page.getTotalElements());
    }

    @Override
    public void delete(List<Long> ids) {

    }

    @Override
    public void deleteResume(Long id) {

    }

    @Override
    public Page<Resume> getResumeList(Pageable pageable) {
        return null;
    }

    @Override
    public String startTaskById(Long id) {
        GeneralizeTaskPO taskPO = generalizeTaskDao.findById(id);
        if(taskPO == null){
            return "任务不存在";
        }
        if(EnumTaskStatus.RUNNING.getName().equals(taskPO.getStatus())){
            return "任务已经在运行中";
        }
        return startTask(taskPO);
    }

    private String startTask(GeneralizeTaskPO taskPO){
        Timer timer = new Timer();
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTime(new Date());
        int currentHour = currentTime.get(Calendar.HOUR);
        currentTime.set(Calendar.HOUR, currentHour);
        currentTime.set(Calendar.MINUTE, (int) (Math.random() * 5) + 5);
        currentTime.set(Calendar.SECOND, 0);
        currentTime.set(Calendar.MILLISECOND, 0);
        Date NextHour = currentTime.getTime();
        timer.scheduleAtFixedRate(new Bitfish(taskPO), NextHour, 1000 * 60 * 60 * 24 * 365);
        Common.taskList.put(taskPO.getId(), timer);
        taskPO.setStatus(EnumTaskStatus.RUNNING.getName());
        generalizeTaskDao.save(taskPO);
        return "操作成功";
    }

    @Override
    public String stopTask(Long id) {
        return null;
    }

    @Override
    public void initTask() {

    }
}
