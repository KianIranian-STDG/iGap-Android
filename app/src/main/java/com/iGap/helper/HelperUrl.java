package com.iGap.helper;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import com.iGap.G;
import com.iGap.activities.ActivityChat;
import com.iGap.activities.ActivityWebView;
import com.iGap.module.SHP_SETTING;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by android3 on 11/26/2016.
 */

public class HelperUrl {


////**********************************************************************************************************
//
//
//        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
//        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
//
////**********************************************************************************************************
//
//    // NOTES:   1) \w includes 0-9, a-z, A-Z, _
////          2) The leading '-' is the '-' character. It must go first in character class expression
//    private static final String VALID_CHARS = "-\\w+&@#/%=~()|";
//    private static final String VALID_NON_TERMINAL = "?!:,.;";
//
//    // Notes on the expression:
////  1) Any number of leading '(' (left parenthesis) accepted.  Will be dealt with.
////  2) s? ==> the s is optional so either [http, https] accepted as scheme
////  3) All valid chars accepted and then one or more
////  4) Case insensitive so that the scheme can be hTtPs (for example) if desired
//    private static final Pattern URI_FINDER_PATTERN = Pattern.compile("\\(*https?://["+ VALID_CHARS + VALID_NON_TERMINAL + "]*[" +VALID_CHARS + "]",
//            Pattern.CASE_INSENSITIVE );
////**********************************************************************************************************

    public static int LinkColor = Color.CYAN;

    public static SpannableStringBuilder setUrlLink(String text, boolean withClickable, boolean withHash, String messageID, boolean withAtSign) {

        if (text == null)
            return null;

        if (text.trim().length() < 1)
            return null;

        SpannableStringBuilder strBuilder = new SpannableStringBuilder(text);

        if (withAtSign) strBuilder = analaysAtSign(strBuilder);

        if (withHash)
            strBuilder = analaysHash(strBuilder, messageID);

        Pattern urlPattern = Pattern.compile("(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)" + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

        Matcher matcher = urlPattern.matcher(text);
        while (matcher.find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();
            setLinkSpan(strBuilder, matchStart, matchEnd, withClickable);
        }


        return strBuilder;
    }

    private static void setLinkSpan(final SpannableStringBuilder strBuilder, final int start, final int end, final boolean withclickable) {


        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                if (withclickable) {

                    boolean openLocalWebPage;
                    SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, G.context.MODE_PRIVATE);

                    int checkedInappBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 0);

                    if (checkedInappBrowser == 1) {
                        openLocalWebPage = true;
                    } else {
                        openLocalWebPage = false;
                    }

                    String url = strBuilder.toString().substring(start, end);

                    if (!url.startsWith("https://") && !url.startsWith("http://")) {
                        url = "http://" + url;
                    }

                    if (openLocalWebPage) {

                        Intent intent = new Intent(G.context, ActivityWebView.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("PATH", url);
                        G.context.startActivity(intent);
                        try {
                            G.context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Log.e("ddd", "can not open url");
                        }
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse(url));

                        try {
                            G.context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Log.e("ddd", "can not open url");
                        }
                    }

                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.linkColor = LinkColor;
                super.updateDrawState(ds);
            }

        };

        strBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    //*********************************************************************************************************

    private static SpannableStringBuilder analaysHash(SpannableStringBuilder builder, String messageID) {

        if (builder == null)
            return builder;

        String text = builder.toString();

        if (text.length() < 1)
            return builder;

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
                if (s.equals("!") || s.equals("@") || s.equals("$") || s.equals("%") || s.equals("^") || s.equals("&") ||
                        s.equals("(") || s.equals(")") || s.equals("-") || s.equals("+") || s.equals("=") || s.equals("!") ||
                        s.equals("`") || s.equals("{") || s.equals("}") || s.equals("[") || s.equals("]") || s.equals(";") ||
                        s.equals(":") || s.equals("'") || s.equals("?") || s.equals("<") || s.equals(">") || s.equals(",") || s.equals(" ") ||
                        s.equals("\\") || s.equals("|") || s.equals("//") || s.codePointAt(0) == 8192 || s.equals(enter) || s.equals("")) {

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

    private static void insertHashLink(final String text, SpannableStringBuilder builder, int start, final String messageID) {


        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (ActivityChat.hashListener != null) {
                    ActivityChat.hashListener.complete(true, text, messageID);
                }
            }

            @Override public void updateDrawState(TextPaint ds) {
                ds.linkColor = LinkColor;
                super.updateDrawState(ds);
            }
        };

        builder.setSpan(clickableSpan, start, start + text.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    //*********************************************************************************************************

    private static SpannableStringBuilder analaysAtSign(SpannableStringBuilder builder) {

        if (builder == null) return builder;

        String text = builder.toString();

        if (text.length() < 1) return builder;

        String s = "";
        String tmp = "";
        Boolean isAtSign = false;
        int start = 0;
        String enter = System.getProperty("line.separator");

        for (int i = 0; i < text.length(); i++) {

            s = text.substring(i, i + 1);
            if (s.equals("@")) {
                isAtSign = true;
                tmp = "";
                start = i;
                continue;
            }

            if (isAtSign) {
                if (s.equals("!") || s.equals("#") || s.equals("$") || s.equals("%") || s.equals("^") || s.equals("&") ||
                    s.equals("(") || s.equals(")") || s.equals("-") || s.equals("+") || s.equals("=") || s.equals("!") ||
                    s.equals("`") || s.equals("{") || s.equals("}") || s.equals("[") || s.equals("]") || s.equals(";") ||
                    s.equals(":") || s.equals("'") || s.equals("?") || s.equals("<") || s.equals(">") || s.equals(",") || s.equals(" ") ||
                    s.equals("\\") || s.equals("|") || s.equals("//") || s.codePointAt(0) == 8192 || s.equals(enter) || s.equals("")) {

                    insertAtSignLink(tmp, builder, start);

                    tmp = "";
                    isAtSign = false;
                } else {
                    tmp += s;
                }
            }
        }

        if (isAtSign) {
            if (!tmp.equals("")) insertAtSignLink(tmp, builder, start);
        }

        return builder;
    }

    private static void insertAtSignLink(final String text, SpannableStringBuilder builder, int start) {

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override public void onClick(View widget) {

                Log.e("ddd", text);
            }

            @Override public void updateDrawState(TextPaint ds) {
                ds.linkColor = LinkColor;
                super.updateDrawState(ds);
            }

        };


        builder.setSpan(clickableSpan, start, start + text.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}
