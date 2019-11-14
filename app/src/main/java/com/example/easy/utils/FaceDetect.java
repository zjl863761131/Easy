package com.example.easy.utils;

import com.example.easy.utils.GsonUtils;
import com.example.easy.utils.HttpUtil;

import java.util.*;

public class FaceDetect {
    private static String result;
    public static String faceDetect(final String assess_token,String img) throws InterruptedException {
        // 请求url

        Thread thread = new Thread() {
            @Override
            public void run() {
                String url = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
                try {
                    Map<String, Object> map = new HashMap<>();
                    map.put("image",img );
                    map.put("face_field", "beauty,age");
                    map.put("image_type", "BASE64");

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
