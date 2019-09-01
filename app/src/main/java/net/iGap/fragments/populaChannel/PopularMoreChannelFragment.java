package net.iGap.fragments.populaChannel;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
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
import net.iGap.viewmodel.PopularMoreChannelViewModel;


public class PopularMoreChannelFragment extends BaseFragment implements ToolbarListener {

    private View rootView;
    private View sliderCv;
    private TextView emptyTextView;
    private BannerSlider slider;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NestedScrollView scrollView;
    private  HelperToolbar helperToolbar;

    private String id;
    private int page = 1;
    private String title = "";
    private String scale;
    private int pageMax = 20;
    private int itemSize;

    private PopularChannelMoreSliderAdapter sliderAdapter;
    private PopularMoreChannelViewModel viewModel;
    private PopularMoreChannelAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(PopularMoreChannelViewModel.class);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        rootView = LayoutInflater.from(G.fragmentActivity).inflate(R.layout.fragment_popular_channel_more, container, false);
        adapter = new PopularMoreChannelAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();

        viewModel.getFirstPage(id, 0, pageMax);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            pageMax = 20;
            page = 1;
            viewModel.getFirstPage(id, 0, pageMax);
        });


        viewModel.getMoreChannelMutableLiveData().observe(getViewLifecycleOwner(), childChannel -> {
            if (childChannel != null) {

                if (title.equals("")){
                    title = childChannel.getInfo().getTitle();
                    helperToolbar.setDefaultTitle(title);
                }

                if (childChannel.getInfo().getAdvertisement() != null && childChannel.getInfo().getHasAd() && page == 1) {
                    sliderCv.setVisibility(View.VISIBLE);
                    scale = childChannel.getInfo().getAdvertisement().getmScale();
                    String[] scales = scale.split(":");
                    float height = Resources.getSystem().getDisplayMetrics().widthPixels * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
                    slider.getLayoutParams().height = Math.round(height);
                    int playBackTime = childChannel.getInfo().getAdvertisement().getmPlaybackTime();
                    sliderAdapter = new PopularChannelMoreSliderAdapter(childChannel.getInfo().getAdvertisement().getSlides(), scale);
                    slider.postDelayed(() -> {
                        slider.setAdapter(sliderAdapter);
                        slider.setSelectedSlide(0);
                        slider.setLoopSlides(true);
                        slider.setInterval(playBackTime);
                        slider.setOnSlideClickListener(position -> {
                            viewModel.onSlideClick(PopularMoreChannelFragment.this, childChannel, position);
                        });
                    }, 200);
                }

                if (childChannel.getChannels().size() > 0) {
                    if (page == 1) {
                        adapter.setChannels(childChannel.getChannels());
                    } else
                        adapter.addChannel(childChannel.getChannels());

                    itemSize = childChannel.getChannels().size();
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


        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (nestedScrollView1, i, i1, i2, i3) -> {
            if (itemSize >= pageMax) {
                int nextPage = pageMax + pageMax;
                viewModel.getFirstPage(id, pageMax, nextPage);
                page = page + 1;
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (scale != null) {
            String[] scales = scale.split(":");
            float height = Resources.getSystem().getDisplayMetrics().widthPixels * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
            slider.getLayoutParams().height = Math.round(height);
            sliderAdapter.setScale(scale);
        }
    }

    public void setupViews() {
        helperToolbar = HelperToolbar.create()
                .setContext(G.fragmentActivity)
                .setListener(this)
                .setLogoShown(true)
                .setDefaultTitle(title)
                .setLeftIcon(R.string.back_icon);

        LinearLayout toolBarContainer = rootView.findViewById(R.id.ll_moreChannel_toolBar);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_moreChannel);
        swipeRefreshLayout = rootView.findViewById(R.id.sr_popularChannel_moreChannel);
        emptyTextView = rootView.findViewById(R.id.tv_popularChannel_emptyText);
        sliderCv = rootView.findViewById(R.id.cv_popularChannel_more);
        slider = rootView.findViewById(R.id.bs_popularChannel_more);
        scrollView = rootView.findViewById(R.id.sv_popularChannel_more);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

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

    public int getPage() {
        return page;
    }
}
