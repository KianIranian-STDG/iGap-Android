/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.adapter.items.chat;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperRadius;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.FontIconTextView;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.enums.LocalFileType;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;
import net.iGap.structs.MessageObject;

import java.util.List;

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
        super.bindView(holder, payloads);

        setTextIfNeeded(holder.messageView);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FragmentChat.isInSelectionMode) {
                    holder.itemView.performLongClick();
                } else {
                    if (messageObject.status == MessageObject.STATUS_SENDING) {
                        return;
                    }
                    if (messageObject.status == MessageObject.STATUS_FAILED) {
                        messageClickListener.onFailedMessageClick(v, messageObject, holder.getAdapterPosition());
                    } else {
                        messageClickListener.onOpenClick(v, messageObject, holder.getAdapterPosition());
                    }
                }
            }
        });

        holder.image.setOnLongClickListener(getLongClickPerform(holder));
    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, final String tag, final String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, tag, localPath, fileType);

//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
//            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder().decodingOptions(options);
//            G.imageLoader.displayImage(suitablePath(localPath), new ImageViewAware(holder.image), builder.build(),
//                    new ImageSize(holder.image.getMeasuredWidth(), holder.image.getMeasuredHeight()), null, null);
        G.imageLoader.displayImage(suitablePath(localPath), holder.image);
//        Glide.with(holder.getContext()).asDrawable().load(suitablePath(localPath)).into(holder.image);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends ChatItemWithTextHolder implements IThumbNailItem, IProgress {
        protected FontIconTextView more;
        protected ReserveSpaceRoundedImageView image;
        protected MessageProgress progress;

        public ViewHolder(View view) {
            super(view);
            boolean withText = true;
            FrameLayout frameLayout = new FrameLayout(view.getContext());
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

            image = new ReserveSpaceRoundedImageView(view.getContext());
            image.setId(R.id.thumbnail);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            LinearLayout.LayoutParams layout_758 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            image.setLayoutParams(layout_758);
            image.setCornerRadius(HelperRadius.computeRadius());
            getContentBloke().addView(frameLayout);
            if (withText) {
                setLayoutMessageContainer();
            }
            frameLayout.addView(image);


            more = new FontIconTextView(view.getContext());
            more.setId(R.id.more);
            //more.setBackgroundResource(R.drawable.bg_message_image_time);
            more.setGravity(Gravity.CENTER);
            more.setText(R.string.horizontal_more_icon);
            setTextSize(more, R.dimen.largeTextSize);
            more.setTextColor(G.context.getResources().getColor(R.color.white));
            more.setPadding(i_Dp(R.dimen.dp8), i_Dp(R.dimen.dp8), i_Dp(R.dimen.dp12), i_Dp(R.dimen.dp8));
            FrameLayout.LayoutParams layout_50 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout_50.gravity = Gravity.RIGHT | Gravity.TOP;
            /*layout_50.bottomMargin = -dpToPx(2);
            layout_50.rightMargin = dpToPx(5);
            layout_50.topMargin = dpToPx(7);*/
            more.setLayoutParams(layout_50);
            frameLayout.addView(more);

            progress = getProgressBar(view.getContext(), 0);
            frameLayout.addView(progress, new FrameLayout.LayoutParams(i_Dp(R.dimen.dp60), i_Dp(R.dimen.dp60), Gravity.CENTER));
        }

        public FontIconTextView getMoreButton() {
            return more;
        }

        @Override
        public ImageView getThumbNailImageView() {
            return image;
        }

        @Override
        public MessageProgress getProgress() {
            return progress;
        }

        @Override
        public TextView getProgressTextView() {
            return null;
        }

        @Override
        public String getTempTextView() {
            return null;
        }
    }
}
