package net.iGap.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import io.realm.Sort;
import java.util.List;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityChat;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityProfile;
import net.iGap.activities.MyDialog;
import net.iGap.helper.FontCache;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperClientCondition;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnComplete;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.DeviceUtils;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.enums.RoomType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestChannelDelete;
import net.iGap.request.RequestChannelLeft;
import net.iGap.request.RequestChatDelete;
import net.iGap.request.RequestClientCondition;
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.request.RequestGroupDelete;
import net.iGap.request.RequestGroupLeft;

import static net.iGap.G.clientConditionGlobal;
import static net.iGap.G.firstTimeEnterToApp;
import static net.iGap.G.userId;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;
import static net.iGap.realm.RealmRoom.putChatToDatabase;


public class FragmentMain extends Fragment implements OnComplete {

    public static final String STR_MAIN_TYPE = "STR_MAIN_TYPE";

    public static boolean isMenuButtonAddShown = false;

    ProgressBar progressBar;

    private SwipeRefreshLayout swipeRefreshLayout;

    private int mOffset = 0;
    private int mLimit = 50;
    boolean isSendRequestForLoading = false;
    boolean isThereAnyMoreItemToLoad = true;

    private RealmRecyclerView mRecyclerView;
    private MainType mainType;


    public enum MainType {
        all, chat, group, channel
    }

    public static FragmentMain newInstance(MainType mainType) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(STR_MAIN_TYPE, mainType);
        FragmentMain fragment = new FragmentMain();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.activity_main_rooms, container, false);
        return fragmentView;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainType = (MainType) getArguments().getSerializable(STR_MAIN_TYPE);


        progressBar = (ProgressBar) view.findViewById(R.id.ac_progress_bar_waiting);
        AppUtils.setProgresColler(progressBar);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setEnabled(false);

        initRecycleView(view);
        initListener();

    }

    //***************************************************************************************************************************

    private void initRecycleView(View view) {

        mRecyclerView = (RealmRecyclerView) view.findViewById(R.id.cl_recycler_view_contact);
        //mRecyclerView.setItemViewCacheSize(50);
        mRecyclerView.setDrawingCacheEnabled(true);

        PreCachingLayoutManager preCachingLayoutManager = new PreCachingLayoutManager(getActivity());
        mRecyclerView.getRecycleView().setLayoutManager(preCachingLayoutManager);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.getRecycleView().setLayoutManager(mLayoutManager);

        preCachingLayoutManager.setExtraLayoutSpace(DeviceUtils.getScreenHeight(getActivity()));

        RealmResults<RealmRoom> results = null;

        switch (mainType) {

            case all:
                results = ((ActivityMain) getActivity()).getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).
                    equalTo(RealmRoomFields.IS_DELETED, false).findAllSorted(RealmRoomFields.UPDATED_TIME, Sort.DESCENDING);

                break;
            case chat:
                results = ((ActivityMain) getActivity()).getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).
                    equalTo(RealmRoomFields.IS_DELETED, false).equalTo(RealmRoomFields.TYPE, RoomType.CHAT.toString()).findAllSorted(RealmRoomFields.UPDATED_TIME, Sort.DESCENDING);

                break;
            case group:
                results = ((ActivityMain) getActivity()).getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).
                    equalTo(RealmRoomFields.IS_DELETED, false).equalTo(RealmRoomFields.TYPE, RoomType.GROUP.toString()).findAllSorted(RealmRoomFields.UPDATED_TIME, Sort.DESCENDING);

                break;
            case channel:
                results = ((ActivityMain) getActivity()).getRealm().where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).
                    equalTo(RealmRoomFields.IS_DELETED, false).equalTo(RealmRoomFields.TYPE, RoomType.CHANNEL.toString()).findAllSorted(RealmRoomFields.UPDATED_TIME, Sort.DESCENDING);

                break;
        }

        RoomAdapter roomAdapter = new RoomAdapter(getActivity(), results, this);
        mRecyclerView.setAdapter(roomAdapter);

        if (mainType == MainType.all) {

            getChatsList();

        /*    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

                @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (isThereAnyMoreItemToLoad) {
                        if (!isSendRequestForLoading) {

                            //int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                            //
                            //if (lastVisiblePosition + 10 >= mOffset) {
                            isSendRequestForLoading = true;

                            //  mOffset = mRecyclerView.getRecycleView().getAdapter().getItemCount();
                            new RequestClientGetRoomList().clientGetRoomList(mOffset, mLimit);
                            progressBar.setVisibility(View.VISIBLE);
                            // }
                        }
                    }
                }
            };

            mRecyclerView.getRecycleView().addOnScrollListener(onScrollListener);*/
        }



      /*  swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                if (heartBeatTimeOut()) {
                    WebSocketClient.checkConnection();
                }
                if (isSendRequestForLoading == false) {

                    mOffset = 0;
                    isThereAnyMoreItemToLoad = true;
                    new RequestClientGetRoomList().clientGetRoomList(mOffset, mLimit);
                    isSendRequestForLoading = true;
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        //   swipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.room_message_blue, R.color.accent);

        swipeRefreshLayout.setColorSchemeColors(Color.parseColor(G.progressColor));*/

        mRecyclerView.getRecycleView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (((ActivityMain) getActivity()).arcMenu.isMenuOpened()) {
                    ((ActivityMain) getActivity()).arcMenu.toggleMenu();
                }

                if (dy > 0) {
                    // Scroll Down
                    if (((ActivityMain) getActivity()).arcMenu.fabMenu.isShown()) {
                        ((ActivityMain) getActivity()).arcMenu.fabMenu.hide();
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!((ActivityMain) getActivity()).arcMenu.fabMenu.isShown()) {
                        ((ActivityMain) getActivity()).arcMenu.fabMenu.show();
                    }
                }
            }
        });




    }



    private void initListener() {

        switch (mainType) {

            case all:

                ((ActivityMain) getActivity()).mainActionApp = new ActivityMain.MainInterface() {
                    @Override public void onAction(ActivityMain.MainAction action) {
                        doAction(action);
                    }
                };

                ((ActivityMain) getActivity()).mainInterfaceGetRoomList = new ActivityMain.MainInterfaceGetRoomList() {
                    @Override public void onClientGetRoomList(List<ProtoGlobal.Room> roomList, ProtoResponse.Response response, boolean fromLogin) {

                        onclientGetRoomList(roomList, response, fromLogin);
                    }

                    @Override public void onError(int majorCode, int minorCode) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override public void run() {
                                swipeRefreshLayout.setRefreshing(false);// swipe refresh is complete and gone
                            }
                        });

                        if (majorCode == 9) {
                            if (G.currentActivity != null) {
                                G.currentActivity.finish();
                            }
                            Intent intent = new Intent(G.context, ActivityProfile.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            G.context.startActivity(intent);
                        }
                    }

                    @Override public void onTimeout() {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override public void run() {
                                progressBar.setVisibility(View.GONE);
                                firstTimeEnterToApp = false;
                                getChatsList();
                                swipeRefreshLayout.setRefreshing(false);// swipe refresh is complete and gone
                            }
                        });
                    }
                };

                break;
            case chat:
                ((ActivityMain) getActivity()).mainActionChat = new ActivityMain.MainInterface() {
                    @Override public void onAction(ActivityMain.MainAction action) {
                        doAction(action);
                    }
                };
                break;
            case group:
                ((ActivityMain) getActivity()).mainActionGroup = new ActivityMain.MainInterface() {
                    @Override public void onAction(ActivityMain.MainAction action) {
                        doAction(action);
                    }
                };
                break;
            case channel:
                ((ActivityMain) getActivity()).mainActionChannel = new ActivityMain.MainInterface() {
                    @Override public void onAction(ActivityMain.MainAction action) {
                        doAction(action);
                    }
                };
                break;
        }
    }

    private void doAction(ActivityMain.MainAction action) {

        switch (action) {

            case downScrool:

                ((Activity) getActivity()).runOnUiThread(new Runnable() {
                    @Override public void run() {
                        int firstVisibleItem = ((LinearLayoutManager) mRecyclerView.getRecycleView().getLayoutManager()).findFirstVisibleItemPosition();
                        if (firstVisibleItem < 5) {
                            mRecyclerView.getRecycleView().scrollToPosition(0);
                        }
                    }
                });

                break;
            case clinetCondition:
                getActivity().runOnUiThread(new Runnable() {
                    @Override public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

                break;
        }
    }

    private void onclientGetRoomList(List<ProtoGlobal.Room> roomList, ProtoResponse.Response response, boolean fromLogin) {
        if (fromLogin) {
            mOffset = 0;
        }

        boolean deleteBefore = false;
        if (mOffset == 0) {
            deleteBefore = true;
        }

        if (roomList.size() > 0) {
            putChatToDatabase(roomList, deleteBefore, false);
            isThereAnyMoreItemToLoad = true;
        } else {
            putChatToDatabase(roomList, deleteBefore, true);
            isThereAnyMoreItemToLoad = false;
        }

        /**
         * to first enter to app , client first compute clientCondition then
         * getRoomList and finally send condition that before get clientCondition;
         * in else state compute new client condition with latest messaging state
         */
        if (firstTimeEnterToApp) {
            firstTimeEnterToApp = false;
            sendClientCondition();
        } else if (fromLogin || mOffset == 0) {

            new Thread(new Runnable() {
                @Override public void run() {
                    new RequestClientCondition().clientCondition(HelperClientCondition.computeClientCondition(null));
                }
            }).start();
        }

        mOffset += roomList.size();

        getActivity().runOnUiThread(new Runnable() {
            @Override public void run() {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);// swipe refresh is complete and gone
            }
        });

        isSendRequestForLoading = false;

        if (isThereAnyMoreItemToLoad) {
            isSendRequestForLoading = true;
            new RequestClientGetRoomList().clientGetRoomList(mOffset, mLimit);

            getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });


        }



    }

    //***************************************************************************************************************************

    private boolean heartBeatTimeOut() {

        long difference;

        long currentTime = System.currentTimeMillis();
        difference = (currentTime - G.latestHearBeatTime);

        if (difference >= Config.HEART_BEAT_CHECKING_TIME_OUT) {
            return true;
        }

        return false;
    }

    private void sendClientCondition() {
        if (clientConditionGlobal != null) {
            new RequestClientCondition().clientCondition(clientConditionGlobal);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override public void run() {
                    sendClientCondition();
                }
            }, 1000);
        }
    }

    private void testIsSecure() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                if (G.isSecure && G.userLogin) {

                    mOffset = 0;
                    new RequestClientGetRoomList().clientGetRoomList(mOffset, mLimit);
                    isSendRequestForLoading = true;
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    testIsSecure();
                }
            }
        }, 1000);
    }

    private void getChatsList() {
        if (firstTimeEnterToApp) {
            testIsSecure();
        }

        if (G.deletedRoomList.size() > 0) {
            cleanDeletedRooms();
        }
    }

    private void cleanDeletedRooms() {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {

                        for (int i = 0; i < G.deletedRoomList.size(); i++) {

                            RealmRoom _RealmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, true).equalTo(RealmRoomFields.KEEP_ROOM, false).
                                equalTo(RealmRoomFields.ID, G.deletedRoomList.get(i)).findFirst();

                            if (_RealmRoom != null) {
                                RealmRoom.deleteRoom(_RealmRoom.getId());
                            }
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override public void onSuccess() {
                        realm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override public void onError(Throwable error) {
                        realm.close();
                    }
                });
            }
        });

        G.deletedRoomList.clear();
    }

    private void muteNotification(final Long id, final boolean mute) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, id).findFirst().setMute(!mute);
            }
        });
        realm.close();
    }

    private void clearHistory(Long id) {
        RealmRoomMessage.clearHistoryMessage(id);
    }

    private void onSelectRoomMenu(String message, RealmRoom item) {
        if (checkValidationForRealm(item)) {
            switch (message) {
                case "txtMuteNotification":
                    muteNotification(item.getId(), item.getMute());
                    break;
                case "txtClearHistory":
                    clearHistory(item.getId());
                    break;
                case "txtDeleteChat":
                    if (item.getType() == ProtoGlobal.Room.Type.CHAT) {
                        new RequestChatDelete().chatDelete(item.getId());
                    } else if (item.getType() == GROUP) {
                        if (item.getGroupRoom().getRole() == GroupChatRole.OWNER) {
                            new RequestGroupDelete().groupDelete(item.getId());
                        } else {
                            new RequestGroupLeft().groupLeft(item.getId());
                        }
                    } else if (item.getType() == CHANNEL) {
                        if (item.getChannelRoom().getRole() == ChannelChatRole.OWNER) {
                            new RequestChannelDelete().channelDelete(item.getId());
                        } else {
                            new RequestChannelLeft().channelLeft(item.getId());
                        }
                    }
                    break;
            }
        }
    }

    private boolean checkValidationForRealm(RealmRoom realmRoom) {
        if (realmRoom != null && realmRoom.isManaged() && realmRoom.isValid() && realmRoom.isLoaded()) {
            return true;
        }
        return false;
    }





    //************************

    @Override public void complete(boolean result, String messageOne, String MessageTow) {
        if (messageOne.equals("closeMenuButton")) {
            ((ActivityMain) getActivity()).arcMenu.toggleMenu();
        }
    }
    //**************************************************************************************************************************************

    public class PreCachingLayoutManager extends LinearLayoutManager {
        private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 600;
        private int extraLayoutSpace = -1;
        private Context context;

        public PreCachingLayoutManager(Context context) {
            super(context);
            this.context = context;
        }

        public PreCachingLayoutManager(Context context, int extraLayoutSpace) {
            super(context);
            this.context = context;
            this.extraLayoutSpace = extraLayoutSpace;
        }

        public PreCachingLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
            this.context = context;
        }

        public void setExtraLayoutSpace(int extraLayoutSpace) {
            this.extraLayoutSpace = extraLayoutSpace;
        }

        @Override protected int getExtraLayoutSpace(RecyclerView.State state) {
            if (extraLayoutSpace > 0) {
                return extraLayoutSpace;
            }
            return DEFAULT_EXTRA_LAYOUT_SPACE;
        }

        private static final float MILLISECONDS_PER_INCH = 2000f; //default is 25f (bigger = slower)

        @Override public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {

            final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

                @Override public PointF computeScrollVectorForPosition(int targetPosition) {
                    return PreCachingLayoutManager.this.computeScrollVectorForPosition(targetPosition);
                }

                @Override protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                }
            };

            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }
    }

    public class RoomAdapter extends RealmBasedRecyclerViewAdapter<RealmRoom, RoomAdapter.ViewHolder> {

        public OnComplete mComplete;
        public String action;
        private Typeface typeFaceIcon;

        public RoomAdapter(Context context, RealmResults<RealmRoom> realmResults, OnComplete complete) {
            super(context, realmResults, true, false, false, "");
            this.mComplete = complete;
        }

        @Override public RoomAdapter.ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.chat_sub_layout, viewGroup, false);
            return new RoomAdapter.ViewHolder(v);
        }

        @Override public void onBindRealmViewHolder(final ViewHolder holder, final int i) {

            LinearLayout lytContainer4 = (LinearLayout) holder.itemView.findViewById(R.id.lytContainer4);
            LinearLayout lytContainer5 = (LinearLayout) holder.itemView.findViewById(R.id.lytContainer5);
            LinearLayout lytContainer6 = (LinearLayout) holder.itemView.findViewById(R.id.lytContainer6);
            LinearLayout lytContainer7 = (LinearLayout) holder.itemView.findViewById(R.id.lytContainer7);

            RealmRoom mInfo = holder.mInfo = realmResults.get(i);

            if (mInfo != null && mInfo.isValid()) {
                if (mInfo.getActionState() != null && ((mInfo.getType() == GROUP || mInfo.getType() == CHANNEL) || ((RealmRoom.isCloudRoom(mInfo.getId()) || (mInfo.getActionStateUserId()
                    != userId))))) {
                    removeView(lytContainer5, R.id.lyt_message_sender_room);

                    addView(holder, lytContainer5, R.layout.room_layout_last_message, R.id.lyt_last_message_room, lytContainer5.getChildCount());
                    TextView txtLastMessage = (TextView) holder.itemView.findViewById(R.id.cs_txt_last_message);
                    txtLastMessage.setText(mInfo.getActionState());
                    txtLastMessage.setTextColor(ContextCompat.getColor(G.context, R.color.room_message_blue));

                    addView(holder, lytContainer5, R.layout.room_layout_avi, R.id.lyt_avi_room, lytContainer5.getChildCount());
                    (holder.itemView.findViewById(R.id.cs_avi)).setVisibility(View.VISIBLE);
                } else if (mInfo.getDraft() != null && !TextUtils.isEmpty(mInfo.getDraft().getMessage())) {

                    addView(holder, lytContainer5, R.layout.room_layout_last_message, R.id.lyt_last_message_room, lytContainer5.getChildCount());
                    TextView txtLastMessage = (TextView) holder.itemView.findViewById(R.id.cs_txt_last_message);
                    txtLastMessage.setText(subStringInternal(mInfo.getDraft().getMessage()));
                    txtLastMessage.setTextColor(ContextCompat.getColor(G.context, R.color.room_message_gray));

                    addView(holder, lytContainer5, R.layout.room_layout_message_sender, R.id.lyt_message_sender_room, 0);
                    TextView txtView = (TextView) holder.itemView.findViewById(R.id.cs_txt_last_message_sender);
                    txtView.setText(R.string.txt_draft);
                    txtView.setTextColor(Color.parseColor("#ff4644"));
                    txtView.setTypeface(FontCache.get("fonts/IRANSansMobile.ttf", G.context));
                    removeView(lytContainer5, R.id.lyt_avi_room);
                    removeView(lytContainer7, R.id.lyt_tic_room);
                } else {
                    removeView(lytContainer5, R.id.lyt_avi_room);

                    if (mInfo.getLastMessage() != null) {
                        String lastMessage = AppUtils.rightLastMessage(mInfo.getId(), holder.itemView.getResources(), mInfo.getType(), mInfo.getLastMessage(),
                            mInfo.getLastMessage().getForwardMessage() != null ? mInfo.getLastMessage().getForwardMessage().getAttachment() : mInfo.getLastMessage().getAttachment());

                        if (lastMessage == null) {
                            lastMessage = mInfo.getLastMessage().getMessage();
                        }

                        if (lastMessage == null || lastMessage.isEmpty()) {
                            removeView(lytContainer7, R.id.lyt_tic_room);
                            removeView(lytContainer5, R.id.lyt_message_sender_room);
                            removeView(lytContainer5, R.id.lyt_last_message_room);
                        } else {
                            if (mInfo.getLastMessage().isAuthorMe()) {
                                addView(holder, lytContainer7, R.layout.room_layout_tic, R.id.lyt_tic_room, 0);
                                AppUtils.rightMessageStatus((ImageView) holder.itemView.findViewById(R.id.cslr_txt_tic), ProtoGlobal.RoomMessageStatus.valueOf(mInfo.getLastMessage().getStatus()),
                                    mInfo.getLastMessage().isAuthorMe());
                            } else {
                                removeView(lytContainer7, R.id.lyt_tic_room);
                            }

                            if (mInfo.getType() == GROUP) {
                                /**
                                 * here i get latest message from chat history with chatId and
                                 * get DisplayName with that . when login app client get latest
                                 * message for each group from server , if latest message that
                                 * send server and latest message that exist in client for that
                                 * room be different latest message sender showing will be wrong
                                 */

                                String lastMessageSender = "";
                                if (mInfo.getLastMessage().isAuthorMe()) {
                                    lastMessageSender = holder.itemView.getResources().getString(R.string.txt_you);
                                } else {
                                    Realm realm = Realm.getDefaultInstance();
                                    RealmRegisteredInfo realmRegisteredInfo =
                                        realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, mInfo.getLastMessage().getUserId()).findFirst();
                                    if (realmRegisteredInfo != null && realmRegisteredInfo.getDisplayName() != null) {

                                        String _name = realmRegisteredInfo.getDisplayName();
                                        if (_name.length() > 0) {

                                            if (Character.getDirectionality(_name.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC) {
                                                if (HelperCalander.isLanguagePersian) {
                                                    lastMessageSender = _name + ": ";
                                                } else {
                                                    lastMessageSender = " :" + _name;
                                                }
                                            } else {
                                                if (HelperCalander.isLanguagePersian) {
                                                    lastMessageSender = " :" + _name;
                                                } else {
                                                    lastMessageSender = _name + ": ";
                                                }
                                            }
                                        }
                                    }
                                    realm.close();
                                }

                                addView(holder, lytContainer5, R.layout.room_layout_message_sender, R.id.lyt_message_sender_room, 0);
                                TextView txtMessageSender = (TextView) holder.itemView.findViewById(R.id.cs_txt_last_message_sender);
                                txtMessageSender.setText(lastMessageSender);
                                txtMessageSender.setTextColor(Color.parseColor("#2bbfbd"));
                                txtMessageSender.setTypeface(FontCache.get("fonts/IRANSansMobile.ttf", G.context));
                            } else {
                                removeView(lytContainer5, R.id.lyt_message_sender_room);
                            }

                            addView(holder, lytContainer5, R.layout.room_layout_last_message, R.id.lyt_last_message_room, lytContainer5.getChildCount());
                            TextView txtLastMessage = (TextView) holder.itemView.findViewById(R.id.cs_txt_last_message);

                            if (mInfo.getLastMessage() != null) {
                                ProtoGlobal.RoomMessageType _type, tmp;

                                _type = mInfo.getLastMessage().getMessageType();

                                try {
                                    if (mInfo.getLastMessage().getReplyTo() != null) {
                                        tmp = mInfo.getLastMessage().getReplyTo().getMessageType();
                                        if (tmp != null) _type = tmp;
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    if (mInfo.getLastMessage().getForwardMessage() != null) {
                                        tmp = mInfo.getLastMessage().getForwardMessage().getMessageType();
                                        if (tmp != null) _type = tmp;
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }

                                String result = AppUtils.conversionMessageType(_type, txtLastMessage, R.color.room_message_blue);
                                if (result.isEmpty()) {
                                    if (!HelperCalander.isLanguagePersian) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                            txtLastMessage.setTextDirection(View.TEXT_DIRECTION_LTR);
                                        }
                                    }
                                    txtLastMessage.setTextColor(ContextCompat.getColor(G.context, R.color.room_message_gray));
                                    txtLastMessage.setText(subStringInternal(lastMessage));
                                }
                            } else {
                                txtLastMessage.setText(subStringInternal(lastMessage));
                            }
                        }
                    } else {
                        removeView(lytContainer5, R.id.lyt_last_message_room);
                        removeView(lytContainer5, R.id.lyt_message_sender_room);
                        removeView(lytContainer7, R.id.lyt_time_room);
                        removeView(lytContainer7, R.id.lyt_tic_room);
                    }
                }

                long idForGetAvatar;
                HelperAvatar.AvatarType avatarType;
                if (mInfo.getType() == ProtoGlobal.Room.Type.CHAT) {
                    idForGetAvatar = mInfo.getChatRoom().getPeerId();
                    avatarType = HelperAvatar.AvatarType.USER;
                } else {
                    idForGetAvatar = mInfo.getId();
                    avatarType = HelperAvatar.AvatarType.ROOM;
                }

                HelperAvatar.getAvatar(idForGetAvatar, avatarType, new OnAvatarGet() {
                    @Override public void onAvatarGet(String avatarPath, long roomId) {
                        G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), holder.image);
                    }

                    @Override public void onShowInitials(String initials, String color) {
                        holder.image.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) holder.itemView.getContext().getResources().getDimension(R.dimen.dp52), initials, color));
                    }
                });

                /**
                 * ********************* chat icon *********************
                 */
                if (mInfo.getType() == ProtoGlobal.Room.Type.CHAT) {
                    removeView(lytContainer4, R.id.lyt_chat_icon_room);
                } else {
                    addView(holder, lytContainer4, R.layout.room_layout_chat_icon, R.id.lyt_chat_icon_room, 0);

                    LinearLayout lytChatIcon = (LinearLayout) holder.itemView.findViewById(R.id.lyt_chat_icon_room);
                    if (G.selectedLanguage.equals("en")) {
                        lytChatIcon.setPadding(0, 0, (int) getResources().getDimension(R.dimen.dp8), 0);
                    } else {
                        lytChatIcon.setPadding((int) getResources().getDimension(R.dimen.dp8), 0, 0, 0);
                    }

                    TextView txtChatIcon = (TextView) holder.itemView.findViewById(R.id.cs_txt_chat_icon);
                    if (mInfo.getType() == GROUP) {
                        typeFaceIcon = Typeface.createFromAsset(G.context.getAssets(), "fonts/MaterialIcons-Regular.ttf");
                        txtChatIcon.setText(getStringChatIcon(RoomType.GROUP));
                    } else if (mInfo.getType() == CHANNEL) {
                        typeFaceIcon = Typeface.createFromAsset(G.context.getAssets(), "fonts/iGap_font.ttf");
                        txtChatIcon.setText(getStringChatIcon(RoomType.CHANNEL));
                    }
                    txtChatIcon.setTypeface(typeFaceIcon);
                }

                holder.name.setText(mInfo.getTitle());

                if (mInfo.getLastMessage() != null && mInfo.getLastMessage().getUpdateOrCreateTime() != 0) {
                    addView(holder, lytContainer7, R.layout.room_layout_time, R.id.lyt_time_room, lytContainer7.getChildCount());
                    TextView txtTime = ((TextView) holder.itemView.findViewById(R.id.cs_txt_contact_time));
                    txtTime.setText(HelperCalander.getTimeForMainRoom(mInfo.getLastMessage().getUpdateOrCreateTime()));
                    txtTime.setTypeface(FontCache.get("fonts/IRANSansMobile.ttf", G.context));
                } else {
                    removeView(lytContainer7, R.id.lyt_time_room);
                }

                /**
                 * ********************* unread *********************
                 */
                if (mInfo.getUnreadCount() < 1) {
                    removeView(lytContainer6, R.id.lyt_unread_room);
                } else {
                    addView(holder, lytContainer6, R.layout.room_layout_unread, R.id.lyt_unread_room, lytContainer6.getChildCount());

                    TextView txtUnread = (TextView) holder.itemView.findViewById(R.id.cs_txt_unread_message);
                    txtUnread.setText(Integer.toString(mInfo.getUnreadCount()));
                    if (mInfo.getMute()) {
                        txtUnread.setBackgroundResource(R.drawable.oval_gray);
                    } else {
                        AndroidUtils.setBackgroundShapeColor(txtUnread, Color.parseColor(G.notificationColor));
                    }
                }

                /**
                 * ********************* mute *********************
                 * hint : message status should be added before mute
                 * for observance order with mute icon
                 */
                if (mInfo.getMute()) {
                    if (holder.itemView.findViewById(R.id.lyt_mute_room) == null) {
                        addView(holder, lytContainer7, R.layout.room_layout_mute, R.id.cs_txt_mute, 0);
                    }
                } else {
                    removeView(lytContainer7, R.id.lyt_mute_room);
                }
            }

            /**
             * for change english number to persian number
             */
            if (HelperCalander.isLanguagePersian) {
                TextView txtLastMessage = (TextView) holder.itemView.findViewById(R.id.cs_txt_last_message);
                if (txtLastMessage != null) {
                    txtLastMessage.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastMessage.getText().toString()));
                }

                TextView txtUnread = (TextView) holder.itemView.findViewById(R.id.cs_txt_unread_message);
                if (txtUnread != null) {
                    txtUnread.setText(HelperCalander.convertToUnicodeFarsiNumber(txtUnread.getText().toString()));
                }

                holder.name.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.name.getText().toString()));
            }

            TextView txtLastMessage = (TextView) holder.itemView.findViewById(R.id.cs_txt_last_message);
            if (txtLastMessage != null) {
                txtLastMessage.setTypeface(FontCache.get("fonts/IRANSansMobile.ttf", G.context));
            }
        }

        private String subStringInternal(String text) {

            if (text == null || text.length() == 0) {
                return "";
            }

            int subLengh = 50;

            if (text.length() > subLengh) {
                return text.substring(0, subLengh);
            } else {
                return text;
            }
        }

        /**
         * add element to view
         *
         * @param holder holder of recycler
         * @param layout chat_sub_layout
         * @param inflateLayout layout that inflated for showing
         * @param parentId parent id in inflated layout
         * @param position position for add element to view
         */
        private void addView(ViewHolder holder, LinearLayout layout, int inflateLayout, int parentId, int position) {
            if (holder.itemView.findViewById(parentId) == null) {
                View muteView = LayoutInflater.from(G.context).inflate(inflateLayout, null);
                layout.addView(muteView, position);
            }
        }

        /**
         * remove element from view
         *
         * @param container parent of view
         * @param id view id
         */
        private void removeView(LinearLayout container, int id) {
            for (int i = 0; i < container.getChildCount(); i++) {
                if (container.getChildAt(i).getId() == id) {
                    container.removeViewAt(i);
                    return;
                }
            }
        }

        public class ViewHolder extends RealmViewHolder {

            RealmRoom mInfo;
            protected CircleImageView image;
            protected EmojiTextViewE name;

            public ViewHolder(View view) {
                super(view);

                image = (CircleImageView) view.findViewById(R.id.cs_img_contact_picture);
                name = (EmojiTextViewE) view.findViewById(R.id.cs_txt_contact_name);

                //AndroidUtils.setBackgroundShapeColor(unreadMessage, Color.parseColor(G.notificationColor));

                view.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {

                        if (ActivityMain.isMenuButtonAddShown) {
                            mComplete.complete(true, "closeMenuButton", "");
                        } else {
                            if (mInfo.isValid()) {

                                Intent intent = new Intent(getActivity(), ActivityChat.class);
                                intent.putExtra("RoomId", mInfo.getId());

                                startActivity(intent);
                                getActivity().overridePendingTransition(0, 0);

                                if (((ActivityMain) getActivity()).arcMenu != null && ((ActivityMain) getActivity()).arcMenu.isMenuOpened()) {
                                    ((ActivityMain) getActivity()).arcMenu.toggleMenu();
                                }
                            }
                        }
                    }
                });

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override public boolean onLongClick(View v) {

                        if (ActivityMain.isMenuButtonAddShown) {
                            mComplete.complete(true, "closeMenuButton", "");
                        } else {
                            if (mInfo.isValid()) {
                                String role = null;
                                if (mInfo.getType() == GROUP) {
                                    role = mInfo.getGroupRoom().getRole().toString();
                                } else if (mInfo.getType() == CHANNEL) {
                                    role = mInfo.getChannelRoom().getRole().toString();
                                }

                                MyDialog.showDialogMenuItemRooms(getActivity(), mInfo.getType(), mInfo.getMute(), role, new OnComplete() {
                                    @Override public void complete(boolean result, String messageOne, String MessageTow) {
                                        onSelectRoomMenu(messageOne, mInfo);
                                    }
                                });
                            }
                        }
                        return true;
                    }
                });
            }
        }

        /**
         * get string chat icon
         *
         * @param chatType chat type
         * @return String
         */
        private String getStringChatIcon(RoomType chatType) {
            switch (chatType) {
                case CHAT:
                    return "";
                case CHANNEL:
                    return G.context.getString(R.string.md_channel_icon);
                case GROUP:
                    return G.context.getString(R.string.md_users_social_symbol);
                default:
                    return null;
            }
        }
    }






}
