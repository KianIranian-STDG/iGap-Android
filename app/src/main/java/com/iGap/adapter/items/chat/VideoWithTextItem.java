package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.interfaces.OnMessageViewClick;
import com.iGap.module.AndroidUtils;
import com.iGap.module.EmojiTextView;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;

import static com.iGap.module.AndroidUtils.suitablePath;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class VideoWithTextItem extends AbstractMessage<VideoWithTextItem, VideoWithTextItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public VideoWithTextItem(ProtoGlobal.Room.Type type, OnMessageViewClick messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutVideoWithText;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_video_with_text;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.attachment != null) {
            int[] dimens = AndroidUtils.scaleDimenWithSavedRatio(holder.itemView.getContext(), mMessage.attachment.width, mMessage.attachment.height);
            ((ViewGroup) holder.image.getParent()).setLayoutParams(new LinearLayout.LayoutParams(dimens[0], dimens[1]));
            holder.image.getParent().requestLayout();
        }

        holder.fileName.setText(mMessage.attachment.name);
        holder.duration.setText(String.format(holder.itemView.getResources().getString(R.string.video_duration), Double.toString(mMessage.attachment.duration).replace(".", ":"), AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true)));

        setTextIfNeeded(holder.messageText);
    }

    @Override
    public void onLoadFromLocal(final ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadFromLocal(holder, localPath, fileType);
        ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.image);
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView fileName;
        protected TextView duration;
        protected EmojiTextView messageText;

        public ViewHolder(View view) {
            super(view);

            messageText = (EmojiTextView) view.findViewById(R.id.messageText);
            messageText.setTextSize(G.userTextSize);
            image = (ImageView) view.findViewById(R.id.thumbnail);
            fileName = (TextView) view.findViewById(R.id.fileName);
            duration = (TextView) view.findViewById(R.id.duration);
        }
    }
}
