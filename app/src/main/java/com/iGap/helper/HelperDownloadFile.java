package com.iGap.helper;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArrayMap;
import android.view.View;
import com.iGap.G;
import com.iGap.R;
import com.iGap.interfaces.OnFileDownloadResponse;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAttachmentFields;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestFileDownload;
import io.realm.Realm;
import io.realm.RealmResults;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Maryam on 12/9/2016.
 */

public class HelperDownloadFile {

    private static ArrayMap<String, StructDownLoad> list = new ArrayMap<>();
    private OnFileDownloadResponse onFileDownloadResponse;

    private static int maxDownloadSize = 10;

    private static ArrayList<StructQueue> mQueue = new ArrayList<>();

    public HelperDownloadFile() {

        onFileDownloadResponse = new OnFileDownloadResponse() {
            @Override
            public void onFileDownload(String token, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress) {

                String PrimaryKey = token + selector;

                if (list.size() > 0 && list.containsKey(PrimaryKey)) {
                    StructDownLoad item = list.get(PrimaryKey);
                    item.offset = offset;
                    item.progress = progress;

                    if (item.selector == ProtoFileDownload.FileDownload.Selector.FILE) {

                        if (mQueue.size() > 0) {
                            if (mQueue.get(0).priority > item.priority) {

                                addItemToQueue(item.Token + item.selector, ++item.priority);
                                addDownloadFromQueue();
                                return;
                            }
                        }
                    }



                    requestDownloadFile(item);
                }
            }

            @Override
            public void onAvatarDownload(String token, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress, long userId, RoomType roomType) {

            }

            @Override
            public void onError(int majorCode, int minorCode, String token, ProtoFileDownload.FileDownload.Selector selector) {

                String primaryKey = token + selector;

                if (list.size() > 0 && list.containsKey(primaryKey)) {
                    StructDownLoad item = list.get(primaryKey);

                    item.attampOnError--;
                    if (item.attampOnError >= 0) {
                        requestDownloadFile(item);
                    } else {

                        for (UpdateListener listener : item.listeners) {
                            if (listener != null) {
                                listener.OnError(item.Token);
                            }
                        }

                        list.remove(primaryKey);

                        if (selector == ProtoFileDownload.FileDownload.Selector.FILE) addDownloadFromQueue();
                    }
                }
            }
        };

        G.onFileDownloadResponse = onFileDownloadResponse;
    }

    private static class StructDownLoad {
        public String Token = "";
        public ArrayList<UpdateListener> listeners = new ArrayList<>();
        public int progress = 0;
        public long offset = 0;
        public String name = "";
        public String moveToDirectoryPAth = "";
        public long size = 0;
        public String identity = "";
        public int attampOnError = 2;
        public ProtoFileDownload.FileDownload.Selector selector;
        public String path = "";
        int priority = 0;
    }

    private static class StructQueue {
        String primaryKey;
        int priority;
    }

    public interface UpdateListener {
        void OnProgress(String token, int progress);

        void OnError(String token);
    }

    private static boolean isNeedItemGoToQueue() {

        if (mQueue.size() > 0) return true;

        int count = 0;

        for (int i = 0; i < list.size(); i++) {

            StructDownLoad _sd = list.valueAt(i);
            if (_sd.selector == ProtoFileDownload.FileDownload.Selector.FILE) count++;
        }

        if ((count) > maxDownloadSize) return true;

        return false;
    }

    private static void addItemToQueue(String primaryKey, int priority) {

        boolean additem = false;

        StructQueue sq = new StructQueue();
        sq.priority = priority;
        sq.primaryKey = primaryKey;

        for (int i = mQueue.size() - 1; i >= 0; i--) {
            if (priority > mQueue.get(i).priority) {
                continue;
            } else {
                mQueue.add(i + 1, sq);
                additem = true;
                break;
            }
        }

        if (!additem) {
            mQueue.add(0, sq);
        }
    }

    public static void startDownload(String token, String name, long size, ProtoFileDownload.FileDownload.Selector selector, String moveToDirectoryPAth, int periority, UpdateListener update) {

        StructDownLoad item;

        String primaryKey = token + selector;

        if (!list.containsKey(primaryKey)) {

            item = new StructDownLoad();
            item.Token = token;
            item.listeners.add(update);
            item.name = name;
            item.moveToDirectoryPAth = moveToDirectoryPAth;
            item.size = size;
            item.priority = periority;
            list.put(primaryKey, item);

        } else {
            item = list.get(primaryKey);
            item.listeners.add(update);
            updateView(item);

            return;
        }

        item.selector = selector;

        switch (item.selector) {
            case FILE:
                item.path = token + "_" + item.name;
                break;
            case SMALL_THUMBNAIL:
            case LARGE_THUMBNAIL:
                item.path = "thumb_" + item.Token + "_" + AppUtils.suitableThumbFileName(item.name);
                break;
        }

        String tmpPath = G.DIR_TEMP + "/" + item.path;
        File tmpFile = new File(tmpPath);

        if (tmpFile.exists()) {
            item.offset = tmpFile.length();

            if (item.offset > 0 && size > 0) {
                item.progress = (int) ((item.offset * 100) / size);
            }
        }

        if (item.selector == ProtoFileDownload.FileDownload.Selector.FILE) {
            if (isNeedItemGoToQueue()) {
                addItemToQueue(primaryKey, periority);
                return;
            }
        }

        requestDownloadFile(item);
    }

    public static void stopDownLoad(String token) {

        String primaryKey = token + ProtoFileDownload.FileDownload.Selector.FILE;

        if (list.size() > 0 && list.containsKey(primaryKey)) {

            HelperCancelDownloadUpload.removeRequestQueue(list.get(primaryKey).identity);

            StructDownLoad item = list.get(primaryKey);

            if (item != null && item.listeners != null) {
                for (UpdateListener listener : item.listeners) {
                    if (listener != null) {
                        listener.OnError(item.Token);
                    }
                }
            }

            list.remove(primaryKey);

            addDownloadFromQueue();
        }
    }

    private static void addDownloadFromQueue() {

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

    private static void requestDownloadFile(StructDownLoad item) {

        if (item.progress == 100) {
            moveTmpFileToOrginFolder(item.Token, item.selector);
            updateView(item);
            list.remove(item.Token + item.selector);

            if (item.selector == ProtoFileDownload.FileDownload.Selector.FILE) addDownloadFromQueue();

            return;
        }

        updateView(item);

        ProtoFileDownload.FileDownload.Selector selector = item.selector;
        item.identity = item.Token + '*' + selector.toString() + '*' + item.size + '*' + item.path + '*' + item.offset + '*' + true;
        new RequestFileDownload().download(item.Token, item.offset, (int) item.size, selector, item.identity);
    }

    private static void moveTmpFileToOrginFolder(String token, ProtoFileDownload.FileDownload.Selector selector) {

        StructDownLoad item = list.get(token + selector);

        if (item.moveToDirectoryPAth.length() > 0) {
            String dirTmp = G.DIR_TEMP + "/" + item.path;
            try {
                AndroidUtils.cutFromTemp(dirTmp, item.moveToDirectoryPAth);
            } catch (IOException e) {
            }
        }

        switch (item.selector) {
            case FILE:
                setFilePAthToDataBaseAttachment(token, item.moveToDirectoryPAth);
                break;

            case SMALL_THUMBNAIL:
            case LARGE_THUMBNAIL:
                String dirPathThumpnail = G.DIR_TEMP + "/" + item.path;
                setThumbnailPathDataBaseAttachment(token, dirPathThumpnail);
                break;
        }
    }

    private static void setThumbnailPathDataBaseAttachment(final String token, final String name) {

        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<RealmAttachment> attachments = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, token).findAll();
                for (RealmAttachment attachment : attachments) {
                    attachment.setLocalThumbnailPath(name);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.close();
            }
        });
    }

    public static boolean isDownLoading(String token) {

        String primaryKey = token + ProtoFileDownload.FileDownload.Selector.FILE;

        if (list.containsKey(primaryKey)) return true;

        return false;
    }

    public static int getProgress(String token) {

        String primaryKey = token + ProtoFileDownload.FileDownload.Selector.FILE;

        if (list.containsKey(primaryKey)) {
            return list.get(primaryKey).progress;
        } else {
            return 0;
        }
    }

    private static void setFilePAthToDataBaseAttachment(final String token, final String name) {

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<RealmAttachment> attachments = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, token).findAll();

                for (RealmAttachment attachment : attachments) {
                    attachment.setLocalFilePath(name);
                }
            }
        });
        realm.close();
    }

    private static void updateView(final StructDownLoad item) {
        for (UpdateListener listener : item.listeners) {
            if (listener != null) {
                listener.OnProgress(item.Token, item.progress);
            }
        }
    }

    private void onError(int majorCode, int minorCode, final Context context) {
        if (majorCode == 713 && minorCode == 1) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_713_1), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 2) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_713_2), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 3) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_713_3), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 4) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_713_4), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 5) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_713_5), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 714) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_714), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 715) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_715), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        }
    }
}
