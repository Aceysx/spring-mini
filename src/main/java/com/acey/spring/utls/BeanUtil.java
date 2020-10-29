package com.acey.spring.utls;

import java.util.Objects;

public class BeanUtil {
    public static String lowerFirstCase(String string) {
        if (Objects.isNull(string)) {
            return null;
        }
        char[] chars = string.toCharArray();
        chars[0] += 32;
        return chars.toString();
    }
}
