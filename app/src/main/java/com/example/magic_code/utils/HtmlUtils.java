package com.example.magic_code.utils;

import android.text.Html;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtils {
    public static String decodeHtml(String encodedHtml) {
        Pattern pattern = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher matcher = pattern.matcher(encodedHtml);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            char ch = (char) Integer.parseInt(matcher.group(1), 16);
            matcher.appendReplacement(sb, String.valueOf(ch));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}