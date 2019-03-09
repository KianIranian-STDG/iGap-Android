package net.iGap.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.AdapterDashboard;
import net.iGap.module.dashboard.DashboardModel;

import java.util.ArrayList;

public class ActivityDashboard extends AppCompatActivity {
    private RecyclerView rcDashboard;
    private ArrayList<DashboardModel> dashboardList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rcDashboard = findViewById(R.id.rcDashboard);

        for (int i = 1; i < 7; i++) {
            DashboardModel dashboardModel = new DashboardModel();
            dashboardModel.setType(i);
            dashboardList.add(dashboardModel);
        }

        AdapterDashboard adapterDashboard= new AdapterDashboard(this,dashboardList);

        rcDashboard.setLayoutManager(layoutManager);
        rcDashboard.setAdapter(adapterDashboard);

    }
}
