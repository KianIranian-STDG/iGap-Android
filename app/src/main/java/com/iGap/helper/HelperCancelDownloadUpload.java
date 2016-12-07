package com.iGap.helper;

import com.iGap.G;
import com.iGap.request.RequestWrapper;

import java.util.Iterator;
import java.util.Map;

public class HelperCancelDownloadUpload {

    /**
     * find RequestWrapper and remove requestQueueMap with that
     * for cancel upload
     *
     * @param cancelKey key for currentUploadFiles {@link java.util.HashMap}
     *                  key for upload is messageId
     */

    public static void cancelUpload(long cancelKey) {
        for (Iterator<Map.Entry<Long, RequestWrapper>> it = G.currentUploadFiles.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Long, RequestWrapper> entry = it.next();

            if (entry.getKey() == cancelKey) {
                removeRequestQueue(entry.getValue().identity);
            }
        }
    }

    private static void removeRequestQueue(String identity) {
        for (Iterator<Map.Entry<String, RequestWrapper>> it = G.requestQueueMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RequestWrapper> entry = it.next();

            if (entry.getValue().identity.equals(identity)) {
                G.requestQueueMap.remove(entry.getKey());
            }
        }
    }

}
