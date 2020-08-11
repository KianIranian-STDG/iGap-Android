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

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

public class UnreadMessage extends AbstractMessage<UnreadMessage, UnreadMessage.ViewHolder> {

    public UnreadMessage(MessagesAdapter<AbstractMessage> mAdapter, IMessageItem messageClickListener) {
        super(mAdapter, false, ProtoGlobal.Room.Type.CHAT, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.cslum_txt_unread_message;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        holder.txtUnreadMessage.setOnClickListener(v -> {

        });

        holder.txtUnreadMessage.setBackgroundColor(theme.getPrimaryDarkColor(holder.txtUnreadMessage.getContext()));

        holder.txtUnreadMessage.setOnLongClickListener(v -> true);

        super.bindView(holder, payloads);

        setTextIfNeeded(holder.txtUnreadMessage);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtUnreadMessage;

        public ViewHolder(View view) {
            super(view);
            txtUnreadMessage = (TextView) ViewMaker.getUnreadMessageItemView(view.getContext());
            ((ViewGroup) itemView).addView(txtUnreadMessage);
        }
    }
}
