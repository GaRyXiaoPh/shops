package com.cmd.wallet.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

import java.io.BufferedReader;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/*
<!-- https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient -->
<dependency>
<groupId>org.apache.httpcomponents</groupId>
<artifactId>httpclient</artifactId>
<version>4.5.4</version>
</dependency>
*/


public class HttpUtil {
    private static final String CHARSET = "UTF-8";

    public static String HttpGet(String url) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            //httpGet.addHeader("token","8464465"); 添加header信息
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
            CloseableHttpResponse response = httpClient.execute(httpGet);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String str = EntityUtils.toString(entity, CHARSET);
                    return str;
                }
            } finally {
                response.close();
                httpClient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //http post请求（数据编码格式application/x-www-form-urlencoded）
    public static String HttpPost(String url, Map<String, Object> params){
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();

            for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext();) {
                String key = iterator.next();
                parameters.add(new BasicNameValuePair(key, params.get(key).toString()));
            }

            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(parameters, CHARSET);
            httpPost.setEntity(urlEncodedFormEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String str = EntityUtils.toString(entity, CHARSET);
                    return str;
                }
            } finally {
                response.close();
                httpClient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * http post请求（数据编码格式application/x-www-form-urlencoded）
     * @param url     请求地址
     * @param params  请求参数(参数格式："key1=value1&key2=value2")
     */
    public static String HttpPost(String url, String params){
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            StringEntity stringEntity = new StringEntity(params,CHARSET);
            stringEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(stringEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String str = EntityUtils.toString(entity, CHARSET);
                    return str;
                }
            } finally {
                response.close();
                httpClient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * http post请求（数据编码格式application/json）
     * @param url  请求地址
     * @param json 请求参数（参数格式为json字符串）
     */
    public static String HttpPostJson(String url, String json) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            //中文乱码
            StringEntity stringEntity = new StringEntity(json,CHARSET);
            //数据编码格式
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity, CHARSET);
                }
            } finally {
                response.close();
                httpClient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	public static String HttpPostDigest(String userName, String passWord, String url, String json) {
		try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
			Credentials creds = new UsernamePasswordCredentials(userName, passWord);
            URI uri = new URI(url);
			httpClient.getCredentialsProvider().setCredentials( new AuthScope(uri.getHost(), uri.getPort()), (Credentials) creds);

			httpClient.getParams().setParameter(AuthPolicy.DIGEST, Collections.singleton(AuthPolicy.DIGEST));
			httpClient.getAuthSchemes().register(AuthPolicy.DIGEST, new DigestSchemeFactory());
			HttpPost method = new HttpPost(url);
			method.addHeader("Content-type","application/json; charset=utf-8");
			method.setHeader("Accept", "application/json");
			method.setEntity(new StringEntity(json, Charset.forName("UTF-8")));
			HttpResponse response = httpClient.execute(method);
			HttpEntity entity = response.getEntity();
            if (entity != null) {
                String str = EntityUtils.toString(entity, CHARSET);
                return str;
            }
		} catch (Exception e) {
            e.printStackTrace();
		}
		return null;
	}


    public static int sendSMSPost(String url, String account, String password, String Mobile, String code) {
        String CorpID = "GZJS003006";     // 账户名
        String Pwd = "cj@668";          // 密码
        String send_content = "You are using ENG11 wallet verification function, your verification code is "+code;
        String strUrl = "https://sdk2.028lk.com/sdk2/BatchSend2.aspx";
        String param = "CorpID=" + CorpID + "&Pwd=" + Pwd + "&Mobile=" + Mobile + "&Content=" + send_content + "&Cell=&SendTime=";

        String inputLine = "";
        int value = -2;
        try {
            inputLine = sendSMSPost(strUrl, param);
            value = new Integer(inputLine).intValue();
        } catch (Exception e) {
            System.out.println("网络异常,发送短信失败！");
            value = -2;
        }
        return value;
    }
    public static String sendSMSPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);     // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*"); // 设置通用的请求属性
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());  // 获取URLConnection对象对应的输出流
            out.print(param);   // 发送请求参数
            out.flush();        // flush输出流的缓冲
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));  // 定义BufferedReader输入流来读取URL的响应
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[]argv){
        //HttpUtil.sendSmsNew("86", "13717058101", "8888");
    }
}
