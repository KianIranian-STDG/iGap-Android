package net.iGap.adapter.items.discovery.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

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
        List<ProtoGlobal.DiscoveryField> discoveryFields = item.getDiscoveryfieldsList();
        G.imageLoader.displayImage(discoveryFields.get(0).getImageurl(), img0);
        G.imageLoader.displayImage(discoveryFields.get(1).getImageurl(), img1);

        card0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDiscoveryFieldsClick(discoveryFields.get(0));
            }
        });

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDiscoveryFieldsClick(discoveryFields.get(1));
            }
        });

    }
}
