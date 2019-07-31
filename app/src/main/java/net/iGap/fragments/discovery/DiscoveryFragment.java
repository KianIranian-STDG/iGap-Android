package net.iGap.fragments.discovery;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryAdapter;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.BaseMainFragments;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.request.RequestClientGetDiscovery;

import java.util.ArrayList;

public class DiscoveryFragment extends BaseMainFragments implements ToolbarListener {

    private RecyclerView rcDiscovery;
    private TextView emptyRecycle;
    private SwipeRefreshLayout pullToRefresh;
    private int page;
    private boolean isSwipeBackEnable = true;
    private HelperToolbar mHelperToolbar;

    private ArrayList<DiscoveryItem> discoveryArrayList;

    public static DiscoveryFragment newInstance(int page) {
        DiscoveryFragment discoveryFragment = new DiscoveryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page", page);
        if (page == 0) {
            discoveryFragment.isSwipeBackEnable = false;
        }
        discoveryFragment.setArguments(bundle);
        return discoveryFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        if (isSwipeBackEnable) {
            return attachToSwipeBack(view);
        } else {
            return view;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        rcDiscovery.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once
                rcDiscovery.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Here you can get the size :)
                if (rcDiscovery.getAdapter() instanceof DiscoveryAdapter) {
                    ((DiscoveryAdapter) rcDiscovery.getAdapter()).setWidth(rcDiscovery.getWidth());
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        page = getArguments().getInt("page");

        //uncomment this lines after added small avatar and discovery setting
        if (page != 0) {
            mHelperToolbar = HelperToolbar.create()
                    .setContext(getContext())
                    .setLogoShown(true)
                    .setLeftIcon(R.string.back_icon)
                    .setListener(this);

        } else {
            mHelperToolbar = HelperToolbar.create()
                    .setContext(getContext())
                    //.setLeftIcon(R.string.flag_icon)
                    // .setRightSmallAvatarShown(true)
                    .setLogoShown(true)
                    .setFragmentActivity(getActivity())
                    .setPassCodeVisibility(true, R.string.unlock_icon)
                    .setScannerVisibility(true, R.string.scan_qr_code_icon)
                  //  .setSearchBoxShown(true, false)
                    .setListener(this);
        }

        ViewGroup layoutToolbar = view.findViewById(R.id.fd_layout_toolbar);
        layoutToolbar.addView(mHelperToolbar.getView());

        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        emptyRecycle = view.findViewById(R.id.emptyRecycle);
        rcDiscovery = view.findViewById(R.id.rcDiscovery);

        rcDiscovery.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once
                rcDiscovery.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Here you can get the size :)
                if (rcDiscovery.getAdapter() instanceof DiscoveryAdapter) {
                    ((DiscoveryAdapter) rcDiscovery.getAdapter()).setWidth(rcDiscovery.getWidth());
                }
            }
        });

//        /*if (page == 0) {
//            rcDiscovery.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//
//                    int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
//
//                    //check recycler scroll for search box animation
//                    if (dy <= 0) {
//                        // Scrolling up
//                        mHelperToolbar.animateSearchBox(false, position, -2);
//                    } else {
//                        // Scrolling down
//                        mHelperToolbar.animateSearchBox(true, position, -2);
//                    }
//                }
//            });

//       }

        pullToRefresh.setOnRefreshListener(() -> {
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

        //load user avatar in toolbar
        //avatarHandler.getAvatar(new ParamWithAvatarType(mHelperToolbar.getAvatarSmall(), G.userId).avatarType(AvatarHandler.AvatarType.USER).showMain());

        rcDiscovery.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rcDiscovery.setAdapter(new DiscoveryAdapter(getActivity(), rcDiscovery.getWidth(), discoveryArrayList));
        if (discoveryArrayList == null) {
            tryToUpdateOrFetchRecycleViewData(0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setRefreshing(boolean value) {
        pullToRefresh.setRefreshing(value);
        if (value) {
            emptyRecycle.setVisibility(View.GONE);
        } else {
            if (rcDiscovery.getAdapter() != null && rcDiscovery.getAdapter().getItemCount() > 0) {
                emptyRecycle.setVisibility(View.GONE);
            } else {
                emptyRecycle.setVisibility(View.VISIBLE);
            }
        }
    }

    private void tryToUpdateOrFetchRecycleViewData(int count) {
        setRefreshing(true);
        boolean isSend = updateOrFetchRecycleViewData();

        if (!isSend && page == 0) {

            loadOfflinePageZero();

            if (count < 3) {
                G.handler.postDelayed(() -> tryToUpdateOrFetchRecycleViewData(count + 1), 1000);
            } else {
                setRefreshing(false);
            }
        } else if (!isSend) {
            setRefreshing(false);
        }
    }

    private boolean updateOrFetchRecycleViewData() {
        return new RequestClientGetDiscovery().getDiscovery(page, new OnDiscoveryList() {
            @Override
            public void onDiscoveryListReady(ArrayList<DiscoveryItem> discoveryArrayList, String title) {
                if (page == 0) {
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    SharedPreferences pref = G.context.getSharedPreferences("DiscoveryPages", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = pref.edit();
                    String cache = gson.toJson(discoveryArrayList);
                    edit.putString("page0", cache).apply();
                    edit.putString("title", title).apply();
                }
                G.handler.post(() -> {
                    setAdapterData(discoveryArrayList, title);
                    setRefreshing(false);
                });
            }

            @Override
            public void onError() {
                G.handler.post(() -> {
                    if (page == 0) {
                        loadOfflinePageZero();
                    }

                    setRefreshing(false);
                });
            }
        });
    }

    private void setAdapterData(ArrayList<DiscoveryItem> discoveryArrayList, String title) {
        this.discoveryArrayList = discoveryArrayList;
        if (rcDiscovery.getAdapter() instanceof DiscoveryAdapter) {
            ((DiscoveryAdapter) rcDiscovery.getAdapter()).setDiscoveryList(discoveryArrayList, rcDiscovery.getWidth());
            if (page != 0) mHelperToolbar.setDefaultTitle(title);
            rcDiscovery.getAdapter().notifyDataSetChanged();
        }
    }

    private void loadOfflinePageZero() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        SharedPreferences pref = G.context.getSharedPreferences("DiscoveryPages", Context.MODE_PRIVATE);
        String json = pref.getString("page0", "");
        String title = pref.getString("title", "");
        if (json != null && !json.equals("")) {
            ArrayList<DiscoveryItem> discoveryArrayList = gson.fromJson(json, new TypeToken<ArrayList<DiscoveryItem>>() {
            }.getType());
            setAdapterData(discoveryArrayList, title);
        }
    }

    @Override
    public void onLeftIconClickListener(View view) {
        popBackStackFragment();
    }

    @Override
    public void onSmallAvatarClickListener(View view) {

    }

    @Override
    public void onSearchClickListener(View view) {

    }

    @Override
    public boolean isAllowToBackPressed() {
        return true;
    }
}
