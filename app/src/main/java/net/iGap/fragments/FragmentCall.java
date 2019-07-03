package net.iGap.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.dialog.topsheet.TopSheetDialog;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.ISignalingGetCallLog;
import net.iGap.interfaces.OnCallLogClear;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.TimeUtils;
import net.iGap.proto.ProtoSignalingGetLog;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmCallLog;
import net.iGap.realm.RealmCallLogFields;
import net.iGap.request.RequestSignalingClearLog;
import net.iGap.request.RequestSignalingGetLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

import static net.iGap.proto.ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class FragmentCall extends BaseFragment implements OnCallLogClear, ToolbarListener {

    public static final String OPEN_IN_FRAGMENT_MAIN = "OPEN_IN_FRAGMENT_MAIN";
    public FloatingActionButton fabContactList;
    boolean openInMain = false;
    boolean isSendRequestForLoading = false;
    boolean isThereAnyMoreItemToLoad = true;
    ProgressBar progressBar;
    boolean canclick = false;
    int move = 0;
    private int mOffset = 0;
    private int mLimit = 50;
    private RecyclerView.OnScrollListener onScrollListener;
    private ImageView imgCallEmpty;
    private TextView empty_call;
    private int attampOnError = 0;
    private RecyclerView mRecyclerView;
    //private CallAdapterA mAdapter;
    private ProtoSignalingGetLog.SignalingGetLog.Filter mSelectedStatus = ProtoSignalingGetLog.SignalingGetLog.Filter.ALL;

    private TextView mBtnAllCalls, mBtnMissedCalls , mBtnIncomingCalls , mBtnOutgoingCalls , mBtnCanceledCalls;
    private HelperToolbar mHelperToolbar;
    private RealmResults<RealmCallLog> realmResults ;
    private CallAdapter callAdapter;
    private Realm mRealm;


    public static FragmentCall newInstance(boolean openInFragmentMain) {

        FragmentCall fragmentCall = new FragmentCall();

        Bundle bundle = new Bundle();
        bundle.putBoolean(OPEN_IN_FRAGMENT_MAIN, openInFragmentMain);
        fragmentCall.setArguments(bundle);

        return fragmentCall;
    }



    private boolean isInit = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isInit) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    init();
                }
            }, 800);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RealmCallLog.manageClearCallLog();
        openInMain = getArguments().getBoolean(OPEN_IN_FRAGMENT_MAIN);
        if (openInMain) {
            return inflater.inflate(R.layout.fragment_call, container, false);
        }
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_call, container, false));
    }

    private View view;

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        addToolbar();
        init();
    }

    private void addToolbar() {


        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.edit_icon)
                .setRightIcons(R.string.add_icon)
                .setLogoShown(true)
                .setListener(this);

        ViewGroup layoutToolbar = view.findViewById(R.id.fc_layout_toolbar);
        layoutToolbar.addView(mHelperToolbar.getView());

    }

    private void init() {
        if (view == null) {
            return;
        }

        mBtnAllCalls = view.findViewById(R.id.fc_btn_all_calls);
        mBtnMissedCalls = view.findViewById(R.id.fc_btn_missed_calls);
        mBtnCanceledCalls = view.findViewById(R.id.fc_btn_canceled_calls);
        mBtnIncomingCalls = view.findViewById(R.id.fc_btn_incoming_calls);
        mBtnOutgoingCalls = view.findViewById(R.id.fc_btn_outgoing_calls);

        setEnableButton(mBtnAllCalls , mBtnMissedCalls , mBtnIncomingCalls , mBtnOutgoingCalls , mBtnCanceledCalls);


        fabContactList = (FloatingActionButton) view.findViewById(R.id.fc_fab_contact_list);
        fabContactList.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));

        if (!getUserVisibleHint()) {
            if (!isInit) {
                view.findViewById(R.id.fc_layot_title).setVisibility(View.GONE);
                view.findViewById(R.id.empty_layout).setVisibility(View.GONE);
                view.findViewById(R.id.pb_load).setVisibility(View.VISIBLE);
                fabContactList.hide();
            }
            return;
        }

        isInit = true;
        view.findViewById(R.id.pb_load).setVisibility(View.GONE);
        view.findViewById(R.id.fc_layot_title).setVisibility(View.VISIBLE);
        view.findViewById(R.id.empty_layout).setVisibility(View.VISIBLE);
        fabContactList.show();

        //G.onCallLogClear = this;
        //openInMain = getArguments().getBoolean(OPEN_IN_FRAGMENT_MAIN);

        view.findViewById(R.id.fc_layot_title).setBackgroundColor(Color.parseColor(G.appBarColor));  //set title bar color


        imgCallEmpty = (AppCompatImageView) view.findViewById(R.id.img_icCall);
        empty_call = (TextView) view.findViewById(R.id.textEmptyCal);
        progressBar = (ProgressBar) view.findViewById(R.id.fc_progress_bar_waiting);


        RippleView rippleBack = (RippleView) view.findViewById(R.id.fc_call_ripple_txtBack);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                G.fragmentActivity.onBackPressed();
            }
        });

        MaterialDesignTextView txtMenu = (MaterialDesignTextView) view.findViewById(R.id.fc_btn_menu);

        txtMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogMenu();
            }
        });


        mRecyclerView = view.findViewById(R.id.fc_recycler_view_call);
        mRecyclerView.setItemAnimator(null);
        LinearLayoutManager linearVertical = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearVertical);


        Realm realm = Realm.getDefaultInstance();
        realmResults = getRealmResult(mSelectedStatus , realm);
        realm.close();

        checkListIsEmpty();

        callAdapter = new CallAdapter(realmResults);
        mRecyclerView.setAdapter(callAdapter);

        getLogListWithOffset();

        callAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (realmResults.size() > 0) {
                    imgCallEmpty.setVisibility(View.GONE);
                    empty_call.setVisibility(View.GONE);

                } else {
                    imgCallEmpty.setVisibility(View.VISIBLE);
                    empty_call.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if (realmResults.size() > 0) {
                    imgCallEmpty.setVisibility(View.GONE);
                    empty_call.setVisibility(View.GONE);

                } else {
                    imgCallEmpty.setVisibility(View.VISIBLE);
                    empty_call.setVisibility(View.VISIBLE);
                }
            }

        });

        onScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isThereAnyMoreItemToLoad) {
                    if (!isSendRequestForLoading) {

                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                        if (lastVisiblePosition + 15 >= mOffset) {
                            getLogListWithOffset();
                        }
                    }
                }
            }
        };

        mRecyclerView.addOnScrollListener(onScrollListener);

        G.iSignalingGetCallLog = new ISignalingGetCallLog() {
            @Override
            public void onGetList(final int size, final List<ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog> signalingLogList) {

                if (signalingLogList != null) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Realm realm = Realm.getDefaultInstance();
                            //realm.executeTransaction(new Realm.Transaction() {
                            //    @Override
                            //    public void execute(Realm realm) {
                            //        for (ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog callLog : signalingLogList) {
                            //            RealmCallLog realmCallLog = realm.where(RealmCallLog.class).equalTo(RealmCallLogFields.ID, callLog.getId()).findFirst();
                            //            if (realmCallLog != null && mAdapter.getPosition(callLog.getId()) == -1) {
                            //                if (imgCallEmpty != null && imgCallEmpty.getVisibility() == View.VISIBLE) {
                            //                    imgCallEmpty.setVisibility(View.GONE);
                            //                    empty_call.setVisibility(View.GONE);
                            //                }
                            //                mAdapter.add(0, new CallItem().setInfo(realmCallLog).withIdentifier(callLog.getId()));
                            //            }
                            //        }
                            //    }
                            //});
                            //realm.close();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

                if (size == -1) {

                    if (attampOnError < 2) {
                        isSendRequestForLoading = false;
                        attampOnError++;
                    } else {
                        isThereAnyMoreItemToLoad = false;
                        mRecyclerView.removeOnScrollListener(onScrollListener);
                    }
                } else if (size == 0) {
                    isThereAnyMoreItemToLoad = false;
                    mRecyclerView.removeOnScrollListener(onScrollListener);
                } else {
                    isSendRequestForLoading = false;
                    mOffset += size;
                }
            }
        };

        fabContactList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showContactListForCall();
            }
        });


        if (openInMain) {

            fabContactList.hide();

            view.findViewById(R.id.fc_layot_title).setVisibility(View.GONE);

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) throws ClassCastException {
                    super.onScrolled(recyclerView, dx, dy);

                    if (((ActivityMain) G.fragmentActivity).arcMenu.isMenuOpened()) {
                        ((ActivityMain) G.fragmentActivity).arcMenu.toggleMenu();
                    }

                    if (dy > 0) {
                        // Scroll Down
                        if (((ActivityMain) G.fragmentActivity).arcMenu.fabMenu.isShown()) {
                            ((ActivityMain) G.fragmentActivity).arcMenu.fabMenu.hide();
                        }
                    } else if (dy < 0) {
                        // Scroll Up
                        if (!((ActivityMain) G.fragmentActivity).arcMenu.fabMenu.isShown()) {
                            ((ActivityMain) G.fragmentActivity).arcMenu.fabMenu.show();
                        }
                    }
                }
            });

        }


        mBtnAllCalls.setOnClickListener(v -> {

            if (mSelectedStatus != ProtoSignalingGetLog.SignalingGetLog.Filter.ALL) {
                setEnableButton(mBtnAllCalls, mBtnMissedCalls, mBtnIncomingCalls, mBtnOutgoingCalls, mBtnCanceledCalls);
                getCallLogsFromRealm(ProtoSignalingGetLog.SignalingGetLog.Filter.ALL);
            }

        });

        mBtnMissedCalls.setOnClickListener(v -> {

            if (mSelectedStatus != ProtoSignalingGetLog.SignalingGetLog.Filter.MISSED) {
                setEnableButton(mBtnMissedCalls, mBtnAllCalls, mBtnIncomingCalls, mBtnOutgoingCalls, mBtnCanceledCalls);
                getCallLogsFromRealm(ProtoSignalingGetLog.SignalingGetLog.Filter.MISSED);
            }

        });

        mBtnOutgoingCalls.setOnClickListener(v -> {

            if (mSelectedStatus != ProtoSignalingGetLog.SignalingGetLog.Filter.OUTGOING) {
                setEnableButton(mBtnOutgoingCalls , mBtnMissedCalls, mBtnAllCalls, mBtnIncomingCalls, mBtnCanceledCalls);
                getCallLogsFromRealm(ProtoSignalingGetLog.SignalingGetLog.Filter.OUTGOING);
            }

        });

        mBtnIncomingCalls.setOnClickListener(v -> {

            if (mSelectedStatus != ProtoSignalingGetLog.SignalingGetLog.Filter.INCOMING) {
                setEnableButton(mBtnIncomingCalls , mBtnMissedCalls, mBtnAllCalls, mBtnOutgoingCalls, mBtnCanceledCalls);
                getCallLogsFromRealm(ProtoSignalingGetLog.SignalingGetLog.Filter.INCOMING);
            }

        });

        mBtnCanceledCalls.setOnClickListener(v -> {

            if (mSelectedStatus != ProtoSignalingGetLog.SignalingGetLog.Filter.CANCELED) {
                setEnableButton(mBtnCanceledCalls , mBtnMissedCalls, mBtnAllCalls, mBtnIncomingCalls, mBtnOutgoingCalls);
                getCallLogsFromRealm(ProtoSignalingGetLog.SignalingGetLog.Filter.CANCELED);
            }

        });
    }

    private void checkListIsEmpty() {
        if (realmResults.size() > 0) {
            imgCallEmpty.setVisibility(View.GONE);
            empty_call.setVisibility(View.GONE);

        } else {
            imgCallEmpty.setVisibility(View.VISIBLE);
            empty_call.setVisibility(View.VISIBLE);
        }
    }

    private void getCallLogsFromRealm(ProtoSignalingGetLog.SignalingGetLog.Filter filter) {
        if (realmResults != null) realmResults.removeAllChangeListeners();
        mSelectedStatus = filter;
        Realm realm = Realm.getDefaultInstance();
        realmResults = getRealmResult(mSelectedStatus , realm);
        realm.close();

        callAdapter = new CallAdapter(realmResults);
        mRecyclerView.setAdapter(callAdapter);

        checkListIsEmpty();
    }

    private void setEnableButton(View enable, View disable , View disable2 , View disable3 , View disable4) {

        //use revert for dark theme : disable drawable is light and enable drawable is dark
        if (G.isDarkTheme){
            enable.setBackground(G.context.getResources().getDrawable(R.drawable.round_button_disabled_bg));
            disable.setBackground(G.context.getResources().getDrawable(R.drawable.round_button_enabled_bg));
            disable2.setBackground(G.context.getResources().getDrawable(R.drawable.round_button_enabled_bg));
            disable3.setBackground(G.context.getResources().getDrawable(R.drawable.round_button_enabled_bg));
            disable4.setBackground(G.context.getResources().getDrawable(R.drawable.round_button_enabled_bg));
        }else {
            enable.setBackground(G.context.getResources().getDrawable(R.drawable.round_button_enabled_bg));
            disable.setBackground(G.context.getResources().getDrawable(R.drawable.round_button_disabled_bg));
            disable2.setBackground(G.context.getResources().getDrawable(R.drawable.round_button_disabled_bg));
            disable3.setBackground(G.context.getResources().getDrawable(R.drawable.round_button_disabled_bg));
            disable4.setBackground(G.context.getResources().getDrawable(R.drawable.round_button_disabled_bg));
        }
    }


    private Realm getRealm() {
        if (mRealm == null || mRealm.isClosed()) {

            mRealm = Realm.getDefaultInstance();
        }

        return mRealm;
    }

    private RealmResults<RealmCallLog> getRealmResult(ProtoSignalingGetLog.SignalingGetLog.Filter status , Realm realm) {

        switch (status){
            case ALL:
                return realm.where(RealmCallLog.class).findAll().sort(RealmCallLogFields.OFFER_TIME, Sort.DESCENDING);

            case MISSED :
            case OUTGOING:
            case CANCELED:
            case INCOMING:
                return realm.where(RealmCallLog.class).equalTo(RealmCallLogFields.STATUS , status.name()).findAll().sort(RealmCallLogFields.OFFER_TIME, Sort.DESCENDING);

            default:
                return realm.where(RealmCallLog.class).findAll().sort(RealmCallLogFields.OFFER_TIME, Sort.DESCENDING);
        }

    }

    public void showContactListForCall() {
        try {
            if (getActivity() != null) {
                final Fragment fragment = RegisteredContactsFragment.newInstance(true,true,RegisteredContactsFragment.CALL);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void getLogListWithOffset() {

        if (G.isSecure && G.userLogin) {
            isSendRequestForLoading = true;
            new RequestSignalingGetLog().signalingGetLog(mOffset, mLimit , mSelectedStatus);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getLogListWithOffset();
                }
            }, 1000);
        }

    }

    @Override
    public void onLeftIconClickListener(View view) {
        openDialogMenu();
    }

    @Override
    public void onRightIconClickListener(View view) {

        showContactListForCall();
    }

    //*************************************************************************************************************

    public void openDialogMenu() {
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.clean_log));
        TopSheetDialog topSheetDialog = new TopSheetDialog(getContext()).setListData(items, -1, position -> {
            if (G.userLogin) {
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.clean_log).content(R.string.are_you_sure_clear_call_logs).
                        positiveText(R.string.B_ok).onPositive((dialog, which) -> {
                    Realm realm = Realm.getDefaultInstance();
                    try {
                        RealmCallLog realmCallLog = realm.where(RealmCallLog.class).findAll().sort(RealmCallLogFields.OFFER_TIME, Sort.DESCENDING).first();
                        new RequestSignalingClearLog().signalingClearLog(realmCallLog.getId());
                        imgCallEmpty.setVisibility(View.VISIBLE);
                        empty_call.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        realm.close();
                    }
                }).negativeText(R.string.B_cancel).show();
            } else {
                HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
            }
        });
        topSheetDialog.show();
    }

    @Override
    public void onCallLogClear() {
        //G.handler.post(new Runnable() {
        //    @Override
        //    public void run() {
        //        if (mAdapter != null) {
        //            mAdapter.clear();
        //        }
        //    }
        //});
    }

    //*************************************************************************************************************

    @Override
    public void onResume() {
        super.onResume();

        if (progressBar != null) {
            AppUtils.setProgresColler(progressBar);
        }

        if (G.isUpdateNotificaionCall) {
            G.isUpdateNotificaionCall = false;

            if (mRecyclerView != null) {
                if (mRecyclerView.getAdapter() != null) {
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }


    }

    /**
     * **********************************************************************************
     * ********************************** RealmAdapter **********************************
     * **********************************************************************************
     */

    public class CallAdapter extends RealmRecyclerViewAdapter<RealmCallLog, CallAdapter.ViewHolder> {

        public CallAdapter(RealmResults<RealmCallLog> realmResults) {
            super(realmResults, true);
        }

        @Override
        public CallAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            //  new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_call_sub_layout, null));

            return new ViewHolder(ViewMaker.getViewItemCall());
        }

        @Override
        public void onBindViewHolder(final CallAdapter.ViewHolder viewHolder, int i) {

            final RealmCallLog item = viewHolder.callLog = getItem(i);

            if (item == null) {
                return;
            }
            // set icon and icon color

            switch (ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog.Status.valueOf(item.getStatus())) {
                case OUTGOING:
                    viewHolder.icon.setText(R.string.voice_call_made_icon);
                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.green));
                    viewHolder.timeDuration.setTextColor(G.context.getResources().getColor(R.color.green));
                    break;
                case MISSED:
                    viewHolder.icon.setText(R.string.voice_call_missed_icon);
                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.red));
                    viewHolder.timeDuration.setTextColor(G.context.getResources().getColor(R.color.red));
                    viewHolder.timeDuration.setText(R.string.miss);
                    break;
                case CANCELED:
                    viewHolder.icon.setText(R.string.voice_call_made_icon);
                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.green));
                    viewHolder.timeDuration.setTextColor(G.context.getResources().getColor(R.color.green));
                    viewHolder.timeDuration.setText(R.string.not_answer);
                    break;
                case INCOMING:
                    viewHolder.icon.setText(R.string.voice_call_received_icon);
                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.colorPrimary));
                    viewHolder.timeDuration.setTextColor(G.context.getResources().getColor(R.color.colorPrimary));
                    break;
            }

            if (ProtoSignalingOffer.SignalingOffer.Type.valueOf(item.getType()) == VIDEO_CALLING) {
                viewHolder.icon.setText(R.string.video_call_icon);
            }


            if (HelperCalander.isPersianUnicode) {
                viewHolder.timeAndInfo.setText(HelperCalander.checkHijriAndReturnTime(item.getOfferTime()) + " " + TimeUtils.toLocal(item.getOfferTime() * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME)); //+ " " + HelperCalander.checkHijriAndReturnTime(item.getOfferTime())
            } else {
                viewHolder.timeAndInfo.setText(HelperCalander.checkHijriAndReturnTime(item.getOfferTime()) + " " + TimeUtils.toLocal(item.getOfferTime() * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME));
            }

            if (item.getDuration() > 0) {
                viewHolder.timeDuration.setText(DateUtils.formatElapsedTime(item.getDuration()));
            }

            if (HelperCalander.isPersianUnicode) {
                viewHolder.timeAndInfo.setText(HelperCalander.convertToUnicodeFarsiNumber(viewHolder.timeAndInfo.getText().toString()));
                viewHolder.timeDuration.setText(HelperCalander.convertToUnicodeFarsiNumber(viewHolder.timeDuration.getText().toString()));
            }

            viewHolder.name.setText(item.getUser().getDisplayName());
            avatarHandler.getAvatar(new ParamWithAvatarType(viewHolder.image, item.getUser().getId()).avatarType(AvatarHandler.AvatarType.USER));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private RealmCallLog callLog;
            private CircleImageView image;
            private EmojiTextViewE name;
            private MaterialDesignTextView icon;
            private TextView timeAndInfo;
            private TextView timeDuration;

            public ViewHolder(View view) {
                super(view);

                imgCallEmpty.setVisibility(View.GONE);
                empty_call.setVisibility(View.GONE);

                timeDuration = (TextView) itemView.findViewById(R.id.fcsl_txt_dureation_time);
                image = (CircleImageView) itemView.findViewById(R.id.fcsl_imv_picture);
                name = (EmojiTextViewE) itemView.findViewById(R.id.fcsl_txt_name);
                icon = (MaterialDesignTextView) itemView.findViewById(R.id.fcsl_txt_icon);
                timeAndInfo = (TextView) itemView.findViewById(R.id.fcsl_txt_time_info);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // HelperPublicMethod.goToChatRoom(realmResults.get(getPosition()).getLogProto().getPeer().getId(), null, null);

                        if (canclick) {
                            long userId = callLog.getUser().getId();

                            if (userId != 134 && G.userId != userId) {
                                CallSelectFragment callSelectFragment = CallSelectFragment.getInstance(userId,false, ProtoSignalingOffer.SignalingOffer.Type.valueOf(callLog.getType()));
                                callSelectFragment.show(getFragmentManager(),null);
                            }
                        }
                    }
                });

                itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            move = (int) event.getX();
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {

                            int i = Math.abs((int) (move - event.getX()));

                            if (i < 10) {
                                canclick = true;
                            } else {
                                canclick = false;
                            }
                        }

                        return false;
                    }
                });
            }
        }
    }
}
