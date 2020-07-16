package net.iGap.module.upload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

public class CipherStreamWithIv extends CipherInputStream {
    private AtomicBoolean ivAdded = new AtomicBoolean(false);
    private byte[] iv = "abcdefghijklmnop".getBytes();
    public CipherStreamWithIv(InputStream is, Cipher c) {
        super(is, c);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (!ivAdded.get() && len > 16) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(iv);
            int count = super.read(b, off, len - 16);
            byteArrayOutputStream.write(b, off, len - 16);
            b = byteArrayOutputStream.toByteArray();

            ivAdded.set(true);
            return count + 16;
        }
        return super.read(b, off, len);
    }
}
