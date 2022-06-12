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

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.messenger.theme.Theme;
import net.iGap.observers.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import static android.view.Gravity.CENTER;
import static android.widget.LinearLayout.HORIZONTAL;

public class TimeItem extends AbstractMessage<TimeItem, TimeItem.ViewHolder> {

    public TimeItem(MessagesAdapter<AbstractMessage> mAdapter, IMessageItem messageClickListener) {
        super(mAdapter, false, ProtoGlobal.Room.Type.CHAT, messageClickListener);
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

    protected class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView text;

        public ViewHolder(View view) {
            super(view);
            LinearLayout linearLayout_33 = new LinearLayout(view.getContext());
            linearLayout_33.setOrientation(HORIZONTAL);
            linearLayout_33.setGravity(CENTER);
            LinearLayout.LayoutParams layout_509 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout_33.setLayoutParams(layout_509);
            linearLayout_33.setPadding(0, ViewMaker.i_Dp(R.dimen.dp8), 0, ViewMaker.i_Dp(R.dimen.dp8));

            View view_12 = new View(view.getContext());
            view_12.setBackgroundColor(Theme.getColor(Theme.key_light_gray));
            LinearLayout.LayoutParams layout_522 = new LinearLayout.LayoutParams(0, 2, 1);
            view_12.setLayoutParams(layout_522);
            linearLayout_33.addView(view_12);

            text = new AppCompatTextView(view.getContext());
            text.setId(R.id.cslt_txt_time_date);
            text.setSingleLine(true);
            text.setPadding(ViewMaker.i_Dp(R.dimen.dp10), ViewMaker.i_Dp(R.dimen.dp1), ViewMaker.i_Dp(R.dimen.dp10), ViewMaker.i_Dp(R.dimen.dp1));
            text.setTextColor(Theme.getColor(Theme.key_dark_theme_color));
            text.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.background_log_time), view.getContext(), Theme.getColor(Theme.key_light_gray)));

            text.setText("Today");
            text.setAllCaps(false);
            ViewMaker.setTextSize(text, R.dimen.dp12);
            ViewMaker.setTypeFace(text);
            LinearLayout.LayoutParams layout_835 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout_835.gravity = Gravity.CENTER_HORIZONTAL;
            text.setLayoutParams(layout_835);
            linearLayout_33.addView(text);

            View vew_147 = new View(view.getContext());
            vew_147.setBackgroundColor(Theme.getColor(Theme.key_light_gray));
            LinearLayout.LayoutParams layout_270 = new LinearLayout.LayoutParams(0, 2, 1);
            vew_147.setLayoutParams(layout_270);
            linearLayout_33.addView(vew_147);
            linearLayout_33.setOnClickListener(null);

            ((ViewGroup) itemView).addView(linearLayout_33);

        }
    }
}
