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

import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.customView.RadialProgressView;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

public class ProgressWaiting extends AbstractMessage<net.iGap.adapter.items.chat.ProgressWaiting, net.iGap.adapter.items.chat.ProgressWaiting.ViewHolder> {

    public ProgressWaiting(MessagesAdapter<AbstractMessage> mAdapter, IMessageItem messageClickListener) {
        super(mAdapter, false, ProtoGlobal.Room.Type.CHAT, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.cslp_progress_bar_waiting;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(net.iGap.adapter.items.chat.ProgressWaiting.ViewHolder holder, List payloads) {
        holder.itemView.setOnLongClickListener(v -> true);
        super.bindView(holder, payloads);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);

            RadialProgressView progressView = new RadialProgressView(view.getContext());
            progressView.setSize(72);

            ((ViewGroup) itemView).addView(progressView, LayoutCreator.createFrame(42, 42, Gravity.CENTER));
        }
    }
}
