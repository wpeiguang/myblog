package mblog.base.lang;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class HttpRequest {
    private static Logger logger = Logger.getLogger(HttpRequest.class);
    private String ip = null;
    private String userAgent = null;
    private String cookie = "";
    private String host = null;
    public HttpRequest(String ip, String userAgent, String cookie){
        this.ip = ip;
        this.userAgent = userAgent;
        this.cookie = cookie;
    }
    public String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            if(param != null && !param.equals("")) {
                urlNameString = urlNameString + "?" + param;
            }
            URL realUrl = new URL(urlNameString);

            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            //connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            if(ip != null){
                connection.setRequestProperty("x-forwarded-for", ip);
            }
            if(userAgent != null){
                connection.setRequestProperty("user-agent", userAgent);
            }
           // connection.setRequestProperty("X-Requested-With", "com.android.browser");
            if(cookie != null){
                connection.setRequestProperty("Cookie", cookie);
            }
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            //Map<String, List<String>> map = connection.getHeaderFields();
            Map headers = connection.getHeaderFields();
            List<String> cookies = (List<String>) headers.get("Set-Cookie");
            if(cookies != null){
                //this.cookie = "";
                for(String c : cookies){
                    this.cookie = this.cookie + c + ";";
                }
            }
            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                common.logger.debug(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.debug("send GET request failed. " + e);
            e.printStackTrace();
            result = "";
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                result = "";
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
//            String ip = RandomValue.getRandomIp();
//            String userAgent = userAgents[(int)(Math.random()*userAgents.length)];
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
//            conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
//            conn.setRequestProperty("X-DevTools-Emulate-Network-Conditions-Client-Id", UUID.randomUUID().toString());
            if(ip != null){
                conn.setRequestProperty("x-forwarded-for", ip);
            }
            if(userAgent != null){
                conn.setRequestProperty("user-agent", userAgent);
            }
            if(host != null){
                conn.setRequestProperty("Host", host);
            }

            if(cookie != null){
                conn.setRequestProperty("Cookie", cookie);
            }
            conn.setRequestProperty("X-DevTools-Emulate-Network-Conditions-Client-Id", "77fe693d-e1c9-46f3-b91a-e8be34c78a29");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            Map headers = conn.getHeaderFields();
            List<String> cookies = (List<String>) headers.get("Set-Cookie");
            if(cookies != null){
                if(cookie == null) {
                    this.cookie = "";
                }
                for(String c : cookies){
                    this.cookie = this.cookie + c + ";";
                }
            }
//            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.debug("send POST request failed." + e);
            e.printStackTrace();
            result = "";
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
                result = "";
            }
        }
        return result;
    }

    public String sendHttpPost(String url, String body) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json");
        if(ip != null){
            httpPost.addHeader("x-forwarded-for", ip);
        }
        if(userAgent != null){
            httpPost.addHeader("user-agent", userAgent);
        }
        if(host != null){
            httpPost.addHeader("Host", host);
        }

        if(cookie != null){
            httpPost.addHeader("Cookie", cookie);
        }
        httpPost.setEntity(new StringEntity(body));

        CloseableHttpResponse response = httpClient.execute(httpPost);
        //System.out.println(response.getStatusLine().getStatusCode() + "\n");
        HttpEntity entity = response.getEntity();
        String responseContent = EntityUtils.toString(entity, "UTF-8");
        //System.out.println(responseContent);

        response.close();
        httpClient.close();
        return responseContent;
    }

    public String getCookie(String url) {
        String cookie = "";
        try {
            String name = "";
            String value = "";
            if(this.cookie != null) {
                String[] arr = this.cookie.split("=");
                name = arr[0];
                value = arr[1];
            }
            Connection conn = Jsoup.connect(url);
            conn.method(Connection.Method.GET);
            conn.cookie(name, value);
            conn.followRedirects(false);
            Connection.Response response;
            response = conn.execute();
            Map<String, String> getCookies = response.cookies();
            cookie = getCookies.toString();
            cookie = cookie.substring(cookie.indexOf("{") + 1, cookie.lastIndexOf("}"));
            cookie = cookie.replaceAll(",", ";");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cookie;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
    public void setHost(String host){
        this.host = host;
    }
}

