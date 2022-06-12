package net.iGap.adapter.items.poll.holder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import net.iGap.R;
import net.iGap.adapter.items.poll.PollAdapter;
import net.iGap.adapter.items.poll.PollItem;
import net.iGap.messenger.theme.Theme;

public class Type1ViewHolder extends BaseViewHolder {
    private ImageView img0;
    private CardView card0;
    private View tick0;

    public Type1ViewHolder(PollAdapter pollAdapter, @NonNull View itemView) {
        super(pollAdapter, itemView);
        img0 = itemView.findViewById(R.id.type1_img0);
        card0 = itemView.findViewById(R.id.type1_card0);
        tick0 = itemView.findViewById(R.id.type1_tick0);
        tick0.setBackgroundColor(Theme.getColor(Theme.key_window_background));
    }

    @Override
    public void bindView(PollItem item) {
        img0.setImageDrawable(null);
        if (item.pollItemFields == null || item.pollItemFields.size() < 1)
            return;

        loadImage(img0, item.pollItemFields.get(0).imageUrl);

        checkTickVisibility(item.pollItemFields.get(0), tick0);

        card0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePollFieldsClick(item.pollItemFields.get(0));
            }
        });
    }
}
