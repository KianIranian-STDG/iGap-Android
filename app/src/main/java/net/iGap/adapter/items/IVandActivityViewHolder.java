package net.iGap.adapter.items;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.proto.ProtoGlobal;

public class IVandActivityViewHolder extends RecyclerView.ViewHolder {
    private CustomTextViewMedium txt_subject;
    private CustomTextViewMedium txt_date;
    private CustomTextViewMedium txt_score;
    private MaterialDesignTextView image;

    public IVandActivityViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_subject = itemView.findViewById(R.id.txt_subject);
        txt_date = itemView.findViewById(R.id.txt_date);
        txt_score = itemView.findViewById(R.id.txt_score);
        image = itemView.findViewById(R.id.image);
    }

    public void bindView(ProtoGlobal.IVandActivity item) {
        txt_subject.setText(item.getTitle());
        txt_score.setText(String.valueOf(Math.abs(item.getScore())));
        txt_date.setText(String.valueOf(item.getTime() * 1000));

        if (item.getScore() > 0) {
            image.setText(G.context.getString(R.string.md_igap_arrow_up_thick));
            image.setTextColor(Color.parseColor(G.appBarColor));
        } else if (item.getScore() < 0) {
            image.setText(G.context.getString(R.string.md_igap_arrow_down_thick));
            image.setTextColor(Color.parseColor(G.notificationColor));
        } else {
            image.setText(G.context.getString(R.string.md_igap_minus));
            image.setTextColor(Color.parseColor(G.textTitleTheme));
        }
    }
}
