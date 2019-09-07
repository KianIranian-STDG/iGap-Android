package net.iGap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.DataUsageAdapter;
import net.iGap.helper.HelperDataUsage;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.DataUsageListener;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.structs.DataUsageStruct;
import net.iGap.realm.RealmDataUsage;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class FragmentDataUsage extends Fragment implements DataUsageListener {

    private ArrayList<DataUsageStruct> usageArrayList = new ArrayList<>();
    private long totalSendByte;
    private long totalReceivedByte;
    private boolean type;
    private DataUsageAdapter adapter;
    private HelperToolbar mHelperToolbar;

    public static FragmentDataUsage newInstance() {
        return new FragmentDataUsage();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_data_usage, container, false);


        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        getActivity().onBackPressed();

                    }
                });

        ViewGroup layoutToolbar = view.findViewById(R.id.fdu_layout_toolbar);
        layoutToolbar.addView(mHelperToolbar.getView());

        type = getArguments().getBoolean("TYPE", false);

        initData(type);

        usageArrayList.add(new DataUsageStruct(1, 0, 0, 0, 0, "Total"));
        usageArrayList.add(new DataUsageStruct(2, 0, 0, 0, 0, "ClearData"));


        return view;
    }

    private void initData(boolean type) {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<RealmDataUsage> wifiRealmDataUsages;
            RealmResults<RealmDataUsage> dataRealmDataUsages;
            if (type) {
                mHelperToolbar.setDefaultTitle(getResources().getString(R.string.wifi_data_usage));
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
                mHelperToolbar.setDefaultTitle(getResources().getString(R.string.mobile_data_usage));
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
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rcDataUsage = view.findViewById(R.id.rcDataUsage);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(G.currentActivity);
        adapter = new DataUsageAdapter(G.currentActivity, usageArrayList, totalReceivedByte, totalSendByte, type, this);
        rcDataUsage.setAdapter(adapter);
        rcDataUsage.setLayoutManager(layoutManager);


    }

    @Override
    public void doClearDB(boolean type) {
        HelperDataUsage.clearUsageRealm(type);
        usageArrayList.clear();
        initData(type);
        adapter.notifyDataSetChanged();
    }
}
