package com.iGap.helper;

import android.support.annotation.CheckResult;
import com.iGap.G;
import com.iGap.adapter.MessagesAdapter;
import com.iGap.request.RequestWrapper;
import java.util.Iterator;
import java.util.Map;

public final class HelperCancelDownloadUpload {

    private HelperCancelDownloadUpload() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    /**
     * find RequestWrapper and remove from requestQueueMap with token
     *
     * @param token String token
     */
    @CheckResult
    public static boolean cancelDownload(String token) {
        //if (MessagesAdapter.hasDownloadRequested(token)) {
        //    MessagesAdapter.downloading.remove(token);
        //}

        for (String t : G.downloadingTokens) {
            if (t != null && token != null && t.equalsIgnoreCase(token)) {
                if (G.downloadingTokens.contains(token)) {
                    G.downloadingTokens.remove(token);
                }
                return removeRequestQueue(token);
            }
        }
        return false;
    }

    /**
     * find RequestWrapper and remove from requestQueueMap with key
     *
     * @param key key for currentUploadFiles {@link java.util.HashMap}
     *            key for upload is messageId
     */
    @CheckResult
    public static boolean cancelUpload(long key) {
        if (MessagesAdapter.hasUploadRequested(key)) {
            MessagesAdapter.uploading.remove(key);
        }

        for (Iterator<Map.Entry<Long, RequestWrapper>> it = G.currentUploadFiles.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Long, RequestWrapper> entry = it.next();

            if (entry.getKey() == key) {
                if (G.currentUploadFiles.containsKey(key)) {
                    G.currentUploadFiles.remove(key);
                }
                return removeRequestQueue(entry.getValue().identity);
            }
        }
        return false;
    }

    public static boolean removeRequestQueue(String identity) {
        for (Iterator<Map.Entry<String, RequestWrapper>> it = G.requestQueueMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RequestWrapper> entry = it.next();

            if (entry.getValue().identity != null && entry.getValue().identity.contains(identity)) {
                G.requestQueueMap.remove(entry.getKey());
                return true;
            }
        }
        return false;
    }

}
