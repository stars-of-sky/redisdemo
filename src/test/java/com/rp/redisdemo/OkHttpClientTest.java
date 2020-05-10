package com.rp.redisdemo;

import cn.hutool.json.JSONUtil;
import com.rp.redisdemo.entity.User;
import com.rp.redisdemo.interceptor.CacheInterceptor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Create By yrp.
 * Date:2020/5/10
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class OkHttpClientTest {
    private static int port = 80;
    private static String CONTEXT_PATH = "/mydemo";
    private String BASE_URL = "http://localhost:" + port + CONTEXT_PATH;
    private int cacheSize = 10 * 1024 * 1024; // 10 MiB

    private OkHttpClient okHttp = new OkHttpClient();
    private OkHttpClient okHttp1 = new OkHttpClient.Builder()
            .addNetworkInterceptor(new CacheInterceptor())//添加缓存拦截器
            .cache(new Cache(new File("F:/cache"), cacheSize)) //使用应用缓存文件路径，缓存大小为10MB
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Test
    public void getTest() {
        Request request = new Request.Builder()
                .url(BASE_URL + "/2")
                .get()//默认get可不写
                .build();
        final Call call = okHttp1.newCall(request);
        //异步调用示例
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.info("请求失败{}", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                log.info(response.body().string());
            }
        });
       /* try {
            //同步
            Response response = call.execute();
            //测试连接的取消
           *//*  long start = System.currentTimeMillis();
            while (true) {
                //1分钟获取不到结果就取消请求
                if (System.currentTimeMillis() - start > 1000) {
                    call.cancel();
                    System.out.println("task canceled");
                    break;
                }
            }*//*
            log.info(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Test
    public void putTest() {
        User user = User.builder().tel("18888888888").age(13).id(2L).build();
        MediaType JSON = MediaType.parse("application/json");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, JSONUtil.toJsonStr(user));
        Request request = new Request.Builder().url(BASE_URL + "/user").put(body).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                // ... handle failed request
            }
            String responseBody = response.body().string();
            // ... do something with response
            log.info("update success !");

        } catch (IOException e) {
            // ... handle IO exception
        }
    }

    @Test
    public void deleteTest() {
        Request request = new Request.Builder()
                .url(BASE_URL + "/user/1")
                .delete().build();
        try (Response response = okHttp.newCall(request).execute()) {
            log.info("delete success !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpload() throws IOException {
        String api = "/api/files/1";
        String url = String.format("%s%s", BASE_URL, api);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "docker_practice.pdf",
                        RequestBody.create(MediaType.parse("multipart/form-data"),
                                new File("C:/Users/Desktop/学习/docker_practice.pdf")))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)  //默认为GET请求，可以不写
                .build();
        final Call call = okHttp.newCall(request);
        Response response = call.execute();
        System.out.println(response.body().string());
    }
}
