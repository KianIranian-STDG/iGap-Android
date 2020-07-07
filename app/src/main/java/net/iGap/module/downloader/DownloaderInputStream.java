package net.iGap.module.downloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

public class DownloaderInputStream extends CipherInputStream {
    private FileOutputStream fileOutputStream;
    public DownloaderInputStream(InputStream is, Cipher c, File file) throws FileNotFoundException {
        super(is, c);
        fileOutputStream = new FileOutputStream(file, true);
    }

    @Override
    public int read(byte[] b) throws IOException {
        int result = super.read(b);
        if (result == -1)
            return result;

        fileOutputStream.write(b, 0, result);
        return result;
    }
}
