package net.iGap.adapter.items.discovery.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;

public class TypeUnknownViewHolder extends BaseViewHolder {
    private TextView txt0;
    private CardView card0;

    public TypeUnknownViewHolder(@NonNull View itemView) {
        super(itemView);
        txt0 = itemView.findViewById(R.id.unknown_text);
        card0 = itemView.findViewById(R.id.unknown_card);
    }

    @Override
    public void bindView(DiscoveryItem item) {
    }
}
