package com.iGap.adapter;

import android.support.v4.util.ArrayMap;

import com.iGap.adapter.items.ImageMessageItem;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/6/2016.
 */
public class ImageMessagesAdapter<Item extends ImageMessageItem> extends FastItemAdapter<Item> {
    public static ArrayMap<String, Integer> requestsProgress = new ArrayMap<>();
    public static ArrayMap<String, Long> requestsOffset = new ArrayMap<>();
    public static CopyOnWriteArrayList<String> thumbnailRequests = new CopyOnWriteArrayList<>();

    public ImageMessagesAdapter() {
        // as we provide id's for the items we want the hasStableIds enabled to speed up things
        setHasStableIds(true);
    }

    public static void removeFileRequest(String token) {
        requestsOffset.remove(token);
        requestsProgress.remove(token);
    }

    /**
     * has already requested for downloading avatar file
     */
    public static boolean hasFileRequested(String token) {
        return requestsProgress.containsKey(token);
    }

    /**
     * has already requested for downloading avatar file
     */
    public static boolean hasThumbnailRequested(String token) {
        return thumbnailRequests.contains(token);
    }

    public void downloadingAvatarFile(String token, int progress, long offset) {
        for (Item item : getAdapterItems()) {

            try {
                if (item.message.getAttachment().getToken().equalsIgnoreCase(token)) {
                    int pos = getAdapterItems().indexOf(item);
                    if (requestsProgress.containsKey(token)) {
                        requestsProgress.put(token, progress);
                    }
                    if (requestsOffset.containsKey(token)) {
                        requestsOffset.put(token, offset);
                    }

                    item.onRequestDownloadAvatar(offset, progress);
                    notifyItemChanged(pos);
                    break;
                }
            } catch (NullPointerException e) {

            }


        }
    }

    public void downloadingAvatarThumbnail(String token) {
        for (Item item : getAdapterItems()) {
            if (item.message.getAttachment().getToken().equalsIgnoreCase(token)) {
                item.onRequestDownloadThumbnail(token, true);
                notifyAdapterDataSetChanged();
                break;
            }
        }
    }
}