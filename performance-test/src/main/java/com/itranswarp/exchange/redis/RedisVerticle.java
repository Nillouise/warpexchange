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

    ConcurrentHashMap<Integer, Long> begins = new ConcurrentHashMap<>();
    ConcurrentHashMap<Integer, Long> ends = new ConcurrentHashMap<>();

    ConcurrentHashMap<Integer, Queue<Integer>> periodIds = new ConcurrentHashMap<>();
    //默认一秒发一次
    int concurrentPeriod = 1000;
    //每个period发的次数
    int concurrentPerSecond = 100;
    AtomicInteger currentRequestId = new AtomicInteger(0);

    //    AtomicInteger currentPeriodId = new AtomicInteger(0);
    void logStatus() {
        int tot = 0;
        var list = Collections.list(ends.keys());
        list.sort(Comparator.reverseOrder());

        int cnt = 0;
        for (int i : list) {
            if (ends.get(i) != -1) {
                tot += ends.get(i) - begins.get(i);
            }
            if (cnt++ > 1000) {
                break;
            }
        }
        logger.info("recent {} request cost average {} ms", cnt, (double) tot / (double) cnt);
    }

    void periodConcurrent() {
        WebClient client = WebClient.create(vertx);
        WebClientOptions options = new WebClientOptions()
                .setUserAgent("My-App/1.2.3");
        options.setKeepAlive(false);
        long produceRequestTime = System.currentTimeMillis();
//        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < concurrentPerSecond; i++) {
            int finalId = currentRequestId.getAndAdd(1);
//            queue.add(finalId);
            begins.put(finalId, System.currentTimeMillis());
            HttpRequest<Buffer> localhost = client
                    .get(8000, "localhost", "/api/ticks");

            localhost.send()
                    .onSuccess(response -> {
//                        System.out
//                                .println("Received response with status code " + response.statusCode());
//                        System.out.println(response.body()
//                        );
                        long endTime = System.currentTimeMillis();
//                        logger.info("id {} pre {} end {}", finalI, begins.get(finalI), endTime);
                        ends.put(finalId, endTime);

                    })
                    .onFailure(err -> {
                        System.out.println("Something went wrong " + finalId + " " + err.getMessage());
                        ends.put(finalId, -1L);
                    });
        }
//        periodIds.put(currentPeriodId.getAndAdd(1), queue);
//        logger.info("producer request cost {} ms", System.currentTimeMillis() - produceRequestTime);
    }

    @Override
    public void start() throws Exception {
        vertx.setPeriodic(3000, id -> {
            logStatus();
        });

        vertx.setPeriodic(concurrentPeriod, id -> {
            periodConcurrent();
        });
    }

    public static void main(String[] args) {
        Vertx vert = Vertx.vertx();


        var push = new RedisVerticle();
        vert.deployVerticle(push);
    }


}
