package mblog.web.controller.admin;

import mblog.base.data.Data;
import mblog.core.data.Comment;
import mblog.core.data.Config;
import mblog.core.data.Resume;
import mblog.core.data.SearchTask;
import mblog.core.persist.service.CommentService;
import mblog.core.persist.service.ConfigService;
import mblog.core.persist.service.EmploymentService;
import mblog.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/employment")
public class EmploymentController extends BaseController {

    @Autowired
    private ConfigService configService;
    @Autowired
    private EmploymentService employmentService;

    @RequestMapping("/resumelist")
    public String list(ModelMap model) {
        Map<String, Config> configs = configService.findAll2Map();
        model.put("configs", configs);
        //任务列表
//        Pageable pageable = wrapPageable();
//        Page<SearchTask> page = employmentService.list(pageable);
//        model.put("page", page);
        //简历列表
        Pageable resumePage = wrapPageableDesc("lockDate");
        Page<Resume> pageResume = employmentService.getResumeList(resumePage);
        model.put("resumes", pageResume);
        return "/admin/employment/resumelist";
    }

    @RequestMapping("/tasklist")
    public String tasklist(ModelMap model) {
        Map<String, Config> configs = configService.findAll2Map();
        model.put("configs", configs);
        //任务列表
        Pageable pageable = wrapPageable();
        Page<SearchTask> page = employmentService.list(pageable);
        model.put("page", page);
        return "/admin/employment/tasklist";
    }

    @RequestMapping("/add_task")
    public String addTask(SearchTask post, ModelMap model) {
        employmentService.addTask(post);
        Pageable pageable = wrapPageable();
        Page<SearchTask> page = employmentService.list(pageable);
        model.put("page", page);

        Map<String, Config> configs = configService.findAll2Map();
        model.put("configs", configs);

        return "/admin/employment/tasklist";
    }

    @RequestMapping("/update_config")
    public String update(HttpServletRequest request, ModelMap model) {
        Map<String, String[]> params = request.getParameterMap();

        List<Config> configs = new ArrayList<>();

        params.forEach((k, v) -> {
            Config conf = new Config();
            conf.setKey(k);
            conf.setValue(v[0]);

            configs.add(conf);
        });
        configService.update(configs);
        return "redirect:/admin/employment/";
    }

    @RequestMapping("/start_task")
    public @ResponseBody
    Data startTask(@RequestParam("id") Long id) {
        Data data = Data.failure("操作失败");
        if (id != null) {
            try {
                String result = employmentService.startTaskById(id);
                data = Data.success(result, Data.NOOP);
            } catch (Exception e) {
                data = Data.failure(e.getMessage());
            }
        }
        return data;
    }

    @RequestMapping("/stop_task")
    public @ResponseBody
    Data stopTask(@RequestParam("id") Long id) {
        Data data = Data.failure("操作失败");
        if (id != null) {
            try {
                String result = employmentService.stopTask(id);
                data = Data.success(result, Data.NOOP);
            } catch (Exception e) {
                data = Data.failure(e.getMessage());
            }
        }
        return data;
    }

    @RequestMapping("/delete_task")
    public @ResponseBody
    Data delete(@RequestParam("id") List<Long> id) {
        Data data = Data.failure("操作失败");
        if (id != null) {
            try {
                employmentService.delete(id);
                data = Data.success("操作成功", Data.NOOP);
            } catch (Exception e) {
                data = Data.failure(e.getMessage());
            }
        }
        return data;
    }

    @RequestMapping("/delete_resume")
    public @ResponseBody
    Data deleteResume(@RequestParam("id")Long id) {
        Data data = Data.failure("操作失败");
        if (id != null) {
            try {
                employmentService.deleteResume(id);
                data = Data.success("操作成功", Data.NOOP);
            } catch (Exception e) {
                data = Data.failure(e.getMessage());
            }
        }
        return data;
    }

    @RequestMapping("/batch_del")
    public @ResponseBody
    Data batchDel(@RequestParam("id") List<Long> id) {
        Data data = Data.failure("操作失败");
        if (id != null) {
            try {
                employmentService.delete(id);
                data = Data.success("操作成功", Data.NOOP);
            } catch (Exception e) {
                data = Data.failure(e.getMessage());
            }
        }
        return data;
    }

    @RequestMapping("/resume")
    public String resume(ModelMap model) {
        Pageable pageable = wrapPageable();
        Page<Resume> page = employmentService.getResumeList(pageable);
        model.put("resumes", page);
        return "/admin/employment/main";
    }

}
