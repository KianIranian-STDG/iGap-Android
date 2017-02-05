package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.iGap.R;
import com.iGap.helper.HelperRadius;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.AndroidUtils;
import com.iGap.module.ReserveSpaceRoundedImageView;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.meness.github.messageprogress.MessageProgress;
import java.util.List;

import static com.iGap.module.AndroidUtils.suitablePath;

public class VideoItem extends AbstractMessage<VideoItem, VideoItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public VideoItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutVideo;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_video;
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);

        if (fileType == LocalFileType.THUMBNAIL) {

            ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.image);

            holder.image.setCornerRadius(HelperRadius.computeRadius(localPath));
        } else {
            holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_play, true);
        }

    }

    @Override
    protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                holder.duration.setText(
                        String.format(holder.itemView.getResources().getString(R.string.video_duration),
                                AndroidUtils.formatDuration((int) (mMessage.forwardedFrom.getAttachment().getDuration() * 1000L)),
                                AndroidUtils.humanReadableByteCount(mMessage.forwardedFrom.getAttachment().getSize(), true)));
            }
        } else {
            if (mMessage.attachment != null) {
                holder.duration.setText(
                        String.format(holder.itemView.getResources().getString(R.string.video_duration),
                                AndroidUtils.formatDuration((int) (mMessage.attachment.duration * 1000L)),
                                AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true)));
            }
        }
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ReserveSpaceRoundedImageView image;
        protected TextView duration;

        public ViewHolder(View view) {
            super(view);

            image = (ReserveSpaceRoundedImageView) view.findViewById(R.id.thumbnail);
            duration = (TextView) view.findViewById(R.id.duration);
        }
    }
}
