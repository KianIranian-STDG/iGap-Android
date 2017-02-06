package com.iGap.adapter.items;

import android.graphics.Color;
import android.net.Uri;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperDownloadFile;
import com.iGap.module.TouchImageView;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.realm.RealmAttachment;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.meness.github.messageprogress.MessageProgress;
import java.io.File;
import java.util.List;

import static com.iGap.module.AndroidUtils.suitablePath;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/26/2016.
 */

public class AvatarItem extends AbstractItem<AvatarItem, AvatarItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    public RealmAttachment avatar;

    public AvatarItem setAvatar(RealmAttachment avatar) {
        this.avatar = avatar;
        return this;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.show_image_sub_layout;
    }



    public void onLoadFromLocal(ViewHolder holder, String localPath) {
        ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.image);
    }


    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (HelperDownloadFile.isDownLoading(avatar.getToken())) {
            holder.progress.withDrawable(R.drawable.ic_cancel, true);
            holder.contentLoading.setVisibility(View.VISIBLE);
        } else {
            holder.progress.withDrawable(R.drawable.ic_download, true);
            holder.contentLoading.setVisibility(View.GONE);
        }

        // if file already exists, simply show the local one
        if (avatar.isFileExistsOnLocal()) {
            // load file from local
            holder.progress.setVisibility(View.GONE);
            onLoadFromLocal(holder, avatar.getLocalFilePath());
        } else {

            holder.progress.setVisibility(View.VISIBLE);
            holder.progress.withDrawable(R.drawable.ic_download, true);

            // file doesn't exist on local, I check for a thumbnail
            // if thumbnail exists, I load it into the view
            if (avatar.isThumbnailExistsOnLocal()) {
                // load thumbnail from local
                onLoadFromLocal(holder, avatar.getLocalThumbnailPath());
            } else if (avatar != null) {

                // if thumpnail not exist download it
                ProtoFileDownload.FileDownload.Selector selector = null;
                long fileSize = 0;

                if (avatar.getSmallThumbnail() != null) {
                    selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                    fileSize = avatar.getSmallThumbnail().getSize();
                } else if (avatar.getLargeThumbnail() != null) {
                    selector = ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL;
                    fileSize = avatar.getLargeThumbnail().getSize();
                }

                final String filePathTumpnail = G.DIR_TEMP + "/" + "thumb_" + avatar.getToken() + "_" + avatar.getName();

                if (selector != null && fileSize > 0) {
                    HelperDownloadFile.startDownload(avatar.getToken(), avatar.getName(), fileSize, selector, "", 4, new HelperDownloadFile.UpdateListener() {
                        @Override public void OnProgress(String token, int progress) {

                            if (progress == 100) {
                                G.currentActivity.runOnUiThread(new Runnable() {
                                    @Override public void run() {
                                        holder.image.setImageURI(Uri.fromFile(new File(filePathTumpnail)));
                                    }
                                });
                            }
                        }

                        @Override public void OnError(String token) {

                        }
                    });
                }

            }

            holder.progress.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {

                    if (HelperDownloadFile.isDownLoading(avatar.getToken())) {
                        HelperDownloadFile.stopDownLoad(avatar.getToken());

                    } else {
                        holder.progress.withDrawable(R.drawable.ic_cancel, true);
                        holder.contentLoading.setVisibility(View.VISIBLE);

                        final String path = G.DIR_IMAGE_USER + "/" + avatar.getToken() + "_" + avatar.getName();

                        HelperDownloadFile.startDownload(avatar.getToken(), avatar.getName(), avatar.getSize(), ProtoFileDownload.FileDownload.Selector.FILE, path, 4,
                            new HelperDownloadFile.UpdateListener() {
                                @Override public void OnProgress(String token, final int progres) {

                                    if (holder.progress != null) {

                                        G.currentActivity.runOnUiThread(new Runnable() {
                                            @Override public void run() {
                                                if (progres < 100) {
                                                    holder.progress.withProgress(progres);
                                                } else {
                                                    holder.progress.withProgress(0);
                                                    holder.progress.setVisibility(View.GONE);
                                                    ImageLoader.getInstance().displayImage(suitablePath(path), holder.image);
                                                }
                                            }
                                        });

                                    }
                                }

                                @Override public void OnError(String token) {

                                    G.currentActivity.runOnUiThread(new Runnable() {
                                        @Override public void run() {
                                            holder.progress.withProgress(0);
                                            holder.progress.withDrawable(R.drawable.ic_download, true);
                                            holder.contentLoading.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            });
                    }
                }
            });


        }

    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ItemFactory implements ViewHolderFactory<AvatarItem.ViewHolder> {
        public AvatarItem.ViewHolder create(View v) {
            return new AvatarItem.ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TouchImageView image;
        protected MessageProgress progress;

        protected ContentLoadingProgressBar contentLoading;

        public ViewHolder(View view) {
            super(view);

            image = (TouchImageView) view.findViewById(R.id.sisl_touch_image_view);
            progress = (MessageProgress) view.findViewById(R.id.progress);

            contentLoading = (ContentLoadingProgressBar) view.findViewById(R.id.ch_progress_loadingContent);
            contentLoading.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);

        }
    }
}
