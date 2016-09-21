package com.iGap.module;


import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

import java.io.IOException;
import java.nio.ByteBuffer;
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
     * @param uploadStructure  FileUploadStructure
     * @param convertTo32Bytes by default the output is 64 bytes, if you want to convert output to 32 bytes, pass true
     */
    public static String getFileHash(FileUploadStructure uploadStructure, boolean convertTo32Bytes) throws NoSuchAlgorithmException, IOException {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = fileToBytes(uploadStructure);
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
}