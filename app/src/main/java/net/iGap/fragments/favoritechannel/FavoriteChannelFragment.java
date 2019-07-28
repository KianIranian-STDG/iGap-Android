package net.iGap.fragments.favoritechannel;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.SVBar;

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
import net.iGap.model.PopularChannel.Category;
import net.iGap.model.PopularChannel.Channel;
import net.iGap.model.PopularChannel.ParentChannel;

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

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_favorite_channel, container, false);
        api = ApiServiceProvider.getChannelApi();
        LinearLayout toolbarContainer = rootView.findViewById(R.id.ll_popular_parent_toolbar);

        toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setListener(this)
                .setLogoShown(true)
                .setDefaultTitle("کانال های پرمخاطب")
//                .setSearchBoxShown(true)
                .setLeftIcon(R.string.back_icon);
        if (G.selectedLanguage.equals("en")) {
            toolbar.setDefaultTitle("Favorite Channel");

        }

        toolbarContainer.addView(toolbar.getView());
        api.getParentChannel().enqueue(new Callback<ParentChannel>() {
            @Override
            public void onResponse(Call<ParentChannel> call, Response<ParentChannel> response) {
                Log.i("nazanin", "onResponse: " + response.isSuccessful());
                LinearLayout linearLayoutItemContainer = rootView.findViewById(R.id.rl_fragmentContainer);
                for (int i = 0; i < response.body().getData().size(); i++) {
                    switch (response.body().getData().get(i).getType()) {
                        case ParentChannel.TYPE_SLIDE:
                            BannerSlider.init(new SliderBannerImageLoadingService());
                            BannerSlider slider = new BannerSlider(getContext());
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            CardView cardView = new CardView(getContext());
                            cardView.setRadius(Utils.dpToPx(12));
                            cardView.setPreventCornerOverlap(false);
                            cardView.setUseCompatPadding(false);
                            cardView.setCardElevation(0);
                            CardView.LayoutParams cardParamse = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            cardParamse.setMargins(Utils.dpToPx(4), Utils.dpToPx(4), Utils.dpToPx(4), Utils.dpToPx(4));
                            cardView.setLayoutParams(cardParamse);
                            scale = response.body().getData().get(i).getInfo().getScale();
                            ProgressBar progressBar = new ProgressBar(getContext());
                            ProgressBar.inflate(getContext(), R.layout.progress_favorite_channel, slider);
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
                                        Log.i("nazanin", "onResponse: " + position);
                                        HelperUrl.checkUsernameAndGoToRoom(getActivity(), response.body().getData().get(slider.getIndex()).getSlides().get(position).getmActionLink(), HelperUrl.ChatEntry.chat);
                                    } else {
                                        Log.i("nazanin", "onResponse: " + position);
                                        Toast.makeText(getContext(), "Empty", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }, 1000);

                            linearLayoutItemContainer.addView(cardView);
                            break;

                        case ParentChannel.TYPE_CHANNEL:
                            View channelView = LayoutInflater.from(getContext()).inflate(R.layout.item_favorite_channel_channelcountainer, null);
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
                            frameLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FavoriteChannelInfoFragment favoriteChannelInfoFragment = new FavoriteChannelInfoFragment();
                                    favoriteChannelInfoFragment.setId(response.body().getData().get(finalId).getId());
                                    FragmentTransaction fragmentTransition = getFragmentManager().beginTransaction();
                                    fragmentTransition.replace(R.id.frame_fragment_container, favoriteChannelInfoFragment);
                                    fragmentTransition.addToBackStack(null);
                                    fragmentTransition.commit();
                                }

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
                            channelsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                            channelItemAdapter = new ChannelItemAdapter(getContext(), response.body().getData().get(i).getChannels());
                            channelsRecyclerView.setAdapter(channelItemAdapter);
                            channelItemAdapter.setOnClickedChannelEventCallBack(channel -> {
                                if (channel.getmType().equals(Channel.TYPE_PRIVATE))
                                    HelperUrl.checkAndJoinToRoom(getActivity(), channel.getSlug());
                                if (channel.getmType().equals(Channel.TYPE_PUBLIC))
                                    HelperUrl.checkUsernameAndGoToRoom(getActivity(), channel.getSlug(), HelperUrl.ChatEntry.chat);
                            });
                            linearLayoutItemContainer.addView(channelView);
                            break;
                        case ParentChannel.TYPE_CATEGORY:
                            RecyclerView categoryRecyclerView = new RecyclerView(getContext());
                            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams2.setMargins(Utils.dpToPx(4), Utils.dpToPx(4), Utils.dpToPx(4), Utils.dpToPx(4));
                            categoryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));
                            categoryRecyclerView.setLayoutParams(layoutParams2);
                            CategoryItemAdapter gridItem = new CategoryItemAdapter(getContext(), true, response.body().getData().get(i).getCategories());
                            gridItem.setOnClickedItemEventCallBack(new CategoryItemAdapter.OnClickedItemEventCallBack() {
                                @Override
                                public void onClickedItem(Category category) {

                                    FavoriteChannelInfoFragment favoriteChannelInfoFragment = new FavoriteChannelInfoFragment();
                                    favoriteChannelInfoFragment.setId(category.getId());
                                    FragmentTransaction fragmentTransition = getFragmentManager().beginTransaction();
                                    fragmentTransition.replace(R.id.frame_fragment_container, favoriteChannelInfoFragment);
                                    fragmentTransition.addToBackStack(null);
                                    fragmentTransition.commit();
                                }
                            });
                            categoryRecyclerView.setAdapter(gridItem);
                            categoryRecyclerView.setNestedScrollingEnabled(false);
                            linearLayoutItemContainer.addView(categoryRecyclerView);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ParentChannel> call, Throwable t) {
                Log.i("nazanin", "onFailure: " + t.getMessage());
                Toast toast = Toast.makeText(getContext(), "No Response", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        return rootView;

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
