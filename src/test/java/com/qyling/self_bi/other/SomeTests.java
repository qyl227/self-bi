package com.qyling.self_bi.other;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qyling
 * @date 2025/4/13 15:43
 */
public class SomeTests {
    AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) {
        System.out.println(32 - Integer.numberOfLeadingZeros(7));
    }

    public int numRabbits(int[] answers) {
        HashMap<Integer, Integer> map = new HashMap<>();

        int ans = 0;
        for (int i = 0; i < answers.length; i++) {
            int count = answers[i];
            map.compute(count + 1, (k, v) -> {
                if (v == null) return k - 1;
                if (v == 1) {
                    atomicInteger.addAndGet(k);
                    return null;
                } else return v - 1;
            });
        }
        for (Integer k : map.keySet()) {
            ans += map.get(k);
        }
        return ans;
    }
}
