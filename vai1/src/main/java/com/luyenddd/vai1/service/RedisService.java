package com.luyenddd.vai1.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class RedisService {
    @Data
    private static class CountEntry{
        private int value;
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Boolean isNumberInBlackList(String hashedNumber) {
        return redisTemplate.opsForSet().isMember("blacklist", hashedNumber);
    }

    public void increaseOutgoing(String hashedNumber){
        redisTemplate.opsForHash().increment("count", hashedNumber, 1);
    }

    public int countOutgoingCalls(String hashedNumber) {
        return Integer.parseInt((String) Objects.requireNonNull(redisTemplate.opsForHash().get("count", hashedNumber)), 10);
    }
}
