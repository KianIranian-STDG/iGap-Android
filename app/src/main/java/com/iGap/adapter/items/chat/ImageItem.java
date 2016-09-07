package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.MyType;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class ImageItem extends AbstractChatItem<ImageItem, ImageItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    @Override
    public int getType() {
        return R.id.chatSubLayoutImage;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_image;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.imvPicture.setImageResource(Integer.parseInt(mMessage.filePath));
        holder.cslr_txt_time.setText(Long.toString(mMessage.time));

        if (!mMessage.messageText.isEmpty()) {
            holder.cslr_txt_message.setText(mMessage.messageText);
            holder.cslr_txt_message.setVisibility(View.VISIBLE);
        } else {
            holder.cslr_txt_message.setVisibility(View.GONE);
        }

        if (mMessage.sendType == MyType.SendType.send) {
            holder.cslr_txt_tic.setText(defineMessageStatus());
        }

        // TODO: 9/6/2016 [Alireza Eskandarpour Shoferi] check if message was a replay, update layout
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

        holder.cslr_txt_tic.setVisibility(View.GONE);
        holder.cslr_ll_frame.setBackgroundResource(R.drawable.rectangle_round_gray);
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);

        holder.cslr_txt_tic.setVisibility(View.VISIBLE);
        holder.cslr_ll_frame.setBackgroundResource(R.drawable.rectangle_round_white);
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
        protected ImageView imvPicture;
        protected FrameLayout cslr_ll_frame;
        protected LinearLayout cslr_ll_content_main;
        protected LinearLayout cslr_ll_forward;
        protected TextView cslr_txt_forward_from;
        protected LinearLayout cslr_ll_time;
        protected TextView cslr_txt_time;
        protected TextView cslr_txt_tic;
        protected TextView cslr_txt_message;

        public ViewHolder(View view) {
            super(view);

            cslr_txt_message = (TextView) view.findViewById(R.id.cslr_txt_message);
            cslr_ll_frame = (FrameLayout) view.findViewById(R.id.cslr_ll_frame);
            cslr_ll_content_main = (LinearLayout) view.findViewById(R.id.cslr_ll_content_main);
            cslr_ll_forward = (LinearLayout) view.findViewById(R.id.cslr_ll_forward);
            cslr_txt_forward_from = (TextView) view.findViewById(R.id.cslr_txt_forward_from);
            cslr_ll_time = (LinearLayout) view.findViewById(R.id.cslr_ll_time);
            cslr_txt_time = (TextView) view.findViewById(R.id.cslr_txt_time);
            cslr_txt_tic = (TextView) view.findViewById(R.id.cslr_txt_tic);
            imvPicture = (ImageView) view.findViewById(R.id.shli_imv_image);

            cslr_txt_tic.setTypeface(G.fontawesome);
        }
    }
}
