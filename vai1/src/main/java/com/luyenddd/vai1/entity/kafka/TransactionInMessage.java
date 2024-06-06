package com.luyenddd.vai1.entity.kafka;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@NoArgsConstructor
@Data
public class TransactionInMessage {
    @SerializedName("id")
    private String id;
    @SerializedName("ma_nhan")
    private String receiveId;
    @SerializedName("ma_gui")
    private String sendId;
    @SerializedName("thoi_luong_cuoc_goi")
    private int duration;
    @SerializedName("thoi_gian_goi")
    private Date timestamp;
}
