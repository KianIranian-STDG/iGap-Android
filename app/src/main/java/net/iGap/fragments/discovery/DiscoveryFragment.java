package net.iGap.fragments.discovery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryAdapter;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.fragments.FragmentToolBarBack;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.request.RequestClientGetDiscovery;

import java.util.ArrayList;

public class DiscoveryFragment extends FragmentToolBarBack {
    private RecyclerView rcDiscovery;
    private TextView emptyRecycle;
    private SwipeRefreshLayout pullToRefresh;
    private int page;

    public static DiscoveryFragment newInstance(int page) {
        DiscoveryFragment discoveryFragment = new DiscoveryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page" , page);
        discoveryFragment.setArguments(bundle);
        return discoveryFragment;
    }

    public DiscoveryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        page = getArguments().getInt("page");
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        if (page == 0) {
            return view;
        } else {
            return attachToSwipeBack(view);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyRecycle = view.findViewById(R.id.emptyRecycle);

        if (page == 0) {
            appBarLayout.setVisibility(View.GONE);
        }

        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateOrFetchRecycleViewData();
            }
        });

        emptyRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new HelperFragment(FragmentWebView.newInstance("https://google.com")).setReplace(false).load(false);
                updateOrFetchRecycleViewData();
            }
        });

        RippleView rippleBackButton = (RippleView) view.findViewById(R.id.chl_ripple_back_Button);
        rippleBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard(view);
                popBackStackFragment();
            }
        });


        rcDiscovery = view.findViewById(R.id.rcDiscovery);
        LinearLayoutManager layoutManager = new LinearLayoutManager(G.currentActivity);
        rcDiscovery.setLayoutManager(layoutManager);
        updateOrFetchRecycleViewData();
    }

    private void updateOrFetchRecycleViewData() {
        pullToRefresh.setRefreshing(true);
        new RequestClientGetDiscovery().getDiscovery(page, new OnDiscoveryList() {
            @Override
            public void onDiscoveryListReady(ArrayList<DiscoveryItem> discoveryArrayList, String title) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefresh.setRefreshing(false);
                        if (page == 0) {
                            GsonBuilder builder = new GsonBuilder();
                            Gson gson = builder.create();
                            SharedPreferences pref = G.context.getSharedPreferences("DiscoveryPages", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = pref.edit();
                            String cache = gson.toJson(discoveryArrayList);
                            edit.putString("page0", cache).apply();
                            edit.putString("title", title).apply();
                        }
                        setAdapterData(discoveryArrayList, title);
                    }
                });
            }

            @Override
            public void onError() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefresh.setRefreshing(false);
                        if (page == 0) {
                            GsonBuilder builder = new GsonBuilder();
                            Gson gson = builder.create();
                            SharedPreferences pref = G.context.getSharedPreferences("DiscoveryPages", Context.MODE_PRIVATE);
                            String json = pref.getString("page0" , "");
                            String title = pref.getString("title" , "");
                            if (json != null && !json.equals("")) {
                                ArrayList<DiscoveryItem> discoveryArrayList = gson.fromJson(json, new TypeToken<ArrayList<DiscoveryItem>>(){}.getType());
                                setAdapterData(discoveryArrayList, title);
                            } else {
                                setAdapterData(new ArrayList<>(), "");
                            }

                        } else {
                            setAdapterData(new ArrayList<>(), "");
                        }
                    }
                });
            }
        });
    }

    private void setAdapterData(ArrayList<DiscoveryItem> discoveryArrayList, String title) {
        DiscoveryAdapter adapterDiscovery = new DiscoveryAdapter(getActivity(), discoveryArrayList);
        if (discoveryArrayList.size() == 0) {
            rcDiscovery.setVisibility(View.GONE);
            emptyRecycle.setVisibility(View.VISIBLE);
        } else {
            rcDiscovery.setVisibility(View.VISIBLE);
            emptyRecycle.setVisibility(View.GONE);
        }
        rcDiscovery.setAdapter(adapterDiscovery);
        titleTextView.setText(title);
    }
}
