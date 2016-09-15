package com.iGap.module;


import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Utils {
    private Utils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation.");
    }

    public static int getWindowWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    /**
     * get n bytes from file, starts from beginning
     *
     * @param filePath   file path
     * @param bytesCount total bytes
     * @return bytes
     * @throws IOException
     */
    public static byte[] getBytesFromStart(String filePath, int bytesCount) throws IOException {
        // FileChannel has better performance than BufferedInputStream
        FileChannel fileChannel = new RandomAccessFile(new File(filePath), "r").getChannel();
        fileChannel.position(0);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytesCount);
        fileChannel.read(byteBuffer);
        return byteBuffer.array();
    }

    /**
     * get n bytes from file, starts from end
     *
     * @param filePath   file path
     * @param bytesCount total bytes
     * @return bytes
     * @throws IOException
     */
    public static byte[] getBytesFromEnd(String filePath, int bytesCount) throws IOException {
        // FileChannel has better performance than RandomAccessFile
        FileChannel fileChannel = new RandomAccessFile(new File(filePath), "r").getChannel();
        fileChannel.position(fileChannel.size() - bytesCount);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytesCount);
        fileChannel.read(byteBuffer);
        return byteBuffer.array();
    }

    /**
     * get n bytes from specified offset
     *
     * @param filePath   file path
     * @param offset     start reading from
     * @param bytesCount total reading bytes
     * @return bytes
     * @throws IOException
     */
    public static byte[] getNBytesFromOffset(String filePath, int offset, int bytesCount) throws IOException {
        // FileChannel has better performance than RandomAccessFile
        FileChannel fileChannel = new RandomAccessFile(new File(filePath), "r").getChannel();
        fileChannel.position(offset);
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytesCount);
        fileChannel.read(byteBuffer);
        return byteBuffer.array();
    }

    /**
     * get SHA-256 from file
     * note: our server needs 32 bytes, so always pass true as second parameter.
     *
     * @param file             File
     * @param convertTo32Bytes by default the output is 64 bytes, if you want to convert output to 32 bytes, pass true
     */
    public static String getFileHash(File file, boolean convertTo32Bytes) throws NoSuchAlgorithmException, IOException {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = fileToBytes(file);
            byte[] fileHash = sha256.digest(fileBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : fileHash) {
                if (convertTo32Bytes) {
                    sb.append(Integer.toString(((b & 0xff) + 0x100) / 8, 16).substring(1));
                } else {
                    sb.append(Integer.toString(((b & 0xff) + 0x100), 16).substring(1));
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * return file to bytes
     *
     * @param file File
     * @return bytes
     * @throws IOException
     */
    public static byte[] fileToBytes(File file) throws IOException {
        FileChannel fileChannel = new RandomAccessFile(file, "r").getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
        fileChannel.read(byteBuffer);
        return byteBuffer.array();
   /*     int size = (int) file.length();
        byte[] bytes = new byte[size];
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
        buf.read(bytes, 0, bytes.length);
        buf.close();
        return bytes;*/
    }
}
