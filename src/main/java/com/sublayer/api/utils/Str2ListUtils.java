package com.sublayer.api.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class Str2ListUtils {
    public static List<String> sliceString2StringArray(String content) {
        return sliceString2StringArray(content, ",");
    }

    public static List<String> sliceString2StringArray(String content, String sepatrator) {
        content = org.apache.commons.lang3.StringUtils.trim(content);
        if (org.apache.commons.lang3.StringUtils.isEmpty(content)) {
            return null;
        }
        if(StringUtils.isEmpty(sepatrator)) {
            return null;
        }
        String[] arr = content.split(sepatrator);
        if (arr != null && arr.length > 0) {
            return Arrays.asList(arr);
        }
        return null;
    }
}
