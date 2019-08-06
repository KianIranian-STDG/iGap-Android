package net.iGap.fragments.favoritechannel;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.favoritechannel.CategoryItemAdapter;
import net.iGap.adapter.items.favoritechannel.ChannelItemAdapter;
import net.iGap.adapter.items.favoritechannel.SliderAdapter;
import net.iGap.api.FavoriteChannelApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.beepTunes.main.SliderBannerImageLoadingService;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.model.FavoriteChannel.Channel;
import net.iGap.model.FavoriteChannel.ParentChannel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FavoriteChannelFragment extends BaseFragment implements ToolbarListener {
    private HelperToolbar toolbar;
    private FavoriteChannelApi api;
    private View rootView;
    private ChannelItemAdapter channelItemAdapter;
    private SliderAdapter sliderAdapter;
    private int playBackTime;
    private String scale;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyRefresh;
    private LinearLayout linearLayoutItemContainer;
    private LinearLayout linearToolbar;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        rootView = LayoutInflater.from(G.fragmentActivity).inflate(R.layout.fragment_favorite_channel, container, false);
        linearToolbar = rootView.findViewById(R.id.ll_popular_parent_toolbar);
        swipeRefreshLayout = rootView.findViewById(R.id.refresh_channel);

        swipeRefreshLayout.setRefreshing(true);
        linearLayoutItemContainer = rootView.findViewById(R.id.rl_fragmentContainer);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            linearLayoutItemContainer.removeAllViews();
            sendChannelRequest();
        });

        emptyRefresh = rootView.findViewById(R.id.empty_iv);
        emptyRefresh.setOnClickListener(v -> {
            swipeRefreshLayout.setRefreshing(true);
            emptyRefresh.setVisibility(View.GONE);
            sendChannelRequest();
        });

        toolbar = HelperToolbar.create()
                .setContext(G.fragmentActivity)
                .setListener(this)
                .setLogoShown(true)
                .setDefaultTitle("کانال های پرمخاطب")
//                .setSearchBoxShown(true)
                .setLeftIcon(R.string.back_icon);
        if (G.selectedLanguage.equals("en")) {
            toolbar.setDefaultTitle("Favorite Channel");
        }
        linearToolbar.addView(toolbar.getView());

        api = ApiServiceProvider.getChannelApi();
        sendChannelRequest();
        return rootView;
    }

    public void sendChannelRequest() {
        api.getParentChannel().enqueue(new Callback<ParentChannel>() {
            @Override
            public void onResponse(Call<ParentChannel> call, Response<ParentChannel> response) {
                emptyRefresh.setVisibility(View.INVISIBLE);
                if (response.body().getData()!=null) {
                    swipeRefreshLayout.setRefreshing(false);
                    for (int i = 0; i < response.body().getData().size(); i++) {
                        switch (response.body().getData().get(i).getType()) {
                            case ParentChannel.TYPE_SLIDE:
                                if (response.body().getData().get(i).getInfo() != null && response.body().getData().get(i).getSlides() != null) {
                                    BannerSlider.init(new SliderBannerImageLoadingService());
                                    BannerSlider slider = new BannerSlider(G.fragmentActivity);
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    CardView.LayoutParams cardParamse = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    cardParamse.setMargins(Utils.dpToPx(4), Utils.dpToPx(4), Utils.dpToPx(4), Utils.dpToPx(4));
                                    CardView cardView = new CardView(G.fragmentActivity);
                                    cardView.setRadius(Utils.dpToPx(12));
                                    cardView.setPreventCornerOverlap(false);
                                    cardView.setUseCompatPadding(false);
                                    cardView.setCardElevation(0);
                                    cardView.setLayoutParams(cardParamse);
                                    scale = response.body().getData().get(i).getInfo().getScale();
                                    ProgressBar progressBar = new ProgressBar(G.fragmentActivity);
                                    ProgressBar.inflate(G.fragmentActivity, R.layout.progress_favorite_channel, slider);
                                    progressBar.setVisibility(View.VISIBLE);
                                    String[] scales = scale.split(":");
                                    float height = Resources.getSystem().getDisplayMetrics().widthPixels * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
                                    slider.setIndex(i);
                                    slider.setLayoutParams(layoutParams);
                                    slider.getLayoutParams().height = Math.round(height);
                                    cardView.addView(slider);

                                    int finalI = i;
                                    playBackTime = response.body().getData().get(i).getInfo().getPlaybackTime();
                                    sliderAdapter = new SliderAdapter(response.body().getData().get(i).getSlides(), response.body().getData().get(i).getInfo().getScale());

                                    slider.postDelayed(() -> {
                                        sliderAdapter = new SliderAdapter(response.body().getData().get(finalI).getSlides(), response.body().getData().get(finalI).getInfo().getScale());
                                        slider.setAdapter(sliderAdapter);
                                        slider.setSelectedSlide(0);
                                        slider.setLoopSlides(true);
                                        slider.setAnimateIndicators(true);
                                        slider.setIndicatorSize(12);
                                        slider.setInterval(playBackTime);
                                        slider.setOnSlideClickListener(position -> {
                                            if (response.body().getData().get(slider.getIndex()).getSlides().get(position).getActionType() == 3) {
                                                HelperUrl.checkUsernameAndGoToRoom(getActivity(), response.body().getData().get(slider.getIndex()).getSlides().get(position).getmActionLink(), HelperUrl.ChatEntry.chat);
                                            } else {
                                                Toast.makeText(G.fragmentActivity, "Empty", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }, 1000);
                                    linearLayoutItemContainer.addView(cardView);
                                }
                                break;
                            case ParentChannel.TYPE_CHANNEL:
                                if (response.body().getData().get(i).getChannels() != null) {
                                    View channelView = LayoutInflater.from(G.fragmentActivity).inflate(R.layout.item_favorite_channel_channelcountainer, null);
                                    RelativeLayout relativeLayoutRow = channelView.findViewById(R.id.rl_item_pop_rows);
                                    LinearLayout linearLayoutRow = channelView.findViewById(R.id.ll_item_pop_rows);
                                    ImageView imageViewMore = channelView.findViewById(R.id.iv_item_popular_more);
                                    if (G.isDarkTheme) {
                                        relativeLayoutRow.setBackground(getResources().getDrawable(R.drawable.shape_favorite_channel_all_them));
                                        linearLayoutRow.setBackground(getResources().getDrawable(R.drawable.shape_favorite_channel_dark_them));
                                        imageViewMore.setColorFilter(getResources().getColor(R.color.navigation_dark_mode_bg));
                                    }
                                    FrameLayout frameLayout = channelView.findViewById(R.id.frame_more_one);
                                    int finalId = i;
                                    frameLayout.setOnClickListener(v -> {
                                        FavoriteChannelInfoFragment favoriteChannelInfoFragment = new FavoriteChannelInfoFragment();
                                        favoriteChannelInfoFragment.setId(response.body().getData().get(finalId).getId());
                                        FragmentTransaction fragmentTransition = getFragmentManager().beginTransaction();
                                        fragmentTransition.replace(R.id.frame_fragment_container, favoriteChannelInfoFragment);
                                        fragmentTransition.addToBackStack(null);
                                        fragmentTransition.commit();
                                    });
                                    TextView textViewTitle = channelView.findViewById(R.id.tv_item_popular_title);
                                    if (G.selectedLanguage.equals("fa"))
                                        textViewTitle.setText(response.body().getData().get(i).getInfo().getTitle());
                                    if (G.selectedLanguage.equals("en"))
                                        textViewTitle.setText(response.body().getData().get(i).getInfo().getTitleEn());
                                    RecyclerView channelsRecyclerView = channelView.findViewById(R.id.rv_item_popular_row);
                                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    layoutParams1.setMargins(Utils.dpToPx(4), Utils.dpToPx(4), Utils.dpToPx(4), Utils.dpToPx(4));
                                    channelView.setLayoutParams(layoutParams1);
                                    channelsRecyclerView.setLayoutManager(new LinearLayoutManager(G.fragmentActivity, RecyclerView.HORIZONTAL, false));
                                    channelItemAdapter = new ChannelItemAdapter(response.body().getData().get(i).getChannels());
                                    channelsRecyclerView.setAdapter(channelItemAdapter);
                                    channelItemAdapter.setOnClickedChannelEventCallBack(channel -> {
                                        if (channel.getmType().equals(Channel.TYPE_PRIVATE))
                                            HelperUrl.checkAndJoinToRoom(getActivity(), channel.getSlug());
                                        if (channel.getmType().equals(Channel.TYPE_PUBLIC))
                                            HelperUrl.checkUsernameAndGoToRoom(getActivity(), channel.getSlug(), HelperUrl.ChatEntry.chat);
                                    });
                                    linearLayoutItemContainer.addView(channelView);
                                }
                                break;
                            case ParentChannel.TYPE_CATEGORY:
                                if (response.body().getData().get(i).getCategories() != null) {
                                    RecyclerView categoryRecyclerView = new RecyclerView(G.fragmentActivity);
                                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    layoutParams2.setMargins(Utils.dpToPx(4), Utils.dpToPx(4), Utils.dpToPx(4), Utils.dpToPx(4));
                                    categoryRecyclerView.setLayoutManager(new GridLayoutManager(G.fragmentActivity, 4, LinearLayoutManager.VERTICAL, false));
                                    categoryRecyclerView.setLayoutParams(layoutParams2);
                                    CategoryItemAdapter gridItem = new CategoryItemAdapter(true, response.body().getData().get(i).getCategories());
                                    gridItem.setOnClickedItemEventCallBack(category -> {
                                        FavoriteChannelInfoFragment favoriteChannelInfoFragment = new FavoriteChannelInfoFragment();
                                        favoriteChannelInfoFragment.setId(category.getId());
                                        FragmentTransaction fragmentTransition = getFragmentManager().beginTransaction();
                                        fragmentTransition.replace(R.id.frame_fragment_container, favoriteChannelInfoFragment);
                                        fragmentTransition.addToBackStack(null);
                                        fragmentTransition.commit();
                                    });
                                    categoryRecyclerView.setAdapter(gridItem);
                                    categoryRecyclerView.setNestedScrollingEnabled(false);
                                    linearLayoutItemContainer.addView(categoryRecyclerView);
                                }
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ParentChannel> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                emptyRefresh.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onLeftIconClickListener(View view) {
        getActivity().onBackPressed();
    }
//    @Override
//    public void onSearchClickListener(View view) {
//
//    }
//
//    @Override
//    public void onBtnClearSearchClickListener(View view) {
//
//    }
//
//    @Override
//    public void onSearchTextChangeListener(View view, String text) {
//
//    }
}
