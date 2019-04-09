package net.iGap.adapter.items.discovery.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import net.iGap.R;
import net.iGap.proto.ProtoGlobal;

public class Type4ViewHolder extends BaseViewHolder {
    private ImageView img0, img1;
    private CardView card0, card1;

    public Type4ViewHolder(@NonNull View itemView) {
        super(itemView);
        img0 = itemView.findViewById(R.id.type4_img0);
        img1 = itemView.findViewById(R.id.type4_img1);
        card0 = itemView.findViewById(R.id.type4_card0);
        card1 = itemView.findViewById(R.id.type4_card1);
    }

    @Override
    public void bindView(ProtoGlobal.Discovery item) {

    }
}
