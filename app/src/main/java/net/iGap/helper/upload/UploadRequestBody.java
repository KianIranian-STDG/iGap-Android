package net.iGap.helper.upload;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class UploadRequestBody extends RequestBody {
    private final InputStream inputStream;
    private final long length;
    private MediaType contentType;
    private long totalUploaded = 0;
    private IOnProgressListener progressListener;

    public UploadRequestBody(MediaType contentType, long length, final InputStream inputStream, IOnProgressListener progressListener) {
        this.contentType = contentType;
        this.inputStream = inputStream;
        this.length = length;
        this.progressListener = progressListener;
    }


    @Override
    public long contentLength() {
        return -1;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {

            source = Okio.source(inputStream);

            while (totalUploaded <= length - 4 * 1024) {
                sink.write(source, 4 * 1024);
                totalUploaded += 4 * 1024;
                progressListener.onProgress(totalUploaded);
            }
            sink.writeAll(source);
        } finally {
            Util.closeQuietly(source);
        }
    }

    public interface IOnProgressListener {
        void onProgress(long totalByte);
    }
}
