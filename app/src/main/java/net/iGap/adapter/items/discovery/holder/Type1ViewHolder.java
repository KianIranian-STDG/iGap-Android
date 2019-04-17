package net.iGap.adapter.items.discovery.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;

public class Type1ViewHolder extends BaseViewHolder {
    private ImageView img0;
    private CardView card0;

    public Type1ViewHolder(@NonNull View itemView) {
        super(itemView);
        img0 = itemView.findViewById(R.id.type1_img0);
        card0 = itemView.findViewById(R.id.type1_card0);
        card0.setCardBackgroundColor(G.getThemeBackgroundColor());
    }

    @Override
    public void bindView(DiscoveryItem item) {

        if (item.discoveryFields.get(0).imageUrl.endsWith(".gif")) {
            Glide.with(G.context)
                    .asGif()
                    .load(item.discoveryFields.get(0).imageUrl)
                    .into(img0);
        } else {
            G.imageLoader.displayImage(item.discoveryFields.get(0).imageUrl, img0, option);
        }

        card0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDiscoveryFieldsClick(item.discoveryFields.get(0));
            }
        });
    }
}
