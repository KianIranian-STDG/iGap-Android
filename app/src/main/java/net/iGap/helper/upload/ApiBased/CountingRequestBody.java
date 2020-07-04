package net.iGap.helper.upload.ApiBased;

import android.util.Base64;
import android.util.Log;

import net.iGap.G;
import net.iGap.module.AESCrypt;

import java.io.IOException;

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

    private boolean isEncryptionActive = true;

    private static final String TAG = "RequestBody http";

    public CountingRequestBody(RequestBody delegate, Listener listener) {
        this.delegate = delegate;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        if (encryptSize != 0) {
            Log.d(TAG, "contentLength inside: " + encryptSize);
            return encryptSize;
        }
        return getSizeM2();
    }

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

        delegate.writeTo(bufferedSink);

        bufferedSink.flush();
        Log.d(TAG, "writeTo: end");
    }

    final class CountingSink extends ForwardingSink {

        private long bytesWritten = 0;
        private long originalBytesWritten = 0;
        private boolean addIV = true;

        CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            Log.d(TAG, "write: forward sink: **encrypt: " + isEncryptionActive + " **IV: " + addIV);
            try {
                long bytesToRead = Math.min(source.size(), byteCount);

                byte[] mainByteArray = source.readByteArray(bytesToRead);
                byte[] encryptedByteArray = null;
                if (isEncryptionActive)
                    encryptedByteArray = AESCrypt.encryptUpload(G.symmetricKey, mainByteArray, addIV);
                else
                    encryptedByteArray = mainByteArray;
                Log.d(TAG, "write: **source " + mainByteArray.length + " **encrypt " + encryptedByteArray.length);
                if (addIV)
                    addIV = false;
                Buffer encryptedSink = new Buffer();
                encryptedSink.write(encryptedByteArray);
                Log.d(TAG, "write: " + Base64.encodeToString(encryptedByteArray, Base64.DEFAULT));
                bytesWritten += encryptedByteArray.length;
                originalBytesWritten += mainByteArray.length;
                Log.d(TAG, "write: **totalWrite " + bytesWritten + " " + originalBytesWritten);
                listener.onRequestProgress(bytesWritten, encryptSize);
                if (originalBytesWritten == originalSize) {
                    encryptSize = bytesWritten;
                }
                super.write(encryptedSink, encryptedSink.size());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface Listener {
        void onRequestProgress(long bytesWritten, long contentLength);
    }

}
