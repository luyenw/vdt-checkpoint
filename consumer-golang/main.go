package main

import (
	"consumer-golang/config"
	"consumer-golang/controller"
	"consumer-golang/service"
	"sync"
)

func main() {
	consumeController := controller.NewConsumeController(
		service.NewKafkaService(config.GetConsumer(), config.GetProducer()),
		service.NewRedisService(config.GetRedis()),
		service.NewRuleService(),
	)
	var wg sync.WaitGroup
	wg.Add(1)
	consumeController.Consume(10)
	wg.Wait()
}
