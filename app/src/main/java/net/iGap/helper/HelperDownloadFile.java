/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.helper;

import android.os.Handler;
import android.os.Looper;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.utils.Utils;

import net.iGap.G;
import net.iGap.api.apiService.TokenContainer;
import net.iGap.module.AndroidUtils;
import net.iGap.module.api.beepTunes.DownloadSong;
import net.iGap.observers.interfaces.OnFileDownloadResponse;
import net.iGap.observers.interfaces.OnSongDownload;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.request.RequestFileDownload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentHashMap;


public class HelperDownloadFile {


    private static final String TAG = "aabolfazl";

    public interface UpdateListener {
        void OnProgress(String path, int progress);

        void OnError(String token);
    }

    public class StructListener {
        public UpdateListener listener;
        public String messageId;

        StructListener(UpdateListener listener, String messageId) {
            this.listener = listener;
            this.messageId = messageId;
        }
    }

    private static final HelperDownloadFile helperDownloadFile = new HelperDownloadFile();
    public ArrayList<String> manuallyStoppedDownload = new ArrayList<>();
    private ConcurrentHashMap<String, StructDownLoad> list = new ConcurrentHashMap<>();
    private ArrayList<StructQueue> mQueue = new ArrayList<>();
    private Handler handler;
    private final static int maxDownloadSize = 4;

    //**********************************************************************************************

    public static HelperDownloadFile getInstance() {
        return helperDownloadFile;
    }

    public void startDownload(ProtoGlobal.RoomMessageType type, String messageID, String token, String url, String cashId, String name, long size, ProtoFileDownload.FileDownload.Selector selector, String moveToDirectoryPAth, int periority, UpdateListener update) {
        StructDownLoad item;
        String primaryKey = cashId + selector;

        if (!list.containsKey(primaryKey)) {

            item = new StructDownLoad();
            item.type = type;
            item.Token = token;
            item.url = url;
            item.cashId = cashId;
            item.progress = 2;
            item.structListeners.add(new StructListener(update, messageID));
            item.name = name;
            item.moveToDirectoryPAth = moveToDirectoryPAth;
            item.size = size;
            item.priority = periority;

            try {
                list.put(primaryKey, item);
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }

        } else {
            item = list.get(primaryKey);

            boolean needAdd = true;

            if (update == null) {
                needAdd = false;
            } else {
                for (StructListener structListener : item.structListeners) {

                    if (structListener.messageId.equals(messageID)) {
                        needAdd = false;
                        structListener.listener = update;
                        break;
                    }
                }
            }

            if (needAdd) {
                item.structListeners.add(new StructListener(update, messageID));
            }


            updateView(item);
            return;
        }

        item.selector = selector;
        switch (item.selector) {
            case FILE:
                item.path = AndroidUtils.getFilePathWithCashId(item.cashId, item.name, G.DIR_TEMP, false);
                break;
            case SMALL_THUMBNAIL:
                if (item.url != null && !item.url.isEmpty()) {
                    item.url = item.url + "?selector=" + 1;
                }
                item.path = AndroidUtils.getFilePathWithCashId(item.cashId, item.name, G.DIR_TEMP, true);
                break;
            case LARGE_THUMBNAIL:
                if (item.url != null && !item.url.isEmpty()) {
                    item.url = item.url + "?selector=" + 2;
                }
                item.path = AndroidUtils.getFilePathWithCashId(item.cashId, item.name, G.DIR_TEMP, true);
                break;
        }


        File tmpFile = new File(item.path);

        if (tmpFile.exists()) {
            item.offset = tmpFile.length();

            if (item.offset > 0 && size > 0) {
                item.progress = (int) ((item.offset * 100) / size);
            }
        }

        if (moveToDirectoryPAth != null && moveToDirectoryPAth.length() > 0) {
            File _localFile = new File(moveToDirectoryPAth);
            if (_localFile.exists()) {
                item.progress = 100;
            }
        }

        if (item.progress < 100) {
            if (item.selector == ProtoFileDownload.FileDownload.Selector.FILE) {
                if (isNeedItemGoToQueue()) {
                    addItemToQueue(primaryKey, periority);
                    updateView(item);
                    return;
                }
            }
        }

        requestDownloadFile(item);

    }


    public void stopDownLoad(String cacheId) {
        manuallyStoppedDownload.add(cacheId);

        String primaryKey = cacheId + ProtoFileDownload.FileDownload.Selector.FILE;

        if (list.size() > 0 && list.containsKey(primaryKey)) {
            //RequestQueue.removeRequestQueue(list.get(primaryKey).identity); // don't need remove this item, remove listener in enough for stop download

            StructDownLoad item = list.get(primaryKey);

            if (item.url != null && !item.url.isEmpty()) {
                PRDownloader.pause(item.idDownload);

            }

            for (StructListener mItem : item.structListeners) {
                if (mItem.listener != null) {
                    item.isPause = true;
                    mItem.listener.OnError(item.Token);
                }
            }

            list.remove(primaryKey);
            addDownloadFromQueue();
        }
    }

    public boolean isDownLoading(String cashID) {
        String primaryKey = cashID + ProtoFileDownload.FileDownload.Selector.FILE;
        return list.containsKey(primaryKey);
    }

    //**********************************************************************************************

    private void getHandler() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                if (handler == null) {
                    handler = new Handler();
                }

                Looper.loop();
            }
        }).start();

    }

    private HelperDownloadFile() {

        getHandler();

        G.onFileDownloadResponse = new OnFileDownloadResponse() {
            @Override
            public void onFileDownload(String cashId, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress) {

                finishDownload(cashId, offset, selector, progress);

            }

            @Override
            public void onError(int majorCode, int minorCode, String cashId, ProtoFileDownload.FileDownload.Selector selector) {
                errorDownload(cashId, selector);
            }
        };

    }

    private void errorDownload(String cashId, ProtoFileDownload.FileDownload.Selector selector) {
        String primaryKey = cashId + selector;

        if (list.size() > 0 && list.containsKey(primaryKey)) {
            StructDownLoad item = list.get(primaryKey);

            item.attampOnError--;
            if (item.attampOnError >= 0) {
                requestDownloadFile(item);
            } else {

                for (StructListener mItem : item.structListeners) {
                    if (mItem.listener != null) {
                        mItem.listener.OnError(item.Token);
                    }
                }

                list.remove(primaryKey);

                // if (selector == ProtoFileDownload.FileDownload.Selector.FILE) {
                addDownloadFromQueue();
                //  }
            }
        }


    }

    private void finishDownload(String cashId, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress) {
        String PrimaryKey = cashId + selector;

        if (list.size() > 0 && list.containsKey(PrimaryKey)) {
            StructDownLoad item = list.get(PrimaryKey);
            item.offset = offset;
            item.progress = progress;

            if (item.selector == ProtoFileDownload.FileDownload.Selector.FILE) {

                if (mQueue.size() > 0) {
                    if (mQueue.get(0).priority > item.priority) {

                        if (item.priority < 3) {
                            ++item.priority;
                        }

                        addItemToQueue(item.cashId + item.selector, item.priority);
                        addDownloadFromQueue();
                        return;
                    }
                }
            }

            requestDownloadFile(item);
        }
    }

    private boolean isNeedItemGoToQueue() {

        return mQueue.size() > 0 || list.size() > maxDownloadSize;


   /*     int count = 0;

        for (int i = 0; i < list.size(); i++) {

            StructDownLoad _sd = list.valueAt(i);
            // if (_sd.selector == ProtoFileDownload.FileDownload.Selector.FILE){
            count++;
            //  }
        }

        if ((count) > maxDownloadSize) return true;

        return false;*/
    }

    private void addItemToQueue(String primaryKey, int priority) {

        boolean additem = false;

        StructQueue sq = new StructQueue();
        sq.priority = priority;
        sq.primaryKey = primaryKey;

        for (int i = mQueue.size() - 1; i >= 0; i--) {
            try {
                if (priority <= mQueue.get(i).priority) {
                    if (mQueue.size() >= (i + 1)) {
                        mQueue.add(i + 1, sq);
                        additem = true;
                        break;
                    }
                }
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        if (!additem) {
            mQueue.add(0, sq);
        }
    }

    private void addDownloadFromQueue() {

        // if any file exist in download queue add one to start download

        int count = mQueue.size();
        for (int i = 0; i < count; i++) {

            String _primaryKey = mQueue.get(0).primaryKey;
            mQueue.remove(0);

            if (list.size() > 0 && list.containsKey(_primaryKey)) {
                requestDownloadFile(list.get(_primaryKey));
                break;
            }
        }
    }

    private void startDownloadManager(final StructDownLoad item) {

        item.path = Utils.getTempPath(item.path, item.name);

        final String path = item.path.replace("/" + new File(item.path).getName(), "");
        final String name = new File(item.path).getName();
        //  G.onCheckConnection = null;
        item.idDownload = PRDownloader.download(item.url, path, name).setTag(item.cashId)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        item.isPause = false;
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {
                        stopDownLoad(item.cashId);
                        item.isPause = true;
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                        stopDownLoad(item.cashId);
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                        item.progress = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                        long downloadByte = progress.currentBytes - item.downloadedByte;
                        item.downloadedByte = progress.currentBytes;
                        if (item.progress < 100 && !item.isPause) {
                            updateView(item);
                            if (item.selector == ProtoFileDownload.FileDownload.Selector.FILE) {
                                HelperDataUsage.progressDownload(downloadByte, item.type);
                            }
                        }
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {

                        try {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    finishDownload(item.cashId, item.offset, item.selector, item.progress);
                                    if (item.selector.toString().toLowerCase().contains("file")) {
                                        HelperDataUsage.increaseDownloadFiles(item.type);
                                    }
                                }
                            });
                        } catch (NullPointerException e) {
                            HelperLog.getInstance().setErrorLog(e);
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        if (error.isConnectionError()) {
                            stopDownLoad(item.cashId);
                            item.isPause = true;
                        } else
                            errorDownload(item.cashId, item.selector);
                    }
                });
    }

    public static void startDownloadManager(DownloadSong song, OnSongDownload onSongDownload) {
        song.setDownloadId(PRDownloader.download(song.getUrl(), song.getPath(), song.getSavedName())
                .setHeader("Authorization", TokenContainer.getInstance().getToken())
                .build()
                .setOnStartOrResumeListener(() -> onSongDownload.startOrResume(song))
                .setOnPauseListener(() -> onSongDownload.pauseDownload(song))
                .setOnCancelListener(() -> onSongDownload.cancelDownload(song))
                .setOnProgressListener(progress -> onSongDownload.progressDownload(song, progress))
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        onSongDownload.completeDownload(song);
                    }

                    @Override
                    public void onError(Error error) {
                        onSongDownload.downloadError(song, error);
                    }
                }));
    }

    private void requestDownloadFile(final StructDownLoad item) {

        manuallyStoppedDownload.remove(item.cashId);
        if (item.progress == 100 || item.offset >= item.size) {
            moveTmpFileToOrginFolder(item.selector, item.cashId);
            updateView(item);
            list.remove(item.cashId + item.selector);
//            if (item.selector == ProtoFileDownload.FileDownload.Selector.FILE) {
            addDownloadFromQueue();
//            }

            // save downloaded file to gallery
            if (G.isSaveToGallery && HelperPermission.grantedUseStorage() && item.selector == ProtoFileDownload.FileDownload.Selector.FILE && item.moveToDirectoryPAth != null) {
                File file = new File(item.moveToDirectoryPAth);
                if (file.exists()) {

                    if (HelperMimeType.isFileImage(item.moveToDirectoryPAth.toLowerCase()) || HelperMimeType.isFileVideo(item.moveToDirectoryPAth.toLowerCase())) {
                        HelperSaveFile.savePicToGallery(item.moveToDirectoryPAth, false);
                    }
                }
            }

            return;
        }
        updateView(item);
        if (item.url != null && !item.url.isEmpty()) {
            startDownloadManager(item);
        } else {
            new RequestFileDownload().download(item.Token, item.offset, (int) item.size, item.selector, new RequestFileDownload.IdentityFileDownload(item.type, item.cashId, item.path, item.selector, item.size, item.offset, RequestFileDownload.TypeDownload.FILE));
        }
    }

    private void moveTmpFileToOrginFolder(ProtoFileDownload.FileDownload.Selector selector, String cashId) {

        StructDownLoad item = list.get(cashId + selector);

        if (item != null) {

            if (item.moveToDirectoryPAth.length() > 0) {
                try {

                    File _File = new File(item.moveToDirectoryPAth);
                    if (!_File.exists()) {
                        AndroidUtils.cutFromTemp(item.path, item.moveToDirectoryPAth);
                    }
                } catch (IOException e) {
                }
            }
            new Thread(() -> {
                switch (item.selector) {
                    case FILE:
                        RealmAttachment.setFilePAthToDataBaseAttachment(cashId, item.moveToDirectoryPAth);
                        break;
                    case SMALL_THUMBNAIL:
                    case LARGE_THUMBNAIL:
                        RealmAttachment.setThumbnailPathDataBaseAttachment(cashId, item.path);
                        break;
                }
            }).start();

        }
    }

    private void updateView(final StructDownLoad item) {
        for (StructListener mItem : item.structListeners) {
            if (mItem.listener != null) {
                String _path = item.moveToDirectoryPAth.length() > 0 ? item.moveToDirectoryPAth : item.path;
                mItem.listener.OnProgress(_path, item.progress);
            }
        }
    }

    private class StructDownLoad {

        ProtoGlobal.RoomMessageType type;
        String Token = "";
        public String url = "";
        int idDownload = 0;
        String cashId = "";
        ArrayList<StructListener> structListeners = new ArrayList<>();
        public int progress = 0;
        public long offset = 0;
        public String name = "";
        String moveToDirectoryPAth = "";
        public long size = 0;
        public String identity = "";
        int attampOnError = 2;
        public ProtoFileDownload.FileDownload.Selector selector;
        public String path = "";
        int priority = 0;
        boolean isPause = false;
        long downloadedByte = 0; // this field just used for CDN download

    }

    private class StructQueue {
        String primaryKey;
        int priority;
    }

}
