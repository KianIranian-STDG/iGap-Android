package net.iGap.helper.upload.ApiBased;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import net.iGap.G;
import net.iGap.helper.HelperNumerical;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.reactivex.annotations.NonNull;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Decorates an OkHttp request body to count the number of bytes written when writing it. Can
 * decorate any request body, but is most useful for tracking the upload progress of large
 * multipart requests.
 *
 * @author Leo Nikkilä
 * with modifications made by Paulina Sadowska
 */
public class CountingRequestBody extends RequestBody {

    private RequestBody delegate;
    private Listener listener;
    private boolean addIV = true;
    long encryptSize = 0;
    long originalSize = 0;
    // encryption
    Cipher cipher;
    byte[] ivBytes;
    // for logs
    File temp;
    FileWriter writer;
    ByteArrayOutputStream tempStream = new ByteArrayOutputStream();
    CipherOutputStream cipherOutputStream;

    private boolean isEncryptionActive = true;

    private static final String TAG = "RequestBody http";

    public CountingRequestBody(RequestBody delegate, Listener listener) {
        this.delegate = delegate;
        this.listener = listener;
        // init encryption based on the current IV and key
        initEncrypt(G.symmetricKey);
        // make temp file for logs
        makeFile();
    }

    public CountingRequestBody(RequestBody delegate, Listener listener, File file) {
        this.delegate = delegate;
        this.listener = listener;
        this.temp = file;
        // init encryption based on the current IV and key
        initEncrypt(G.symmetricKey);
        // make temp file for logs
        makeFile();
    }

    public CountingRequestBody(Listener listener) {
        this.listener = listener;
        initEncrypt(G.symmetricKey);
    }

    private void makeFile() {
        // make directory
        File tempq = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/upload");
        if (!tempq.exists())
            tempq.mkdirs();
        // make file
        File temp = new File(tempq, "logs.txt");
        if (temp.exists())
            temp.delete();
        temp = new File(tempq, "logs.txt");
        // make writer
        try {
            writer = new FileWriter(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return super.contentLength();
    }

    /*@Override
    public long contentLength() throws IOException {
        *//*if (encryptSize != 0) {
            Log.d(TAG, "contentLength inside: " + encryptSize);
            return encryptSize;
        }
        return getSizeM2();*//*
        return -1;
    }*/

    private long getSizeM2() {
        try {
            originalSize = delegate.contentLength();
            if (!isEncryptionActive)
                encryptSize = delegate.contentLength();
            if (addIV)
                encryptSize = delegate.contentLength() + 32;
            else
                encryptSize = delegate.contentLength() + 16;
            Log.d(TAG, "contentLength2: " + delegate.contentLength() + " " + encryptSize + " " + isEncryptionActive);
            return encryptSize;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private long getSizeM1() {
        try {
            if (!isEncryptionActive)
                encryptSize = delegate.contentLength();
            if (addIV)
                encryptSize = ((delegate.contentLength() / Long.valueOf("16") + 1) * 16) + 16;
            else
                encryptSize = ((delegate.contentLength() / 16 + 1) * 16);
            Log.d(TAG, "contentLength: " + delegate.contentLength() + " " + encryptSize + " " + isEncryptionActive);
            return encryptSize;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        Log.d(TAG, "writeTo: entry");
        CountingSink countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);
//        progressOutPut progressOutputStream = new progressOutPut(sink.outputStream(), contentLength());
//        BufferedSink bufferedSink = Okio.buffer(Okio.sink(progressOutputStream));
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();
        Log.d(TAG, "writeTo: end");
        // close log file
//        cipherOutputStream.flush();
//        cipherOutputStream.close();
        writer.append(Base64.encodeToString(tempStream.toByteArray(), Base64.DEFAULT));
        writer.flush();
        writer.close();
    }

    class progressOutPut extends OutputStream {

        private final OutputStream stream;

        private long total;
        private long totalWritten;

        progressOutPut(OutputStream stream, long total) {
            this.stream = stream;
            this.total = total;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            Log.d(TAG, "write: " + off + " " + len + " " + b.length);
            this.stream.write(b, off, len);
            if (this.total < 0) {
//                this.listener.onProgressChanged(-1, -1, -1);
                return;
            }
            if (len < b.length) {
                this.totalWritten += len;
            } else {
                this.totalWritten += b.length;
            }
//            this.listener.onProgressChanged(this.totalWritten, this.total, (this.totalWritten * 1.0F) / this.total);
        }

        @Override
        public void write(int b) throws IOException {
            this.stream.write(b);
            if (this.total < 0) {
//                this.listener.onProgressChanged(-1, -1, -1);
                return;
            }
            this.totalWritten++;
//            this.listener.onProgressChanged(this.totalWritten, this.total, (this.totalWritten * 1.0F) / this.total);
        }

        @Override
        public void close() throws IOException {
            if (this.stream != null) {
                this.stream.close();
            }
        }

        @Override
        public void flush() throws IOException {
            if (this.stream != null) {
                this.stream.flush();
            }
        }
    }

    final class CountingSink extends ForwardingSink {

        private long bytesWritten = 0;
        private long originalBytesWritten = 0;
        private boolean addIV = false;

        CountingSink(Sink delegate) {
            super(delegate);
        }

        /*@Override
        public void write(Buffer source, long byteCount) throws IOException {
            long bytesToRead = Math.min(source.size(), byteCount);
            Log.d(TAG, "write: " + bytesToRead + " " + addIV);
             encryptedSink = new Buffer();
             file = new CipherOutputStream(tempStream, cipher);
             stream = new CipherOutputStream(encryptedSink.outputStream(), cipher);
            if (addIV) {
                encryptedSink.write(ivBytes);
                addIV = false;
            }
            byte[] main = source.readByteArray(bytesToRead);
            file.write(main);
            stream.write(main);
            super.write(encryptedSink, encryptedSink.size());
        }*/

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            long bytesToRead = Math.min(source.size(), byteCount);
            byte[] mainByteArray = source.readByteArray(bytesToRead);
            byte[] encryptedByteArray = null;
            if (isEncryptionActive)
                encryptedByteArray = encryptUpload(mainByteArray, addIV);
            else
                encryptedByteArray = mainByteArray;
            Log.d(TAG, "write: **chunk **source " + mainByteArray.length + " **encrypt " + encryptedByteArray.length);
            tempStream.write(mainByteArray);
//            cipherOutputStream.write(mainByteArray);
//            Log.d(TAG, "write: " + Base64.encodeToString(encryptedByteArray, Base64.DEFAULT));
            if (addIV)
                addIV = false;
            Buffer encryptedSink = new Buffer();
            encryptedSink.write(encryptedByteArray);
            bytesWritten += encryptedByteArray.length;
            originalBytesWritten += mainByteArray.length;
            Log.d(TAG, "write: **totalWrite **Encrypt " + bytesWritten + " **original " + originalBytesWritten);
//            listener.onRequestProgress(bytesWritten, 1);
//            if (originalBytesWritten == originalSize) {
//                encryptSize = bytesWritten;
//            }
            super.write(encryptedSink, encryptedSink.size());
        }
    }

    public void initEncrypt(SecretKeySpec key) {
        try {
            cipher = Cipher.getInstance("AES_256/CBC/PKCS5Padding");
            SecureRandom r = new SecureRandom();
            ivBytes = new byte[G.ivSize];
            ivBytes = "1234567890123456".getBytes();
//            System.arraycopy(Base64.decode("MTIzNDU2Nzg5MDEyMzQ1Ng", Base64.NO_PADDING), 0, ivBytes, 0, cipher.getBlockSize());
//            r.nextBytes(ivBytes);
//            Log.d(TAG, "initEncrypt: " + Base64.encodeToString(ivBytes, Base64.DEFAULT));
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            Log.d(TAG, "initEncrypt: IV " + Base64.encodeToString(ivSpec.getIV(), Base64.DEFAULT));
//            byte[] encodedKey = Base64.decode("1TPeILQOA6IIzyLQWZQy0q95WuiAskia", Base64.NO_PADDING);
            SecretKey key2 = new SecretKeySpec("1TPeILQOA6IIzyLQWZQy0q95WuiAskia".getBytes(), "AES");
            Log.d(TAG, "initEncrypt: key " + Base64.encodeToString(key2.getEncoded(), Base64.DEFAULT));
            cipher.init(Cipher.ENCRYPT_MODE, key2/*key*/, ivSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initEncrypt2() {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(256);
            SecretKey key = keygen.generateKey();
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            Log.d(TAG, "initEncrypt2: " + key.getEncoded());
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] encryptUpload(byte[] message, boolean addIv) {
        new CipherOutputStream(null, null);
        try {
            byte[] encryptMessage = cipher.doFinal(message);
            if (addIv) {
                Log.e(TAG, "initEncrypt2: cipher success bitch");
                return HelperNumerical.appendByteArrays(ivBytes, encryptMessage);
            } else {
                Log.e(TAG, "initEncrypt2: cipher success bitch");
                return encryptMessage;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("NO Encryption");
        }
    }

    public interface Listener {
        void onRequestProgress(long bytesWritten, long contentLength);
    }

}
