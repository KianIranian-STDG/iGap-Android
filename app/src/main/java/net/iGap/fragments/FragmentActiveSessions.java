/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.AdapterActiveSessions;
import net.iGap.adapter.items.chat.AdapterActiveSessionsHeader;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.OnUserSessionTerminate;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.module.AppUtils;
import net.iGap.module.SUID;
import net.iGap.module.structs.StructSessions;
import net.iGap.request.RequestUserSessionGetActiveList;
import net.iGap.request.RequestUserSessionTerminate;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentActiveSessions extends BaseFragment {

    private RecyclerView rcvContent;
    private List<StructSessions> structItems = new ArrayList<>();
    private ProgressBar prgWaiting;
    private FastItemAdapter fastItemAdapter;
    private List<StructSessions> list = new ArrayList<>();

    public FragmentActiveSessions() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_active_sessions, container, false));
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setDefaultTitle(getString(R.string.Active_Sessions))
                .setLeftIcon(R.string.icon_back)
                .setLogoShown(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        removeFromBaseFragment(FragmentActiveSessions.this);
                    }
                });

        ViewGroup layoutToolbar = view.findViewById(R.id.fas_layout_toolbar);
        layoutToolbar.addView(toolbar.getView());

        prgWaiting = view.findViewById(R.id.stas_prgWaiting);
        AppUtils.setProgresColler(prgWaiting);

        prgWaiting.setVisibility(View.VISIBLE);

        fastItemAdapter = new FastItemAdapter();
        rcvContent = view.findViewById(R.id.stas_rcvContent);
        rcvContent.setLayoutManager(new LinearLayoutManager(rcvContent.getContext()));
        rcvContent.setItemAnimator(new DefaultItemAnimator());
        rcvContent.setAdapter(fastItemAdapter);

        G.onUserSessionGetActiveList = session -> G.handler.post(() -> {

            for (int i = 0; i < session.size(); i++) {

                StructSessions item = new StructSessions();
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
                } else {
                    structItems.add(item);
                }

                list.add(item);
            }

            itemAdapter();
        });
        new RequestUserSessionGetActiveList().userSessionGetActiveList();

        G.onUserSessionTerminate = new OnUserSessionTerminate() {

            @Override
            public void onUserSessionTerminate(final Long messageId) {
                G.handler.post(() -> {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    prgWaiting.setVisibility(View.GONE);

                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getSessionId() == messageId) {
                            int j = fastItemAdapter.getPosition(list.get(i).getSessionId());
                            if (j >= 0) {
                                fastItemAdapter.remove(j);
                                list.remove(i);
                            }
                        }
                    }
                });
            }

            @Override
            public void onTimeOut() {
                G.handler.post(() -> {

                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    prgWaiting.setVisibility(View.GONE);

                    HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.error), false);
                });
            }

            @Override
            public void onError() {
                G.handler.post(() -> {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    prgWaiting.setVisibility(View.GONE);
                    HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.error), false);
                });
            }
        };

        fastItemAdapter.withSelectable(true);
        fastItemAdapter.withOnClickListener((v, adapter, item, position) -> {

            if (item instanceof AdapterActiveSessions) {
                if (((AdapterActiveSessions) item).getItem().isCurrent()) {

                } else {
                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.active_session_title).content(R.string.active_session_content).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            prgWaiting.setVisibility(View.VISIBLE);
                            G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            new RequestUserSessionTerminate().userSessionTerminate(((AdapterActiveSessions) item).getItem().getSessionId());
                        }
                    }).show();
                }
            } else {
                final int size = list.size();
                if (size > 1) {
                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.active_session_all_title).content(R.string.active_session_all_content).positiveText(R.string.yes).negativeText(R.string.no).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            v.setVisibility(View.GONE); // click on AdapterActiveSessionsHeader

                            for (int i = 0; i < size; i++) {
                                if (!list.get(i).isCurrent()) {
                                    new RequestUserSessionTerminate().userSessionTerminate(list.get(i).getSessionId());
                                }
                            }
                        }
                    }).show();
                }
            }
            return false;
        });
    }

    private void itemAdapter() {
        boolean b = false;

        for (StructSessions s : structItems) {

            if (s.isCurrent()) {
                fastItemAdapter.add(new AdapterActiveSessions(s).withIdentifier(s.getSessionId()));
            } else if (!b) {
                fastItemAdapter.add(new AdapterActiveSessionsHeader(structItems).withIdentifier(SUID.id().get()));
                fastItemAdapter.add(new AdapterActiveSessions(s).withIdentifier(s.getSessionId()));
                b = true;
            } else if (b) {
                fastItemAdapter.add(new AdapterActiveSessions(s).withIdentifier(s.getSessionId()));
            }
        }
        rcvContent.addItemDecoration(new DividerItemDecoration(rcvContent.getContext(), LinearLayoutManager.VERTICAL), /*fastItemAdapter.getAdapterItemCount() > 2 ? 2 :*/ 0);
        prgWaiting.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        G.onUserSessionGetActiveList = null;
        G.onUserSessionTerminate = null;
    }
}
