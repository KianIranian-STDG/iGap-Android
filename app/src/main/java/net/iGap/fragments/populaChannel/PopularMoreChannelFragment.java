package net.iGap.fragments.populaChannel;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.popularChannel.PopularChannelMoreSliderAdapter;
import net.iGap.adapter.items.popularChannel.PopularMoreChannelAdapter;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.bannerslider.BannerSlider;


public class PopularMoreChannelFragment extends BaseFragment implements ToolbarListener {

    private View rootView;
    private View sliderCv;
    private TextView emptyTextView;
    private BannerSlider slider;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private String id;
    private int page = 1;
    private String title;
    private GridLayoutManager layoutManager;

    private PopularMoreChannelViewModel viewModel;
    private PopularMoreChannelAdapter adapter;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        rootView = LayoutInflater.from(G.fragmentActivity).inflate(R.layout.fragment_popular_channel_more, container, false);
        viewModel = new PopularMoreChannelViewModel();
        adapter = new PopularMoreChannelAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();

        viewModel.getFirstPage(id, page);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.getFirstPage(id, 1);
            page = 1;
        });


        viewModel.getMoreChannelMutableLiveData().observe(getViewLifecycleOwner(), childChannel -> {
            if (childChannel != null) {
                if (childChannel.getChannels().size() > 0)
                    adapter.setChannels(childChannel.getChannels());

                if (childChannel.getInfo().getAdvertisement() != null && childChannel.getInfo().getHasAd()) {
                    sliderCv.setVisibility(View.VISIBLE);
                    String scale = childChannel.getInfo().getAdvertisement().getmScale();
                    String[] scales = scale.split(":");
                    float height = Resources.getSystem().getDisplayMetrics().widthPixels * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
                    slider.getLayoutParams().height = Math.round(height);
                    int playBackTime = childChannel.getInfo().getAdvertisement().getmPlaybackTime();
                    PopularChannelMoreSliderAdapter sliderAdapter = new PopularChannelMoreSliderAdapter(childChannel.getInfo().getAdvertisement().getSlides(), scale);
                    slider.postDelayed(() -> {
                        slider.setAdapter(sliderAdapter);
                        slider.setSelectedSlide(0);
                        slider.setLoopSlides(true);
                        slider.setAnimateIndicators(true);
                        slider.setIndicatorSize(12);
                        slider.setInterval(playBackTime);
                        slider.setOnSlideClickListener(position -> {
                            viewModel.onSlideClick(PopularMoreChannelFragment.this, childChannel, position);
                        });
                    }, 200);
                }
            }
        });

        adapter.setCallBack(channel -> viewModel.onChannelClick(channel, PopularMoreChannelFragment.this));

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


//        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                page++;
//                viewModel.getFirstPage(id, page);
//            }
//        });

    }

    public void setupViews() {
        HelperToolbar helperToolbar = HelperToolbar.create()
                .setContext(G.fragmentActivity)
                .setListener(this)
                .setLogoShown(true)
                .setDefaultTitle(title)
                .setLeftIcon(R.string.back_icon);

        LinearLayout toolBarContainer = rootView.findViewById(R.id.ll_moreChannel_toolBar);
        recyclerView = rootView.findViewById(R.id.rv_moreChannel);
        swipeRefreshLayout = rootView.findViewById(R.id.sr_popularChannel_moreChannel);
        emptyTextView = rootView.findViewById(R.id.tv_popularChannel_emptyText);
        sliderCv = rootView.findViewById(R.id.cv_popularChannel_more);
        slider = rootView.findViewById(R.id.bs_popularChannel_more);
        swipeRefreshLayout = rootView.findViewById(R.id.sr_popularChannel_moreChannel);

        layoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        toolBarContainer.addView(helperToolbar.getView());
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }
}
