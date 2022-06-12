package net.iGap.adapter.items.discovery.holder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.messenger.theme.Theme;

public class Type1ViewHolder extends BaseViewHolder {
    private ImageView img0;
    private CardView card0;

    public Type1ViewHolder(@NonNull View itemView, FragmentActivity activity) {
        super(itemView, activity);
        img0 = itemView.findViewById(R.id.type1_img0);
        card0 = itemView.findViewById(R.id.type1_card0);
        card0.setCardBackgroundColor(Theme.getColor(Theme.key_window_background));
    }

    @Override
    public void bindView(DiscoveryItem item) {
        img0.setImageDrawable(null);
        if (item.discoveryFields == null || item.discoveryFields.size() < 1)
            return;
        loadImage(img0, item.discoveryFields.get(0).imageUrl);

        card0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDiscoveryFieldsClick(item.discoveryFields.get(0));
            }
        });
    }
}
