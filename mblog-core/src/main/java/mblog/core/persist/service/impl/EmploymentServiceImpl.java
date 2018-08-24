package mblog.core.persist.service.impl;

import mblog.base.context.AppContext;
import mblog.base.lang.Common;
import mblog.base.lang.EnumTaskStatus;
import mblog.core.data.Resume;
import mblog.core.data.SearchTask;
import mblog.core.persist.dao.ResumeDao;
import mblog.core.persist.dao.SchoolDao;
import mblog.core.persist.dao.SearchTaskDao;
import mblog.core.persist.entity.ResumePO;
import mblog.core.persist.entity.SchoolPO;
import mblog.core.persist.entity.SearchTaskPO;
import mblog.core.persist.service.ConfigService;
import mblog.core.persist.service.EmploymentService;
import mblog.core.persist.utils.BeanMapUtils;
import mblog.core.task.ResumeTask;
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
public class EmploymentServiceImpl implements EmploymentService {
    @Autowired
    private SearchTaskDao searchTaskDao;
    @Autowired
    private ResumeDao resumeDao;
    @Autowired
    private AppContext appContext;
    @Autowired
    private SchoolDao schoolDao;
    @Autowired
    private ConfigService configService;

    @Override
    public void addTask(SearchTask task) {
        SearchTaskPO po = new SearchTaskPO();

        BeanUtils.copyProperties(task, po);
        po.setStatus(EnumTaskStatus.STOPED.getName());
        searchTaskDao.save(po);
//        return BeanMapUtils.copy(po);
    }

    @Override
    public Page<SearchTask> list(Pageable pageable) {
        Page<SearchTaskPO> page = searchTaskDao.findAll(pageable);
        List<SearchTask> rets = new ArrayList<>();

        page.getContent().forEach(po -> {
            SearchTask ret = BeanMapUtils.copy(po);
            ret.setStatus(EnumTaskStatus.valueOf(ret.getStatus()).getValue());
            rets.add(ret);
        });

        return new PageImpl<>(rets, pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    @CacheEvict(value = "commentsCaches", allEntries = true)
    public void delete(List<Long> ids) {
        searchTaskDao.deleteAllByIdIn(ids);
    }

    @Override
    public void deleteResume(List<Long> ids) {
        resumeDao.deleteAllByIdIn(ids);
    }

    @Override
    public String startTaskById(Long id){
        SearchTaskPO taskPO = searchTaskDao.findById(id);
        if(taskPO == null){
            return "任务不存在";
        }
        if(EnumTaskStatus.RUNNING.getName().equals(taskPO.getStatus())){
            return "任务已经在运行中";
        }
        return startTask(taskPO);
    }

    private String startTask(SearchTaskPO taskPO){
        Timer timer = new Timer();
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTime(new Date());
        int currentHour = currentTime.get(Calendar.HOUR);
        currentTime.set(Calendar.HOUR, currentHour);
        currentTime.set(Calendar.MINUTE, (int) (Math.random() * 5) + 5);
        currentTime.set(Calendar.SECOND, 0);
        currentTime.set(Calendar.MILLISECOND, 0);
        Date NextHour = currentTime.getTime();
        System.out.println(NextHour);
        timer.scheduleAtFixedRate(new ResumeTask(taskPO, appContext.getConfig(), resumeDao, configService), NextHour, 1000 * 60 * 60);
        Common.taskList.put(taskPO.getId(), timer);
        taskPO.setStatus(EnumTaskStatus.RUNNING.getName());
        searchTaskDao.save(taskPO);
        return "操作成功";
    }

    @Override
    public String stopTask(Long id){
        SearchTaskPO taskPO = searchTaskDao.findById(id);
        if(taskPO == null){
            return "任务不存在";
        }
        if(EnumTaskStatus.STOPED.getName().equals(taskPO.getStatus())){
            return "任务已经停止";
        }
        Timer timer = Common.taskList.get(id);
        if(timer != null){
            timer.cancel();
        }
        taskPO.setStatus(EnumTaskStatus.STOPED.getName());
        searchTaskDao.save(taskPO);
        return "操作成功";
    }

    @Override
    public void initTask() {
        List<SchoolPO> schools = schoolDao.findAll();
        schools.forEach(po -> {
            Common.schools.put(po.getName(), po.getDegree());
        });
        List<SearchTaskPO> taskList = searchTaskDao.findAll();
        if(taskList != null){
            for(SearchTaskPO task : taskList){
                if(EnumTaskStatus.RUNNING.getName().equals(task.getStatus())){
                    startTask(task);
                }
            }
        }
    }

    @Override
    public Page<Resume> getResumeList(Pageable pageable) {
        Page<ResumePO> page = resumeDao.findAll(pageable);
        List<Resume> rets = new ArrayList<>();

        page.getContent().forEach(po -> {
            Resume ret = BeanMapUtils.copy(po);
            rets.add(ret);
        });

        return new PageImpl<>(rets, pageable, page.getTotalElements());
    }
}
