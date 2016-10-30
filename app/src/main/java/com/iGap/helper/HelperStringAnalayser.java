package com.iGap.helper;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.view.View;
import com.iGap.activities.ActivityChat;

/**
 * Created by android3 on 10/24/2016.
 */

public class HelperStringAnalayser {

    public static SpannableStringBuilder analaysHash(String text, String messageID) {

        if (text == null) return null;
        if (text.length() == 0) return null;

        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        String s = "";
        String tmp = "";
        Boolean isHash = false;
        int start = 0;
        String enter = System.getProperty("line.separator");

        for (int i = 0; i < text.length(); i++) {

            s = text.substring(i, i + 1);
            if (s.equals("#")) {
                isHash = true;
                tmp = "";
                start = i;
                continue;
            }

            if (isHash) {
                if (s.equals("!")
                    || s.equals("@")
                    || s.equals("$")
                    || s.equals("%")
                    || s.equals("^")
                    || s.equals("&")
                    ||
                    s.equals("(")
                    || s.equals(")")
                    || s.equals("-")
                    || s.equals("+")
                    || s.equals("=")
                    || s.equals("!")
                    ||
                    s.equals("`")
                    || s.equals("{")
                    || s.equals("}")
                    || s.equals("[")
                    || s.equals("]")
                    || s.equals(";")
                    ||
                    s.equals(":")
                    || s.equals("'")
                    || s.equals("?")
                    || s.equals("<")
                    || s.equals(">")
                    || s.equals(",")
                    ||
                    s.equals("\\")
                    || s.equals("|")
                    || s.equals("//")
                    || s.codePointAt(0) == 8192
                    || s.equals(enter)) {

                    insertHashLink(tmp, builder, start, messageID);

                    tmp = "";
                    isHash = false;
                } else {
                    tmp += s;
                }
            }
        }

        if (isHash) {
            if (!tmp.equals("")) insertHashLink(tmp, builder, start, messageID);
        }

        return builder;
    }

    private static void insertHashLink(final String text, SpannableStringBuilder builder, int start,
        final String messageID) {

        builder.setSpan(new ClickableSpan() {
            @Override public void onClick(View arg0) {

                if (ActivityChat.hashListener != null) {
                    ActivityChat.hashListener.complete(true, text, messageID);
                }
            }
        }, start, start + text.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
