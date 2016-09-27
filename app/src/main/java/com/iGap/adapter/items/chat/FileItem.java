package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.nostra13.universalimageloader.core.ImageLoader;

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

        ImageLoader.getInstance().displayImage(suitablePath(mMessage.filePic), holder.cslf_imv_image_file);

        // TODO: 9/15/2016 [Alireza Eskandarpour Shoferi] implement fileState

        holder.cslf_txt_file_name.setText(mMessage.fileName);
        holder.cslf_txt_file_mime_type.setText(mMessage.fileMime);
        holder.cslf_txt_file_size.setText(Long.toString(mMessage.fileSize));

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
            cslr_txt_message.setTextSize(G.userTextSize);
            cslf_imv_image_file = (ImageView) view.findViewById(R.id.cslf_imv_image_file);
            cslf_imv_state_file = (ImageView) view.findViewById(R.id.cslf_imv_state_file);
            cslf_txt_file_name = (TextView) view.findViewById(R.id.cslf_txt_file_name);
            cslf_txt_file_mime_type = (TextView) view.findViewById(R.id.cslf_txt_file_mime_type);
            cslf_txt_file_size = (TextView) view.findViewById(R.id.cslf_txt_file_size);

            cslf_imv_state_file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 9/15/2016 [Alireza Eskandarpour Shoferi] fill
                }
            });
        }
    }
}
