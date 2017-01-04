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
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);
        holder.myLocationLabel.setTextColor(holder.itemView.getResources().getColor(R.color.gray));
        holder.location.setTextColor(holder.itemView.getResources().getColor(R.color.gray));
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);
        //   holder.myLocationLabel.setTextColor(Color.WHITE);
        //  holder.location.setTextColor(Color.WHITE);
    }

    @Override
    protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
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
        protected TextView myLocationLabel;
        public ViewHolder(View view) {
            super(view);

            location = (TextView) view.findViewById(R.id.location);
            myLocationLabel = (TextView) view.findViewById(R.id.myLocationLabel);
        }
    }
}
