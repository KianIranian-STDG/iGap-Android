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
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.IMessageItem;
import net.iGap.module.AndroidUtils;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

public class FileItem extends AbstractMessage<FileItem, FileItem.ViewHolder> {

    public FileItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutFile;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {

        if (holder.itemView.findViewById(R.id.mainContainer) == null) {
            ((ViewGroup) holder.itemView).addView(ViewMaker.getFileItem());
        }

        super.bindView(holder, payloads);

        String text = "";

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                ((TextView) holder.itemView.findViewById(R.id.songArtist)).setText(mMessage.forwardedFrom.getAttachment().getName());
                ((TextView) holder.itemView.findViewById(R.id.fileSize)).setText(AndroidUtils.humanReadableByteCount(mMessage.forwardedFrom.getAttachment().getSize(), true));
            }
            text = mMessage.forwardedFrom.getMessage();
        } else {
            if (mMessage.attachment != null) {
                ((TextView) holder.itemView.findViewById(R.id.songArtist)).setText(mMessage.attachment.name);
                ((TextView) holder.itemView.findViewById(R.id.fileSize)).setText(AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true));
            }

            text = mMessage.messageText;
        }

        if (mMessage.hasEmojiInText) {
            setTextIfNeeded((EmojiTextViewE) holder.itemView.findViewById(R.id.messageSenderTextMessage), text);
        } else {
            setTextIfNeeded((TextView) holder.itemView.findViewById(R.id.messageSenderTextMessage), text);
        }

        RealmRoomMessage roomMessage = G.getRealm().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.valueOf(mMessage.messageID)).findFirst();
        if (roomMessage != null) {
            (holder.itemView.findViewById(R.id.thumbnail)).setVisibility(View.VISIBLE);
            if (roomMessage.getForwardMessage() != null) {
                if (roomMessage.getForwardMessage().getAttachment().getName().toLowerCase().endsWith(".pdf")) {
                    ((ImageView) holder.itemView.findViewById(R.id.thumbnail)).setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.pdf_icon));
                } else if (roomMessage.getForwardMessage().getAttachment().getName().toLowerCase().endsWith(".txt")) {
                    ((ImageView) holder.itemView.findViewById(R.id.thumbnail)).setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.txt_icon));
                } else if (roomMessage.getForwardMessage().getAttachment().getName().toLowerCase().endsWith(".exe")) {
                    ((ImageView) holder.itemView.findViewById(R.id.thumbnail)).setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.exe_icon));
                } else if (roomMessage.getForwardMessage().getAttachment().getName().toLowerCase().endsWith(".docs")) {
                    ((ImageView) holder.itemView.findViewById(R.id.thumbnail)).setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.docx_icon));
                } else {
                    ((ImageView) holder.itemView.findViewById(R.id.thumbnail)).setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.file_icon));
                }
            } else {
                if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".pdf")) {
                    ((ImageView) holder.itemView.findViewById(R.id.thumbnail)).setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.pdf_icon));
                } else if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".txt")) {
                    ((ImageView) holder.itemView.findViewById(R.id.thumbnail)).setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.txt_icon));
                } else if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".exe")) {
                    ((ImageView) holder.itemView.findViewById(R.id.thumbnail)).setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.exe_icon));
                } else if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".docs")) {
                    ((ImageView) holder.itemView.findViewById(R.id.thumbnail)).setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.docx_icon));
                } else {
                    ((ImageView) holder.itemView.findViewById(R.id.thumbnail)).setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.file_icon));
                }
            }
        }
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);
        ((TextView) holder.itemView.findViewById(R.id.songArtist)).setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        ((TextView) holder.itemView.findViewById(R.id.fileSize)).setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * this commented code used with xml layout
         */
        //protected TextView cslf_txt_file_name;
        //protected TextView cslf_txt_file_size;
        //protected ImageView thumbnail;

        public ViewHolder(View view) {
            super(view);
            //cslf_txt_file_name = (TextView) view.findViewById(R.id.songArtist);
            //cslf_txt_file_size = (TextView) view.findViewById(R.id.fileSize);
            //thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
}
