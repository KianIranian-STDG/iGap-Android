package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.interfaces.IMessageItem;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class LocationItem extends AbstractMessage<LocationItem, LocationItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public LocationItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutLocation;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_location;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.forwardedFrom != null) {
            if (!TextUtils.isEmpty(mMessage.forwardedFrom.getLocation().toString())) {
                holder.location.setText(mMessage.forwardedFrom.getLocation().toString());
            }
        } else {
            if (!TextUtils.isEmpty(mMessage.location)) {
                holder.location.setText(mMessage.location);
            }
        }
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
        protected TextView location;
        public ViewHolder(View view) {
            super(view);

            location = (TextView) view.findViewById(R.id.location);
        }
    }
}
