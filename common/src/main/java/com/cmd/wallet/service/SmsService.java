package com.cmd.wallet.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmd.wallet.common.enums.SmsCaptchaType;
import com.cmd.wallet.common.exception.ServerException;
import com.cmd.wallet.common.mapper.SmsMapper;
import com.cmd.wallet.common.model.SmsCaptcha;
import com.cmd.wallet.common.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 短信业务处理类
 * Created by Administrator on 2017/6/18.
 */
@Service
public class SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    private static final String SUCCESS = "0";

    private static final String PARAM = "%@%";

    @Value("${spring.profiles.active}")
    private String profiles;

    @Value("${sms.url:null}")
    private String url;
    @Value("${sms.account:null}")
    private String account;
    @Value("${sms.passwd:null}")
    private String passwd;
    @Value("${sms.tplId:null}")
    private String tplId;

    @Value("${sms.urlGw:null}")
    private String urlGw;
    @Value("${sms.accountGw:null}")
    private String accountGw;
    @Value("${sms.passwdGw:null}")
    private String passwdGw;

    @Value("${sms.expire:120}")
    private int expire;
    @Value("${sms.ignore:false}")
    //是否校验短信验证码
    private boolean ignoreCheck;

    @Value("${email.serverHost:null}")
    private String serverHost;
    @Value("${email.serverPort:null}")
    private String serverPort;
    @Value("${email.userName:null}")
    private String userName;
    @Value("${email.userPassword:null}")
    private String userPassword;
    @Value("${email.fromAddress:null}")
    private String fromAddress;
    @Value("${email.validate:true}")
    private boolean validate;

    private static final int SMS_CAPTCHA_LENGTH = 6;

    @Autowired
    private CaptchaImgService captchaImgService;
    @Autowired
    private SmsMapper smsMapper;

    //获取图形验证码
    public String getCaptchaImgWord(String nationalCode, String mobile){
        return captchaImgService.getWord(nationalCode, mobile);
    }

    //验证图形验证码
    public void CheckCaptchaImgWord(String nationalCode, String mobile, String words){
        //if (ignoreCheck) {
        //    logger.info("ignore captcha checking.");
        //    return;
        //}
        captchaImgService.check(nationalCode, mobile, words);
    }

    //发送短信验证码
    public void sendSmsCaptcha(String nationalCode, String mobile, String captchaTypeCode, String captchaImgWord) {
        if (ignoreCheck) {
            logger.info("ignore sms message sending.");
            return;
        }
        // 1.检查图形验证码
        this.captchaImgService.check(nationalCode, mobile, captchaImgWord);

        SmsCaptchaType captchaType = Assert.assertEnumParam(SmsCaptchaType.class, captchaTypeCode.trim(), 500,"不支持的验证码类型:" + captchaTypeCode);

        // 2.检查是否连续发送短信验证码
        Assert.check(!isAllowCaptcha(nationalCode, mobile, captchaTypeCode), 500,"1分钟内只允许请求1条短信验证码");

        // 开发环境使用
        if (profiles.contains("dev") || profiles.contains("test")) {
            SmsCaptcha devCaptcha = new SmsCaptcha().setMobile(nationalCode+mobile).setType(captchaType.getValue()).setCode("123456").setLastTime(new Date(new Date().getTime()+expire*1000));
            updateMobileCaptcha(devCaptcha); // 存储短信记录
           return;
        }

        // 3.生成新的短信验证码
        String captcha = RandomUtil.getCode(SMS_CAPTCHA_LENGTH);
        SmsCaptcha smsCaptcha = new SmsCaptcha().setMobile(nationalCode+mobile).setType(captchaType.getValue()).setCode(captcha).setLastTime(new Date(new Date().getTime()+expire*1000));

        try {
            // 4.获得短信内容
            String msg = this.getCaptchaMsg(captchaType, captcha);

            // 5.调用邮件,短信服务
            if (mobile.contains("@")){
                if (captchaType==SmsCaptchaType.REGISTER){
                    sendEmailRegister(nationalCode, mobile, msg);
                } else {
                    sendEmail(nationalCode, mobile, msg);
                }
            } else {
                sendPostSmsNew(nationalCode, mobile, captcha);
            }

            // 6.存储短信记录
            updateMobileCaptcha(smsCaptcha);
        } catch (Exception e) {
            throw new ServerException(500, "调用发送短信接口错误");
        }
    }

    private void updateMobileCaptcha(SmsCaptcha smsCaptcha) {
        SmsCaptcha sms = smsMapper.getSmsCaptchaByMobile(smsCaptcha.getMobile(), smsCaptcha.getType());
        if (sms==null){
            smsMapper.addSmsCaptcha(smsCaptcha.getMobile(), smsCaptcha.getType(), smsCaptcha.getCode(), smsCaptcha.getLastTime());
        }else{
            smsMapper.updateSmsCaptcha(smsCaptcha.getMobile(), smsCaptcha.getType(), smsCaptcha.getCode(), smsCaptcha.getLastTime());
        }
    }

    /**
     * 获取验证码短信
     * @param type    验证码类型：SmsCaptchaType
     * @param captcha 6位数字验证码
     * @return String 消息内容
     */
    private String getCaptchaMsg(SmsCaptchaType type, String captcha) {
        StringBuffer template = new StringBuffer(type.getTemplate());
        return replaceParam(template, captcha).toString();
    }

    private StringBuffer replaceParam(StringBuffer template, String paramVal) {
        int idx = template.indexOf(PARAM);
        return template.replace(idx, idx + PARAM.length(), paramVal);
    }

    //检查是否允许发送短信验证码(1分钟以内只能发送一条短信)
    private boolean isAllowCaptcha(String nationalCode, String mobile, String smsCaptchaType) {
        SmsCaptcha smsCaptcha = smsMapper.getSmsCaptchaByMobile(nationalCode+mobile, smsCaptchaType);
        if (smsCaptcha!=null){
            if (new Date().getTime() - smsCaptcha.getLastTime().getTime()<60*1000){
                return false;
            }
        }
        return true;
    }

    //检查手机短信验证码是否有效
    public boolean checkCaptcha(String nationalCode, String mobile, String smsCaptchaType, String captcha) {
        logger.info("ignore ignoreCheck message verify.",ignoreCheck);
        if (ignoreCheck) {
            logger.info("ignore sms message verify.");
            return true;
        }
        logger.info("check:"+nationalCode+":"+mobile+":"+smsCaptchaType+captcha);
        SmsCaptcha smsCaptcha = smsMapper.getSmsCaptcha(nationalCode+mobile, smsCaptchaType, captcha);
        if (smsCaptcha==null){
            return false;
        }
        if (new Date().getTime() - smsCaptcha.getLastTime().getTime()<expire*1000){
            return true;
        }
        return false;
    }

    //两步验证（业务和验证分离）
    // 第一步OK，设置状态
    public boolean checkCaptchaFirst(String nationalCode, String mobile, String smsCaptchaType, String captcha) {
        SmsCaptcha smsCaptcha = smsMapper.getSmsCaptcha(nationalCode+mobile, smsCaptchaType, captcha);
        if (smsCaptcha==null){
            return false;
        }
        if (new Date().getTime() - smsCaptcha.getLastTime().getTime()<expire*1000){
            smsMapper.updateSmsCaptchaStatus(nationalCode+mobile, smsCaptchaType);
            return true;
        }
        return false;
    }
    // 第二步，检查状态
    public boolean checkCaptchaSecond(String nationalCode, String mobile, String smsCaptchaType){
        SmsCaptcha smsCaptcha = smsMapper.getSmsCaptchaByMobile(nationalCode+mobile, smsCaptchaType);
        if (smsCaptcha==null){
            return false;
        }
        if (smsCaptcha.getStatus()==1 && new Date().getTime() - smsCaptcha.getLastTime().getTime()<expire*1000){
            return true;
        }
        return false;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String toGBK(String source) {
        try {
            StringBuilder sb = new StringBuilder();
            byte[] bytes = source.getBytes("GBK");
            for (byte b : bytes) {
                sb.append("%" + Integer.toHexString((b & 0xff)).toUpperCase());
            }
            return sb.toString();
        }catch (Exception e){
            return null;
        }
    }
    public static String toGB2312( String source) {
        try {
            byte[] bytes = new byte[source.length() / 2];
            for (int i = 0; i < bytes.length; i++) {
                byte high = Byte.parseByte(source.substring(i * 2, i * 2 + 1), 16);
                byte low = Byte.parseByte(source.substring(i * 2 + 1, i * 2 + 2), 16);
                bytes[i] = (byte) (high << 4 | low);
            }
            return new String(bytes, "gb2312");
        }catch (Exception e){
            return null;
        }
    }

    public int sendPostSmsNormal(String nationalCode, String mobile, String msg){
        msg = SmsService.toGBK(msg);
        if (msg==null){
            logger.info("TO GBK error:"+msg);
            return -2;
        }

        String param = "CorpID=" + account + "&Pwd=" + passwd + "&Mobile=" + mobile + "&Content=" + msg + "&Cell=&SendTime=";
        String strUrl = url;

        String inputLine = "";
        int value = -2;
        try {
            inputLine = sendPostNew(strUrl, param);
            value = new Integer(inputLine).intValue();
            logger.info("sendPostSmsNew:"+mobile+","+param+","+value);
        } catch (Exception e) {
            logger.error("sendPostSmsNew exception:"+e.getMessage());
        }
        return value;
    }
    //客户短信新接口
    public  int sendPostSmsNew(String nationalCode, String Mobile, String code) {
        //String account = "GZJS003006";     // 账户名
        //String passwd = "cj@668";          // 密码
        //String strUrl = "https://sdk2.028lk.com/sdk2/BatchSend2.aspx";
        //String param = "CorpID=" + CorpID + "&Pwd=" + Pwd + "&Mobile=" + Mobile + "&Content=" + send_content + "&Cell=&SendTime=";
        String send_content = "You are using Bean sprout verification function, your verification code is "+code;
        String param = "CorpID=" + account + "&Pwd=" + passwd + "&Mobile=" + Mobile + "&Content=" + send_content + "&Cell=&SendTime=";
        String strUrl = url;

        String inputLine = "";
        int value = -2;
        try {
            inputLine = sendPostNew(strUrl, param);
            value = new Integer(inputLine).intValue();
            logger.info("sendPostSmsNew:"+Mobile+","+value);
        } catch (Exception e) {
            logger.error("sendPostSmsNew exception:"+e.getMessage());
        }
        return value;
    }
    public  String sendPostNew(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);     // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*"); // 设置通用的请求属性
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setDoOutput(true); // 发送POST请求必须设置如下两行
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
        } finally { // 使用finally块来关闭输出流、输入流
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


    //发送短信客户自己接口
    public void sendSmsNew(String nationalCode, String mobile, String code){
        if (ignoreCheck) {
            logger.info("ignore sms message verify.");
            return ;
        }

        String [] mobiles = {"+"+nationalCode+mobile};
        String [] codes = {code};

        String strUrl = null;
        Map<String, Object> m = new HashMap<>();
        strUrl = this.url;
        m.put("account", this.account);
        m.put("password", this.passwd);
        m.put("mobiles", mobiles);
        m.put("tplId", tplId);
        m.put("tplParams", codes);
        String strJson = JSONUtil.toJSONString(m);
        this.sendMsgNew(strUrl, strJson);
    }
    public void sendMsgNew(String url, String params){
        logger.info("请求参数为:" + params);
        try{
            String result= HttpUtil.HttpPostJson(url, params);
            logger.info("返回参数为:" + result);

            JSONObject jsonObject =  JSON.parseObject(result);
            String code = jsonObject.get("code").toString();
            String status = null;
            if (code!=null || "000000".equals(code)){
                JSONArray rst = (JSONArray)jsonObject.get("result");
                if (rst!=null && rst.size()==1){
                    JSONObject json = (JSONObject)rst.get(0);
                    status = (String)json.get("status").toString();
                }
            }
            logger.info("状态码:" + code + ",状态码说明:" + status);
        } catch (Exception e){
            logger.error("请求异常：" + e);
        }
    }



    //发送短信
    public void sendSms(String nationalCode, String mobile, String msg){
        if (ignoreCheck) {
            logger.info("ignore sms message verify.");
            return ;
        }

        String strUrl = null;
        Map<String, String> m = new HashMap<>();
        if ("86".equals(nationalCode)){
            //国内短信
            strUrl = this.url;
            m.put("account", this.account);
            m.put("password", this.passwd);
            m.put("msg", msg);
            m.put("phone", mobile);
            m.put("report","true");
            String strJson = JSONUtil.toJSONString(m);
            this.sendMsg(strUrl, strJson);
        }else{
            //国际短信
            strUrl = this.urlGw;
            m.put("account", this.accountGw);
            m.put("password", this.passwdGw);
            m.put("msg", msg);
            m.put("mobile", nationalCode+mobile);
            m.put("report","true");
            String strJson = JSONUtil.toJSONString(m);
            this.sendMsg(strUrl, strJson);
        }
    }
    public void sendMsg(String url, String params){
        logger.info("请求参数为:" + params);
        try{
            String result= HttpUtil.HttpPostJson(url, params);
            logger.info("返回参数为:" + result);

            JSONObject jsonObject =  JSON.parseObject(result);
            String code = jsonObject.get("code").toString();
            String msgid = jsonObject.get("msgId").toString();
            String error = jsonObject.get("errorMsg").toString();
            logger.info("状态码:" + code + ",状态码说明:" + error + ",消息id:" + msgid);
        } catch (Exception e){
            logger.error("请求异常：" + e);
        }
    }

    //发送邮件验证码
    public void sendEmail(String nationalCode, String email, String msg){
        SimpleMailUtil.EmailProperty info = new SimpleMailUtil.EmailProperty()
                .setMailFromAddress(fromAddress).setValidate(validate)
                .setMailUserName(userName).setMailPassword(userPassword)
                .setMailServerHost(serverHost).setMailServerPort(serverPort);
        String object = "BSTS商城验证码";
        SimpleMailUtil.sendTextMail(info, email, object, msg);
    }
    public void sendEmailRegister(String nationalCode, String email, String code){
        SimpleMailUtil.EmailProperty info = new SimpleMailUtil.EmailProperty()
                .setMailFromAddress(fromAddress).setValidate(validate)
                .setMailUserName(userName).setMailPassword(userPassword)
                .setMailServerHost(serverHost).setMailServerPort(serverPort);
        String object = "BSTS验证码";
        String content = "您好，\n" +
                "您正在注册 BSTS商城 账号。\n" +
                "【BSTS商城】安全验证："+code +"\n"+
                "妥善保管，请勿转发，5分钟内有效。\n\n";
        SimpleMailUtil.sendTextMail(info, email, object, content);
    }

/*    public static void main(String args[]){
        SmsService smsService = new SmsService();
        smsService.sendSms("86", "18720975730", "13012");
    }*/

}
