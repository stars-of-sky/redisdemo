package com.rp.redisdemo;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.rp.redisdemo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


/**
 * Create By yrp.
 * Date:2020/5/10
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.properties")
public class HttpClientTest {

    @Value("${server.port}")
    private static int port = 80;
    @Value("${server.servlet.context-path}")
    private static String CONTEXT_PATH = "/mydemo";
    @Value("${httpclient.socketTimeout}")
    private int socketTimeout = 5;
    @Value("${httpclient.connectionTimeout}")
    private int connectionTimeout = 60;

    private String BASE_URL = "http://localhost:" + port + CONTEXT_PATH;
    private CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(socketTimeout * 1000)
            .setConnectTimeout(connectionTimeout * 1000)
            .build();

    @Test
    public void postHttp() {
        String api = "/user/save";
        String url = String.format("%s%s", BASE_URL, api);
        HttpPost httpPost = new HttpPost(url);
        User user = User.builder().age(12).name("语沫").tel("153535353535").build();
        httpPost.setHeader("Content-Type", String.valueOf(ContentType.APPLICATION_JSON));
        httpPost.setEntity(new StringEntity(JSONUtil.toJsonStr(user), ContentType.APPLICATION_JSON));
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            log.info(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getTest() {
        Map<String, Object> param = new HashMap<>();
        param.put("start", 0);
        param.put("size", 6);
        log.info("paprm:{}", param);
        //Map转为URL参数字符串
        //url = url + HttpUtil.toParams(param);
        //或者
        String url = "/user/users";
        url = HttpUtil.urlWithForm(url, param, Charset.forName("utf-8"), true);
        HttpGet httpGet = new HttpGet(BASE_URL + url);
        try {
            //测试连接的取消
            CloseableHttpResponse response = httpClient.execute(httpGet);
//            long begin = System.currentTimeMillis();
//            while (true) {
//                if (System.currentTimeMillis() - begin > 1000) {
//                    httpGet.releaseConnection();
//                    log.info("task canceled");
//                    break;
//                }
//            }
            log.info(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
