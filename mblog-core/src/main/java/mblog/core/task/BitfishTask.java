package mblog.core.task;

import mblog.base.lang.*;
import mblog.base.print.Printer;
import mblog.core.persist.dao.GeneralizeListDao;
import mblog.core.persist.dao.GeneralizeTaskDao;
import mblog.core.persist.entity.GeneralizeListPO;
import mblog.core.persist.entity.GeneralizeTaskPO;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class BitfishTask extends Thread {

    private GeneralizeTaskPO po;
    private GeneralizeTaskDao dao;
    private GeneralizeListDao generalizeListDao;

    public BitfishTask(GeneralizeTaskPO po, GeneralizeTaskDao dao, GeneralizeListDao generalizeListDao){
        this.po = po;
        this.dao = dao;
        this.generalizeListDao = generalizeListDao;
    }
    @Override
    public void run() {
        String url = "https://www.mybtcai.com/support?ref=";
        String itemId = "23280";
        int count = po.getAmount() - po.getSuccessCount();
        String token = po.getCode();
        int inteval = po.getInteval();
        while (count-- > 0 && Common.threadList.get(Common.GENERALIZE_THREAD+po.getId()) != null){
            try {
                String ip = RandomValue.getRandomIp();
                String userAgent = Common.userAgents[(int)(Math.random()* Common.userAgents.length)];
                HttpRequest httpRequest = new HttpRequest(ip, userAgent, null);
                String response = httpRequest.sendGet(url+token, "");
                if(StringUtils.isEmpty(response)){
                    po.setFailedCount(po.getFailedCount()+1);
                    dao.save(po);
                    continue;
                }
                Document doc = Jsoup.parse(response);
                Element body = doc.body();
                Element input = body.select("a").first();
                String id = input.attr("href").split("=")[1];

                String phone = RandomValue.getPhone(itemId);
                String name = RandomValue.getChineseName();
                int len = (int)(7+Math.random()*(14-7+1));
                String password = RandomValue.getRandomString(len);
                response = httpRequest.sendGet("https://www.mybtcai.com/repage_and.html?id="+id, "");
                doc = Jsoup.parse(response);
                body = doc.body();
                input = body.getElementById("token");
                String invite = input.attr("value");
                httpRequest.sendGet("https://www.mybtcai.com/phone.html?u_phone="+phone, "");
                StringBuffer param = new StringBuffer();
                param.append("u_phone=");
                param.append(phone);
                param.append("&u_name=");
                param.append(name);
                param.append("&password=");
                param.append(password);
                param.append("&token=");
                param.append(invite);
                response = httpRequest.sendPost("https://www.mybtcai.com/register_and.html", param.toString());
                param.setLength(0);
                param.append("u_phone=");
                param.append(phone);
                param.append("&password=");
                param.append(password);
                response = httpRequest.sendPost("https://www.mybtcai.com/login.html", param.toString());
                JSONObject resJson = new JSONObject(response);
                if(resJson.getInt("status") != 1){
                    Printer.error("bitfish login failed. "+response);
                    count++;
                    continue;
                }
                response = httpRequest.sendGet("https://www.mybtcai.com/reg_sms.html", "");
                doc = Jsoup.parse(response);
                body = doc.body();
                input = body.getElementById("token");
                invite = input.attr("value");
                response = httpRequest.sendPost("https://www.mybtcai.com/reg_sms_sub.html", "");
                String yzm = RandomValue.getVeriCode(phone, itemId, 4);
                RandomValue.releasePhone(phone, itemId);
                if(yzm == null){
                    count = count + 1;
                    continue;
                }
                param.setLength(0);
                param.append("google=");
                param.append(yzm);
                param.append("&token=");
                param.append(invite);
                response = httpRequest.sendPost("https://www.mybtcai.com/valid.html", param.toString());

                GeneralizeListPO listPO = new GeneralizeListPO();
                listPO.setProject(EnumProject.BITFISH.getValue());
                listPO.setMobile(phone);
                listPO.setPassword(password);
                listPO.setUsername(name);
                listPO.setInviter(po.getCode());
                generalizeListDao.save(listPO);

                po.setSuccessCount(po.getSuccessCount()+1);
                dao.save(po);

                int interval = (int)(900+Math.random()*(1100-900))*inteval;
                Printer.info("token: "+token+", sleep:"+interval);
                Thread.sleep(interval);
            } catch (Exception e) {
                e.printStackTrace();
                count++;
                continue;
            }
        }
        po.setStatus(EnumTaskStatus.STOPED.getName());
        dao.save(po);
        Thread thread = Common.threadList.get(Common.GENERALIZE_THREAD+po.getId());
        if(thread != null){
            Common.threadList.remove(Common.GENERALIZE_THREAD+po.getId());
        }
    }
}
