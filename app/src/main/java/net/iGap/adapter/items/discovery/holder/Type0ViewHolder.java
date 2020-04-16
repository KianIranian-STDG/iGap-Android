package net.iGap.adapter.items.discovery.holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;

import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;

public class Type0ViewHolder extends BaseViewHolder {

    public AppCompatTextView ivWeather, tvCityName, tvDegree, tvDate, tvScore;

    public Type0ViewHolder(@NonNull View itemView, FragmentActivity activity) {
        super(itemView, activity);
        ivWeather = itemView.findViewById(R.id.iv_weather);
        tvCityName = itemView.findViewById(R.id.tv_cityName);
        tvDegree = itemView.findViewById(R.id.tv_degree);
        tvDate = itemView.findViewById(R.id.tv_date);
        tvScore = itemView.findViewById(R.id.tv_score);
    }

    @Override
    public void bindView(DiscoveryItem item) {

    }
}
