package com.qyling.self_bi.manager;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.qyling.self_bi.common.ErrorCode;
import com.qyling.self_bi.constant.AIConstant;
import com.qyling.self_bi.exception.BusinessException;
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

    public String doChat(String aiModel, String secretKey, String apiUrl, String userContent) {
        // 封装请求
        Map<String, Object> requestBody = new HashMap<>();
        // 用户指定的模型优先
        if (StrUtil.isBlank(aiModel)) aiModel = aiServiceProperties.getModel();
        requestBody.put("model", aiModel);
        // 预设
        Map<String, String> systemRuleMap = new HashMap<>();
        systemRuleMap.put("role", "system");
        // 封装后的用户内容
        systemRuleMap.put("content", AIConstant.SYSTEM_PRESET);
        Map<String, String> userContentMap = new HashMap<>();
        userContentMap.put("role", "user");
        userContentMap.put("content", userContent);
        requestBody.put("messages", Lists.newArrayList(systemRuleMap, userContentMap));
        String json = JSONUtil.toJsonStr(requestBody);
        log.debug(json);

        // 密钥
        if (StrUtil.isBlank(secretKey)) secretKey = aiServiceProperties.getSecretKey();
        if (secretKey != null && !secretKey.startsWith("Bearer")) secretKey = "Bearer " + secretKey;
        // 服务商地址
        if (StrUtil.isBlank(apiUrl)) apiUrl = aiServiceProperties.getApiUrl();

        // 发起请求
        HttpResponse response = HttpRequest.post(apiUrl)
                .header("Authorization", secretKey)
                .body(json, "application/json")
                .execute(false);

        if (!response.isOk()) {
            log.error(response.body());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI调用失败");
        }
        // 解析响应
        String answer = JSONUtil.parseObj(response.body()).getByPath("choices.message.content", String.class);
        return answer.substring(1, answer.length() - 1); // 去掉前后的 []（hutool解析json时自己补充的）
    }

    public String doChat(String userContent) {
        return doChat(null, null, null, userContent);
    }
}
