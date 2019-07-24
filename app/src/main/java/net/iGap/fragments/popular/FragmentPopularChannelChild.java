package net.iGap.fragments.popular;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.iGap.R;
import net.iGap.adapter.items.popular.AdapterChannelInfoItem;
import net.iGap.adapter.items.popular.ImageLoadingService;
import net.iGap.adapter.items.popular.MainSliderAdapter;
import net.iGap.api.PopularChannelApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperUrl;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.model.PopularChannel.Channel;
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
    private MainSliderAdapter mainSliderAdapter;
    private int page = 1;
    private long totalPage;
    private int playBackTime;
    private String scale;

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
                    if (totalPage >= page)
                        setupViews();
                }
            }
        });
    }

    private void setupViews() {
        popularChannelApi.getChildChannel(id, page).enqueue(new Callback<ChildChannel>() {

            @Override
            public void onResponse(Call<ChildChannel> call, Response<ChildChannel> response) {
                LinearLayout linearLayoutItemContainerChild = view.findViewById(R.id.ll_container_child);
                totalPage = response.body().getPagination().getTotalPages();
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);

                    if (page == 1) {
                        if (response.body().getInfo().getAdvertisement() != null) {
                            mainSliderAdapter = new MainSliderAdapter(response.body().getInfo().getAdvertisement().getSlides(), response.body().getInfo().getAdvertisement().getmScale());
                            Slider.init(new ImageLoadingService());
                            Slider slider = new Slider(getContext());
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            scale = response.body().getInfo().getScale();
                            scale = response.body().getInfo().getAdvertisement().getmScale();
                            layoutParams.setMargins(0, Utils.pxToDp(8), 0, Utils.pxToDp(8));
                            ProgressBar progressBar = new ProgressBar(getContext());
                            ProgressBar.inflate(getContext(), R.layout.progress_favorite_channel, slider);
                            progressBar.setVisibility(View.VISIBLE);
                            String[] scales = scale.split(":");
                            float height = Resources.getSystem().getDisplayMetrics().widthPixels * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
                            slider.setLayoutParams(layoutParams);
                            slider.getLayoutParams().height = Math.round(height);
                            playBackTime = response.body().getInfo().getAdvertisement().getmPlaybackTime();
                            slider.postDelayed(() -> {
                                slider.setAdapter(mainSliderAdapter);
                                slider.setSelectedSlide(0);
                                slider.setLoopSlides(true);
                                slider.setAnimateIndicators(true);
                                slider.setIndicatorSize(12);
                                slider.setInterval(playBackTime);
                                slider.setOnSlideClickListener(position -> {
                                    if (response.body().getInfo().getAdvertisement().getSlides().get(position).getActionType()== 0) {
                                        HelperUrl.checkUsernameAndGoToRoom(getActivity(), response.body().getInfo().getAdvertisement().getSlides().get(position).getmActionLink(), HelperUrl.ChatEntry.chat);
                                    } else {
                                        Toast.makeText(getContext(), "nnnnnn", Toast.LENGTH_SHORT).show();
                                    }

                                });
                            }, 1000);
                            linearLayoutItemContainerChild.addView(slider);
                        }

                        adapterChannel = new AdapterChannelInfoItem(getContext());

                        RecyclerView categoryRecyclerViewChild = new RecyclerView(getContext());
                        categoryRecyclerViewChild.setLayoutManager(new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false));
                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        categoryRecyclerViewChild.setLayoutParams(layoutParams1);
                        categoryRecyclerViewChild.setAdapter(adapterChannel);
                        adapterChannel.setOnClickedChannelEventCallBack(new AdapterChannelInfoItem.OnClickedChannelInfoEventCallBack() {

                            @Override
                            public void onClickChannelInfo(Channel channel) {
                                if (channel.getmType().equals(Channel.TYPE_PRIVATE))
                                    HelperUrl.checkAndJoinToRoom(getActivity(), channel.getSlug());
                                if (channel.getmType().equals(Channel.TYPE_PUBLIC))
                                    HelperUrl.checkUsernameAndGoToRoom(getActivity(), channel.getSlug(), HelperUrl.ChatEntry.chat);
                            }
                        });
                        linearLayoutItemContainerChild.addView(categoryRecyclerViewChild);
                        if (response.body().getInfo().getAdvertisement() == null && response.body().getChannels() == null)
                            Toast.makeText(getContext(), "This Page Is Empty", Toast.LENGTH_SHORT).show();
                    }
                    if (page == 1) {
                        adapterChannel.setChannelList(response.body().getChannels());
                    }
                    if (page > 1) {
                        adapterChannel.addChannelList(response.body().getChannels());
                    }
                }
                page = page + 1;
            }

            @Override
            public void onFailure(Call<ChildChannel> call, Throwable t) {
                Log.i("nazanin", "onFailure: " + t.getMessage());
                Toast toast = Toast.makeText(getContext(), "No Response", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void setId(String id) {
        this.id = id;
    }

}
