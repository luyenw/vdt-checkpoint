package service

import (
	"consumer-golang/entity"
	"encoding/json"

	"github.com/confluentinc/confluent-kafka-go/v2/kafka"
)

type KafkaService struct {
	c *kafka.Consumer
	p *kafka.Producer
}

func NewKafkaService(c *kafka.Consumer, p *kafka.Producer) *KafkaService {
	return &KafkaService{c: c, p: p}
}

func (k *KafkaService) ConsumeMessage() (*entity.KafkaMessage, error) {
	msg, err := k.c.ReadMessage(-1)
	if err == nil {
		message := &entity.KafkaMessage{}
		err := json.Unmarshal(msg.Value, message)
		if err != nil {
			return nil, err
		}
		return message, nil
	} else {
		// fmt.Printf("Consumer error: %v (%v)\n", err, msg)
		return nil, err
	}
}

func (k *KafkaService) SendMessage(topic string, value []byte) error {
	return k.p.Produce(&kafka.Message{
		TopicPartition: kafka.TopicPartition{Topic: &topic, Partition: kafka.PartitionAny},
		Value:          value,
	}, nil)
}
