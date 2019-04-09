package net.iGap.adapter.items.discovery.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.iGap.proto.ProtoGlobal;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder{

    BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bindView(ProtoGlobal.Discovery item);
}
