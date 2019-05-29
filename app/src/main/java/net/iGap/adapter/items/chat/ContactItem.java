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

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.MessagesAdapter;
import net.iGap.helper.LayoutCreator;
import net.iGap.interfaces.IMessageItem;
import net.iGap.module.AppUtils;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

public class ContactItem extends AbstractMessage<ContactItem, ContactItem.ViewHolder> {

    public ContactItem(MessagesAdapter<AbstractMessage> mAdapter, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(mAdapter, true, type, messageClickListener);
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);
        AppUtils.setImageDrawable(holder.contactImage, R.drawable.black_contact);
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);
        holder.contactName.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        holder.contactName.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        AppUtils.setImageDrawable(holder.contactImage, R.drawable.green_contact);
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
                holder.contactName.setText(mMessage.forwardedFrom.getRoomMessageContact().getFirstName() + " " + mMessage.forwardedFrom.getRoomMessageContact().getLastName());
                holder.contactNumber.setText(mMessage.forwardedFrom.getRoomMessageContact().getLastPhoneNumber());
            }
        } else {
            if (mMessage.userInfo != null) {
                holder.contactName.setText(mMessage.userInfo.displayName);
                holder.contactNumber.setText(mMessage.userInfo.phone);
            }
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends NewChatItemHolder {

        private AppCompatTextView contactName;
        private AppCompatTextView contactNumber;
        private AppCompatImageView contactImage;
        private ConstraintLayout rootView;
        private ConstraintSet set;
        private Button viewContactBtn;

        public ViewHolder(View view) {
            super(view);

            /**
             * contact image
             * */

            contactImage = new AppCompatImageView(G.context);
            contactImage.setId(R.id.iv_contactItem_contact);
            contactImage.setContentDescription(null);
            AppUtils.setImageDrawable(contactImage, R.mipmap.user);

            /**
             * contact name
             * */

            contactName = new AppCompatTextView(G.context);
            contactName.setId(R.id.tv_contactItem_contactName);
            contactName.setTextColor(Color.parseColor(G.textBubble));
            ViewMaker.setTextSize(contactName, R.dimen.dp14);
            ViewMaker.setTypeFace(contactName);


            /**
             * contact number
             * */

            contactNumber = new AppCompatTextView(G.context);
            contactNumber.setId(R.id.tv_contactItem_contactNumber);
            ViewMaker.setTypeFace(contactNumber);
            contactNumber.setTextColor(Color.parseColor(G.textBubble));

            /**
             * view contact button
             * */

            viewContactBtn = new Button(getContext());
            viewContactBtn.setId(R.id.btn_contactItem_viewContact);
            viewContactBtn.setText(getContext().getResources().getString(R.string.view_contact));
            viewContactBtn.setBackground(getDrawable(R.drawable.background_view_contact));
            viewContactBtn.setPadding(LayoutCreator.dp(16), 0, LayoutCreator.dp(16), 0);
            viewContactBtn.setTextSize(12);
            viewContactBtn.setTextColor(getColor(R.color.md_blue_500));
            viewContactBtn.setAllCaps(false);


            /**
             * root view
             * */

            rootView = new ConstraintLayout(getContext());
            set = new ConstraintSet();


            /**
             * set views dependency
             * */

            set.constrainWidth(contactImage.getId(), LayoutCreator.dp(35));
            set.constrainHeight(contactImage.getId(), LayoutCreator.dp(35));

            set.constrainWidth(contactNumber.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainHeight(contactNumber.getId(), ConstraintSet.WRAP_CONTENT);

            set.constrainWidth(contactName.getId(), ConstraintSet.WRAP_CONTENT);
            set.constrainHeight(contactName.getId(), ConstraintSet.WRAP_CONTENT);


            set.connect(contactImage.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, LayoutCreator.dp(8));
            set.connect(contactImage.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, LayoutCreator.dp(8));
            set.connect(contactImage.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, LayoutCreator.dp(8));
            rootView.addView(contactImage);


            set.connect(contactName.getId(), ConstraintSet.LEFT, contactImage.getId(), ConstraintSet.RIGHT, LayoutCreator.dp(8));
            rootView.addView(contactName);

            set.connect(contactNumber.getId(), ConstraintSet.LEFT, contactName.getId(), ConstraintSet.LEFT);
            rootView.addView(contactNumber);

            int[] chainViews = {contactName.getId(), contactNumber.getId()};
            float[] chainWeights = {0, 0};
            set.createVerticalChain(contactImage.getId(), ConstraintSet.TOP, contactImage.getId(), ConstraintSet.BOTTOM,
                    chainViews, chainWeights, ConstraintSet.CHAIN_PACKED);


            set.applyTo(rootView);
            getContentBloke().addView(rootView, 0);
            getContentBloke().addView(viewContactBtn, 1, LayoutCreator.createFrame(200, 30, Gravity.CENTER,
                    4, 4, 4, 0));
        }
    }
}
