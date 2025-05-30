package com.qyling.self_bi.manager;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.qyling.self_bi.common.ErrorCode;
import com.qyling.self_bi.constant.AIConstant;
import com.qyling.self_bi.exception.ThrowUtils;
import com.qyling.self_bi.model.properties.AIServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * ai服务中间件
 */
@Service
@Slf4j
public class AIManager {

    @Resource
    private AIServiceProperties aiServiceProperties;

    public String doChat(String userContent) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiServiceProperties.getModel());
        Map<String, String> systemRuleMap = new HashMap<>();
        systemRuleMap.put("role", "system");
        systemRuleMap.put("content", AIConstant.SYSTEM_PRESET);
        Map<String, String> userContentMap = new HashMap<>();
        userContentMap.put("role", "user");
        userContentMap.put("content", userContent);
        requestBody.put("messages", Lists.newArrayList(systemRuleMap, userContentMap));
        String json = JSONUtil.toJsonStr(requestBody);
        log.debug(json);

        HttpResponse response = HttpRequest.post(aiServiceProperties.getApiUrl())
                .header("Authorization", aiServiceProperties.getSecretKey())
                .body(json, "application/json")
                .execute(false);
        log.debug(response.body());
        ThrowUtils.throwIf(!response.isOk(), ErrorCode.SYSTEM_ERROR, "AI调用失败");
        String answer = JSONUtil.parseObj(response.body()).getByPath("choices.message.content", String.class);
        return answer.substring(1, answer.length() - 1); // 去掉前后的 []（hutool解析json时自己补充的）
    }
}
