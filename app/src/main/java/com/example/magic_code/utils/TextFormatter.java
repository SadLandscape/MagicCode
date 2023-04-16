package com.example.magic_code.utils;

import java.util.regex.Pattern;

public class TextFormatter {
    private static final Pattern CODE_PATTERN = Pattern.compile("```(.*?)```");

    private static final Pattern BOLD_PATTERN = Pattern.compile("\\*(.*?)\\*");

    private static final Pattern ITALIC_PATTERN = Pattern.compile("_(.*?)_");

    public static String formatText(String text) {
        text = CODE_PATTERN.matcher(text).replaceAll("<pre><code>$1</code></pre>");

        text = BOLD_PATTERN.matcher(text).replaceAll("<b>$1</b>");

        text = ITALIC_PATTERN.matcher(text).replaceAll("<i>$1</i>");

        return text.replaceAll("\"","");
    }
    public static String formatTextWithHtml(String text,Boolean editable) {
        text = CODE_PATTERN.matcher(text).replaceAll("<pre><code>$1</code></pre>");

        text = BOLD_PATTERN.matcher(text).replaceAll("<b>$1</b>");

        text = ITALIC_PATTERN.matcher(text).replaceAll("<i>$1</i>");

        return ("<html><head><style type=\"text/css\">* {padding:0px; margin:0px;}</style></head><body contentEditable=\""+editable+"\" style=\"background-color: transparent;\">"+text.replaceAll("\"","")+"</body></html>");
    }

    public static String convertToRawText(String text) {
        text = text.replaceAll("<pre><code>(.*?)</code></pre>", "```$1```");

        text = text.replaceAll("<b>(.*?)</b>", "*$1*");

        text = text.replaceAll("<i>(.*?)</i>", "_$1_");

        return text.replaceAll("\"","");
    }
    public static String convertToRawTextWithHtml(String text,Boolean editable) {
        text = text.replaceAll("<pre><code>(.*?)</code></pre>", "```$1```");

        text = text.replaceAll("<b>(.*?)</b>", "*$1*");

        text = text.replaceAll("<i>(.*?)</i>", "_$1_");

        return ("<html><head><style type=\"text/css\">* {padding:0px; margin:0px;}</style></head><body contentEditable=\""+editable+"\" style=\"background-color: transparent;\">"+text.replaceAll("\"","")+"</body></html>");
    }
}