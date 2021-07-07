package net.iGap.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.CallActivity;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.HelperWallet;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.messenger.ui.components.FragmentMediaContainer;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.NumberTextView;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItem;
import net.iGap.messenger.ui.toolBar.ToolbarItems;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.StatusBarUtil;
import net.iGap.module.Theme;
import net.iGap.module.TimeUtils;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.CheckBox;
import net.iGap.observers.interfaces.ISignalingGetCallLog;
import net.iGap.observers.interfaces.OnCallLogClear;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoSignalingGetLog;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmCallLog;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestSignalingClearLog;
import net.iGap.request.RequestSignalingGetLog;

import org.jetbrains.annotations.NotNull;
import org.paygear.WalletActivity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

import static net.iGap.G.isAppRtl;
import static net.iGap.activities.ActivityMain.WALLET_REQUEST_CODE;
import static net.iGap.proto.ProtoSignalingOffer.SignalingOffer.Type.VIDEO_CALLING;

public class FragmentCall extends BaseMainFragments {

    public static final String OPEN_IN_FRAGMENT_MAIN = "OPEN_IN_FRAGMENT_MAIN";

    private boolean isSendRequestForLoading = false;
    private boolean isThereAnyMoreItemToLoad = true;
    private ProgressBar progressBar;
    private View emptuListView;
    private boolean canclick = false;
    private int move = 0;
    private int mOffset = 0;
    private RecyclerView.OnScrollListener onScrollListener;
    private int attampOnError = 0;
    private RecyclerView mRecyclerView;
    private ProtoSignalingGetLog.SignalingGetLog.Filter mSelectedStatus = ProtoSignalingGetLog.SignalingGetLog.Filter.ALL;

    private TextView mBtnAllCalls, mBtnMissedCalls, mBtnIncomingCalls, mBtnOutgoingCalls, mBtnCanceledCalls;
    private RealmResults<RealmCallLog> realmResults;
    private boolean mIsMultiSelectEnable = false;
    private List<RealmCallLog> mSelectedLogList = new ArrayList<>();
    private ViewGroup mFiltersLayout;
    private Toolbar callToolbar;
    private ToolbarItem deleteItem;
    private ToolbarItem deleteAllItem;
    private final int createCallTag = 1;
    private final int qrWalletTag = 2;
    private final int deleteTag = 4;
    private final int deleteAllTag = 5;
    private FragmentMediaContainer mediaContainer;
    private ArrayList<ToolbarItem> actionModeViews = new ArrayList<>();
    private NumberTextView multiSelectCounter;
    private final int selectCounter = 16;

    public static FragmentCall newInstance(boolean openInFragmentMain) {
        FragmentCall fragmentCall = new FragmentCall();
        Bundle bundle = new Bundle();
        bundle.putBoolean(OPEN_IN_FRAGMENT_MAIN, openInFragmentMain);
        fragmentCall.setArguments(bundle);
        return fragmentCall;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HelperTracker.sendTracker(HelperTracker.TRACKER_CALL_PAGE);

        if (getContext() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtil.setColor(getActivity(), new Theme().getPrimaryDarkColor(getContext()), 50);
        }

        callToolbar = new Toolbar(getContext());
        callToolbar.setTitle(isAppRtl ? R.string.logo_igap_fa : R.string.logo_igap_en);
        ToolbarItems toolbarItems = callToolbar.createToolbarItems();
        toolbarItems.addItemWithWidth(createCallTag, R.string.icon_add, 54);
        toolbarItems.addItemWithWidth(qrWalletTag, R.string.icon_QR_code, 54);
        callToolbar.setListener(i -> {
            switch (i) {
                case -1:
                    setViewState(!mIsMultiSelectEnable);
                    hideActionMode();
                    break;
                case createCallTag:
                    showContactListForCall();
                    break;
                case qrWalletTag:
                    onScannerClickListener();
                    break;
                case deleteAllTag:
                    if (getRequestManager().isUserLogin()) {
                        new MaterialDialog.Builder(getContext()).title(R.string.clean_log).content(R.string.are_you_sure_clear_call_logs).
                                positiveText(R.string.B_ok).onPositive((dialog, which) -> {
                            DbManager.getInstance().doRealmTask(realm -> {
                                //ToDo: add callback to proto request
                                setViewState(false);
                                RealmCallLog realmCallLog = realm.where(RealmCallLog.class).findAll().sort("offerTime", Sort.DESCENDING).first();
                                new RequestSignalingClearLog().signalingClearLog(realmCallLog.getId());
                                view.findViewById(R.id.empty_layout).setVisibility(View.VISIBLE);
                                mSelectedLogList.clear();
                            });
                            hideActionMode();
                        }).negativeText(R.string.B_cancel).show();
                    } else {
                        HelperError.showSnackMessage(getString(R.string.there_is_no_connection_to_server), false);
                    }
                    break;
                case deleteTag:
                    if (mSelectedLogList.size() == 0) {
                        Toast.makeText(_mActivity, getString(R.string.no_item_selected), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (getRequestManager().isUserLogin()) {
                        new MaterialDialog.Builder(getActivity()).title(R.string.clean_log).content(R.string.are_you_sure_clear_call_log).positiveText(R.string.B_ok).onPositive((dialog, which) -> {

                            try {
                                List<Long> logIds = new ArrayList<>();

                                for (int j = 0; j < mSelectedLogList.size(); j++) {
                                    logIds.add(mSelectedLogList.get(j).getLogId());
                                }
                                new RequestSignalingClearLog().signalingClearLog(logIds);


                                setViewState(false);

                                mSelectedLogList.clear();

                                if (realmResults.size() == 0) {
                                    view.findViewById(R.id.empty_layout).setVisibility(View.VISIBLE);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            hideActionMode();

                        }).negativeText(R.string.B_cancel).show();
                    } else {
                        HelperError.showSnackMessage(getString(R.string.there_is_no_connection_to_server), false);
                    }

                    break;
            }
        });
        createToolbarActions();
        mediaContainer = new FragmentMediaContainer(getContext(), this);
        mediaContainer.setListener(i -> {
            switch (i) {
                case FragmentMediaContainer.CALL_TAG:
                    getActivity().startActivity(new Intent(getContext(), CallActivity.class));
                    break;
                case FragmentMediaContainer.MEDIA_TAG:
                    if (!MusicPlayer.isVoice) {
                        Intent intent = new Intent(context, ActivityMain.class);
                        intent.putExtra(ActivityMain.openMediaPlyer, true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().startActivity(intent);
                    }
                    break;
                case FragmentMediaContainer.PLAY_TAG:
                    break;
            }
        });

        ViewGroup layoutToolbar = view.findViewById(R.id.fc_layout_toolbar);
        layoutToolbar.addView(mediaContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 38, Gravity.BOTTOM,0,60,0,0));
        layoutToolbar.addView(callToolbar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 64, Gravity.TOP));

        mBtnAllCalls = view.findViewById(R.id.fc_btn_all_calls);
        mBtnMissedCalls = view.findViewById(R.id.fc_btn_missed_calls);
        mBtnCanceledCalls = view.findViewById(R.id.fc_btn_canceled_calls);
        mBtnIncomingCalls = view.findViewById(R.id.fc_btn_incoming_calls);
        mBtnOutgoingCalls = view.findViewById(R.id.fc_btn_outgoing_calls);
        progressBar = view.findViewById(R.id.fc_progress_bar_waiting);
        emptuListView = view.findViewById(R.id.empty_layout);
        progressBar = view.findViewById(R.id.fc_progress_bar_waiting);
        mRecyclerView = view.findViewById(R.id.fc_recycler_view_call);
        mFiltersLayout = view.findViewById(R.id.fc_layout_filters);

        setEnableButton(mBtnAllCalls, mBtnMissedCalls, mBtnIncomingCalls, mBtnOutgoingCalls, mBtnCanceledCalls);

        mRecyclerView = view.findViewById(R.id.fc_recycler_view_call);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));


        if (realmResults == null) {
            realmResults = DbManager.getInstance().doRealmTask(realm -> {
                return getRealmResult(mSelectedStatus, realm);
            });
        }

        realmResults.addChangeListener((realmCallLogs, changeSet) -> {
            checkListIsEmpty();
        });
        checkListIsEmpty();

        mRecyclerView.setAdapter(new CallAdapter(realmResults));

        mOffset = 0;
        getLogListWithOffset();

        mRecyclerView.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkListIsEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkListIsEmpty();
            }

        });

        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
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

        G.iSignalingGetCallLog = (size, signalingLogList) -> {
            if (signalingLogList != null) {
                G.handler.post(() -> progressBar.setVisibility(View.GONE));
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
        };

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
                setEnableButton(mBtnOutgoingCalls, mBtnMissedCalls, mBtnAllCalls, mBtnIncomingCalls, mBtnCanceledCalls);
                getCallLogsFromRealm(ProtoSignalingGetLog.SignalingGetLog.Filter.OUTGOING);
            }

        });

        mBtnIncomingCalls.setOnClickListener(v -> {

            if (mSelectedStatus != ProtoSignalingGetLog.SignalingGetLog.Filter.INCOMING) {
                setEnableButton(mBtnIncomingCalls, mBtnMissedCalls, mBtnAllCalls, mBtnOutgoingCalls, mBtnCanceledCalls);
                getCallLogsFromRealm(ProtoSignalingGetLog.SignalingGetLog.Filter.INCOMING);
            }

        });

        mBtnCanceledCalls.setOnClickListener(v -> {

            if (mSelectedStatus != ProtoSignalingGetLog.SignalingGetLog.Filter.CANCELED) {
                setEnableButton(mBtnCanceledCalls, mBtnMissedCalls, mBtnAllCalls, mBtnIncomingCalls, mBtnOutgoingCalls);
                getCallLogsFromRealm(ProtoSignalingGetLog.SignalingGetLog.Filter.CANCELED);
            }

        });
        //Todo: fixed it, cause load view with delay
        setViewState(mIsMultiSelectEnable);
    }

    private void showToolbarActions() {
        callToolbar.showActionToolbar();

        BackDrawable backDrawable = new BackDrawable(true);
        backDrawable.setRotation(1, true);
        backDrawable.setRotatedColor(Theme.getInstance().getPrimaryTextColor(getContext()));
        callToolbar.setBackIcon(backDrawable);
        multiSelectCounter.setNumber(mSelectedLogList.size(), true);
        AnimatorSet animatorSet = new AnimatorSet();
        ArrayList<Animator> animators = new ArrayList<>();
        for (int a = 0; a < actionModeViews.size(); a++) {
            View itemView = actionModeViews.get(a);
            itemView.setPivotY(Toolbar.getCurrentActionBarHeight() / 2);
            animators.add(ObjectAnimator.ofFloat(itemView, View.SCALE_Y, 0.1f, 1.0f));
        }
        animatorSet.playTogether(animators);
        animatorSet.setDuration(180);
        animatorSet.start();
    }
    private void createToolbarActions() {
        if (callToolbar.isInActionMode())
            return;

        ToolbarItems actionToolbar = callToolbar.createActionToolbar(null);
        actionToolbar.setBackground(null);

        deleteItem = actionToolbar.addItemWithWidth(deleteTag, R.string.icon_delete, 54);
        deleteAllItem = actionToolbar.addItemWithWidth(deleteAllTag, R.string.icon_delete_all, 54);

        multiSelectCounter = new NumberTextView(getContext());
        multiSelectCounter.setTextSize(18);
        multiSelectCounter.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font_bold));
        multiSelectCounter.setTextColor(Theme.getInstance().getPrimaryTextColor(getContext()));
        multiSelectCounter.setTag(selectCounter);
        actionToolbar.addView(multiSelectCounter, LayoutCreator.createLinear(0, LayoutCreator.MATCH_PARENT, 1.0f, 72, 0, 0, 0));

        actionModeViews.add(deleteItem);
        actionModeViews.add(deleteAllItem);
    }

    private void hideActionMode() {
        callToolbar.hideActionToolbar();
        callToolbar.setBackIcon(null);
    }

    private void checkListIsEmpty() {
        emptuListView.setVisibility(realmResults.size() > 0 ? View.GONE : View.VISIBLE);
    }

    private void getCallLogsFromRealm(ProtoSignalingGetLog.SignalingGetLog.Filter filter) {
        if (realmResults != null) realmResults.removeAllChangeListeners();
        mSelectedStatus = filter;
        realmResults = DbManager.getInstance().doRealmTask(realm -> {
            return getRealmResult(mSelectedStatus, realm);
        });

        mRecyclerView.setAdapter(new CallAdapter(realmResults));
        checkListIsEmpty();
    }

    private void setEnableButton(TextView enable, TextView disable, TextView disable2, TextView disable3, TextView disable4) {
        enable.setSelected(true);
        disable.setSelected(false);
        disable2.setSelected(false);
        disable3.setSelected(false);
        disable4.setSelected(false);
    }

    private RealmResults<RealmCallLog> getRealmResult(ProtoSignalingGetLog.SignalingGetLog.Filter status, Realm realm) {

        switch (status) {
            case MISSED:
            case OUTGOING:
            case CANCELED:
            case INCOMING:
                return realm.where(RealmCallLog.class).equalTo("status", status.name()).findAll().sort("offerTime", Sort.DESCENDING);

            default:
                return realm.where(RealmCallLog.class).findAll().sort("offerTime", Sort.DESCENDING);
        }

    }

    public void showContactListForCall() {
        try {
            if (getActivity() != null) {
                Fragment fragment = RegisteredContactsFragment.newInstance(true, true, RegisteredContactsFragment.CALL);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void getLogListWithOffset() {

        if (getRequestManager().isSecure() && getRequestManager().isUserLogin()) {
            isSendRequestForLoading = true;
            int mLimit = 50;
            new RequestSignalingGetLog().signalingGetLog(mOffset, mLimit, mSelectedStatus);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            new Handler().postDelayed(this::getLogListWithOffset, 1000);
        }

    }

    private void setViewState(boolean state) {

        if (!state) {
            mIsMultiSelectEnable = false;

        } else {

            mIsMultiSelectEnable = true;

        }
        refreshCallList(0, true);
    }


    //*************************************************************************************************************

    @Override
    public void onResume() {
        super.onResume();

        if (progressBar != null) {
            AppUtils.setProgresColler(progressBar);
        }
        mediaContainer.didLayoutChanged();
        if (G.isUpdateNotificaionCall) {
            G.isUpdateNotificaionCall = false;

            if (mRecyclerView != null) {
                if (mRecyclerView.getAdapter() != null) {
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }

    }


    private void refreshCallList(int pos, boolean isRefreshAll) {
        if (mRecyclerView.getAdapter() != null) {
            if (isRefreshAll) {
                mRecyclerView.getAdapter().notifyDataSetChanged();
            } else {
                mRecyclerView.getAdapter().notifyItemChanged(pos);
            }
        }
    }

    @Override
    public boolean isAllowToBackPressed() {
        if (mIsMultiSelectEnable) {
            hideActionMode();
            setViewState(false);
            mSelectedLogList.clear();
            return false;
        }
        return true;
    }

    @Override
    public void scrollToTopOfList() {
        if (mRecyclerView != null) mRecyclerView.smoothScrollToPosition(0);
    }

    private void onScannerClickListener() {
        DbManager.getInstance().doRealmTask(realm -> {
            String phoneNumber = "";
            RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
            try {
                if (userInfo != null) {
                    phoneNumber = userInfo.getUserInfo().getPhoneNumber().substring(2);
                } else {
                    phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber().substring(2);
                }
            } catch (Exception e) {
                //maybe exception was for realm substring
                try {
                    phoneNumber = AccountManager.getInstance().getCurrentUser().getPhoneNumber().substring(2);
                } catch (Exception ex) {
                    //nothing
                }
            }

            if (userInfo == null || !userInfo.isWalletRegister()) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentWalletAgrement.newInstance(phoneNumber)).load();
            } else {
                getActivity().startActivityForResult(new HelperWallet().goToWallet(getContext(), new Intent(getActivity(), WalletActivity.class), "0" + phoneNumber, true), WALLET_REQUEST_CODE);
            }

        });
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

        @NotNull
        @Override
        public CallAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int i) {
            //  new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_call_sub_layout, null));

            return new ViewHolder(ViewMaker.getViewItemCall(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NotNull final CallAdapter.ViewHolder viewHolder, int i) {

            final RealmCallLog item = viewHolder.callLog = getItem(i);

            if (item == null) {
                return;
            }
            // set icon and icon color

            if (mIsMultiSelectEnable) {
                viewHolder.checkBox.setVisibility(View.VISIBLE);

                try {

                    if (mSelectedLogList.contains(item)) {
                        viewHolder.checkBox.setChecked(true,true);
                    } else {
                        viewHolder.checkBox.setChecked(false,true);
                    }

                } catch (Exception e) {

                }

            } else {
                viewHolder.checkBox.setVisibility(View.GONE);
                viewHolder.checkBox.setChecked(false,true);
            }

            switch (ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog.Status.valueOf(item.getStatus())) {
                case OUTGOING:
                    viewHolder.icon.setText(R.string.icon_voice_call);
                    viewHolder.icon.setTextColor(getResources().getColor(R.color.green));
                    viewHolder.timeDuration.setTextColor(getResources().getColor(R.color.green));
                    break;
                case MISSED:
                    viewHolder.icon.setText(R.string.icon_missed_call);
                    viewHolder.icon.setTextColor(getResources().getColor(R.color.red));
                    viewHolder.timeDuration.setTextColor(getResources().getColor(R.color.red));
                    viewHolder.timeDuration.setText(R.string.miss);
                    break;
                case CANCELED:
                    viewHolder.icon.setText(R.string.icon_voice_call);
                    viewHolder.icon.setTextColor(getResources().getColor(R.color.green));
                    viewHolder.timeDuration.setTextColor(getResources().getColor(R.color.green));
                    viewHolder.timeDuration.setText(R.string.not_answer);
                    break;
                case INCOMING:
                    viewHolder.icon.setText(R.string.icon_incoming_call);
                    viewHolder.icon.setTextColor(getResources().getColor(R.color.colorPrimary));
                    viewHolder.timeDuration.setTextColor(getResources().getColor(R.color.colorPrimary));
                    break;
            }

            if (ProtoSignalingOffer.SignalingOffer.Type.valueOf(item.getType()) == VIDEO_CALLING) {
                viewHolder.icon.setText(R.string.icon_video_call);
            }


            viewHolder.timeAndInfo.setText(HelperCalander.checkHijriAndReturnTime(item.getOfferTime()) + " " + TimeUtils.toLocal(item.getOfferTime() * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME)); //+ " " + HelperCalander.checkHijriAndReturnTime(item.getOfferTime())

            if (item.getDuration() > 0) {
                viewHolder.timeDuration.setText(DateUtils.formatElapsedTime(item.getDuration()));
            }

            if (HelperCalander.isPersianUnicode) {
                viewHolder.timeAndInfo.setText(HelperCalander.convertToUnicodeFarsiNumber(viewHolder.timeAndInfo.getText().toString()));
                viewHolder.timeDuration.setText(HelperCalander.convertToUnicodeFarsiNumber(viewHolder.timeDuration.getText().toString()));
            }

            viewHolder.name.setText(EmojiManager.getInstance().replaceEmoji(item.getUser().getDisplayName(), viewHolder.name.getPaint().getFontMetricsInt()));
            avatarHandler.getAvatar(new ParamWithAvatarType(viewHolder.image, item.getUser().getId()).avatarType(AvatarHandler.AvatarType.USER));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private RealmCallLog callLog;
            private CircleImageView image;
            private TextView name;
            private MaterialDesignTextView icon;
            private TextView timeAndInfo;
            private TextView timeDuration;
            private CheckBox checkBox;

            public ViewHolder(View view) {
                super(view);
                timeDuration = itemView.findViewById(R.id.fcsl_txt_dureation_time);
                image = itemView.findViewById(R.id.fcsl_imv_picture);
                name = itemView.findViewById(R.id.fcsl_txt_name);
                icon = itemView.findViewById(R.id.fcsl_txt_icon);
                timeAndInfo = itemView.findViewById(R.id.fcsl_txt_time_info);
                checkBox = itemView.findViewById(R.id.fcsl_check_box);

                itemView.setOnClickListener(v -> {
                    if (mIsMultiSelectEnable) {
                        multiSelectHandler(getItem(getAdapterPosition()), getAdapterPosition(), !checkBox.isChecked());
                        multiSelectCounter.setNumber(mSelectedLogList.size(), true);
                        if (mSelectedLogList.size() < 1) {
                            callToolbar.hideActionToolbar();
                            callToolbar.setBackIcon(null);
                            mIsMultiSelectEnable = false;
                        }
                    } else {
                        if (canclick) {
                            long userId = callLog.getUser().getId();
                            if (userId != 134 && AccountManager.getInstance().getCurrentUser().getId() != userId) {
                                CallSelectFragment callSelectFragment = CallSelectFragment.getInstance(userId, false, ProtoSignalingOffer.SignalingOffer.Type.valueOf(callLog.getType()));
                                callSelectFragment.show(getFragmentManager(), null);
                            }
                        }
                    }
                });
                itemView.setOnLongClickListener(v -> {
                    mIsMultiSelectEnable = true;
                    multiSelectHandler(getItem(getAdapterPosition()), getAdapterPosition(), !checkBox.isChecked());
                    showToolbarActions();
                    return true;
                });

                itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            move = (int) event.getX();
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {

                            int i = Math.abs((int) (move - event.getX()));

                            canclick = i < 10;
                        }

                        return false;
                    }
                });
            }

            private void multiSelectHandler(RealmCallLog item, int pos, boolean checked) {
                if (checked) {
                    mSelectedLogList.add(item);
                } else {
                    mSelectedLogList.remove(item);
                }
                refreshCallList(pos, false);
            }
        }
    }
}
