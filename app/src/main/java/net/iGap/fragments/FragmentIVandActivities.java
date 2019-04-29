package net.iGap.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.IVandActivityAdapter;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestUserIVandGetActivities;

import java.util.ArrayList;

import static net.iGap.viewmodel.FragmentIVandProfileViewModel.scanBarCode;

public class FragmentIVandActivities extends FragmentToolBarBack {
    private RecyclerView recyclerView;
    private TextView retry;
    private TextView emptyActivitiesText;
    private SwipeRefreshLayout pullToRefresh;
    private IVandActivityAdapter iVandActivityAdapter;
    private boolean isLoading;
    private boolean existMoreItem;
    private Button btnScanBarCode;

    public static FragmentIVandActivities newInstance() {
        FragmentIVandActivities fragmentIVandActivities = new FragmentIVandActivities();
        Bundle bundle = new Bundle();
        fragmentIVandActivities.setArguments(bundle);
        return fragmentIVandActivities;
    }

    public FragmentIVandActivities() {
    }

    @Override
    public void onCreateViewBody(LayoutInflater inflater, LinearLayout root, @Nullable Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_ivand_activities, root, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnScanBarCode).getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(G.appBarColor), PorterDuff.Mode.SRC_IN));

        iVandActivityAdapter = new IVandActivityAdapter(new ArrayList<>());
        titleTextView.setText(getString(R.string.ivand_activities_title));
        btnScanBarCode =view.findViewById(R.id.btnScanBarCode);
        retry = view.findViewById(R.id.retry);
        emptyActivitiesText = view.findViewById(R.id.emptyActivitiesText);
        isLoading = false;
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                boolean isSend = updateOrFetchRecycleViewData(0);
                if (!isSend) {
                    turnOffRefresh(false);
                    HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
                }
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSend = updateOrFetchRecycleViewData(0);
                if (!isSend) {
                    HelperError.showSnackMessage(getString(R.string.wallet_error_server), false);
                }
            }
        });

        btnScanBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarCode(G.currentActivity);
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
                        iVandActivityAdapter.notifyItemInserted(iVandActivityAdapter.getItemCount() - 1);
                        if (isSend) {
                            isLoading = true;
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        turnOnRefresh();

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tryToUpdateOrFetchRecycleViewData(0);
            }
        }, 100);
    }

    private void turnOnRefresh() {
        retry.setVisibility(View.GONE);
        emptyActivitiesText.setVisibility(View.GONE);
        pullToRefresh.setRefreshing(true);
    }

    private void turnOffRefresh(boolean isShowingEmptyList) {
        if (iVandActivityAdapter.getItemCount() == 0) {
            if (isShowingEmptyList) {
                emptyActivitiesText.setVisibility(View.VISIBLE);
                retry.setVisibility(View.GONE);
            } else {
                emptyActivitiesText.setVisibility(View.GONE);
                retry.setVisibility(View.VISIBLE);
            }
        } else {
            retry.setVisibility(View.GONE);
            emptyActivitiesText.setVisibility(View.GONE);
        }

        pullToRefresh.setRefreshing(false);
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
        } else if (!isSend){
            turnOffRefresh(false);
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
                            setAdapterData(discoveryArrayList);
                            turnOffRefresh(true);
                            existMoreItem = true;
                        } else {
                            iVandActivityAdapter.removeLoadingItem();
                            isLoading = false;
                            iVandActivityAdapter.addMoreItemList(discoveryArrayList);
                            iVandActivityAdapter.notifyDataSetChanged();
                        }

                        if (discoveryArrayList.size() == 0) {
                            existMoreItem = false;
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
                            turnOffRefresh(false);
                        } else {
                            iVandActivityAdapter.removeLoadingItem();
                            iVandActivityAdapter.notifyItemRemoved(iVandActivityAdapter.getItemCount());
                            isLoading = false;
                        }
                    }
                });
            }

        });

        if (isSend && offset == 0) {
            turnOnRefresh();
        }

        return isSend;
    }


    private void setAdapterData(ArrayList<ProtoGlobal.IVandActivity> iVandActivities) {
        iVandActivityAdapter.setData(iVandActivities);
        iVandActivityAdapter.notifyDataSetChanged();
    }
}
