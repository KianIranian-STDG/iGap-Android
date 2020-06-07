package net.iGap.adapter.items.discovery.holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.api.WeatherApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.fragments.FragmentUserScore;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.realm.RealmUserInfo;

public class Type0ViewHolder extends BaseViewHolder implements HandShakeCallback {
    private WeatherApi weatherApi;

    public AppCompatTextView tvCityName, tvDegree, tvDate, tvScore;
    public AppCompatImageView ivWeather;
    private RealmUserInfo userInfo;
    private ConstraintLayout frameScore;

    public Type0ViewHolder(@NonNull View itemView, FragmentActivity activity) {
        super(itemView, activity);
     /*   ivWeather = itemView.findViewById(R.id.iv_weather);
        tvCityName = itemView.findViewById(R.id.tv_cityName);
        tvDegree = itemView.findViewById(R.id.tv_degree);*/
        tvDate = itemView.findViewById(R.id.tv_date);
        tvScore = itemView.findViewById(R.id.tv_score);
        frameScore = itemView.findViewById(R.id.frame_score);

        weatherApi = new RetrofitFactory().getWeatherRetrofit();

        DbManager.getInstance().doRealmTask(realm -> {
            userInfo = realm.where(RealmUserInfo.class).findFirst();
        });

 /*
     weatherApi.getCities().enqueue(new Callback<List<Cities>>() {
            @Override
            public void onResponse(Call<List<Cities>> call, Response<List<Cities>> response) {
                tvCityName.setText(response.body().get(1).getTitle());
            }

            @Override
            public void onFailure(Call<List<Cities>> call, Throwable t) {
                HelperError.showSnackMessage("onFailure", false);
            }
        });


        weatherApi.getWeatherInfo().enqueue(new Callback<WeatherInfo>() {
            @Override
            public void onResponse(Call<WeatherInfo> call, Response<WeatherInfo> response) {
                tvDate.setText(response.body().getDaily().get(0).getDateTime());
                tvDegree.setText(response.body().getDaily().get(0).getTemperature().getDay().toString());
                loadImage(ivWeather, response.body().getDaily().get(0).getIcon());
            }

            @Override
            public void onFailure(Call<WeatherInfo> call, Throwable t) {
                HelperError.showSnackMessage("onFailure", false);
            }
        });*/
    }

    @Override
    public void bindView(DiscoveryItem item) {
        tvScore.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(userInfo.getIvandScore())) : String.valueOf(userInfo.getIvandScore()));
        frameScore.setOnClickListener(view -> new HelperFragment(G.currentActivity.getSupportFragmentManager(), new FragmentUserScore()).setReplace(false).load());
    }
}
