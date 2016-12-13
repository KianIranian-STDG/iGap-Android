package com.iGap.adapter.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AvatarsAdapter;
import com.iGap.interfaces.IChatItemAvatar;
import com.iGap.module.AndroidUtils;
import com.iGap.module.TouchImageView;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.realm.RealmAttachment;
import com.iGap.request.RequestFileDownload;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.List;

import io.meness.github.messageprogress.MessageProgress;
import io.meness.github.messageprogress.OnMessageProgressClick;
import io.realm.Realm;

import static com.iGap.module.AndroidUtils.suitablePath;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/26/2016.
 */

public class AvatarItem extends AbstractItem<AvatarItem, AvatarItem.ViewHolder>
        implements IChatItemAvatar {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();
    public RealmAttachment avatar;
    public long imageId;

    public AvatarItem setAvatar(RealmAttachment avatar, long id) {
        this.avatar = avatar;
        this.imageId = id;
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

    /**
     * request for avatar file
     */
    private void requestForAvatarFile(String token) {
        if (!AvatarsAdapter.hasFileRequested(token)) {
            AvatarsAdapter.requestsProgress.put(token, 0);
            AvatarsAdapter.requestsOffset.put(token, 0L);

            onRequestDownloadAvatar(0, 0);
        }
    }

    /**
     * request for avatar thumbnail
     */
    private void requestForAvatarThumbnail(String token) {
        if (!AvatarsAdapter.hasThumbnailRequested(token)) {
            AvatarsAdapter.thumbnailRequests.add(token);

            onRequestDownloadThumbnail(token, false);
        }
    }

    public void onRequestDownloadThumbnail(String token, boolean done) {
        final String fileName = "thumb_" + token + "_" + avatar.getName();
        if (done) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    avatar.setLocalThumbnailPath(G.DIR_TEMP + "/" + fileName);
                }
            });
            realm.close();

            return; // necessary
        }

        ProtoFileDownload.FileDownload.Selector selector =
                ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
        String identity =
                avatar.getToken() + '*' + selector.toString() + '*' + avatar.getSmallThumbnail()
                        .getSize() + '*' + fileName + '*' + 0;

        new RequestFileDownload().download(token, 0, (int) avatar.getSmallThumbnail().getSize(),
                selector, identity);
    }

    public void onLoadFromLocal(ViewHolder holder, String localPath) {
        ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.image);
    }

    @Override
    public void onRequestDownloadAvatar(long offset, int progress) {
        ProtoFileDownload.FileDownload.Selector selector =
                ProtoFileDownload.FileDownload.Selector.FILE;
        final String fileName = avatar.getToken() + "_" + avatar.getName();

        if (progress == 100) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    avatar.setLocalFilePath(G.DIR_IMAGE_USER + "/" + fileName);
                }
            });
            realm.close();

            try {
                AndroidUtils.cutFromTemp(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // remove from requests when downloading has finished
            AvatarsAdapter.removeFileRequest(avatar.getToken());

            return; // necessary
        }

        String identity = avatar.getToken()
                + '*'
                + selector.toString()
                + '*'
                + avatar.getSize()
                + '*'
                + fileName
                + '*'
                + offset;
        new RequestFileDownload().download(avatar.getToken(), offset, (int) avatar.getSize(),
                selector, identity);
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        // if file already exists, simply show the local one
        if (avatar.isFileExistsOnLocal()) {
            // load file from local
            onLoadFromLocal(holder, avatar.getLocalFilePath());
        } else {
            // file doesn't exist on local, I check for a thumbnail
            // if thumbnail exists, I load it into the view
            if (avatar.isThumbnailExistsOnLocal()) {
                // load thumbnail from local
                onLoadFromLocal(holder, avatar.getLocalThumbnailPath());
            } else {
                requestForAvatarThumbnail(avatar.getToken());
            }

            holder.progress.withOnMessageProgress(new OnMessageProgressClick() {
                @Override
                public void onMessageProgressClick(MessageProgress progress) {
                    holder.progress.withDrawable(R.drawable.ic_gray_cancel, false);
                    holder.progress.withIndeterminate(true);

                    // make sure to not request multiple times by checking last offset with the new one
                    if (!AvatarsAdapter.hasFileRequested(avatar.getToken())) {
                        requestForAvatarFile(avatar.getToken());
                    }
                }
            });
        }

        prepareProgress(holder);
    }

    private void prepareProgress(ViewHolder holder) {
        if (avatar.isFileExistsOnLocal()) {
            holder.progress.setVisibility(View.INVISIBLE);
        } else {
            if (AvatarsAdapter.hasFileRequested(avatar.getToken())) {
                holder.progress.setVisibility(View.VISIBLE);
                holder.progress.withDrawable(R.drawable.ic_gray_cancel, false);
                holder.progress.withProgress(
                        AvatarsAdapter.requestsProgress.get(avatar.getToken()));
            } else {
                holder.progress.setVisibility(View.VISIBLE);
                holder.progress.withDrawable(R.drawable.ic_download, true);
            }
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

        public ViewHolder(View view) {
            super(view);

            image = (TouchImageView) view.findViewById(R.id.sisl_touch_image_view);
            progress = (MessageProgress) view.findViewById(R.id.progress);
        }
    }
}
