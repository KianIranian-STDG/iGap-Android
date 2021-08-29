package net.iGap.fragments.poll;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.poll.PollAdapter;
import net.iGap.adapter.items.poll.PollItem;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.request.RequestClientGetPoll;

import java.util.ArrayList;

public class PollFragment extends BaseFragment {
    private int pollId;
    private RecyclerView rcDiscovery;
    private TextView emptyRecycle;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PollAdapter pollAdapter;
    private HelperToolbar mHelperToolbar;

    public static PollFragment newInstance(int page) {
        PollFragment pollFragment = new PollFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("pollId", page);
        pollFragment.setArguments(bundle);
        return pollFragment;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        PollAdapter adapter = (PollAdapter) rcDiscovery.getAdapter();
        if (adapter != null) {
            pollAdapter.notifyChangeData();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discovery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pollId = getArguments().getInt("pollId");
        init(view);
    }

    private void init(View view) {
        emptyRecycle = view.findViewById(R.id.emptyRecycle);
        rcDiscovery = view.findViewById(R.id.rcDiscovery);
        pollAdapter = new PollAdapter(getActivity(), new ArrayList<>());
        swipeRefreshLayout = view.findViewById(R.id.pullToRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            setRefreshing(true);
            boolean isSend = updateOrFetchRecycleViewData();
            if (!isSend) {
                setRefreshing(false);
                HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
            }
        });

        emptyRecycle.setOnClickListener(v -> {
            boolean isSend = updateOrFetchRecycleViewData();
            if (!isSend) {
                HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
            }
        });

        rcDiscovery.setLayoutManager(new LinearLayoutManager(getContext()));
        rcDiscovery.setAdapter(pollAdapter);

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setLogoShown(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });
        ViewGroup viewGroup = view.findViewById(R.id.fd_layout_toolbar);
        viewGroup.addView(mHelperToolbar.getView());
        tryToUpdateOrFetchRecycleViewData(0);
    }

    private void tryToUpdateOrFetchRecycleViewData(int count) {
        setRefreshing(true);
        boolean isSend = updateOrFetchRecycleViewData();
        if (!isSend) {
            if (count < 3) {
                G.handler.postDelayed(() -> tryToUpdateOrFetchRecycleViewData(count + 1), 1000);
            } else {
                setRefreshing(false);
            }
        }
    }

    private boolean updateOrFetchRecycleViewData() {
        return new RequestClientGetPoll().getPoll(pollId, new OnPollList() {
            @Override
            public void onPollListReady(ArrayList<PollItem> pollArrayList, String title) {
                G.handler.post(() -> {
                    setAdapterData(pollArrayList, title);
                    setRefreshing(false);
                });
            }

            @Override
            public void onError(int major, int minor) {
                G.handler.post(() -> setRefreshing(false));
            }
        });
    }

    private void setRefreshing(boolean value) {
        swipeRefreshLayout.setRefreshing(value);
        if (value) {
            emptyRecycle.setVisibility(View.GONE);
        } else {
            if (pollAdapter.getItemCount() == 0) {
                emptyRecycle.setVisibility(View.VISIBLE);
            } else {
                emptyRecycle.setVisibility(View.GONE);
            }
        }
    }

    private void setAdapterData(ArrayList<PollItem> pollArrayList, String title) {
        pollAdapter.setPollList(pollArrayList);
        mHelperToolbar.setDefaultTitle(title);
        pollAdapter.notifyChangeData();
    }
}
