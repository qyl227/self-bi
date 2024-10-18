package com.yupi.springbootinit.other;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.ThrowUtils;

import java.util.concurrent.CompletableFuture   ;

public class CompletableFutureTests {
    public static void main(String[] args) {
        CompletableFuture.runAsync(() -> {
            try {
                ThrowUtils.throwIf(true, ErrorCode.SYSTEM_ERROR);  // 抛出异常
            } catch (Exception e) {
                System.out.println(1);
                throw new RuntimeException(e);
            }
        });

//        CompletableFuture.runAsync(() -> {
//            try {
//                ThrowUtils.throwIf(true, ErrorCode.SYSTEM_ERROR);  // 抛出异常
//            } catch (Exception e) {
//                System.out.println(1);
//                throw new RuntimeException(e);
//
//            }
//        }).exceptionally(e -> {
//            // 捕获异常
//            System.out.println("Exception caught: " + e.getMessage());
//            return null;  // 因为是 runAsync，返回 null 表示不需要结果
//        });
    }
}
