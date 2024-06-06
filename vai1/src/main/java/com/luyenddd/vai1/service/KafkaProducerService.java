package com.luyenddd.vai1.service;

import com.google.gson.Gson;
import com.luyenddd.vai1.entity.kafka.TransactionOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    public void sendMessage(TransactionOutMessage message){
        kafkaTemplate.send("out", new Gson().toJson(message));
    }
}
