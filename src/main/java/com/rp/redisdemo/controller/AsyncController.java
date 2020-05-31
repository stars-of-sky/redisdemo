package com.rp.redisdemo.controller;

import com.rp.redisdemo.service.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Description:
 * Create By yrp.
 * Date:2020/5/31
 * Version: 1.0
 */

@Slf4j
@RestController
@RequestMapping("/async")
public class AsyncController {
    @Autowired
    AsyncService asyncService;

    @GetMapping("/movies")
    public String completableFutureTask() {
        long start = System.currentTimeMillis();
        // 开始执行大量的异步任务
        List<String> words = Arrays.asList("F", "T", "S", "Z", "J", "C");
        List<CompletableFuture<List<String>>> completableFuture = words.stream()
                .map(word -> asyncService.completableFutureTask(word))
                .collect(Collectors.toList());
        // CompletableFuture.join（）方法可以获取他们的结果并将结果连接起来
        List<List<String>> lists = completableFuture.stream().map(CompletableFuture::join).collect(Collectors.toList());
        log.info("end cost time: {}", System.currentTimeMillis() - start);
        return lists.toString();
    }
}
