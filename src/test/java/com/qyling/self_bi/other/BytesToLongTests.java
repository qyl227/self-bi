package com.qyling.self_bi.other;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BytesToLongTests {
    @Test
    void byteToLong() {
        byte[] bytes = {49, 56, 52, 56, 54, 57, 56, 52, 48, 56, 53, 56, 51, 55, 52, 49, 52, 52, 50};
        String.valueOf(bytes);
        Assertions.assertEquals(Long.valueOf(new String(bytes)), 1848698408583741442L);
    }
}
