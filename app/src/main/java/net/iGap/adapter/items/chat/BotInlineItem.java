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
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.helper.LayoutCreator;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

public class BotInlineItem extends AbstractMessage<BotInlineItem, BotInlineItem.ViewHolder> {

    public BotInlineItem(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.cardToCard;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {

        super.bindView(holder, payloads);
        holder.getChatBloke().setBackgroundResource(0);

        setTextIfNeeded(holder.messageTv);

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends NewChatItemHolder {
        private LinearLayout rootView;
        private LinearLayout buttonContainer;
        private TextView messageTv;

        public ViewHolder(View view) {
            super(view);
            messageTv = new TextView(getContext());
            buttonContainer = new LinearLayout(getContext());
            buttonContainer.setOrientation(LinearLayout.VERTICAL);

            rootView = new LinearLayout(getContext());
            rootView.setOrientation(LinearLayout.VERTICAL);

            rootView.addView(messageTv, 0, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));
            rootView.addView(buttonContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 8, 0, 0));


            getContentBloke().addView(rootView, 0, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));
        }

        public LinearLayout getButtonContainer() {
            return buttonContainer;
        }

        public TextView getMessageTv() {
            return messageTv;
        }

        public LinearLayout getRootView() {
            return rootView;
        }
    }
}
