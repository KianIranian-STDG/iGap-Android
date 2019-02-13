/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.adapter.items.chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperRadius;
import net.iGap.interfaces.IMessageItem;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;

import java.io.InputStream;
import java.util.List;

import io.realm.Realm;

import static net.iGap.module.AndroidUtils.suitablePath;

public class ImageWithTextItem extends AbstractMessage<ImageWithTextItem, ImageWithTextItem.ViewHolder> {

    public ImageWithTextItem(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutImageWithText;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        holder.image.setTag(getCacheId(mMessage));

        super.bindView(holder, payloads);

        setTextIfNeeded(holder.messageView);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("bagi" , "ClickImage");
                if (FragmentChat.isInSelectionMode){
                        holder.itemView.performLongClick();
                } else {
                    if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                        return;
                    }
                    if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                        messageClickListener.onFailedMessageClick(v, mMessage, holder.getAdapterPosition());
                    } else {
                        messageClickListener.onOpenClick(v, mMessage, holder.getAdapterPosition());
                    }
                }
            }
        });

        holder.image.setOnLongClickListener(getLongClickPerform(holder));
    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, final String tag, final String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, tag, localPath, fileType);
        if (holder.image.getTag() != null && holder.image.getTag().equals(tag)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder().decodingOptions(options);
            G.imageLoader.displayImage(suitablePath(localPath), new ImageViewAware(holder.image), builder.build(),
                    new ImageSize(holder.image.getMeasuredWidth(), holder.image.getMeasuredHeight()), null, null);
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends ChatItemWithTextHolder implements IThumbNailItem, IProgress {
        protected ReserveSpaceRoundedImageView image;
        protected MessageProgress progress;

        public ViewHolder(View view) {
            super(view);
            boolean withText = true;
            FrameLayout frameLayout = new FrameLayout(G.context);
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

            image = new ReserveSpaceRoundedImageView(G.context);
            image.setId(R.id.thumbnail);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            LinearLayout.LayoutParams layout_758 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            image.setLayoutParams(layout_758);
            image.setCornerRadius(HelperRadius.computeRadius());
            m_container.addView(frameLayout);
            if (withText) {
                setLayoutMessageContainer();
            }
            frameLayout.addView(image);
            progress = getProgressBar(0);
            frameLayout.addView(progress, new FrameLayout.LayoutParams(i_Dp(R.dimen.dp60), i_Dp(R.dimen.dp60), Gravity.CENTER));
        }

        @Override
        public ImageView getThumbNailImageView() {
            return image;
        }

        @Override
        public MessageProgress getProgress() {
            return progress;
        }
    }
}
