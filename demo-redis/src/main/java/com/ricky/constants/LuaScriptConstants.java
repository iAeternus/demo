package com.ricky.constants;

public interface LuaScriptConstants {

    // 返回第一个KEY和第一个ARGV的值
    String HELLO_LUA_SCRIPT = """
            return {KEYS[1], ARGV[1]}
            """;

}
