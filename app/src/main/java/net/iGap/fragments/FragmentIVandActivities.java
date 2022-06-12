package net.iGap.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.IVandActivityAdapter;
import net.iGap.databinding.FragmentIvandActivitiesBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestUserIVandGetActivities;

import java.util.ArrayList;

public class FragmentIVandActivities extends BaseFragment {
    private FragmentIvandActivitiesBinding binding;

    private RecyclerView recyclerView;
    private TextView retry;
    private TextView emptyActivitiesText;
    private SwipeRefreshLayout pullToRefresh;
    private IVandActivityAdapter iVandActivityAdapter;
    private boolean isLoading;
    private boolean existMoreItem;
    private Toolbar ivAndActivitiesToolbar;

    public static FragmentIVandActivities newInstance() {
        FragmentIVandActivities fragmentIVandActivities = new FragmentIVandActivities();
        Bundle bundle = new Bundle();
        fragmentIVandActivities.setArguments(bundle);
        return fragmentIVandActivities;
    }

    public FragmentIVandActivities() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ivand_activities, container, false);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        iVandActivityAdapter = new IVandActivityAdapter(new ArrayList<>());
        retry = view.findViewById(R.id.retry);
        retry.setTextColor(Theme.getColor(Theme.key_title_text));
        emptyActivitiesText = view.findViewById(R.id.emptyActivitiesText);
        emptyActivitiesText.setTextColor(Theme.getColor(Theme.key_title_text));
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

        ivAndActivitiesToolbar = new Toolbar(getContext());
        ivAndActivitiesToolbar.setTitle(getString(R.string.ivand_activities_title));
        ivAndActivitiesToolbar.setBackIcon(new BackDrawable(false));
        ivAndActivitiesToolbar.setListener(i -> {
                    switch (i) {
                        case -1:
                            if (getActivity() != null) {
                                getActivity().onBackPressed();
                            }
                            break;

                    }
                }
        );

        binding.toolbar.addView(ivAndActivitiesToolbar, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.dp(56), Gravity.TOP));

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
        } else if (!isSend) {
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
