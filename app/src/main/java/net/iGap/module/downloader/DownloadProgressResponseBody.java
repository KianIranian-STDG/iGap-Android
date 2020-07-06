package net.iGap.module.downloader;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class DownloadProgressResponseBody extends ResponseBody {
    private ResponseBody responseBody;
    private IProgress progressCallback;
    private BufferedSource bufferedSource;
    private int progress = 0;
    private volatile byte[] iv = null;

    public DownloadProgressResponseBody(ResponseBody responseBody, IProgress progressCallback) {
        this.responseBody = responseBody;
        this.progressCallback = progressCallback;
        this.bufferedSource = Okio.buffer(source(responseBody.source()));
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    @NonNull
    public BufferedSource source() {
        return bufferedSource;
    }

    private BufferedSource decrypt(BufferedSource source) throws IOException, InvalidAlgorithmParameterException, InvalidKeyException {
//        if (iv == null)
//            iv = source.readByteArray(16);
//
//
//        CipherInputStream inputStream = new CipherInputStream(source.inputStream(),
//                AesCipherDownloadOptimized.getCipher("5183666c72eec9e4".getBytes(), HelperString.generateSymmetricKey("bf3c199c2470cb477d907b1e0917c17b")));
        return source;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);

                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                float percent = bytesRead == -1 ? 100f : (((float) totalBytesRead / (float) responseBody.contentLength()) * 100);

                if (progressCallback != null && (int) percent > progress) {
                    progress = (int) percent;
                    progressCallback.onProgress(progress);
                }

                return bytesRead;
            }
        };
    }
}
