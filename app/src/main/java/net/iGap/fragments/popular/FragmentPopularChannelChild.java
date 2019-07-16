package net.iGap.fragments.popular;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.iGap.R;
import net.iGap.adapter.items.popular.AdapterChannelInfoItem;
import net.iGap.adapter.items.popular.ImageLoadingService;
import net.iGap.adapter.items.popular.MainSliderAdapter;
import net.iGap.api.PopularChannelApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.fragments.BaseFragment;
import net.iGap.model.PopularChannel.ChildChannel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;


public class FragmentPopularChannelChild extends BaseFragment {
    private PopularChannelApi popularChannelApi;
    private ProgressBar progressBar;
    private View view;
    private int page = 1;
    private String id;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_popular_channel_child, container, false);
        progressBar = view.findViewById(R.id.progress_popular);
        popularChannelApi = ApiServiceProvider.getChannelApi();
        setupViews();
        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NestedScrollView scrollView = view.findViewById(R.id.scroll_channel);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    setupViews();
                    Log.i("nazanin", "onScrollChange: ");
                }
            }
        });

    }


    private void setupViews() {
        popularChannelApi.getChildChannel(id, page).enqueue(new Callback<ChildChannel>() {
            @Override
            public void onResponse(Call<ChildChannel> call, Response<ChildChannel> response) {
                progressBar.setVisibility(View.GONE);
                LinearLayout linearLayoutItemContainerChild = view.findViewById(R.id.ll_container_child);
                if (response.isSuccessful()) {
                    if (response.body().getInfo().getHasAd()) {
                        Slider slider = new Slider(getContext());
                        slider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        Slider.init(new ImageLoadingService(getContext()));
                        slider.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MainSliderAdapter mainSliderAdapter = new MainSliderAdapter(getContext(), response.body().getInfo().getAdvertisement().getSlides());
                                slider.setInterval(3000);
                                slider.setAdapter(mainSliderAdapter);
                                slider.setSelectedSlide(0);
                                slider.setLoopSlides(true);
                                slider.setAnimateIndicators(true);
                                slider.setIndicatorSize(12);
                            }
                        }, 0);
                        linearLayoutItemContainerChild.addView(slider);
                    }

                }

                RecyclerView categoryRecyclerViewChild = new RecyclerView(getContext());
                categoryRecyclerViewChild.setLayoutManager(new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false));
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams1.setMargins(0, 8, 0, 8);
                categoryRecyclerViewChild.setLayoutParams(layoutParams1);
                AdapterChannelInfoItem adapter = new AdapterChannelInfoItem(getContext());
                if (page == 1) {
                    adapter.setChannelList(response.body().getChannels());
                }
                if (page > 1) {
                    adapter.addChannelList(response.body().getChannels());
                }

                categoryRecyclerViewChild.setAdapter(adapter);
                linearLayoutItemContainerChild.addView(categoryRecyclerViewChild);

                page = page + 1;
            }

            @Override
            public void onFailure(Call<ChildChannel> call, Throwable t) {
            }
        });
    }

    public void setId(String id) {
        this.id = id;
    }

}
