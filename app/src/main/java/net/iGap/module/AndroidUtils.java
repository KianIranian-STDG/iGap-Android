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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseArray;
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
import net.iGap.messenger.ui.toolBar.AlertDialog;
import net.iGap.proto.ProtoGlobal;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.webrtc.ContextUtils.getApplicationContext;

public final class AndroidUtils {
    private static SparseArray<File> mediaDirs = null;
    public static final int MEDIA_DIR_CACHE = 4;
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

        if (filePath != null && !filePath.isEmpty()) {
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

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
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
        if (!cutTo.exists()) {
            cutTo.createNewFile();
        }
        File cutFrom = new File(pathTmp);
        copyFile(cutFrom, cutTo);
        deleteFile(cutFrom);
    }

    public static void saveFile(String fullPath, Context context, final int type, final String name, final String mime) {
        saveFile(fullPath, context, type, name, mime, null);
    }

    public static void saveFile(String fullPath, Context context, final int type, final String name, final String mime, final Runnable onSaved) {
        if (fullPath == null) {
            return;
        }

        File file = null;
        if (!TextUtils.isEmpty(fullPath)) {
            file = new File(fullPath);
            if (!file.exists() /*|| isInternalUri(Uri.fromFile(file))*/) {
                file = null;
            }
        }

        if (file == null) {
            return;
        }

        final File sourceFile = file;
        final boolean[] cancelled = new boolean[]{false};
        if (sourceFile.exists()) {
            AlertDialog progressDialog = null;
            final boolean[] finished = new boolean[1];
            if (context != null && type != 0) {
                try {
                    final AlertDialog dialog = new AlertDialog(G.currentActivity, 2);
                    dialog.setMessage(context.getString(R.string.Loading));
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(true);
                    dialog.setOnCancelListener(d -> cancelled[0] = true);
                    G.runOnUiThread(() -> {
                        if (!finished[0]) {
                            dialog.show();
                        }
                    }, 250);
                    progressDialog = dialog;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            final AlertDialog finalProgress = progressDialog;

            new Thread(() -> {
                try {
                    File destFile;
                    if (type == 0) {
                        destFile = generatePicturePath(false, getFileExtension(sourceFile));
                    } else if (type == 1) {
                        destFile = generateVideoPath();
                    } else {
                        File dir;
                        if (type == 2) {
                            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        } else {
                            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                        }
                        dir.mkdir();
                        destFile = new File(dir, name);
                        if (destFile.exists()) {
                            int idx = name.lastIndexOf('.');
                            for (int a = 0; a < 10; a++) {
                                String newName;
                                if (idx != -1) {
                                    newName = name.substring(0, idx) + "(" + (a + 1) + ")" + name.substring(idx);
                                } else {
                                    newName = name + "(" + (a + 1) + ")";
                                }
                                destFile = new File(dir, newName);
                                if (!destFile.exists()) {
                                    break;
                                }
                            }
                        }
                    }
                    if (!destFile.exists()) {
                        destFile.createNewFile();
                    }
                    boolean result = true;
                    long lastProgress = System.currentTimeMillis() - 500;
                    try (FileInputStream inputStream = new FileInputStream(sourceFile); FileChannel source = inputStream.getChannel(); FileChannel destination = new FileOutputStream(destFile).getChannel()) {
                        long size = source.size();
                        try {
                            @SuppressLint("DiscouragedPrivateApi") Method getInt = FileDescriptor.class.getDeclaredMethod("getInt$");
                            int fdint = (Integer) getInt.invoke(inputStream.getFD());
                            /*if (isInternalUri(fdint)) {
                                if (finalProgress != null) {
                                    G.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                finalProgress.dismiss();
                                            } catch (Exception e) {
                                                FileLog.e(e);
                                            }
                                        }
                                    });
                                }
                                return;
                            }*/
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                        for (long a = 0; a < size; a += 4096) {
                            if (cancelled[0]) {
                                break;
                            }
                            destination.transferFrom(source, a, Math.min(4096, size - a));
                            if (finalProgress != null) {
                                if (lastProgress <= System.currentTimeMillis() - 500) {
                                    lastProgress = System.currentTimeMillis();
                                    final int progress = (int) ((float) a / (float) size * 100);
                                    G.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                finalProgress.setProgress(progress);
                                            } catch (Exception e) {
                                                FileLog.e(e);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                        result = false;
                    }
                    if (cancelled[0]) {
                        destFile.delete();
                        result = false;
                    }

                    if (result) {
                        if (type == 2) {
                            DownloadManager downloadManager = (DownloadManager) G.context.getSystemService(Context.DOWNLOAD_SERVICE);
                            downloadManager.addCompletedDownload(destFile.getName(), destFile.getName(), false, mime, destFile.getAbsolutePath(), destFile.length(), true);
                        } else {
                            addMediaToGallery(Uri.fromFile(destFile));
                        }
                        if (onSaved != null) {
                            G.runOnUiThread(onSaved);
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (finalProgress != null) {
                    G.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (finalProgress.isShowing()) {
                                    finalProgress.dismiss();
                                } else {
                                    finished[0] = true;
                                }
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                    });
                }
            }).start();
        }
    }

    public static void addMediaToGallery(Uri uri) {
        if (uri == null) {
            return;
        }
        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(uri);
            G.context.sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static boolean isInternalUri(Uri uri) {
        return isInternalUri(uri, 0);
    }

    public static boolean isInternalUri(int fd) {
        return isInternalUri(null, fd);
    }

    private static boolean isInternalUri(Uri uri, int fd) {
        String pathString;
        if (uri != null) {
            pathString = uri.getPath();
            if (pathString == null) {
                return false;
            }
            // Allow sending VoIP logs from cache/voip_logs
            if (pathString.matches(Pattern.quote(new File(G.context.getCacheDir(), "voip_logs").getAbsolutePath()) + "/\\d+\\.log")) {
                return false;
            }
            int tries = 0;
            while (true) {
                if (pathString != null && pathString.length() > 4096) {
                    return true;
                }
                String newPath;
                try {
                    newPath = readlink(pathString);
                } catch (Throwable e) {
                    return true;
                }
                if (newPath == null || newPath.equals(pathString)) {
                    break;
                }
                pathString = newPath;
                tries++;
                if (tries >= 10) {
                    return true;
                }
            }
        } else {
            pathString = "";
            int tries = 0;
            while (true) {
                if (pathString != null && pathString.length() > 4096) {
                    return true;
                }
                String newPath;
                try {
                    newPath = readlinkFd(fd);
                } catch (Throwable e) {
                    return true;
                }
                if (newPath == null || newPath.equals(pathString)) {
                    break;
                }
                pathString = newPath;
                tries++;
                if (tries >= 10) {
                    return true;
                }
            }
        }
        if (pathString != null) {
            try {
                String path = new File(pathString).getCanonicalPath();
                if (path != null) {
                    pathString = path;
                }
            } catch (Exception e) {
                pathString.replace("/./", "/");
                //igonre
            }
        }
        if (pathString.endsWith(".attheme")) {
            return false;
        }
        return pathString != null && pathString.toLowerCase().contains("/data/data/" + G.context.getPackageName());
    }

    public static File generatePicturePath() {
        return generatePicturePath(false, null);
    }

    public static File generatePicturePath(boolean secretChat, String ext) {
        try {
            File storageDir = getAlbumDir(secretChat);
            Date date = new Date();
            date.setTime(System.currentTimeMillis() + random.nextInt(1000) + 1);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date);
            return new File(storageDir, "IMG_" + timeStamp + "." + (TextUtils.isEmpty(ext) ? "jpg" : ext));
        } catch (Exception e) {
            FileLog.e(e);
        }
        return null;
    }

    public static SecureRandom random = new SecureRandom();

    public static File generateVideoPath() {
        return generateVideoPath(false);
    }

    public static File generateVideoPath(boolean secretChat) {
        try {
            File storageDir = getAlbumDir(secretChat);
            Date date = new Date();
            date.setTime(System.currentTimeMillis() + random.nextInt(1000) + 1);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(date);
            return new File(storageDir, "VID_" + timeStamp + ".mp4");
        } catch (Exception e) {
            FileLog.e(e);
        }
        return null;
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf('.') + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public native static String readlink(String path);

    public native static String readlinkFd(int fd);

    private static File getAlbumDir(boolean secretChat) {
        if (secretChat || Build.VERSION.SDK_INT >= 23 && G.context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return getDirectory(MEDIA_DIR_CACHE);
        }
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "iGap");
            if (!storageDir.mkdirs()) {
                if (!storageDir.exists()) {
                    FileLog.e("failed to create directory");
                    return null;
                }
            }
        } else {
            FileLog.e("External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    public static void setMediaDirs(SparseArray<File> dirs) {
        mediaDirs = dirs;
    }

    public static File checkDirectory(int type) {
        return mediaDirs.get(type);
    }

    public static File getDirectory(int type) {
        File dir = mediaDirs.get(type);
        if (dir == null && type != MEDIA_DIR_CACHE) {
            dir = mediaDirs.get(MEDIA_DIR_CACHE);
        }
        try {
            if (dir != null && !dir.isDirectory()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
            //don't promt
        }
        return dir;
    }

    public static void copyFile(InputStream in, File dst) throws IOException {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        copyFile(in, dst);
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
            case STORY:
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

    public static Bitmap blurImage(Bitmap input) {
        try {
            RenderScript rsScript = RenderScript.create(getApplicationContext());
            Allocation alloc = Allocation.createFromBitmap(rsScript, input);

            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript, Element.U8_4(rsScript));
            blur.setRadius(21);
            blur.setInput(alloc);

            Bitmap result = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
            Allocation outAlloc = Allocation.createFromBitmap(rsScript, result);

            blur.forEach(outAlloc);
            outAlloc.copyTo(result);

            rsScript.destroy();
            return result;
        } catch (Exception e) {
            // TODO: handle exception
            return input;
        }

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