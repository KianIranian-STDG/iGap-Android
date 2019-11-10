package net.iGap.fragments.populaChannel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.popularChannel.PopularChannelHomeAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.model.popularChannel.Category;
import net.iGap.model.popularChannel.Channel;
import net.iGap.model.popularChannel.Slide;
import net.iGap.viewmodel.PopularChannelHomeViewModel;

public class PopularChannelHomeFragment extends BaseAPIViewFrag implements ToolbarListener {
    private PopularChannelHomeViewModel popularChannelHomeViewModel;
    private PopularChannelHomeAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;
    private TextView epmtyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popularChannelHomeViewModel = ViewModelProviders.of(this).get(PopularChannelHomeViewModel.class);
        viewModel = popularChannelHomeViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_popular_channel, container, false);
        adapter = new PopularChannelHomeAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        popularChannelHomeViewModel.onStartFragment(this);

        popularChannelHomeViewModel.getFirstPageMutableLiveData().observe(getViewLifecycleOwner(), parentChannel -> {
            if (parentChannel != null)
                adapter.setData(parentChannel.getData());
        });

        adapter.setCallBack(new PopularChannelHomeAdapter.OnFavoriteChannelCallBack() {
            @Override
            public void onCategoryClick(Category category) {
                popularChannelHomeViewModel.onMoreClick(category.getId(), G.isAppRtl ? category.getTitle() : category.getTitleEn(), PopularChannelHomeFragment.this);
            }

            @Override
            public void onChannelClick(Channel channel) {
                popularChannelHomeViewModel.onChannelClick(channel, PopularChannelHomeFragment.this);
            }

            @Override
            public void onSlideClick(Slide slide) {
                popularChannelHomeViewModel.onSlideClick(PopularChannelHomeFragment.this, slide);
            }

            @Override
            public void onMoreClick(String moreId, String title) {
                popularChannelHomeViewModel.onMoreClick(moreId, title, PopularChannelHomeFragment.this);
            }
        });

        popularChannelHomeViewModel.getProgressMutableLiveData().observe(getViewLifecycleOwner(), progress -> {
            if (progress != null && progress)
                swipeRefreshLayout.setRefreshing(true);
            else
                swipeRefreshLayout.setRefreshing(false);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            popularChannelHomeViewModel.getFirstPage();
        });

        popularChannelHomeViewModel.getEmptyViewMutableLiveData().observe(getViewLifecycleOwner(), visibility -> {
            if (visibility != null)
                epmtyView.setVisibility(visibility);
        });

        epmtyView.setOnClickListener(v -> {
            popularChannelHomeViewModel.getFirstPage();
        });
    }


    private void setupViews() {
        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(G.fragmentActivity)
                .setListener(this)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.popular_channel))
                .setLeftIcon(R.string.back_icon);

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_popularChannel_home);
        LinearLayout toolBall = rootView.findViewById(R.id.ll_popularChannel_toolBar);
        swipeRefreshLayout = rootView.findViewById(R.id.sr_popularChannel_home);
        epmtyView = rootView.findViewById(R.id.emptyRecycle);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        toolBall.addView(toolbar.getView());
    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }
}
