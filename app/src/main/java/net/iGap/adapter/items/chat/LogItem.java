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
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import net.iGap.R;
import net.iGap.helper.HelperLogMessage;
import net.iGap.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

public class LogItem extends AbstractMessage<LogItem, LogItem.ViewHolder> {

    public LogItem(IMessageItem messageClickListener) {
        super(false, ProtoGlobal.Room.Type.CHAT, messageClickListener);
    }

    @Override public int getType() {
        return R.id.chatSubLayoutLog;
    }

    @Override public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override public void bindView(ViewHolder holder, List payloads) {

        if (holder.itemView.findViewById(R.id.csll_txt_log_text) == null) {
            ((ViewGroup) holder.itemView).addView(ViewMaker.getLogItem());
        }

        holder.text = (TextView) holder.itemView.findViewById(R.id.csll_txt_log_text);
        holder.text.setMovementMethod(LinkMovementMethod.getInstance());

        super.bindView(holder, payloads);

        //Realm realm=Realm.getDefaultInstance();
        //
        //RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID ,Long.parseLong(mMessage.messageID)).findFirst();
        //if(roomMessage!=null){
        //
        //}
        //
        //realm.close();

        holder.text.setText(HelperLogMessage.getLogMessageWithLink(mMessage.messageText));

        // setTextIfNeeded(holder.text, mMessage.messageText);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView text;

        public ViewHolder(View view) {
            super(view);
            /**
             *  this commented code used with xml layout
             */
            //text = (TextView) view.findViewById(R.id.csll_txt_log_text);
            //text.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
}
