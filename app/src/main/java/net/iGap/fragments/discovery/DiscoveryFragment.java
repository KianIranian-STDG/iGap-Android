package net.iGap.fragments.discovery;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.items.discovery.DiscoveryAdapter;
import net.iGap.fragments.BaseFragment;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestClientGetDiscovery;

import java.util.ArrayList;

public class DiscoveryFragment extends BaseFragment {
    private RecyclerView rcDiscovery;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (G.fragmentActivity != null && page != 0) {
            ((ActivityMain) G.fragmentActivity).lockNavigation();
        }

        rcDiscovery = view.findViewById(R.id.rcDiscovery);
        LinearLayoutManager layoutManager = new LinearLayoutManager(G.currentActivity);
        rcDiscovery.setLayoutManager(layoutManager);

        new RequestClientGetDiscovery().getDiscovery(page, new OnDiscoveryList() {
            @Override
            public void onDiscoveryListReady(ArrayList<ProtoGlobal.Discovery> discoveryArrayList, String title) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("bagi" , discoveryArrayList.size() + "size");
                        Log.d("bagi" , title + "size");
                        DiscoveryAdapter adapterDiscovery = new DiscoveryAdapter(getActivity(), discoveryArrayList);
                        rcDiscovery.setAdapter(adapterDiscovery);
                    }
                });
            }
        });
    }

}
