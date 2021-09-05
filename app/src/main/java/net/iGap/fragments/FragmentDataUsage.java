package net.iGap.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.DataUsageAdapter;
import net.iGap.helper.HelperDataUsage;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.structs.DataUsageStruct;
import net.iGap.observers.interfaces.DataUsageListener;
import net.iGap.realm.RealmDataUsage;

import java.util.ArrayList;

import io.realm.RealmResults;

public class FragmentDataUsage extends Fragment implements DataUsageListener {

    private ArrayList<DataUsageStruct> usageArrayList = new ArrayList<>();
    private long totalSendByte;
    private long totalReceivedByte;
    private boolean type;
    private DataUsageAdapter adapter;
    private Toolbar dataUsageToolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data_usage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataUsageToolbar = new Toolbar(getContext());
        dataUsageToolbar.setBackIcon(new BackDrawable(false));
        dataUsageToolbar.setListener(i -> {
            switch (i) {
                case -1:
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                    break;
            }
        });



        ViewGroup layoutToolbar = view.findViewById(R.id.fdu_layout_toolbar);
        layoutToolbar.addView(dataUsageToolbar, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.dp(56), Gravity.TOP));

        type = getArguments().getBoolean("TYPE", false);

        initData(type);

        usageArrayList.add(new DataUsageStruct(1, 0, 0, 0, 0, "Total"));
        usageArrayList.add(new DataUsageStruct(2, 0, 0, 0, 0, "ClearData"));

        RecyclerView rcDataUsage = view.findViewById(R.id.rcDataUsage);
        adapter = new DataUsageAdapter(usageArrayList, totalReceivedByte, totalSendByte, type, this);
        rcDataUsage.setAdapter(adapter);

    }

    private void initData(boolean type) {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmResults<RealmDataUsage> wifiRealmDataUsages;
            RealmResults<RealmDataUsage> dataRealmDataUsages;
            if (type) {
                dataUsageToolbar.setTitle(getString(R.string.wifi_data_usage));
                totalReceivedByte = 0;
                totalSendByte = 0;
                wifiRealmDataUsages = realm.where(RealmDataUsage.class).equalTo("connectivityType", true).findAll();
                if (wifiRealmDataUsages.size() == 0)
                    wifiRealmDataUsages = realm.where(RealmDataUsage.class).findAll();

                for (RealmDataUsage usage : wifiRealmDataUsages) {
                    usageArrayList.add(new DataUsageStruct(0, usage.getDownloadSize(), usage.getUploadSize(), usage.getNumUploadedFiles(), usage.getNumDownloadedFile(), usage.getType()));
                    totalReceivedByte += usage.getDownloadSize();
                    totalSendByte += usage.getUploadSize();
                }

            } else {
                dataUsageToolbar.setTitle(getResources().getString(R.string.mobile_data_usage));
                totalReceivedByte = 0;
                totalSendByte = 0;
                dataRealmDataUsages = realm.where(RealmDataUsage.class).equalTo("connectivityType", false).findAll();
                if (dataRealmDataUsages.size() == 0)
                    dataRealmDataUsages = realm.where(RealmDataUsage.class).findAll();
                for (RealmDataUsage usage : dataRealmDataUsages) {
                    usageArrayList.add(new DataUsageStruct(0, usage.getDownloadSize(), usage.getUploadSize(), usage.getNumUploadedFiles(), usage.getNumDownloadedFile(), usage.getType()));
                    totalReceivedByte += usage.getDownloadSize();
                    totalSendByte += usage.getUploadSize();
                }
            }
        });
    }

    @Override
    public void doClearDB(boolean type) {
        HelperDataUsage.clearUsageRealm(type);
        usageArrayList.clear();
        initData(type);
        adapter.notifyDataSetChanged();
    }
}
