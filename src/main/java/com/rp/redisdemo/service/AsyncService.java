package com.rp.redisdemo.service;
/**
 * Description:  演示异步执行服务
 * Create By yrp.
 * Date:2020/5/31
 * Version: 1.0
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AsyncService {

    private List<String> movies = new ArrayList<>(
            Arrays.asList(
                    "Forrest Gump",
                    "Titanic",
                    "Spirited Away",
                    "The Shawshank Redemption",
                    "Zootopia",
                    "Farewell ",
                    "Joker",
                    "Crawl"));

    /**
     * 示范使用：找到特定字符/字符串开头的电影
     * 这个方法的返回值 CompletableFuture.completedFuture(results)这代表我们需要返回结果，也就是说程序必须把任务执行完成之后再返回给用户。
     */
    @Async
    public CompletableFuture<List<String>> completableFutureTask(String start) {
        log.info("{} start this task", Thread.currentThread().getName());
        // 找到特定字符/字符串开头的电影
        List<String> results = movies.stream()
                .filter(movie -> movie.startsWith(start)).collect(Collectors.toList());
        // 模拟这是一个耗时的任务
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //返回一个已经用给定值完成的新的CompletableFuture。
        return CompletableFuture.completedFuture(results);
    }
}
