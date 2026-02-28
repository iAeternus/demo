package com.ricky.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class Student {
    private String name;
    private String[] hobbies;
    private Map<String, Integer> scores;
    private BirthPlace birthPlace;

    @Data
    @AllArgsConstructor
    public static class BirthPlace {
        private String country;
        private String city;
    }

    public static Student test() {
        String[] hobbies = {"原神", "rust"};
        HashMap<String, Integer> scores = new HashMap<>();
        scores.put("语文", 85);
        scores.put("数学", 99);
        BirthPlace birthPlace = new BirthPlace("中国", "武汉");
        return new Student("zhangSan", hobbies, scores, birthPlace);
    }
}