package mblog.web.controller.admin;

import mblog.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/generalize")
public class GeneralizeController extends BaseController {

    @RequestMapping("/task")
    public String task(ModelMap model) {
        return "/admin/generalize/task";
    }
}
