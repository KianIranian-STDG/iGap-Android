/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.module;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.DispatchQueue;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperLog;
import net.iGap.observers.interfaces.OnFileCopyComplete;
import net.iGap.proto.ProtoGlobal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AndroidUtils {
    public static Pattern hashTagLink = Pattern.compile("[#]+[\\p{L}A-Za-z0-9۰-۹٠-٩-_]+\\b");
    public static Pattern atSignLink = Pattern.compile("[@]+[A-za-z0-9]+\\b");
    public static Pattern igapLink = Pattern.compile("(https?\\:\\/\\/)?(?i)(igap.net)/(.*)");
    public static String igapResolve = "igap://resolve?";
    public static Pattern botLink = Pattern.compile("^\\/\\w+");
    public static Pattern webLink = Pattern.compile("([a-z]+)?(://)?([a-z0-9\\-.:]+(?<!igap))(\\.[a-z0-9]+)+/[A-Za-z0-9\\-/_]+");
    public static Pattern webLink_with_port = Pattern.compile("([a-z]+)?(://)?([a-z\\d\\-.]+(?<!igap))(:\\d+/)(/)?[A-Za-z0-9\\-/_]*");
    public static Pattern digitLink = Pattern.compile("^\\s*(?:\\+?(\\d{1,3}))?([-. (]*(\\d{3})[-. )]*)?((\\d{3})[-. ]*(\\d{2,4})(?:[-.x ]*(\\d+))?)\\s*$");
    public static Pattern deepLink = Pattern.compile("(igap?://)([^:^/]*)(:\\d*)?(.*)?");
    public static Pattern specialCharacter = Pattern.compile("^[^\\p{L}\\w!@#$%&*()\\-.\\\\,_+=|<>?{}/^\\]\\[~{}ًٌٍْ.َُِّ\\s]");
    public static Pattern emojiPattern = Pattern.compile("^[^\\p{L}\\w!@#$%&*()\\-.\\\\,_+=|<>?{}/^\\]\\[~{}ًٌٍْ.َُِّ\\s]");

    private AndroidUtils() throws InstantiationException {

        throw new InstantiationException("This class is not for instantiation.");
    }

    public static int getWindowWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static void clearDrawableAnimation(View view) {
        if (Build.VERSION.SDK_INT < 21 || view == null) {
            return;
        }
        Drawable drawable;
        if (view instanceof ListView) {
            drawable = ((ListView) view).getSelector();
            if (drawable != null) {
                drawable.setState(StateSet.NOTHING);
            }
        } else {
            drawable = view.getBackground();
            if (drawable != null) {
                drawable.setState(StateSet.NOTHING);
                drawable.jumpToCurrentState();
            }
        }
    }

    public static String formatDuration(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public static String getAudioArtistName(String filePath) throws IllegalArgumentException {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();

        Uri uri;
        File file = new File(filePath);

        if (file.exists()) {
            uri = Uri.fromFile(file);

            try {
                metaRetriever.setDataSource(G.context, uri);
                return metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            } catch (Exception e) {

            }
        }

        return "";
    }

    public static long getAudioDuration(Context context, String filePath) throws IllegalArgumentException {

        if (filePath == null || filePath.length() == 0) {
            return 1;
        }

        Uri uri;
        File file = new File(filePath);

        if (file.exists()) {
            uri = Uri.fromFile(file);

            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(context, uri);
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                return Integer.parseInt(durationStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 1;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Audio.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static int[] getImageDimens(String filePath) {

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(filePath, options);

            int width = options.outWidth;
            int height = options.outHeight;

            return new int[]{width, height};
        } catch (Exception e) {
            return new int[]{0, 0};
        }
    }

    /**
     * return suitable path for using with UIL
     *
     * @param path String path
     * @return correct local path/passed path
     */
    public static String suitablePath(String path) {
        if (path != null) {
            if (path.matches("\\w+?://")) {
                return path;
            } else {
                String encoded = Uri.fromFile(new File(path)).toString();
                return Uri.decode(encoded);
            }
        }
        return null;
    }

    public static String saveBitmap(Bitmap bmp) {
        FileOutputStream out = null;
        String outPath = G.DIR_TEMP + "/thumb_" + SUID.id().get() + ".jpg";
        try {
            out = new FileOutputStream(outPath);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outPath;
    }

    /**
     * get n bytes from file, starts from beginning
     *
     * @param fileChannel fileChannel
     * @param bytesCount  total bytes
     * @return bytes
     * @throws IOException
     */
    public static byte[] getBytesFromStart(FileChannel fileChannel, int bytesCount) throws IOException {
        // FileChannel has better performance than BufferedInputStream
        fileChannel.position(0);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytesCount);
        fileChannel.read(byteBuffer);

        byteBuffer.flip();

        if (byteBuffer.hasArray()) {
            return byteBuffer.array();
        }
        return null;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void writeBytesToFile(String filePath, byte[] chunk) {
        FileOutputStream fop = null;
        File file;
        try {
            file = new File(filePath);
            fop = new FileOutputStream(file, true);
            fop.write(chunk);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get n bytes from file, starts from end
     *
     * @param fileChannel fileChannel
     * @param bytesCount  total bytes
     * @return bytes
     * @throws IOException
     */
    public static byte[] getBytesFromEnd(FileChannel fileChannel, int bytesCount) throws IOException {
        // FileChannel has better performance than RandomAccessFile
        fileChannel.position(fileChannel.size() - bytesCount);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytesCount);
        fileChannel.read(byteBuffer);

        byteBuffer.flip();

        if (byteBuffer.hasArray()) {
            return byteBuffer.array();
        }
        return null;
    }

    /**
     * get n bytes from specified offset
     *
     * @param fileChannel fileChannel
     * @param offset      start reading from
     * @param bytesCount  total reading bytes
     * @return bytes
     * @throws IOException
     */
    public static byte[] getNBytesFromOffset(FileChannel fileChannel, long offset, int bytesCount) throws IOException {
        // FileChannel has better performance than RandomAccessFile
        fileChannel.position(offset);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytesCount);
        fileChannel.read(byteBuffer);

        byteBuffer.flip();

        if (byteBuffer.hasArray()) {
            return byteBuffer.array();
        }
        return null;
    }

    /**
     * get SHA-256 from file
     * note: our server needs 32 bytes, so always pass true as second parameter.
     *
     * @param fileChannel fileChannel
     * @param fileSize    fileSize
     */
    public static byte[] getFileHash(FileChannel fileChannel, long fileSize) throws NoSuchAlgorithmException, IOException {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = fileToBytes(fileChannel, fileSize);
            return sha256.digest(fileBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getFileHashFromPath(String path) {
        File file = new File(path);
        if (!file.exists()) return null;

        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] hash;
        int read;
        byte[] buffer = new byte[8192];

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            hash = digest.digest();
        } catch (Exception e) {
            return null;
        }

        return hash;
    }

    public static void cutFromTemp(String pathTmp, String newPath) throws IOException {
        File cutTo = new File(newPath);
        File cutFrom = new File(pathTmp);

        copyFile(cutFrom, cutTo,0,null);
        deleteFile(cutFrom);
    }

    public static void copyFile(File src, File dst, int successMessage, OnFileCopyComplete onFileCopyComplete) throws IOException {
        InputStream in = new FileInputStream(src);

        copyFile(in, dst, successMessage, onFileCopyComplete);
    }

    public static void copyFile(InputStream in, File dst,int successMessage,OnFileCopyComplete onFileCopyComplete) throws IOException {

        OutputStream out = new FileOutputStream(dst);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len = 0;

                    while (true) {
                        if (!((len = in.read(buf)) > 0)) break;
                        out.write(buf, 0, len);
                    }

                    in.close();
                    out.close();

                    if (successMessage != 0 && onFileCopyComplete != null) {
                        G.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onFileCopyComplete.complete(successMessage);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static boolean deleteFile(File src) {
        return src.delete();
    }

    public static String suitableAppFilePath(ProtoGlobal.RoomMessageType messageType) {
        switch (messageType) {
            case AUDIO:
            case AUDIO_TEXT:
            case VOICE:
                return G.DIR_AUDIOS;
            case FILE:
            case FILE_TEXT:
                return G.DIR_DOCUMENT;
            case IMAGE:
            case IMAGE_TEXT:
            case STICKER:
            case GIF:
            case GIF_TEXT:
                return G.DIR_IMAGES;
            case VIDEO:
            case VIDEO_TEXT:
                return G.DIR_VIDEOS;
            default:
                return G.DIR_APP;
        }
    }

    /**
     * convert bytes to human readable length
     *
     * @param bytes bytes
     * @param si    Boolean : true is in Binary Mode and false is Decimal Mode
     * @return String
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = (!si ? 1000 : 1024);
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * return file to bytes
     *
     * @param fileChannel fileChannel
     * @return bytes
     * @throws IOException
     */
    public static byte[] fileToBytes(FileChannel fileChannel, long fileSize) throws IOException, OutOfMemoryError, RuntimeException {
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileSize);
        fileChannel.read(byteBuffer);

        byteBuffer.flip();

        if (byteBuffer.hasArray()) {
            return byteBuffer.array();
        }
        return null;
    }

    public static int[] scaleDimenWithSavedRatio(Context context, float width, float height, ProtoGlobal.Room.Type roomType) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        float density = display.density * 0.9f;
        float maxWidth;
        if (roomType == ProtoGlobal.Room.Type.CHANNEL || roomType == ProtoGlobal.Room.Type.CHAT) {
            maxWidth = context.getResources().getDimension(R.dimen.dp240);
        } else {
            maxWidth = context.getResources().getDimension(R.dimen.dp200);
        }
        float newWidth;
        float newHeight;

        if (width < maxWidth) {
            newWidth = width * density;

           /* if (newWidth > maxWidth) {
                if (maxWidth < width) {
                    newWidth = maxWidth;
                } else {
                    newWidth = width;
                }
            }*/

            while (newWidth > maxWidth) {
                newWidth = (newWidth * 90) / 100;
            }
        } else {
            newWidth = maxWidth;
        }

        newHeight = Math.round((height / width) * newWidth);

        return new int[]{Math.round(newWidth), Math.round(newHeight)};
    }

    /**
     * @param s mixed language text
     * @return true if text is RTL
     */
    public static boolean isTextRtl(String s) {
        if (TextUtils.isEmpty(s)) {
            return false;
        }

        int c = s.codePointAt(0);
        return c >= 0x0600 && c <= 0x06FF;
    }

    public static void setBackgroundShapeColor(View view, int color) {

        Drawable background = view.getBackground();
        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(color);
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(color);
        }
    }

    //*****************************************************************************************************************
    private static String makeSHA1Hash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.reset();

        byte[] buffer;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            buffer = input.getBytes(StandardCharsets.UTF_8);
        } else {
            buffer = input.getBytes(Charset.forName("UTF-8"));
        }
        md.update(buffer);
        byte[] digest = md.digest();

        StringBuilder hexStr = new StringBuilder();
        for (byte aDigest : digest) {
            hexStr.append(Integer.toString((aDigest & 0xff) + 0x100, 16).substring(1));
        }
        return hexStr.toString();
    }

    public static String getFilePathWithCashId(String cashId, String name, int messageType) {
        return getFilePathWithCashId(cashId, name, ProtoGlobal.RoomMessageType.forNumber(messageType));
    }

    public static String getFilePathWithCashId(String cashId, String name, ProtoGlobal.RoomMessageType messageType) {

        String _hash = cashId;
        String _mimeType = "";

        int index = name.lastIndexOf(".");
        if (index >= 0) {
            _mimeType = name.substring(index);
        }

        if (messageType == ProtoGlobal.RoomMessageType.IMAGE || messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
            if (_mimeType.equals("")) {
                _mimeType = ".jpg";
            }
        }

        try {
            if (cashId != null && cashId.length() > 0) {
                _hash = makeSHA1Hash(cashId);
            }
        } catch (Exception e) {

        }

        return suitableAppFilePath(messageType) + "/" + _hash + _mimeType;
    }

    public static String getFilePathWithCashId(String cashId, String name, String selectDir, boolean isThumbNail) {

        String _hash = cashId;
        String _mimeType = "";

        if (isThumbNail) {

            _mimeType = ".jpg";
        } else {
            int index = name.lastIndexOf(".");
            if (index >= 0) {
                _mimeType = name.substring(index);
            }
        }

        String _result = "";

        try {
            if (cashId != null && cashId.length() > 0) {
                _hash = makeSHA1Hash(cashId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isThumbNail) {
            _result = selectDir + "/" + "thumb_" + _hash + _mimeType;
        } else {
            _result = selectDir + "/" + _hash + _mimeType;
        }

        // AppUtils.suitableThumbFileName(name);

        return _result;
    }

    //*****************************************************************************************************************

    public static void closeKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (IllegalStateException e) {
            e.getStackTrace();
        }
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }


    /**
     * check latest activity or base activity from latest open fragment currently is running or no.
     * Hint: usage of this method is for related action to open activity. for example: open alert dialog,...
     **/
    public static Boolean isActivityRunning() {
        if (G.fragmentActivity != null && !G.fragmentActivity.isFinishing()) {
            return true;
        } else if (G.currentActivity != null && !G.currentActivity.isFinishing()) {
            return true;
        } else {
            HelperLog.getInstance().setErrorLog(new Exception("Please check ! isActivityRunning After Fix 1 Cu" + (G.currentActivity == null) + "fa:" + (G.fragmentActivity == null)));
            return false;
        }
    }

    public static boolean canOpenDialog() {
        return isActivityRunning();
    }

    public static boolean showKeyboard(View view) {
        if (view == null) {
            return false;
        }
        try {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                return inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isKeyboardShowed(View view) {
        if (view == null) {
            return false;
        }
        try {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            return inputManager.isActive(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!imm.isActive()) {
                return;
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int statusBarHeight = 0;
    public static Point displaySize = new Point();

    private static Field mAttachInfoField;
    private static Field mStableInsetsField;

    public static float density = 1;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static boolean usingKeyboardInput;


    public static int getViewInset(View view) {
        if (view == null || Build.VERSION.SDK_INT < 21 || view.getHeight() == AndroidUtils.displaySize.y || view.getHeight() == AndroidUtils.displaySize.y - statusBarHeight) {
            return 0;
        } else if (Build.VERSION.SDK_INT > 28) {
            return view.getRootWindowInsets().getStableInsetBottom();
        }
        try {
            if (mAttachInfoField == null) {
                mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
                mAttachInfoField.setAccessible(true);
            }
            Object mAttachInfo = mAttachInfoField.get(view);
            if (mAttachInfo != null) {
                if (mStableInsetsField == null) {
                    mStableInsetsField = mAttachInfo.getClass().getDeclaredField("mStableInsets");
                    mStableInsetsField.setAccessible(true);
                }
                Rect insets = (Rect) mStableInsetsField.get(mAttachInfo);
                return insets.bottom;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    static {
        checkDisplaySize(G.context, null);
    }

    public static void checkDisplaySize(Context context, Configuration newConfiguration) {
        try {

            density = context.getResources().getDisplayMetrics().density;

            Configuration configuration = newConfiguration;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }

            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            usingKeyboardInput = configuration.keyboard != Configuration.KEYBOARD_NOKEYS && configuration.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO;

            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getMetrics(displayMetrics);
                    display.getSize(displaySize);
                }
            }
            if (configuration.screenWidthDp != Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenWidthDp * density);
                if (Math.abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize;
                }
            }
            if (configuration.screenHeightDp != Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenHeightDp * density);
                if (Math.abs(displaySize.y - newSize) > 3) {
                    displaySize.y = newSize;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static volatile DispatchQueue globalQueue = new DispatchQueue("globalQueue");
    public static Pattern pattern = Pattern.compile("[\\-0-9]+");
    private static String adjustOwnerClassname;

    public static Integer parseInt(CharSequence value) {
        if (value == null) {
            return 0;
        }
        int val = 0;
        try {
            Matcher matcher = pattern.matcher(value);
            if (matcher.find()) {
                String num = matcher.group(0);
                val = Integer.parseInt(num);
            }
        } catch (Exception ignore) {

        }
        return val;
    }

    public static Long parseLong(String value) {
        if (value == null) {
            return 0L;
        }
        long val = 0L;
        try {
            Matcher matcher = pattern.matcher(value);
            if (matcher.find()) {
                String num = matcher.group(0);
                val = Long.parseLong(num);
            }
        } catch (Exception ignore) {

        }
        return val;
    }


    public static void requestAdjustResize(Activity activity, String className) {
        if (activity == null || G.twoPaneMode) {
            return;
        }
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        adjustOwnerClassname = className;
    }

    public static void setAdjustResizeToNothing(Activity activity, String className) {
        if (activity == null || G.twoPaneMode) {
            return;
        }
        if (adjustOwnerClassname != null && adjustOwnerClassname.equals(className)) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        }
    }

    public static void removeAdjustResize(Activity activity, String className) {
        if (activity == null || G.twoPaneMode) {
            return;
        }
        if (adjustOwnerClassname != null && adjustOwnerClassname.equals(className)) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    public static String compatibleUnicode(String entry) {
        return HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(entry)) : entry;
    }

    public static String formatShortDuration(int duration) {
        return formatDuration(duration, false);
    }

    public static String formatLongDuration(int duration) {
        return formatDuration(duration, true);
    }

    private static String formatDuration(int duration, boolean isLong) {
        int h = duration / 3600;
        int m = duration / 60 % 60;
        int s = duration % 60;
        if (h == 0) {
            if (isLong) {
                return String.format(Locale.US, "%02d:%02d", m, s);
            } else {
                return String.format(Locale.US, "%d:%02d", m, s);
            }
        } else {
            return String.format(Locale.US, "%d:%02d:%02d", h, m, s);
        }
    }

    public static String MD5(String md5) {
        if (md5 == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(AndroidUtils.getStringBytes(md5));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            FileLog.e(e);
        }
        return null;
    }

    public static byte[] getStringBytes(String src) {
        try {
            return src.getBytes("UTF-8");
        } catch (Exception ignore) {

        }
        return new byte[0];
    }

    public static void copyFileToDownload(File src, File dst, CopyFileCompleted copyFileCompleted) {
        AndroidUtils.globalQueue.postRunnable(() -> {
            try {
                InputStream in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(dst);
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                G.runOnUiThread(copyFileCompleted::onCompleted);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public interface CopyFileCompleted {
        void onCompleted();
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}