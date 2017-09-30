package com.joshkryo.schedule.service;

import com.joshkryo.schedule.config.ApiKeyConfig;
import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsSingleSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by king on 30/09/2017.
 */
public class MsgSenderService {
    private static final Logger log = LoggerFactory.getLogger(MsgSenderService.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    public void sendAlertMsg(String msg){
        YunpianClient clnt = new YunpianClient(ApiKeyConfig.YUN_PIAN).init();
        //初始化clnt,使用单例方式
        //发送短信API
        Map<String, String> param = clnt.newParam(2);
        param.put("mobile", "+8617190412166");
        param.put("text", "【亿网百汇】您的订单编号：1,物流信息："+msg+"检测到异常,请及时修复!,发现时间:"+dateFormat.format(new Date()));
        Result<SmsSingleSend> r = clnt.sms().single_send(param);
        log.info("yunPian:{}",r.toString());
        //获取返回结果，返回码:r.getCode(),返回码描述:r.getMsg(),API结果:r.getData(),其他说明:r.getDetail(),调用异常:r.getThrowable()
        //账户:clnt.user().* 签名:clnt.sign().* 模版:clnt.tpl().* 短信:clnt.sms().* 语音:clnt.voice().* 流量:clnt.flow().* 隐私通话:clnt.call().*
        //释放clnt
        clnt.close();
    }

}
