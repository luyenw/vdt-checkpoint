package service

type IRule interface {
	applyRule(number string) bool
}

type ExceededOutgoingRule struct {
	redisService *RedisService
}

func NewExceededOutgoingRule(redisService *RedisService) *ExceededOutgoingRule {
	return &ExceededOutgoingRule{redisService: redisService}
}
func (e *ExceededOutgoingRule) applyRule(number string) bool {
	return true
}

type InBlackListRule struct {
	redisService *RedisService
}

func NewInBlackListRule(redisService *RedisService) *InBlackListRule {
	return &InBlackListRule{redisService: redisService}
}
func (i *InBlackListRule) applyRule(number string) bool {
	return true
}

type RuleService struct {
	rules []IRule
}

func NewRuleService() *RuleService {
	return &RuleService{}
}
func (r *RuleService) AddRule(rule IRule) {
	r.rules = append(r.rules, rule)
}
func (r *RuleService) ApplyRules(number string) bool {
	for _, rule := range r.rules {
		if rule.applyRule(number) {
			return true
		}
	}
	return false
}
func (r *RuleService) GetRules() []IRule {
	return r.rules
}
