package net.iGap.libs.emojiKeyboard.emoji;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;

import androidx.annotation.NonNull;

import net.iGap.G;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.AndroidUtils;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.eventbus.EventManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class EmojiManager {
    private String TAG = "abbasiEmoji";

    private static EmojiManager instance;

    private final int MAX_RECENT_EMOJI_COUNT = 56;
    public final int EMOJI_CATEGORY_SIZE = 8;

    private final int VERSION = 1;
    private final String EMOJI_VERSION_KEY = "emoji_v" + VERSION;
    private final String EMOJI_DEFAULT_KEY = "default_emoji";

    private HashMap<CharSequence, DrawableInfo> drawableInfoMap = new HashMap<>();
    private HashMap<String, Integer> emojiUseHistory = new HashMap<>();
    private ArrayList<String> recentEmoji = new ArrayList<>();
    private HashMap<String, String> emojiColor = new HashMap<>();

    private Bitmap[][] bitmaps = new Bitmap[EMOJI_CATEGORY_SIZE][];
    private boolean[][] loadingEmoji = new boolean[EMOJI_CATEGORY_SIZE][];

    private int drawImgSize;
    private int bigImgSize;
    private Paint placeholderPaint;
    private boolean recentEmojiLoaded;

    private Runnable invalidateRunnable = () -> EventManager.getInstance(AccountManager.selectedAccount).postNotificationName(EventManager.EMOJI_LOADED);

    static {
        getInstance();
    }

    public static EmojiManager getInstance() {
        if (instance == null) {
            instance = new EmojiManager();

            instance.drawImgSize = LayoutCreator.dp(20);
            instance.bigImgSize = LayoutCreator.dp(G.twoPaneMode ? 36 : 34);

            for (int a = 0; a < instance.bitmaps.length; a++) {
                instance.bitmaps[a] = new Bitmap[instance.getCategoryManager().getEmojiCategory()[a].getEmojies().length];
                instance.loadingEmoji[a] = new boolean[instance.getCategoryManager().getEmojiCategory()[a].getEmojies().length];
            }

            for (int j = 0; j < instance.getCategoryManager().getEmojiCategory().length; j++) {
                for (int i = 0; i < instance.getCategoryManager().getEmojiCategory()[j].getEmojies().length; i++) {
                    instance.drawableInfoMap.put(instance.getCategoryManager().getEmojiCategory()[j].getEmojies()[i], new DrawableInfo((byte) j, (short) i, i));
                }
            }

            instance.placeholderPaint = new Paint();
            instance.placeholderPaint.setColor(0x00000000);

        }
        return instance;
    }

    public EmojiCategoryManager getCategoryManager() {
        return EmojiCategoryManager.getInstance();
    }

    public class EmojiDrawable extends Drawable {
        private DrawableInfo drawableInfo;
        private boolean fullSize = false;
        private Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        private Rect rect = new Rect();

        EmojiDrawable(DrawableInfo drawableInfo) {
            this.drawableInfo = drawableInfo;
        }

        public DrawableInfo getDrawableInfo() {
            return drawableInfo;
        }

        Rect getDrawRect() {
            Rect original = getBounds();
            int cX = original.centerX(), cY = original.centerY();
            rect.left = cX - (fullSize ? bigImgSize : drawImgSize) / 2;
            rect.right = cX + (fullSize ? bigImgSize : drawImgSize) / 2;
            rect.top = cY - (fullSize ? bigImgSize : drawImgSize) / 2;
            rect.bottom = cY + (fullSize ? bigImgSize : drawImgSize) / 2;
            return rect;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            if (bitmaps[drawableInfo.categoryIndex][drawableInfo.emojiCategoryIndex] == null) {
                if (loadingEmoji[drawableInfo.categoryIndex][drawableInfo.emojiCategoryIndex]) {
                    return;
                }
                loadingEmoji[drawableInfo.categoryIndex][drawableInfo.emojiCategoryIndex] = true;
                AndroidUtils.globalQueue.postRunnable(() -> {
                    loadEmoji(drawableInfo.categoryIndex, drawableInfo.emojiCategoryIndex);
                    loadingEmoji[drawableInfo.categoryIndex][drawableInfo.emojiCategoryIndex] = false;
                });
                canvas.drawRect(getBounds(), placeholderPaint);
                return;
            }

            Rect b;
            if (fullSize) b = getDrawRect();
            else b = getBounds();

            canvas.drawBitmap(bitmaps[drawableInfo.categoryIndex][drawableInfo.emojiCategoryIndex], null, b, paint);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSPARENT;
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter cf) {

        }
    }

    public CharSequence replaceEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics) {
        return replaceEmoji(cs, fontMetrics, 0, false, null);
    }

    public CharSequence replaceEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics, boolean createNew) {
        return replaceEmoji(cs, fontMetrics, 0, createNew, null);
    }

    public CharSequence replaceEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics, int size, boolean createNew) {
        return replaceEmoji(cs, fontMetrics, size, createNew, null);
    }

    private CharSequence replaceEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics, int size, boolean createNew, int[] emojiOnly) {
        if (/*SharedConfig.useSystemEmoji || */cs == null || cs.length() == 0) {
            return cs;
        }
        Spannable s;
        if (!createNew && cs instanceof Spannable) {
            s = (Spannable) cs;
        } else {
            s = Spannable.Factory.getInstance().newSpannable(cs.toString());
        }
        long buf = 0;
        int emojiCount = 0;
        char c;
        int startIndex = -1;
        int startLength = 0;
        int previousGoodIndex = 0;
        StringBuilder emojiCode = new StringBuilder(16);
        EmojiDrawable drawable;
        EmojiSpan span;
        int length = cs.length();
        boolean doneEmoji = false;

        try {
            for (int i = 0; i < length; i++) {
                c = cs.charAt(i);
                if (c >= 0xD83C && c <= 0xD83E || (buf != 0 && (buf & 0xFFFFFFFF00000000L) == 0 && (buf & 0xFFFF) == 0xD83C && (c >= 0xDDE6 && c <= 0xDDFF))) {
                    if (startIndex == -1) {
                        startIndex = i;
                    }
                    emojiCode.append(c);
                    startLength++;
                    buf <<= 16;
                    buf |= c;
                } else if (emojiCode.length() > 0 && (c == 0x2640 || c == 0x2642 || c == 0x2695)) {
                    emojiCode.append(c);
                    startLength++;
                    buf = 0;
                    doneEmoji = true;
                } else if (buf > 0 && (c & 0xF000) == 0xD000) {
                    emojiCode.append(c);
                    startLength++;
                    buf = 0;
                    doneEmoji = true;
                } else if (c == 0x20E3) {
                    if (i > 0) {
                        char c2 = cs.charAt(previousGoodIndex);
                        if ((c2 >= '0' && c2 <= '9') || c2 == '#' || c2 == '*') {
                            startIndex = previousGoodIndex;
                            startLength = i - previousGoodIndex + 1;
                            emojiCode.append(c2);
                            emojiCode.append(c);
                            doneEmoji = true;
                        }
                    }
                } else if ((c == 0x00A9 || c == 0x00AE || c >= 0x203C && c <= 0x3299) /*&& EmojiData.dataCharsMap.containsKey(c)*/) {// TODO: 1/5/20
                    if (startIndex == -1) {
                        startIndex = i;
                    }
                    startLength++;
                    emojiCode.append(c);
                    doneEmoji = true;
                } else if (startIndex != -1) {
                    emojiCode.setLength(0);
                    startIndex = -1;
                    startLength = 0;
                    doneEmoji = false;
                } else if (c != 0xfe0f) {
                    if (emojiOnly != null) {
                        emojiOnly[0] = 0;
                        emojiOnly = null;
                    }
                }
                if (doneEmoji && i + 2 < length) {
                    char next = cs.charAt(i + 1);
                    if (next == 0xD83C) {
                        next = cs.charAt(i + 2);
                        if (next >= 0xDFFB && next <= 0xDFFF) {
                            emojiCode.append(cs.subSequence(i + 1, i + 3));
                            startLength += 2;
                            i += 2;
                        }
                    } else if (emojiCode.length() >= 2 && emojiCode.charAt(0) == 0xD83C && emojiCode.charAt(1) == 0xDFF4 && next == 0xDB40) {
                        i++;
                        while (true) {
                            emojiCode.append(cs.subSequence(i, i + 2));
                            startLength += 2;
                            i += 2;
                            if (i >= cs.length() || cs.charAt(i) != 0xDB40) {
                                i--;
                                break;
                            }
                        }

                    }
                }
                previousGoodIndex = i;
                char prevCh = c;
                for (int a = 0; a < 3; a++) {
                    if (i + 1 < length) {
                        c = cs.charAt(i + 1);
                        if (a == 1) {
                            if (c == 0x200D && emojiCode.length() > 0) {
                                emojiCode.append(c);
                                i++;
                                startLength++;
                                doneEmoji = false;
                            }
                        } else if (startIndex != -1 || prevCh == '*' || prevCh >= '1' && prevCh <= '9') {
                            if (c >= 0xFE00 && c <= 0xFE0F) {
                                i++;
                                startLength++;
                            }
                        }
                    }
                }
                if (doneEmoji && i + 2 < length && cs.charAt(i + 1) == 0xD83C) {
                    char next = cs.charAt(i + 2);
                    if (next >= 0xDFFB && next <= 0xDFFF) {
                        emojiCode.append(cs.subSequence(i + 1, i + 3));
                        startLength += 2;
                        i += 2;
                    }
                }
                if (doneEmoji) {
                    if (emojiOnly != null) {
                        emojiOnly[0]++;
                    }
                    CharSequence code = emojiCode.subSequence(0, emojiCode.length());
                    drawable = getEmojiDrawable(code);
                    if (drawable != null) {
                        // -1 undefined size just for set ALIGN_BASELINE to improve when use staticLayout for caption on message
                        span = new EmojiSpan(drawable, size == -1 ? DynamicDrawableSpan.ALIGN_BASELINE : DynamicDrawableSpan.ALIGN_BOTTOM, size, fontMetrics);
                        s.setSpan(span, startIndex, startIndex + startLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        emojiCount++;
                    }
                    startLength = 0;
                    startIndex = -1;
                    emojiCode.setLength(0);
                    doneEmoji = false;
                }
                if ((Build.VERSION.SDK_INT < 23 || Build.VERSION.SDK_INT >= 29) && emojiCount >= 50) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return cs;
        }
        return s;
    }

    public EmojiDrawable getEmojiDrawable(CharSequence code) {
        DrawableInfo drawableInfo = drawableInfoMap.get(code);
        if (drawableInfo == null) {
            // TODO: 1/5/20
//            CharSequence newCode = EmojiData.emojiAliasMap.get(code);
//            if (newCode != null) {
//                drawableInfo = drawableInfoMap.get(newCode);
//            }
        }
        if (drawableInfo == null) {
            return null;
        }
        EmojiDrawable emojiDrawable = new EmojiDrawable(drawableInfo);
        emojiDrawable.setBounds(0, 0, drawImgSize, drawImgSize);
        return emojiDrawable;
    }

    public EmojiDrawable getEmojiBigDrawable(String code) {
        EmojiDrawable emojiDrawable = getEmojiDrawable(code);
        if (emojiDrawable == null) {
            // TODO: 1/9/20  
//            CharSequence newCode = EmojiData.emojiAliasMap.get(code);
//            if (newCode != null) {
//                emojiDrawable = getEmojiDrawable(newCode);
//            }
        }
        if (emojiDrawable == null) {
            return null;
        }
        emojiDrawable.setBounds(0, 0, bigImgSize, bigImgSize);
        emojiDrawable.fullSize = true;
        return emojiDrawable;
    }

    private void loadEmoji(final byte a, final short b) {
        try {
            int imageResize = 1;
            if (AndroidUtils.density <= 1.0f) {
                imageResize = 2;
            }

            Bitmap bitmap = null;
            try {
                InputStream is = G.context.getAssets().open("emoji/" + String.format(Locale.US, "%d_%d.png", a, b));
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = false;
                opts.inSampleSize = imageResize;
                bitmap = BitmapFactory.decodeStream(is, null, opts);
                is.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            final Bitmap finalBitmap = bitmap;
            bitmaps[a][b] = finalBitmap;
            G.cancelRunOnUiThread(invalidateRunnable);
            G.runOnUiThread(invalidateRunnable);
        } catch (Throwable x) {
            x.printStackTrace();
        }
    }

    public void addRecentEmoji(String code) {
        Integer count = emojiUseHistory.get(code);
        if (count == null) {
            count = 0;
        }
        if (count == 0 && emojiUseHistory.size() >= MAX_RECENT_EMOJI_COUNT) {
            String emoji = recentEmoji.get(recentEmoji.size() - 1);
            emojiUseHistory.remove(emoji);
            recentEmoji.set(recentEmoji.size() - 1, code);
        }
        emojiUseHistory.put(code, ++count);
    }

    public void sortEmoji() {
        recentEmoji.clear();
        for (HashMap.Entry<String, Integer> entry : emojiUseHistory.entrySet()) {
            recentEmoji.add(entry.getKey());
        }
        Collections.sort(recentEmoji, (lhs, rhs) -> {
            Integer count1 = emojiUseHistory.get(lhs);
            Integer count2 = emojiUseHistory.get(rhs);
            if (count1 == null) {
                count1 = 0;
            }
            if (count2 == null) {
                count2 = 0;
            }
            if (count1 > count2) {
                return -1;
            } else if (count1 < count2) {
                return 1;
            }
            return 0;
        });
        while (recentEmoji.size() > MAX_RECENT_EMOJI_COUNT) {
            recentEmoji.remove(recentEmoji.size() - 1);
        }
    }

    public void saveRecentEmoji() {
        SharedPreferences preferences = getGlobalEmojiSettings();
        StringBuilder stringBuilder = new StringBuilder();
        for (HashMap.Entry<String, Integer> entry : emojiUseHistory.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        preferences.edit().putString(EMOJI_VERSION_KEY, stringBuilder.toString()).apply();
    }

    public void clearRecentEmoji() {
        SharedPreferences preferences = getGlobalEmojiSettings();
        preferences.edit().putBoolean(EMOJI_DEFAULT_KEY, true).apply();
        emojiUseHistory.clear();
        recentEmoji.clear();
        saveRecentEmoji();
    }

    public void loadRecentEmoji() {
        if (recentEmojiLoaded) {
            return;
        }
        recentEmojiLoaded = true;
        SharedPreferences preferences = getGlobalEmojiSettings();

        String str;
        try {
            emojiUseHistory.clear();
            str = preferences.getString(EMOJI_VERSION_KEY, "");
            if (str != null && str.length() > 0) {
                String[] args = str.split(",");
                for (String arg : args) {
                    String[] split = arg.split("=");
                    emojiUseHistory.put(split[0], AndroidUtils.parseInt(split[1]));
                }
            }
            if (emojiUseHistory.isEmpty()) {
                if (!preferences.getBoolean(EMOJI_DEFAULT_KEY, false)) {
                    String[] newRecent = new String[]{
                            "\uD83D\uDE02", "\uD83D\uDE18", "\u2764", "\uD83D\uDE0D", "\uD83D\uDE0A", "\uD83D\uDE01",
                            "\uD83D\uDC4D", "\u263A", "\uD83D\uDE14", "\uD83D\uDE04", "\uD83D\uDE2D", "\uD83D\uDC8B",
                            "\uD83D\uDE12", "\uD83D\uDE33", "\uD83D\uDE1C", "\uD83D\uDE48", "\uD83D\uDE09", "\uD83D\uDE03",
                            "\uD83D\uDE22", "\uD83D\uDE1D", "\uD83D\uDE31", "\uD83D\uDE21", "\uD83D\uDE0F", "\uD83D\uDE1E",
                            "\uD83D\uDE05", "\uD83D\uDE1A", "\uD83D\uDE4A", "\uD83D\uDE0C", "\uD83D\uDE00", "\uD83D\uDE0B",
                            "\uD83D\uDE06", "\uD83D\uDC4C", "\uD83D\uDE10", "\uD83D\uDE15"};
                    for (int i = 0; i < newRecent.length; i++) {
                        emojiUseHistory.put(newRecent[i], newRecent.length - i);
                    }
                    preferences.edit().putBoolean(EMOJI_DEFAULT_KEY, true).apply();
                    saveRecentEmoji();
                }
            }
            sortEmoji();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            str = preferences.getString("color", "");
            if (str != null && str.length() > 0) {
                String[] args = str.split(",");
                for (String arg : args) {
                    String[] split = arg.split("=");
                    emojiColor.put(split[0], split[1]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveEmojiColors() {
        SharedPreferences preferences = getGlobalEmojiSettings();
        StringBuilder stringBuilder = new StringBuilder();
        for (HashMap.Entry<String, String> entry : emojiColor.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        preferences.edit().putString("color", stringBuilder.toString()).apply();
    }

    private SharedPreferences getGlobalEmojiSettings() {
        return G.context.getSharedPreferences(SHP_SETTING.EMOJI, Context.MODE_PRIVATE);
    }


    public boolean isValidEmoji(CharSequence code) {
        DrawableInfo info = drawableInfoMap.get(code);
        if (info == null) {// TODO: 1/7/20
//            CharSequence newCode = EmojiData.emojiAliasMap.get(code);
//            if (newCode != null) {
//                drawableInfo = drawableInfoMap.get(newCode);
//            }
        }
        return info != null;
    }

    public ArrayList<String> getRecentEmoji() {
        return recentEmoji;
    }

    public boolean isRecentEmojiLoaded() {
        return recentEmojiLoaded;
    }

    private EmojiManager() {
    }
}
