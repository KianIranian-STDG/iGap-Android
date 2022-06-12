package net.iGap.adapter.items.discovery.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.messenger.theme.Theme;

public class TypeUnknownViewHolder extends BaseViewHolder {
    private TextView txt0;
    private CardView card0;

    public TypeUnknownViewHolder(@NonNull View itemView, FragmentActivity activity) {
        super(itemView, activity);
        txt0 = itemView.findViewById(R.id.unknown_text);
        card0 = itemView.findViewById(R.id.unknown_card);
        card0.setCardBackgroundColor(Theme.getColor(Theme.key_window_background));
    }

    @Override
    public void bindView(DiscoveryItem item) {
    }
}
