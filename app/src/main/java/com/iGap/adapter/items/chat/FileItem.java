package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.MyType;
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

        holder.cslr_txt_time.setText(formatTime());

        if (!mMessage.messageText.isEmpty()) {
            holder.cslr_txt_message.setText(mMessage.messageText);
            holder.cslr_txt_message.setVisibility(View.VISIBLE);
        } else {
            holder.cslr_txt_message.setVisibility(View.GONE);
        }

        if (mMessage.sendType == MyType.SendType.send) {
            updateMessageStatus(holder.cslr_txt_tic);
        }
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

        holder.cslr_txt_tic.setVisibility(View.GONE);
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);

        holder.cslr_txt_tic.setVisibility(View.VISIBLE);
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
        protected LinearLayout cslr_ll_frame;
        protected LinearLayout cslr_ll_content_main;
        protected LinearLayout cslr_ll_forward;
        protected TextView cslr_txt_forward_from;
        protected LinearLayout cslr_ll_time;
        protected TextView cslr_txt_time;
        protected MaterialDesignTextView cslr_txt_tic;
        protected ImageView cslf_imv_image_file;
        protected ImageView cslf_imv_state_file;
        protected TextView cslf_txt_file_name;
        protected TextView cslf_txt_file_mime_type;
        protected TextView cslf_txt_file_size;
        protected TextView cslr_txt_message;

        protected ImageView chslr_imv_icon_replay;
        protected View chslr_v_vertical_line;
        protected ImageView chslr_imv_replay_pic;
        protected TextView chslr_txt_replay_from;
        protected TextView chslr_txt_replay_message;

        public ViewHolder(View view) {
            super(view);

            cslr_txt_message = (TextView) view.findViewById(R.id.cslr_txt_message);
            cslr_ll_frame = (LinearLayout) view.findViewById(R.id.mainContainer);
            cslr_ll_content_main = (LinearLayout) view.findViewById(R.id.cslr_ll_content_main);
            cslr_ll_forward = (LinearLayout) view.findViewById(R.id.cslr_ll_forward);
            cslr_txt_forward_from = (TextView) view.findViewById(R.id.cslr_txt_forward_from);
            cslr_ll_time = (LinearLayout) view.findViewById(R.id.cslr_ll_time);
            cslr_txt_time = (TextView) view.findViewById(R.id.cslr_txt_time);
            cslr_txt_tic = (MaterialDesignTextView) view.findViewById(R.id.cslr_txt_tic);
            cslf_imv_image_file = (ImageView) view.findViewById(R.id.cslf_imv_image_file);
            cslf_imv_state_file = (ImageView) view.findViewById(R.id.cslf_imv_state_file);
            cslf_txt_file_name = (TextView) view.findViewById(R.id.cslf_txt_file_name);
            cslf_txt_file_mime_type = (TextView) view.findViewById(R.id.cslf_txt_file_mime_type);
            cslf_txt_file_size = (TextView) view.findViewById(R.id.cslf_txt_file_size);

            chslr_imv_icon_replay = (ImageView) view.findViewById(R.id.chslr_imv_icon_replay);
            chslr_v_vertical_line = view.findViewById(R.id.chslr_v_vertical_line);
            chslr_imv_replay_pic = (ImageView) view.findViewById(R.id.chslr_imv_replay_pic);
            chslr_txt_replay_from = (TextView) view.findViewById(R.id.chslr_txt_replay_from);
            chslr_txt_replay_message = (TextView) view.findViewById(R.id.chslr_txt_replay_message);
        }
    }
}
