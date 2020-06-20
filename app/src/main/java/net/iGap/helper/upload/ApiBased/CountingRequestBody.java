package net.iGap.helper.upload.ApiBased;

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

    private boolean isEncryptionActive = false;

    private static final String TAG = "RequestBody http";

    public CountingRequestBody(RequestBody delegate) {
        this.delegate = delegate;
    }

    public CountingRequestBody(RequestBody delegate, boolean addIV, Listener listener) {
        this.delegate = delegate;
        this.listener = listener;
        this.addIV = addIV;
    }

    public CountingRequestBody(RequestBody delegate, Listener listener) {
        this.delegate = delegate;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        Log.d(TAG, "contentType: " + delegate.contentType().toString());
        return delegate.contentType(); //MediaType.parse("application/octet-stream");
    }

    @Override
    public long contentLength() {
        try {
            Log.d(TAG, "contentLength: " + delegate.contentLength() + " " + (((delegate.contentLength() / 16 + 1) * 16) + 16) + " " + isEncryptionActive);
            if (!isEncryptionActive)
                return delegate.contentLength();
            if (addIV)
                return ((delegate.contentLength() / 16 + 1) * 16) + 16;
            else
                return ((delegate.contentLength() / 16 + 1) * 16);
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

        CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            Log.d(TAG, "write: forward sink: " + source.size() + " " + byteCount);
            try {
                long bytesToRead = Math.min(source.size(), byteCount);

                byte[] mainByteArray = source.readByteArray(bytesToRead);
                byte[] encryptedByteArray = null;
                if (isEncryptionActive)
                    encryptedByteArray = AESCrypt.encryptUpload(G.symmetricKey, mainByteArray, addIV);
                else
                    encryptedByteArray = mainByteArray;
                Log.d(TAG, "write: encrypt " + mainByteArray.length + " " + encryptedByteArray.length + " " + addIV);
                if (addIV)
                    addIV = false;
                Buffer encryptedSink = new Buffer();
                encryptedSink.write(encryptedByteArray);
                bytesWritten += bytesToRead;
                listener.onRequestProgress(bytesWritten, 0);
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
