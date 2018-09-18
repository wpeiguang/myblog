package mblog.core.task;

import mblog.base.lang.HttpRequest;
import mblog.base.lang.MtonsException;
import mblog.base.lang.RandomValue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import redis.clients.jedis.Jedis;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class TokenTask {
    private static String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Safari/537.36 Maxthon/5.2.4.3000";
    private static String cookie = "RK=Ql7NGVBEPZ; pgv_pvi=9407668224; pgv_si=s5022614528; pgv_info=ssid=s7823503032; pgv_pvid=2482593388; _qpsvr_localtk=0.3520179335653375; zzpaneluin=; zzpanelkey=; ptisp=ctc; ptcz=ac95abcd7883aab895dbacb3ec7a74fdbf60fd3d0c1fd8101612891e3400e90a; uin=o1609972202; skey=@ZEkZKnRA3; pt2gguin=o1609972202; p_uin=o1609972202; pt4_token=1mI-kaXwtweup3bajZ3T0G87nPCMgzOKTJH3W8Qq2w4_";
    private static String cookieM = "RK=wk6FW3BlY7; pt2gguin=o1609972202; ptcz=320e4e46ffe3e51563bf46ef17b04bf210581d807e6300d13eedba64ae87e4e2; pgv_pvid=8135860565; uin=o1609972202; skey=ZsH2cdT4O9";
    private static HttpRequest httpRequest;
    public static void main(String[] args) {
        httpRequest = new HttpRequest(RandomValue.getRandomIp(), userAgent, cookie);
//        setAccounts("1609972202");
//        sendEmail();
        getUinsByGroupid("50473150");
    }

    private static void sendEmail(){
        try {

            JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();

            senderImpl.setHost("smtp.qq.com");
            senderImpl.setUsername("1609972202@qq.com");
            senderImpl.setPassword("keaqtpdjyjvqijhj");
            senderImpl.setPort(465);

            Properties props = new Properties();

            props.put("mail.smtp.auth","true");
            props.put("mail.smtp.ssl.enable","true");
            props.put("mail.smtp.starttls.enable","true");
            props.put("mail.smtp.starttls.required","true");

            senderImpl.setJavaMailProperties(props);

            MimeMessage mimeMessage = senderImpl.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true, "UTF-8");

            //  在构建MimeMessageHelper时候，所给定的值是true表示启用，

            //multipart模式

            mimeMessageHelper.setTo("359700547@qq.com");

            mimeMessageHelper.setFrom("1609972202@qq.com");

            mimeMessageHelper.setSubject("小趣宝藏-红包福利");

            mimeMessageHelper.setText("<html><head></head><body>比火牛分红更多，超越火牛，早注册早分红，正在内测，注册的人还很少，早到早得，微信扫码注册，绑定支付宝，注册后 点宝箱领钻石 现在邀请一人获得30钻，" +
                    "坚持推广100人每天分10块," +
                    "1000人每天分100," +
                    "目前收益"+ "<img src=\"cid:image\"/></body></html>",true);

            FileSystemResource img = new FileSystemResource(new File("D:\\picture\\小趣宝藏.jpg"));

            mimeMessageHelper.addInline("image",img);

            senderImpl.send(mimeMessage);

        } catch (Exception e) {
            throw new MtonsException("邮件发送失败", e);
        }
    }

    private static void setAccounts(String uin){
        Jedis jedis = new Jedis("localhost");
        StringBuffer param = new StringBuffer();
        param.append("uin=");
        param.append(uin);
        param.append("&ua=");
        param.append("Mozilla%2F5.0%20(Windows%20NT%206.1%3B%20WOW64)%20AppleWebKit%2F537.36%20(KHTML%2C%20like%20Gecko)%20Chrome%2F61.0.3163.79%20Safari%2F537.36%20Maxthon%2F5.2.4.3000&random=0.9951982801804014");
        param.append("&random=");
        param.append(Math.random());
        param.append("&g_tk=1575146664");
        String response = httpRequest.sendGet("http://qun.qzone.qq.com/cgi-bin/get_group_list", param.toString());
        List<String> uins = new ArrayList<>();
        try {
            String resJson = response.substring(10, response.length()-2);
            JSONObject jsonObject = new JSONObject(resJson);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray group = data.getJSONArray("group");
            for(int i = 0; i < group.length(); i++) {
                JSONObject item = group.getJSONObject(i);
                String groupid = item.getString("groupid");
                List<String> temp = getUinsByGroupid(groupid);
                uins.addAll(temp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int size = uins.size();
        System.out.println("size: "+size);
        for(int i = 0; i < size; i++){
            System.out.println(uins.get(i));
        }
    }

    private static List<String> getUinsByGroupid(String groupId){
        List<String> result = new ArrayList<>();
        StringBuffer param = new StringBuffer();
        param.append("friends=1&name=1");
        param.append("&gc=");
        param.append(groupId);
        param.append("&bkn=1591649635&src=qinfo_v3");
        param.append("&_ti=");
        param.append(System.currentTimeMillis());
        httpRequest.setCookie(cookieM);
        String response = httpRequest.sendGet("https://qinfo.clt.qq.com/cgi-bin/qun_info/get_members_info_v1", param.toString());
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.getJSONObject("members");
            Iterator keys = data.keys();
            while (keys.hasNext()){
                String uin = keys.next().toString();
                if(uin.equals("1609972202"))
                result.add(keys.next().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
