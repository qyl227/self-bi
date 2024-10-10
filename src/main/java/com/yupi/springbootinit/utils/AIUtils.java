package com.yupi.springbootinit.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.AIConstant;
import com.yupi.springbootinit.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AIUtils {

    public static String doChat(String csvData) {
        return doChat(csvData, AIConstant.DEFAULT_AI_MODEL);
    }

    public static String doChat(String userContent, String model) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        Map<String, String> systemRuleMap = new HashMap<>();
        systemRuleMap.put("role", "system");
        systemRuleMap.put("content", AIConstant.SYSTEM_PRESET);
        Map<String, String> userContentMap = new HashMap<>();
        userContentMap.put("role", "user");
        userContentMap.put("content", userContent);
        requestBody.put("messages", Lists.newArrayList(systemRuleMap, userContentMap));
        String json = JSONUtil.toJsonStr(requestBody);
        log.debug(json);

        HttpResponse response = HttpRequest.post(AIConstant.AI_API_URL)
                .header("Authorization", AIConstant.SECRET_KEY)
                .body(json, "application/json")
                .execute(true);
//        log.debug(response.body());
        ThrowUtils.throwIf(!response.isOk(), ErrorCode.SYSTEM_ERROR, "AI调用失败");
        String answer = JSONUtil.parseObj(response.body()).getByPath("choices.message.content", String.class);
        return answer.substring(1, answer.length() - 1); // 去掉前后的 []（hutool解析json时自己补充的）
    }
}
