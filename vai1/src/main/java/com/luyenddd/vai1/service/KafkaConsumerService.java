package com.luyenddd.vai1.service;

import com.google.gson.Gson;
import com.luyenddd.vai1.entity.kafka.TransactionInMessage;
import com.luyenddd.vai1.entity.kafka.TransactionOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class KafkaConsumerService {
    @Autowired
    private KafkaProducerService producerService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private BlockingRuleService blockingRuleService;
    int numberOfCores = Runtime.getRuntime().availableProcessors();
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfCores);
    @KafkaListener(topics = "in", groupId = "worker_17", containerFactory = "kafkaListenerContainerFactory")
    public void consume(List<String> messages){
        Runnable runnable = () -> {
            for(String message: messages) {
                Gson gson = new Gson();
                TransactionInMessage in = gson.fromJson(message, TransactionInMessage.class);
                redisService.increaseOutgoing(in.getSendId());

                if (blockingRuleService.applyAllRules(in.getSendId())) {
                    TransactionOutMessage out = new TransactionOutMessage();
                    out.setId(in.getId());
                    out.setBlock(1);
                    producerService.sendMessage(out);
                }
            }
        };

        executorService.execute(runnable);
    }
}