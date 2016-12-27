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
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.items.AdapterActiveSessions;
import com.iGap.adapter.items.chat.AdapterActiveSessionsHeader;
import com.iGap.interfaces.OnUserSessionGetActiveList;
import com.iGap.interfaces.OnUserSessionLogout;
import com.iGap.interfaces.OnUserSessionTerminate;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.SUID;
import com.iGap.module.StructSessionsGetActiveList;
import com.iGap.proto.ProtoUserSessionGetActiveList;
import com.iGap.request.RequestUserSessionGetActiveList;
import com.iGap.request.RequestUserSessionLogout;
import com.iGap.request.RequestUserSessionTerminate;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

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
    private boolean isClearAdapter = false;

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

                getActivity().getSupportFragmentManager().popBackStack();
//                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentActiveSessions.this).commit();
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

                            } else {
                                structItems.add(item);
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

                G.onUserSessionTerminate = new OnUserSessionTerminate() {
                    @Override
                    public void onUserSessionTerminate() {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWaiting.setVisibility(View.GONE);

                                if (!isClearAdapter) {
                                    fastItemAdapter.remove(position);
                                    isClearAdapter = true;
                                }
                                fastItemAdapter.remove(position);
                            }
                        });
                    }

                    @Override
                    public void onTimeOut() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWaiting.setVisibility(View.GONE);
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
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                prgWaiting.setVisibility(View.GONE);
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

                if (item instanceof AdapterActiveSessions) {
                    if (((AdapterActiveSessions) item).getItem().isCurrent()) {
                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.active_session_title)
                                .content(R.string.active_session_content)
                                .positiveText(R.string.B_ok)
                                .negativeText(R.string.B_cancel)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        prgWaiting.setVisibility(View.VISIBLE);
                                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        G.onUserSessionLogout = new OnUserSessionLogout() {
                                            @Override
                                            public void onUserSessionLogout() {

                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                        prgWaiting.setVisibility(View.GONE);
                                                    }
                                                });


                                            }

                                            @Override
                                            public void onError() {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                        prgWaiting.setVisibility(View.GONE);
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
                                            public void onTimeOut() {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                        prgWaiting.setVisibility(View.GONE);
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
                                        };

                                        new RequestUserSessionLogout().userSessionLogout();
                                    }
                                }).show();
                    } else {
                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.active_session_title)
                                .content(R.string.active_session_content)
                                .positiveText(R.string.B_ok)
                                .negativeText(R.string.B_cancel)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        prgWaiting.setVisibility(View.VISIBLE);
                                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                        new RequestUserSessionTerminate().userSessionTerminate(((AdapterActiveSessions) item).getItem().getSessionId());
                                    }
                                }).show();
                    }
                } else {
                    v.setVisibility(View.GONE);
                    if (((AdapterActiveSessionsHeader) item).getItem().size() > 0) {
                        for (int i = 0; i < ((AdapterActiveSessionsHeader) item).getItem().size(); i++) {
                            if (!((AdapterActiveSessionsHeader) item).getItem().get(i).isCurrent()) {
                                new RequestUserSessionTerminate().userSessionTerminate(((AdapterActiveSessionsHeader) item).getItem().get(i).getSessionId());
                            }
                        }
                    }
                }
                return false;
            }
        });

        G.onUserSessionTerminate = new OnUserSessionTerminate() {
            @Override
            public void onUserSessionTerminate() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        prgWaiting.setVisibility(View.GONE);
                        fastItemAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onTimeOut() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        prgWaiting.setVisibility(View.GONE);
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
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        prgWaiting.setVisibility(View.GONE);
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

    public void itemAdapter() {
        boolean b = false;

        for (StructSessionsGetActiveList s : structItems) {

            if (s.isCurrent()) {
                fastItemAdapter.add(new AdapterActiveSessions(s).withIdentifier(SUID.id().get()));
            } else if (!s.isCurrent() && !b) {
                fastItemAdapter.add(new AdapterActiveSessionsHeader(structItems).withIdentifier(SUID.id().get()));
                fastItemAdapter.add(new AdapterActiveSessions(s).withIdentifier(SUID.id().get()));
                b = true;
            } else if (!s.isCurrent() && b) {
                fastItemAdapter.add(new AdapterActiveSessions(s).withIdentifier(SUID.id().get()));
            }
        }

        prgWaiting.setVisibility(View.GONE);


    }
}
