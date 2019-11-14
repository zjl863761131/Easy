package com.example.easy.utils;

import com.example.easy.utils.GsonUtils;
import com.example.easy.utils.HttpUtil;

import java.lang.reflect.Array;
import java.util.*;

public class FaceMatch {

    private static String result;
    public static String faceMatch(final String assess_token, String img1, String img2) throws InterruptedException {
        // 请求url
        Thread thread = new Thread() {
            @Override
            public void run() {
                String url = "https://aip.baidubce.com/rest/2.0/face/v3/match";
                try {
                    List<Map<String, Object>> map = new ArrayList<>();
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("image", img1);
                    map1.put("image_type", "BASE64");
                    map1.put("face_type", "LIVE");
                    map1.put("quality_control", "LOW");
                    map1.put("liveness_control", "HIGH");
                    Map<String, Object> map2 = new HashMap<>();
                    map2.put("image", img2);
                    map2.put("image_type", "BASE64");
                    map2.put("face_type", "LIVE");
                    map2.put("quality_control", "LOW");
                    map2.put("liveness_control", "HIGH");

                    map.add(map1);
                    map.add(map2);
                    String param = GsonUtils.toJson(map);

                    // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
                    String accessToken = assess_token;

                    result = HttpUtil.post(url, accessToken, "application/json", param);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();
        thread.join();
        return result;
    }
}