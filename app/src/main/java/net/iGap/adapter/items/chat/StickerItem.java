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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperRadius;
import net.iGap.interfaces.IMessageItem;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import io.realm.Realm;

import static net.iGap.module.AndroidUtils.suitablePath;

public class StickerItem extends AbstractMessage<StickerItem, StickerItem.ViewHolder> {

    public StickerItem(Realm realmChat, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(realmChat, true, type, messageClickListener);
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

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ReserveSpaceRoundedImageView image;

        public ViewHolder(View view) {
            super(view);

            if (itemView.findViewById(R.id.mainContainer) == null) {
                ((ViewGroup) itemView).addView(ViewMaker.getImageItem(true));
            }




            image = itemView.findViewById(R.id.thumbnail);
            image.reserveSpace(180,180,ProtoGlobal.Room.Type.CHAT);

        }
    }
}
