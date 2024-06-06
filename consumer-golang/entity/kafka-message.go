package entity

type KafkaMessage struct {
	Id        string `json:"id"`
	SendId    string `json:"ma_gui"`
	ReceiveId string `json:"ma_nhan"`
	Duration  int    `json:"thoi_luong_cuoc_goi"`
	Timestamp int64  `json:"thoi_gian_cuoc_goi"`
}
