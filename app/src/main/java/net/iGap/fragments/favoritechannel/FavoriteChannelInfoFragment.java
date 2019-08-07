package net.iGap.fragments.favoritechannel;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.favoritechannel.ChannelInfoAdapter;
import net.iGap.adapter.items.favoritechannel.ImageLoadingService;
import net.iGap.adapter.items.favoritechannel.SliderAdapter;
import net.iGap.api.FavoriteChannelApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperUrl;
import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.model.FavoriteChannel.Channel;
import net.iGap.model.FavoriteChannel.ChildChannel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FavoriteChannelInfoFragment extends BaseFragment {
    private View view;
    private LinearLayout itemContainer;
    private FavoriteChannelApi favoriteChannelApi;
    private SwipeRefreshLayout swipeRefreshLayout;


    private String id;
    private ChannelInfoAdapter adapterChannel;
    private SliderAdapter sliderAdapter;
    private int page = 1;
    private long totalPage;
    private int playBackTime;
    private String scale;
    RecyclerView categoryRecyclerViewChild = new RecyclerView(G.fragmentActivity);
    CardView cardView = new CardView(G.fragmentActivity);
    private TextView emptyImage;
    private NestedScrollView nestedScrollView;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        view = LayoutInflater.from(G.fragmentActivity).inflate(R.layout.fragment_favorite_channel_info, container, false);
        itemContainer = view.findViewById(R.id.ll_container_child);
        nestedScrollView = view.findViewById(R.id.scroll_channel);
        emptyImage = view.findViewById(R.id.empty_iv_info);
        swipeRefreshLayout = view.findViewById(R.id.refresh_channelInfo);

        favoriteChannelApi = ApiServiceProvider.getChannelApi();
        setupViews();
        sendChannelRequest();
        return view;
    }

    public void setupViews() {
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (nestedScrollView1, i, i1, i2, i3) -> {
            if (totalPage >= page)
                sendChannelRequest();
        });
        emptyImage.setOnClickListener(v -> {
            HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
            sendChannelRequest();
        });
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            itemContainer.removeAllViews();
            page = 1;
            sendChannelRequest();

        });

    }

    private void sendChannelRequest() {
        favoriteChannelApi.getChildChannel(id, page).enqueue(new Callback<ChildChannel>() {

            @Override
            public void onResponse(Call<ChildChannel> call, Response<ChildChannel> response) {
                totalPage = response.body().getPagination().getTotalPages();
                if (response.isSuccessful()) {
                    if (page == 1) {
                        swipeRefreshLayout.setRefreshing(false);
                        emptyImage.setVisibility(View.INVISIBLE);
                        if (response.body().getInfo().getAdvertisement() != null) {
                            sliderAdapter = new SliderAdapter(response.body().getInfo().getAdvertisement().getSlides(), response.body().getInfo().getAdvertisement().getmScale());
                            BannerSlider.init(new ImageLoadingService());
                            BannerSlider slider = new BannerSlider(G.fragmentActivity);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            CardView.LayoutParams params = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.setMargins(Utils.dpToPx(4), Utils.dpToPx(4), Utils.dpToPx(4), Utils.dpToPx(4));
                            cardView.setLayoutParams(params);
                            cardView.setRadius(Utils.dpToPx(12));
                            cardView.setPreventCornerOverlap(false);
                            scale = response.body().getInfo().getScale();
                            scale = response.body().getInfo().getAdvertisement().getmScale();
                            ProgressBar progressBar = new ProgressBar(G.fragmentActivity);
                            ProgressBar.inflate(G.fragmentActivity, R.layout.progress_favorite_channel, slider);
                            progressBar.setVisibility(View.VISIBLE);
                            String[] scales = scale.split(":");
                            float height = Resources.getSystem().getDisplayMetrics().widthPixels * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
                            slider.setLayoutParams(layoutParams);
                            slider.getLayoutParams().height = Math.round(height);
                            playBackTime = response.body().getInfo().getAdvertisement().getmPlaybackTime();
                            cardView.addView(slider);
                            slider.postDelayed(() -> {
                                slider.setAdapter(sliderAdapter);
                                slider.setSelectedSlide(0);
                                slider.setLoopSlides(true);
                                slider.setAnimateIndicators(true);
                                slider.setIndicatorSize(12);
                                slider.setInterval(playBackTime);
                                slider.setOnSlideClickListener(position -> {
                                    if (response.body().getInfo().getAdvertisement().getSlides().get(position).getActionType() == 0) {
                                        HelperUrl.checkUsernameAndGoToRoom(getActivity(), response.body().getInfo().getAdvertisement().getSlides().get(position).getmActionLink(), HelperUrl.ChatEntry.chat);
                                    }
                                });
                            }, 1000);

                            itemContainer.addView(cardView);
                        }

                        adapterChannel = new ChannelInfoAdapter();

                        categoryRecyclerViewChild.setLayoutManager(new GridLayoutManager(G.fragmentActivity, 4, RecyclerView.VERTICAL, false));
                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams1.setMargins(Utils.dpToPx(4), Utils.dpToPx(4), Utils.dpToPx(4), Utils.dpToPx(4));
                        categoryRecyclerViewChild.setLayoutParams(layoutParams1);
                        categoryRecyclerViewChild.setNestedScrollingEnabled(false);
                        categoryRecyclerViewChild.setAdapter(adapterChannel);
                        adapterChannel.setOnClickedChannelEventCallBack(channel -> {
                            if (channel.getmType().equals(Channel.TYPE_PRIVATE))
                                HelperUrl.checkAndJoinToRoom(getActivity(), channel.getSlug());
                            if (channel.getmType().equals(Channel.TYPE_PUBLIC))
                                HelperUrl.checkUsernameAndGoToRoom(getActivity(), channel.getSlug(), HelperUrl.ChatEntry.chat);
                        });
                        itemContainer.addView(categoryRecyclerViewChild);
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
                swipeRefreshLayout.setRefreshing(false);
                emptyImage.setVisibility(View.VISIBLE);
                HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
            }
        });
    }

    public void setId(String id) {
        this.id = id;
    }

}
