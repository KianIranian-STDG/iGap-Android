package net.iGap.fragments.popular;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentPopularChannelChild extends BaseFragment {
    private PopularChannelApi popularChannelApi;
    private int page = 1;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_popular_channel_child, container, false);

        popularChannelApi = ApiServiceProvider.getChannelApi();

        popularChannelApi.getChildChannel("5d1cc0bc072e82477b6a957c",page ).enqueue(new Callback<ChildChannel>() {
            @Override
            public void onResponse(Call<ChildChannel> call, Response<ChildChannel> response) {
                Log.i("nazanin", "onResponse: " + response.isSuccessful());

                LinearLayout linearLayoutItemContainerChild = view.findViewById(R.id.ll_container_child);

                RecyclerView sliderRecyclerViewChild = new RecyclerView(getContext());
                sliderRecyclerViewChild.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                sliderRecyclerViewChild.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                PagerSnapHelper snapHelper = new PagerSnapHelper();
                snapHelper.attachToRecyclerView(sliderRecyclerViewChild);
                sliderRecyclerViewChild.setAdapter(new AdapterSliderItem(getContext(), false, response.body().getInfo().getAdvertisement().getSlides()));
                linearLayoutItemContainerChild.addView(sliderRecyclerViewChild);

                RecyclerView categoryRecyclerViewChild = new RecyclerView(getContext());
                categoryRecyclerViewChild.setLayoutManager(new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false));
                categoryRecyclerViewChild.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                AdapterChannelInfoItem gridItem = new AdapterChannelInfoItem(getContext(), response.body().getChannels());
                categoryRecyclerViewChild.setAdapter(gridItem);
                linearLayoutItemContainerChild.addView(categoryRecyclerViewChild);

            }

            @Override
            public void onFailure(Call<ChildChannel> call, Throwable t) {
                Log.i("nazanin", "onFailure: " + t.getMessage());
            }
        });
        return view;

    }


}
