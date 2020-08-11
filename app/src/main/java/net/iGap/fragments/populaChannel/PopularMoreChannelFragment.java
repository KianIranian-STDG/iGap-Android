package net.iGap.fragments.populaChannel;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.iGap.R;
import net.iGap.adapter.items.popularChannel.PopularChannelMoreSliderAdapter;
import net.iGap.adapter.items.popularChannel.PopularMoreChannelAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperUrl;
import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.model.popularChannel.Channel;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.PopularMoreChannelViewModel;


public class PopularMoreChannelFragment extends BaseAPIViewFrag<PopularMoreChannelViewModel> {

    private View sliderCv;
    private TextView emptyTextView;
    private BannerSlider slider;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HelperToolbar helperToolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                String id = null;
                String title = null;
                if (getArguments() != null) {
                    id = getArguments().getString("id", "");
                    title = getArguments().getString("title", "");
                }
                return (T) new PopularMoreChannelViewModel(id, title);
            }
        }).get(PopularMoreChannelViewModel.class);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return attachToSwipeBack(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_popular_channel_more, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        helperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        viewModel.toolbarBackClick();
                    }
                })
                .setLogoShown(true)
                .setDefaultTitle("")
                .setLeftIcon(R.string.back_icon);

        ((LinearLayout) view.findViewById(R.id.ll_moreChannel_toolBar)).addView(helperToolbar.getView());

        RecyclerView recyclerView = view.findViewById(R.id.rv_moreChannel);
        swipeRefreshLayout = view.findViewById(R.id.sr_popularChannel_moreChannel);
        emptyTextView = view.findViewById(R.id.tv_popularChannel_emptyText);
        sliderCv = view.findViewById(R.id.cv_popularChannel_more);
        slider = view.findViewById(R.id.bs_popularChannel_more);

        recyclerView.setAdapter(new PopularMoreChannelAdapter(new PopularMoreChannelAdapter.OnMoreChannelCallBack() {
            @Override
            public void onChannelClick(Channel channel) {
                viewModel.onChannelClick(channel);
            }

            @Override
            public void onLoadMore() {
                viewModel.loadMoreData();
            }
        }));

        recyclerView.setNestedScrollingEnabled(false);

        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.onSwipeRefresh());

        view.findViewById(R.id.retryView).setOnClickListener(v -> viewModel.onSwipeRefresh());

        viewModel.getGoBack().observe(getViewLifecycleOwner(), isGoBack -> {
            if (getActivity() != null && isGoBack != null && isGoBack) {
                getActivity().onBackPressed();
            }
        });

        viewModel.getToolbarTitle().observe(getViewLifecycleOwner(), toolbarTitle -> {
            if (toolbarTitle != null) {
                helperToolbar.setDefaultTitle(toolbarTitle);
            }
        });

        viewModel.getShowAdvertisement().observe(getViewLifecycleOwner(), adv -> {
            if (adv != null) {
                sliderCv.setVisibility(View.VISIBLE);
                String scale = adv.getmScale();
                slider.postDelayed(() -> {
                    slider.setAdapter(new PopularChannelMoreSliderAdapter(adv.getSlides(), scale));
                    slider.setSelectedSlide(0);
                    slider.setLoopSlides(true);
                    slider.setInterval(adv.getmPlaybackTime());
                    slider.setOnSlideClickListener(position -> {
                        viewModel.onSlideClick(adv.getSlides().get(position));
                    });
                }, 200);
            } else {
                sliderCv.setVisibility(View.GONE);
            }
        });

        viewModel.getGoToChannel().observe(getViewLifecycleOwner(), data -> {
            if (getActivity() != null && data != null) {
                if (data.isPrivate()) {
                    HelperUrl.checkAndJoinToRoom(getActivity(), data.getSlug());
                } else {
                    HelperUrl.checkUsernameAndGoToRoom(getActivity(), data.getSlug(), HelperUrl.ChatEntry.chat);
                }
            }
        });


        viewModel.getMoreChannelMutableLiveData().observe(getViewLifecycleOwner(), childChannel -> {
            Log.wtf(this.getClass().getName(), "getMoreChannelMutableLiveData");
            if (childChannel != null && recyclerView.getAdapter() instanceof PopularMoreChannelAdapter) {
                Log.wtf(this.getClass().getName(), "getMoreChannelMutableLiveData");
                ((PopularMoreChannelAdapter) recyclerView.getAdapter()).setChannels(childChannel);
            }
        });

        viewModel.getShowRetryView().observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null) {
                view.findViewById(R.id.retryView).setVisibility(isShow ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getProgressMutableLiveData().observe(getViewLifecycleOwner(), progress -> {
            if (progress != null && progress)
                swipeRefreshLayout.setRefreshing(true);
            else
                swipeRefreshLayout.setRefreshing(false);
        });

        viewModel.getEmptyViewMutableLiveData().observe(getViewLifecycleOwner(), haveEmptyView -> {
            if (haveEmptyView != null && haveEmptyView)
                emptyTextView.setVisibility(View.VISIBLE);
            else
                emptyTextView.setVisibility(View.GONE);
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (viewModel.getScale() != null) {
            ((PopularChannelMoreSliderAdapter) slider.getAdapter()).setScale(viewModel.getScale());
        }
    }
}
