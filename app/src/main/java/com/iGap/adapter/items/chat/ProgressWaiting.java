package com.iGap.adapter.items.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import com.iGap.R;
import com.iGap.interfaces.IMessageItem;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import java.util.List;

public class ProgressWaiting extends AbstractMessage<com.iGap.adapter.items.chat.ProgressWaiting, com.iGap.adapter.items.chat.ProgressWaiting.ViewHolder> {
    private static final ViewHolderFactory<? extends com.iGap.adapter.items.chat.ProgressWaiting.ViewHolder> FACTORY = new com.iGap.adapter.items.chat.ProgressWaiting.ItemFactory();

    public ProgressWaiting(IMessageItem messageClickListener) {
        super(false, ProtoGlobal.Room.Type.CHAT, messageClickListener);
    }

    @Override public int getType() {
        return R.id.cslp_progress_bar_waiting;
    }

    @Override public int getLayoutRes() {
        return R.layout.chat_sub_layout_progress;
    }

    @Override public void bindView(com.iGap.adapter.items.chat.ProgressWaiting.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
    }

    @Override protected void voteAction(com.iGap.adapter.items.chat.ProgressWaiting.ViewHolder holder) {
        super.voteAction(holder);
    }

    @Override public ViewHolderFactory<? extends com.iGap.adapter.items.chat.ProgressWaiting.ViewHolder> getFactory() {
        return FACTORY;
    }

    protected static class ItemFactory implements ViewHolderFactory<com.iGap.adapter.items.chat.ProgressWaiting.ViewHolder> {
        public com.iGap.adapter.items.chat.ProgressWaiting.ViewHolder create(View v) {
            return new com.iGap.adapter.items.chat.ProgressWaiting.ViewHolder(v);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);

            progressBar = (ProgressBar) view.findViewById(R.id.cslp_progress_bar_waiting);
        }
    }
}
