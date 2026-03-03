package com.ricky;

import java.util.ArrayList;
import java.util.List;

public class TestGc2 {

    static List<byte[]> list = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            list.add(new byte[1024 * 1024]);
            Thread.sleep(1000);
        }
    }

}
