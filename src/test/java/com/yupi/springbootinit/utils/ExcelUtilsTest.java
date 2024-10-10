package com.yupi.springbootinit.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootTest
public class ExcelUtilsTest {
    @Test
    public void exceltoCsv() {
        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:test_excel.xlsx");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<Map<Integer, String>> list = EasyExcel.read(file)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet()
                .headRowNumber(0)
                .doReadSync();
        StringBuilder sb = new StringBuilder();
        list.forEach(sheet -> sb.append(StringUtils.join(sheet.values().stream().filter(Objects::nonNull).collect(Collectors.toList()), ",")).append("\n"));
        System.out.println(sb.toString());
    }
}
