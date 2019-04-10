package net.iGap.fragments.discovery;

import android.graphics.Color;
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
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.items.discovery.DiscoveryAdapter;
import net.iGap.fragments.BaseFragment;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.MyAppBarLayout;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestClientGetDiscovery;

import java.util.ArrayList;

public class DiscoveryFragment extends BaseFragment {
    private RecyclerView rcDiscovery;
    private TextView titleTextView;
    private MyAppBarLayout appBarLayout;
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
        page = getArguments().getInt("page");
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);
        if (page == 0) {
            return view;
        } else {
            return attachToSwipeBack(view);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (G.fragmentActivity != null && page != 0) {
            ((ActivityMain) G.fragmentActivity).lockNavigation();
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appBarLayout = view.findViewById(R.id.ac_appBarLayout);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
        if (page == 0) {
            appBarLayout.setVisibility(View.GONE);
        }

        titleTextView = view.findViewById(R.id.title);
        titleTextView.setTypeface(G.typeface_IRANSansMobile);

        SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateOrFetchRecycleViewData();
                pullToRefresh.setRefreshing(false);
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
        new RequestClientGetDiscovery().getDiscovery(page, new OnDiscoveryList() {
            @Override
            public void onDiscoveryListReady(ArrayList<ProtoGlobal.Discovery> discoveryArrayList, String title) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        DiscoveryAdapter adapterDiscovery = new DiscoveryAdapter(getActivity(), discoveryArrayList);
                        rcDiscovery.setAdapter(adapterDiscovery);
                        titleTextView.setText(title);
                    }
                });
            }
        });
    }

}
