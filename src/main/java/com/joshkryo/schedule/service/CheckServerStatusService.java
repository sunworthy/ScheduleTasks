package com.joshkryo.schedule.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

/**
 * Created by king on 30/09/2017.
 * 检查服务器状态
 */
public class CheckServerStatusService {
    private static final Logger log = LoggerFactory.getLogger(CheckServerStatusService.class);
    static CheckServerStatusService mInstance;
    CloseableHttpAsyncClient httpClient;
    MsgSenderService msgSenderService;
    private CheckServerStatusService(){
        msgSenderService = new MsgSenderService();
        try {
            httpClient = createHttpAsyncClient();
        } catch (IOReactorException e) {
            e.printStackTrace();
        }
    }
    public static CheckServerStatusService getInstence(){
        if(mInstance == null){
            synchronized (MsgSenderService.class){
                if(mInstance == null){
                    mInstance = new CheckServerStatusService();
                }
            }
        }
        return mInstance;
    }


    String api = "s?ie=utf-8&wd=test&p=";
    String jsonP = "jsonp_20170930";
    private String[] servers = new String[]{
            "https://www.baidu.com/",
    };

    /**
     * Check server status ,if response not 200 send message to me.
     */
    public void checkServerStatus(){
        if(httpClient == null){
            log.error("null httpClient");
            return;
        }
        for (int i = 0; i < servers.length; i++) {
            String serverName = servers[i];
            String url = serverName + api+jsonP;

            HttpGet httpGet = new HttpGet(url);
            httpClient.execute(httpGet, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(HttpResponse httpResponse) {
                    int statusCode = httpResponse.getStatusLine().getStatusCode();
                    log.info(serverName,"statusCode:{}",statusCode);
                    if(statusCode == 200){
                        log.info(serverName + ",running check pass !");
                    }else {
                        log.info(serverName + ",server is shutdown,please fix is as soon as possible!");
                        if(msgSenderService != null){
                            msgSenderService.sendAlertMsg(serverName);
                        }
                    }
                }

                @Override
                public void failed(Exception e) {
                    if(msgSenderService != null){
                        msgSenderService.sendAlertMsg(serverName);
                    }
                }

                @Override
                public void cancelled() {

                }
            });
        }
    }

    private CloseableHttpAsyncClient createHttpAsyncClient() throws IOReactorException {
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().
                setIoThreadCount(
                        Runtime.getRuntime().
                                availableProcessors()).
                setConnectTimeout(10000).
                setSoTimeout(30000).build();
        DefaultConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
        ConnectionConfig connectionConfig = ConnectionConfig.
                custom().
                setMalformedInputAction(CodingErrorAction.IGNORE).
                setUnmappableInputAction(CodingErrorAction.IGNORE).
                setCharset(Charset.forName("utf-8")).build();
        connManager.setDefaultConnectionConfig(connectionConfig);
        connManager.setMaxTotal(100);
        connManager.setDefaultMaxPerRoute(10);
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom().setConnectionManager(connManager).build();
        httpclient.start();
        return httpclient;
    }
}
