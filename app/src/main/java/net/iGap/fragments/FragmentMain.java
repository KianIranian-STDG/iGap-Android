package net.iGap.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityRegisteration;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.adapter.items.chat.BadgeView;
import net.iGap.adapter.items.chat.ChatCell;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetAction;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.avatar.ParamWithInitBitmap;
import net.iGap.interfaces.OnActivityChatStart;
import net.iGap.interfaces.OnChannelDeleteInRoomList;
import net.iGap.interfaces.OnChatDeleteInRoomList;
import net.iGap.interfaces.OnChatSendMessageResponse;
import net.iGap.interfaces.OnChatUpdateStatusResponse;
import net.iGap.interfaces.OnClientGetRoomListResponse;
import net.iGap.interfaces.OnClientGetRoomResponseRoomList;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnDateChanged;
import net.iGap.interfaces.OnGroupDeleteInRoomList;
import net.iGap.interfaces.OnNotifyTime;
import net.iGap.interfaces.OnRemoveFragment;
import net.iGap.interfaces.OnSetActionInRoom;
import net.iGap.interfaces.OnVersionCallBack;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.MyRealmRecyclerViewAdapter;
import net.iGap.libs.Tuple;
import net.iGap.module.AppUtils;
import net.iGap.module.BotInit;
import net.iGap.module.CircleImageView;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.FontIconTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.MyDialog;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.enums.RoomType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestChannelDelete;
import net.iGap.request.RequestChannelLeft;
import net.iGap.request.RequestChatDelete;
import net.iGap.request.RequestClientCondition;
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.request.RequestClientMuteRoom;
import net.iGap.request.RequestClientPinRoom;
import net.iGap.request.RequestGroupDelete;
import net.iGap.request.RequestGroupLeft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollection;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static net.iGap.G.clientConditionGlobal;
import static net.iGap.G.context;
import static net.iGap.G.userId;
import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;
import static net.iGap.fragments.FragmentMain.MainType.all;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.CARD_TO_CARD;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.MONEY_TRANSFER;
import static net.iGap.proto.ProtoGlobal.RoomMessageWallet.Type.PAYMENT;
import static net.iGap.realm.RealmRoom.putChatToDatabase;


public class FragmentMain extends BaseFragment implements ToolbarListener, OnClientGetRoomListResponse, OnVersionCallBack, OnComplete, OnSetActionInRoom, OnRemoveFragment, OnChatUpdateStatusResponse, OnChatDeleteInRoomList, OnGroupDeleteInRoomList, OnChannelDeleteInRoomList, OnChatSendMessageResponse, OnClientGetRoomResponseRoomList, OnDateChanged {

    public static final String STR_MAIN_TYPE = "STR_MAIN_TYPE";
    public static HashMap<MainType, RoomAdapter> roomAdapterHashMap = new HashMap<>();
    public MainType mainType;
    boolean isThereAnyMoreItemToLoad = true;
    private ProgressBar progressBar;
    private int mOffset = 0;
    private View viewById;
    private RecyclerView mRecyclerView;
    private long tagId;
    private Realm realmFragmentMain;
    private RecyclerView.OnScrollListener onScrollListener;
    private View mView = null;
    private String switcher;
    private int channelSwitcher, allSwitcher, groupSwitcher, chatSwitcher = 0;
    private ProgressBar pbLoading;
    private long latestScrollToTop;

    private HelperToolbar mHelperToolbar;
    private boolean isChatMultiSelectEnable = false;
    private onChatCellClick onChatCellClickedInEditMode;
    private RoomAdapter roomsAdapter;
    private List<RealmRoom> mSelectedRoomList = new ArrayList<>();
    private ViewGroup mLayoutMultiSelectedActions;
    private TextView mBtnRemoveSelected;

    public static FragmentMain newInstance(MainType mainType) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(STR_MAIN_TYPE, mainType);
        FragmentMain fragment = new FragmentMain();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isNeedResume = true;
        return inflater.inflate(R.layout.activity_main_rooms, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        G.onVersionCallBack = this;
        realmFragmentMain = Realm.getDefaultInstance();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HelperTracker.sendTracker(HelperTracker.TRACKER_ROOM_PAGE);
        this.mView = view;
        tagId = System.currentTimeMillis();

        mainType = (MainType) getArguments().getSerializable(STR_MAIN_TYPE);
        progressBar = view.findViewById(R.id.ac_progress_bar_waiting);
        viewById = view.findViewById(R.id.empty_icon);
        mLayoutMultiSelectedActions = view.findViewById(R.id.amr_layout_selected_actions);
        pbLoading = view.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);
        viewById.setVisibility(View.GONE);

        G.handler.postDelayed(this::initRecycleView, 10);

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.edit_icon)
                .setRightIcons(R.string.add_icon)
                .setLogoShown(true)
                .setPlayerEnable(true)
                .setSearchBoxShown(true, false)
                .setListener(this);

        ViewGroup layoutToolbar = view.findViewById(R.id.amr_layout_toolbar);
        layoutToolbar.addView(mHelperToolbar.getView());

        initMultiSelectActions();

        onChatCellClickedInEditMode = (v, item, position, status) -> {

            if (!status) {
                mSelectedRoomList.add(item);
            } else {
                mSelectedRoomList.remove(item);
            }
            refreshChatList(position, false);
            setVisiblityForSelectedActionsInEverySelection();
        };

        if(MusicPlayer.playerStateChangeListener != null){
            MusicPlayer.playerStateChangeListener.observe(this , isVisible -> {
                notifyChatRoomsList();

                if (!mHelperToolbar.getmSearchBox().isShown()){
                    mHelperToolbar.animateSearchBox(false);
                }
            });
        }

        G.callStripLayoutVisiblityListener.observe(this , isVisible -> {
           notifyChatRoomsList();

            if (!mHelperToolbar.getmSearchBox().isShown()){
                mHelperToolbar.animateSearchBox(false);
            }

        });

        //just check at first time page loaded
        notifyChatRoomsList();

    }

    private void notifyChatRoomsList() {

        try{
            if (mRecyclerView != null) {
                if (MusicPlayer.mainLayout != null && MusicPlayer.mainLayout.isShown()) {
                    mRecyclerView.setPadding(0, i_Dp(R.dimen.dp80), 0, 0);
                } else if (G.isInCall) {
                    mRecyclerView.setPadding(0, i_Dp(R.dimen.dp68), 0, 0);
                } else {
                    mRecyclerView.setPadding(0, i_Dp(R.dimen.dp24), 0, 0);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void initMultiSelectActions() {

        mBtnRemoveSelected = mView.findViewById(R.id.amr_btn_delete_selected);
        TextView mBtnClearCacheSelected = mView.findViewById(R.id.amr_btn_clear_cache_selected);
        TextView mBtnMakeAsReadSelected = mView.findViewById(R.id.amr_btn_make_as_read_selected);
        TextView mBtnReadAllSelected = mView.findViewById(R.id.amr_btn_read_all_selected);


        mBtnRemoveSelected.setOnClickListener(v -> {
            if (mSelectedRoomList.size() > 0)
                confirmActionForRemoveSelected();
        });

        mBtnClearCacheSelected.setOnClickListener(v -> {
            if (mSelectedRoomList.size() > 0) {
                confirmActionForClearHistoryOfSelected();
            }
        });

        mBtnMakeAsReadSelected.setOnClickListener(v -> {

            if (mSelectedRoomList.size() > 0) {
                for (int i = 0; i < mSelectedRoomList.size(); i++) {
                    markAsRead(mSelectedRoomList.get(i).getType(), mSelectedRoomList.get(i).getId());
                }
                onLeftIconClickListener(v);
            }
        });

        if (G.isDarkTheme) {
            setColorToDarkMode(mBtnRemoveSelected);
            setColorToDarkMode(mBtnClearCacheSelected);
            setColorToDarkMode(mBtnMakeAsReadSelected);
            setColorToDarkMode(mBtnReadAllSelected);
        }else {
            setColorToLightMode(mBtnRemoveSelected);
            setColorToLightMode(mBtnClearCacheSelected);
            setColorToLightMode(mBtnMakeAsReadSelected);
            setColorToLightMode(mBtnReadAllSelected);
        }

    }

    private void setColorToDarkMode(TextView textView) {
        textView.setBackground(getContext().getResources().getDrawable(R.drawable.round_button_enabled_bg));
        textView.setTextColor(getContext().getResources().getColor(R.color.white));
    }

    private void setColorToLightMode(TextView textView) {
        textView.setBackground(getContext().getResources().getDrawable(R.drawable.round_button_disabled_bg));
        textView.setTextColor(getContext().getResources().getColor(R.color.gray_4c));
    }

    private void refreshChatList(int pos, boolean isRefreshAll) {

        if (isRefreshAll) {
            roomsAdapter.notifyDataSetChanged();
        } else {
            roomsAdapter.notifyItemChanged(pos);
        }

    }

    private void initRecycleView() {

        if (mView != null) {
            mRecyclerView = mView.findViewById(R.id.cl_recycler_view_contact);
            mRecyclerView.setItemAnimator(null);
            mRecyclerView.setItemViewCacheSize(0);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
        }

        RealmResults<RealmRoom> results = null;
        String[] fieldNames = {RealmRoomFields.IS_PINNED, RealmRoomFields.PIN_ID, RealmRoomFields.UPDATED_TIME};
        Sort[] sort = {Sort.DESCENDING, Sort.DESCENDING, Sort.DESCENDING};
        RealmQuery<RealmRoom> temp = getRealmFragmentMain().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.IS_DELETED, false);

        switch (mainType) {
            case all:
                results = temp.sort(fieldNames, sort).findAllAsync();
                break;
            case chat:
                results = temp.equalTo(RealmRoomFields.TYPE, RoomType.CHAT.toString()).sort(fieldNames, sort).findAllAsync();
                break;
            case group:
                results = temp.equalTo(RealmRoomFields.TYPE, RoomType.GROUP.toString()).sort(fieldNames, sort).findAllAsync();
                break;
            case channel:
                results = temp.equalTo(RealmRoomFields.TYPE, RoomType.CHANNEL.toString()).sort(fieldNames, sort).findAllAsync();
                break;
        }


        roomsAdapter = new RoomAdapter(results, this, viewById, pbLoading);

        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isThereAnyMoreItemToLoad) {
                    if (mOffset > 0) {
                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                        if (lastVisiblePosition + 10 >= mOffset) {
                            boolean send = new RequestClientGetRoomList().clientGetRoomList(mOffset, Config.LIMIT_LOAD_ROOM, tagId + "");
                            if (send)
                                progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                } /*else {
                    mRecyclerView.removeOnScrollListener(onScrollListener);
                }*/

                //check if music player was enable disable scroll detecting for search box
                if (G.isInCall || isChatMultiSelectEnable || (MusicPlayer.mainLayout != null && MusicPlayer.mainLayout.isShown())) {

                    if (!mHelperToolbar.getmSearchBox().isShown()){
                        mHelperToolbar.animateSearchBox(false);

                    }

                    return;
                }

                //check recycler scroll for search box animation
                if (dy <= 0) {

                    // Scrolling up
                    if (!mHelperToolbar.isToolbarSearchAnimationInProccess && !mHelperToolbar.getmSearchBox().isShown()){
                        mHelperToolbar.animateSearchBox(false);

                    }

                } else  {

                    // Scrolling down
                    if (!mHelperToolbar.isToolbarSearchAnimationInProccess && mHelperToolbar.getmSearchBox().isShown()){
                        mHelperToolbar.animateSearchBox(true);
                    }

                }
            }
        };
        mRecyclerView.addOnScrollListener(onScrollListener);

        mRecyclerView.setAdapter(roomsAdapter);

        if (roomAdapterHashMap == null) {
            roomAdapterHashMap = new HashMap<>();
        }
        roomAdapterHashMap.put(mainType, roomsAdapter);

        if (mainType == all) {
            getChatLists();
        }


        G.onNotifyTime = new OnNotifyTime() {
            @Override
            public void notifyTime() {
                if (mRecyclerView != null) {
                    if (mRecyclerView.getAdapter() != null) {
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        };

    }
    
    //***************************************************************************************************************************


    //***************************************************************************************************************************

    private void sendClientCondition() {
        if (clientConditionGlobal != null) {
            new RequestClientCondition().clientCondition(clientConditionGlobal);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendClientCondition();
                }
            }, 1000);
        }
    }

    private void getChatLists() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (G.isSecure && G.userLogin) {
                    boolean send = new RequestClientGetRoomList().clientGetRoomList(mOffset, Config.LIMIT_LOAD_ROOM, tagId + "");
                    if (send)
                        progressBar.setVisibility(View.VISIBLE);
                } else {
                    getChatLists();
                }
            }
        }, 1000);
    }

    private void onSelectRoomMenu(String message, RealmRoom item) {
        if (checkValidationForRealm(item)) {
            switch (message) {
                case "pinToTop":

                    pinToTop(item.getId(), item.isPinned());

                    break;
                case "txtMuteNotification":
                    muteNotification(item.getId(), item.getMute());
                    break;
                case "txtClearHistory":
                    clearHistory(item.getId());
                    break;
                case "txtDeleteChat":
                    deleteChat(item);
                    break;
            }
        }
    }

    private void deleteChat(RealmRoom item) {

        if (item.getType() == CHAT) {
            new RequestChatDelete().chatDelete(item.getId());
        } else if (item.getType() == GROUP) {
            if (item.getGroupRoom().getRole() == GroupChatRole.OWNER) {
                new RequestGroupDelete().groupDelete(item.getId());
            } else {
                new RequestGroupLeft().groupLeft(item.getId());
            }
        } else if (item.getType() == CHANNEL) {

            if (MusicPlayer.mainLayout != null) {
                if (item.getId() == MusicPlayer.roomId) {
                    MusicPlayer.closeLayoutMediaPlayer();
                }
            }


            if (item.getChannelRoom().getRole() == ChannelChatRole.OWNER) {
                new RequestChannelDelete().channelDelete(item.getId());
            } else {
                new RequestChannelLeft().channelLeft(item.getId());
            }
        }

    }

    private void muteNotification(final long roomId, final boolean mute) {
        new RequestClientMuteRoom().muteRoom(roomId, !mute);
    }

    private void clearHistory(final long roomId) {
        RealmRoomMessage.clearHistoryMessage(roomId);
    }

    private void pinToTop(final long roomId, final boolean isPinned) {
        //+Realm realm = Realm.getDefaultInstance();

        new RequestClientPinRoom().pinRoom(roomId, !isPinned);
        if (!isPinned) {
            goToTop();
        }
    }

    private void goToTop() {
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition() <= 1) {
                    mRecyclerView.smoothScrollToPosition(0);
                }
            }
        }, 50);
    }


    private boolean checkValidationForRealm(RealmRoom realmRoom) {
        if (realmRoom != null && realmRoom.isManaged() && realmRoom.isValid() && realmRoom.isLoaded()) {
            return true;
        }
        return false;
    }


    /**
     * ************************************ Callbacks ************************************
     */
    @Override
    public void onChange() {
        for (Map.Entry<MainType, RoomAdapter> entry : roomAdapterHashMap.entrySet()) {
            RoomAdapter requestWrapper = entry.getValue();
            requestWrapper.notifyDataSetChanged();
        }
    }

    @Override
    public void onSetAction(final long roomId, final long userId, final ProtoGlobal.ClientAction clientAction) {
        RealmRoom.setAction(roomId, userId, HelperGetAction.getAction(roomId, RealmRoom.detectType(roomId), clientAction));
    }

    @Override
    public void onRemoveFragment(Fragment fragment) {
        removeFromBaseFragment(fragment);
    }

    @Override
    public void onChatUpdateStatus(final long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, long statusVersion) {

    }

    @Override
    public void onChatDelete(final long roomId) {

    }

    @Override
    public void onGroupDelete(long roomId) {

    }

    @Override
    public void onGroupDeleteError(int majorCode, int minorCode) {

    }

    @Override
    public void onGroupDeleteTimeOut() {

    }

    @Override
    public void onChannelDelete(long roomId) {

    }

    @Override
    public void onChannelDeleteError(int majorCode, int minorCode) {

    }

    @Override
    public void onChannelDeleteTimeOut() {

    }

    @Override
    public void onMessageUpdate(final long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGlobal.RoomMessage roomMessage) {

    }

    @Override
    public void onMessageReceive(final long roomId, String message, ProtoGlobal.RoomMessageType messageType, ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType) {

    }

    @Override
    public void onMessageFailed(long roomId, RealmRoomMessage roomMessage) {

    }

    @Override
    public void onClientGetRoomResponse(final long roomId) {

    }

    @Override
    public synchronized void onClientGetRoomList(List<ProtoGlobal.Room> roomList, ProtoResponse.Response response, RequestClientGetRoomList.IdentityGetRoomList identity) {
        // todo : we must change roomList with the change of out client condition. merge roomList with clientCondition.
        boolean fromLogin = false;
        if (identity.isFromLogin) {
            mOffset = 0;
            fromLogin = true;
        } else if (Long.parseLong(identity.content) < tagId) {
            return;
        }

        if (mOffset == 0) {
            BotInit.checkDrIgap();
        }


        if (roomList.size() == 0) {
            isThereAnyMoreItemToLoad = false;
        } else {
            isThereAnyMoreItemToLoad = true;
        }

        putChatToDatabase(roomList);

        /**
         * to first enter to app , client first compute clientCondition then
         * getRoomList and finally send condition that before get clientCondition;
         * in else changeState compute new client condition with latest messaging changeState
         */
        if (!G.userLogin) {
            G.userLogin = true;
            sendClientCondition();
        } else if (fromLogin || mOffset == 0) {
            if (G.clientConditionGlobal != null) {
                new RequestClientCondition().clientCondition(G.clientConditionGlobal);
            } else {
                new RequestClientCondition().clientCondition(RealmClientCondition.computeClientCondition(null));
            }

        }

        mOffset += roomList.size();

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });

        //else {
        //    mOffset = 0;
        //}


    }

    @Override
    public void onClientGetRoomListError(int majorCode, int minorCode) {
        if (majorCode == 9) {
            if (G.currentActivity != null) {
                G.currentActivity.finish();
            }
            Intent intent = new Intent(context, ActivityRegisteration.class);
            intent.putExtra(ActivityRegisteration.showProfile, true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    @Override
    public void onClientGetRoomListTimeout() {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                getChatLists();
            }
        });
    }


    @Override
    public void onChatDeleteError(int majorCode, int minorCode) {

    }

    @Override
    public void complete(boolean result, String messageOne, String MessageTow) {

    }

    private Realm getRealmFragmentMain() {
        if (realmFragmentMain == null || realmFragmentMain.isClosed()) {
            realmFragmentMain = Realm.getDefaultInstance();
        }
        return realmFragmentMain;
    }

    //**************************************************************************************************************************************

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (realmFragmentMain != null && !realmFragmentMain.isClosed()) {
            realmFragmentMain.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        G.onSetActionInRoom = this;
        G.onDateChanged = this;
        if (G.isDepricatedApp)
            isDeprecated();

        G.onClientGetRoomListResponse = this;

        if (progressBar != null) {
            AppUtils.setProgresColler(progressBar);
        }

        try{
            mHelperToolbar.checkIsAvailableOnGoingCall();
        }catch (Exception e){}

        boolean canUpdate = false;

        if (mainType != null) {
            switch (mainType) {

                case all:
                    if (G.isUpdateNotificaionColorMain) {
                        canUpdate = true;
                        G.isUpdateNotificaionColorMain = false;
                    }
                    break;
                case chat:
                    if (G.isUpdateNotificaionColorChat) {
                        canUpdate = true;
                        G.isUpdateNotificaionColorChat = false;
                    }
                    break;
                case group:
                    if (G.isUpdateNotificaionColorGroup) {
                        canUpdate = true;
                        G.isUpdateNotificaionColorGroup = false;
                    }
                    break;
                case channel:
                    if (G.isUpdateNotificaionColorChannel) {
                        canUpdate = true;
                        G.isUpdateNotificaionColorChannel = false;
                    }
                    break;
            }
        }

        if (canUpdate) {

            if (mRecyclerView != null) {
                if (mRecyclerView.getAdapter() != null) {
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }

    }

    @Override
    public void isDeprecated() {
        try {
            if (getActivity() != null && !getActivity().isFinishing()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity().hasWindowFocus()) {
                            new MaterialDialog.Builder(getActivity())
                                    .cancelable(false)
                                    .title(R.string.new_version_alert).titleGravity(GravityEnum.CENTER)
                                    .titleColor(Color.parseColor("#f44336"))
                                    .content(R.string.deprecated)
                                    .contentGravity(GravityEnum.CENTER)
                                    .positiveText(R.string.startUpdate).itemsGravity(GravityEnum.START).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    // HelperUrl.openBrowser("http://d.igap.net/update");
                                    String url = "http://d.igap.net/update";
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            })
                                    .show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            HelperLog.setErrorLog(e);
        }

    }

    @Override
    public void isUpdateAvailable() {
        try {
            if (getActivity() != null && !getActivity().isFinishing()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity().hasWindowFocus()) {
                            new MaterialDialog.Builder(getActivity())
                                    .title(R.string.igap_update).titleColor(Color.parseColor("#1DE9B6"))
                                    .titleGravity(GravityEnum.CENTER)
                                    .buttonsGravity(GravityEnum.CENTER)
                                    .content(R.string.new_version_avilable).contentGravity(GravityEnum.CENTER)
                                    .negativeText(R.string.ignore).negativeColor(Color.parseColor("#798e89")).onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    dialog.dismiss();
                                }
                            }).positiveText(R.string.startUpdate).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    // HelperUrl.openBrowser("http://d.igap.net/update");
                                    String url = "http://d.igap.net/update";
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                    dialog.dismiss();
                                }
                            })
                                    .show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            HelperLog.setErrorLog(e);
        }
    }

    @Override
    public void onLeftIconClickListener(View view) {

        if (isChatMultiSelectEnable) {
            mLayoutMultiSelectedActions.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
            marginLayoutParams.setMargins(0, (int) context.getResources().getDimension(R.dimen.margin_for_below_layouts_of_toolbar_with_search), 0, 10);
            mRecyclerView.setLayoutParams(marginLayoutParams);
            isChatMultiSelectEnable = false;
            refreshChatList(0, true);
            mHelperToolbar.getRightButton().setVisibility(View.VISIBLE);
            mHelperToolbar.setLeftIcon(R.string.edit_icon);
            mSelectedRoomList.clear();
            setVisiblityForSelectedActionsInEverySelection();
        } else {
            mLayoutMultiSelectedActions.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
            marginLayoutParams.setMargins(0, (int) context.getResources().getDimension(R.dimen.margin_for_below_layouts_of_toolbar_with_room_selected_mode), 0, 10);
            mRecyclerView.setLayoutParams(marginLayoutParams);
            isChatMultiSelectEnable = true;
            refreshChatList(0, true);
            mHelperToolbar.getRightButton().setVisibility(View.GONE);
            mHelperToolbar.setLeftIcon(R.string.back_icon);

            if (!mHelperToolbar.getmSearchBox().isShown()){
                mHelperToolbar.animateSearchBox(false);

            }

        }
    }

    @Override
    public void onSearchClickListener(View view) {
        if (getActivity() != null) {
            Fragment fragment = SearchFragment.newInstance();
            try {
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment)
                        .setAnimated(true)
                        .setAnimation(R.anim.fade_in , R.anim.fade_out , R.anim.fade_in , R.anim.fade_out)
                        .load();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    @Override
    public void onRightIconClickListener(View view) {
        final Fragment fragment = RegisteredContactsFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isBackSwipable", true);
        bundle.putString("TITLE", "ADD");
        fragment.setArguments(bundle);
        try {
            if (getActivity() != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).load();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private boolean isItemAvailableOnSelectedList(RealmRoom mInfo) {
        return mSelectedRoomList.contains(mInfo);
    }

    private void confirmActionForRemoveSelected() {

        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.delete_chat))
                .content(getString(R.string.do_you_want_delete_this)).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();

                    if (mSelectedRoomList.size() > 0) {

                        for (RealmRoom item : mSelectedRoomList) {
                            deleteChat(item);
                        }

                        onLeftIconClickListener(null);
                    }
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    private void confirmActionForClearHistoryOfSelected() {

        new MaterialDialog.Builder(G.fragmentActivity).title(getString(R.string.clear_history))
                .content(getString(R.string.do_you_want_clear_history_this)).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .onPositive((dialog, which) -> {
                    dialog.dismiss();

                    for (RealmRoom item : mSelectedRoomList) {
                        clearHistory(item.getId());
                    }
                    onLeftIconClickListener(null);

                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    private void setVisiblityForSelectedActionsInEverySelection() {

        if (mSelectedRoomList.size() == 0) mBtnRemoveSelected.setVisibility(View.VISIBLE);

        for (RealmRoom item : mSelectedRoomList) {

            if (item != null && !RealmRoom.isPromote(item.getId())) {

                if (item.getType() == ProtoGlobal.Room.Type.CHAT || item.getType() == ProtoGlobal.Room.Type.GROUP
                        || item.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                    mBtnRemoveSelected.setVisibility(View.VISIBLE);
                } else {
                    mBtnRemoveSelected.setVisibility(View.GONE);
                    break;
                }

            } else {
                mBtnRemoveSelected.setVisibility(View.GONE);
                break;
            }

        }
    }

    private void markAsRead(ProtoGlobal.Room.Type chatType, long roomId) {

        G.handler.postDelayed(() -> {
            Realm realm = Realm.getDefaultInstance();
            if (chatType == ProtoGlobal.Room.Type.CHAT || chatType == ProtoGlobal.Room.Type.GROUP) {
                RealmRoomMessage.fetchMessages(realm, roomId, new OnActivityChatStart() {
                    @Override
                    public void sendSeenStatus(RealmRoomMessage message) {
                        G.chatUpdateStatusUtil.sendUpdateStatus(chatType, roomId, message.getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                    }

                    @Override
                    public void resendMessage(RealmRoomMessage message) {

                    }

                    @Override
                    public void resendMessageNeedsUpload(RealmRoomMessage message, long messageId) {

                    }
                });
            }

            RealmRoom.setCount(roomId, 0);

            G.handler.postDelayed(() -> {
                AppUtils.updateBadgeOnly(realm, roomId);
                realm.close();
            }, 250);
        }, 5);
    }

    public enum MainType {
        all, chat, group, channel
    }

    private interface onChatCellClick {
        void onClicked(View v, RealmRoom item, int pos, boolean status);
    }

    /**
     * **********************************************************************************
     * ********************************** RealmAdapter **********************************
     * **********************************************************************************
     */

    public class RoomAdapter extends MyRealmRecyclerViewAdapter<RealmRoom, RoomAdapter.ViewHolder> {

        public OnComplete mComplete;
        public String action;
        private View emptyView;
        private View loadingView;
        private ConstraintLayout rootView;

        public RoomAdapter(@Nullable OrderedRealmCollection<RealmRoom> data, OnComplete complete, View emptyView, View loadingView) {
            super(data, true);
            this.mComplete = complete;
            this.emptyView = emptyView;
            this.loadingView = loadingView;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            rootView = new ChatCell(getContext());
            rootView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, i_Dp(R.dimen.dp70)));
            return new ViewHolder(rootView);
        }

        @Override
        protected OrderedRealmCollectionChangeListener createListener() {
            return new OrderedRealmCollectionChangeListener() {
                @Override
                public void onChange(Object collection, OrderedCollectionChangeSet changeSet) {
                    if (getData() != null && getData().size() > 0) {
                        emptyView.setVisibility(View.GONE);
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    if (changeSet.getState() == OrderedCollectionChangeSet.State.INITIAL) {
                        loadingView.setVisibility(View.GONE);
                        notifyDataSetChanged();
                        return;
                    }
                    // For deletions, the adapter has to be notified in reverse order.
                    OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                    for (int i = deletions.length - 1; i >= 0; i--) {
                        OrderedCollectionChangeSet.Range range = deletions[i];
                        notifyItemRangeRemoved(range.startIndex, range.length);
                    }

                    OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                    for (OrderedCollectionChangeSet.Range range : insertions) {
                        notifyItemRangeInserted(range.startIndex, range.length);
                        //goToTop();
                    }

                    if (!updateOnModification) {
                        return;
                    }

                    OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                    for (OrderedCollectionChangeSet.Range range : modifications) {
                        notifyItemRangeChanged(range.startIndex, range.length);
                    }
                }
            };
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int i) {


            final RealmRoom mInfo = holder.mInfo = getItem(i);
            if (mInfo == null) {
                return;
            }

            if (isChatMultiSelectEnable) {
                holder.selectedRoomCB.setVisibility(View.VISIBLE);

                if (isItemAvailableOnSelectedList(mInfo)) {
                    holder.selectedRoomCB.setChecked(true);
                } else {
                    holder.selectedRoomCB.setChecked(false);
                }

            } else {
                holder.selectedRoomCB.setVisibility(View.GONE);
                holder.selectedRoomCB.setChecked(false);
            }
            final boolean isMyCloud;

            isMyCloud = mInfo.getChatRoom() != null && mInfo.getChatRoom().getPeerId() > 0 && mInfo.getChatRoom().getPeerId() == userId;

            if (mInfo.isValid()) {

                setLastMessage(mInfo, holder, isMyCloud);

                if (isMyCloud) {

                    holder.avatarIv.setImageResource(R.drawable.ic_cloud_space_blue);

                } else {
                    if (holder.avatarIv.getVisibility() == View.GONE) {
                        holder.avatarIv.setVisibility(View.VISIBLE);
                    }
                    setAvatar(mInfo, holder);
                }

                setChatIcon(mInfo, holder.chatIconTv);
                setMuteIcon(mInfo, holder.muteRoomTv);

                holder.roomNameTv.setText(mInfo.getTitle());

                if ((mInfo.getType() == CHAT) && mInfo.getChatRoom().isVerified()) {
                    holder.verifyRoomTv.setVisibility(View.VISIBLE);
                } else if ((mInfo.getType() == CHANNEL) && mInfo.getChannelRoom().isVerified()) {
                    holder.verifyRoomTv.setVisibility(View.VISIBLE);
                } else {
                    holder.verifyRoomTv.setVisibility(View.INVISIBLE);
                }

                if (mInfo.getLastMessage() != null && mInfo.getLastMessage().getUpdateOrCreateTime() != 0) {
                    holder.lastMessageTv.setText(HelperCalander.getTimeForMainRoom(mInfo.getLastMessage().getUpdateOrCreateTime()));
                } else {
                    holder.lastMessageTv.setVisibility(View.GONE);
                }

                /**
                 * ********************* unread *********************
                 */

                if (mInfo.isPinned()) {
                    if (mInfo.isFromPromote()) {
                        holder.pinView.setVisibility(View.GONE);
                    } else {
                        holder.pinView.setVisibility(View.VISIBLE);
                    }

                } else {
                    holder.pinView.setVisibility(View.GONE);
                }

                if (mInfo.getUnreadCount() < 1) {

                    holder.badgeView.setVisibility(View.GONE);

                } else {
                    holder.badgeView.setVisibility(View.VISIBLE);
                    holder.badgeView.getTextView().setText(mInfo.getUnreadCount() + "");

                    if (mInfo.getMute()) {
                        holder.badgeView.setBadgeColor(getResources().getColor(R.color.gray_9d));
                    } else {
                        if (G.isDarkTheme) {
                            holder.badgeView.setBadgeColor(getResources().getColor(R.color.md_blue_500));
                        } else {
                            holder.badgeView.setBadgeColor(getResources().getColor(R.color.notification_badge));
                        }
                    }
                }


            }

            /**
             * for change english number to persian number
             */
            if (HelperCalander.isPersianUnicode) {
                holder.LastMessageTv.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.LastMessageTv.getText().toString()));
                holder.badgeView.getTextView().setText(HelperCalander.convertToUnicodeFarsiNumber(holder.badgeView.getTextView().getText().toString()));
            }

        }

        private void setMuteIcon(RealmRoom mInfo, FontIconTextView muteTv) {
            if (mInfo.getMute())
                muteTv.setVisibility(View.VISIBLE);
            else
                muteTv.setVisibility(View.INVISIBLE);
        }


        private String subStringInternal(String text) {
            if (text == null || text.length() == 0) {
                return "";
            }

            ArrayList<Tuple<Integer, Integer>> boldPlaces = AbstractMessage.getBoldPlaces(text);
            text = AbstractMessage.removeBoldMark(text, boldPlaces);

            int subLength = 150;
            if (text.length() > subLength) {
                return text.substring(0, subLength);
            } else {
                return text;
            }
        }

        //*******************************************************************************************
        private void setLastMessage(RealmRoom mInfo, ViewHolder holder, boolean isMyCloud) {

            holder.lastMessageStatusTv.setVisibility(View.INVISIBLE);
            holder.LastMessageFileTextTv.setVisibility(View.GONE);
            holder.LastMessageTv.setText("");

            if (mInfo.getActionState() != null && ((mInfo.getType() == GROUP || mInfo.getType() == CHANNEL) || ((isMyCloud || (mInfo.getActionStateUserId() != G.userId))))) {

                holder.lastMessageSender.setVisibility(View.GONE);
                holder.LastMessageTv.setText(mInfo.getActionState());
                holder.LastMessageTv.setTextColor(ContextCompat.getColor(context, R.color.room_message_blue));
                holder.LastMessageTv.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            } else if (mInfo.getDraft() != null && !TextUtils.isEmpty(mInfo.getDraft().getMessage())) {
                ArrayList<Tuple<Integer, Integer>> boldPlaces = AbstractMessage.getBoldPlaces(mInfo.getDraft().getMessage());
                holder.LastMessageTv.setText(subStringInternal(AbstractMessage.removeBoldMark(mInfo.getDraft().getMessage(), boldPlaces)));
                holder.LastMessageTv.setTextColor(Color.parseColor(G.textSubTheme));
                holder.lastMessageSender.setVisibility(View.VISIBLE);
                holder.lastMessageSender.setText(R.string.txt_draft);
                holder.lastMessageSender.setTextColor(getResources().getColor(R.color.red));
                holder.lastMessageSender.setTypeface(G.typeface_IRANSansMobile);
            } else {

                if (mInfo.getLastMessage() != null) {
                    holder.lastMessageTv.setVisibility(View.VISIBLE);
                    String lastMessage = AppUtils.rightLastMessage(RealmRoomMessage.getFinalMessage(mInfo.getLastMessage()));

                    if (lastMessage == null) {
                        lastMessage = mInfo.getLastMessage().getMessage();
                    }

                    if (lastMessage == null || lastMessage.isEmpty()) {

                        holder.lastMessageSender.setVisibility(View.GONE);
                    } else {
                        if (mInfo.getLastMessage().isAuthorMe()) {

                            holder.lastMessageStatusTv.setVisibility(View.VISIBLE);

                            ProtoGlobal.RoomMessageStatus status = ProtoGlobal.RoomMessageStatus.UNRECOGNIZED;
                            if (mInfo.getLastMessage().getStatus() != null) {
                                try {
                                    status = ProtoGlobal.RoomMessageStatus.valueOf(mInfo.getLastMessage().getStatus());
                                } catch (RuntimeException e) {
                                    e.printStackTrace();
                                }
                            }
                            AppUtils.rightMessageStatus(holder.lastMessageStatusTv, status, mInfo.getLastMessage().isAuthorMe());
                        }

                        if (mInfo.getType() == GROUP) {
                            /**
                             * here i get latest message from chat history with chatId and
                             * get DisplayName with that . when login app client get latest
                             * message for each group from server , if latest message that
                             * send server and latest message that exist in client for that
                             * room be different latest message sender showing will be wrong
                             */

                            if (mInfo.getLastMessage().isAuthorMe()) {
                                holder.lastMessageSender.setText(holder.itemView.getResources().getString(R.string.txt_you));
                            } else {
                                holder.lastMessageSender.setText("");
                                if (mInfo.getLastMessage() != null) {
                                    RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmFragmentMain(), mInfo.getLastMessage().getUserId());
                                    if (realmRegisteredInfo != null && realmRegisteredInfo.getDisplayName() != null) {
                                        setSenderName(holder, realmRegisteredInfo.getDisplayName());
                                    }
                                }
                            }

                            holder.lastMessageSender.setVisibility(View.VISIBLE);
                            holder.lastMessageSender.setTextColor(Color.parseColor(G.roomSenderTextColor));
                        } else {
                            holder.lastMessageSender.setVisibility(View.GONE);
                        }

                        if (mInfo.getLastMessage() != null) {
                            ProtoGlobal.RoomMessageType _type, tmp;

                            _type = mInfo.getLastMessage().getMessageType();
                            String fileText = mInfo.getLastMessage().getMessage();

                            try {
                                if (mInfo.getLastMessage().getForwardMessage() != null) {
                                    tmp = mInfo.getLastMessage().getForwardMessage().getMessageType();
                                    if (tmp != null) {
                                        _type = tmp;
                                    }
                                    if (mInfo.getLastMessage().getForwardMessage().getMessage() != null) {
                                        fileText = mInfo.getLastMessage().getForwardMessage().getMessage();
                                    }
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                            String result = AppUtils.conversionMessageType(_type, holder.LastMessageTv, G.roomMessageTypeColor);
                            if (_type == ProtoGlobal.RoomMessageType.WALLET) {
                                String type;
                                if (mInfo.getLastMessage().getForwardMessage() != null) {
                                    type = mInfo.getLastMessage().getForwardMessage().getRoomMessageWallet().getType();
                                } else {
                                    type = mInfo.getLastMessage().getRoomMessageWallet().getType();
                                }

                                if (type.equals(CARD_TO_CARD.toString())) {
                                    result = G.fragmentActivity.getResources().getString(R.string.card_to_card_message);
                                } else if (type.equals(PAYMENT.toString())) {
                                    result = G.fragmentActivity.getResources().getString(R.string.payment_message);
                                } else if (type.equals(MONEY_TRANSFER.toString())) {
                                    result = G.fragmentActivity.getResources().getString(R.string.wallet_message);
                                } else {
                                    result = G.fragmentActivity.getResources().getString(R.string.unknown_message);
                                }
                                holder.LastMessageTv.setText(result);
                            }


                            if (result.isEmpty()) {
                                if (!HelperCalander.isPersianUnicode) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                        holder.LastMessageTv.setTextDirection(View.TEXT_DIRECTION_LTR);
                                    }
                                }
                                holder.LastMessageTv.setTextColor(Color.parseColor(G.textSubTheme));
                                holder.LastMessageTv.setText(subStringInternal(lastMessage));
                                holder.LastMessageTv.setEllipsize(TextUtils.TruncateAt.END);
                            } else {
                                if (fileText != null && !fileText.isEmpty()) {
                                    holder.LastMessageFileTextTv.setVisibility(View.VISIBLE);
                                    holder.LastMessageFileTextTv.setText(fileText);

                                    holder.LastMessageTv.setText(holder.LastMessageTv.getText() + " : ");
                                } else {
                                    holder.LastMessageFileTextTv.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            holder.LastMessageTv.setText(subStringInternal(lastMessage));
                        }
                    }
                } else {

                    holder.lastMessageSender.setVisibility(View.GONE);
                    holder.lastMessageTv.setVisibility(View.GONE);
                }
            }
        }

        private void setSenderName(ViewHolder holder, String _name) {
            if (_name.length() > 0) {
                String lastMessageSenderAsync;

                if (Character.getDirectionality(_name.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC) {
                    if (HelperCalander.isPersianUnicode) {
                        lastMessageSenderAsync = _name + ": ";
                    } else {
                        lastMessageSenderAsync = " :" + _name;
                    }
                } else {
                    if (HelperCalander.isPersianUnicode) {
                        lastMessageSenderAsync = " :" + _name;
                    } else {
                        lastMessageSenderAsync = _name + ": ";
                    }
                }
                holder.lastMessageSender.setText(lastMessageSenderAsync);
            }
        }

        private void setAvatar(final RealmRoom mInfo, ViewHolder holder) {
            long idForGetAvatar;
            if (mInfo.getType() == CHAT) {
                idForGetAvatar = mInfo.getChatRoom().getPeerId();
            } else {
                idForGetAvatar = mInfo.getId();
            }

            Bitmap init = HelperImageBackColor.drawAlphabetOnPicture((int) context.getResources().getDimension(R.dimen.dp52), mInfo.getInitials(), mInfo.getColor());
            avatarHandler.getAvatar(new ParamWithInitBitmap(holder.avatarIv, idForGetAvatar).initBitmap(init));
        }

        /**
         * change muteRoomTv icon for channel pv and group chats
         * font type face FontIconTextView
         */
        private void setChatIcon(RealmRoom mInfo, FontIconTextView chatIconTv) {
            if (mInfo.getType() == CHAT) {
                chatIconTv.setVisibility(View.GONE);
                chatIconTv.setText("");
            } else if (mInfo.getType() == GROUP) {
                chatIconTv.setVisibility(View.VISIBLE);
                chatIconTv.setText(";");
            } else if (mInfo.getType() == CHANNEL) {
                chatIconTv.setVisibility(View.VISIBLE);
                chatIconTv.setText(":");
            } else {
                chatIconTv.setVisibility(View.GONE);
            }
        }

        //*******************************************************************************************

        public class ViewHolder extends RecyclerView.ViewHolder {

            private CircleImageView avatarIv;
            private EmojiTextViewE roomNameTv;
            private FontIconTextView muteRoomTv;
            private RealmRoom mInfo;
            private EmojiTextViewE LastMessageTv;
            private EmojiTextViewE LastMessageFileTextTv;
            private FontIconTextView chatIconTv;
            private TextView lastMessageTv;
            private View pinView;
            private FontIconTextView verifyRoomTv;
            private BadgeView badgeView;
            private EmojiTextViewE lastMessageSender;
            private FontIconTextView lastMessageStatusTv;
            private View root;
            private CheckBox selectedRoomCB;


            public ViewHolder(View view) {
                super(view);

                root = view;
                /**
                 * user avatar avatarIv
                 * */
                avatarIv = view.findViewById(R.id.iv_chatCell_userAvatar);

                /**
                 * multi select room list
                 * */
                selectedRoomCB = view.findViewById(R.id.iv_itemContactChat_checkBox);

                /**
                 * user roomNameTv
                 * */
                roomNameTv = view.findViewById(R.id.tv_chatCell_roomName);
                roomNameTv.setTypeface(G.typeface_IRANSansMobile_Bold);

                LastMessageTv = view.findViewById(R.id.tv_chatCell_secondTextView);
                LastMessageFileTextTv = view.findViewById(R.id.tv_chatCell_thirdTextView);

                /**
                 * channel or group icon
                 * */
                chatIconTv = view.findViewById(R.id.tv_chatCell_chatIcon);

                /**
                 * sended message time
                 * */
                lastMessageTv = view.findViewById(R.id.tv_chatCell_messageData);
                lastMessageTv.setTypeface(G.typeface_IRANSansMobile);

                /**
                 * pin icon
                 * */
                pinView = view.findViewById(R.id.iv_iv_chatCell_pin);

                /**
                 * verify imageView
                 * */
                verifyRoomTv = view.findViewById(R.id.tv_chatCell_verify);

                /**
                 * unread text counter
                 * */
                badgeView = view.findViewById(R.id.iv_chatCell_messageCount);
                badgeView.getTextView().setTypeface(G.typeface_IRANSansMobile);

                /**
                 * muteRoomTv icon
                 * */
                muteRoomTv = view.findViewById(R.id.iv_chatCell_mute);

                /**
                 * last message sender roomNameTv
                 * */
                lastMessageSender = view.findViewById(R.id.tv_chatCell_firstTextView);
                lastMessageSender.setTypeface(G.typeface_IRANSansMobile);


                /**
                 * message status
                 * */
                lastMessageStatusTv = view.findViewById(R.id.iv_chatCell_messageStatus);


                root.setOnClickListener(v -> {

                    if (isChatMultiSelectEnable) {

                        onChatCellClickedInEditMode.onClicked(selectedRoomCB, mInfo, getAdapterPosition(), selectedRoomCB.isChecked());


                    } else {

                        if (ActivityMain.isMenuButtonAddShown) {
                            mComplete.complete(true, "closeMenuButton", "");
                        } else {
                            if (mInfo.isValid() && G.fragmentActivity != null) {

                                boolean openChat = true;

                                if (G.twoPaneMode) {
                                    if (getActivity() != null) {
                                        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(FragmentChat.class.getName());
                                        if (fragment != null) {

                                            FragmentChat fm = (FragmentChat) fragment;
                                            if (fm.isAdded() && fm.mRoomId == mInfo.getId()) {
                                                openChat = false;
                                            } else {
                                                removeFromBaseFragment(fragment);
                                            }
                                        }
                                    }
                                }

                                if (openChat) {
                                    new GoToChatActivity(mInfo.getId()).startActivity(getActivity());

                                }
                            }
                        }

                    }
                });

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if (isChatMultiSelectEnable) return false;
                        if (ActivityMain.isMenuButtonAddShown) {

                            if (mComplete != null) {
                                mComplete.complete(true, "closeMenuButton", "");
                            }

                        } else {
                            if (mInfo.isValid() && G.fragmentActivity != null) {
                                String role = null;
                                if (mInfo.getType() == GROUP) {
                                    role = mInfo.getGroupRoom().getRole().toString();
                                } else if (mInfo.getType() == CHANNEL) {
                                    role = mInfo.getChannelRoom().getRole().toString();
                                }

                                if (!G.fragmentActivity.isFinishing()) {
                                    long peerId = mInfo.getChatRoom() != null ? mInfo.getChatRoom().getPeerId() : 0;
                                    MyDialog.showDialogMenuItemRooms(G.fragmentActivity, mInfo.getTitle(), mInfo.getType(), mInfo.getMute(), role, peerId, mInfo, new OnComplete() {
                                        @Override
                                        public void complete(boolean result, String messageOne, String MessageTow) {
                                            onSelectRoomMenu(messageOne, mInfo);
                                        }
                                    }, mInfo.isPinned());
                                }
                            }
                        }
                        return true;
                    }
                });
            }

        }
    }

}
