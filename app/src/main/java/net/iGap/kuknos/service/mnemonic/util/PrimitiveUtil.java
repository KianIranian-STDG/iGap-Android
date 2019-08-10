package net.iGap.kuknos.service.mnemonic.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Util class.
 * Created by cristi.paval on 3/13/18.
 */
public class PrimitiveUtil {
    public static byte[] concatByteArrays(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static char[] bytesToBinaryAsChars(byte[] bytes) {
        StringBuilder binaryStringBuilder = new StringBuilder();
        for (byte b : bytes) {
            binaryStringBuilder.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        int binaryLength = binaryStringBuilder.length();
        char[] binaryChars = new char[binaryLength];
        binaryStringBuilder.getChars(0, binaryLength, binaryChars, 0);
        return binaryChars;
    }

    public static byte[] byteSubArray(byte[] source, int startIndex, int endIndex) {
        byte[] subArray = new byte[endIndex - startIndex];
        System.arraycopy(source, startIndex, subArray, 0, endIndex - startIndex);
        return subArray;
    }

    public static char[] charSubArray(char[] source, int startIndex, int endIndex) {
        char[] subArray = new char[endIndex - startIndex];
        System.arraycopy(source, startIndex, subArray, 0, endIndex - startIndex);
        return subArray;
    }

    public static byte[] last4BytesFromLong(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(x);
        return byteSubArray(buffer.array(), 4, 8);
    }

    public static int binaryCharsToInt(char[] binary) {
        int result = 0;
        for (int i = binary.length - 1; i >= 0; i--)
            if (binary[i] == '1')
                result += Math.pow(2, (binary.length - i - 1));
        return result;
    }

    public static byte[] toBytes(char[] chars) {

        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
    }

    public static char[] concatCharArrays(char[] a, char[] b) {

        char[] c = new char[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
