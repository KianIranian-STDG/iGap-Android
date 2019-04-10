package net.iGap.adapter.items.discovery.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

public class Type1ViewHolder extends BaseViewHolder {
    private ImageView img0;
    private CardView card0;

    public Type1ViewHolder(@NonNull View itemView) {
        super(itemView);
        img0 = itemView.findViewById(R.id.type1_img0);
        card0 = itemView.findViewById(R.id.type1_card0);
    }

    @Override
    public void bindView(ProtoGlobal.Discovery item) {
        List<ProtoGlobal.DiscoveryField> discoveryFields = item.getDiscoveryfieldsList();
        G.imageLoader.displayImage(discoveryFields.get(0).getImageurl(), img0);
    }
}
