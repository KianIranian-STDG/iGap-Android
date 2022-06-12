package net.iGap.fragments.populaChannel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import net.iGap.fragments.FragmentWebView;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperUrl;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.PopularChannelHomeViewModel;

public class PopularChannelHomeFragment extends BaseAPIViewFrag<PopularChannelHomeViewModel> {

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView epmtyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(PopularChannelHomeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_popular_channel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_popularChannel_home);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        swipeRefreshLayout = view.findViewById(R.id.sr_popularChannel_home);
        epmtyView = view.findViewById(R.id.emptyRecycle);
        Toolbar popularChannelsToolbar = new Toolbar(getContext());
        popularChannelsToolbar.setBackIcon(new BackDrawable(false));
        popularChannelsToolbar.setTitle(getString(R.string.popular_channel));
        popularChannelsToolbar.setListener(i -> {
            if (i == -1) {
                if (getActivity() != null)
                    getActivity().onBackPressed();
            }
        });
        recyclerView.setAdapter(new PopularChannelHomeAdapter(viewModel.getRecyclerItemClick()));
        ((ViewGroup) view.findViewById(R.id.ll_popularChannel_toolBar)).addView(popularChannelsToolbar);


        viewModel.getFirstPageMutableLiveData().observe(getViewLifecycleOwner(), parentChannel -> {
            if (recyclerView.getAdapter() instanceof PopularChannelHomeAdapter && parentChannel != null) {
                ((PopularChannelHomeAdapter) recyclerView.getAdapter()).setData(parentChannel.getData());
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

        viewModel.getEmptyViewMutableLiveData().observe(getViewLifecycleOwner(), visibility -> {
            if (visibility != null)
                epmtyView.setVisibility(visibility);
        });

        viewModel.getGoToMorePage().observe(getViewLifecycleOwner(), data -> {
            if (getActivity() != null && data != null) {
                PopularMoreChannelFragment moreChannelFragment = new PopularMoreChannelFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", data.getId());
                bundle.putString("title", G.isAppRtl ? data.getTitle() : data.getTitleEn());
                moreChannelFragment.setArguments(bundle);
                new HelperFragment(getActivity().getSupportFragmentManager(), moreChannelFragment).setReplace(false).load(true);
            }
        });

        viewModel.getGoToRoom().observe(getViewLifecycleOwner(), link -> {
            if (getActivity() != null && link != null) {
                HelperUrl.checkUsernameAndGoToRoom(getActivity(), link, HelperUrl.ChatEntry.chat);
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

        viewModel.getGoToWebViewPage().observe(getViewLifecycleOwner(), link -> {
            if (getActivity() != null && link != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentWebView.newInstance(link, false, null)).setReplace(false).load();
            }
        });

        viewModel.getOpenBrowser().observe(getViewLifecycleOwner(), link -> {
            if (getActivity() != null && link != null) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
                int checkedInAppBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
                if (checkedInAppBrowser == 1 && !HelperUrl.isNeedOpenWithoutBrowser(link)) {
                    HelperUrl.openBrowser(link);
                } else {
                    HelperUrl.openWithoutBrowser(link);
                }
            }
        });

        epmtyView.setOnClickListener(v -> viewModel.getFirstPage());
    }
}
