package com.iGap.helper;

import com.iGap.G;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * HelperNumerical use for processing at numerical formats in java like int , long , byte .etc
 */

public class HelperNumerical {

    /**
     * append firstByteArray with secondByteArray and return single byteArray
     *
     * @param firstByteArray  id byteArray
     * @param secondByteArray proto byteArray
     * @return message byteArray
     */

    public static byte[] appendByteArrays(byte[] firstByteArray, byte[] secondByteArray) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            outputStream.write(firstByteArray);
            outputStream.write(secondByteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    /**
     * order byte to littleEndian
     */

    public static byte[] orderBytesToLittleEndian(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).array();
    }

    /**
     * get Iv from server message
     *
     * @param byteArray server message
     * @param length    ivSize
     */

    public static byte[] getIv(byte[] byteArray, int length) {
        byteArray = Arrays.copyOfRange(byteArray, 0, length);
        return byteArray;
    }

    /**
     * split payload from byteArray
     *
     * @param byteArray message that received from server
     * @return payload
     */

    public static byte[] getMessage(byte[] byteArray) {
        byteArray = Arrays.copyOfRange(byteArray, G.ivSize, byteArray.length);
        return byteArray;
    }

    /**
     * convert byteArray to Hex
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * convert int to byteArray
     *
     * @param value int
     * @return byteArray (size is 2)
     */

    public static byte[] intToByteArray(int value) {
        byte[] bytes = new byte[2];
        for (int i = 0; i < 2; i++) {
            bytes[i] = (byte) (value >>> (i * 8));
        }
        return bytes;
    }
}
