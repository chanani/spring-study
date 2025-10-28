local key1 = KEYS[1]
local key2 = KEYS[2]
local resultKey = KEYS[3]

local value = tonumber(redis.call('GET', key1) or 0)
local value2 = tonumber(redis.call('GET', key2) or 0)

local result = value + value2
redis.call('SET', resultKey, result)

return result