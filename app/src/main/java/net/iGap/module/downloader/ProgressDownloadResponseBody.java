package net.iGap.module.downloader;

import java.io.IOException;

import javax.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressDownloadResponseBody extends ResponseBody {
    private ResponseBody responseBody;
    private IProgress progressCallback;
    private BufferedSource bufferedSource;

    public ProgressDownloadResponseBody(ResponseBody responseBody, IProgress progressCallback) {
        this.responseBody = responseBody;
        this.progressCallback = progressCallback;
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
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);

                totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                float percent = bytesRead == -1 ? 100f : (((float)totalBytesRead / (float) responseBody.contentLength()) * 100);

                if(progressCallback != null)
                    progressCallback.onProgress((int)percent);

                return bytesRead;
            }
        };
    }
}
