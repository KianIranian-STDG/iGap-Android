package net.iGap.fragments.popular;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.adapter.items.popular.AdapterCategoryItem;
import net.iGap.adapter.items.popular.AdapterChannelItem;
import net.iGap.adapter.items.popular.AdapterSliderItem;
import net.iGap.api.PopularChannelApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.model.PopularChannel.ParentChannel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentPopularChannelParent extends BaseFragment implements ToolbarListener {
    private HelperToolbar toolbar;
    private PopularChannelApi api;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_popular_channel_parent, container, false);
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
                            RecyclerView sliderRecyclerView = new RecyclerView(getContext());
                            sliderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                            sliderRecyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            PagerSnapHelper snapHelper = new PagerSnapHelper();
                            snapHelper.attachToRecyclerView(sliderRecyclerView);
                            sliderRecyclerView.setAdapter(new AdapterSliderItem(getContext(), false, response.body().getData().get(i).getSlides()));
                            linearLayoutItemContainer.addView(sliderRecyclerView);
                            break;

                        case ParentChannel.TYPE_CHANNEL:
                            View channelView = LayoutInflater.from(getContext()).inflate(R.layout.item_popular_channel_channel, null);
                            FrameLayout frameLayout = new FrameLayout(getContext());
                            frameLayout = channelView.findViewById(R.id.frame_more_one);
                            frameLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FragmentTransaction fragmentTransition = getFragmentManager().beginTransaction();
                                    fragmentTransition.replace(R.id.ll_container, new FragmentPopularChannelChild());
                                    fragmentTransition.addToBackStack(null);
                                    fragmentTransition.commit();
                                }
                            });
                            TextView textViewTitle = channelView.findViewById(R.id.tv_item_popular_title);
                            textViewTitle.setText(response.body().getData().get(0).getInfo().getTitle());
                            RecyclerView channelsRecyclerView = channelView.findViewById(R.id.rv_item_popular_row);
                            channelsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                            channelsRecyclerView.setAdapter(new AdapterChannelItem(getContext(), response.body().getData().get(i).getChannels()));
                            linearLayoutItemContainer.addView(channelView);
                            break;
                        case ParentChannel.TYPE_CATEGORY:
                            RecyclerView categoryRecyclerView = new RecyclerView(getContext());
                            categoryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));
                            categoryRecyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            AdapterCategoryItem gridItem = new AdapterCategoryItem(getContext(), true, response.body().getData().get(i).getCategories());
                            gridItem.setOnClickedItemEventCallBack(new AdapterCategoryItem.OnClickedItemEventCallBack() {
                                @Override
                                public void onClickedItem() {
                                    FragmentTransaction fragmentTransition = getFragmentManager().beginTransaction();
                                    fragmentTransition.replace(R.id.ll_container, new FragmentPopularChannelChild());
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
