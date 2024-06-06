package controller

import (
	"consumer-golang/entity"
	"consumer-golang/service"
	"fmt"
)

type ConsumeController struct {
	kafkaService *service.KafkaService
	redisService *service.RedisService
	ruleService  *service.RuleService
}

func NewConsumeController(kafkaService *service.KafkaService, redisService *service.RedisService, ruleService *service.RuleService) *ConsumeController {
	return &ConsumeController{
		kafkaService: kafkaService,
		redisService: redisService,
		ruleService:  ruleService,
	}
}

func (c *ConsumeController) Consume(workers int) {
	fmt.Printf("Consuming messages with %d workers\n", workers)
	c.ruleService.AddRule(service.NewInBlackListRule(c.redisService))
	c.ruleService.AddRule(service.NewExceededOutgoingRule(c.redisService))
	fmt.Printf("Rules: %+v\n", c.ruleService.GetRules())

	consumer_queue := make(chan *entity.KafkaMessage, 4096)
	go func() {
		// for i := 0; i < workers; i++ {
		// go func() {
		for {
			message, err := c.kafkaService.ConsumeMessage()
			if err == nil {
				// fmt.Printf("%+v", message)
				consumer_queue <- message
			}
		}
		// }()
		// }
	}()

	for i := 0; i < workers; i++ {
		go func() {
			for message := range consumer_queue {
				if c.ruleService.ApplyRules(message.SendId) {
					// fmt.Printf("Blocked message %s\n", message.Id)
					go c.kafkaService.SendMessage("out", []byte(message.Id))
				} else {
					// fmt.Printf("Message from %s is allowed\n", message.Id)
				}
			}
		}()
	}
}
