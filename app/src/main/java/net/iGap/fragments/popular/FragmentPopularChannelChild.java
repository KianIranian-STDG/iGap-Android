package net.iGap.fragments.popular;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.iGap.G;
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
    private String id;
    private AdapterChannelInfoItem adapterChannel;
    private int page = 1;

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
                    Toast toast = Toast.makeText(getContext(), "scrollllinggg", Toast.LENGTH_SHORT);
                    toast.show();
                    page++;
                    setupViews();


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
                        if (page == 1) {
                            Slider slider = new Slider(G.context);
                            slider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            Slider.init(new ImageLoadingService(G.context));
                            slider.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MainSliderAdapter mainSliderAdapter = new MainSliderAdapter(G.context, response.body().getInfo().getAdvertisement().getSlides());
                                    slider.setInterval(1000);
                                    slider.setAdapter(mainSliderAdapter);
                                    slider.setSelectedSlide(0);
                                    slider.setLoopSlides(true);
                                    slider.setAnimateIndicators(true);
                                    slider.setIndicatorSize(12);
                                }
                            }, 0);
                            linearLayoutItemContainerChild.addView(slider);
                        }


                    RecyclerView categoryRecyclerViewChild = new RecyclerView(G.context);
                    categoryRecyclerViewChild.setLayoutManager(new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false));
                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams1.setMargins(0, 8, 0, 8);
                    categoryRecyclerViewChild.setLayoutParams(layoutParams1);
                    adapterChannel = new AdapterChannelInfoItem(getContext(), response.body().getChannels());
                    if (page == 1) {
                        adapterChannel.setChannelList(response.body().getChannels());
                    }
                    if (page > 1) {
                        adapterChannel.addChannelList(response.body().getChannels());
                    }

                    categoryRecyclerViewChild.setAdapter(adapterChannel);
                    linearLayoutItemContainerChild.addView(categoryRecyclerViewChild);
                }
                page = page + 1;

            }

            @Override
            public void onFailure(Call<ChildChannel> call, Throwable t) {
                Toast toast = Toast.makeText(getContext(), "No Response", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void setId(String id) {
        this.id = id;
    }

}
