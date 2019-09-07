package net.iGap.adapter.items.discovery.holder;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;

public class TypeUnknownViewHolder extends BaseViewHolder {
    private TextView txt0;
    private CardView card0;

    public TypeUnknownViewHolder(@NonNull View itemView, FragmentActivity activity) {
        super(itemView,activity);
        txt0 = itemView.findViewById(R.id.unknown_text);
        card0 = itemView.findViewById(R.id.unknown_card);
        card0.setCardBackgroundColor(G.getThemeBackgroundColor());
    }

    @Override
    public void bindView(DiscoveryItem item) {
    }
}
