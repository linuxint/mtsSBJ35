package com.devkbil.mtssbj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.devkbil.mtssbj.common.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;

public class JsonUtilTest {
    public static void main(String[] args) {
        toJsonInlcudeTest();
        toJsonTest();
    }

    public static void toJsonInlcudeTest() {
        List<Map<String, String>> testList = new ArrayList<>();
        Map<String, String> test = new HashMap<>();
        test.put("A", "1");
        test.put("B", "1");
        test.put("C", "1");
        testList.add(test);
        System.out.println(JsonUtil.toJsonInlcude(testList, "B")); // [{"B":"1"}]
        System.out.println(JsonUtil.toJson(testList)); // [{"A":"1","B":"1","C":"1"}]
    }

    public static void toJsonTest() {
        Car car = new Car();
        car.setName("차이름");
        car.setColor("차색상");
        System.out.println(JsonUtil.toJson(car)); // {"name":"차이름","color":"차색상"}
        System.out.println(JsonUtil.toJson(car, CarJsonView.class)); // {"name":"차이름"}
    }

    public static class Car {
        private String name;
        @Getter
        private String color;

        @JsonView(CarJsonView.class)
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    public static class CarJsonView {
    }
}
