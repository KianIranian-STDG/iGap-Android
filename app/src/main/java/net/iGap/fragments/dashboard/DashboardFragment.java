package net.iGap.fragments.dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterDashboard;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.FragmentCall;
import net.iGap.module.dashboard.DashboardModel;

import java.util.ArrayList;

public class DashboardFragment extends BaseFragment {
    public static final String OPEN_IN_FRAGMENT_MAIN = "OPEN_IN_FRAGMENT_MAIN";
    private RecyclerView rcDashboard;
    private ArrayList<DashboardModel> dashboardList = new ArrayList<>();

    public static DashboardFragment newInstance(boolean openInFragmentMain) {

        DashboardFragment dashboardFragment = new DashboardFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean(OPEN_IN_FRAGMENT_MAIN, openInFragmentMain);
        dashboardFragment.setArguments(bundle);

        return dashboardFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(G.currentActivity);
        rcDashboard = view.findViewById(R.id.rcDashboard);

        for (int i = 1; i < 7; i++) {
            DashboardModel dashboardModel = new DashboardModel();
            dashboardModel.setType(i);
            dashboardList.add(dashboardModel);
        }

        AdapterDashboard adapterDashboard = new AdapterDashboard(G.currentActivity, dashboardList);

        rcDashboard.setLayoutManager(layoutManager);
        rcDashboard.setAdapter(adapterDashboard);

    }

}
