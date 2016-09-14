package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/3/2016.
 */
public class VoiceItem extends AbstractChatItem<VoiceItem, VoiceItem.ViewHolder> {
    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public VoiceItem(ProtoGlobal.Room.Type type) {
        super(true, type);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutVoice;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_voice;
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
        protected ImageView csla_imv_state_audio;
        protected TextView csla_txt_audio_name;
        protected TextView csla_txt_audio_mime_type;
        protected TextView csla_txt_audio_size;
        protected Button csla_btn_audio_menu;
        protected TextView cslr_txt_message;

        public ViewHolder(View view) {
            super(view);

            cslr_txt_message = (TextView) view.findViewById(R.id.messageText);
            csla_imv_state_audio = (ImageView) view.findViewById(R.id.csla_imv_state_audio);
            csla_txt_audio_name = (TextView) view.findViewById(R.id.csla_txt_audio_name);
            csla_txt_audio_mime_type = (TextView) view.findViewById(R.id.csla_txt_audio_mime_type);
            csla_txt_audio_size = (TextView) view.findViewById(R.id.csla_txt_audio_size);
            csla_btn_audio_menu = (Button) view.findViewById(R.id.csla_btn_audio_menu);

            csla_btn_audio_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("ddd", "click audio menu");
                }
            });
        }
    }
}
