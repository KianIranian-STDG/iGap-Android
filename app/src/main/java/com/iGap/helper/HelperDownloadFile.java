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
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestFileDownload;
import java.io.File;
import java.io.IOException;

/**
 * Created by Maryam on 12/9/2016.
 */

public class HelperDownloadFile {

    private static ArrayMap<String, StructDownLoad> list = new ArrayMap<>();
    private static OnFileDownloadResponse onFileDownloadResponse;

    public HelperDownloadFile() {

        onFileDownloadResponse = new OnFileDownloadResponse() {
            @Override public void onFileDownload(String token, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress) {

                if (progress < 100) {
                    if (list.containsKey(token)) {
                        StructDownLoad item = list.get(token);
                        item.offset = offset;
                        item.progress = progress;

                        requestDownloadFile(item);
                    }
                } else {

                    if (list.containsKey(token)) {
                        StructDownLoad item = list.get(token);
                        item.offset = offset;
                        item.progress = progress;

                        updateView(item);

                        moveTmpFileToOrginFolder(token);

                        list.remove(token);
                    }
                }
            }

            @Override public void onAvatarDownload(String token, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress, long userId, RoomType roomType) {

            }

            @Override public void onError(int majorCode, int minorCode) {

            }

            @Override public void onBadDownload(String token) {

            }

            //@Override
            //public void onError(int majorCode, int minorCode, String token) {
            //
            //    if (list.containsKey(token)) {
            //        StructDownLoad item = list.get(token);
            //        item.attampOnError--;
            //        if (item.attampOnError >= 0) {
            //            requestDownloadFile(item);
            //        } else {
            //            if (item.update != null) {
            //                item.update.OnError(item.token);
            //            }
            //            list.remove(token);
            //        }
            //    }
            //
            //}
        };

        G.onFileDownloadResponse = onFileDownloadResponse;
    }

    private static class StructDownLoad {
        public UpdateListener update = null;
        public int progress = 0;
        public long offset = 0;
        public String name = "";
        public String token = "";
        public ProtoGlobal.RoomMessageType fileType;
        public long size = 0;
        public int attampOnError = 2;
        public ProtoFileDownload.FileDownload.Selector selector;
        public String path = "";
    }

    public interface UpdateListener {
        void OnProgress(String token, int progress);

        void OnError(String token);
    }

    public static void startDoanload(String token, String name, long size, ProtoFileDownload.FileDownload.Selector selector, ProtoGlobal.RoomMessageType fileType, UpdateListener update) {

        StructDownLoad item;

        if (!list.containsKey(token)) {

            item = new StructDownLoad();
            item.update = update;
            item.name = name;
            item.fileType = fileType;
            item.token = token;
            item.size = size;

            list.put(token, item);
        } else {
            item = list.get(token);
            item.update = update;

            return;
        }

        item.selector = selector;

        switch (item.selector) {
            case FILE:
                item.path = token + "_" + item.name;
                break;
            case SMALL_THUMBNAIL:
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

        updateView(item);

        if (item.progress == 100) {

            moveTmpFileToOrginFolder(item.token);
            list.remove(item.token);
            return;
        }

        ProtoFileDownload.FileDownload.Selector selector = item.selector;

        String identity = item.token + '*' + selector.toString() + '*' + item.size + '*' + item.path + '*' + item.offset;
        new RequestFileDownload().download(item.token, item.offset, (int) item.size, selector, identity);



    }

    private static void moveTmpFileToOrginFolder(String token) {

        StructDownLoad item = list.get(token);

        switch (item.selector) {
            case FILE:
                String dirPath = AndroidUtils.suitableAppFilePath(item.fileType) + "/" + item.path;
                String dirTmp = G.DIR_TEMP + "/" + item.path;
                try {
                    AndroidUtils.cutFromTemp(dirTmp, dirPath);
                } catch (IOException e) {
                }
                break;
        }
    }

    private static void updateView(StructDownLoad item) {

        if (item.update != null) {
            item.update.OnProgress(item.token, item.progress);
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
