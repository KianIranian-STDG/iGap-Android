package net.iGap.adapter.items.poll.holder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import net.iGap.R;
import net.iGap.adapter.items.poll.PollAdapter;
import net.iGap.adapter.items.poll.PollItem;
import net.iGap.messenger.theme.Theme;

public class Type4ViewHolder extends BaseViewHolder {
    private ImageView img0, img1;
    private CardView card0, card1;
    private View tick0, tick1;

    public Type4ViewHolder(PollAdapter pollAdapter, @NonNull View itemView) {
        super(pollAdapter, itemView);
        img0 = itemView.findViewById(R.id.type4_img0);
        img1 = itemView.findViewById(R.id.type4_img1);
        card0 = itemView.findViewById(R.id.type4_card0);
        card1 = itemView.findViewById(R.id.type4_card1);

        tick0 = itemView.findViewById(R.id.type4_tick0);
        tick0.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        tick1 = itemView.findViewById(R.id.type4_tick1);
        tick1.setBackgroundColor(Theme.getColor(Theme.key_window_background));
    }

    @Override
    public void bindView(PollItem item) {
        img0.setImageDrawable(null);
        img1.setImageDrawable(null);
        if (item.pollItemFields == null || item.pollItemFields.size() < 2)
            return;
        loadImage(img0, item.pollItemFields.get(0).imageUrl);
        loadImage(img1, item.pollItemFields.get(1).imageUrl);

        checkTickVisibility(item.pollItemFields.get(0), tick0);
        checkTickVisibility(item.pollItemFields.get(1), tick1);

        card0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePollFieldsClick(item.pollItemFields.get(0));
            }
        });

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePollFieldsClick(item.pollItemFields.get(1));
            }
        });

    }
}
