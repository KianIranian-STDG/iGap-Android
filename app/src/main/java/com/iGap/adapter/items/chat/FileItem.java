package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class FileItem extends AbstractChatItem<FileItem, FileItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public FileItem(ProtoGlobal.Room.Type type) {
        super(true, type);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutFile;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_file;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (!mMessage.messageText.isEmpty()) {
            holder.cslr_txt_message.setText(mMessage.messageText);
            holder.cslr_txt_message.setVisibility(View.VISIBLE);
        } else {
            holder.cslr_txt_message.setVisibility(View.GONE);
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
        protected ImageView cslf_imv_image_file;
        protected ImageView cslf_imv_state_file;
        protected TextView cslf_txt_file_name;
        protected TextView cslf_txt_file_mime_type;
        protected TextView cslf_txt_file_size;
        protected TextView cslr_txt_message;

        public ViewHolder(View view) {
            super(view);

            cslr_txt_message = (TextView) view.findViewById(R.id.messageText);
            cslf_imv_image_file = (ImageView) view.findViewById(R.id.cslf_imv_image_file);
            cslf_imv_state_file = (ImageView) view.findViewById(R.id.cslf_imv_state_file);
            cslf_txt_file_name = (TextView) view.findViewById(R.id.cslf_txt_file_name);
            cslf_txt_file_mime_type = (TextView) view.findViewById(R.id.cslf_txt_file_mime_type);
            cslf_txt_file_size = (TextView) view.findViewById(R.id.cslf_txt_file_size);
        }
    }
}
