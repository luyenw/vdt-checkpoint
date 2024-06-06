package config

import (
	"sync"

	"github.com/redis/go-redis/v9"
)

var (
	instance   *redis.Client
	mu         sync.Mutex
	REDIS_HOST = "localhost:6379"
	REDIS_PWD  = "eYVX7EwVmmxKPCDmwMtyKVge8oLd2t81"
)

func getRedis() *redis.Client {
	return redis.NewClient(&redis.Options{
		Addr:     REDIS_HOST,
		Password: REDIS_PWD,
		DB:       1,
	})
}

func GetRedis() *redis.Client {
	mu.Lock()
	defer mu.Unlock()
	if instance == nil {
		instance = getRedis()
	}
	return instance
}
