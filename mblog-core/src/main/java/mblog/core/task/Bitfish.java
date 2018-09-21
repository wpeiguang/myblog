package mblog.core.task;

import mblog.base.lang.Common;
import mblog.base.lang.HttpRequest;
import mblog.base.lang.RandomValue;
import mblog.base.print.Printer;
import mblog.core.persist.entity.GeneralizeTaskPO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.TimerTask;

public class Bitfish extends TimerTask {

    private GeneralizeTaskPO po;

    public Bitfish(GeneralizeTaskPO po){
        Printer.info("new bitfish");
        this.po = po;
    }
    @Override
    public void run() {
        String url = "https://www.mybtcai.com/support?ref=";
        String itemId = "23280";
        int count = po.getAmount();
        String token = po.getCode();
        int inteval = po.getInteval();
        Printer.info("run: "+token+", "+count);
        while (count-- > 0){
            Printer.info("start the task");
            String ip = RandomValue.getRandomIp();
            String userAgent = Common.userAgents[(int)(Math.random()* Common.userAgents.length)];
            String cookie = Common.getCookie(url+token);
            HttpRequest httpRequest = new HttpRequest(ip, userAgent, cookie+";");
            String phone = RandomValue.getPhone(itemId);
            String name = RandomValue.getChineseName();
            int len = (int)(7+Math.random()*(14-7+1));
            String password = RandomValue.getRandomString(len);
            String response = httpRequest.sendGet("https://www.mybtcai.com/repage_and.html?id=115372", "");
            Document doc = Jsoup.parse(response);
            Element body = doc.body();
            Element input = body.getElementById("token");
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
            try {
                int interval = (int)(5+Math.random()*(10-5))*(int)(900+Math.random()*(1000-900))*inteval;
                Printer.info("token: "+token+", sleep:"+interval);
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

