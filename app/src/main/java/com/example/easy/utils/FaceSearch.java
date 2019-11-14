package com.example.easy.utils;

import com.example.easy.utils.GsonUtils;
import com.example.easy.utils.HttpUtil;

import java.util.*;

/**
 * 人脸搜索
 */
public class FaceSearch {

    private static String result;

    public static String faceSearch(final String assess_token, String img) throws InterruptedException {
        // 请求url
        Thread thread = new Thread() {
            @Override
            public void run() {
                String url = "https://aip.baidubce.com/rest/2.0/face/v3/multi-search";
                try {
                    Map<String, Object> map = new HashMap<>();
                    map.put("image", img);
                    map.put("liveness_control", "NORMAL");
                    map.put("image_type", "BASE64");
                    map.put("group_id_list", "zjl");
                    map.put("quality_control", "LOW");
                    map.put("max_face_num", 10);
                    map.put("match_threshold", 10);
                    map.put("max_user_num", 1);

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