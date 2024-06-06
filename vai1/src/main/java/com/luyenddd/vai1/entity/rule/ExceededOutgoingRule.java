package com.luyenddd.vai1.entity.rule;

import com.luyenddd.vai1.entity.kafka.TransactionInMessage;
import com.luyenddd.vai1.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExceededOutgoingRule implements BlockingRule {
    @Autowired
    private RedisService redisService;
    @Override
    public boolean applyFilter(String in) {

        return redisService.countOutgoingCalls(in) > 2;
    }
}
