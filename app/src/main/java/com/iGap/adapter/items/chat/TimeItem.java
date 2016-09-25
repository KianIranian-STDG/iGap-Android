package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class TimeItem extends AbstractChatItem<TimeItem, TimeItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public TimeItem() {
        super(false, ProtoGlobal.Room.Type.CHAT);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutTime;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_time;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
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
        protected TextView cslr_txt_time;

        public ViewHolder(View view) {
            super(view);

            cslr_txt_time = (TextView) view.findViewById(R.id.cslr_txt_time);
        }
    }
}
