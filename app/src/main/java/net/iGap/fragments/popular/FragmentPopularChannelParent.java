package net.iGap.fragments.popular;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.popular.AdapterCategoryItem;
import net.iGap.adapter.items.popular.AdapterChannelItem;
import net.iGap.adapter.items.popular.AdapterSliderItem;
import net.iGap.api.PopularChannelApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.model.PopularChannel.Category;
import net.iGap.model.PopularChannel.ParentChannel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentPopularChannelParent extends BaseFragment implements ToolbarListener {
    private HelperToolbar toolbar;
    private PopularChannelApi api;
    private View rootView;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_popular_channel_parent, container, false);
        api = ApiServiceProvider.getChannelApi();
        LinearLayout toolbarContainer = rootView.findViewById(R.id.ll_popular_parent_toolbar);
        toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setListener(this)
                .setLogoShown(true)
                .setDefaultTitle("کانال های پر مخاطب")
                .setLeftIcon(R.string.back_icon);
        toolbarContainer.addView(toolbar.getView());

        api.getParentChannel().enqueue(new Callback<ParentChannel>() {
            @Override
            public void onResponse(Call<ParentChannel> call, Response<ParentChannel> response) {
                LinearLayout linearLayoutItemContainer = rootView.findViewById(R.id.rl_fragmentContainer);
                for (int i = 0; i < response.body().getData().size(); i++) {
                    switch (response.body().getData().get(i).getType()) {
                        case ParentChannel.TYPE_SLIDE:
                            Log.i("nazanin", "onResponse: "+ParentChannel.TYPE_SLIDE);
                            RecyclerView sliderRecyclerView = new RecyclerView(getContext());
                            sliderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            layoutParams.setMargins(0, 8, 0, 8);
                            sliderRecyclerView.setLayoutParams(layoutParams);
                            PagerSnapHelper snapHelper = new PagerSnapHelper();
                            snapHelper.attachToRecyclerView(sliderRecyclerView);
                            sliderRecyclerView.setAdapter(new AdapterSliderItem(getContext(), response.body().getData().get(i).getSlides()));
                            linearLayoutItemContainer.addView(sliderRecyclerView);
                            break;

                        case ParentChannel.TYPE_CHANNEL:
                            Log.i("nazanin", "onResponse: "+ParentChannel.TYPE_CHANNEL);
                            View channelView = LayoutInflater.from(getContext()).inflate(R.layout.item_popular_channel_channel, null);
                            RelativeLayout relativeLayoutRow = channelView.findViewById(R.id.rl_item_pop_rows);
                            LinearLayout linearLayoutRow = channelView.findViewById(R.id.ll_item_pop_rows);
                            ImageView imageViewMore = channelView.findViewById(R.id.iv_item_popular_more);
                            if (G.isDarkTheme) {
                                relativeLayoutRow.setBackground(getResources().getDrawable(R.drawable.shape_popular_channel_all_them));
                                linearLayoutRow.setBackground(getResources().getDrawable(R.drawable.shape_popular_channel_dark_them));
                                imageViewMore.setColorFilter(getResources().getColor(R.color.navigation_dark_mode_bg));
                            }
                            FrameLayout frameLayout = channelView.findViewById(R.id.frame_more_one);
                            int finalId = i;
                            frameLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FragmentPopularChannelChild fragmentPopularChannelChild = new FragmentPopularChannelChild();
                                    fragmentPopularChannelChild.setId(response.body().getData().get(finalId).getId());
                                    FragmentTransaction fragmentTransition = getFragmentManager().beginTransaction();
                                    fragmentTransition.replace(R.id.frame_fragment_container, fragmentPopularChannelChild);
                                    fragmentTransition.addToBackStack(null);
                                    fragmentTransition.commit();
                                }

                            });

                            TextView textViewTitle = channelView.findViewById(R.id.tv_item_popular_title);
                            textViewTitle.setText(response.body().getData().get(i).getInfo().getTitle());

                            RecyclerView channelsRecyclerView = channelView.findViewById(R.id.rv_item_popular_row);
                            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams1.setMargins(0, 8, 0, 8);
                            channelView.setLayoutParams(layoutParams1);
                            channelsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                            channelsRecyclerView.setAdapter(new AdapterChannelItem(getContext(), response.body().getData().get(i).getChannels()));
                            linearLayoutItemContainer.addView(channelView);
                            break;
                        case ParentChannel.TYPE_CATEGORY:
                            Log.i("nazanin", "onResponse: "+ParentChannel.TYPE_CATEGORY);
                            RecyclerView categoryRecyclerView = new RecyclerView(getContext());
                            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams2.setMargins(0, 8, 0, 8);
                            categoryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));
                            categoryRecyclerView.setLayoutParams(layoutParams2);
                            AdapterCategoryItem gridItem = new AdapterCategoryItem(getContext(), true, response.body().getData().get(i).getCategories());
                            gridItem.setOnClickedItemEventCallBack(new AdapterCategoryItem.OnClickedItemEventCallBack() {
                                @Override
                                public void onClickedItem(Category category) {
                                    FragmentPopularChannelChild fragmentPopularChannelChild = new FragmentPopularChannelChild();
                                    fragmentPopularChannelChild.setId(category.getId());
                                    FragmentTransaction fragmentTransition = getFragmentManager().beginTransaction();
                                    fragmentTransition.replace(R.id.frame_fragment_container, fragmentPopularChannelChild);
                                    fragmentTransition.addToBackStack(null);
                                    fragmentTransition.commit();
                                }
                            });
                            categoryRecyclerView.setAdapter(gridItem);
                            linearLayoutItemContainer.addView(categoryRecyclerView);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ParentChannel> call, Throwable t) {

            }
        });
        return rootView;

    }

    @Override
    public void onLeftIconClickListener(View view) {
        getActivity().onBackPressed();
    }
}
