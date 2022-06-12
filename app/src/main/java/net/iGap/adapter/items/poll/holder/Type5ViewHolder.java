package net.iGap.adapter.items.poll.holder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import net.iGap.R;
import net.iGap.adapter.items.poll.PollAdapter;
import net.iGap.adapter.items.poll.PollItem;
import net.iGap.messenger.theme.Theme;

public class Type5ViewHolder extends BaseViewHolder {
    private ImageView img0, img1, img2;
    private CardView card0, card1, card2;
    private View tick0, tick1, tick2;

    public Type5ViewHolder(PollAdapter pollAdapter, @NonNull View itemView) {
        super(pollAdapter, itemView);
        img0 = itemView.findViewById(R.id.type5_img0);
        img1 = itemView.findViewById(R.id.type5_img1);
        img2 = itemView.findViewById(R.id.type5_img2);
        card0 = itemView.findViewById(R.id.type5_card0);
        card1 = itemView.findViewById(R.id.type5_card1);
        card2 = itemView.findViewById(R.id.type5_card2);

        tick0 = itemView.findViewById(R.id.type5_tick0);
        tick0.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        tick1 = itemView.findViewById(R.id.type5_tick1);
        tick1.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        tick2 = itemView.findViewById(R.id.type5_tick2);
        tick2.setBackgroundColor(Theme.getColor(Theme.key_window_background));
    }

    @Override
    public void bindView(PollItem item) {
        img0.setImageDrawable(null);
        img1.setImageDrawable(null);
        img2.setImageDrawable(null);
        if (item.pollItemFields == null || item.pollItemFields.size() < 3)
            return;
        loadImage(img0, item.pollItemFields.get(0).imageUrl);
        loadImage(img1, item.pollItemFields.get(1).imageUrl);
        loadImage(img2, item.pollItemFields.get(2).imageUrl);


        checkTickVisibility(item.pollItemFields.get(0), tick0);
        checkTickVisibility(item.pollItemFields.get(1), tick1);
        checkTickVisibility(item.pollItemFields.get(2), tick2);


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

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePollFieldsClick(item.pollItemFields.get(2));
            }
        });
    }
}
