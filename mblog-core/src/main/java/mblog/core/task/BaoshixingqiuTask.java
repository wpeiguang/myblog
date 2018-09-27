package mblog.core.task;

import mblog.base.lang.*;
import mblog.base.print.Printer;
import mblog.core.persist.dao.GeneralizeListDao;
import mblog.core.persist.dao.GeneralizeTaskDao;
import mblog.core.persist.entity.GeneralizeListPO;
import mblog.core.persist.entity.GeneralizeTaskPO;
import org.json.JSONException;
import org.json.JSONObject;

public class BaoshixingqiuTask extends Thread {

    private GeneralizeTaskPO po;
    private static GeneralizeTaskDao dao;
    private static GeneralizeListDao generalizeListDao;

    public BaoshixingqiuTask(GeneralizeTaskPO po, GeneralizeTaskDao dao, GeneralizeListDao generalizeListDao){
        this.po = po;
        this.dao = dao;
        this.generalizeListDao = generalizeListDao;
    }

    @Override
    public void run() {
        String itemId = "24071";
        int count = po.getAmount() - po.getSuccessCount();
        String token = po.getCode();
        int inteval = po.getInteval();
        while (count-- > 0 && Common.threadList.get(Common.GENERALIZE_THREAD+po.getId()) != null){
            String userAgent = "Mozilla/5.0 (Linux; U; Android 5.1.1; zh-cn; SM-G9350 Build/LMY48Z) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
            String ip = RandomValue.getRandomIp();
            HttpRequest httpRequest = new HttpRequest(ip, userAgent, null);
            String phone = RandomValue.getPhone(itemId);
            StringBuffer param  = new StringBuffer();
            param.append("versionCode=113&device=android");
            param.append("&phone=");
            param.append(phone);
            param.append("&type=1");
            String response = httpRequest.sendGet("http://interface.baoshixingqiu.com/index/send-msg", param.toString());
            try {
                JSONObject resJson = new JSONObject(response);
                if(resJson.getInt("code") != 0){
                    updateFailTask();
                    return;
                }
            } catch (JSONException e) {
                updateFailTask();
                return;
            }

            String yzm = RandomValue.getVeriCode(phone, itemId, 6);
            RandomValue.releasePhone(phone, itemId);
            if(yzm == null){
                count = count + 1;
                continue;
            }
            param.setLength(0);
            param.append("versionCode=113&device=android");
            param.append("&phone=");
            param.append(phone);
            param.append("&code=");
            param.append(yzm);
            param.append("&invite_code=");
            param.append(token);
            response = httpRequest.sendGet("http://interface.baoshixingqiu.com/index/check", param.toString());
            String hash = "";
            try {
                JSONObject resJson = new JSONObject(response);
                if(resJson.getInt("code") != 0){
                    updateFailTask();
                    return;
                }
                JSONObject data = resJson.getJSONObject("data");
                hash = data.getString("hash");
            } catch (JSONException e) {
                updateFailTask();
                return;
            }

            int len = (int)(7+Math.random()*(14-7+1));
            String password = RandomValue.getRandomString(len);
            param.append("&hash=");
            param.append(hash);
            param.append("&pwd=");
            param.append(password);
            param.append("&rePwd=");
            param.append(password);
            response = httpRequest.sendGet("http://interface.baoshixingqiu.com/index/register", param.toString());
            String myToken = "";
            try {
                JSONObject resJson = new JSONObject(response);
                if(resJson.getInt("code") != 0){
                    updateFailTask();
                    return;
                }
                JSONObject data = resJson.getJSONObject("data");
                myToken = data.getString("token");
            } catch (JSONException e) {
                updateFailTask();
                return;
            }
            len = (int)(6+Math.random()*(10-6+1));
            String nickname = RandomValue.getUserName(len);
            param.setLength(0);
            param.append("versionCode=113&device=android");
            param.append("&token=");
            param.append(myToken);
            param.append("&nickname=");
            param.append(nickname);
            response = httpRequest.sendGet("http://interface.baoshixingqiu.com/user/set-nickname", param.toString());

            GeneralizeListPO listPO = new GeneralizeListPO();
            listPO.setProject(EnumProject.BAOSHI_XINGQIU.getValue());
            listPO.setMobile(phone);
            listPO.setPassword(password);
            listPO.setUsername(nickname);
            listPO.setInviter(po.getCode());
            generalizeListDao.save(listPO);
            po.setSuccessCount(po.getSuccessCount()+1);
            dao.save(po);

            int interval = (int)(900+Math.random()*(1100-900))*inteval;
            Printer.info("token: "+token+", sleep:"+interval);
            try {
                sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        po.setStatus(EnumTaskStatus.STOPED.getName());
        dao.save(po);
        Thread thread = Common.threadList.get(Common.GENERALIZE_THREAD+po.getId());
        if(thread != null){
            Common.threadList.remove(Common.GENERALIZE_THREAD+po.getId());
        }
    }

    private void updateFailTask(){
        po.setFailedCount(po.getFailedCount()+1);
        po.setStatus(EnumTaskStatus.STOPED.getName());
        dao.save(po);
    }
}
