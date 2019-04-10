package net.iGap.adapter.items.discovery.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

public class Type7ViewHolder extends BaseViewHolder {
    private ImageView img0, img1, img2;
    private CardView card0, card1, card2;

    public Type7ViewHolder(@NonNull View itemView) {
        super(itemView);
        img0 = itemView.findViewById(R.id.type7_img0);
        img1 = itemView.findViewById(R.id.type7_img1);
        img2 = itemView.findViewById(R.id.type7_img2);
        card0 = itemView.findViewById(R.id.type7_card0);
        card1 = itemView.findViewById(R.id.type7_card1);
        card2 = itemView.findViewById(R.id.type7_card2);
    }

    @Override
    public void bindView(ProtoGlobal.Discovery item) {
        List<ProtoGlobal.DiscoveryField> discoveryFields = item.getDiscoveryfieldsList();
        G.imageLoader.displayImage(discoveryFields.get(0).getImageurl(), img0);
        G.imageLoader.displayImage(discoveryFields.get(1).getImageurl(), img1);
        G.imageLoader.displayImage(discoveryFields.get(2).getImageurl(), img2);

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

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDiscoveryFieldsClick(discoveryFields.get(2));
            }
        });
    }
}
