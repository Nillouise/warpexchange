package com.itranswarp.exchange.redis;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class RedisVerticle extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    //每个请求的开始时间
    ConcurrentHashMap<Integer, Long> begins = new ConcurrentHashMap<>();

    //每个请求的结束时间，-1代表请求出错
    ConcurrentHashMap<Integer, Long> ends = new ConcurrentHashMap<>();

    //默认一秒为一个period，每个period开始，利用vert发出所有请求。
    int concurrentPeriodTime = 1000;
    //每个period发的次数，即并发数
    int concurrentPerPeriod = 500;
    AtomicInteger currentRequestId = new AtomicInteger(0);

    void logRequestStatus() {
        int tot = 0;
        var list = Collections.list(ends.keys());
        list.sort(Comparator.reverseOrder());

        int cnt = 0;
        for (int i : list) {
            if (ends.get(i) != -1) {
                tot += ends.get(i) - begins.get(i);
            }
            if (++cnt >= 1000) {
                break;
            }
        }
        logger.info("recent {} requests cost average {} ms, {} requests complete", cnt, (double) tot / cnt, list.size());
    }

    void periodConcurrent() {
        WebClient client = WebClient.create(vertx);
        WebClientOptions options = new WebClientOptions()
                .setUserAgent("My-App/1.2.3");
        options.setKeepAlive(false);
        long produceRequestTime = System.currentTimeMillis();
        for (int i = 0; i < concurrentPerPeriod; i++) {
            int finalId = currentRequestId.getAndAdd(1);
            begins.put(finalId, System.currentTimeMillis());
            HttpRequest<Buffer> localhost = client
                    .get(8000, "localhost", "/api/ticks");

            localhost.send()
                    .onSuccess(response -> {
                        long endTime = System.currentTimeMillis();
                        ends.put(finalId, endTime);

                    })
                    .onFailure(err -> {
                        logger.error("Something went wrong " + finalId + " " + err.getMessage());
                        ends.put(finalId, -1L);
                    });
        }
        logger.info("produce request cost {} ms", System.currentTimeMillis() - produceRequestTime);
    }

    @Override
    public void start() throws Exception {
        vertx.setPeriodic(3000, id -> {
            logRequestStatus();
        });

        vertx.setPeriodic(concurrentPeriodTime, id -> {
            periodConcurrent();
        });
    }

    public static void main(String[] args) {
        Vertx vert = Vertx.vertx();
        var push = new RedisVerticle();
        vert.deployVerticle(push);
    }


}
