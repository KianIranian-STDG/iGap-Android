package net.iGap.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.IVandActivityAdapter;
import net.iGap.helper.HelperError;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestUserIVandGetActivities;

import java.util.ArrayList;

public class FragmentIVandActivities extends FragmentToolBarBack {
    private RecyclerView recyclerView;
    private TextView emptyRecycle;
    private SwipeRefreshLayout pullToRefresh;
    private IVandActivityAdapter iVandActivityAdapter;
    private boolean isLoading;
    private boolean existMoreItem;

    public static FragmentIVandActivities newInstance() {
        FragmentIVandActivities fragmentIVandActivities = new FragmentIVandActivities();
        Bundle bundle = new Bundle();
        fragmentIVandActivities.setArguments(bundle);
        return fragmentIVandActivities;
    }

    public FragmentIVandActivities() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_ivand_activities, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        iVandActivityAdapter = new IVandActivityAdapter(new ArrayList<>());
        emptyRecycle = view.findViewById(R.id.emptyRecycle);
        isLoading = false;
        existMoreItem = true;
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                boolean isSend = updateOrFetchRecycleViewData(0);
                if (!isSend) {
                    pullToRefresh.setRefreshing(false);
                    HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
                }
            }
        });

        emptyRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSend = updateOrFetchRecycleViewData(0);
                if (!isSend) {
                    HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
                }
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(G.currentActivity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(iVandActivityAdapter);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(requireContext(),
                layoutManager.getOrientation());
        itemDecorator.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider_rv));

        recyclerView.addItemDecoration(itemDecorator);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (existMoreItem && !isLoading) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == iVandActivityAdapter.getItemCount() - 1) {
                        boolean isSend = updateOrFetchRecycleViewData(iVandActivityAdapter.getItemCountWithoutLoadingItem());
                        iVandActivityAdapter.addLoadingItem();
                        if (isSend) {
                            isLoading = true;
                        }
                    }
                }
            }
        });
        recyclerView.setVisibility(View.GONE);
        emptyRecycle.setVisibility(View.VISIBLE);

        iVandActivityAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (iVandActivityAdapter.getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    emptyRecycle.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyRecycle.setVisibility(View.GONE);
                }
            }
        });
        tryToUpdateOrFetchRecycleViewData(0);
    }

    private void tryToUpdateOrFetchRecycleViewData(int count) {
        boolean isSend = updateOrFetchRecycleViewData(0);
        if (!isSend && count < 3) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tryToUpdateOrFetchRecycleViewData(count + 1);
                }
            }, 1000);
        }
    }

    private boolean updateOrFetchRecycleViewData(int offset) {

        boolean isSend = new RequestUserIVandGetActivities().getActivities(offset, 10, new RequestUserIVandGetActivities.OnGetActivities() {
            @Override
            public void onGetActivitiesReady(ProtoGlobal.Pagination pagination, ArrayList<ProtoGlobal.IVandActivity> discoveryArrayList) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (offset == 0) {
                            pullToRefresh.setRefreshing(false);
                            setAdapterData(discoveryArrayList);
                        } else {
                            iVandActivityAdapter.removeLoadingItem();
                            isLoading = false;
                            iVandActivityAdapter.addMoreItemList(discoveryArrayList);
                            iVandActivityAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void onError(ProtoGlobal.Pagination pagination) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (offset == 0) {
                            pullToRefresh.setRefreshing(false);
                        } else {
                            iVandActivityAdapter.removeLoadingItem();
                            isLoading = false;
                        }
                    }
                });
            }

        });

        if (isSend && offset == 0) {
            pullToRefresh.setRefreshing(true);
        }

        return isSend;
    }


    private void setAdapterData(ArrayList<ProtoGlobal.IVandActivity> iVandActivities) {
        iVandActivityAdapter.setData(iVandActivities);
        iVandActivityAdapter.notifyDataSetChanged();
    }
}
