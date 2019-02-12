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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import io.realm.Realm;

import static android.view.Gravity.CENTER;
import static android.widget.LinearLayout.HORIZONTAL;
import static net.iGap.G.isDarkTheme;

public class TimeItem extends AbstractMessage<TimeItem, TimeItem.ViewHolder> {

    public TimeItem(Realm realmChat, IMessageItem messageClickListener) {
        super(realmChat, false, ProtoGlobal.Room.Type.CHAT, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutTime;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        setTextIfNeeded(holder.text);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView text;

        public ViewHolder(View view) {
            super(view);
            LinearLayout linearLayout_33 = new LinearLayout(G.context);
            linearLayout_33.setOrientation(HORIZONTAL);
            linearLayout_33.setGravity(CENTER);
            LinearLayout.LayoutParams layout_509 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout_33.setLayoutParams(layout_509);
            linearLayout_33.setPadding(0, ViewMaker.i_Dp(R.dimen.dp12), 0, ViewMaker.i_Dp(R.dimen.dp12));

            View view_12 = new View(G.context);
            view_12.setBackgroundColor(Color.parseColor(G.logLineTheme));
            LinearLayout.LayoutParams layout_522 = new LinearLayout.LayoutParams(0, 1, 1);
            view_12.setLayoutParams(layout_522);
            linearLayout_33.addView(view_12);

            text = new TextView(G.context);
            text.setId(R.id.cslt_txt_time_date);
            text.setSingleLine(true);
            text.setPadding(ViewMaker.i_Dp(R.dimen.dp16), ViewMaker.i_Dp(R.dimen.dp4), ViewMaker.i_Dp(R.dimen.dp16), ViewMaker.i_Dp(R.dimen.dp4));
            if (isDarkTheme) {
                text.setBackgroundResource(R.drawable.background_log_time_dark);
                text.setTextColor(Color.parseColor(G.textSubTheme));
            } else {
                text.setBackgroundResource(R.drawable.background_log_time);
                text.setTextColor(G.context.getResources().getColor(R.color.text_log_time));
            }

            text.setText("Today");
            text.setAllCaps(false);
            ViewMaker.setTextSize(text, R.dimen.dp12);
            ViewMaker.setTypeFace(text);
            LinearLayout.LayoutParams layout_835 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout_835.gravity = Gravity.CENTER_HORIZONTAL;
            text.setLayoutParams(layout_835);
            linearLayout_33.addView(text);

            View vew_147 = new View(G.context);
            vew_147.setBackgroundColor(Color.parseColor(G.logLineTheme));
            LinearLayout.LayoutParams layout_270 = new LinearLayout.LayoutParams(0, 1, 1);
            vew_147.setLayoutParams(layout_270);
            linearLayout_33.addView(vew_147);

            ((ViewGroup) itemView).addView(linearLayout_33);

        }
    }
}
