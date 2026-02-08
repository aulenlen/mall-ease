package com.mallease.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class NoGeneratorUtil {

    public static String generate(Long userId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String timestamp = LocalDateTime.now().format(formatter);
        String userIdSuffix = String.format("%04d", userId % 10000);
        String randomSuffix = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        return timestamp + userIdSuffix + randomSuffix;
    }
}
