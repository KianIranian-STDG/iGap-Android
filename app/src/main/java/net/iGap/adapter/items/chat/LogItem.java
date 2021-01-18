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

import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperLogMessage;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import me.saket.bettermovementmethod.BetterLinkMovementMethod;

public class LogItem extends AbstractMessage<LogItem, LogItem.ViewHolder> {

    public LogItem(MessagesAdapter<AbstractMessage> mAdapter, IMessageItem messageClickListener) {
        super(mAdapter, false, ProtoGlobal.Room.Type.CHAT, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutLog;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);// TODO: 12/29/20 MESSAGE_REFACTOR
       holder.text.setText(HelperLogMessage.deserializeLog(holder.text.getContext(), messageObject.log.data, true));
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView text;

        public ViewHolder(View view) {
            super(view);
            text = (TextView) ViewMaker.getLogItemView(view.getContext());

            ((ViewGroup) itemView).addView(text);

            text.setMovementMethod(LinkMovementMethod.getInstance());
            BetterLinkMovementMethod
                    .linkify(Linkify.ALL, text)
                    .setOnLinkClickListener((tv, url) -> FragmentChat.isInSelectionMode)
                    .setOnLinkLongClickListener((tv, url) -> true);
        }
    }
}
