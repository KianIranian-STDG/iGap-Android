package com.iGap.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.items.AdapterActiveSessions;
import com.iGap.adapter.items.chat.AdapterActiveSessionsHeader;
import com.iGap.interfaces.OnUserSessionGetActiveList;
import com.iGap.interfaces.OnUserSessionTerminate;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.SUID;
import com.iGap.module.StructSessionsGetActiveList;
import com.iGap.proto.ProtoUserSessionGetActiveList;
import com.iGap.request.RequestUserSessionGetActiveList;
import com.iGap.request.RequestUserSessionTerminate;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
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
    private List<StructSessionsGetActiveList> structItems = new ArrayList<>();
    private List<IItem> items = new ArrayList<>();
    private ProgressBar prgWaiting;
    private FastItemAdapter fastItemAdapter;

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

        prgWaiting = (ProgressBar) view.findViewById(R.id.stas_prgWaiting);
        prgWaiting.setVisibility(View.VISIBLE);
        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.stas_txt_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.stas_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentActiveSessions.this).commit();
            }
        });

        ViewGroup root = (ViewGroup) view.findViewById(R.id.stas_rootActiveSession);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fastItemAdapter = new FastItemAdapter();
        rcvContent = (RecyclerView) view.findViewById(R.id.stas_rcvContent);
        rcvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvContent.setItemAnimator(new DefaultItemAnimator());
        rcvContent.setAdapter(fastItemAdapter);

        new RequestUserSessionGetActiveList().userSessionGetActiveList();
        G.onUserSessionGetActiveList = new OnUserSessionGetActiveList() {
            @Override
            public void onUserSessionGetActiveList(final List<ProtoUserSessionGetActiveList.UserSessionGetActiveListResponse.Session> session) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

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


                            if (item.isCurrent()) {
                                structItems.add(0, item);
//                                items.add(0,new AdapterActiveSessions(item).withIdentifier(100 + i));
//                                items.add(new AdapterActiveSessionsHeader().withIdentifier(10000 + i));
                            } else {
                                structItems.add(item);
//                                items.add(new AdapterActiveSessions(item).withIdentifier(100 + i));
                            }

                        }

                        itemAdapter();

                    }
                });
            }
        };

        fastItemAdapter.withSelectable(true);
        fastItemAdapter.withOnClickListener(new FastAdapter.OnClickListener() {
            @Override
            public boolean onClick(View v, IAdapter adapter, final IItem item, final int position) {

                if (((AdapterActiveSessions) item).getItem().isCurrent()) {

                } else {
                    if (item instanceof AdapterActiveSessions) {
                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.active_session_title)
                                .content(R.string.active_session_content)
                                .positiveText(R.string.B_ok)
                                .negativeText(R.string.B_cancel)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        new RequestUserSessionTerminate().userSessionTerminate(((AdapterActiveSessions) item).getItem().getSessionId());
                                        G.onUserSessionTerminate = new OnUserSessionTerminate() {
                                            @Override
                                            public void onUserSessionTerminate() {

                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        fastItemAdapter.remove(position);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onTimeOut() {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                                R.string.error,
                                                                Snackbar.LENGTH_LONG);
                                                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                snack.dismiss();
                                                            }
                                                        });
                                                        snack.show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError() {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                                getString(R.string.error),
                                                                Snackbar.LENGTH_LONG);
                                                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                snack.dismiss();
                                                            }
                                                        });
                                                        snack.show();
                                                    }
                                                });
                                            }
                                        };
                                    }
                                }).show();
                    } else {
//                        Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
                    }


                }
                return false;
            }
        });

    }

    public void itemAdapter() {
        boolean b = false;

        for (StructSessionsGetActiveList s : structItems) {

            if (s.isCurrent()) {
                fastItemAdapter.add(new AdapterActiveSessions(s).withIdentifier(SUID.id().get()));
            } else if (!s.isCurrent() && !b) {
                fastItemAdapter.add(new AdapterActiveSessionsHeader().withIdentifier(SUID.id().get()));
                fastItemAdapter.add(new AdapterActiveSessions(s).withIdentifier(SUID.id().get()));
                b = true;
            } else if (!s.isCurrent() && b) {
                fastItemAdapter.add(new AdapterActiveSessions(s).withIdentifier(SUID.id().get()));
            }
        }

        prgWaiting.setVisibility(View.GONE);


    }
}
