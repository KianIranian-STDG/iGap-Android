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

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.IMessageItem;
import net.iGap.module.AndroidUtils;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

import java.util.List;

import io.realm.Realm;

import static net.iGap.fragments.FragmentChat.getRealmChat;

public class FileItem extends AbstractMessage<FileItem, FileItem.ViewHolder> {

    public FileItem(Realm realmChat, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(realmChat, true, type, messageClickListener);
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
    public void onLoadThumbnailFromLocal(final ViewHolder holder, final String tag, final String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, tag, localPath, fileType);
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        String text = "";

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                holder.cslf_txt_file_name.setText(mMessage.forwardedFrom.getAttachment().getName());
                holder.cslf_txt_file_size.setText(AndroidUtils.humanReadableByteCount(mMessage.forwardedFrom.getAttachment().getSize(), true));
            }
            text = mMessage.forwardedFrom.getMessage();
        } else {
            if (mMessage.attachment != null) {
                holder.cslf_txt_file_name.setText(mMessage.attachment.name);
                holder.cslf_txt_file_size.setText(AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true));
            }

            text = mMessage.messageText;
        }

        setTextIfNeeded(holder.itemView.findViewById(R.id.messageSenderTextMessage), text);

        RealmRoomMessage roomMessage = RealmRoomMessage.getFinalMessage(getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.valueOf(mMessage.messageID)).findFirst());
        if (roomMessage != null) {
            holder.thumbnail.setVisibility(View.VISIBLE);
            if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".pdf")) {
                holder.thumbnail.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.pdf_icon));
            } else if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".txt")) {
                holder.thumbnail.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.txt_icon));
            } else if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".exe")) {
                holder.thumbnail.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.exe_icon));
            } else if (roomMessage.getAttachment().getName().toLowerCase().endsWith(".docs")) {
                holder.thumbnail.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.docx_icon));
            } else {
                holder.thumbnail.setImageDrawable(net.iGap.messageprogress.AndroidUtils.getDrawable(G.currentActivity, R.drawable.file_icon));
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
//        if (G.isDarkTheme) {
//            holder.cslf_txt_file_name.setTextColor(Color.parseColor(G.textTitleTheme));
//            holder.cslf_txt_file_size.setTextColor(Color.parseColor(G.textSubTheme));
//        } else {
//            holder.cslf_txt_file_name.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
//            holder.cslf_txt_file_size.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
//        }
//
        holder.cslf_txt_file_name.setTextColor(Color.parseColor(G.textBubble));
        holder.cslf_txt_file_size.setTextColor(Color.parseColor(G.textBubble));

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView cslf_txt_file_name;
        protected TextView cslf_txt_file_size;
        protected ImageView thumbnail;

        public ViewHolder(View view) {
            super(view);
            if (itemView.findViewById(R.id.mainContainer) == null) {
                ((ViewGroup) itemView).addView(ViewMaker.getFileItem());
            }
            cslf_txt_file_name = (TextView) itemView.findViewById(R.id.songArtist);
            cslf_txt_file_size = (TextView) itemView.findViewById(R.id.fileSize);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }
}
