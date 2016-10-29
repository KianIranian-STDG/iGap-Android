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
import com.iGap.realm.RealmRoomMessage;
import com.iGap.request.RequestFileDownload;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.meness.github.messageprogress.MessageProgress;
import io.meness.github.messageprogress.OnMessageProgressClick;
import io.realm.Realm;
import java.io.IOException;
import java.util.List;

import static com.iGap.module.AndroidUtils.suitablePath;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/26/2016.
 */

public class ImageMessageItem extends AbstractItem<ImageMessageItem, ImageMessageItem.ViewHolder>
    implements IChatItemAvatar {
    public RealmRoomMessage message;

    public ImageMessageItem setMessage(RealmRoomMessage message) {
        this.message = message;
        return this;
    }

    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    @Override public int getType() {
        return 0;
    }

    @Override public int getLayoutRes() {
        return R.layout.show_image_sub_layout;
    }

    /**
     * request for avatar file
     */
    private void requestForAvatarFile(String token) {
        if (!AvatarsAdapter.hasFileRequested(token)) {
            AvatarsAdapter.requestsProgress.put(token, 0);
            AvatarsAdapter.requestsOffset.put(token, 0);

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
        final String fileName = token + "_" + message.getAttachment().getName();
        if (done) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override public void execute(Realm realm) {
                    message.getAttachment().setLocalThumbnailPath(G.DIR_TEMP + "/" + fileName);
                }
            });
            realm.close();

            return; // necessary
        }

        ProtoFileDownload.FileDownload.Selector selector =
            ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
        String identity = message.getAttachment().getToken()
            + '*'
            + selector.toString()
            + '*'
            + message.getAttachment().getSmallThumbnail().getSize()
            + '*'
            + fileName
            + '*'
            + 0;

        new RequestFileDownload().download(token, 0,
            (int) message.getAttachment().getSmallThumbnail().getSize(), selector, identity);
    }

    public void onLoadFromLocal(ViewHolder holder, String localPath) {
        ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.image);
    }

    @Override public void onRequestDownloadAvatar(int offset, int progress) {
        ProtoFileDownload.FileDownload.Selector selector =
            ProtoFileDownload.FileDownload.Selector.FILE;
        final String fileName =
            message.getAttachment().getToken() + "_" + message.getAttachment().getName();

        if (progress == 100) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override public void execute(Realm realm) {
                    message.getAttachment().setLocalFilePath(G.DIR_IMAGE_USER + "/" + fileName);
                }
            });
            realm.close();

            try {
                AndroidUtils.cutFromTemp(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // remove from requests when downloading has finished
            AvatarsAdapter.removeFileRequest(message.getAttachment().getToken());

            return; // necessary
        }

        String identity = message.getAttachment().getToken()
            + '*'
            + selector.toString()
            + '*'
            + message.getAttachment().getSize()
            + '*'
            + fileName
            + '*'
            + offset;
        new RequestFileDownload().download(message.getAttachment().getToken(), offset,
            (int) message.getAttachment().getSize(), selector, identity);
    }

    protected static class ItemFactory implements ViewHolderFactory<ImageMessageItem.ViewHolder> {
        public ImageMessageItem.ViewHolder create(View v) {
            return new ImageMessageItem.ViewHolder(v);
        }
    }

    @Override public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        // if file already exists, simply show the local one
        if (message.getAttachment().isFileExistsOnLocal()) {
            // load file from local
            onLoadFromLocal(holder, message.getAttachment().getLocalFilePath());
        } else {
            // file doesn't exist on local, I check for a thumbnail
            // if thumbnail exists, I load it into the view
            if (message.getAttachment().isThumbnailExistsOnLocal()) {
                // load thumbnail from local
                onLoadFromLocal(holder, message.getAttachment().getLocalThumbnailPath());
            } else {
                requestForAvatarThumbnail(message.getAttachment().getToken());
            }

            holder.progress.withOnMessageProgress(new OnMessageProgressClick() {
                @Override public void onMessageProgressClick(MessageProgress progress) {
                    holder.progress.withDrawable(R.drawable.ic_cancel);
                    holder.progress.withIndeterminate(true);

                    // make sure to not request multiple times by checking last offset with the new one
                    if (!AvatarsAdapter.hasFileRequested(message.getAttachment().getToken())) {
                        requestForAvatarFile(message.getAttachment().getToken());
                    }
                }
            });
        }

        prepareProgress(holder);
    }

    private void prepareProgress(ViewHolder holder) {
        if (message.getAttachment().isFileExistsOnLocal()) {
            holder.progress.setVisibility(View.INVISIBLE);
        } else {
            if (AvatarsAdapter.hasFileRequested(message.getAttachment().getToken())) {
                holder.progress.setVisibility(View.VISIBLE);
                holder.progress.withDrawable(R.drawable.ic_cancel);
                holder.progress.withProgress(
                    AvatarsAdapter.requestsProgress.get(message.getAttachment().getToken()));
            } else {
                holder.progress.setVisibility(View.VISIBLE);
                holder.progress.withDrawable(R.drawable.ic_download);
            }
        }
    }

    @Override public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
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
