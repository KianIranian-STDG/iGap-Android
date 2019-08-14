package net.iGap.fragments.populaChannel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.popularChannel.PopularChannelHomeAdapter;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.model.popularChannel.Category;
import net.iGap.model.popularChannel.Channel;

public class PopularChannelHomeFragment extends BaseFragment implements ToolbarListener {
    private PopularChannelHomeViewModel viewModel;
    private PopularChannelHomeAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_popular_channel, container, false);
        viewModel = new PopularChannelHomeViewModel();
        adapter = new PopularChannelHomeAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        viewModel.onStartFragment(this);

        viewModel.getFirstPageMutableLiveData().observe(getViewLifecycleOwner(), parentChannel -> {
            if (parentChannel != null)
                adapter.setData(parentChannel.getData());
        });

        adapter.setCallBack(new PopularChannelHomeAdapter.OnFavoriteChannelCallBack() {
            @Override
            public void onCategoryClick(Category category) {
                viewModel.onMoreClick(category.getId(), G.isAppRtl ? category.getTitle() : category.getTitleEn(), PopularChannelHomeFragment.this);
            }

            @Override
            public void onChannelClick(Channel channel) {
                viewModel.onChannelClick(channel, PopularChannelHomeFragment.this);
            }

            @Override
            public void onSlideClick(int position) {
                viewModel.onSlideClick(position);
            }

            @Override
            public void onMoreClick(String moreId, String title) {
                viewModel.onMoreClick(moreId, title, PopularChannelHomeFragment.this);
            }
        });

        viewModel.getProgressMutableLiveData().observe(getViewLifecycleOwner(), progress -> {
            if (progress != null && progress)
                swipeRefreshLayout.setRefreshing(true);
            else
                swipeRefreshLayout.setRefreshing(false);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.getFirstPage();
        });
    }

    private void setupViews() {
        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(G.fragmentActivity)
                .setListener(this)
                .setLogoShown(true)
                .setDefaultTitle("کانال های پرمخاطب")
                .setLeftIcon(R.string.back_icon);
        if (!G.isAppRtl) {
            toolbar.setDefaultTitle("Popular Channel");
        }

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_popularChannel_home);
        LinearLayout toolBall = rootView.findViewById(R.id.ll_popularChannel_toolBar);
        swipeRefreshLayout = rootView.findViewById(R.id.sr_popularChannel_home);

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
