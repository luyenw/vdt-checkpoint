package com.luyenddd.vai1.entity.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TransactionOutMessage {
    private String id;
    private int block;
}
