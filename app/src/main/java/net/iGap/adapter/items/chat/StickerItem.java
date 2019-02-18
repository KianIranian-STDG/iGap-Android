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

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.interfaces.IMessageItem;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import static net.iGap.module.AndroidUtils.suitablePath;

public class StickerItem extends AbstractMessage<StickerItem, StickerItem.ViewHolder> {

    public StickerItem(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.sticker_layout;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        holder.image.setTag(getCacheId(mMessage));
        super.bindView(holder, payloads);

        ((View) (holder.itemView.findViewById(R.id.contentContainer)).getParent()).setBackgroundResource(0);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    public void onLoadThumbnailFromLocal(ViewHolder holder, String tag, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, tag, localPath, fileType);

        if (holder.image != null && holder.image.getTag() != null && (holder.image.getTag()).equals(tag)) {
            G.imageLoader.displayImage(suitablePath(localPath), holder.image);
        }

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends ChatItemHolder {
        protected ReserveSpaceRoundedImageView image;

        public ViewHolder(View view) {
            super(view);

            FrameLayout frameLayout = new FrameLayout(G.context);
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

            image = new ReserveSpaceRoundedImageView(G.context);
            image.setId(R.id.thumbnail);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            image.setCornerRadius((int) G.context.getResources().getDimension(R.dimen.messageBox_cornerRadius));
            LinearLayout.LayoutParams layout_758 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            image.setLayoutParams(layout_758);
            m_container.addView(frameLayout);
            frameLayout.addView(image);
            image.reserveSpace(180,180,ProtoGlobal.Room.Type.CHAT);

        }
    }
}
