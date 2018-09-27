package mblog.core.persist.service.impl;

import mblog.base.lang.Common;
import mblog.base.lang.EnumProject;
import mblog.base.lang.EnumTaskStatus;
import mblog.core.data.GeneralizeTask;
import mblog.core.data.Resume;
import mblog.core.persist.dao.GeneralizeListDao;
import mblog.core.persist.dao.GeneralizeTaskDao;
import mblog.core.persist.entity.GeneralizeTaskPO;
import mblog.core.persist.service.GeneralizeService;
import mblog.core.persist.utils.BeanMapUtils;
import mblog.core.task.BaoshixingqiuTask;
import mblog.core.task.BitfishTask;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GeneralizeServiceImpl implements GeneralizeService {

    @Autowired
    private GeneralizeTaskDao generalizeTaskDao;

    @Autowired
    private GeneralizeListDao generalizeListDao;

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
            ret.setStatus(EnumTaskStatus.valueOf(ret.getStatus()).getValue());
            rets.add(ret);
        });

        return new PageImpl<>(rets, pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    @CacheEvict(value = "commentsCaches", allEntries = true)
    public void delete(List<Long> ids) {
        generalizeTaskDao.deleteAllByIdIn(ids);
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
        Thread thread = null;
        if(taskPO.getProject().equals(EnumProject.BITFISH.getValue())) {
            thread = new BitfishTask(taskPO, generalizeTaskDao, generalizeListDao);
        }
        else if(taskPO.getProject().equals(EnumProject.BAOSHI_XINGQIU.getValue())){
            thread = new BaoshixingqiuTask(taskPO, generalizeTaskDao, generalizeListDao);
        }
        else{
            return "项目不存在";
        }
        Common.threadList.put(Common.GENERALIZE_THREAD+taskPO.getId(), thread);
        Common.cachedThreadPool.execute(thread);
        taskPO.setStatus(EnumTaskStatus.RUNNING.getName());
        taskPO.setSuccessCount(0);
        taskPO.setFailedCount(0);
        generalizeTaskDao.save(taskPO);
        return "操作成功";
    }

    @Override
    public String stopTask(Long id) {
        GeneralizeTaskPO taskPO = generalizeTaskDao.findById(id);
        if(taskPO == null){
            return "任务不存在";
        }
        if(EnumTaskStatus.STOPED.getName().equals(taskPO.getStatus())){
            return "任务已经停止";
        }
        Thread thread = Common.threadList.get(Common.GENERALIZE_THREAD+id);
        if(thread != null){
            Common.threadList.remove(Common.GENERALIZE_THREAD+id);
        }
        taskPO.setStatus(EnumTaskStatus.STOPED.getName());
        generalizeTaskDao.save(taskPO);
        return "操作成功";
    }

    @Override
    public void initTask() {

    }
}
