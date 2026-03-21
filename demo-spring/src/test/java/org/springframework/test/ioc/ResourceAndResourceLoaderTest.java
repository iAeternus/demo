package org.springframework.test.ioc;

import cn.hutool.core.io.IoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.*;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ResourceAndResourceLoaderTest {

    @Test
    void test_resource_loader() throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();

        // 加载classpath下的资源
        Resource resource = resourceLoader.getResource("classpath:hello.txt");
        InputStream inputStream = resource.getInputStream();
        String content = IoUtil.readUtf8(inputStream);
        System.out.println(content);
        assertEquals("hello world", content);

        // 加载文件系统资源
        resource = resourceLoader.getResource("src/test/resources/hello.txt");
        assertInstanceOf(FileSystemResource.class, resource);
        inputStream = resource.getInputStream();
        content = IoUtil.readUtf8(inputStream);
        System.out.println(content);
        assertEquals("hello world", content);

        // 加载url资源
        resource = resourceLoader.getResource("https://www.baidu.com");
        assertInstanceOf(UrlResource.class, resource);
        inputStream = resource.getInputStream();
        content = IoUtil.readUtf8(inputStream);
        System.out.println(content);
    }

}
