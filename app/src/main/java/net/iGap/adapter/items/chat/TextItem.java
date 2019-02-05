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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vanniktech.emoji.EmojiUtils;

import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.IMessageItem;
import net.iGap.module.EmojiTextViewE;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import io.realm.Realm;

public class TextItem extends AbstractMessage<TextItem, TextItem.ViewHolder> {

    public TextItem(Realm realmChat, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(realmChat, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutMessage;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        String text;
        if (mMessage.forwardedFrom != null) {
            text = mMessage.forwardedFrom.getMessage();
        } else {
            text = mMessage.messageText;
        }

        View msgView = holder.itemView.findViewById(R.id.messageSenderTextMessage);
        if (mMessage.hasEmojiInText) {
            if (text.length() <= 2) {
                if (EmojiUtils.emojisCount(text) == 1) {
                    EmojiTextViewE textViewE = (EmojiTextViewE) msgView;
                    textViewE.setEmojiSize((int) G.context.getResources().getDimension(R.dimen.dp28));
                }
            }
        }

        setTextIfNeeded((TextView) msgView, text);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends ChatItemHolder {

        public ViewHolder(View view) {
            super(view);
            LinearLayout csliwt_layout_container_message = ViewMaker.getTextView();
            LinearLayout.LayoutParams layout_577 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            csliwt_layout_container_message.setLayoutParams(layout_577);

            m_container.addView(csliwt_layout_container_message);
        }
    }
}
