package com.ricky.lua;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@SpringBootTest
public class LuaScriptTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisScript<List> helloScript;

    @Autowired
    private RedisScript<Long> incrScript;

    @Autowired
    private RedisScript<Long> unlockScript;

    @Test
    void test_hello_lua_script() {
        List result = stringRedisTemplate.execute(
                helloScript,
                List.of("Hello"),
                "World"
        );

        assertIterableEquals(List.of("Hello", "World"), result);
    }

    @Test
    void test_incr_lua_script() {
        Long result1 = stringRedisTemplate.execute(
                incrScript,
                List.of("counter"),
                "60" // 过期时间60s
        );

        Long result2 = stringRedisTemplate.execute(
                incrScript,
                List.of("counter"),
                "60"
        );

        assertEquals(1, result1);
        assertEquals(2, result2);
    }

    @Test
    void test_unlock_lua_script() {
        String key = "lock:test";
        String uuid = UUID.randomUUID().toString();

        // 上锁
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent(key, uuid, Duration.ofSeconds(30));
        assertEquals(Boolean.TRUE, lock);

        // 解锁
        Long result = stringRedisTemplate.execute(
                unlockScript,
                List.of(key),
                uuid
        );
        assertEquals(1L, result);
    }

    // TODO 限流、库存扣减

}
