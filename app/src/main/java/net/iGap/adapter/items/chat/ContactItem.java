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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.IMessageItem;
import net.iGap.module.AppUtils;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import io.realm.Realm;

import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static net.iGap.G.context;
import static net.iGap.R.dimen.messageContainerPadding;

public class ContactItem extends AbstractMessage<ContactItem, ContactItem.ViewHolder> {

    public ContactItem(Realm realmChat, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(realmChat, true, type, messageClickListener);
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);
        AppUtils.setImageDrawable(holder.image, R.drawable.black_contact);
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);
        holder.name.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        holder.name.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        AppUtils.setImageDrawable(holder.image, R.drawable.green_contact);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutContact;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getRoomMessageContact() != null) {
                holder.name.setText(mMessage.forwardedFrom.getRoomMessageContact().getFirstName() + " " + mMessage.forwardedFrom.getRoomMessageContact().getLastName());
                holder.number.setText(mMessage.forwardedFrom.getRoomMessageContact().getLastPhoneNumber());
            }
        } else {
            if (mMessage.userInfo != null) {
                holder.name.setText(mMessage.userInfo.displayName);
                holder.number.setText(mMessage.userInfo.phone);
            }
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends ChatItemHolder {

        protected TextView name;
        protected TextView number;
        protected ImageView image;

        public ViewHolder(View view) {
            super(view);
            LinearLayout container2 = new LinearLayout(context);
            LinearLayout.LayoutParams layoutParamsContainer2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            container2.setOrientation(HORIZONTAL);
            container2.setPadding((int) G.context.getResources().getDimension(messageContainerPadding), 0, 5, 2);
            container2.setLayoutParams(layoutParamsContainer2);

            image = new ImageView(G.context);
            LinearLayout.LayoutParams layoutParamsImage = new LinearLayout.LayoutParams(ViewMaker.i_Dp(R.dimen.dp48), ViewMaker.i_Dp(R.dimen.dp48));
            layoutParamsImage.rightMargin = 14;
            image.setId(R.id.image);
            image.setContentDescription(null);
            AppUtils.setImageDrawable(image, R.mipmap.user);
            image.setLayoutParams(layoutParamsImage);

            LinearLayout container3 = new LinearLayout(context);
            LinearLayout.LayoutParams layoutParamsContainer3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            container3.setOrientation(VERTICAL);
            container3.setLayoutParams(layoutParamsContainer3);

            name = new TextView(G.context);
            LinearLayout.LayoutParams layoutParamsName = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            name.setId(R.id.name);
            name.setTextAppearance(context, android.R.style.TextAppearance_Medium);
            name.setTextColor(Color.parseColor(G.textBubble));
            name.setText("Contact Name");
            ViewMaker.setTextSize(name, R.dimen.dp14);
            ViewMaker.setTypeFace(name);
            name.setLayoutParams(layoutParamsName);
            container3.addView(name);

            number = new TextView(G.context);
            LinearLayout.LayoutParams layoutParamsNumber = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            number.setId(R.id.number);
            number.setTextAppearance(context, android.R.style.TextAppearance_Small);
            ViewMaker.setTypeFace(number);

            number.setTextColor(Color.parseColor(G.textBubble));
            number.setText("Contact Number");
            number.setLayoutParams(layoutParamsNumber);

            container3.addView(number);
            container2.addView(image);
            container2.addView(container3);

            m_container.addView(container2);
        }
    }
}
