package mblog.core.task;

import mblog.base.context.AppContext;
import mblog.base.lang.Common;
import mblog.base.lang.HttpRequest;
import mblog.base.lang.MtonsException;
import mblog.base.print.Printer;
import mblog.core.data.Config;
import mblog.core.persist.dao.ResumeDao;
import mblog.core.persist.entity.ResumePO;
import mblog.core.persist.entity.SearchTaskPO;
import mblog.core.persist.service.ConfigService;
import mblog.core.persist.service.impl.ConfigServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

public class ResumeTask extends TimerTask {

    private HttpRequest tpgHttp;

    private HttpRequest zhilianHttp;

    private SearchTaskPO searchTaskPO;

    private JavaMailSender javaMailSender;

    private ResumeDao resumeDao;

    private ConfigService configService;

    private String jobKey;

    private List<String> resumeIds = new ArrayList<>();

    private Map<String, String> config = new HashMap<>();

    private String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Safari/537.36 Maxthon/5.2.1.6000";

    public ResumeTask(SearchTaskPO searchTaskPO, Map<String, String> config, ResumeDao resumeDao, ConfigService configService, JavaMailSender javaMailSender){
        this.searchTaskPO = searchTaskPO;
//        this.config.putAll(config);
        this.resumeDao = resumeDao;
        this.configService = configService;
        jobKey = searchTaskPO.getJobKey();
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void run() {
        List<Config> configs = configService.findAll();
        configs.forEach(conf -> {
            config.put(conf.getKey(), conf.getValue());
        });
        if(!StringUtils.isEmpty(config.get("tpg_cookies"))) {
            tpgHttp = new HttpRequest(config.get("ip"), userAgent, null);
            tpgHttp.setCookie(config.get("tpg_cookies"));
        }
        Calendar curTime = Calendar.getInstance();
        curTime.setTime(new Date());
        int currentHour = curTime.get(Calendar.HOUR_OF_DAY);
        if(currentHour <= 16){
            if(!StringUtils.isEmpty(config.get("zhilian_cookies"))) {
                processZhilian();
            }
            if(!StringUtils.isEmpty(config.get("h51_cookies"))) {
                process51Job();
            }
        }
    }

    private void process51Job(){
        HttpRequest httpRequest = new HttpRequest(config.get("ip"), userAgent, null);
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTime(new Date());
        //搜索简历页面
        httpRequest.setCookie(config.get("h51_cookies"));
        String response = httpRequest.sendGet("https://ehire.51job.com/Candidate/SearchResumeIndexNew.aspx", "");
        Document doc = Jsoup.parse(response);
        Element body = doc.body();
        Element input = body.select("input").first();
        String viewState = input.attr("value");

        StringBuffer param = new StringBuffer();
        param.append("__VIEWSTATE=");
        param.append(viewState);
        param.append("&search_area_hid=");
        param.append("深圳|040000");
        param.append("&sex_ch=");
        param.append("99|不限");
        param.append("&sex_en=");
        param.append("99|Unlimited");
        param.append("&hidSearchEngineid=");
        param.append("&send_cycle=1");
        param.append("&send_time=7");
        param.append("&send_sum=10");
        param.append("&hidWhere=");
        param.append("&searchValueHid=" + jobKey);
        param.append("#040000#0###4#99#5#99#20#36##99############1#0##040000#0#0#0");
        param.append("&showGuide=");
        //搜索结果
//        System.out.println(new Date());
        response = httpRequest.sendPost("https://ehire.51job.com/Candidate/SearchResumeNew.aspx", param.toString());
        param.setLength(0);
        param.append("pageCode=3&pagerTopNew$ctl06=20");
        search(httpRequest, response, param);
    }

    private void search(HttpRequest httpRequest, String response, StringBuffer param){
        Document doc;
        Element body;
        String ids = "";
        doc = Jsoup.parse(response);
        body = doc.body();
        Elements inputs = body.select("input");
        for(Element element : inputs){
            String name = element.attr("name");
            if(name.equals("hidCheckUserIds")){
                ids = element.val();
            }
            if(!name.equals("") && !name.equals("hidUserID") && !name.equals("__VIEWSTATE") && !name.equals("__EVENTTARGET") && !name.equals("chkBox")){
                param.append("&");
                param.append(name);
                param.append("=");
                param.append(element.val());
            }
        }
        try {
            filter(httpRequest, param, ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filter(HttpRequest httpRequest, StringBuffer param, String ids) throws Exception {
        String response;
        Document doc;
        Element body;
        String[] idArr = ids.split(",");
        Map<String, String> map = new HashMap<>();
        int index = 0;
        int size = idArr.length;
        Printer.info(jobKey+"前程无忧刷新个数：" + size+", ids: "+ids);
        if(size <= 1){
            Printer.error("前程无忧cookie失效");
            List<Config> configs = new ArrayList<>();
            Config conf = new Config();
            conf.setKey("h51_cookies");
            conf.setValue("");
            configs.add(conf);
            configService.update(configs);
            return;
        }
        for(index = 0; index < size - 1; index++){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String id = idArr[index];
            boolean isAdd = true;
            String paramUser = "&hidUserID=" + id;

            if(resumeIds.contains(jobKey+id)){
                //已经搜索过
                continue;
            }
            String searchUrl = "https://ehire.51job.com/Candidate/ResumeView.aspx";
            response = httpRequest.sendPost(searchUrl, param.toString()+paramUser);
            if(response.equals("")){
                Printer.error("获取简历详情失败，id: " + id);
                continue;
            }
            doc = Jsoup.parse(response);
            body = doc.body();

            //手机号和标签
            String phone = "";
            String labelTitle = "";
            Elements tagTd = body.select("td[style=\"white-space:nowrap;\"]");
            if(tagTd.size() > 1) {
                phone = tagTd.get(1).text();
                labelTitle = tagTd.get(0).text();
            }else if(tagTd.size() > 0){
                phone = tagTd.get(0).text();
            }
            if(labelTitle.contains("外包")){
                resumeIds.add(jobKey+id);
//                System.out.println("简历标签不外包");
                continue;
            }
            //年龄和性别
            String age = "0";
            String sex = "0";
            Elements tds = body.select("td");
            for(int i = 0; i < tds.size(); i++){
                String string = tds.get(i).text();
                if(string.contains("岁")){
                    int indexAge = string.indexOf("岁");
                    age = string.substring(indexAge - 2, indexAge);
                    sex = string.substring(indexAge - 4, indexAge - 3);
                    break;
                }
            }
            if(age.equals("0")){
                if(tds.size() > 2 && "抱歉！该简历被求职者设为保密，暂不能查看！".equals(tds.get(2).text())){
                    Printer.error("该简历保密，id: "+ id);
                    resumeIds.add(jobKey+id);
                    continue;
                }
                Printer.error("51job简历查看需要人工验证码，id: "+ id);
                List<Config> configs = new ArrayList<>();
                Config conf = new Config();
                conf.setKey("verify");
                conf.setValue("1");
                configs.add(conf);
                configService.update(configs);
                return;
            }
            if(!searchTaskPO.getSex().contains(sex)){
//                System.out.println("这种性别不适合："+sex);
                resumeIds.add(jobKey+id);
                continue;
            }
            if(Integer.valueOf(age) > 35){
                resumeIds.add(jobKey+id);
//                System.out.println("太老了，年龄："+age);
                continue;
            }

//            System.out.println("phone: " + phone + ", id: "+id);
            Elements keys = body.select(".keys");
            for(int i = 0; i < keys.size(); i++){
                Element value = keys.get(i).nextElementSibling();
                map.put(keys.get(i).text(), value.text());
            }
            String graduateTime = "";
            Elements plates = body.select(".plate1");
            for(int i = 0; i < plates.size(); i++){
                Element key = plates.get(i);
                if(key.text().equals("教育经历")){
                    String eduTime = key.parent().nextElementSibling().select(".time").text();
                    graduateTime = eduTime.split("-|/")[2];
                    Calendar date = Calendar.getInstance();
                    String year = String.valueOf(date.get(Calendar.YEAR));
                    long workTime = Long.valueOf(year) - searchTaskPO.getWorkLimit();
                    if(graduateTime.contains("至今") || Long.valueOf(graduateTime) >= workTime){
                        //2017年后毕业
                        isAdd = false;
                        break;
                    }
                }
            }
            if(!isAdd) {
                resumeIds.add(jobKey+id);
                continue;
            }
            if(map.size() == 0){
                Printer.error("无法获取内容："+id);
                continue;
            }
            String name = doc.title();
            String job = map.get("职　位：").toLowerCase();
            String school = map.get("学　校：");
            String degree = map.get("学历/学位：");
            String salary = map.get("期望薪资：");
            String perJob = map.get("职能/职位：") != null ? map.get("职能/职位：").toLowerCase() : "";
            String company1 = map.get("所属公司：") != null ? map.get("所属公司：") : "";
            String company2 = map.get("公　司：") != null ? map.get("公　司：") : "";
            if(school.contains("专修")){
//                System.out.println("学校："+school);
                isAdd = false;
            }
            if(!searchTaskPO.getDegree().contains(degree)){
                isAdd = false;
            }
            if(company1.contains("中软") || company2.contains("中软") || company1.contains("软通") || company2.contains("软通")){
//                System.out.println("曾在中软任职过："+company1+"/"+company2);
                isAdd = false;
            }
            if(!job.contains(jobKey) && !perJob.contains(jobKey)){
//                System.out.println("职位不符："+job+"/"+perJob);
                isAdd = false;
            }
            String jobExclude = searchTaskPO.getExcludeKey();
            if(!StringUtils.isEmpty(jobExclude) && (job.toLowerCase().contains(jobExclude.toLowerCase()) || perJob.toLowerCase().contains(jobExclude.toLowerCase()))){
//                System.out.println("职位不符："+job+"/"+perJob);
                isAdd = false;
            }
            try {
                int lowSalary = Integer.valueOf(salary.split("-")[0]);
                if (lowSalary >= 25000 && lowSalary <= 50000) {
//                    System.out.println("要的钱太多："+salary);
                    isAdd = false;
                }else if(lowSalary > 30 && lowSalary < 50){
//                    System.out.println("要的钱太多："+salary);
                    isAdd = false;
                }
            }catch (Exception e){
//                System.out.println("期望薪资："+salary);
            }

            if(Common.schools.get(school) != null){
                if(!searchTaskPO.getDegree().contains(Common.schools.get(school))) {
//                    System.out.println("学历有点低哦：" + school);
                    isAdd = false;
                }
//                }else if((Common.schools.get(school).equals("专科")||degree.equals("大专")) && Integer.valueOf(graduateTime) > Common.workLimit){
//                    System.out.println("专科：" + graduateTime);
//                    isAdd = false;
//                }
            }else{
//                System.out.println("学校不存在："+school);
                isAdd = false;
            }

            if(isAdd) {
                if(phone.contains("**") || phone.equals("")){
                    resumeIds.add(jobKey+id);
                    //插入简历数据
                    ResumePO po = new ResumePO();
                    po.setLockDate(Calendar.getInstance().getTime());
                    po.setSource("51job");
                    po.setMobile(phone);
                    po.setName(name);
                    po.setJob(jobKey);
                    po.setResumeId(id);
                    resumeDao.save(po);
                    resumeIds.add(jobKey+id);
//                    System.out.println("手机号："+phone+", id: "+id);
                    continue;
                }
                String result = optTPG(phone, id, name);
                if(result.equals("")){
                    //插入简历数据
                    ResumePO po = new ResumePO();
                    po.setLockDate(Calendar.getInstance().getTime());
                    po.setSource("51job");
                    po.setMobile(phone);
                    po.setName(name);
                    po.setJob(jobKey);
                    po.setResumeId(id);
                    resumeDao.save(po);
                    resumeIds.add(jobKey+id);
                }
                continue;
            }else{
                resumeIds.add(jobKey+id);
            }
        }
    }

    private void processZhilian(){
        zhilianHttp = new HttpRequest(config.get("ip"), userAgent, config.get("zhilian_cookies"));
        try {
            //"S_GENDER":2,
            Format f = new SimpleDateFormat("yyyyMM");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.YEAR, (int) (0-searchTaskPO.getWorkLimit()));
            String workYears = f.format(c.getTime());
            String str = "{\"start\":0,\"rows\":100,\"S_DISCLOSURE_LEVEL\":2,\"S_EXCLUSIVE_COMPANY\":\"中软国际科技服务有限公司;华为群深圳招聘中心\",\"S_EDUCATION\":\"4,1\",\"S_DESIRED_CITY\":\"765\",\"S_CURRENT_CITY\":\"765\",\"S_ENGLISH_RESUME\":\"1\",\"isrepeat\":1,\"sort\":\"date\"}";
            JSONObject body = new JSONObject(str);
            body.put("S_KEYWORD", jobKey);
            body.put("S_WORK_YEARS", "191909,"+workYears);
            String response = zhilianHttp.sendHttpPost("https://rdapi.zhaopin.com/rd/search/resumeList?_="+System.currentTimeMillis(), body.toString());
            JSONObject result = new JSONObject(response);
            if(result.getInt("code") == 0){
                JSONObject data = result.getJSONObject("data");
                JSONArray dataList = data.getJSONArray("dataList");
                int size = dataList.length();
                Printer.info(jobKey+"智联刷新个数：" + size);
                for(int i = 0; i < size; i++){
                    JSONObject resume = dataList.getJSONObject(i);
                    String id = resume.getString("id");
                    String k = resume.getString("k");
                    String t = resume.getString("t");
                    if(resumeIds.contains(jobKey+id)){
                        continue;
                    }
                    String sex = resume.getString("gender");
                    if(!searchTaskPO.getSex().contains(sex)){
//                        System.out.println("这种性别不适合："+sex);
                        resumeIds.add(jobKey+id);
                        continue;
                    }
                    String desireCity = resume.getString("desireCity");
                    if(!desireCity.contains("深圳")){
//                        System.out.println("期望工作城市："+desireCity);
                        resumeIds.add(jobKey+id);
                        continue;
                    }
                    String jobTitle = resume.getString("jobTitle").toLowerCase();
                    String jobType = resume.getString("jobType").toLowerCase();
                    if(!jobTitle.contains(jobKey) && !jobType.contains(jobKey)){
                        //职位不符
                        resumeIds.add(jobKey+id);
//                        System.out.println("职位不符："+resume.getString("jobTitle") +"/"+resume.getString("jobType"));
                        continue;
                    }
                    String jobExclude = searchTaskPO.getExcludeKey();
                    if(!StringUtils.isEmpty(jobExclude) && (jobTitle.toLowerCase().contains(jobExclude.toLowerCase()) || jobType.toLowerCase().contains(jobExclude.toLowerCase()))){
                        //职位不符
                        resumeIds.add(jobKey+id);
//                        System.out.println("职位不符："+resume.getString("jobTitle") +"/"+resume.getString("jobType"));
                        continue;
                    }
                    JSONObject schoolDetail = resume.getJSONObject("schoolDetail");
                    String endDate = schoolDetail.getString("endDate").split("-")[0];
                    try {
                        Calendar date = Calendar.getInstance();
                        String year = String.valueOf(date.get(Calendar.YEAR));
                        long workTime = Long.valueOf(year) - searchTaskPO.getWorkLimit();
                        if(Integer.valueOf(endDate) > workTime){
                            //毕业年限不符
                            resumeIds.add(jobKey+id);
//                            System.out.println("毕业年限不符："+endDate);
                            continue;
                        }
                    }catch (Exception e){
//                        System.out.println("毕业时间："+endDate);
                        continue;
                    }

                    String desiredSalary = resume.getString("desiredSalary");
                    String lowSalary = desiredSalary.split("-")[0];
                    try {
                        if(Integer.valueOf(lowSalary) > 25000 && Integer.valueOf(lowSalary) <= 50000){
//                            System.out.println("要的钱太多："+desiredSalary);
                            resumeIds.add(jobKey+id);
                            continue;
                        }
                    }catch (Exception e){

                    }

                    String schoolName = schoolDetail.getString("schoolName");
                    String degree = Common.schools.get(schoolName);
                    if(degree == null){
//                        System.out.println("查不到该学校："+schoolName);
                        resumeIds.add(jobKey+id);
                        continue;
                    }
                    if(!searchTaskPO.getDegree().contains(degree)){
//                        System.out.println("学历太低："+schoolName);
                        resumeIds.add(jobKey+id);
                        continue;
                    }
//                    if(degree.equals("专科") && Integer.valueOf(endDate) > Common.workLimit){
//                        System.out.println("专科：" + endDate);
//                        jedis.set(jobKey+id, "resumeId");
//                        continue;
//                    }
                    if(resume.getInt("age") > 35){
                        //年龄不符
//                        System.out.println("太老了，年龄："+resume.getInt("age") );
                        resumeIds.add(jobKey+id);
                        continue;
                    }

                    StringBuffer param = new StringBuffer();
                    param.append("_=");
                    param.append(System.currentTimeMillis());
                    param.append("&resumeNo=");
                    param.append(id);
                    param.append("_1_1;");
                    param.append(k);
                    param.append(";");
                    param.append(t);

                    response = zhilianHttp.sendGet("https://rdapi.zhaopin.com/rd/resume/detail", param.toString());
                    if(response.contains("外包")){
//                        System.out.println("不考虑外包：" + id);
                        resumeIds.add(jobKey+id);
                        continue;
                    }
                    JSONObject personal = new JSONObject(response);
                    String userName = "";
                    if(personal.getInt("code") == 0){
                        JSONObject perData = personal.getJSONObject("data");
                        JSONObject candidate = perData.getJSONObject("candidate");
                        String phone = candidate.getString("mobilePhone");
                        userName = candidate.getString("userName");
                        if(phone.equals("")){
//                            System.out.println("号码为空：" + id);
                            resumeIds.add(jobKey+id);
                            continue;
                        }
                        JSONArray workExperience = perData.getJSONObject("detail").getJSONArray("WorkExperience");
                        for(int j = 0; j < workExperience.length(); j++){
                            JSONObject work = workExperience.getJSONObject(j);
                            String company = work.getString("CompanyName");
                            if(company.contains("中软") || company.contains("软通")){
//                                System.out.println("曾在中软就职："+work.getString("CompanyName"));
                                resumeIds.add(jobKey+id);
                                continue;
                            }
                        }
                        String desiredCityId = perData.getJSONObject("detail").getString("DesiredCity");
                        if(!desiredCityId.equals("765")){
//                            System.out.println("期望工作城市："+desireCity +" "+desiredCityId);
                            resumeIds.add(jobKey+id);
                            continue;
                        }
                        String optResult = optTPG(phone, id, userName);
                        if(optResult.equals("")){
                            //插入简历数据
                            ResumePO po = new ResumePO();
                            po.setLockDate(Calendar.getInstance().getTime());
                            po.setSource("zhilian");
                            po.setMobile(phone);
                            po.setName(userName);
                            po.setResumeId(id);
                            po.setJob(jobKey);
                            resumeDao.save(po);
                            resumeIds.add(jobKey+id);
                        }
                    }else{
                        Printer.error("获取详情失败："+response);
                    }
                }
            }else if(result.getInt("code") == 4){
//                System.out.println(response);
                Printer.error("智联招聘cookie失效");
                List<Config> configs = new ArrayList<>();
                Config conf = new Config();
                conf.setKey("zhilian_cookies");
                conf.setValue("");
                configs.add(conf);
                configService.update(configs);
                return;
            }else{
                Printer.error(jobKey + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(String resumeId){
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom("wpeiguang@foxmail.com");
            helper.setTo("1315972502@qq.com");

            helper.setSubject("简历提醒");
            helper.setText("有一份TPG门户系统无记录的简历，id是："+resumeId+"，请及时查看！", true);
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new MtonsException("邮件发送失败", e);
        }
    }

    public String optTPG(String phone, String optId, String name) throws JSONException {
//        if(keys.contains(phone)){
//            return "已被锁定";
//        }
        //通过手机号查询候选人
        String url = "http://ics.chinasoftosg.com:8066/EM/resumeAction!loadResumes";
        StringBuffer param = new StringBuffer();
        param.append("resume.mobile=");
        param.append(phone);
        param.append("&resume.name=&resume.email=&resume.skill.name=&resume.currentProcessO.id=0&beginExp=&endExp=&resume.nativePlace=&resume.city=&resume.resumeStatus.id=&resumeKey=&resume.education.id=&resume.startDateOpt=&resume.endDateOpt=&page=1&rows=10");
        String response = tpgHttp.sendPost(url, param.toString());
        JSONObject jsonObject = new JSONObject(response);
        int total = jsonObject.getInt("total");
        if(total <= 0) {
            Printer.info("没有记录：" + phone);
            ResumePO resume = resumeDao.findByResumeId(optId);
            if(resume != null){
                Printer.info("简历已经存在: "+optId);
                return "TPG系统没有该记录";
            }
            resumeIds.add(jobKey+optId);
            ResumePO po = new ResumePO();
            po.setLockDate(Calendar.getInstance().getTime());
            po.setMobile(phone);
            po.setName(name);
            po.setResumeId(optId);
            po.setSource("");
            po.setJob(jobKey);
            resumeDao.save(po);
            sendEmail(optId);
            return "TPG系统没有该记录";
        }
        JSONArray jsonArray = jsonObject.getJSONArray("rows");
        JSONObject object = jsonArray.getJSONObject(0);
        JSONObject resumeStatus = object.getJSONObject("resumeStatus");
        String status = resumeStatus.getString("name");
        if ("锁定".equals(status)) {
            String lockName = object.getString("currentLockedBy");
            String lockTime = object.getString("lastOperatorDate");
            if(lockName.contains("133387")){
//                System.out.println("已经操作过该简历");
                resumeIds.add(jobKey+optId);
                return "已经操作过该简历";
            }
//            keys.add(phone);
            return "已经被锁定";
        }
        JSONObject education = object.getJSONObject("education");
        if(!searchTaskPO.getDegree().contains(education.getString("name"))){
//            System.out.println("TPG系统中的学历不符："+education.getString("name"));
            resumeIds.add(jobKey+optId);
            return "TPG系统中的学历不符";
        }
        //查看候选人锁定、释放记录
        String id = object.getString("id");
        String userName = object.getString("name");
        param.setLength(0);
        param.append("hireProcessInfoWebVO.resumeId=");
        param.append(id);
        param.append("&page=1&rows=100");
        response = tpgHttp.sendPost("http://ics.chinasoftosg.com:8066/EM/hireProcessInfoAction!loadHireProcessInfoByResumeId", param.toString());
        jsonObject = new JSONObject(response);
        jsonArray = jsonObject.getJSONArray("rows");
        int size = jsonObject.getInt("total");
        if(size > 100){
//            System.out.println("操作数太多，size："+size);
            resumeIds.add(jobKey+optId);
            return "操作数太多";
        }
        int hrCount = 0;
        String preHr = "";
        for(int i = 0; i < size; i++){
            JSONObject row;
            try {
                row = jsonArray.getJSONObject(i);
            }catch (Exception e){
                e.printStackTrace();
                break;
            }
            String operator = row.getString("operator");
            if(operator.contains("133387")){
//                System.out.println("已经操作过该简历");
                resumeIds.add(jobKey+optId);
                return "已经操作过该简历";
            }
            if(operator.equals("")){
                continue;
            }
            if(!operator.equals(preHr)){
                preHr = operator;
                hrCount++;
            }
            if(hrCount > 8){
//                System.out.println("太多hr联系过");
                resumeIds.add(jobKey+optId);
                return "太多hr联系过";
            }
            String desc = row.getString("interviewAssessment");
            List<String> condition = new ArrayList<>();
//            condition.add("外包");
            condition.add("中软");
            condition.add("华为");
            condition.add("毕业");
            condition.add("学位");
            condition.add("学历");
            condition.add("查");
            condition.add("已经");
            condition.add("工作");
            condition.add("入职");
            condition.add("礼貌");
            condition.add("联系");
            condition.add("建议");
            condition.add("挂断");
            condition.add("态度");
            condition.add("自考");
            for(String cond : condition){
                if(desc.contains(cond)){
                    resumeIds.add(jobKey+optId);
                    return desc;
                }
            }
        }
        //锁定
        param.setLength(0);
        param.append("id=");
        param.append(id);
        param.append("&resume_status_id=1&releaseReason=&hireProcessId=1&processStatus=1");
        param.append("&name=");
        param.append(userName);
        param.append("&mobile=");
        param.append(phone);
        response = tpgHttp.sendPost("http://ics.chinasoftosg.com:8066/EM/resumeAction!updateStatusResume", param.toString());
//        Printer.info("成功锁定简历：" + phone+"，response：" + response);
        //分配
        param.setLength(0);
        param.append("hireProcessInfoWebVO.phoneProcess.id=&hireProcessInfoWebVO.phoneProcess.interviewResultStr=true&hireProcessInfoWebVO.phoneProcess.interviewAssessment=&hireProcessInfoWebVO.phoneProcess.notPassReason=-1&hireProcessInfoWebVO.phoneProcess.technicalInterviewer=&hireProcessInfoWebVO.phoneProcess.comprehensiveInterviewer=&hireProcessInfoWebVO.technicalProcess.id=&hireProcessInfoWebVO.technicalProcess.interviewAssessment=&hireProcessInfoWebVO.technicalProcess.technicalInterviewerAct=&hireProcessInfoWebVO.technicalProcess.technicalInterviewer=&hireProcessInfoWebVO.technicalProcess.comprehensiveInterviewer=&hireProcessInfoWebVO.comprehensiveProcess.id=&hireProcessInfoWebVO.comprehensiveProcess.interviewAssessment=&hireProcessInfoWebVO.comprehensiveProcess.comprehensiveInterviewerAct=&hireProcessInfoWebVO.comprehensiveProcess.technicalInterviewer=&hireProcessInfoWebVO.comprehensiveProcess.comprehensiveInterviewer=&hireProcessInfoWebVO.hrProcess.id=&hireProcessInfoWebVO.hrProcess.interviewAssessment=&hireProcessInfoWebVO.hrProcess.nextStageDate=&hireProcessInfoWebVO.hrProcess.technicalInterviewer=&hireProcessInfoWebVO.hrProcess.comprehensiveInterviewer=&hireProcessInfoWebVO.customerProcess.id=&hireProcessInfoWebVO.customerProcess.interviewAssessment=&hireProcessInfoWebVO.customerProcess.customerRankId=-1&hireProcessInfoWebVO.customerProcess.technicalInterviewer=&hireProcessInfoWebVO.customerProcess.comprehensiveInterviewer=&hireProcessInfoWebVO.lobProcess.id=&hireProcessInfoWebVO.lobProcess.interviewAssessment=&hireProcessInfoWebVO.lobProcess.notPassReason=-1&hireProcessInfoWebVO.lobProcess.technicalInterviewer=&hireProcessInfoWebVO.lobProcess.comprehensiveInterviewer=&hireProcessInfoWebVO.entryProcess.id=&hireProcessInfoWebVO.entryProcess.entryStr=&hireProcessInfoWebVO.entryProcess.interviewAssessment=&hireProcessInfoWebVO.entryProcess.notEntryReason=-1&hireProcessInfoWebVO.entryProcess.technicalInterviewer=&hireProcessInfoWebVO.entryProcess.comprehensiveInterviewer=&hireProcessInfoWebVO.requirementNumber=");
        param.append("&hireProcessInfoWebVO.phoneProcess.nextStageDate=");
        param.append(getInterviewTime());
        param.append("&hireProcessInfoWebVO.name");
        param.append(userName);
        param.append("&hireProcessInfoWebVO.mobile=");
        param.append(phone);
        param.append("&hireProcessInfoWebVO.resumeId=");
        param.append(id);
        response = tpgHttp.sendPost("http://ics.chinasoftosg.com:8066/EM/hireProcessInfoAction!addHireProcessInfoNew", param.toString());
        Printer.info("成功分配：" + phone+"，response：" + response);
        return "";
    }

    private static String getInterviewTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date curDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        calendar.add(Calendar.DATE, 2);
        Date date = calendar.getTime();
        String out = sdf.format(date);
        return out+" 14:30";
    }



}
