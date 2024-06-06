package service

import (
	"context"
	"strconv"

	"github.com/redis/go-redis/v9"
)

type RedisService struct {
	cli *redis.Client
}

func NewRedisService(cli *redis.Client) *RedisService {
	return &RedisService{cli: cli}
}

func (k *RedisService) isNumberInBlackList(number string) (bool, error) {
	res := k.cli.SIsMember(context.Background(), "blacklist", number)
	if res.Err() != nil {
		return false, res.Err()
	}
	return res.Val(), nil
}

func (k *RedisService) increaseOutgoing(number string) (int64, error) {
	res := k.cli.HIncrBy(context.Background(), "count", number, 1)
	if res.Err() != nil {
		return 0, res.Err()
	}
	return res.Val(), nil
}

func (k *RedisService) countOutgoingCalls(number string) (int64, error) {
	res := k.cli.HGet(context.Background(), "count", number)
	if res.Err() != nil {
		return 0, res.Err()
	}
	count, err := strconv.ParseInt(res.Val(), 10, 64)
	if err != nil {
		return 0, err
	}
	return count, nil
}
