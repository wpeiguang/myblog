package mblog.web.controller.admin;

import mblog.base.data.Data;
import mblog.core.data.GeneralizeTask;
import mblog.core.persist.service.GeneralizeService;
import mblog.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin/generalize")
public class GeneralizeController extends BaseController {

    @Autowired
    private GeneralizeService generalizeService;

    @RequestMapping("/list")
    public String list(ModelMap model) {
        Pageable pageable = wrapPageable();
        Page<GeneralizeTask> page = generalizeService.list(pageable);
        model.put("page", page);
        return "/admin/generalize/list";
    }

    @RequestMapping("/add_task")
    public String addTask(GeneralizeTask post, ModelMap model) {
        generalizeService.addTask(post);
        Pageable pageable = wrapPageable();
        Page<GeneralizeTask> page = generalizeService.list(pageable);
        model.put("page", page);

        return "/admin/generalize/list";
    }

    @RequestMapping("/start_task")
    public @ResponseBody
    Data startTask(@RequestParam("id") Long id) {
        Data data = Data.failure("操作失败");
        if (id != null) {
            try {
                String result = generalizeService.startTaskById(id);
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
                String result = generalizeService.stopTask(id);
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
                generalizeService.delete(id);
                data = Data.success("操作成功", Data.NOOP);
            } catch (Exception e) {
                data = Data.failure(e.getMessage());
            }
        }
        return data;
    }
}
