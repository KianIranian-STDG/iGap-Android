package net.iGap.adapter.items;

import android.text.format.DateUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.module.TimeUtils;
import net.iGap.proto.ProtoGlobal;

import java.util.Calendar;

public class IVandActivityViewHolder extends RecyclerView.ViewHolder {
    private AppCompatTextView txt_subject;
    private AppCompatTextView txt_date;
    private AppCompatTextView txt_score;
    private AppCompatTextView image;

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
            image.setText(R.string.icon_upload);
            image.setTextColor(ContextCompat.getColor(image.getContext(), R.color.green));
        } else if (item.getScore() < 0) {
            image.setText(R.string.icon_download);
            image.setTextColor(ContextCompat.getColor(image.getContext(), R.color.red));
        } else {
            image.setText(R.string.icon_delete_minus);
            image.setTextColor(ContextCompat.getColor(image.getContext(), R.color.gray));
        }
    }
}
