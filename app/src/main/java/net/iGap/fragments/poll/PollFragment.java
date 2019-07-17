package net.iGap.fragments.poll;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.poll.PollAdapter;
import net.iGap.adapter.items.poll.PollItem;
import net.iGap.fragments.FragmentToolBarBack;
import net.iGap.helper.HelperError;
import net.iGap.request.RequestClientGetPoll;

import java.util.ArrayList;

public class PollFragment extends FragmentToolBarBack {

    private RecyclerView rcDiscovery;
    private TextView emptyRecycle;
    private SwipeRefreshLayout pullToRefresh;
    private int pollId;
    PollAdapter pollAdapter;

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

    @Override
    public void onCreateViewBody(LayoutInflater inflater, LinearLayout root, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_discovery, root, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pollId = getArguments().getInt("pollId");
        init(view);
    }

    private void init(View view) {
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        emptyRecycle = view.findViewById(R.id.emptyRecycle);
        rcDiscovery = view.findViewById(R.id.rcDiscovery);

        pollAdapter = new PollAdapter(getActivity(), new ArrayList<>());
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRefreshing(true);
                boolean isSend = updateOrFetchRecycleViewData();
                if (!isSend) {
                    setRefreshing(false);
                    HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
                }
            }
        });

        emptyRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSend = updateOrFetchRecycleViewData();
                if (!isSend) {
                    HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(G.currentActivity);
        rcDiscovery.setLayoutManager(layoutManager);
        rcDiscovery.setAdapter(pollAdapter);
        tryToUpdateOrFetchRecycleViewData(0);
    }

    private void setRefreshing(boolean value) {
        pullToRefresh.setRefreshing(value);
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

    private void tryToUpdateOrFetchRecycleViewData(int count) {
        setRefreshing(true);
        boolean isSend = updateOrFetchRecycleViewData();

        if (!isSend) {
            if (count < 3) {
                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tryToUpdateOrFetchRecycleViewData(count + 1);
                    }
                }, 1000);
            } else {
                setRefreshing(false);
            }
        }
    }

    private boolean updateOrFetchRecycleViewData() {
        return new RequestClientGetPoll().getPoll(pollId, new OnPollList() {
            @Override
            public void onPollListReady(ArrayList<PollItem> pollArrayList, String title) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        setAdapterData(pollArrayList, title);

                        setRefreshing(false);
                    }
                });
            }

            @Override
            public void onError(int major, int minor) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setRefreshing(false);
                    }
                });
            }
        });
    }

    private void setAdapterData(ArrayList<PollItem> pollArrayList, String title) {
        pollAdapter.setPollList(pollArrayList);
        titleTextView.setText(title);
        pollAdapter.notifyChangeData();
    }

}
