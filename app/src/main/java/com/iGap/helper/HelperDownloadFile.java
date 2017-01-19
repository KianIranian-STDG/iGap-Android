package com.iGap.helper;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArrayMap;
import android.util.Log;
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
import java.io.File;
import java.io.IOException;

/**
 * Created by Maryam on 12/9/2016.
 */

public class HelperDownloadFile {

    private static ArrayMap<String, StructDownLoad> list = new ArrayMap<>();
    private OnFileDownloadResponse onFileDownloadResponse;

    public HelperDownloadFile() {

        onFileDownloadResponse = new OnFileDownloadResponse() {
            @Override public void onFileDownload(String token, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress) {

                    if (list.containsKey(token)) {
                        StructDownLoad item = list.get(token);
                        item.offset = offset;
                        item.progress = progress;

                        requestDownloadFile(item);
                    }
            }

            @Override public void onAvatarDownload(String token, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress, long userId, RoomType roomType) {

            }

            @Override public void onError(int majorCode, int minorCode, String token) {

                Log.e("ddddd", "helper download file    major  =" + majorCode + "   " + minorCode);

                if (list.containsKey(token)) {
                    StructDownLoad item = list.get(token);
                    item.attampOnError--;
                    if (item.attampOnError >= 0) {
                        requestDownloadFile(item);
                    } else {
                        if (item.update != null) {
                            item.update.OnError(item.token);
                        }
                        list.remove(token);
                    }
                }

            }
        };

        G.onFileDownloadResponse = onFileDownloadResponse;
    }

    private static class StructDownLoad {
        public UpdateListener update = null;
        public int progress = 0;
        public long offset = 0;
        public String name = "";
        public String token = "";
        public String moveToDirectoryPAth = "";
        public long size = 0;
        public int attampOnError = 2;
        public ProtoFileDownload.FileDownload.Selector selector;
        public String path = "";
    }

    public interface UpdateListener {
        void OnProgress(String token, int progress);

        void OnError(String token);
    }

    public static void startDoanload(String token, String name, long size, ProtoFileDownload.FileDownload.Selector selector, String moveToDirectoryPAth, UpdateListener update) {

        StructDownLoad item;

        if (!list.containsKey(token)) {

            item = new StructDownLoad();
            item.update = update;
            item.name = name;
            item.moveToDirectoryPAth = moveToDirectoryPAth;
            item.token = token;
            item.size = size;

            list.put(token, item);
        } else {
            item = list.get(token);
            item.update = update;
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
                item.path = "thumb_" + item.token + "_" + AppUtils.suitableThumbFileName(item.name);
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

        requestDownloadFile(item);
    }

    public static void stopDownLoad(String token) {

        if (list.containsKey(token)) {
            list.remove(token);
        }
    }

    private static void requestDownloadFile(StructDownLoad item) {

        if (item.progress == 100) {
            moveTmpFileToOrginFolder(item.token);
            updateView(item);
            list.remove(item.token);
            return;
        }

        updateView(item);

        ProtoFileDownload.FileDownload.Selector selector = item.selector;
        String identity = item.token + '*' + selector.toString() + '*' + item.size + '*' + item.path + '*' + item.offset + '*' + true;

        new RequestFileDownload().download(item.token, item.offset, (int) item.size, selector, identity);

    }

    private static void moveTmpFileToOrginFolder(String token) {

        StructDownLoad item = list.get(token);

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
                ;
                setThumpnailPathDataBaseAttachment(token, dirPathThumpnail);
                break;

        }
    }

    private static void setThumpnailPathDataBaseAttachment(final String token, final String name) {

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmAttachment attachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, token).findFirst();
                if (attachment != null) {
                    attachment.setLocalThumbnailPath(name);
                }
            }
        });
        realm.close();
    }

    public static boolean isDownLoading(String token) {

        if (list.containsKey(token)) if (list.get(token).selector == ProtoFileDownload.FileDownload.Selector.FILE) return true;

        return false;
    }

    public static int getProgress(String token) {

        if (list.containsKey(token)) {
            return list.get(token).progress;
        } else {
            return 0;
        }
    }

    private static void setFilePAthToDataBaseAttachment(final String token, final String name) {

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmAttachment attachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, token).findFirst();
                if (attachment != null) {
                    attachment.setLocalFilePath(name);
                }
            }
        });
        realm.close();
    }


    private static void updateView(StructDownLoad item) {

        Log.e("ddddd", item.offset + "   " + item.progress + "    " + item.size + "   " + item.selector);

        if (item.update != null) {
            item.update.OnProgress(item.token, item.progress);
        } else {
            Log.e("ddddd", "helper download file   listener is null");
        }
    }

    private void onError(int majorCode, int minorCode, final Context context) {
        if (majorCode == 713 && minorCode == 1) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_713_1), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 2) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_713_2), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 3) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_713_3), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 4) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_713_4), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 5) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_713_5), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 714) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_714), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 715) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), context.getResources().getString(R.string.E_715), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        }
    }
}
