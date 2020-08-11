/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.helper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityEnhanced;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.fragments.BottomNavigationFragment;
import net.iGap.fragments.FragmentAddContact;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentContactsProfile;
import net.iGap.fragments.discovery.DiscoveryFragment;
import net.iGap.libs.Tuple;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.dialog.BottomSheetItemClickCallback;
import net.iGap.module.dialog.JoinDialogFragment;
import net.iGap.module.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.module.structs.StructMessageOption;
import net.iGap.observers.interfaces.OnChatGetRoom;
import net.iGap.observers.interfaces.OnClientCheckInviteLink;
import net.iGap.observers.interfaces.OnClientGetRoomResponse;
import net.iGap.observers.interfaces.OnClientJoinByInviteLink;
import net.iGap.observers.interfaces.OnClientResolveUsername;
import net.iGap.proto.ProtoClientGetRoom;
import net.iGap.proto.ProtoClientResolveUsername;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestClientCheckInviteLink;
import net.iGap.request.RequestClientGetRoom;
import net.iGap.request.RequestClientGetRoomHistory;
import net.iGap.request.RequestClientJoinByInviteLink;
import net.iGap.request.RequestClientResolveUsername;

import org.chromium.customtabsclient.CustomTabsActivityHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;

import static android.content.Context.CLIPBOARD_SERVICE;
import static net.iGap.proto.ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.DOWN;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class HelperUrl {

    //TODO: change this class. dependency is in all line of code
    public static int LinkColor = Color.BLUE;
    public static int LinkColorDark = Color.CYAN;
    public static MaterialDialog dialogWaiting;
    public static String igapResolve = "igap://resolve?";
    public static Pattern patternMessageLink = Pattern.compile("(https?:(//|\\\\\\\\))?(www\\.)?(igap\\.net(/|\\\\))(.*)(/|\\\\)([0-9]+)(\\\\|/)?");
    public static Pattern patternMessageLink2 = Pattern.compile("igap://resolve\\?domain=(.*)&post=([0-9]*)");

    public static Pattern patternRoom1 = Pattern.compile("(https?:(//|\\\\\\\\))?(www\\.)?(igap\\.net(/|\\\\))(.*)");
    public static Pattern patternRoom2 = Pattern.compile("igap://resolve\\?domain=(.*)");
    public static Pattern patternRoom3 = Pattern.compile("igap://join\\?domain=(.*)");

    private static boolean isIgapLink(String text) {
        return text.matches("(https?\\:\\/\\/)?igap.net/(.*)");
    }

    public static SpannableStringBuilder setUrlLink(FragmentActivity activity, String text, boolean withClickable, boolean withHash, String messageID, boolean withAtSign) {

        if (text == null) return null;

        if (text.trim().length() < 1) return null;

        SpannableStringBuilder strBuilder = new SpannableStringBuilder(text);

        if (withAtSign) strBuilder = analaysAtSign(activity, strBuilder);

        if (withHash) strBuilder = analaysHash(activity, strBuilder, messageID);

        String newText = text.toLowerCase();

        String[] list = newText.replace(System.getProperty("line.separator"), " ").split(" ");

        int count = 0;

        for (int i = 0; i < list.length; i++) {
            String str = list[i];
            if (isIgapLink(str)) {
                insertIgapLink(activity, strBuilder, count, count + str.length());
            } else if (str.contains(igapResolve)) {
                insertIgapResolveLink(activity, strBuilder, count, count + str.length());
            } else if (isTextLink(str)) {
                insertLinkSpan(activity, strBuilder, count, count + str.length(), withClickable);
            }
            count += str.length() + 1;
        }

        return strBuilder;
    }

    public static boolean handleAppUrl(FragmentActivity activity, String url) {
        Matcher matcher2 = HelperUrl.patternMessageLink2.matcher(url);
        Matcher matcher4 = HelperUrl.patternRoom2.matcher(url);
        Matcher matcher5 = HelperUrl.patternRoom3.matcher(url);
        if (matcher2.find()) {
            String username = matcher2.group(1);
            long messageId = Long.parseLong(matcher2.group(2));
            checkUsernameAndGoToRoomWithMessageId(activity, username, HelperUrl.ChatEntry.profile, messageId);
            return true;
        } else if (matcher4.find()) {
            checkUsernameAndGoToRoom(activity, matcher4.group(1), HelperUrl.ChatEntry.profile);
            return true;
        } else if (matcher5.find()) {
            checkAndJoinToRoom(activity, matcher5.group(1));
            return true;
        }
        return false;
    }

    public static boolean isTextLink(String text) {
        Pattern p = Pattern.compile("((http|https)\\:\\/\\/)?[a-zA-Z0-9\\.\\/\\?\\:@\\-_=#]+\\.([a-zA-Z0-9\\&\\.\\/\\?\\:@\\-_=#])*");
        Matcher m = p.matcher(text);
        if (m.find()) {
            String[] strings = new String[]{
                    "abogado", "ac", "academy", "accountants", "active", "actor", "ad", "adult", "ae", "aero", "af", "ag", "agency", "ai", "airforce", "al", "allfinanz", "alsace", "am", "amsterdam", "an", "android", "ao", "apartments", "aq", "aquarelle", "ar", "archi", "army", "arpa", "as", "asia", "associates", "at", "attorney", "au", "auction", "audio", "autos", "aw", "ax", "axa", "az", "ba",
                    "band", "bank", "bar", "barclaycard", "barclays", "bargains", "bayern", "bb", "bd", "be", "beer", "berlin", "best", "bf", "bg", "bh", "bi", "bid", "bike", "bingo", "bio", "biz", "bj", "black", "blackfriday", "bloomberg", "blue", "bm", "bmw", "bn", "bnpparibas", "bo", "boo", "boutique", "br", "brussels", "bs", "bt", "budapest", "build", "builders", "business", "buzz", "bv",
                    "bw", "by", "bz", "bzh", "ca", "cab", "cal", "camera", "camp", "cancerresearch", "canon", "capetown", "capital", "caravan", "cards", "care", "career", "careers", "cartier", "casa", "cash", "cat", "catering", "cc", "cd", "center", "ceo", "cern", "cf", "cg", "ch", "channel", "chat", "cheap", "christmas", "chrome", "church", "ci", "citic", "city", "ck", "cl", "claims", "cleaning",
                    "click", "clinic", "clothing", "club", "cm", "cn", "co", "coach", "codes", "coffee", "college", "cologne", "com", "community", "company", "computer", "condos", "construction", "consulting", "contractors", "cooking", "cool", "coop", "country", "cr", "credit", "creditcard", "cricket", "crs", "cruises", "cu", "cuisinella", "cv", "cw", "cx", "cy", "cymru", "cz", "dabur", "dad",
                    "dance", "dating", "day", "dclk", "de", "deals", "degree", "delivery", "democrat", "dental", "dentist", "desi", "design", "dev", "diamonds", "diet", "digital", "direct", "directory", "discount", "dj", "dk", "dm", "dnp", "do", "docs", "domains", "doosan", "durban", "dvag", "dz", "eat", "ec", "edu", "education", "ee", "eg", "email", "emerck", "energy", "engineer", "engineering",
                    "enterprises", "equipment", "er", "es", "esq", "estate", "et", "eu", "eurovision", "eus", "events", "everbank", "exchange", "expert", "exposed", "fail", "farm", "fashion", "feedback", "fi", "finance", "financial", "firmdale", "fish", "fishing", "fit", "fitness", "fj", "fk", "flights", "florist", "flowers", "flsmidth", "fly", "fm", "fo", "foo", "forsale", "foundation", "fr",
                    "frl", "frogans", "fund", "furniture", "futbol", "ga", "gal", "gallery", "garden", "gb", "gbiz", "gd", "ge", "gent", "gf", "gg", "ggee", "gh", "gi", "gift", "gifts", "gives", "gl", "glass", "gle", "global", "globo", "gm", "gmail", "gmo", "gmx", "gn", "goog", "google", "gop", "gov", "gp", "gq", "gr", "graphics", "gratis", "green", "gripe", "gs", "gt", "gu", "guide", "guitars",
                    "guru", "gw", "gy", "hamburg", "hangout", "haus", "healthcare", "help", "here", "hermes", "hiphop", "hiv", "hk", "hm", "hn", "holdings", "holiday", "homes", "horse", "host", "hosting", "house", "how", "hr", "ht", "hu", "ibm", "id", "ie", "ifm", "il", "im", "immo", "immobilien", "in", "industries", "info", "ing", "ink", "institute", "insure", "int", "international",
                    "investments", "io", "iq", "ir", "irish", "is", "it", "iwc", "jcb", "je", "jetzt", "jm", "jo", "jobs", "joburg", "jp", "juegos", "kaufen", "kddi", "ke", "kg", "kh", "ki", "kim", "kitchen", "kiwi", "km", "kn", "koeln", "kp", "kr", "krd", "kred", "kw", "ky", "kyoto", "kz", "la", "lacaixa", "land", "lat", "latrobe", "lawyer", "lb", "lc", "lds", "lease", "legal", "lgbt", "li",
                    "lidl", "life", "lighting", "limited", "limo", "link", "lk", "loans", "london", "lotte", "lotto", "lr", "ls", "lt", "ltda", "lu", "luxe", "luxury", "lv", "ly", "ma", "madrid", "maison", "management", "mango", "market", "marketing", "marriott", "mc", "md", "me", "media", "meet", "melbourne", "meme", "memorial", "menu", "mg", "mh", "miami", "mil", "mini", "mk", "ml", "mm", "mn",
                    "mo", "mobi", "moda", "moe", "monash", "money", "mormon", "mortgage", "moscow", "motorcycles", "mov", "mp", "mq", "mr", "ms", "mt", "mu", "museum", "mv", "mw", "mx", "my", "mz", "na", "nagoya", "name", "navy", "nc", "ne", "net", "network", "neustar", "new", "nexus", "nf", "ng", "ngo", "nhk", "ni", "nico", "ninja", "nl", "no", "np", "nr", "nra", "nrw", "ntt", "nu", "nyc", "nz",
                    "okinawa", "om", "one", "ong", "onl", "ooo", "org", "organic", "osaka", "otsuka", "ovh", "pa", "paris", "partners", "parts", "party", "pe", "pf", "pg", "ph", "pharmacy", "photo", "photography", "photos", "physio", "pics", "pictures", "pink", "pizza", "pk", "pl", "place", "plumbing", "pm", "pn", "pohl", "poker", "porn", "post", "pr", "praxi", "press", "pro", "prod",
                    "productions", "prof", "properties", "property", "ps", "pt", "pub", "pw", "py", "qa", "qpon", "quebec", "re", "realtor", "recipes", "red", "rehab", "reise", "reisen", "reit", "ren", "rentals", "repair", "report", "republican", "rest", "restaurant", "reviews", "rich", "rio", "rip", "ro", "rocks", "rodeo", "rs", "rsvp", "ru", "ruhr", "rw", "ryukyu", "sa", "saarland", "sale",
                    "samsung", "sarl", "saxo", "sb", "sc", "sca", "scb", "schmidt", "schule", "schwarz", "science", "scot", "sd", "se", "services", "sew", "sg", "sh", "shiksha", "shoes", "shriram", "si", "singles", "sj", "sk", "sky", "sl", "sm", "sn", "so", "social", "software", "sohu", "solar", "solutions", "soy", "space", "spiegel", "sr", "st", "style", "su", "supplies", "supply", "support",
                    "surf", "surgery", "suzuki", "sv", "sx", "sy", "sydney", "systems", "sz", "taipei", "tatar", "tattoo", "tax", "tc", "td", "technology", "tel", "temasek", "tennis", "tf", "tg", "th", "tienda", "tips", "tires", "tirol", "tj", "tk", "tl", "tm", "tn", "to", "today", "tokyo", "tools", "top", "toshiba", "town", "toys", "tp", "tr", "trade", "training", "travel", "trust", "tt", "tui",
                    "tv", "tw", "tz", "ua", "ug", "uk", "university", "uno", "uol", "us", "uy", "uz", "va", "vacations", "vc", "ve", "vegas", "ventures", "versicherung", "vet", "vg", "vi", "viajes", "video", "villas", "vision", "vlaanderen", "vn", "vodka", "vote", "voting", "voto", "voyage", "vu", "wales", "wang", "watch", "webcam", "website", "wed", "wedding", "wf", "whoswho", "wien", "wiki",
                    "williamhill", "wme", "work", "works", "world", "ws", "wtc", "wtf", "佛山", "集团", "在线", "한국", "ভারত", "八卦", "موقع", "公益", "公司", "移动", "我爱你", "москва", "қаз", "онлайн", "сайт", "срб", "淡马锡", "орг", "삼성", "சிங்கப்பூர்", "商标", "商店", "商城", "дети", "мкд", "中文网", "中信", "中国", "中國", "谷歌", "భారత్", "ලංකා", "ભારત", "भारत", "网店", "संगठन", "网络", "укр", "香港", "台湾", "台灣", "手机", "мон",
                    "الجزائر", "عمان", "ایران", "امارات", "بازار", "الاردن", "بھارت", "المغرب", "السعودية", "مليسيا", "شبكة", "გე", "机构", "组织机构", "ไทย", "سورية", "рус", "рф", "تونس", "みんな", "グーグル", "世界", "ਭਾਰਤ", "网址", "游戏", "vermögensberater", "vermögensberatung", "企业", "مصر", "قطر", "广东", "இலங்கை", "இந்தியா", "新加坡", "فلسطين", "政务", "xxx", "xyz", "yachts", "yandex", "ye", "yoga", "yokohama",
                    "youtube", "yt", "za", "zip", "zm", "zone", "zuerich", "zw"
            };

            List<String> urlList = new ArrayList<String>(Arrays.asList(strings));

            for (String matches : urlList) {

                if (text.contains("." + matches) && text.length() > matches.length() + 2) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isTextEmail(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private static void insertLinkSpan(Context context, SpannableStringBuilder strBuilder, final int start, final int end, final boolean withclickable) {

        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {

                if (FragmentChat.onLinkClick != null)
                    FragmentChat.onLinkClick.onClick(view);

                new CountDownTimer(300, 100) {

                    public void onTick(long millisUntilFinished) {
                        view.setEnabled(false);
                    }

                    public void onFinish() {
                        view.setEnabled(true);
                    }
                }.start();

                if (withclickable) {

                    G.isLinkClicked = true;
                    String mUrl = strBuilder.toString().substring(start, end).trim();

                    if (isTextEmail(mUrl)) {

                        openEmail(context, mUrl);

                    } else { //text is url
                        openWebBrowser(context, mUrl);
                    }
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.linkColor = Theme.getInstance().getLinkColor(context);
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        strBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static void openWebBrowser(Context context, String mUrl) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
        int checkedInappBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);

        if (!mUrl.startsWith("https://") && !mUrl.startsWith("http://")) {
            mUrl = "http://" + mUrl;
        }

        if (checkedInappBrowser == 1 && !isNeedOpenWithoutBrowser(mUrl)) {
            openBrowser(mUrl); //internal chrome
        } else {
            openWithoutBrowser(mUrl);//external intent
        }
    }

    public static void openEmail(Context context, String email) {

        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null));
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.email)));
        } catch (ActivityNotFoundException e) {
            HelperError.showSnackMessage(context.getString(R.string.device_dosenot_support), false);
        }

    }

    public static boolean isNeedOpenWithoutBrowser(String url) {
        ArrayList<String> listApps = new ArrayList<>();
        listApps.add("facebook.com");
        listApps.add("twitter.com");
        listApps.add("instagram.com");
        listApps.add("pinterest.com");
        listApps.add("tumblr.com");
        listApps.add("telegram.org");
        listApps.add("flickr.com");
        listApps.add("500px.com");
        listApps.add("behance.net");
        listApps.add("t.me");

        for (String string : listApps) {
            if (url.contains(string)) {
                return true;
            }
        }
        return false;
    }

    public static void openWithoutBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(G.fragmentActivity.getPackageManager()) != null && isAvailable(G.fragmentActivity, intent)) {
            G.fragmentActivity.startActivity(intent);
        } else {
            HelperError.showSnackMessage(G.context.getResources().getString(R.string.error), false);
        }
    }

    private static boolean isAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static void openBrowser(String url) {
        //todo: fixed it and do not user G.currentActivity
        final CustomTabsHelperFragment mCustomTabsHelperFragment = CustomTabsHelperFragment.attachTo(G.currentActivity);

        int mColorPrimary = Theme.getInstance().getPrimaryColor(G.currentActivity);
        final Uri PROJECT_URI = Uri.parse(url);

        CustomTabsIntent mCustomTabsIntent = new CustomTabsIntent.Builder()
                .enableUrlBarHiding()
                .setToolbarColor(mColorPrimary)
                .setSecondaryToolbarColor(Color.WHITE)
                .setShowTitle(true)
                .build();

        mCustomTabsHelperFragment.setConnectionCallback(new CustomTabsActivityHelper.ConnectionCallback() {
            @Override
            public void onCustomTabsConnected() {
                mCustomTabsHelperFragment.mayLaunchUrl(PROJECT_URI, null, null);
            }

            @Override
            public void onCustomTabsDisconnected() {
            }
        });

        CustomTabsHelperFragment.open(G.currentActivity, mCustomTabsIntent, PROJECT_URI, new CustomTabsActivityHelper.CustomTabsFallback() {
            @Override
            public void openUri(Activity activity, Uri uri) {
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void insertIgapLink(FragmentActivity activity, final SpannableStringBuilder strBuilder, final int start, final int end) {
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {

                if (FragmentChat.onLinkClick != null)
                    FragmentChat.onLinkClick.onClick(view);

                G.isLinkClicked = true;
                String url = strBuilder.toString().substring(start, end);

                int index = url.lastIndexOf("/");
                if (index >= 0 && index < url.length() - 1) {
                    String token = url.substring(index + 1);

                    if (url.toLowerCase().contains("join")) {
                        checkAndJoinToRoom(activity, token);
                    } else {
                        Matcher matcher = patternMessageLink.matcher(url);
                        if (matcher.find()) {
                            String username = matcher.group(6);
                            long messageId = Long.parseLong(matcher.group(8));
                            checkUsernameAndGoToRoomWithMessageId(activity, username, ChatEntry.profile, messageId);
                        } else {
                            checkUsernameAndGoToRoom(activity, token, ChatEntry.profile);
                        }
                    }
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.linkColor = Theme.getInstance().getLinkColor(activity);
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        strBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void insertIgapBot(Context context, SpannableStringBuilder strBuilder, final int start, final int end) {

        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {

                if (FragmentChat.onLinkClick != null)
                    FragmentChat.onLinkClick.onClick(view);

                G.isLinkClicked = true;
                String botCommandText = strBuilder.toString().substring(start, end);

                if (G.onBotClick != null) {
                    G.onBotClick.onBotCommandText(botCommandText, 0);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.linkColor = Theme.getInstance().getLinkColor(context);
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        strBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void insertIgapResolveLink(FragmentActivity activity, final SpannableStringBuilder strBuilder, final int start, final int end) {

        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {

                if (FragmentChat.onLinkClick != null)
                    FragmentChat.onLinkClick.onClick(view);

                String url = strBuilder.toString().substring(start, end);

                G.isLinkClicked = true;
                Matcher matcher2 = patternMessageLink2.matcher(url);
                if (matcher2.find()) {
                    String username = matcher2.group(1);
                    long messageId = Long.parseLong(matcher2.group(2));
                    checkUsernameAndGoToRoomWithMessageId(activity, username, ChatEntry.profile, messageId);
                } else {
                    try {
                        Uri path = Uri.parse(url);

                        String domain = path.getQueryParameter("domain");

                        if (domain != null && domain.length() > 0) {
                            checkUsernameAndGoToRoom(activity, domain, ChatEntry.profile);
                        }
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.linkColor = Theme.getInstance().getLinkColor(activity);
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        strBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void insertDigitLink(FragmentActivity activity, final SpannableStringBuilder strBuilder, final int start, final int end) {

        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {

                if (FragmentChat.onLinkClick != null)
                    FragmentChat.onLinkClick.onClick(view);

                G.isLinkClicked = true;
                String digitLink = strBuilder.toString().substring(start, end);
                openDialogDigitClick(activity, digitLink);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.linkColor = Theme.getInstance().getLinkColor(activity);
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        strBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void insertIgapDeepLink(FragmentActivity activity, final SpannableStringBuilder strBuilder, final int start, final int end) {

        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {

                if (FragmentChat.onLinkClick != null)
                    FragmentChat.onLinkClick.onClick(view);

                G.isLinkClicked = true;

                if (activity == null)
                    return;

                String deepLink = strBuilder.toString().trim().substring(start, end).toLowerCase().replace("igap://", "");

                if (!deepLink.equals("")) {
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();

                    BottomNavigationFragment navigationFragment = (BottomNavigationFragment) fragmentManager.findFragmentByTag(BottomNavigationFragment.class.getName());
                    if (navigationFragment != null)
                        navigationFragment.autoLinkCrawler(deepLink, new DiscoveryFragment.CrawlerStruct.OnDeepValidLink() {
                            @Override
                            public void linkValid(String link) {
                                if (!G.twoPaneMode)
                                    activity.onBackPressed();
                            }

                            @Override
                            public void linkInvalid(String link) {
                                HelperError.showSnackMessage(link + " " + activity.getResources().getString(R.string.link_not_valid), false);
                            }
                        });
                } else
                    HelperError.showSnackMessage(activity.getResources().getString(R.string.link_not_valid), false);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.linkColor = Theme.getInstance().getLinkColor(activity);
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        strBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static SpannableStringBuilder analaysHash(Context context, SpannableStringBuilder builder, String messageID) {

        if (builder == null) return builder;

        String text = builder.toString();

        if (text.length() < 1) return builder;

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
                if (!(s.matches("\\w") || s.equals("-"))) {
                    if (tmp.length() > 0) {
                        insertHashLink(context, tmp, builder, start, messageID);
                    }

                    tmp = "";
                    isHash = false;
                } else {
                    tmp += s;
                }
            }
        }

        if (isHash) {
            if (tmp.length() > 0) insertHashLink(context, tmp, builder, start, messageID);
        }

        return builder;
    }

    private static void insertHashLink(Context context, String text, SpannableStringBuilder builder, int start, final String messageID) {

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {

                if (FragmentChat.onLinkClick != null)
                    FragmentChat.onLinkClick.onClick(view);

                G.isLinkClicked = true;
                if (FragmentChat.hashListener != null) {
                    FragmentChat.hashListener.complete(true, "#" + text, messageID);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //ToDo: fixed it and pass color to this function
                ds.linkColor = Theme.getInstance().getLinkColor(context);

                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        builder.setSpan(clickableSpan, start, start + text.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    //*********************************************************************************************************

    private static SpannableStringBuilder analaysAtSign(FragmentActivity activity, SpannableStringBuilder builder) {

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
                if (!(s.matches("\\w") || s.equals("-"))) {
                    //if (s.equals("!") || s.equals("#") || s.equals("$") || s.equals("%") || s.equals("^") || s.equals("&") ||
                    //    s.equals("(") || s.equals(")") || s.equals("-") || s.equals("+") || s.equals("=") || s.equals("!") ||
                    //    s.equals("`") || s.equals("{") || s.equals("}") || s.equals("[") || s.equals("]") || s.equals(";") ||
                    //    s.equals(":") || s.equals("'") || s.equals("?") || s.equals("<") || s.equals(">") || s.equals(",") || s.equals(" ") ||
                    //    s.equals("\\") || s.equals("|") || s.equals("//") || s.codePointAt(0) == 8192 || s.equals(enter) || s.equals("")) {
                    if (tmp.length() > 0) {
                        insertAtSignLink(activity, tmp, builder, start);
                    }

                    tmp = "";
                    isAtSign = false;
                } else {
                    tmp += s;
                }
            }
        }

        if (isAtSign) {
            if (tmp.length() > 0) insertAtSignLink(activity, tmp, builder, start);
        }

        return builder;
    }

    private static void insertAtSignLink(FragmentActivity activity, final String text, SpannableStringBuilder builder, int start) {

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {

                if (FragmentChat.onLinkClick != null)
                    FragmentChat.onLinkClick.onClick(view);

                G.isLinkClicked = true;
                checkUsernameAndGoToRoom(activity, text, ChatEntry.profile);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.linkColor = Theme.getInstance().getLinkColor(activity);

                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        builder.setSpan(clickableSpan, start, start + text.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    //*********************************************************************************************************

    public static SpannableStringBuilder getLinkText(FragmentActivity activity, String text, String linkInfo, String messageID) {

        if (text == null) return null;
        if (text.trim().length() < 1) return null;

        SpannableStringBuilder strBuilder = new SpannableStringBuilder(text);

        if (linkInfo != null && linkInfo.length() > 0) {

            String[] list = linkInfo.split("@");

            for (int i = 0; i < list.length; i++) {

                String[] info = list[i].split("_");

                int start = Integer.parseInt(info[0]);
                int end = Integer.parseInt(info[1]);
                String type = info[2];

                try {
                    switch (type) {
                        case "hash":
                            insertHashLink(activity, text.substring(start + 1, end), strBuilder, start, messageID);
                            break;
                        case "atSighn":
                            insertAtSignLink(activity, text.substring(start + 1, end), strBuilder, start);
                            break;
                        case "igapLink":
                            insertIgapLink(activity, strBuilder, start, end);
                            break;
                        case "igapResolve":
                            insertIgapResolveLink(activity, strBuilder, start, end);
                            break;
                        case "bot":
                            insertIgapBot(activity, strBuilder, start, end);
                            break;
                        case "webLink":
                            insertLinkSpan(activity, strBuilder, start, end, true);
                            break;
                        case "digitLink":
                            insertDigitLink(activity, strBuilder, start, end);
                            break;
                        case "igapDeepLink":
                            insertIgapDeepLink(activity, strBuilder, start, end);
                            break;
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }

        return strBuilder;
    }

    public static String getLinkInfo(String text) {

        String linkInfo = "";

        if (text == null) {
            return linkInfo;
        }

        if (text.trim().length() < 1) {
            return linkInfo;
        }

        ArrayList<Tuple<Integer, Integer>> boldPlaces = AbstractMessage.getBoldPlaces(text);
        text = AbstractMessage.removeBoldMark(text, boldPlaces);

        linkInfo += analysisAtSignLinkInfo(text);

        linkInfo += analysisHashLinkInfo(text);

        String newText = text.toLowerCase();

        String[] list = newText.replace(System.getProperty("line.separator"), " ").split(" ");

        int count = 0;

        for (int i = 0; i < list.length; i++) {

            String str = list[i];

            if (isIgapLink(str)) {
                linkInfo += count + "_" + (count + str.length()) + "_" + linkType.igapLink.toString() + "@";
            } else if (str.contains(igapResolve)) {
                linkInfo += count + "_" + (count + str.length()) + "_" + linkType.igapResolve.toString() + "@";
            } else if (isBotLink(str)) {
                linkInfo += count + "_" + (count + str.length()) + "_" + linkType.bot.toString() + "@";
            } else if (isTextLink(str)) {
                linkInfo += count + "_" + (count + str.length()) + "_" + linkType.webLink.toString() + "@";
            } else if (isDigitLink(str)) {
                linkInfo += count + "_" + (count + str.length()) + "_" + linkType.digitLink.toString() + "@";
            } else if (isIgapDeepLink(str)) {
                linkInfo += count + "_" + (count + str.length()) + "_" + linkType.igapDeepLink.toString() + "@";
            }
            count += str.length() + 1;
        }

        return linkInfo;
    }

    private static boolean isDigitLink(String text) {
        return text.matches("^\\s*(?:\\+?(\\d{1,3}))?([-. (]*(\\d{3})[-. )]*)?((\\d{3})[-. ]*(\\d{2,4})(?:[-.x ]*(\\d+))?)\\s*$");
    }

    private static boolean isIgapDeepLink(String text) {
        return text.matches("(igap?://)([^:^/]*)(:\\d*)?(.*)?");
    }

    private static boolean isBotLink(String text) {
        return text.matches("^\\/\\w+");
    }

    private static String analysisAtSignLinkInfo(String text) {
        String result = "";
        if (text == null || text.length() < 1) {
            return result;
        }

        Pattern p = Pattern.compile("[@]+[A-Za-z0-9-_]+\\b");
        Matcher m = p.matcher(text);
        while (m.find()) {
            result += m.start() + "_" + m.end() + "_" + linkType.atSighn.toString() + "@";
        }

        return result;
    }

    private static String analysisHashLinkInfo(String text) {
        String result = "";
        if (text == null || text.length() < 1) {
            return result;
        }
        Pattern p = Pattern.compile("[#]+[\\p{L}A-Za-z0-9۰-۹٠-٩-_]+\\b");
        Matcher m = p.matcher(text);
        while (m.find()) {
            result += m.start() + "_" + m.end() + "_" + linkType.hash.toString() + "@";
        }

        return result;
    }

    public static void checkAndJoinToRoom(FragmentActivity activity, final String token) {
        if (token == null || token.length() < 0 || isInCurrentChat(token)) return;
        if (G.userLogin) {
            showIndeterminateProgressDialog(activity);

            G.onClientCheckInviteLink = new OnClientCheckInviteLink() {
                @Override
                public void onClientCheckInviteLinkResponse(ProtoGlobal.Room room) {
                    closeDialogWaiting();
                    openDialogJoin(activity, room, token);
                }

                @Override
                public void onError(int majorCode, int minorCode) {
                    closeDialogWaiting();
                }
            };

            new RequestClientCheckInviteLink().clientCheckInviteLink(token);
        } else {
            closeDialogWaiting();
            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
        }
    }

    private static void openDialogJoin(FragmentActivity activity, final ProtoGlobal.Room room, final String token) {
        if (room == null) {
            return;
        }
        DbManager.getInstance().doRealmTask(realm -> {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).equalTo(RealmRoomFields.IS_DELETED, false).findFirst();
            if (realmRoom != null) {
                if (room.getId() != FragmentChat.lastChatRoomId) {
                    new GoToChatActivity(room.getId()).startActivity(activity);
                }
                return;
            }

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = RealmRoom.putOrUpdate(room, realm);
                    realmRoom.setDeleted(true);
                }
            });
        });

        if (!room.getIsParticipant()) {
            activity.runOnUiThread(() -> {
                closeDialogWaiting();
                new JoinDialogFragment().setData(room, new JoinDialogFragment.JoinDialogListener() {
                    @Override
                    public void onJoinClicked() {
                        joinToRoom(activity, token, room);
                    }

                    @Override
                    public void onCancelClicked() {
                        RealmRoom.deleteRoom(room.getId());
                    }
                }).show(activity.getSupportFragmentManager(), "JoinDialogFragment");
            });
        }
    }

    private static void joinToRoom(FragmentActivity activity, String token, final ProtoGlobal.Room room) {
        if (G.userLogin) {
            showIndeterminateProgressDialog(activity);

            G.onClientJoinByInviteLink = new OnClientJoinByInviteLink() {
                @Override
                public void onClientJoinByInviteLinkResponse() {

                    closeDialogWaiting();
                    RealmRoom.joinByInviteLink(room.getId());

                    new RequestClientGetRoom().clientGetRoom(room.getId(), RequestClientGetRoom.CreateRoomMode.requestFromOwner);

                    if (room.getId() != FragmentChat.lastChatRoomId) {
                        new GoToChatActivity(room.getId()).startActivity(activity);
                    }

                }

                @Override
                public void onError(int majorCode, int minorCode) {
                    closeDialogWaiting();
                }
            };

            new RequestClientJoinByInviteLink().clientJoinByInviteLink(token);
        } else {
            closeDialogWaiting();
            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
        }
    }

    private static boolean isInCurrentChat(String userName) {
        if (FragmentChat.lastChatRoomId > 0) {
            return DbManager.getInstance().doRealmTask(realm -> {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, FragmentChat.lastChatRoomId).findFirst();
                String currentChatUserName = "";
                String currentChatInviteLink = "";
                if (realmRoom != null) {
                    if (realmRoom.getChannelRoom() != null) {
                        currentChatUserName = realmRoom.getChannelRoom().getUsername();
                        currentChatInviteLink = realmRoom.getChannelRoom().getInviteLink();
                    } else if (realmRoom.getGroupRoom() != null) {
                        currentChatUserName = realmRoom.getGroupRoom().getUsername();
                        currentChatInviteLink = realmRoom.getGroupRoom().getInvite_link();
                    } else if (realmRoom.getChatRoom() != null) {
                        RealmRegisteredInfo registeredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, realmRoom.getChatRoom().getPeerId());
                        if (registeredInfo != null) {
                            currentChatUserName = registeredInfo.getUsername();
                        }
                    }
                }

                if (currentChatUserName != null && currentChatUserName.toLowerCase().equals(userName.toLowerCase())) {
                    return true;
                }

                if (currentChatInviteLink != null && currentChatInviteLink.length() > 0) {
                    int index = currentChatInviteLink.lastIndexOf("/");
                    if (index != -1) {
                        String st = currentChatInviteLink.toLowerCase().substring(index + 1);
                        if (st.equals(userName.toLowerCase())) {
                            return true;
                        }
                    }
                }

                return false;
            });
        }
        return false;
    }

    public static void checkUsernameAndGoToRoom(FragmentActivity activity, final String userName, final ChatEntry chatEntery) {
        checkUsernameAndGoToRoomWithMessageId(activity, userName, chatEntery, 0);
    }


    /**
     * @param username
     * @param chatEntry
     * @param messageId // use for detect message position
     */

    public static void checkUsernameAndGoToRoomWithMessageId(FragmentActivity activity, final String username, final ChatEntry chatEntry, final long messageId) {
        if (username == null || username.length() < 1) return;

        if (G.userLogin) {

            // this methode check user name and if it is ok go to room
            G.onClientResolveUsername = new OnClientResolveUsername() {
                @Override
                public void onClientResolveUsername(ProtoClientResolveUsername.ClientResolveUsernameResponse.Type type, ProtoGlobal.RegisteredUser user, ProtoGlobal.Room room) {
                    if (messageId == 0 || type == ProtoClientResolveUsername.ClientResolveUsernameResponse.Type.USER) {
                        openChat(activity, username, type, user, room, user.getBot() ? ChatEntry.chat : chatEntry, messageId);
                    } else {
                        resolveMessageAndOpenChat(activity, messageId, username, user.getBot() ? ChatEntry.chat : chatEntry, type, user, room);
                    }
                }

                @Override
                public void onError(int majorCode, int minorCode) {
                    closeDialogWaiting();
                }
            };

            showIndeterminateProgressDialog(activity);

            new RequestClientResolveUsername().clientResolveUsername(username);
        } else {
            closeDialogWaiting();
            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
        }
    }

    /**
     * if message isn't exist in Realm resolve from server and then open chat
     */
    private static void resolveMessageAndOpenChat(FragmentActivity activity, final long messageId, final String username, final ChatEntry chatEntry, final ProtoClientResolveUsername.ClientResolveUsernameResponse.Type type, final ProtoGlobal.RegisteredUser user, final ProtoGlobal.Room room) {
        DbManager.getInstance().doRealmTask(new DbManager.RealmTask() {
            @Override
            public void doTask(Realm realm) {
                RealmRoomMessage rm = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, room.getId()).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                if (rm != null) {
                    openChat(activity, username, type, user, room, chatEntry, messageId);
                } else {
                    new RequestClientGetRoomHistory().getRoomHistory(room.getId(), messageId - 1, 1, DOWN, new RequestClientGetRoomHistory.OnHistoryReady() {
                        @Override
                        public void onHistory(List<ProtoGlobal.RoomMessage> messageList) {
                            if (messageList.size() == 0 || messageList.get(0).getMessageId() != messageId || messageList.get(0).getDeleted()) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        closeDialogWaiting();
                                        HelperError.showSnackMessage(activity.getString(R.string.not_found_message), false);
                                        openChat(activity, username, type, user, room, chatEntry, 0);
                                    }
                                });
                            } else {
                                DbManager.getInstance().doRealmTransaction(realm -> {
                                    for (ProtoGlobal.RoomMessage roomMessage : messageList) {
                                        if (roomMessage.getAuthor().hasUser()) {
                                            RealmRegisteredInfo.needUpdateUser(roomMessage.getAuthor().getUser().getUserId(), roomMessage.getAuthor().getUser().getCacheId());
                                        }
                                        RealmRoomMessage.putOrUpdate(realm, room.getId(), roomMessage, new StructMessageOption().setGap());
                                    }
                                });

                                RealmRoomMessage.setGap(messageList.get(0).getMessageId());
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        G.refreshRealmUi();
                                        openChat(activity, username, type, user, room, chatEntry, messageList.get(0).getMessageId());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onErrorHistory(int major, int minor) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    closeDialogWaiting();
                                    if (major == 626) {
                                        HelperError.showSnackMessage(activity.getString(R.string.not_found_message), false);
                                    } else if (minor == 624) {
                                        HelperError.showSnackMessage(activity.getString(R.string.ivnalid_data_provided), false);
                                    } else {
                                        HelperError.showSnackMessage(activity.getString(R.string.there_is_no_connection_to_server), false);
                                    }
                                }
                            });
                        }
                    });

                }
            }
        });
    }

    public static void closeDialogWaiting() {
        try {
            if (dialogWaiting != null && dialogWaiting.isShowing() && !(G.currentActivity).isFinishing()) {
                dialogWaiting.dismiss();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    //************************************  go to room by userName   *********************************************************************

    private static void openChat(FragmentActivity activity, String username, ProtoClientResolveUsername.ClientResolveUsernameResponse.Type type, ProtoGlobal.RegisteredUser user, ProtoGlobal.Room room, ChatEntry chatEntery, long messageId) {
        switch (type) {
            case USER:
                goToChat(activity, user, chatEntery, messageId);
                break;
            case ROOM:
                goToRoom(activity, username, room, messageId);
                break;
        }
    }

    private static void goToActivity(FragmentActivity activity, final long roomId, final long peerId, ChatEntry chatEntry, final long messageId) {

        switch (chatEntry) {
            case chat:
                if (roomId != FragmentChat.lastChatRoomId) {
                    DbManager.getInstance().doRealmTask(new DbManager.RealmTask() {
                        @Override
                        public void doTask(Realm realm) {
                            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, peerId).findFirst();

                            if (realmRoom != null) {
                                new GoToChatActivity(realmRoom.getId()).setMessageID(messageId).startActivity(activity);
                            } else {
                                G.onChatGetRoom = new OnChatGetRoom() {
                                    @Override
                                    public void onChatGetRoom(final ProtoGlobal.Room room) {
                                        DbManager.getInstance().doRealmTransaction(realm2 -> {
                                            if (realm2.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).findFirst() == null) {
                                                RealmRoom realmRoom1 = RealmRoom.putOrUpdate(room, realm2);
                                                realmRoom1.setDeleted(true);
                                            } else {
                                                RealmRoom.putOrUpdate(room, realm2);
                                            }
                                        });
                                        G.handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                new GoToChatActivity(room.getId()).setPeerID(peerId).startActivity(activity);
                                                G.onChatGetRoom = null;
                                            }
                                        }, 500);
                                    }

                                    @Override
                                    public void onChatGetRoomTimeOut() {

                                    }

                                    @Override
                                    public void onChatGetRoomError(int majorCode, int minorCode) {

                                    }
                                };

                                new RequestChatGetRoom().chatGetRoom(peerId);
                            }
                        }
                    });
                }

                break;
            case profile:
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        FragmentContactsProfile contactsProfile = new FragmentContactsProfile();
                        Bundle bundle = new Bundle();
                        bundle.putLong("peerId", peerId);
                        bundle.putLong("RoomId", roomId);
                        bundle.putString("enterFrom", GROUP.toString());
                        contactsProfile.setArguments(bundle);
                        new HelperFragment(activity.getSupportFragmentManager(), contactsProfile).setReplace(false).load();
                    }
                });
                break;
        }
    }

    public static void goToActivityFromFCM(FragmentActivity activity, final long roomId, final long peerId) {

        if (roomId != FragmentChat.lastChatRoomId) {
            DbManager.getInstance().doRealmTask(realm -> {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                if (realmRoom != null) {
                    // room with given roomID exists.
                    new GoToChatActivity(realmRoom.getId()).startActivity(activity);
                } else if (peerId > 0) {
                    realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, peerId).findFirst();
                    if (realmRoom != null) {
                        new GoToChatActivity(realmRoom.getId()).startActivity(activity);
                    } else {
                        G.onChatGetRoom = new OnChatGetRoom() {
                            @Override
                            public void onChatGetRoom(final ProtoGlobal.Room room) {
                                DbManager.getInstance().doRealmTask(new DbManager.RealmTask() {
                                    @Override
                                    public void doTask(Realm realm1) {
                                        realm1.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                if (realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).findFirst() == null) {
                                                    RealmRoom realmRoom1 = RealmRoom.putOrUpdate(room, realm);
                                                    realmRoom1.setDeleted(true);
                                                } else {
                                                    RealmRoom.putOrUpdate(room, realm);
                                                }
                                            }
                                        });
                                    }
                                });
                                G.handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        new GoToChatActivity(room.getId()).setPeerID(peerId).startActivity(activity);
                                        G.onChatGetRoom = null;
                                    }
                                }, 500);
                            }

                            @Override
                            public void onChatGetRoomTimeOut() {

                            }

                            @Override
                            public void onChatGetRoomError(int majorCode, int minorCode) {

                            }
                        };
                        new RequestChatGetRoom().chatGetRoom(peerId);
                    }
                } else {
                    G.onClientGetRoomResponse = new OnClientGetRoomResponse() {
                        @Override
                        public void onClientGetRoomResponse(ProtoGlobal.Room room, ProtoClientGetRoom.ClientGetRoomResponse.Builder builder, RequestClientGetRoom.IdentityClientGetRoom identity) {
                            G.onClientGetRoomResponse = null;
                            G.handler.postDelayed(() -> {
                                new GoToChatActivity(room.getId()).setPeerID(peerId).startActivity(activity);
                                G.onChatGetRoom = null;
                            }, 500);
                        }

                        @Override
                        public void onError(int majorCode, int minorCode) {

                        }

                        @Override
                        public void onTimeOut() {

                        }
                    };
                    new RequestClientGetRoom().clientGetRoom(roomId, null);
                    Toast.makeText(activity, "Please Wait...", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private static void goToChat(FragmentActivity activity, final ProtoGlobal.RegisteredUser user, final ChatEntry chatEntery, long messageId) {
        long id = user.getId();
        DbManager.getInstance().doRealmTask(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, id).equalTo(RealmRoomFields.IS_DELETED, false).findFirst();

            if (realmRoom != null) {
                closeDialogWaiting();
                goToActivity(activity, realmRoom.getId(), id, user.getBot() ? ChatEntry.chat : chatEntery, messageId);

            } else {
                if (G.userLogin) {
                    addChatToDatabaseAndGoToChat(activity, user, -1, user.getBot() ? ChatEntry.chat : chatEntery);
                } else {
                    closeDialogWaiting();
                    HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
                }
            }
        });
    }

    public static void showIndeterminateProgressDialog(Activity activity) {

        try {
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialogWaiting != null && dialogWaiting.isShowing()) {

                        } else if (!(activity).isFinishing()) {
                            dialogWaiting = new MaterialDialog.Builder(activity).title("").content(R.string.please_wait).progress(true, 0).cancelable(false).progressIndeterminateStyle(false).show();
                        }
                    }
                });
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private static void addChatToDatabaseAndGoToChat(FragmentActivity activity, final ProtoGlobal.RegisteredUser user, final long roomId, final ChatEntry chatEntery) {

        closeDialogWaiting();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                DbManager.getInstance().doRealmTask(realm -> {
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmRegisteredInfo.putOrUpdate(realm, user);
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            goToActivity(activity, roomId, user.getId(), user.getBot() ? ChatEntry.chat : chatEntery, 0);
                        }
                    });
                });
            }
        });
    }

    private static void goToRoom(FragmentActivity activity, String username, final ProtoGlobal.Room room, long messageId) {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).findFirst();

            if (realmRoom != null) {

                if (realmRoom.isDeleted()) {
                    addRoomToDataBaseAndGoToRoom(activity, username, room, messageId);
                } else {
                    closeDialogWaiting();

                    if (room.getId() != FragmentChat.lastChatRoomId) {
                        new GoToChatActivity(room.getId()).setMessageID(messageId).startActivity(activity);
                    } else {
                        try {
                            if (activity != null) {
                                activity.getSupportFragmentManager().popBackStack();
                                new GoToChatActivity(room.getId()).setMessageID(messageId).startActivity(activity);
                            }
                        } catch (Exception e) {
                            HelperLog.getInstance().setErrorLog(e);
                            e.printStackTrace();
                        }
                    }
                }

            } else {
                addRoomToDataBaseAndGoToRoom(activity, username, room, messageId);
            }
        });
    }

    private static void addRoomToDataBaseAndGoToRoom(FragmentActivity activity, final String username, final ProtoGlobal.Room room, long messageId) {
        closeDialogWaiting();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                DbManager.getInstance().doRealmTask(realm -> {
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            RealmRoom realmRoom1 = RealmRoom.putOrUpdate(room, realm);
                            realmRoom1.setDeleted(true);                            // if in chat activity join to room set deleted goes to false
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            if (room.getId() != FragmentChat.lastChatRoomId) {
                                new GoToChatActivity(room.getId()).setfromUserLink(true).setisNotJoin(true).setuserName(username).setMessageID(messageId).startActivity(activity);
                            } else {
                                try {
                                    if (activity != null) {
                                        activity.getSupportFragmentManager().popBackStack();
                                        new GoToChatActivity(room.getId()).setfromUserLink(true).setisNotJoin(true).setuserName(username).setMessageID(messageId).startActivity(activity);
                                    }
                                } catch (Exception e) {
                                    HelperLog.getInstance().setErrorLog(e);
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                });
            }
        });
    }

    public static void getLinkInfo(Intent intent, FragmentActivity activity) {
        String action = intent.getAction();

        if (action == null || intent.getData() == null) return;

        if (action.equals(Intent.ACTION_VIEW)) {
            G.currentActivity = (ActivityEnhanced) activity;
            showIndeterminateProgressDialog(activity);
            checkConnection(activity, intent.getData(), 0);
        }
    }

    private static void checkConnection(FragmentActivity activity, final Uri path, int countTime) {
        countTime++;

        if (G.userLogin) {
            Matcher matcher = patternMessageLink.matcher(path.toString());
            Matcher matcher2 = patternMessageLink2.matcher(path.toString());
            if (matcher.find()) {
                String username = matcher.group(6);
                long messageId = Long.parseLong(matcher.group(8));
                checkUsernameAndGoToRoomWithMessageId(activity, username, ChatEntry.profile, messageId);
            } else if (matcher2.find()) {
                String username = matcher2.group(1);
                long messageId = Long.parseLong(matcher2.group(2));
                checkUsernameAndGoToRoomWithMessageId(activity, username, ChatEntry.profile, messageId);
            } else {
                getToRoom(activity, path);
            }


        } else {
            if (countTime < 15) {
                final int finalCountTime = countTime;
                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkConnection(activity, path, finalCountTime);
                    }
                }, 1000);
            } else {
                closeDialogWaiting();
                HelperError.showSnackMessage(activity.getString(R.string.can_not_connent_to_server), false);
            }
        }
    }

    private static void openDialogDigitClick(FragmentActivity activity, String text) {

        try {
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    List<String> items = new ArrayList<>();
                    items.add(activity.getString(R.string.copy_item_dialog));
                    items.add(activity.getString(R.string.verify_register_call));
                    items.add(activity.getString(R.string.add_to_contact));
                    items.add(activity.getString(R.string.verify_register_sms));

                    new BottomSheetFragment().setData(items, -1, new BottomSheetItemClickCallback() {
                        @Override
                        public void onClick(int position) {
                            if (items.get(position).equals(activity.getString(R.string.copy_item_dialog))) {
                                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Copied Text", text);
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(activity, R.string.text_copied, Toast.LENGTH_SHORT).show();
                            } else if (items.get(position).equals(G.fragmentActivity.getString(R.string.verify_register_call))) {
                                String uri = "tel:" + text;
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse(uri));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                activity.startActivity(intent);
                            } else if (items.get(position).equals(activity.getString(R.string.add_to_contact))) {
                                FragmentAddContact fragment = FragmentAddContact.newInstance(
                                        text, FragmentAddContact.ContactMode.ADD
                                );
                                new HelperFragment(activity.getSupportFragmentManager(), fragment).setReplace(false).load();
                            } else if (items.get(position).equals(activity.getString(R.string.verify_register_sms))) {
                                String uri = "smsto:" + text;
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse(uri));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                activity.startActivity(intent);
                            }
                        }
                    }).show(activity.getSupportFragmentManager(), "bottom sheet");
                });
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    public static void openLinkDialog(FragmentActivity fa, String mUrl) {
        String url = mUrl;
        List<String> items = new ArrayList<>();
        items.add(fa.getString(R.string.copy_item_dialog));

        if (isTextEmail(url)) {
            items.add(fa.getString(R.string.email));
        } else {
            if (!url.startsWith("https://") && !url.startsWith("http://")) {
                url = "http://" + mUrl;
            }
            items.add(fa.getString(R.string.open_url));
        }

        String finalUrl = url;
        new BottomSheetFragment().setTitle(url).setData(items, -1, new BottomSheetItemClickCallback() {
            @Override
            public void onClick(int position) {
                if (items.get(position).equals(fa.getString(R.string.copy_item_dialog))) {

                    ClipboardManager clipboard = (ClipboardManager) fa.getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied Url", finalUrl);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(fa, R.string.copied, Toast.LENGTH_SHORT).show();

                } else if (items.get(position).equals(fa.getString(R.string.open_url))) {
                    openWebBrowser(fa, finalUrl);
                } else if (items.get(position).equals(fa.getString(R.string.email))) {
                    openEmail(fa, finalUrl);
                }
            }
        }).show(fa.getSupportFragmentManager(), "bottom sheet");
    }

    //************************************  go to room by urlLink   *********************************************************************

    private static void getToRoom(FragmentActivity activity, Uri path) {

        if (path != null) {
            if (isIgapLink(path.toString().toLowerCase())) {

                String url = path.toString();
                int index = url.lastIndexOf("/");
                if (index >= 0 && index < url.length() - 1) {
                    String token = url.substring(index + 1);

                    if (url.toLowerCase().contains("join")) {
                        checkAndJoinToRoom(activity, token);
                    } else {
                        checkUsernameAndGoToRoom(activity, token, ChatEntry.profile);
                    }
                }
            } else {

                try {
                    String domain = path.getQueryParameter("domain");

                    if (domain != null && domain.length() > 0) {
                        checkUsernameAndGoToRoom(activity, domain, ChatEntry.profile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            closeDialogWaiting();
        }
    }

    public enum linkType {
        hash, atSighn, igapLink, igapResolve, webLink, bot, digitLink, igapDeepLink

    }

    public enum ChatEntry {
        chat, profile
    }
}
