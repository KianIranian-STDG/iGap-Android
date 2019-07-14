package net.iGap.fragments.popular;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.adapter.items.popular.AdapterChannelInfoItem;
import net.iGap.adapter.items.popular.AdapterSliderItem;
import net.iGap.api.PopularChannelApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.fragments.BaseFragment;
import net.iGap.model.PopularChannel.ChildChannel;

import life.knowledge4.videotrimmer.view.ProgressBarView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentPopularChannelChild extends BaseFragment {
    private PopularChannelApi popularChannelApi;
    private int page = 1;
    private String id;
    private ProgressBarView progressBarView;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_popular_channel_child, container, false);
        progressBarView = view.findViewById(R.id.progress_frag_popular);
        popularChannelApi = ApiServiceProvider.getChannelApi();
        popularChannelApi.getChildChannel(id, page).enqueue(new Callback<ChildChannel>() {
            @Override
            public void onResponse(Call<ChildChannel> call, Response<ChildChannel> response) {
                LinearLayout linearLayoutItemContainerChild = view.findViewById(R.id.ll_container_child);
                if (response.body().getInfo().getHasAd() == true) {
                    RecyclerView sliderRecyclerViewChild = new RecyclerView(getContext());
                    AdapterSliderItem slideAdapter = new AdapterSliderItem(getContext(), false, response.body().getInfo().getAdvertisement().getSlides());
                    sliderRecyclerViewChild.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 8, 0, 8);
                    sliderRecyclerViewChild.setLayoutParams(layoutParams);
                    PagerSnapHelper snapHelper = new PagerSnapHelper();
                    snapHelper.attachToRecyclerView(sliderRecyclerViewChild);
                    sliderRecyclerViewChild.setAdapter(slideAdapter);
                    linearLayoutItemContainerChild.addView(sliderRecyclerViewChild);
                }

                RecyclerView categoryRecyclerViewChild = new RecyclerView(getContext());
                categoryRecyclerViewChild.setLayoutManager(new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false));
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams1.setMargins(0, 8, 0, 8);
                categoryRecyclerViewChild.setLayoutParams(layoutParams1);
                AdapterChannelInfoItem gridItem = new AdapterChannelInfoItem(getContext(), response.body().getChannels());
                categoryRecyclerViewChild.setAdapter(gridItem);
                linearLayoutItemContainerChild.addView(categoryRecyclerViewChild);


            }

            @Override
            public void onFailure(Call<ChildChannel> call, Throwable t) {
            }
        });
        return view;

    }


    public void setId(String id) {
        this.id = id;
    }

}
