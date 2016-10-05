package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.interface_package.OnMessageViewClick;
import com.iGap.module.CircleImageView;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class ContactItem extends AbstractChatItem<ContactItem, ContactItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public ContactItem(ProtoGlobal.Room.Type type, OnMessageViewClick messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutContact;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_contact;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.name.setText(mMessage.userInfo.displayName);
        holder.number.setText(mMessage.userInfo.phone);
        holder.username.setText("@" + mMessage.userInfo.username);
        // TODO: 10/5/2016 [Alireza] set avatar
            /*if (mMessage.userInfo.avatar != null && !mMessage.userInfo.avatar.isEmpty()) {
                holder.image.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(suitablePath(mMessage.userInfo.imageSource), holder.image);
            } else {
                holder.image.setVisibility(View.GONE);
            }*/
    }

    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView image;
        protected TextView name;
        protected TextView number;
        protected TextView username;

        public ViewHolder(View view) {
            super(view);
            image = (CircleImageView) view.findViewById(R.id.image);
            name = (TextView) view.findViewById(R.id.name);
            number = (TextView) view.findViewById(R.id.number);
            username = (TextView) view.findViewById(R.id.username);
        }
    }
}
