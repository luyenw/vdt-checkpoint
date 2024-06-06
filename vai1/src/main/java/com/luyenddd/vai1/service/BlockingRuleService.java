package com.luyenddd.vai1.service;

import com.luyenddd.vai1.entity.rule.BlockingRule;
import com.luyenddd.vai1.entity.kafka.TransactionInMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class BlockingRuleService {
    private final List<BlockingRule> rules;

    @Autowired
    public BlockingRuleService(List<BlockingRule> rules) {
        System.out.println(rules);
        this.rules = rules;
    }
    private final Logger logger = Logger.getLogger(String.valueOf(this.getClass()));
    public boolean applyAllRules(String maGui) {
        for (BlockingRule rule : rules) {
            if (rule.applyFilter(maGui)) {
                logger.log(Level.INFO, "Blocked "+maGui);
                return true;
            }
        }
        return false;
    }
}
