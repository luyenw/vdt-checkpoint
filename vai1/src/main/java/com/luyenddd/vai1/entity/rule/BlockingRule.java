package com.luyenddd.vai1.entity.rule;

import com.luyenddd.vai1.entity.kafka.TransactionInMessage;

public interface BlockingRule {
    boolean applyFilter(String in);
}
