package com.iGap.module;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.iGap.G;
import com.iGap.R;
import com.iGap.proto.ProtoGlobal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class AndroidUtils {
    private AndroidUtils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation.");
    }

    public static int getWindowWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static String getAudioArtistName(String filePath) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(filePath);
        return metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
    }

    public static long getAudioDuration(Context context, String filePath) {
        Uri uri = Uri.parse(filePath);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Integer.parseInt(durationStr);
    }

    public static int[] getImageDimens(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        return new int[]{width, height};
    }

    /**
     * return suitable path for using with UIL
     *
     * @param path String path
     * @return correct local path/passed path
     */
    public static String suitablePath(String path) {
        if (path.matches("\\w+?://")) {
            return path;
        } else {
            return Uri.fromFile(new File(path)).toString();
        }
    }

    /**
     * get n bytes from file, starts from beginning
     *
     * @param uploadStructure FileUploadStructure
     * @param bytesCount      total bytes
     * @return bytes
     * @throws IOException
     */
    public static byte[] getBytesFromStart(FileUploadStructure uploadStructure, int bytesCount) throws IOException {
        // FileChannel has better performance than BufferedInputStream
        uploadStructure.fileChannel.position(0);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytesCount);
        uploadStructure.fileChannel.read(byteBuffer);

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

    public static void writeBytesToFile(String filePath, byte[] chunk, int offset) {
        FileOutputStream fop = null;
        File file;
        try {
            Log.i("GGG", "filePath : " + filePath);
            file = new File(filePath);
            Log.i("GGG", "file.getPath : " + file.getPath());
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
     * @param uploadStructure FileUploadStructure
     * @param bytesCount      total bytes
     * @return bytes
     * @throws IOException
     */
    public static byte[] getBytesFromEnd(FileUploadStructure uploadStructure, int bytesCount) throws IOException {
        // FileChannel has better performance than RandomAccessFile
        uploadStructure.fileChannel.position(uploadStructure.fileChannel.size() - bytesCount);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytesCount);
        uploadStructure.fileChannel.read(byteBuffer);

        byteBuffer.flip();

        if (byteBuffer.hasArray()) {
            return byteBuffer.array();
        }
        return null;
    }

    /**
     * get n bytes from specified offset
     *
     * @param uploadStructure FileUploadStructure
     * @param offset          start reading from
     * @param bytesCount      total reading bytes
     * @return bytes
     * @throws IOException
     */
    public static byte[] getNBytesFromOffset(FileUploadStructure uploadStructure, int offset, int bytesCount) throws IOException {
        // FileChannel has better performance than RandomAccessFile
        uploadStructure.fileChannel.position(offset);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytesCount);
        uploadStructure.fileChannel.read(byteBuffer);

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
     * @param uploadStructure FileUploadStructure
     */
    public static byte[] getFileHash(FileUploadStructure uploadStructure) throws NoSuchAlgorithmException, IOException {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = fileToBytes(uploadStructure);
            return sha256.digest(fileBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void cutFromTemp(ProtoGlobal.RoomMessageType messageType, String fileName) throws IOException {
        File cutTo = new File(suitableAppFilePath(messageType) + "/" + fileName);
        File cutFrom = new File(G.DIR_TEMP + "/" + fileName);

        copyFile(cutFrom, cutTo);
        deleteFile(cutFrom);
    }

    public static void cutFromTemp(String fileName) throws IOException {
        File cutTo = new File(G.DIR_IMAGE_USER + "/" + fileName);
        File cutFrom = new File(G.DIR_TEMP + "/" + fileName);

        copyFile(cutFrom, cutTo);
        deleteFile(cutFrom);
    }

    public static void copyFile(File src, File dst) throws IOException {
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
    }

    private static boolean deleteFile(File src) {
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
     * @param si    Boolean
     * @return String
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * return file to bytes
     *
     * @param uploadStructure FileUploadStructure
     * @return bytes
     * @throws IOException
     */
    public static byte[] fileToBytes(FileUploadStructure uploadStructure) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) uploadStructure.fileSize);
        uploadStructure.fileChannel.read(byteBuffer);

        byteBuffer.flip();

        if (byteBuffer.hasArray()) {
            return byteBuffer.array();
        }
        return null;
    }

    public static int[] scaleDimenWithSavedRatio(Context context, int width, int height) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int density = Math.round(display.density * 0.9f);
        float newWidth;
        int newHeight;
        float maxWidth = context.getResources().getDimension(R.dimen.dp300) - (context.getResources().getDimension(R.dimen.messageContainerPadding) * 4);

        if (width < maxWidth) {
            newHeight = height * density;
            newWidth = width * density;

            if (newWidth > maxWidth) {
                newHeight = (int) (height * (newWidth / width)) / density;
                newWidth = maxWidth;
            }

        } else {
            newWidth = maxWidth;
            int calculatedFromWidth = (int) (width - maxWidth);
            newHeight = height - calculatedFromWidth;
        }


        return new int[]{(int) newWidth, newHeight};
    }

    /**
     * return file to bytes
     *
     * @return bytes
     * @throws IOException
     */
    public static byte[] fileToBytes(File file) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        FileChannel fileChannel = randomAccessFile.getChannel();
        fileChannel.read(byteBuffer);

        byteBuffer.flip();

        if (byteBuffer.hasArray()) {
            return byteBuffer.array();
        }
        return null;
    }
}