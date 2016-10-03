package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.interface_package.OnMessageViewClick;
import com.iGap.module.Utils;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class FileItem extends AbstractChatItem<FileItem, FileItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public FileItem(ProtoGlobal.Room.Type type, OnMessageViewClick messageClickListener) {
        super(true, type, messageClickListener);
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
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }

    @Override
    public void onLoadFromLocal(ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadFromLocal(holder, localPath, fileType);
        /*if (fileType == LocalFileType.THUMBNAIL) {
            ImageLoader.getInstance().displayImage(suitablePath(localPath), holder.cslf_imv_image_file);
        } else {
            // TODO: 10/2/2016 [Alireza] implement
        }*/
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        // TODO: 9/15/2016 [Alireza Eskandarpour Shoferi] implement fileState

        holder.cslf_txt_file_name.setText(mMessage.fileName);
        holder.cslf_txt_file_size.setText(Utils.humanReadableByteCount(mMessage.fileSize, true));

        if (mMessage.messageText != null && !mMessage.messageText.isEmpty()) {
            holder.cslr_txt_message.setText(mMessage.messageText);
            holder.cslr_txt_message.setVisibility(View.VISIBLE);
        } else {
            holder.cslr_txt_message.setVisibility(View.GONE);
        }
    }

    protected static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView cslf_txt_file_name;
        protected TextView cslf_txt_file_size;
        protected TextView cslr_txt_message;

        public ViewHolder(View view) {
            super(view);

            cslr_txt_message = (TextView) view.findViewById(R.id.messageText);
            cslr_txt_message.setTextSize(G.userTextSize);
            cslf_txt_file_name = (TextView) view.findViewById(R.id.songArtist);
            cslf_txt_file_size = (TextView) view.findViewById(R.id.cslf_txt_file_size);
        }
    }
}
