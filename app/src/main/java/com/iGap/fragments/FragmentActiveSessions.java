package com.iGap.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.items.AdapterActiveSessions;
import com.iGap.interfaces.OnUserSessionGetActiveList;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.StructSessionsGetActiveList;
import com.iGap.proto.ProtoUserSessionGetActiveList;
import com.iGap.request.RequestUserSessionGetActiveList;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentActiveSessions extends Fragment {

    private FastAdapter fastAdapter;
    private RecyclerView rcvContent;
    private List<StructSessionsGetActiveList> itemSessionsGetActiveList = new ArrayList<>();
    private List<IItem> items = new ArrayList<>();

    public FragmentActiveSessions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_sessions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.stas_txt_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.stas_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        ViewGroup root = (ViewGroup) view.findViewById(R.id.stas_rootActiveSession);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        fastAdapter = new FastAdapter();
        fastAdapter.withSelectable(true);
        final FastItemAdapter fastItemAdapter = new FastItemAdapter();

        new RequestUserSessionGetActiveList().userSessionGetActiveList();

        rcvContent = (RecyclerView) view.findViewById(R.id.stas_rcvContent);
        rcvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvContent.setItemAnimator(new DefaultItemAnimator());
        rcvContent.setAdapter(fastItemAdapter);

        G.onUserSessionGetActiveList = new OnUserSessionGetActiveList() {
            @Override
            public void onUserSessionGetActiveList(List<ProtoUserSessionGetActiveList.UserSessionGetActiveListResponse.Session> session) {

                for (int i = 0; i < session.size(); i++) {

                    StructSessionsGetActiveList item = new StructSessionsGetActiveList();

                    item.setSessionId(session.get(i).getSessionId());
                    item.setName(session.get(i).getAppName());
                    item.setAppId(session.get(i).getAppId());
                    item.setBuildVersion(session.get(i).getAppBuildVersion());
                    item.setAppVersion(session.get(i).getAppVersion());
                    item.setPlatform(session.get(i).getPlatform());
                    item.setPlatformVersion(session.get(i).getPlatformVersion());
                    item.setDevice(session.get(i).getDevice());
                    item.setDeviceName(session.get(i).getDeviceName());
                    item.setLanguage(session.get(i).getLanguage());
                    item.setCountry(session.get(i).getCountry());
                    item.setCurrent(session.get(i).getCurrent());
                    item.setCreateTime(session.get(i).getCreateTime());
                    item.setActiveTime(session.get(i).getActiveTime());
                    item.setIp(session.get(i).getIp());

                    items.add(new AdapterActiveSessions(item).withIdentifier(100 + items.indexOf(item)));

                    Log.i("CCCCCCC", "onUserSessionGetActiveList: " + item.getAppId());


                }

            }
        };
        fastItemAdapter.notifyAdapterDataSetChanged();
        fastItemAdapter.add(items);
    }
}
