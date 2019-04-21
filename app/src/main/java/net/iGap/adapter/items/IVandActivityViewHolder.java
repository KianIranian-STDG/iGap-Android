package net.iGap.adapter.items;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.module.CustomTextViewMedium;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.TimeUtils;
import net.iGap.proto.ProtoGlobal;

import java.util.Calendar;

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
        txt_subject.setTextColor(Color.parseColor(G.textBubble));
        txt_score.setTextColor(Color.parseColor(G.textBubble));
        txt_date.setTextColor(Color.parseColor(G.textBubble));

        txt_subject.setText(item.getTitle());
        txt_score.setText(String.valueOf(Math.abs(item.getScore())));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(item.getTime() * DateUtils.SECOND_IN_MILLIS);

        txt_date.setText(TimeUtils.getDate(calendar) + "\n" + TimeUtils.toLocal(item.getTime() * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME));

        if (HelperCalander.isPersianUnicode) {
            txt_date.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_date.getText().toString()));
        }

        if (HelperCalander.isPersianUnicode) {
            txt_score.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_score.getText().toString()));
        }

        if (item.getScore() > 0) {
            image.setText(G.context.getString(R.string.md_igap_arrow_up_thick));
            image.setTextColor(G.context.getResources().getColor(R.color.green));
        } else if (item.getScore() < 0) {
            image.setText(G.context.getString(R.string.md_igap_arrow_down_thick));
            image.setTextColor(G.context.getResources().getColor(R.color.red));
        } else {
            image.setText(G.context.getString(R.string.md_igap_minus));
            image.setTextColor(G.context.getResources().getColor(R.color.gray));
        }
    }
}
