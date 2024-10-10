package com.yupi.springbootinit.model.pojo;

import lombok.Data;

@Data
public class AIMessage {
    private String role;
    private String content;

    public AIMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
