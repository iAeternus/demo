local current = redis.call("GET", KEYS[1])
if not current then
    -- 键不存在，初始化为1，设置过期时间（ARGV[1]秒）
    redis.call("SET", KEYS[1], 1, "EX", ARGV[1])
    return 1 -- 返回初始值
else
    -- 键存在，递增并返回新值
    return redis.call("INCR", KEYS[1])
end