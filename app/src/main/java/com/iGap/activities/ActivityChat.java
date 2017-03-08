package com.iGap.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewStubCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.Config;
import com.iGap.G;
import com.iGap.IntentRequests;
import com.iGap.R;
import com.iGap.adapter.AdapterBottomSheet;
import com.iGap.adapter.MessagesAdapter;
import com.iGap.adapter.items.chat.AbstractMessage;
import com.iGap.adapter.items.chat.AudioItem;
import com.iGap.adapter.items.chat.ContactItem;
import com.iGap.adapter.items.chat.FileItem;
import com.iGap.adapter.items.chat.GifItem;
import com.iGap.adapter.items.chat.GifWithTextItem;
import com.iGap.adapter.items.chat.ImageItem;
import com.iGap.adapter.items.chat.ImageWithTextItem;
import com.iGap.adapter.items.chat.LocationItem;
import com.iGap.adapter.items.chat.LogItem;
import com.iGap.adapter.items.chat.ProgressWaiting;
import com.iGap.adapter.items.chat.TextItem;
import com.iGap.adapter.items.chat.TimeItem;
import com.iGap.adapter.items.chat.UnreadMessage;
import com.iGap.adapter.items.chat.VideoItem;
import com.iGap.adapter.items.chat.VideoWithTextItem;
import com.iGap.adapter.items.chat.VoiceItem;
import com.iGap.fragments.FragmentMap;
import com.iGap.fragments.FragmentShowImage;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperCalander;
import com.iGap.helper.HelperCancelDownloadUpload;
import com.iGap.helper.HelperGetAction;
import com.iGap.helper.HelperGetDataFromOtherApp;
import com.iGap.helper.HelperGetMessageState;
import com.iGap.helper.HelperMimeType;
import com.iGap.helper.HelperNotificationAndBadge;
import com.iGap.helper.HelperPermision;
import com.iGap.helper.HelperSetAction;
import com.iGap.helper.HelperUrl;
import com.iGap.helper.ImageHelper;
import com.iGap.interfaces.IMessageItem;
import com.iGap.interfaces.IResendMessage;
import com.iGap.interfaces.OnActivityChatStart;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.interfaces.OnChannelAddMessageReaction;
import com.iGap.interfaces.OnChannelGetMessagesStats;
import com.iGap.interfaces.OnChatClearMessageResponse;
import com.iGap.interfaces.OnChatDelete;
import com.iGap.interfaces.OnChatDeleteMessageResponse;
import com.iGap.interfaces.OnChatEditMessageResponse;
import com.iGap.interfaces.OnChatMessageRemove;
import com.iGap.interfaces.OnChatMessageSelectionChanged;
import com.iGap.interfaces.OnChatSendMessageResponse;
import com.iGap.interfaces.OnChatUpdateStatusResponse;
import com.iGap.interfaces.OnClearChatHistory;
import com.iGap.interfaces.OnClientJoinByUsername;
import com.iGap.interfaces.OnDeleteChatFinishActivity;
import com.iGap.interfaces.OnFileUploadForActivities;
import com.iGap.interfaces.OnGroupAvatarResponse;
import com.iGap.interfaces.OnHelperSetAction;
import com.iGap.interfaces.OnLastSeenUpdateTiming;
import com.iGap.interfaces.OnMessageReceive;
import com.iGap.interfaces.OnPathAdapterBottomSheet;
import com.iGap.interfaces.OnSetAction;
import com.iGap.interfaces.OnUpdateUserStatusInChangePage;
import com.iGap.interfaces.OnUserContactsBlock;
import com.iGap.interfaces.OnUserContactsUnBlock;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.interfaces.OnUserUpdateStatus;
import com.iGap.interfaces.OnVoiceRecord;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.AttachFile;
import com.iGap.module.ChatSendMessageUtil;
import com.iGap.module.ContactUtils;
import com.iGap.module.FileUploadStructure;
import com.iGap.module.FileUtils;
import com.iGap.module.LastSeenTimeUtil;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.MessageLoader;
import com.iGap.module.MusicPlayer;
import com.iGap.module.MyAppBarLayout;
import com.iGap.module.MyType;
import com.iGap.module.OnComplete;
import com.iGap.module.ResendMessage;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.SUID;
import com.iGap.module.StructBottomSheet;
import com.iGap.module.StructChannelExtra;
import com.iGap.module.StructMessageAttachment;
import com.iGap.module.StructMessageInfo;
import com.iGap.module.TimeUtils;
import com.iGap.module.VoiceRecord;
import com.iGap.module.enums.LocalFileType;
import com.iGap.module.enums.ProgressState;
import com.iGap.proto.ProtoChannelGetMessagesStats;
import com.iGap.proto.ProtoClientGetRoomHistory;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAttachmentFields;
import com.iGap.realm.RealmChannelExtra;
import com.iGap.realm.RealmChannelRoom;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmContactsFields;
import com.iGap.realm.RealmDraftFile;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmOfflineDelete;
import com.iGap.realm.RealmOfflineDeleteFields;
import com.iGap.realm.RealmOfflineEdited;
import com.iGap.realm.RealmOfflineSeen;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomDraft;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageContact;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.RealmRoomMessageLocation;
import com.iGap.realm.RealmUserInfo;
import com.iGap.realm.enums.ChannelChatRole;
import com.iGap.realm.enums.GroupChatRole;
import com.iGap.request.RequestChannelDeleteMessage;
import com.iGap.request.RequestChannelEditMessage;
import com.iGap.request.RequestChannelUpdateDraft;
import com.iGap.request.RequestChatDelete;
import com.iGap.request.RequestChatDeleteMessage;
import com.iGap.request.RequestChatEditMessage;
import com.iGap.request.RequestChatUpdateDraft;
import com.iGap.request.RequestClientJoinByUsername;
import com.iGap.request.RequestClientSubscribeToRoom;
import com.iGap.request.RequestClientUnsubscribeFromRoom;
import com.iGap.request.RequestGroupDeleteMessage;
import com.iGap.request.RequestGroupEditMessage;
import com.iGap.request.RequestGroupUpdateDraft;
import com.iGap.request.RequestUserContactsBlock;
import com.iGap.request.RequestUserContactsUnblock;
import com.iGap.request.RequestUserInfo;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wang.avi.AVLoadingIndicatorView;
import io.github.meness.emoji.EmojiEditText;
import io.github.meness.emoji.EmojiTextView;
import io.github.meness.emoji.emoji.Emoji;
import io.github.meness.emoji.listeners.OnEmojiBackspaceClickListener;
import io.github.meness.emoji.listeners.OnEmojiClickedListener;
import io.github.meness.emoji.listeners.OnEmojiPopupDismissListener;
import io.github.meness.emoji.listeners.OnEmojiPopupShownListener;
import io.github.meness.emoji.listeners.OnSoftKeyboardCloseListener;
import io.github.meness.emoji.listeners.OnSoftKeyboardOpenListener;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import me.leolin.shortcutbadger.ShortcutBadger;
import org.parceler.Parcels;

import static com.iGap.G.chatSendMessageUtil;
import static com.iGap.G.context;
import static com.iGap.R.id.ac_ll_parent;
import static com.iGap.R.id.replyFrom;
import static com.iGap.R.string.member;
import static com.iGap.helper.HelperGetDataFromOtherApp.messageType;
import static com.iGap.module.AttachFile.getFilePathFromUri;
import static com.iGap.module.AttachFile.request_code_VIDEO_CAPTURED;
import static com.iGap.module.MessageLoader.getLocalMessage;
import static com.iGap.module.enums.ProgressState.HIDE;
import static com.iGap.module.enums.ProgressState.SHOW;
import static com.iGap.proto.ProtoGlobal.ClientAction.CHOOSING_CONTACT;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_AUDIO;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_FILE;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_GIF;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_IMAGE;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_LOCATION;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_VIDEO;
import static com.iGap.proto.ProtoGlobal.ClientAction.SENDING_VOICE;
import static com.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static com.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static com.iGap.proto.ProtoGlobal.Room.Type.GROUP;
import static com.iGap.proto.ProtoGlobal.RoomMessageType.CONTACT;
import static com.iGap.proto.ProtoGlobal.RoomMessageType.GIF_TEXT;
import static com.iGap.proto.ProtoGlobal.RoomMessageType.IMAGE_TEXT;
import static com.iGap.proto.ProtoGlobal.RoomMessageType.LOG;
import static com.iGap.proto.ProtoGlobal.RoomMessageType.VIDEO_TEXT;
import static java.lang.Long.parseLong;


public class ActivityChat extends ActivityEnhanced
        implements IMessageItem, OnChatClearMessageResponse, OnChatSendMessageResponse, OnChatUpdateStatusResponse, OnChatMessageSelectionChanged<AbstractMessage>, OnChatMessageRemove, OnVoiceRecord, OnUserInfoResponse, OnFileUploadForActivities, OnSetAction, OnUserUpdateStatus, OnLastSeenUpdateTiming, OnGroupAvatarResponse, OnChannelAddMessageReaction, OnChannelGetMessagesStats {

    public static ActivityChat activityChat;
    public static OnComplete hashListener;
    private AttachFile attachFile;
    private LinearLayout mediaLayout;
    public static MusicPlayer musicPlayer;
    //  private boolean isNeedAddTime = true;
    private LinearLayout ll_Search;
    private EditText edtSearchMessage;
    private long messageId;
    private int scrollPosition = 0;
    private SharedPreferences sharedPreferences;
    private io.github.meness.emoji.EmojiEditText edtChat;
    private MaterialDesignTextView imvSendButton;
    private MaterialDesignTextView imvAttachFileButton;
    private LinearLayout layoutAttachBottom;
    private MaterialDesignTextView imvMicButton;
    private MaterialDesignTextView btnReplaySelected;
    private ArrayList<String> listPathString;
    private MaterialDesignTextView btnCancelSendingFile;
    private TextView txtFileNameForSend;
    private LinearLayout ll_attach_text;
    private TextView txtNumberOfSelected;
    private LinearLayout ll_AppBarSelected;
    private LinearLayout toolbar;
    private TextView txtName;
    private TextView txtLastSeen;
    private ViewGroup viewGroupLastSeen;

    private ImageView imvUserPicture;
    private RecyclerView recyclerView;
    private MaterialDesignTextView imvSmileButton;
    private LocationManager locationManager;
    private OnComplete complete;
    private View viewAttachFile;
    private View viewMicRecorder;
    private VoiceRecord voiceRecord;
    private boolean sendByEnter = false;
    private LinearLayout ll_navigate_Message;
    private TextView btnUpMessage;
    private TextView btnDownMessage;
    private MaterialDesignTextView txtClearMessageSearch;
    private TextView txtMessageCounter;
    private int messageCounter = 0;
    private int selectedPosition = 0;
    private LinearLayout ll_navigateHash;
    private TextView btnUpHash;
    private TextView btnDownHash;
    private TextView txtHashCounter;
    private Button btnHashLayoutClose;
    private SearchHash searchHash;
    private MessagesAdapter<AbstractMessage> mAdapter;
    private ProtoGlobal.Room.Type chatType;
    private static ProtoGlobal.Room.Type chatTypeStatic;
    private long lastSeen;
    public long mRoomId = 0;
    public static long mRoomIdStatic = 0;
    private TextView btnUp;
    private TextView btnDown;
    private TextView txtChannelMute;
    //popular (chat , group , channel)
    public String title;
    public String phoneNumber;
    public static String titleStatic;
    private String initialize;
    private String color;
    private io.github.meness.emoji.EmojiPopup emojiPopup;

    private boolean isChatReadOnly = false;
    //chat
    private long chatPeerId;
    private boolean isMuteNotification;
    private String userStatus;
    private long userTime;
    public static OnComplete onMusicListener;

    //group
    private GroupChatRole groupRole;
    private String groupParticipantsCountLabel;
    //channel
    private ChannelChatRole channelRole;
    private String channelParticipantsCountLabel;
    private PopupWindow popupWindow;
    // save latest intent data and requestCode from result activity for set draft if not send
    // file yet
    private Uri latestUri;
    private int latestRequestCode;
    private String latestFilePath;
    private Calendar lastDateCalendar = Calendar.getInstance();
    private LinearLayout mReplayLayout;

    private MaterialDesignTextView iconMute;

    public static OnComplete onComplete;
    private MyAppBarLayout appBarLayout;
    private boolean hasDraft = false;
    private long replyToMessageId = 0;
    private long userId;
    public static Activity activityChatForFinish;
    private LinearLayout lyt_user;
    private ProgressBar prgWaiting;
    private AVLoadingIndicatorView avi;
    private Boolean isGoingFromUserLink = false;
    private Boolean isNotJoin = false;
    private String userName = "";
    private boolean blockUser = false;
    private ViewGroup vgSpamUser;
    private TextView txtSpamUser;
    private TextView txtSpamClose;
    private Realm mRealm;

    private RecyclerView.OnScrollListener scrollListener;

    private boolean hasForward = false;
    private ImageView imvCancelForward;

    private int countLoadItemToChat = 0;
    private FrameLayout llScrollNavigate;
    private TextView txtNewUnreadMessage;
    private int countNewMessage = 0;
    private int lastPosition = 0;
    private boolean firsInitScrollPosition = false;

    private RecyclerView rcvBottomSheet;
    private ArrayList<StructBottomSheet> itemGalleryList = new ArrayList<>();
    private FastItemAdapter fastItemAdapter;
    private BottomSheetDialog bottomSheetDialog;
    private static List<StructBottomSheet> contacts;
    private boolean isCheckBottomSheet = false;
    public static OnPathAdapterBottomSheet onPathAdapterBottomSheet;
    private ImageView send;
    private TextView txtCountItem;
    private View viewBottomSheet;
    private RealmRoom realmRoom;
    private boolean initHash = false;
    private boolean initAttach = false;
    private boolean initEmoji = false;

    @Override
    protected void onStart() {
        super.onStart();

        RealmRoomMessage.fetchMessages(mRoomId, new OnActivityChatStart() {
            @Override
            public void resendMessage(RealmRoomMessage message) {
                G.chatSendMessageUtil.build(chatType, message.getRoomId(), message);
            }

            @Override
            public void resendMessageNeedsUpload(RealmRoomMessage message) {
                new UploadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message.getAttachment().getLocalFilePath(), message.getMessageId(), message.getMessageType(), message.getRoomId(), message.getMessage());
            }

            @Override
            public void sendSeenStatus(RealmRoomMessage message) {
                G.chatUpdateStatusUtil.sendUpdateStatus(chatType, mRoomId, message.getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
            }
        });

        try {
            ShortcutBadger.applyCount(context, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //initMain();
                initLayoutHashNavigationCallback();
                showSpamBar();

                musicPlayer = new MusicPlayer(mediaLayout);
                if (MusicPlayer.mp != null) {
                    MusicPlayer.initLayoutTripMusic(mediaLayout);
                }

                /**
                 * update view for played music
                 */
                mAdapter.updateChengedItem(MusicPlayer.playedList);
                MusicPlayer.playedList.clear();

                if (isGoingFromUserLink) {
                    new RequestClientSubscribeToRoom().clientSubscribeToRoom(mRoomId);
                }

                final Realm updateUnreadCountRealm = Realm.getDefaultInstance();
                updateUnreadCountRealm.executeTransactionAsync(new Realm.Transaction() {//ASYNC
                    @Override
                    public void execute(Realm realm) {
                        final RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                        if (room != null) {
                            room.setUnreadCount(0);
                            realm.copyToRealmOrUpdate(room);

                            /**
                             * set member count
                             * set this code in onResume for update this value when user
                             * come back from profile activities
                             */

                            String members = null;
                            if (room.getType() == GROUP && room.getGroupRoom() != null) {
                                members = room.getGroupRoom().getParticipantsCountLabel();
                            } else if (room.getType() == CHANNEL && room.getChannelRoom() != null) {
                                members = room.getChannelRoom().getParticipantsCountLabel();
                            }

                            final String finalMembers = members;
                            if (finalMembers != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        txtLastSeen.setText(finalMembers + " " + getResources().getString(member));
                                        avi.setVisibility(View.GONE);

                                        if (HelperCalander.isLanguagePersian) txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                                    }
                                });
                            }
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        updateUnreadCountRealm.close();
                    }
                });
            }
        }, 25);

        chatTypeStatic = chatType;
        mRoomIdStatic = mRoomId;
        titleStatic = title;

        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.uploaderUtil.setActivityCallbacks(this);
        G.uploaderUtil.setActivityCallbacks(this);
        G.onUserInfoResponse = this;
        G.onChannelAddMessageReaction = this;
        G.onChannelGetMessagesStats = this;
        activityChatForFinish = this;
        activityChat = this;
        G.onSetAction = this;
        G.onUserUpdateStatus = this;
        G.onLastSeenUpdateTiming = this;
        G.helperNotificationAndBadge.cancelNotification();

        initCallbacks();
        HelperNotificationAndBadge.isChatRoomNow = true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        startPageFastInitialize();

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initMain();
            }
        }, 20);
    }

    /**
     * set just important item to view in onCreate and load another objects in onResume
     * actions : set app color, load avatar, set background, set title, set status chat or member for group or channel
     */
    private void startPageFastInitialize() {
        mRealm = Realm.getDefaultInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mRoomId = extras.getLong("RoomId");
            realmRoom = mRealm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
            pageSettings();

            /**
             * need this info for load avatar
             */
            if (realmRoom != null) {
                chatType = realmRoom.getType();
                if (chatType == CHAT) {
                    chatPeerId = realmRoom.getChatRoom().getPeerId();
                    RealmRegisteredInfo realmRegisteredInfo = mRealm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, chatPeerId).findFirst();
                    title = realmRegisteredInfo.getDisplayName();
                    lastSeen = realmRegisteredInfo.getLastSeen();
                    userStatus = realmRegisteredInfo.getStatus();
                } else {
                    mRoomId = realmRoom.getId();
                    title = realmRoom.getTitle();
                    if (chatType == GROUP) {
                        groupParticipantsCountLabel = realmRoom.getGroupRoom().getParticipantsCountLabel();
                    } else {
                        groupParticipantsCountLabel = realmRoom.getChannelRoom().getParticipantsCountLabel();
                    }
                }

                avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
                txtName = (TextView) findViewById(R.id.chl_txt_name);
                txtLastSeen = (TextView) findViewById(R.id.chl_txt_last_seen);
                viewGroupLastSeen = (ViewGroup) findViewById(R.id.chl_txt_viewGroup_seen);
                imvUserPicture = (ImageView) findViewById(R.id.chl_imv_user_picture);

                if (title != null) {
                    txtName.setText(title);
                }

                /**
                 * change english number to persian number
                 */
                if (HelperCalander.isLanguagePersian) {
                    txtName.setText(HelperCalander.convertToUnicodeFarsiNumber(txtName.getText().toString()));
                }

                if (chatType == CHAT) {
                    setUserStatus(userStatus, lastSeen);
                } else if ((chatType == GROUP) || (chatType == CHANNEL)) {
                    if (groupParticipantsCountLabel != null) {
                        txtLastSeen.setText(groupParticipantsCountLabel + " " + getResources().getString(R.string.member));
                        avi.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        }
                    }
                }

                /**
                 * change english number to persian number
                 */
                if (HelperCalander.isLanguagePersian) {
                    txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                }

                setAvatar();
            }
        }
    }

    private void initMain() {
        HelperGetMessageState.clearMessageViews();
        MusicPlayer.playedList.clear();

        /**
         * define views
         */
        mediaLayout = (LinearLayout) findViewById(R.id.ac_ll_music_layout);
        lyt_user = (LinearLayout) findViewById(R.id.lyt_user);
        viewAttachFile = findViewById(R.id.layout_attach_file);
        viewMicRecorder = findViewById(R.id.layout_mic_recorde);
        prgWaiting = (ProgressBar) findViewById(R.id.chl_prgWaiting);

        voiceRecord = new VoiceRecord(this, viewMicRecorder, viewAttachFile, this);
        attachFile = new AttachFile(this);
        prgWaiting.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.toolbar_background), android.graphics.PorterDuff.Mode.MULTIPLY);
        prgWaiting.setVisibility(View.VISIBLE);
        lastDateCalendar.clear();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mRoomId = extras.getLong("RoomId");
            isGoingFromUserLink = extras.getBoolean("GoingFromUserLink");
            isNotJoin = extras.getBoolean("ISNotJoin");
            userName = extras.getString("UserName");

            if (isNotJoin) {
                final LinearLayout layoutJoin = (LinearLayout) findViewById(R.id.ac_ll_join);
                layoutJoin.setBackgroundColor(Color.parseColor(G.appBarColor));
                layoutJoin.setVisibility(View.VISIBLE);
                layoutJoin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HelperUrl.showIndeterminateProgressDialog();
                        G.onClientJoinByUsername = new OnClientJoinByUsername() {
                            @Override
                            public void onClientJoinByUsernameResponse() {

                                isNotJoin = false;
                                HelperUrl.closeDialogWaiting();

                                ActivityChat.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        layoutJoin.setVisibility(View.GONE);

                                        findViewById(R.id.ac_ll_parent).invalidate();

                                        if (chatType == GROUP) {
                                            viewAttachFile.setVisibility(View.VISIBLE);
                                            isChatReadOnly = false;
                                        }
                                    }
                                });

                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();

                                        if (realmRoom != null) {
                                            realmRoom.setDeleted(false);
                                            if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                                                realmRoom.setReadOnly(false);
                                            }

                                            realmRoom.setUpdatedTime(TimeUtils.currentLocalTime());

                                        }
                                    }
                                });
                                realm.close();
                            }

                            @Override
                            public void onError(int majorCode, int minorCode) {
                                HelperUrl.dialogWaiting.dismiss();
                            }
                        };

                        new RequestClientJoinByUsername().clientJoinByUsername(userName);
                    }
                });
            }
            messageId = extras.getLong("MessageId");

            /**
             * get userId . use in chat set action.
             */
            RealmUserInfo realmUserInfo = mRealm.where(RealmUserInfo.class).findFirst();
            if (realmUserInfo == null) {
                finish();
                return;
            }
            userId = realmUserInfo.getUserId();

            if (realmRoom != null) { // room exist

                title = realmRoom.getTitle();
                initialize = realmRoom.getInitials();
                color = realmRoom.getColor();
                isChatReadOnly = realmRoom.getReadOnly();
                countLoadItemToChat = realmRoom.getUnreadCount();

                if (isChatReadOnly) {
                    viewAttachFile.setVisibility(View.GONE);
                    ((RecyclerView) findViewById(R.id.chl_recycler_view_chat)).setPadding(0, 0, 0, 0);
                }

                if (chatType == CHAT) {

                    //RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                    //chatPeerId = realmChatRoom.getPeerId();

                    RealmRegisteredInfo realmRegisteredInfo = mRealm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, chatPeerId).findFirst();
                    if (realmRegisteredInfo != null) {
                        //title = realmRegisteredInfo.getDisplayName();
                        initialize = realmRegisteredInfo.getInitials();
                        color = realmRegisteredInfo.getColor();
                        //lastSeen = realmRegisteredInfo.getLastSeen();
                        //userStatus = realmRegisteredInfo.getStatus();
                        phoneNumber = realmRegisteredInfo.getPhoneNumber();
                    } else {
                        title = realmRoom.getTitle();
                        initialize = realmRoom.getInitials();
                        color = realmRoom.getColor();
                        userStatus = getResources().getString(R.string.last_seen_recently);
                    }
                } else if (chatType == GROUP) {
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    groupRole = realmGroupRoom.getRole();
                    groupParticipantsCountLabel = realmGroupRoom.getParticipantsCountLabel();
                } else if (chatType == CHANNEL) {
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    channelRole = realmChannelRoom.getRole();
                    channelParticipantsCountLabel = realmChannelRoom.getParticipantsCountLabel();
                }
            } else {
                chatPeerId = extras.getLong("peerId");
                chatType = CHAT;
                RealmRegisteredInfo realmRegisteredInfo = mRealm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, chatPeerId).findFirst();
                if (realmRegisteredInfo != null) {
                    title = realmRegisteredInfo.getDisplayName();
                    initialize = realmRegisteredInfo.getInitials();
                    color = realmRegisteredInfo.getColor();
                    lastSeen = realmRegisteredInfo.getLastSeen();
                    userStatus = realmRegisteredInfo.getStatus();
                }
            }
        }

        initComponent();
        initAppbarSelected();
        getDraft();
        getUserInfo();
        checkAction();
        insertShearedData();
    }

    /**
     * do forward actions if any message forward to this room
     */
    private void manageForwardedMessage() {
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getParcelableArrayList(ActivitySelectChat.ARG_FORWARD_MESSAGE) != null) {

            final LinearLayout ll_Forward = (LinearLayout) findViewById(R.id.ac_ll_forward);

            if (hasForward) {
                imvCancelForward.performClick();
                ArrayList<Parcelable> messageInfos = getIntent().getParcelableArrayListExtra(ActivitySelectChat.ARG_FORWARD_MESSAGE);
                for (Parcelable messageInfo : messageInfos) {
                    sendForwardedMessage((StructMessageInfo) Parcels.unwrap(messageInfo));
                }
            } else {
                imvCancelForward = (ImageView) findViewById(R.id.cslhf_imv_cansel);
                imvCancelForward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ll_Forward.setVisibility(View.GONE);
                        hasForward = false;

                        if (edtChat.getText().length() == 0) {

                            layoutAttachBottom.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    layoutAttachBottom.setVisibility(View.VISIBLE);
                                }
                            }).start();
                            imvSendButton.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            imvSendButton.clearAnimation();
                                            imvSendButton.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                });

                layoutAttachBottom.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layoutAttachBottom.setVisibility(View.GONE);
                    }
                }).start();

                imvSendButton.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imvSendButton.clearAnimation();
                                imvSendButton.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }).start();

                int _count = getIntent().getExtras().getInt(ActivitySelectChat.ARG_FORWARD_MESSAGE_COUNT);
                String str = _count > 1 ? getString(R.string.messages_selected) : getString(R.string.message_selected);

                EmojiTextView emMessage = (EmojiTextView) findViewById(R.id.cslhf_txt_message);

                if (HelperCalander.isLanguagePersian) {

                    emMessage.setText(HelperCalander.convertToUnicodeFarsiNumber(_count + " " + str));
                } else {

                    emMessage.setText(_count + " " + str);
                }

                hasForward = true;
                ll_Forward.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * manage need showSpamBar for user or no
     */
    private void showSpamBar() {
        /**
         * use handler for run async
         */
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                Realm realm = Realm.getDefaultInstance();
                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, chatPeerId).findFirst();
                RealmContacts realmContacts = realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, chatPeerId).findFirst();
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmRegisteredInfo != null && realmUserInfo != null && realmRegisteredInfo.getId() != realmUserInfo.getUserId()) {
                    if (phoneNumber == null && realmRegisteredInfo.getId() != realm.where(RealmUserInfo.class).findFirst().getUserId()) {
                        if (realmContacts == null && chatType == CHAT && chatPeerId != 134) {
                            initSpamBarLayout(realmRegisteredInfo);
                            vgSpamUser.setVisibility(View.VISIBLE);
                        }
                    }

                    if (realmRegisteredInfo.getId() != realm.where(RealmUserInfo.class).findFirst().getUserId()) {
                        if (!realmRegisteredInfo.getDoNotshowSpamBar()) {

                            if (realmRegisteredInfo.isBlockUser()) {
                                initSpamBarLayout(realmRegisteredInfo);
                                blockUser = true;
                                txtSpamUser.setText(getResources().getString(R.string.un_block_user));
                                vgSpamUser.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    if (realmContacts != null && realmRegisteredInfo.getId() != realm.where(RealmUserInfo.class).findFirst().getUserId()) {
                        if (realmContacts.isBlockUser()) {
                            if (!realmRegisteredInfo.getDoNotshowSpamBar()) {
                                initSpamBarLayout(realmRegisteredInfo);
                                blockUser = true;
                                txtSpamUser.setText(getResources().getString(R.string.un_block_user));
                                vgSpamUser.setVisibility(View.VISIBLE);
                            } else {
                                initSpamBarLayout(realmRegisteredInfo);
                                blockUser = true;
                                txtSpamUser.setText(getResources().getString(R.string.un_block_user));
                                vgSpamUser.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                realm.close();
            }
        });
    }

    /**
     * init spamBar layout
     */
    private void initSpamBarLayout(final RealmRegisteredInfo registeredInfo) {
        vgSpamUser = (ViewGroup) findViewById(R.id.layout_add_contact);
        txtSpamUser = (TextView) findViewById(R.id.chat_txt_addContact);
        txtSpamClose = (TextView) findViewById(R.id.chat_txt_close);
        txtSpamClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vgSpamUser.setVisibility(View.GONE);
                if (registeredInfo != null) {
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            registeredInfo.setDoNotshowSpamBar(true);
                        }
                    });
                }
            }
        });

        txtSpamUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (blockUser) {
                    G.onUserContactsUnBlock = new OnUserContactsUnBlock() {
                        @Override
                        public void onUserContactsUnBlock(final long userId) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    blockUser = false;
                                    if (userId == chatPeerId) {
                                        txtSpamUser.setText(getResources().getString(R.string.block_user));
                                    }
                                }
                            });
                        }
                    };
                    new RequestUserContactsUnblock().userContactsUnblock(chatPeerId);
                } else {

                    G.onUserContactsBlock = new OnUserContactsBlock() {
                        @Override
                        public void onUserContactsBlock(final long userId) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    blockUser = true;
                                    if (userId == chatPeerId) {
                                        txtSpamUser.setText(getResources().getString(R.string.un_block_user));
                                    }
                                }
                            });
                        }
                    };
                    new RequestUserContactsBlock().userContactsBlock(chatPeerId);
                }
            }
        });
    }


    /**
     * get settings state and change view
     */
    private void pageSettings() {
        /**
         * get sendByEnter action from setting value
         */
        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        int checkedSendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
        sendByEnter = checkedSendByEnter == 1;

        /**
         * set background
         */
        String backGroundPath = sharedPreferences.getString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
        if (backGroundPath.length() > 0) {

            File f = new File(backGroundPath);
            if (f.exists()) {
                Drawable d = Drawable.createFromPath(f.getAbsolutePath());
                getWindow().setBackgroundDrawable(d);
            }
        } else {
            getWindow().setBackgroundDrawableResource(R.drawable.newbg);
        }

        /**
         * set app color to appBar
         */
        appBarLayout = (MyAppBarLayout) findViewById(R.id.ac_appBarLayout);
        appBarLayout.setBackgroundColor(Color.parseColor(G.appBarColor));
        findViewById(R.id.ac_green_line).setBackgroundColor(Color.parseColor(G.appBarColor));
    }

    public void sendPosition(final Double latitude, final Double longitude, final String imagePath) {

        Realm realm = Realm.getDefaultInstance();
        final long id = SUID.id().get();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmRoomMessageLocation messageLocation = realm.createObject(RealmRoomMessageLocation.class, SUID.id().get());
                messageLocation.setLocationLat(latitude);
                messageLocation.setLocationLong(longitude);
                messageLocation.setImagePath(imagePath);
                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, id);
                roomMessage.setLocation(messageLocation);
                roomMessage.setCreateTime(TimeUtils.currentLocalTime());
                roomMessage.setMessageType(ProtoGlobal.RoomMessageType.LOCATION);
                roomMessage.setRoomId(mRoomId);
                roomMessage.setUserId(userId);
                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();

                if (realmRoom != null) {
                    realmRoom.setLastMessage(roomMessage);
                }
            }
        });
        realm.close();

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Realm realm1 = Realm.getDefaultInstance();
                RealmRoomMessage roomMessage = realm1.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, id).findFirst();
                switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(roomMessage))), false);
                chatSendMessageUtil.build(chatType, mRoomId, roomMessage);
                scrollToEnd();
                realm1.close();
            }
        }, 300);
    }

    private void checkAction() {
        Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null && realmRoom.getActionState() != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (realmRoom.getActionState() != null && (chatType == GROUP || chatType == CHANNEL) || ((RealmRoom.isCloudRoom(mRoomId) || (!RealmRoom.isCloudRoom(mRoomId) && realmRoom.getActionStateUserId() != userId)))) {
                        txtLastSeen.setText(realmRoom.getActionState());
                        avi.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
                            //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        }
                    } else if (chatType == CHAT) {
                        if (RealmRoom.isCloudRoom(mRoomId)) {
                            txtLastSeen.setText(getResources().getString(R.string.chat_with_yourself));
                        } else {
                            if (userStatus != null) {
                                if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                                    txtLastSeen.setText(LastSeenTimeUtil.computeTime(chatPeerId, userTime, true, false));
                                } else {
                                    txtLastSeen.setText(userStatus);
                                }
                            }
                        }
                        avi.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                            //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        }

                    } else if (chatType == GROUP) {
                        avi.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        }

                        txtLastSeen.setText(groupParticipantsCountLabel + " " + getString(member));

                    }
                    // change english number to persian number
                    if (HelperCalander.isLanguagePersian) txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                }
            });
        }
        realm.close();
    }

    private void getUserInfo() {
        /**
         * client should send request for get user info because need to update user online timing
         */
        if (chatType == CHAT) {
            new RequestUserInfo().userInfo(chatPeerId);
        }
    }

    private void sendForwardedMessage(final StructMessageInfo messageInfo) {
        final Realm realm = Realm.getDefaultInstance();
        final long messageId = SUID.id().get();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(messageInfo.messageID)).findFirst();

                if (roomMessage != null) {
                    long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
                    RealmRoomMessage forwardedMessage = realm.createObject(RealmRoomMessage.class, messageId);
                    if (roomMessage.getForwardMessage() != null) {
                        forwardedMessage.setForwardMessage(roomMessage.getForwardMessage());
                        forwardedMessage.setHasMessageLink(roomMessage.getForwardMessage().getHasMessageLink());
                    } else {
                        forwardedMessage.setForwardMessage(roomMessage);
                        forwardedMessage.setHasMessageLink(roomMessage.getHasMessageLink());
                    }

                    forwardedMessage.setCreateTime(TimeUtils.currentLocalTime());
                    forwardedMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
                    forwardedMessage.setRoomId(mRoomId);
                    forwardedMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                    forwardedMessage.setUserId(userId);
                    forwardedMessage.setShowMessage(true);

                    realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst().setLastMessage(forwardedMessage);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                RealmRoomMessage forwardedMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                if (forwardedMessage.isValid() && !forwardedMessage.isDeleted()) {
                    switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(forwardedMessage))), false);
                    scrollToEnd();

                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(messageInfo.messageID)).findFirst();
                    chatSendMessageUtil.buildForward(chatType, forwardedMessage.getRoomId(), forwardedMessage, roomMessage.getRoomId(), roomMessage.getMessageId());
                }
                realm.close();
            }
        });
    }

    /**
     * initialize some callbacks that used in this page
     */
    public void initCallbacks() {
        chatSendMessageUtil.setOnChatSendMessageResponse(this);
        G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);

        G.onChatEditMessageResponse = new OnChatEditMessageResponse() {
            @Override
            public void onChatEditMessage(long roomId, final long messageId, long messageVersion, final String message, ProtoResponse.Response response) {
                if (mRoomId == roomId) {
                    // I'm in the room
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // update message text in adapter
                            mAdapter.updateMessageText(messageId, message);
                        }
                    });
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        G.onChatDeleteMessageResponse = new OnChatDeleteMessageResponse() {
            @Override
            public void onChatDeleteMessage(long deleteVersion, final long messageId, long roomId, ProtoResponse.Response response) {
                if (response.getId().isEmpty()) { // another account deleted this message

                    //Realm realm = Realm.getDefaultInstance();
                    //RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                    //if (roomMessage != null) {
                    //    roomMessage.setDeleted(true);
                    //}
                    //realm.close();

                    // if deleted message is for current room clear from adapter
                    if (roomId == mRoomId) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // remove deleted message from adapter
                                mAdapter.removeMessage(messageId);

                                // remove tag from edtChat if the message has deleted
                                if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                                    if (Long.toString(messageId).equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
                                        edtChat.setTag(null);
                                    }
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        /**
         * call from ActivityGroupProfile for update group member number or clear history
         */
        onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                if (result) {

                    txtLastSeen.setText(messageOne + " " + getResources().getString(member));

                    avi.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                    }

                    // change english number to persian number
                    if (HelperCalander.isLanguagePersian) txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                } else {
                    clearHistory(Long.parseLong(messageOne));
                }



            }
        };

        onMusicListener = new OnComplete() {
            @Override
            public void complete(boolean result, String messageID, String beforMessageID) {

                if (beforMessageID != null) {
                    for (int i = mAdapter.getAdapterItemCount() - 1; i >= 0; i--) {
                        if (mAdapter.getItem(i).mMessage.messageID.equals(beforMessageID)) {
                            mAdapter.notifyAdapterItemChanged(i);
                            break;
                        }
                    }
                }

                if (messageID != null) {
                    for (int i = mAdapter.getAdapterItemCount() - 1; i >= 0; i--) {
                        if (mAdapter.getItem(i).mMessage.messageID.equals(messageID)) {
                            mAdapter.notifyAdapterItemChanged(i);
                            break;
                        }
                    }
                }
            }
        };

        /**
         * after get position from gps
         */
        complete = new OnComplete() {
            @Override
            public void complete(boolean result, final String messageOne, String MessageTow) {
                HelperSetAction.sendCancel(messageId);

                String[] split = messageOne.split(",");
                Double latitude = Double.parseDouble(split[0]);
                Double longitude = Double.parseDouble(split[1]);

                FragmentMap fragment = FragmentMap.getInctance(latitude, longitude, FragmentMap.Mode.sendPosition);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(ac_ll_parent, fragment, FragmentMap.flagFragmentMap).commit();
            }
        };

        G.onHelperSetAction = new OnHelperSetAction() {
            @Override
            public void onAction(ProtoGlobal.ClientAction ClientAction) {
                //TODO [Saeed Mozaffari] [2017-02-16 11:28 AM] - if chatType was null get roomType with roomId
                HelperSetAction.setActionFiles(mRoomId, messageId, ClientAction, chatType);
            }
        };

        G.onClearChatHistory = new OnClearChatHistory() {
            @Override
            public void onClearChatHistory() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.clear();
                    }
                });
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        G.onDeleteChatFinishActivity = new OnDeleteChatFinishActivity() {
            @Override
            public void onFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        };

        G.onUpdateUserStatusInChangePage = new OnUpdateUserStatusInChangePage() {
            @Override
            public void updateStatus(long peerId, String status, long lastSeen) {
                if (chatType == CHAT) {
                    setUserStatus(status, lastSeen);

                    if (chatType == CHAT) {
                        new RequestUserInfo().userInfo(peerId);
                    }
                }
            }
        };
    }

    private StructMessageInfo makeLayoutTime(long time) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        String timeString = TimeUtils.getChatSettingsTimeAgo(this, calendar.getTime());

        RealmRoomMessage timeMessage = new RealmRoomMessage();
        timeMessage.setMessageId(SUID.id().get());
        // -1 means time message
        timeMessage.setUserId(-1);
        timeMessage.setUpdateTime(time);
        timeMessage.setMessage(timeString);
        timeMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);

        return StructMessageInfo.convert(timeMessage);
    }

    private void switchAddItem(ArrayList<StructMessageInfo> messageInfos, boolean addTop) {
        if (prgWaiting != null) prgWaiting.setVisibility(View.GONE);

        long identifier = SUID.id().get();
        for (StructMessageInfo messageInfo : messageInfos) {

            ProtoGlobal.RoomMessageType messageType = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getMessageType() : messageInfo.messageType;
            if (!messageInfo.isTimeOrLogMessage() || (messageType == LOG)) {
                int index = 0;
                if (addTop && messageInfo.showTime) {

                    for (int i = 0; i < mAdapter.getAdapterItemCount(); i++) {
                        if (mAdapter.getAdapterItem(i) instanceof TimeItem) {
                            if (!RealmRoomMessage.isTimeDayDiferent(messageInfo.time, mAdapter.getAdapterItem(i).mMessage.time)) {
                                mAdapter.remove(i);
                            }
                            break;
                        }
                    }
                    mAdapter.add(0, new TimeItem(this).setMessage(makeLayoutTime(messageInfo.time)).withIdentifier(identifier++));
                    index = 1;
                }

                if (!addTop && messageInfo.showTime) {
                    mAdapter.add(new TimeItem(this).setMessage(makeLayoutTime(messageInfo.time)).withIdentifier(identifier++));
                }

                switch (messageType) {
                    case TEXT:
                        if (!addTop) {
                            mAdapter.add(new TextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new TextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case IMAGE:
                        if (!addTop) {
                            mAdapter.add(new ImageItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new ImageItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case IMAGE_TEXT:
                        if (!addTop) {
                            mAdapter.add(new ImageWithTextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new ImageWithTextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case VIDEO:
                        if (!addTop) {
                            mAdapter.add(new VideoItem(chatType, this, ActivityChat.this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new VideoItem(chatType, this, ActivityChat.this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case VIDEO_TEXT:
                        if (!addTop) {
                            mAdapter.add(new VideoWithTextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new VideoWithTextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case LOCATION:
                        if (!addTop) {
                            mAdapter.add(new LocationItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new LocationItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case FILE:
                    case FILE_TEXT:
                        if (!addTop) {
                            mAdapter.add(new FileItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new FileItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case VOICE:
                        if (!addTop) {
                            mAdapter.add(new VoiceItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new VoiceItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case AUDIO:
                    case AUDIO_TEXT:
                        if (!addTop) {
                            mAdapter.add(new AudioItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new AudioItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case CONTACT:
                        if (!addTop) {
                            mAdapter.add(new ContactItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new ContactItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case GIF:
                        if (!addTop) {
                            mAdapter.add(new GifItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new GifItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case GIF_TEXT:
                        if (!addTop) {
                            mAdapter.add(new GifWithTextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new GifWithTextItem(chatType, this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                    case LOG:
                        if (!addTop) {
                            mAdapter.add(new LogItem(this).setMessage(messageInfo).withIdentifier(identifier));
                        } else {
                            mAdapter.add(index, new LogItem(this).setMessage(messageInfo).withIdentifier(identifier));
                        }
                        break;
                }
            } else {

                //if (!addTop) {
                //    mAdapter.add(new TimeItem(this).setMessage(messageInfo).withIdentifier(identifier));
                //} else {
                //    mAdapter.add(0, new TimeItem(this).setMessage(messageInfo).withIdentifier(identifier));
                //}
            }

            // super necessary
            identifier++;
        }
    }

    private void selectMessage(int position) {
        try {
            if (mAdapter.getItem(position).mMessage.view != null) {
                ((FrameLayout) mAdapter.getItem(position).mMessage.view).setForeground(new ColorDrawable(getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
            }
        } catch (NullPointerException e) {

        }
    }

    private void deSelectMessage(int position) {

        if (mAdapter.getItem(position) != null && mAdapter.getItem(position).mMessage.view != null) {
            ((FrameLayout) mAdapter.getItem(position).mMessage.view).setForeground(null);
        }
    }

    private void initComponent() {
        toolbar = (LinearLayout) findViewById(R.id.toolbar);
        iconMute = (MaterialDesignTextView) findViewById(R.id.imgMutedRoom);
        RippleView rippleBackButton = (RippleView) findViewById(R.id.chl_ripple_back_Button);

        final Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null) {

            iconMute.setVisibility(realmRoom.getMute() ? View.VISIBLE : View.GONE);
            isMuteNotification = realmRoom.getMute();
        }
        realm.close();

        ll_attach_text = (LinearLayout) findViewById(R.id.ac_ll_attach_text);
        txtFileNameForSend = (TextView) findViewById(R.id.ac_txt_file_neme_for_sending);
        btnCancelSendingFile = (MaterialDesignTextView) findViewById(R.id.ac_btn_cancel_sending_file);
        btnCancelSendingFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_attach_text.setVisibility(View.GONE);

                if (edtChat.getText().length() == 0) {

                    layoutAttachBottom.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            layoutAttachBottom.setVisibility(View.VISIBLE);
                        }
                    }).start();
                    imvSendButton.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imvSendButton.clearAnimation();
                                    imvSendButton.setVisibility(View.GONE);
                                }
                            });

                        }
                    }).start();
                }
            }
        });

        final int screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.2);

        RippleView rippleMenuButton = (RippleView) findViewById(R.id.chl_ripple_menu_button);
        rippleMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rippleView) {

                LinearLayout layoutDialog = new LinearLayout(ActivityChat.this);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutDialog.setOrientation(LinearLayout.VERTICAL);
                layoutDialog.setBackgroundColor(getResources().getColor(android.R.color.white));
                final TextView text1 = new TextView(ActivityChat.this);
                final TextView text2 = new TextView(ActivityChat.this);
                final TextView text3 = new TextView(ActivityChat.this);
                final TextView text4 = new TextView(ActivityChat.this);
                final TextView text5 = new TextView(ActivityChat.this);
                final TextView text6 = new TextView(ActivityChat.this);

                text1.setTextColor(getResources().getColor(android.R.color.black));
                text2.setTextColor(getResources().getColor(android.R.color.black));
                text3.setTextColor(getResources().getColor(android.R.color.black));
                text4.setTextColor(getResources().getColor(android.R.color.black));
                text5.setTextColor(getResources().getColor(android.R.color.black));
                text6.setTextColor(getResources().getColor(android.R.color.black));

                text1.setText(getResources().getString(R.string.Search));
                text2.setText(getResources().getString(R.string.clear_history));
                text3.setText(getResources().getString(R.string.delete_chat));
                text4.setText(getResources().getString(R.string.mute_notification));
                text5.setText(getResources().getString(R.string.chat_to_group));
                text6.setText(getResources().getString(R.string.clean_up));

                final int dim20 = (int) getResources().getDimension(R.dimen.dp20);
                int dim16 = (int) getResources().getDimension(R.dimen.dp16);
                final int dim12 = (int) getResources().getDimension(R.dimen.dp12);
                final int dim8 = (int) getResources().getDimension(R.dimen.dp8);
                int sp16 = (int) getResources().getDimension(R.dimen.sp12);
                int sp14_Popup = 14;

                /**
                 * change dpi tp px
                 */
                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int widthDpi = Math.round(width / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

                if (widthDpi >= 720) {
                    sp14_Popup = 30;
                } else if (widthDpi >= 600) {
                    sp14_Popup = 22;
                } else {
                    sp14_Popup = 15;
                }

                text1.setTextSize(sp14_Popup);
                text2.setTextSize(sp14_Popup);
                text3.setTextSize(sp14_Popup);
                text4.setTextSize(sp14_Popup);
                text5.setTextSize(sp14_Popup);
                text6.setTextSize(sp14_Popup);

                text1.setPadding(dim20, dim12, dim12, dim20);
                text2.setPadding(dim20, 0, dim12, dim20);
                text3.setPadding(dim20, 0, dim12, dim20);
                text4.setPadding(dim20, 0, dim12, dim20);
                text5.setPadding(dim20, 0, dim12, (dim16));
                text6.setPadding(dim20, 0, dim12, (dim16));

                layoutDialog.addView(text1, params);
                layoutDialog.addView(text2, params);
                layoutDialog.addView(text3, params);
                layoutDialog.addView(text4, params);
                layoutDialog.addView(text5, params);
                layoutDialog.addView(text6, params);

                if (chatType == CHAT) {
                    text3.setVisibility(View.VISIBLE);
                    text5.setVisibility(View.VISIBLE);
                } else {
                    text3.setVisibility(View.GONE);
                    text5.setVisibility(View.GONE);

                    if (chatType == CHANNEL) {
                        text2.setVisibility(View.GONE);
                    }
                }

                final Realm realm = Realm.getDefaultInstance();
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                if (realmRoom != null) {

                    if (realmRoom.getMute()) {
                        text4.setText(getResources().getString(R.string.unmute_notification));
                    } else {
                        text4.setText(getResources().getString(R.string.mute_notification));
                    }
                } else {
                    text1.setPadding(dim20, dim12, dim12, dim12);
                    text2.setVisibility(View.GONE);
                    text3.setVisibility(View.GONE);
                    text4.setVisibility(View.GONE);
                    text5.setVisibility(View.GONE);
                    text6.setVisibility(View.GONE);
                }
                realm.close();

                popupWindow = new PopupWindow(layoutDialog, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.shadow3, ActivityChat.this.getTheme()));
                } else {
                    popupWindow.setBackgroundDrawable((getResources().getDrawable(R.mipmap.shadow3)));
                }
                if (popupWindow.isOutsideTouchable()) {
                    popupWindow.dismiss();
                }

                popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
                popupWindow.showAtLocation(rippleView, Gravity.RIGHT | Gravity.TOP, (int) getResources().getDimension(R.dimen.dp16), (int) getResources().getDimension(R.dimen.dp32));

                text1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        initLayoutSearchNavigation();
                        findViewById(R.id.ac_green_line).setVisibility(View.GONE);
                        popupWindow.dismiss();
                        //findViewById(R.id.toolbarContainer).setVisibility(View.GONE);
                        ll_Search.setVisibility(View.VISIBLE);
                        popupWindow.dismiss();
                        ll_navigate_Message.setVisibility(View.VISIBLE);
                        viewAttachFile.setVisibility(View.GONE);
                        edtSearchMessage.requestFocus();
                    }
                });
                text2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new MaterialDialog.Builder(ActivityChat.this).title(R.string.clear_history).content(R.string.clear_history_content).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                onSelectRoomMenu("txtClearHistory", (int) mRoomId);
                            }
                        }).negativeText(R.string.B_cancel).show();
                        popupWindow.dismiss();
                    }
                });
                text3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new MaterialDialog.Builder(ActivityChat.this).title(R.string.delete_chat).content(R.string.delete_chat_content).positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                onSelectRoomMenu("txtDeleteChat", (int) mRoomId);
                            }
                        }).negativeText(R.string.B_cancel).show();
                        popupWindow.dismiss();
                    }
                });
                text4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        onSelectRoomMenu("txtMuteNotification", (int) mRoomId);

                        popupWindow.dismiss();
                    }
                });
                text5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        popupWindow.dismiss();
                        new MaterialDialog.Builder(ActivityChat.this).title(R.string.convert_chat_to_group_title).content(R.string.convert_chat_to_group_content).positiveText(R.string.B_ok).negativeText(R.string.B_cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                                dialog.dismiss();
                                G.onConvertToGroup.openFragmentOnActivity("ConvertToGroup", mRoomId);
                            }
                        }).show();
                    }
                });

                text6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        popupWindow.dismiss();

                        RealmRoomMessage.ClearAllMessage(false, mRoomId);
                        mAdapter.clear();

                        llScrollNavigate.setVisibility(View.GONE);

                        recyclerView.addOnScrollListener(scrollListener);


                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.add(new ProgressWaiting(ActivityChat.this).withIdentifier(SUID.id().get()));
                            }
                        });
                        /**
                         * get history from server
                         */
                        getOnlineMessage(0);
                    }
                });
            }
        });

        imvSmileButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_smile_button);

        edtChat = (EmojiEditText) findViewById(R.id.chl_edt_chat);
        edtChat.requestFocus();

        imvSendButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_send_button);
        imvSendButton.setTextColor(Color.parseColor(G.attachmentColor));

        imvAttachFileButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_attach_button);
        layoutAttachBottom = (LinearLayout) findViewById(R.id.layoutAttachBottom);

        imvMicButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_mic_button);

        recyclerView = (RecyclerView) findViewById(R.id.chl_recycler_view_chat);
        // remove blinking for updates on items
        recyclerView.setItemAnimator(null);
        // following lines make scrolling smoother
        //recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(1000);
        recyclerView.setDrawingCacheEnabled(true);

        mAdapter = new MessagesAdapter<>(this, this, this);

        mAdapter.withFilterPredicate(new IItemAdapter.Predicate<AbstractMessage>() {
            @Override
            public boolean filter(AbstractMessage item, CharSequence constraint) {
                return !item.mMessage.messageText.toLowerCase().contains(constraint.toString().toLowerCase());
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(ActivityChat.this);
        /**
         * make start messages from bottom, this is exactly what Telegram and other messengers do
         * for their messages list
         */
        layoutManager.setStackFromEnd(true);

        /**
         * set behavior to RecyclerView
         * CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) recyclerView.getLayoutParams();
         * params.setBehavior(new ShouldScrolledBehavior(layoutManager, mAdapter));
         * recyclerView.setLayoutParams(params);
         */
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        /**
         * load message , use handler for load async
         */
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                //TODO [Saeed Mozaffari] [2017-02-28 4:20 PM] - use switchAddItem in getLocalMessages method
                switchAddItem(getLocalMessages(), true);
                manageForwardedMessage();

                if (messageId > 0) {
                    // TODO: 10/15/2016  if list biger then 50 mList list should load some data we need
                    scrollPosition = 0;
                    for (AbstractMessage chatItem : mAdapter.getAdapterItems()) {
                        if (chatItem.mMessage.messageID.equals(messageId + "")) {
                            break;
                        }
                        scrollPosition++;
                    }
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.scrollToPosition(scrollPosition);
                        }
                    }, 1500);
                } else {
                    /**
                     * show unread message
                     */
                    if (chatType != CHANNEL) {
                        addLayoutUnreadMessage();
                    }
                }
            }
        });

        llScrollNavigate = (FrameLayout) findViewById(R.id.ac_ll_scrool_navigate);
        txtNewUnreadMessage = (TextView) findViewById(R.id.cs_txt_unread_message);
        AndroidUtils.setBackgroundShapeColor(txtNewUnreadMessage, Color.parseColor(G.notificationColor));

        MaterialDesignTextView txtNavigationLayout = (MaterialDesignTextView) findViewById(R.id.ac_txt_down_navigation);
        AndroidUtils.setBackgroundShapeColor(txtNavigationLayout, Color.parseColor(G.appBarColor));

        llScrollNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                llScrollNavigate.setVisibility(View.GONE);

                if (countNewMessage > 0) {
                    for (int i = 0; i < mAdapter.getItemCount(); i++) {
                        if (mAdapter.getItem(i) instanceof UnreadMessage) mAdapter.remove(i);
                    }

                    RealmRoomMessage unreadMessage = new RealmRoomMessage();
                    unreadMessage.setMessageId(TimeUtils.currentLocalTime());
                    // -1 means time message
                    unreadMessage.setUserId(-1);
                    unreadMessage.setMessage(countNewMessage + " " + getString(R.string.unread_message));
                    unreadMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
                    int _posi = recyclerView.getAdapter().getItemCount() - countNewMessage;
                    mAdapter.add(_posi, new UnreadMessage(ActivityChat.this).setMessage(StructMessageInfo.convert(unreadMessage)).withIdentifier(SUID.id().get()));

                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                    llm.scrollToPositionWithOffset(_posi, 0);

                    countNewMessage = 0;
                } else {

                    if (recyclerView.getAdapter().getItemCount() > 0) {

                        LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();

                        int lastPosition = llm.findLastVisibleItemPosition();
                        if (lastPosition + 50 > mAdapter.getItemCount()) {
                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                        } else {
                            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                        }
                    }
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                if (!firsInitScrollPosition) {
                    lastPosition = lastVisiblePosition;
                    firsInitScrollPosition = true;
                }

                int state = lastPosition - lastVisiblePosition;

                if (state > 0) {
                    // up

                    if (countNewMessage == 0) {
                        llScrollNavigate.setVisibility(View.GONE);
                    } else {
                        llScrollNavigate.setVisibility(View.VISIBLE);

                        txtNewUnreadMessage.setText(countNewMessage + "");
                        txtNewUnreadMessage.setVisibility(View.VISIBLE);
                    }

                    lastPosition = lastVisiblePosition;
                } else if (state < 0) {
                    //down

                    if (mAdapter.getItemCount() - lastVisiblePosition > 10) {
                        llScrollNavigate.setVisibility(View.VISIBLE);
                        if (countNewMessage > 0) {
                            txtNewUnreadMessage.setText(countNewMessage + "");
                            txtNewUnreadMessage.setVisibility(View.VISIBLE);
                        } else {
                            txtNewUnreadMessage.setVisibility(View.GONE);
                        }
                    } else {
                        llScrollNavigate.setVisibility(View.GONE);
                        countNewMessage = 0;
                    }

                    lastPosition = lastVisiblePosition;
                }
            }
        });

        rippleBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityPopUpNotification.isGoingToChatFromPopUp) {
                    ActivityPopUpNotification.isGoingToChatFromPopUp = false;
                    Intent intent = new Intent(context, ActivityMain.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                finish();
            }
        });

        imvUserPicture = (ImageView) findViewById(R.id.chl_imv_user_picture);
        imvUserPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                goToProfile();
            }
        });

        lyt_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile();
            }
        });

        imvSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HelperSetAction.setCancel(mRoomId);

                clearDraftRequest();

                if (hasForward) {
                    manageForwardedMessage();

                    if (edtChat.getText().length() == 0) {
                        return;
                    }
                }

                if (ll_attach_text.getVisibility() == View.VISIBLE) {

                    //// if need to add time before insert new message
                    //if (isNeedAddTime) {
                    //    addTimeToList(SUID.id().get());
                    //}

                    sendMessage(latestRequestCode, listPathString.get(0));
                    listPathString.clear();
                    ll_attach_text.setVisibility(View.GONE);
                    edtChat.setText("");

                    if (mReplayLayout != null && userTriesReplay()) {
                        mReplayLayout.setTag(null);
                        mReplayLayout.setVisibility(View.GONE);
                    }

                    return;
                }

                /**
                 * if use click on edit message, the message's text will be put to the EditText
                 * i set the message object for that view's tag to obtain it here
                 * request message edit only if there is any changes to the message text
                 */

                if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                    final StructMessageInfo messageInfo = (StructMessageInfo) edtChat.getTag();
                    final String message = getWrittenMessage();
                    if (!message.equals(messageInfo.messageText)) {

                        final Realm realm1 = Realm.getDefaultInstance();
                        realm1.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoomMessage roomMessage = realm1.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(messageInfo.messageID)).findFirst();

                                RealmClientCondition realmClientCondition = realm1.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, mRoomId).findFirst();

                                RealmOfflineEdited realmOfflineEdited = realm.createObject(RealmOfflineEdited.class, SUID.id().get());
                                realmOfflineEdited.setMessageId(parseLong(messageInfo.messageID));
                                realmOfflineEdited.setMessage(message);
                                realmOfflineEdited = realm.copyToRealm(realmOfflineEdited);

                                realmClientCondition.getOfflineEdited().add(realmOfflineEdited);

                                if (roomMessage != null) {
                                    // update message text in database
                                    roomMessage.setMessage(message);
                                    roomMessage.setEdited(true);

                                    String linkInfo = HelperUrl.getLinkInfo(message);
                                    if (linkInfo.length() > 0) {
                                        roomMessage.setHasMessageLink(true);
                                        roomMessage.setLinkInfo(linkInfo);
                                    } else {
                                        roomMessage.setHasMessageLink(false);
                                    }

                                    RealmRoomMessage.addTimeIfNeed(roomMessage, realm);

                                    RealmRoomMessage.isEmojeInText(roomMessage, message);

                                }

                                RealmRoom rm = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                                if (rm != null) rm.setUpdatedTime(TimeUtils.currentLocalTime() / 1000);
                            }
                        });

                        realm1.close();
                        //End

                        // I'm in the room
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update message text in adapter
                                mAdapter.updateMessageText(parseLong(messageInfo.messageID), message);
                            }
                        });

                        /**
                         * should be null after requesting
                         */
                        edtChat.setTag(null);
                        if (mReplayLayout != null) {
                            mReplayLayout.setTag(null);
                        }
                        edtChat.setText("");

                        /**
                         * send edit message request
                         */
                        if (chatType == CHAT) {
                            new RequestChatEditMessage().chatEditMessage(mRoomId, parseLong(messageInfo.messageID), message);
                        } else if (chatType == GROUP) {
                            new RequestGroupEditMessage().groupEditMessage(mRoomId, parseLong(messageInfo.messageID), message);
                        } else if (chatType == CHANNEL) {
                            new RequestChannelEditMessage().channelEditMessage(mRoomId, parseLong(messageInfo.messageID), message);
                        }
                    }
                } else {

                    ///**
                    // * if need to add time before insert new message
                    // */
                    //if (isNeedAddTime) {
                    //    addTimeToList(SUID.id().get());
                    //}

                    // new message has written
                    final String message = getWrittenMessage();
                    final Realm realm = Realm.getDefaultInstance();
                    final long senderId = realm.where(RealmUserInfo.class).findFirst().getUserId();
                    if (!message.isEmpty()) {
                        final RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                        final String identity = Long.toString(SUID.id().get());
                        final long currentTime = TimeUtils.currentLocalTime();

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, parseLong(identity));

                                roomMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
                                roomMessage.setMessage(message);
                                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());

                                String linkInfo = HelperUrl.getLinkInfo(message);
                                if (linkInfo.length() > 0) {
                                    roomMessage.setHasMessageLink(true);
                                    roomMessage.setLinkInfo(linkInfo);
                                } else {
                                    roomMessage.setHasMessageLink(false);
                                }

                                RealmRoomMessage.addTimeIfNeed(roomMessage, realm);
                                RealmRoomMessage.isEmojeInText(roomMessage, message);

                                roomMessage.setRoomId(mRoomId);
                                roomMessage.setShowMessage(true);

                                roomMessage.setUserId(senderId);
                                roomMessage.setCreateTime(currentTime);

                                // user wants to replay to a message
                                if (mReplayLayout != null && mReplayLayout.getTag() instanceof StructMessageInfo) {
                                    RealmRoomMessage messageToReplay = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID)).findFirst();
                                    if (messageToReplay != null) {
                                        roomMessage.setReplyTo(messageToReplay);
                                    }
                                }
                            }
                        });

                        final RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(identity)).findFirst();

                        if (roomMessage != null) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    if (room != null) {
                                        room.setLastMessage(roomMessage);
                                        //room.setUpdatedTime(currentTime);
                                    }
                                }
                            });
                        }

                        if (chatType == CHANNEL) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    RealmChannelExtra realmChannelExtra = realm.createObject(RealmChannelExtra.class);
                                    realmChannelExtra.setMessageId(parseLong(identity));
                                    realmChannelExtra.setThumbsUp("0");
                                    realmChannelExtra.setThumbsDown("0");
                                    if (realmRoom.getChannelRoom().isSignature()) {
                                        realmChannelExtra.setSignature(realm.where(RealmUserInfo.class).findFirst().getUserInfo().getDisplayName());
                                    } else {
                                        realmChannelExtra.setSignature("");
                                    }
                                    realmChannelExtra.setViewsLabel("1");
                                    if (roomMessage != null) {
                                        roomMessage.setChannelExtra(realmChannelExtra);
                                    }
                                }
                            });
                        }
                        mAdapter.add(new TextItem(chatType, ActivityChat.this).setMessage(StructMessageInfo.convert(roomMessage)).withIdentifier(SUID.id().get()));

                        realm.close();

                        scrollToEnd();

                        if (G.userLogin) {
                            new ChatSendMessageUtil().build(chatType, mRoomId, roomMessage);
                        } else {
                            makeFailed(Long.parseLong(identity));
                        }

                        edtChat.setText("");

                        // if replay layout is visible, gone it
                        if (mReplayLayout != null) {
                            mReplayLayout.setTag(null);
                            mReplayLayout.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(G.context, R.string.please_write_your_message, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        imvAttachFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!initAttach) {
                    initAttach = true;
                    initAttach();
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                itemAdapterBottomSheet();
                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomSheetDialog.show();
                    }
                }, 100);
            }
        });

        imvMicButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (ContextCompat.checkSelfPermission(ActivityChat.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    try {
                        HelperPermision.getMicroPhonePermission(ActivityChat.this, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(ActivityChat.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        try {
                            HelperPermision.getStoragePermision(ActivityChat.this, null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        voiceRecord.setItemTag("ivVoice");
                        viewAttachFile.setVisibility(View.GONE);
                        viewMicRecorder.setVisibility(View.VISIBLE);
                        voiceRecord.startVoiceRecord();
                    }
                }

                return true;
            }
        });

        // to toggle between keyboard and emoji popup
        imvSmileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!initEmoji) {
                    initEmoji = true;
                    setUpEmojiPopup();
                }

                emojiPopup.toggle();
            }
        });

        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {

                if (text.length() > 0) {
                    HelperSetAction.setActionTyping(mRoomId, chatType);
                }

                // if in the seeting page send by enter is on message send by enter key
                if (text.toString().endsWith(System.getProperty("line.separator"))) {
                    if (sendByEnter) imvSendButton.performClick();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (ll_attach_text.getVisibility() == View.GONE && hasForward == false) {

                    if (edtChat.getText().length() > 0) {
                        layoutAttachBottom.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                layoutAttachBottom.setVisibility(View.GONE);
                            }
                        }).start();
                        imvSendButton.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imvSendButton.clearAnimation();
                                        imvSendButton.setVisibility(View.VISIBLE);
                                    }
                                });

                            }
                        }).start();
                    } else {
                        layoutAttachBottom.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);

                                layoutAttachBottom.setVisibility(View.VISIBLE);
                            }
                        }).start();
                        imvSendButton.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        imvSendButton.clearAnimation();
                                        imvSendButton.setVisibility(View.GONE);
                                    }
                                });

                            }
                        }).start();
                    }
                }
            }
        });
    }

    private void goToProfile() {
        if (chatType == CHAT) {//TODO [Saeed Mozaffari] [2016-09-07 11:46 AM] -  in if eshtebah ast // && chatPeerId != 134
            // check for iGap message ==> chatPeerId == 134(alan baraye check kardane) ,
            Intent intent = new Intent(G.context, ActivityContactsProfile.class);
            intent.putExtra("peerId", chatPeerId);
            intent.putExtra("RoomId", mRoomId);
            intent.putExtra("enterFrom", CHAT.toString());
            startActivity(intent);
        } else if (chatType == GROUP) {
            if (!isChatReadOnly) {
                Intent intent = new Intent(G.context, ActivityGroupProfile.class);
                intent.putExtra("RoomId", mRoomId);
                startActivity(intent);
            }
        } else if (chatType == CHANNEL) {
            Intent intent = new Intent(G.context, ActivityChannelProfile.class);
            intent.putExtra(Config.PutExtraKeys.CHANNEL_PROFILE_ROOM_ID_LONG.toString(), mRoomId);
            startActivity(intent);
        }
    }

    private void addLayoutUnreadMessage() {

        int unreadPosition = 0;
        int timeLayoutCount = 0;

        for (int i = mAdapter.getAdapterItemCount() - 1; i >= 0; i--) {
            try {
                if ((mAdapter.getAdapterItem(i) instanceof TimeItem)) {
                    if (i > 0) timeLayoutCount++;
                    continue;
                }

                if (mAdapter.getAdapterItem(i).mMessage.status.equals(ProtoGlobal.RoomMessageStatus.SEEN.toString()) || mAdapter.getAdapterItem(i).mMessage.isSenderMe() || mAdapter.getAdapterItem(i).mMessage.isAuthorMe()) {
                    unreadPosition = i;
                    break;
                }
            } catch (NullPointerException e) {
            }
        }

        int unreadMessageCount = mAdapter.getAdapterItemCount() - 1 - unreadPosition - timeLayoutCount;

        if (unreadMessageCount > 0) {
            RealmRoomMessage unreadMessage = new RealmRoomMessage();
            unreadMessage.setMessageId(TimeUtils.currentLocalTime());
            // -1 means time message
            unreadMessage.setUserId(-1);
            unreadMessage.setMessage(unreadMessageCount + " " + getString(R.string.unread_message));
            unreadMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
            mAdapter.add(unreadPosition + 1, new UnreadMessage(this).setMessage(StructMessageInfo.convert(unreadMessage)).withIdentifier(SUID.id().get()));

            LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
            llm.scrollToPositionWithOffset(unreadPosition + 1, 0);
        }
    }

    private void setUserStatus(String status, long time) {
        userStatus = status;
        userTime = time;
        if (RealmRoom.isCloudRoom(mRoomId)) {
            txtLastSeen.setText(getResources().getString(R.string.chat_with_yourself));
            avi.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
            }
        } else {
            if (status != null) {
                if (status.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                    txtLastSeen.setText(LastSeenTimeUtil.computeTime(chatPeerId, time, true, false));
                } else {
                    txtLastSeen.setText(status);
                }
                avi.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                }
                // change english number to persian number
                if (HelperCalander.isLanguagePersian) txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));

                checkAction();
            }
        }
    }

    private void initLayoutSearchNavigation() {

        ll_navigate_Message = (LinearLayout) findViewById(R.id.ac_ll_message_navigation);
        btnUpMessage = (TextView) findViewById(R.id.ac_btn_message_up);
        txtClearMessageSearch = (MaterialDesignTextView) findViewById(R.id.ac_btn_clear_message_search);
        btnDownMessage = (TextView) findViewById(R.id.ac_btn_message_down);
        txtMessageCounter = (TextView) findViewById(R.id.ac_txt_message_counter);

        btnUpMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedPosition > 0) {
                    deSelectMessage(selectedPosition);
                    selectedPosition--;
                    selectMessage(selectedPosition);
                    recyclerView.scrollToPosition(selectedPosition);
                    txtMessageCounter.setText(selectedPosition + 1 + " " + getString(R.string.of) + " " + messageCounter);
                }
            }
        });

        btnDownMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition < messageCounter - 1) {
                    deSelectMessage(selectedPosition);
                    selectedPosition++;
                    selectMessage(selectedPosition);
                    recyclerView.scrollToPosition(selectedPosition);
                    txtMessageCounter.setText(selectedPosition + 1 + " " + getString(R.string.of) + messageCounter);
                }
            }
        });
        final RippleView rippleClose = (RippleView) findViewById(R.id.chl_btn_close_ripple_search_message);
        rippleClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deSelectMessage(selectedPosition);
                edtSearchMessage.setText("");
            }
        });

        ll_Search = (LinearLayout) findViewById(R.id.ac_ll_search_message);
        RippleView rippleBack = (RippleView) findViewById(R.id.chl_ripple_back);
        rippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deSelectMessage(selectedPosition);
                edtSearchMessage.setText("");
                ll_Search.setVisibility(View.GONE);
                findViewById(R.id.toolbarContainer).setVisibility(View.VISIBLE);
                ll_navigate_Message.setVisibility(View.GONE);
                viewAttachFile.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        //btnCloseLayoutSearch = (Button) findViewById(R.id.ac_btn_close_layout_search_message);
        edtSearchMessage = (EditText) findViewById(R.id.chl_edt_search_message);
        edtSearchMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {

                mAdapter.filter(charSequence);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        messageCounter = mAdapter.getAdapterItemCount();

                        if (messageCounter > 0) {
                            selectedPosition = messageCounter - 1;
                            recyclerView.scrollToPosition(selectedPosition);

                            if (charSequence.length() > 0) {
                                selectMessage(selectedPosition);
                                txtMessageCounter.setText(messageCounter + " " + getString(R.string.of) + messageCounter);
                            } else {
                                txtMessageCounter.setText("0 " + getString(R.string.of) + " 0");
                            }
                        } else {
                            txtMessageCounter.setText("0 " + getString(R.string.of) + messageCounter);
                            selectedPosition = 0;
                        }
                    }
                }, 600);

                if (charSequence.length() > 0) {
                    txtClearMessageSearch.setTextColor(Color.WHITE);
                    ((View) rippleClose).setEnabled(true);
                } else {
                    txtClearMessageSearch.setTextColor(Color.parseColor("#dededd"));
                    ((View) rippleClose).setEnabled(false);
                    txtMessageCounter.setText("0 " + getString(R.string.of) + " 0");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    /**
     * init layout for hashtak up and down
     */
    private void initLayoutHashNavigationCallback() {

        hashListener = new OnComplete() {
            @Override
            public void complete(boolean result, String text, String messageId) {

                if (!initHash) {
                    initHash = true;
                    initHashView();
                }

                searchHash.setHashString(text);
                searchHash.setPosition(messageId);
                ll_navigateHash.setVisibility(View.VISIBLE);
                viewAttachFile.setVisibility(View.GONE);
            }
        };
    }

    /**
     * init layout hashtak for up and down
     */
    private void initHashView() {
        ll_navigateHash = (LinearLayout) findViewById(R.id.ac_ll_hash_navigation);
        btnUpHash = (TextView) findViewById(R.id.ac_btn_hash_up);
        btnDownHash = (TextView) findViewById(R.id.ac_btn_hash_down);
        txtHashCounter = (TextView) findViewById(R.id.ac_txt_hash_counter);

        searchHash = new SearchHash();

        btnHashLayoutClose = (Button) findViewById(R.id.ac_btn_hash_close);
        btnHashLayoutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ll_navigateHash.setVisibility(View.GONE);
                viewAttachFile.setVisibility(View.VISIBLE);

                if (mAdapter.getItem(searchHash.currentSelectedPosition).mMessage.view != null) {
                    ((FrameLayout) mAdapter.getItem(searchHash.currentSelectedPosition).mMessage.view).setForeground(null);
                }
            }
        });

        btnUpHash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchHash.upHash();
            }
        });

        btnDownHash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchHash.downHash();
            }
        });
    }

    private void insertShearedData() {
        /**
         * run this method with delay , because client get local message with delay
         * for show messages with async state and before run getLocalMessage this shared
         * item added to realm and view, and after that getLocalMessage called and new item
         * got from realm and add to view again but in this time from getLocalMessage method
         */
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (HelperGetDataFromOtherApp.hasSharedData) {

                    HelperGetDataFromOtherApp.hasSharedData = false;
                    if (messageType == HelperGetDataFromOtherApp.FileType.message) {

                        String message = HelperGetDataFromOtherApp.message;
                        edtChat.setText(message);
                        imvSendButton.performClick();

                    } else if (messageType == HelperGetDataFromOtherApp.FileType.image) {

                        for (int i = 0; i < HelperGetDataFromOtherApp.messageFileAddress.size(); i++) {
                            sendMessage(AttachFile.request_code_TAKE_PICTURE, HelperGetDataFromOtherApp.messageFileAddress.get(i).toString());
                        }

                    } else if (messageType == HelperGetDataFromOtherApp.FileType.video) {

                        for (int i = 0; i < HelperGetDataFromOtherApp.messageFileAddress.size(); i++) {
                            sendMessage(request_code_VIDEO_CAPTURED, HelperGetDataFromOtherApp.messageFileAddress.get(i).toString());
                        }

                    } else if (messageType == HelperGetDataFromOtherApp.FileType.audio) {

                        for (int i = 0; i < HelperGetDataFromOtherApp.messageFileAddress.size(); i++) {
                            sendMessage(AttachFile.request_code_pic_audi, HelperGetDataFromOtherApp.messageFileAddress.get(i).toString());
                        }

                    } else if (messageType == HelperGetDataFromOtherApp.FileType.file) {

                        for (int i = 0; i < HelperGetDataFromOtherApp.messageFileAddress.size(); i++) {

                            if (HelperGetDataFromOtherApp.fileTypeArray.size() > 0) {
                                HelperGetDataFromOtherApp.FileType fileType = HelperGetDataFromOtherApp.fileTypeArray.get(i);
                                if (fileType == HelperGetDataFromOtherApp.FileType.image) {
                                    sendMessage(AttachFile.request_code_TAKE_PICTURE, HelperGetDataFromOtherApp.messageFileAddress.get(i).toString());
                                } else if (fileType == HelperGetDataFromOtherApp.FileType.video) {
                                    sendMessage(request_code_VIDEO_CAPTURED, HelperGetDataFromOtherApp.messageFileAddress.get(i).toString());
                                } else if (fileType == HelperGetDataFromOtherApp.FileType.audio) {
                                    sendMessage(AttachFile.request_code_pic_audi, HelperGetDataFromOtherApp.messageFileAddress.get(i).toString());
                                } else if (fileType == HelperGetDataFromOtherApp.FileType.file) {
                                    sendMessage(AttachFile.request_code_open_document, HelperGetDataFromOtherApp.messageFileAddress.get(i).toString());
                                }
                            }

                        }
                    }
                    HelperGetDataFromOtherApp.messageType = null;
                }
            }
        }, 300);
    }

    private void shearedDataToOtherProgram(StructMessageInfo messageInfo) {

        if (messageInfo == null) return;

        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            String chooserDialogText = "";

            ProtoGlobal.RoomMessageType type = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getMessageType() : messageInfo.messageType;

            switch (type.toString()) {

                case "TEXT":
                    intent.setType("text/plain");
                    String message = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getMessage() : messageInfo.messageText;
                    intent.putExtra(Intent.EXTRA_TEXT, message);
                    break;
                case "CONTACT":
                    intent.setType("text/plain");
                    String messageContact;
                    if (messageInfo.forwardedFrom != null) {
                        messageContact = messageInfo.forwardedFrom.getRoomMessageContact().getFirstName() + " " + messageInfo.forwardedFrom.getRoomMessageContact().getLastName() + "\n" + messageInfo.forwardedFrom.getRoomMessageContact().getLastPhoneNumber();
                    } else {
                        messageContact = messageInfo.userInfo.firstName + "\n" + messageInfo.userInfo.phone;
                    }
                    intent.putExtra(Intent.EXTRA_TEXT, messageContact);
                    break;
                case "LOCATION":
                    String imagePathPosition = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getLocation().getImagePath() : messageInfo.location.getImagePath();
                    intent.setType("image/*");
                    if (imagePathPosition != null) {
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imagePathPosition)));
                    }
                    break;
                case "VOICE":
                case "AUDIO":
                case "AUDIO_TEXT":
                    intent.setType("audio/*");
                    putExtra(intent, messageInfo);
                    chooserDialogText = getString(R.string.share_audio_file);
                    break;
                case "IMAGE":
                case "IMAGE_TEXT":
                    intent.setType("image/*");
                    putExtra(intent, messageInfo);
                    chooserDialogText = getString(R.string.share_image);
                    break;
                case "VIDEO":
                case "VIDEO_TEXT":
                    intent.setType("video/*");
                    putExtra(intent, messageInfo);
                    chooserDialogText = getString(R.string.share_video_file);
                    break;
                case "FILE":
                case "FILE_TEXT":
                    String mfilepath = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getAttachment().getLocalFilePath() : messageInfo.attachment.getLocalFilePath();
                    if (mfilepath != null) {
                        Uri uri = Uri.fromFile(new File(mfilepath));
                        String mimeType = FileUtils.getMimeType(ActivityChat.this, uri);

                        if (mimeType == null || mimeType.length() < 1) {
                            mimeType = "*/*";
                        }

                        intent.setType(mimeType);
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        chooserDialogText = getString(R.string.share_file);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(G.context, R.string.file_not_download_yet, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    break;
            }

            startActivity(Intent.createChooser(intent, chooserDialogText));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putExtra(Intent intent, StructMessageInfo messageInfo) {

        try {
            String filePath = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getAttachment().getLocalFilePath() : messageInfo.attachment.getLocalFilePath();

            if (filePath != null) {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAvatar() {

        long idForGetAvatar;
        HelperAvatar.AvatarType type;
        if (chatType == CHAT) {
            idForGetAvatar = chatPeerId;
            type = HelperAvatar.AvatarType.USER;
        } else {
            idForGetAvatar = mRoomId;
            type = HelperAvatar.AvatarType.ROOM;
        }

        HelperAvatar.getAvatar(idForGetAvatar, type, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(avatarPath), imvUserPicture);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imvUserPicture.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvUserPicture.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });
    }

    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        imvSmileButton.setText(drawableResourceId);
    }

    @Override
    public void onBackPressed() {

        android.app.Fragment fragment = getFragmentManager().findFragmentByTag("ShowImageMessage");

        if (fragment != null) {

            getFragmentManager().beginTransaction().remove(fragment).commit();

            // for update view that image download in fragment show image
            int count = FragmentShowImage.downloadedList.size();

            for (int i = 0; i < count; i++) {
                String token = FragmentShowImage.downloadedList.get(i) + "";

                for (int j = mAdapter.getAdapterItemCount() - 1; j >= 0; j--) {
                    try {

                        String mToken = mAdapter.getItem(j).mMessage.forwardedFrom != null ? mAdapter.getItem(j).mMessage.forwardedFrom.getAttachment().getToken() : mAdapter.getItem(j).mMessage.attachment.token;

                        if (mToken.equals(token)) {
                            mAdapter.notifyItemChanged(j);
                        }
                    } catch (NullPointerException e) {
                    }
                }
            }

            FragmentShowImage.downloadedList.clear();



        } else if (mAdapter != null && mAdapter.getSelections().size() > 0) {
            mAdapter.deselect();
        } else if (emojiPopup != null && emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        } else {

            if (ActivityPopUpNotification.isGoingToChatFromPopUp) {
                ActivityPopUpNotification.isGoingToChatFromPopUp = false;
                Intent intent = new Intent(context, ActivityMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isGoingFromUserLink) {
            new RequestClientUnsubscribeFromRoom().clientUnsubscribeFromRoom(mRoomId);
        }
        onMusicListener = null;

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStop() {
        setDraft();
        HelperNotificationAndBadge.isChatRoomNow = false;

        if (isNotJoin) {

            /**
             * delete all  deleted row from database
             */
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, true).findAll().deleteAllFromRealm();
                }
            });
            realm.close();
        }

        if (mRealm != null) mRealm.close();

        super.onStop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        voiceRecord.dispatchTouchEvent(event);

        return super.dispatchTouchEvent(event);
    }

    /**
     * initialize bottomSheet for use in attachment
     */
    private void initAttach() {

        fastItemAdapter = new FastItemAdapter();

        viewBottomSheet = getLayoutInflater().inflate(R.layout.bottom_sheet, null);

        send = (ImageView) viewBottomSheet.findViewById(R.id.send);
        txtCountItem = (TextView) viewBottomSheet.findViewById(R.id.txtNumberItem);
        ViewGroup camera = (ViewGroup) viewBottomSheet.findViewById(R.id.camera);
        ViewGroup picture = (ViewGroup) viewBottomSheet.findViewById(R.id.picture);
        ViewGroup video = (ViewGroup) viewBottomSheet.findViewById(R.id.video);
        ViewGroup music = (ViewGroup) viewBottomSheet.findViewById(R.id.music);
        ViewGroup document = (ViewGroup) viewBottomSheet.findViewById(R.id.document);
        ViewGroup close = (ViewGroup) viewBottomSheet.findViewById(R.id.close);
        ViewGroup file = (ViewGroup) viewBottomSheet.findViewById(R.id.file);
        ViewGroup paint = (ViewGroup) viewBottomSheet.findViewById(R.id.paint);
        ViewGroup location = (ViewGroup) viewBottomSheet.findViewById(R.id.location);
        ViewGroup contact = (ViewGroup) viewBottomSheet.findViewById(R.id.contact);

        onPathAdapterBottomSheet = new OnPathAdapterBottomSheet() {
            @Override
            public void path(String path, boolean isCheck) {

                if (isCheck) {
                    listPathString.add(path);
                } else {
                    listPathString.remove(path);
                }

                listPathString.size();
                if (listPathString.size() > 0) {
                    send.setImageResource(R.mipmap.send2);
                    isCheckBottomSheet = true;
                    txtCountItem.setText("" + listPathString.size() + " item");
                } else {
                    send.setImageResource(R.mipmap.ic_close);
                    isCheckBottomSheet = false;
                    txtCountItem.setText(getResources().getString(R.string.navigation_drawer_close));
                }
            }
        };

        rcvBottomSheet = (RecyclerView) viewBottomSheet.findViewById(R.id.rcvContent);
        rcvBottomSheet.setLayoutManager(new GridLayoutManager(ActivityChat.this, 1, GridLayoutManager.HORIZONTAL, false));
        rcvBottomSheet.setItemAnimator(new DefaultItemAnimator());
        rcvBottomSheet.setDrawingCacheEnabled(true);
        rcvBottomSheet.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rcvBottomSheet.setItemViewCacheSize(100);
        rcvBottomSheet.setAdapter(fastItemAdapter);
        bottomSheetDialog = new BottomSheetDialog(ActivityChat.this);
        bottomSheetDialog.setContentView(viewBottomSheet);

        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                dialog.dismiss();
                send.setImageResource(R.mipmap.ic_close);
                txtCountItem.setText(getResources().getString(R.string.navigation_drawer_close));
            }
        });


        fastItemAdapter.withSelectable(true);
        listPathString = new ArrayList<>();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

                if (sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1) == 1) {
                    attachFile.showDialogOpenCamera(toolbar, null);
                } else {
                    attachFile.showDialogOpenCamera(toolbar, null);
                }
            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestOpenGalleryForImageMultipleSelect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestOpenGalleryForVideoMultipleSelect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestPickAudio();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestOpenDocumentFolder();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCheckBottomSheet) {
                    bottomSheetDialog.dismiss();
                    for (String path : listPathString) {
                        if (!path.toLowerCase().endsWith(".gif")) {
                            String localpathNew = attachFile.saveGalleryPicToLocal(path);
                            sendMessage(AttachFile.requestOpenGalleryForImageMultipleSelect, localpathNew);
                            fastItemAdapter.clear();
                            send.setImageResource(R.mipmap.ic_close);
                            txtCountItem.setText(getResources().getString(R.string.navigation_drawer_close));
                        }
                    }
                } else {
                    bottomSheetDialog.dismiss();
                }

            }
        });
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestPickFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        paint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestPaint();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestGetPosition(complete);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                try {
                    attachFile.requestPickContact();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private boolean userTriesReplay() {
        return mReplayLayout != null && mReplayLayout.getTag() instanceof StructMessageInfo;
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            HelperSetAction.sendCancel(messageId);

            if (prgWaiting != null) {
                prgWaiting.setVisibility(View.GONE);
            }
        }

        if (requestCode == AttachFile.request_code_position && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                attachFile.requestGetPosition(complete);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (resultCode == Activity.RESULT_OK) {

            HelperSetAction.sendCancel(messageId);

            if (requestCode == AttachFile.request_code_contact_phone) {
                latestUri = data.getData();
                sendMessage(requestCode, "");
                return;
            }

            listPathString = null;
            if (AttachFile.request_code_TAKE_PICTURE == requestCode) {

                listPathString = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    listPathString.add(AttachFile.mCurrentPhotoPath);
                } else {
                    listPathString.add(AttachFile.imagePath);
                }

                latestUri = null; // check
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (data.getClipData() != null) { // multi select file
                        listPathString = attachFile.getClipData(data.getClipData());
                    }
                }

                if (listPathString == null || listPathString.size() < 1) {
                    listPathString = new ArrayList<>();
                    listPathString.add(getFilePathFromUri(data.getData()));
                }
            }
            latestRequestCode = requestCode;

            if (listPathString.size() == 1) {
                showDraftLayout();
                setDraftMessage(requestCode);
            } else if (listPathString.size() > 1) {
                for (final String path : listPathString) {
                    if (requestCode == AttachFile.requestOpenGalleryForImageMultipleSelect && !path.toLowerCase().endsWith(".gif")) {
                        String localpathNew = attachFile.saveGalleryPicToLocal(path);
                        sendMessage(requestCode, localpathNew);

                    } else {
                        sendMessage(requestCode, path);
                    }
                }
            }

            if (listPathString.size() == 1) {

                if (sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1) == 1) {

                    if (requestCode == AttachFile.requestOpenGalleryForImageMultipleSelect) {
                        if (!listPathString.get(0).toLowerCase().endsWith(".gif")) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                listPathString.set(0, attachFile.saveGalleryPicToLocal(listPathString.get(0)));

                                Uri uri = Uri.parse(listPathString.get(0));
                                Intent intent = new Intent(ActivityChat.this, ActivityCrop.class);
                                intent.putExtra("IMAGE_CAMERA", AttachFile.getFilePathFromUri(uri));
                                intent.putExtra("TYPE", "gallery");
                                intent.putExtra("PAGE", "chat");
                                startActivityForResult(intent, IntentRequests.REQ_CROP);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (prgWaiting != null) {
                                            prgWaiting.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            } else {
                                listPathString.set(0, attachFile.saveGalleryPicToLocal(listPathString.get(0)));
                                Intent intent = new Intent(ActivityChat.this, ActivityCrop.class);
                                Uri uri = Uri.parse(listPathString.get(0));
                                uri = Uri.parse("file://" + AttachFile.getFilePathFromUri(uri));
                                intent.putExtra("IMAGE_CAMERA", uri.toString());
                                intent.putExtra("TYPE", "gallery");
                                intent.putExtra("PAGE", "chat");
                                startActivityForResult(intent, IntentRequests.REQ_CROP);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (prgWaiting != null) {
                                            prgWaiting.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }


                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (prgWaiting != null) {
                                        prgWaiting.setVisibility(View.GONE);
                                    }
                                }
                            });
                            return;
                        }
                    } else if (requestCode == AttachFile.request_code_TAKE_PICTURE) {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                            ImageHelper.correctRotateImage(listPathString.get(0), true);
                            Intent intent = new Intent(ActivityChat.this, ActivityCrop.class);

                            intent.putExtra("IMAGE_CAMERA", listPathString.get(0));
                            intent.putExtra("TYPE", "camera");
                            intent.putExtra("PAGE", "chat");
                            startActivityForResult(intent, IntentRequests.REQ_CROP);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (prgWaiting != null) {
                                        prgWaiting.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            ImageHelper.correctRotateImage(listPathString.get(0), true);

                            Intent intent = new Intent(ActivityChat.this, ActivityCrop.class);

                            intent.putExtra("IMAGE_CAMERA", "file://" + listPathString.get(0));
                            intent.putExtra("TYPE", "camera");
                            intent.putExtra("PAGE", "chat");
                            startActivityForResult(intent, IntentRequests.REQ_CROP);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (prgWaiting != null) {
                                        prgWaiting.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }


                    }
                } else {

                    if (requestCode == AttachFile.request_code_TAKE_PICTURE) {

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ImageHelper.correctRotateImage(listPathString.get(0), true);
                            }
                        });
                        thread.start();
                    } else if (requestCode == AttachFile.requestOpenGalleryForImageMultipleSelect && !listPathString.get(0).toLowerCase().endsWith(".gif")) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                listPathString.set(0, attachFile.saveGalleryPicToLocal(listPathString.get(0)));
                            }
                        });
                        thread.start();
                    }
                }
            }
        }
    }

    private void setDraftMessage(int requestCode) {
        if (listPathString == null) return;
        if (listPathString.size() < 1) return;

        switch (requestCode) {
            case AttachFile.request_code_TAKE_PICTURE:
                txtFileNameForSend.setText(getString(R.string.image_selected_for_send));
                break;
            case AttachFile.requestOpenGalleryForImageMultipleSelect:
                if (listPathString.size() == 1) {
                    if (!listPathString.get(0).toLowerCase().endsWith(".gif")) {
                        txtFileNameForSend.setText(getString(R.string.image_selected_for_send));
                    } else {
                        txtFileNameForSend.setText(getString(R.string.gif_selected_for_send));
                    }
                } else {
                    txtFileNameForSend.setText(listPathString.size() + getString(R.string.image_selected_for_send));
                }

                break;

            case AttachFile.requestOpenGalleryForVideoMultipleSelect:
                txtFileNameForSend.setText(getString(R.string.multi_video_selected_for_send));
                break;
            case request_code_VIDEO_CAPTURED:

                if (listPathString.size() == 1) {
                    txtFileNameForSend.setText(getString(R.string.video_selected_for_send));
                } else {
                    txtFileNameForSend.setText(listPathString.size() + getString(R.string.video_selected_for_send));
                }
                break;

            case AttachFile.request_code_pic_audi:
                if (listPathString.size() == 1) {
                    txtFileNameForSend.setText(getString(R.string.audio_selected_for_send));
                } else {
                    txtFileNameForSend.setText(listPathString.size() + getString(R.string.audio_selected_for_send));
                }
                break;
            case AttachFile.request_code_pic_file:
                txtFileNameForSend.setText(getString(R.string.file_selected_for_send));
                break;
            case AttachFile.request_code_open_document:
                if (listPathString.size() == 1) {
                    txtFileNameForSend.setText(getString(R.string.file_selected_for_send));
                }
                break;
            case AttachFile.request_code_paint:
                if (listPathString.size() == 1) {
                    txtFileNameForSend.setText(getString(R.string.pain_selected_for_send));
                }
                break;
            case AttachFile.request_code_contact_phone:
                txtFileNameForSend.setText(getString(R.string.phone_selected_for_send));
                break;
            case IntentRequests.REQ_CROP:
                txtFileNameForSend.setText(getString(R.string.crop_selected_for_send));
                break;
        }
    }


    private void sendMessage(int requestCode, String filePath) {
        Realm realm = Realm.getDefaultInstance();
        long messageId = SUID.id().get();
        final long updateTime = TimeUtils.currentLocalTime();
        ProtoGlobal.RoomMessageType messageType = null;
        String fileName = null;
        long duration = 0;
        long fileSize = 0;
        int[] imageDimens = {0, 0};
        final long senderID = realm.where(RealmUserInfo.class).findFirst().getUserId();

        /**
         * check if path is uri detect real path from uri
         */
        String path = AndroidUtils.pathFromContentUri(getApplicationContext(), Uri.parse(filePath));
        if (path != null) {
            filePath = path;
        }

        StructMessageInfo messageInfo = null;

        switch (requestCode) {
            case IntentRequests.REQ_CROP:
                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isMessageWrote()) {
                    messageType = IMAGE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                }
                if (userTriesReplay()) {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
                }
                break;
            case AttachFile.request_code_TAKE_PICTURE:

                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                if (AndroidUtils.getImageDimens(filePath)[0] == 0 && AndroidUtils.getImageDimens(filePath)[1] == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(G.context, "Picture Not Loaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isMessageWrote()) {
                    messageType = IMAGE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                }
                if (userTriesReplay()) {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
                }

                break;

            case AttachFile.requestOpenGalleryForImageMultipleSelect:
                if (!filePath.toLowerCase().endsWith(".gif")) {
                    if (isMessageWrote()) {
                        messageType = IMAGE_TEXT;
                    } else {
                        messageType = ProtoGlobal.RoomMessageType.IMAGE;
                    }
                } else {
                    if (isMessageWrote()) {
                        messageType = GIF_TEXT;
                    } else {
                        messageType = ProtoGlobal.RoomMessageType.GIF;
                    }
                }

                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                imageDimens = AndroidUtils.getImageDimens(filePath);

                if (userTriesReplay()) {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
                }
                break;

            case AttachFile.requestOpenGalleryForVideoMultipleSelect:
            case request_code_VIDEO_CAPTURED:
                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                duration = AndroidUtils.getAudioDuration(getApplicationContext(), filePath) / 1000;
                if (isMessageWrote()) {
                    messageType = VIDEO_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.VIDEO;
                }
                File videoFile = new File(filePath);
                String videoFileMime = FileUtils.getMimeType(videoFile);
                if (userTriesReplay()) {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, videoFileMime, filePath, null, filePath, null, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), getWrittenMessage(), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, videoFileMime, filePath, null, filePath, null, updateTime);
                }
                break;
            case AttachFile.request_code_pic_audi:
                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                duration = AndroidUtils.getAudioDuration(getApplicationContext(), filePath) / 1000;
                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.AUDIO_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.AUDIO;
                }
                String songArtist = AndroidUtils.getAudioArtistName(filePath);
                long songDuration = AndroidUtils.getAudioDuration(getApplicationContext(), filePath);

                messageInfo = StructMessageInfo.buildForAudio(mRoomId, messageId, senderID, ProtoGlobal.RoomMessageStatus.SENDING, messageType, MyType.SendType.send, updateTime, getWrittenMessage(), null, filePath, songArtist, songDuration, userTriesReplay() ? parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID) : -1);
                break;
            case AttachFile.request_code_pic_file:
            case AttachFile.request_code_open_document:

                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.FILE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.FILE;
                }
                File fileFile = new File(filePath);
                String fileFileMime = FileUtils.getMimeType(fileFile);
                if (userTriesReplay()) {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, fileFileMime, filePath, null, filePath, null, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), getWrittenMessage(), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, fileFileMime, filePath, null, filePath, null, updateTime);
                }
                break;
            case AttachFile.request_code_contact_phone:
                if (latestUri == null) {
                    break;
                }
                ContactUtils contactUtils = new ContactUtils(getApplicationContext(), latestUri);
                String name = contactUtils.retrieveName();
                String number = contactUtils.retrieveNumber();
                messageType = CONTACT;
                messageInfo = StructMessageInfo.buildForContact(mRoomId, messageId, senderID, MyType.SendType.send, updateTime, ProtoGlobal.RoomMessageStatus.SENDING, name, "", number, userTriesReplay() ? parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID) : -1);
                break;
            case AttachFile.request_code_paint:
                fileName = new File(filePath).getName();

                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isMessageWrote()) {
                    messageType = IMAGE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                }
                if (userTriesReplay()) {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), getWrittenMessage(), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType, MyType.SendType.send, null, filePath, updateTime);
                }
                break;
        }

        final ProtoGlobal.RoomMessageType finalMessageType = messageType;
        final String finalFilePath = filePath;
        final String finalFileName = fileName;
        final long finalDuration = duration;
        final long finalFileSize = fileSize;
        final int[] finalImageDimens = imageDimens;



        //messageInfo.channelExtra = StructChannelExtra.makeDefaultStructure(messageId, mRoomId);

        final StructMessageInfo finalMessageInfo = messageInfo;
        final long finalMessageId = messageId;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, finalMessageId);

                roomMessage.setMessageType(finalMessageType);
                roomMessage.setMessage(getWrittenMessage());

                String linkInfo = HelperUrl.getLinkInfo(getWrittenMessage());
                if (linkInfo.length() > 0) {
                    roomMessage.setHasMessageLink(true);
                    roomMessage.setLinkInfo(linkInfo);
                } else {
                    roomMessage.setHasMessageLink(false);
                }

                RealmRoomMessage.addTimeIfNeed(roomMessage, realm);
                RealmRoomMessage.isEmojeInText(roomMessage, getWrittenMessage());


                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                roomMessage.setRoomId(mRoomId);
                roomMessage.setAttachment(finalMessageId, finalFilePath, finalImageDimens[0], finalImageDimens[1], finalFileSize, finalFileName, finalDuration, LocalFileType.FILE);
                roomMessage.setUserId(senderID);
                roomMessage.setShowMessage(true);
                roomMessage.setCreateTime(updateTime);
                if (userTriesReplay()) {
                    if (finalMessageInfo != null && finalMessageInfo.replayTo != null) {
                        roomMessage.setReplyTo(finalMessageInfo.replayTo);
                    }
                }

                /**
                 * make channel extra if room is channel
                 */
                if (chatType == CHANNEL) {
                    roomMessage.setChannelExtra(RealmChannelExtra.convert(realm, StructChannelExtra.makeDefaultStructure(finalMessageId, mRoomId)));
                }

                if (finalMessageType == CONTACT) {
                    RealmRoomMessageContact realmRoomMessageContact = realm.createObject(RealmRoomMessageContact.class, SUID.id().get());
                    realmRoomMessageContact.setFirstName(finalMessageInfo.userInfo.firstName);
                    realmRoomMessageContact.setLastName(finalMessageInfo.userInfo.lastName);
                    realmRoomMessageContact.addPhone(finalMessageInfo.userInfo.phone);
                    roomMessage.setRoomMessageContact(realmRoomMessageContact);
                }

                if (finalMessageType != CONTACT) {
                    finalMessageInfo.attachment = StructMessageAttachment.convert(roomMessage.getAttachment());
                }

                if (finalMessageType == ProtoGlobal.RoomMessageType.VIDEO || finalMessageType == VIDEO_TEXT) {
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(finalFilePath, MediaStore.Video.Thumbnails.MINI_KIND);
                    if (bitmap != null) {
                        String path = AndroidUtils.saveBitmap(bitmap);
                        roomMessage.getAttachment().setLocalThumbnailPath(path);
                        roomMessage.getAttachment().setWidth(bitmap.getWidth());
                        roomMessage.getAttachment().setHeight(bitmap.getHeight());

                        finalMessageInfo.attachment.setLocalFilePath(roomMessage.getMessageId(), finalFilePath);
                        finalMessageInfo.attachment.width = bitmap.getWidth();
                        finalMessageInfo.attachment.height = bitmap.getHeight();
                    }
                }

                if (finalFilePath != null && finalMessageType != CONTACT) {
                    new UploadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, finalFilePath, finalMessageId, finalMessageType, mRoomId, getWrittenMessage());
                } else {
                    ChatSendMessageUtil messageUtil = new ChatSendMessageUtil().newBuilder(chatType, finalMessageType, mRoomId).message(getWrittenMessage());
                    if (finalMessageType == CONTACT) {
                        messageUtil.contact(finalMessageInfo.userInfo.firstName, finalMessageInfo.userInfo.lastName, finalMessageInfo.userInfo.phone);
                    }
                    messageUtil.sendMessage(Long.toString(finalMessageId));
                }

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomMessage.getRoomId()).findFirst();
                if (realmRoom != null) {
                    realmRoom.setLastMessage(roomMessage);
                    //realmRoom.setUpdatedTime(roomMessage.getUpdateOrCreateTime());
                }
            }
        });

        realm.close();
        if (finalMessageType == CONTACT) {
            mAdapter.add(new ContactItem(chatType, this).setMessage(messageInfo));
        }

        if (mReplayLayout != null && userTriesReplay()) {
            mReplayLayout.setTag(null);
            mReplayLayout.setVisibility(View.GONE);
        }

        scrollToEnd();
    }

    private boolean isMessageWrote() {
        return !getWrittenMessage().isEmpty();
    }

    private String getWrittenMessage() {
        return edtChat.getText().toString().trim();
    }

    private void inflateReplayLayoutIntoStub(StructMessageInfo chatItem) {
        if (findViewById(R.id.replayLayoutAboveEditText) == null) {
            ViewStubCompat stubView = (ViewStubCompat) findViewById(R.id.replayLayoutStub);
            stubView.setInflatedId(R.id.replayLayoutAboveEditText);
            stubView.setLayoutResource(R.layout.layout_chat_reply);
            stubView.inflate();

            inflateReplayLayoutIntoStub(chatItem);
        } else {
            mReplayLayout = (LinearLayout) findViewById(R.id.replayLayoutAboveEditText);
            mReplayLayout.setVisibility(View.VISIBLE);
            TextView replayTo = (TextView) mReplayLayout.findViewById(R.id.replayTo);
            TextView replayFrom = (TextView) mReplayLayout.findViewById(replyFrom);
            ImageView thumbnail = (ImageView) mReplayLayout.findViewById(R.id.thumbnail);
            ImageView closeReplay = (ImageView) mReplayLayout.findViewById(R.id.cancelIcon);
            closeReplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mReplayLayout != null) {
                        mReplayLayout.setTag(null);
                        mReplayLayout.setVisibility(View.GONE);
                    }
                }
            });
            Realm realm = Realm.getDefaultInstance();
            thumbnail.setVisibility(View.VISIBLE);
            if (chatItem.forwardedFrom != null) {
                AppUtils.rightFileThumbnailIcon(thumbnail, chatItem.forwardedFrom.getMessageType(), chatItem.forwardedFrom);
                replayTo.setText(chatItem.forwardedFrom.getMessage());
            } else {
                RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(chatItem.messageID)).findFirst();
                AppUtils.rightFileThumbnailIcon(thumbnail, chatItem.messageType, message);
                replayTo.setText(chatItem.messageText);
            }
            if (chatType == CHANNEL) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, chatItem.roomId).findFirst();
                if (realmRoom != null) {
                    replayFrom.setText(realmRoom.getTitle());
                }
            } else {
                RealmRegisteredInfo userInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, Long.parseLong(chatItem.senderID)).findFirst();
                if (userInfo != null) {
                    replayFrom.setText(userInfo.getDisplayName());
                }
            }

            realm.close();
            // I set tag to retrieve it later when sending message
            mReplayLayout.setTag(chatItem);
        }
    }

    private Intent makeIntentForForwardMessages(ArrayList<Parcelable> messageInfos) {
        Intent intent = new Intent(ActivityChat.this, ActivitySelectChat.class);
        intent.putParcelableArrayListExtra(ActivitySelectChat.ARG_FORWARD_MESSAGE, messageInfos);

        return intent;
    }

    private Intent makeIntentForForwardMessages(StructMessageInfo messageInfos) {
        return makeIntentForForwardMessages(new ArrayList<>(Arrays.asList(Parcels.wrap(messageInfos))));
    }

    private void replay(StructMessageInfo item) {
        if (mAdapter != null) {
            Set<AbstractMessage> messages = mAdapter.getSelectedItems();
            // replay works if only one message selected
            inflateReplayLayoutIntoStub(item == null ? messages.iterator().next().mMessage : item);

            ll_AppBarSelected.setVisibility(View.GONE);
            findViewById(R.id.ac_green_line).setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.VISIBLE);

            mAdapter.deselect();
        }
    }

    private void initAppbarSelected() {

        ll_AppBarSelected = (LinearLayout) findViewById(R.id.chl_ll_appbar_selelected);

        RippleView rippleCloseAppBarSelected = (RippleView) findViewById(R.id.chl_ripple_close_layout);
        rippleCloseAppBarSelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                mAdapter.deselect();
                toolbar.setVisibility(View.VISIBLE);
                ll_AppBarSelected.setVisibility(View.GONE);
                // gone replay layout
                if (mReplayLayout != null) {
                    mReplayLayout.setVisibility(View.GONE);
                }
            }
        });

        btnReplaySelected = (MaterialDesignTextView) findViewById(R.id.chl_btn_replay_selected);
        RippleView rippleReplaySelected = (RippleView) findViewById(R.id.chl_ripple_replay_selected);

        if (chatType == CHANNEL) {
            if (channelRole == ChannelChatRole.MEMBER) {
                btnReplaySelected.setVisibility(View.GONE);
            }
        } else {
            btnReplaySelected.setVisibility(View.VISIBLE);
        }
        rippleReplaySelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (!mAdapter.getSelectedItems().isEmpty() && mAdapter.getSelectedItems().size() == 1) {
                    replay(mAdapter.getSelectedItems().iterator().next().mMessage);
                }
            }
        });
        RippleView rippleCopySelected = (RippleView) findViewById(R.id.chl_ripple_copy_selected);
        rippleCopySelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                copySelectedItemTextToClipboard();

            }
        });
        RippleView rippleForwardSelected = (RippleView) findViewById(R.id.chl_ripple_forward_selected);
        rippleForwardSelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                // forward selected messages to room list for selecting room
                if (mAdapter != null && mAdapter.getSelectedItems().size() > 0) {
                    startActivity(makeIntentForForwardMessages(getMessageStructFromSelectedItems()));
                    finish();
                }
            }
        });
        RippleView rippleDeleteSelected = (RippleView) findViewById(R.id.chl_ripple_delete_selected);
        rippleDeleteSelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                final ArrayList<Long> list = new ArrayList<Long>();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for (final AbstractMessage messageID : mAdapter.getSelectedItems()) {
                            Long messageId = parseLong(messageID.mMessage.messageID);
                            list.add(messageId);

                            // remove deleted message from adapter
                            mAdapter.removeMessage(messageId);

                            // remove tag from edtChat if the message has deleted
                            if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                                if (messageID.mMessage.messageID.equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
                                    edtChat.setTag(null);
                                }
                            }

                        }
                    }
                });

                deleteSelectedMessages(mRoomId, list, chatType);


                int size = mAdapter.getItemCount();
                for (int i = 0; i < size; i++) {

                    if (mAdapter.getItem(i) instanceof TimeItem) {
                        if (i < size - 1) {
                            if (mAdapter.getItem(i + 1) instanceof TimeItem) {
                                mAdapter.remove(i);
                            }
                        } else {
                            mAdapter.remove(i);
                        }
                    }
                }
            }
        });
        txtNumberOfSelected = (TextView) findViewById(R.id.chl_txt_number_of_selected);

        if (chatType == CHANNEL && channelRole == ChannelChatRole.MEMBER) {
            initLayoutChannelFooter();
        }
    }

    public void copySelectedItemTextToClipboard() {

        boolean showToast = false;

        for (AbstractMessage _message : mAdapter.getSelectedItems()) {

            String text = _message.mMessage.forwardedFrom != null ? _message.mMessage.forwardedFrom.getMessage() : _message.mMessage.messageText;

            if (text == null || text.length() == 0) continue;

            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }

        if (showToast) Toast.makeText(G.context, R.string.text_copied, Toast.LENGTH_SHORT).show();

        mAdapter.deselect();
        toolbar.setVisibility(View.VISIBLE);
        ll_AppBarSelected.setVisibility(View.GONE);
        findViewById(R.id.ac_green_line).setVisibility(View.VISIBLE);
        // gone replay layout
        if (mReplayLayout != null) {
            mReplayLayout.setVisibility(View.GONE);
        }
    }



    public static void deleteSelectedMessages(final long RoomId, final ArrayList<Long> list, final ProtoGlobal.Room.Type chatType) {

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // get offline delete list , add new deleted list and update in
                // client condition , then send request for delete message to server
                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, RoomId).findFirst();

                for (final Long messageId : list) {
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                    if (roomMessage != null) {
                        roomMessage.setDeleted(true);
                    }

                    RealmOfflineDelete realmOfflineDelete = realm.createObject(RealmOfflineDelete.class, SUID.id().get());
                    realmOfflineDelete.setOfflineDelete(messageId);

                    realmClientCondition.getOfflineDeleted().add(realmOfflineDelete);

                    if (chatType == GROUP) {
                        new RequestGroupDeleteMessage().groupDeleteMessage(RoomId, messageId);
                    } else if (chatType == CHAT) {
                        new RequestChatDeleteMessage().chatDeleteMessage(RoomId, messageId);
                    } else if (chatType == CHANNEL) {
                        new RequestChannelDeleteMessage().channelDeleteMessage(RoomId, messageId);
                    }
                }
            }
        });

        realm.close();
    }


    private ArrayList<Parcelable> getMessageStructFromSelectedItems() {
        ArrayList<Parcelable> messageInfos = new ArrayList<>(mAdapter.getSelectedItems().size());
        for (AbstractMessage item : mAdapter.getSelectedItems()) {
            messageInfos.add(Parcels.wrap(item.mMessage));
        }
        return messageInfos;
    }

    private void initLayoutChannelFooter() {

        LinearLayout layoutAttach = (LinearLayout) findViewById(R.id.chl_ll_attach);
        RelativeLayout layoutChannelFooter = (RelativeLayout) findViewById(R.id.chl_ll_channel_footer);

        layoutAttach.setVisibility(View.GONE);
        layoutChannelFooter.setVisibility(View.VISIBLE);

        btnUp = (TextView) findViewById(R.id.chl_btn_up);
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = recyclerView.getAdapter().getItemCount();
                if (position > 0) recyclerView.scrollToPosition(0);
            }
        });

        btnDown = (TextView) findViewById(R.id.chl_btn_down);
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = recyclerView.getAdapter().getItemCount();
                if (position > 0) recyclerView.scrollToPosition(position - 1);
            }
        });

        txtChannelMute = (TextView) findViewById(R.id.chl_txt_mute_channel);
        txtChannelMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onSelectRoomMenu("txtMuteNotification", (int) mRoomId);

            }
        });

        if (isMuteNotification) {
            txtChannelMute.setText(R.string.unmute);
        } else {
            txtChannelMute.setText(R.string.mute);
        }
    }


    private void setUpEmojiPopup() {
        emojiPopup = io.github.meness.emoji.EmojiPopup.Builder.fromRootView(findViewById(ac_ll_parent)).setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
            @Override
            public void onEmojiBackspaceClicked(final View v) {

            }
        }).setOnEmojiClickedListener(new OnEmojiClickedListener() {
            @Override
            public void onEmojiClicked(final Emoji emoji) {

            }
        }).setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
            @Override
            public void onEmojiPopupShown() {
                changeEmojiButtonImageResource(R.string.md_black_keyboard_with_white_keys);
            }
        }).setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
            @Override
            public void onKeyboardOpen(final int keyBoardHeight) {

            }
        }).setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
            @Override
            public void onEmojiPopupDismiss() {
                changeEmojiButtonImageResource(R.string.md_emoticon_with_happy_face);
            }
        }).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                emojiPopup.dismiss();
            }
        }).build(edtChat);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //checkIfOrientationChanged(newConfig);
    }

    //private void checkIfOrientationChanged(Configuration configuration) {
    //    if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
    //        getWindow().setBackgroundDrawableResource(R.drawable.newbg);
    //    }
    //}


    private boolean topMore; // more message exist in local for load in top direction
    private boolean bottomMore;  // more message exist in local for load in bottom direction
    private boolean isWaitingForHistory; // client send request for getHistory, avoid for send request again
    private long gapMessageId; // messageId that maybe lost in local
    private long reachMessageId; // messageId that will be checked after getHistory for detect reached to that or no
    //TODO [Saeed Mozaffari] [2017-03-01 7:28 PM] - check for use this field
    private boolean allowGetHistory = true; // after insuring for get end of message from server set this false. (set false in history error maybe was wrong , because maybe this was for another error not end  of message, (hint: can check error code for end of message from history))

    private ArrayList<StructMessageInfo> getLocalMessages() {
        Realm realm = Realm.getDefaultInstance();

        /**
         * get message in first enter to chat
         */
        RealmResults<RealmRoomMessage> results = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).notEqualTo(RealmRoomMessageFields.CREATE_TIME, 0).equalTo(RealmRoomMessageFields.DELETED, false).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).findAllSorted(RealmRoomMessageFields.CREATE_TIME, Sort.DESCENDING);
        ArrayList<StructMessageInfo> messageInfos = new ArrayList<>();

        /**
         * detect gap exist in this room or not
         * (hint : if gapMessageId==0 means that gap not exist)
         * if gapMessageId exist, not compute again
         */
        if (gapMessageId == 0 && results.size() > 0) {
            Object[] objects = MessageLoader.gapExist(mRoomId, results.first().getMessageId(), ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP);
            gapMessageId = (long) objects[0];
            reachMessageId = (long) objects[1];
        }

        if (results.size() > 0) {

            Object[] object = MessageLoader.getLocalMessage(mRoomId, results.first().getMessageId(), gapMessageId, 10, true, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP);
            messageInfos = (ArrayList<StructMessageInfo>) object[0];
            topMore = (boolean) object[1];

            /**
             * if gap is exist ,check that reached to gap or not and if
             * reached send request to server for clientGetRoomHistory
             */
            if (gapMessageId > 0) {
                boolean hasSpaceToGap = (boolean) object[2];
                if (!hasSpaceToGap) {

                    long oldMessageId = 0;
                    if (messageInfos.size() > 0) {
                        oldMessageId = Long.parseLong(messageInfos.get(messageInfos.size() - 1).messageID);
                    }
                    /**
                     * send request to server for clientGetRoomHistory
                     */
                    getOnlineMessage(oldMessageId);
                }
            } else {
                if (!topMore) {
                    if (messageInfos.size() > 0) {
                        getOnlineMessage(Long.parseLong(messageInfos.get(messageInfos.size() - 1).messageID));
                    } else {
                        getOnlineMessage(0);
                    }
                }
            }

        } else {
            /**
             * send request to server for get message
             */
            getOnlineMessage(0);
        }

        /**
         * make scrollListener for detect change in scroll and load more in chat
         */
        scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = (recyclerView.getLayoutManager()).getChildCount();
                int totalItemCount = (recyclerView.getLayoutManager()).getItemCount();
                int firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if (firstVisiblePosition < 15) {

                    if (topMore) {
                        /**
                         * get first item in view (hint : time item should be ignore)
                         */
                        for (AbstractMessage message : mAdapter.getAdapterItems()) {
                            if (message != null && message.mMessage != null && (!message.mMessage.senderID.equals("-1") || message.mMessage.messageType == LOG) && message.mMessage.messageID != null) {
                                Object[] object = getLocalMessage(mRoomId, Long.parseLong(message.mMessage.messageID), gapMessageId, 10, false, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP);
                                topMore = (boolean) object[1];
                                ArrayList<StructMessageInfo> structMessageInfos = (ArrayList<StructMessageInfo>) object[0];
                                switchAddItem(structMessageInfos, true);

                                /**
                                 * if gap is exist ,check that reached to gap or not and if
                                 * reached send request to server for clientGetRoomHistory
                                 */
                                if (gapMessageId > 0) {
                                    boolean hasSpaceToGap = (boolean) object[2];
                                    if (!hasSpaceToGap) {
                                        /**
                                         * send request to server for clientGetRoomHistory
                                         */

                                        if (structMessageInfos.size() > 0) {
                                            long oldMessageId = Long.parseLong(structMessageInfos.get(structMessageInfos.size() - 1).messageID);
                                            getOnlineMessage(oldMessageId);
                                        }
                                    }
                                }

                                break;
                            }
                        }
                    } else if (gapMessageId > 0) {
                        for (AbstractMessage message : mAdapter.getAdapterItems()) {
                            if (message != null && message.mMessage != null && (!message.mMessage.senderID.equals("-1") || message.mMessage.messageType == LOG) && message.mMessage.messageID != null) {
                                /**
                                 * detect old messageId that should get history from server with that
                                 */
                                getOnlineMessage(Long.parseLong(message.mMessage.messageID));
                            }
                        }
                    } else {

                        for (AbstractMessage message : mAdapter.getAdapterItems()) {
                            if (message != null && message.mMessage != null && (!message.mMessage.senderID.equals("-1") || message.mMessage.messageType == LOG) && message.mMessage.messageID != null) {
                                if (!topMore && allowGetHistory) {
                                    getOnlineMessage(Long.parseLong(message.mMessage.messageID));
                                }
                            }
                        }

                    }

                } else {

                    if (firstVisiblePosition + visibleItemCount >= (totalItemCount - 15)) {

                        if (bottomMore) {
                            for (int i = (mAdapter.getAdapterItemCount() - 1); i >= 0; i--) {
                                AbstractMessage message = mAdapter.getAdapterItem(i);
                                if (message != null && message.mMessage != null && (!message.mMessage.senderID.equals("-1") || message.mMessage.messageType == LOG) && message.mMessage.messageID != null) {
                                    Object[] object = getLocalMessage(mRoomId, Long.parseLong(message.mMessage.messageID), gapMessageId, 10, false, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.DOWN);
                                    bottomMore = (boolean) object[1];
                                    ArrayList<StructMessageInfo> structMessageInfos = (ArrayList<StructMessageInfo>) object[0];
                                    switchAddItem(structMessageInfos, false);

                                    /**
                                     * if gap is exist ,check that reached to gap or not and if
                                     * reached send request to server for clientGetRoomHistory
                                     */
                                    if (gapMessageId > 0) {
                                        boolean hasGap = (boolean) object[2];
                                        if (!hasGap) {
                                            /**
                                             * send request to server for clientGetRoomHistory
                                             */

                                            long oldMessageId = 0;
                                            if (structMessageInfos.size() > 0) {
                                                oldMessageId = Long.parseLong(structMessageInfos.get(structMessageInfos.size() - 1).messageID);
                                            }
                                            getOnlineMessage(oldMessageId);
                                        }
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
        realm.close();

        if (prgWaiting != null) {
            prgWaiting.setVisibility(View.GONE);
        }

        return messageInfos;
    }

    /**
     * get message history from server
     *
     * @param oldMessageId if set oldMessageId=0 messages will be get from latest message that exist in server
     */
    private void getOnlineMessage(long oldMessageId) {
        if (!isWaitingForHistory) {
            isWaitingForHistory = true;

            /**
             * show progress when start for get history from server
             */
            progressItem(SHOW, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP);

            MessageLoader.getOnlineMessage(mRoomId, oldMessageId, reachMessageId, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP, new OnMessageReceive() {
                @Override
                public void onMessage(final long roomId, long startMessageId, long endMessageId, boolean gapReached) {

                    /**
                     * hide progress received history
                     */
                    progressItem(HIDE, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP);

                    Realm realm = Realm.getDefaultInstance();

                    RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, endMessageId).between(RealmRoomMessageFields.MESSAGE_ID, startMessageId, endMessageId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);

                    /**
                     * send seen status to server when get message from server
                     */
                    for (int i = 0; i < realmRoomMessages.size(); i++) {
                        G.chatUpdateStatusUtil.sendUpdateStatus(chatType, roomId, realmRoomMessages.get(i).getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                    }

                    isWaitingForHistory = false;
                    /**
                     * when reached to gap set gapMessageId = 0 , do this action
                     * means that gap not exist (need this value for future get message)
                     * set topMore true for allow that get message from local after
                     * that gap reached
                     */
                    if (gapReached) {
                        gapMessageId = 0;
                        reachMessageId = 0;
                        topMore = true;

                        /**
                         * calculate that exist any gap again or not
                         */
                        if (realmRoomMessages.size() > 0) {
                            Object[] objects = MessageLoader.gapExist(mRoomId, startMessageId, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP);
                            gapMessageId = (long) objects[0];
                            reachMessageId = (long) objects[1];
                        }
                    }

                    final ArrayList<StructMessageInfo> structMessageInfos = new ArrayList<>();
                    //Collections.sort(realmRoomMessages, SortMessages.DESC);
                    for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                        structMessageInfos.add(StructMessageInfo.convert(realmRoomMessage));
                    }
                    switchAddItem(structMessageInfos, true);

                    realm.close();
                }

                @Override
                public void onError(int majorCode, int minorCode) {

                    /**
                     * hide progress if have any error
                     */
                    progressItem(HIDE, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP);
                    //TODO [Saeed Mozaffari] [2017-03-06 9:50 AM] - for avoid from 'Inconsistency detected. Invalid item position' error i set notifyDataSetChanged. Find Solution And Clear it!!!
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            recyclerView.removeOnScrollListener(scrollListener);
                        }
                    });

                    isWaitingForHistory = false;
                    allowGetHistory = false;
                    if (majorCode == 5) {
                        //TODO [Saeed Mozaffari] [2017-02-28 3:56 PM] - retry for get message after timeout
                    }
                }
            });
        }
    }

    /**
     * manage progress state in adapter
     *
     * @param progressState SHOW or HIDE state detect with enum
     * @param direction define direction for show progress in UP or DOWN
     */
    private void progressItem(final ProgressState progressState, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int progressIndex = 0;
                if (direction == ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.DOWN) {
                    // direction down not tested yet
                    progressIndex = mAdapter.getAdapterItemCount() - 1;
                }
                if (progressState == SHOW) {
                    if ((mAdapter.getAdapterItemCount() > 0) && !(mAdapter.getAdapterItem(progressIndex) instanceof ProgressWaiting)) {
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.add(0, new ProgressWaiting(ActivityChat.this).withIdentifier(SUID.id().get()));
                            }
                        });
                    }
                } else {
                    /**
                     * i do this action with delay because sometimes instance wasn't successful
                     * for detect progress so client need delay for detect this instance
                     */
                    if ((mAdapter.getItemCount() > 0) && (mAdapter.getAdapterItem(progressIndex) instanceof ProgressWaiting)) {
                        mAdapter.remove(progressIndex);
                    } else {
                        final int index = progressIndex;
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if ((mAdapter.getItemCount() > 0) && (mAdapter.getAdapterItem(index) instanceof ProgressWaiting)) {
                                    mAdapter.remove(index);
                                }
                            }
                        }, 500);
                    }
                }
            }
        });
    }

    @Override
    public void onSenderAvatarClick(View view, StructMessageInfo messageInfo, int position) {
        Intent intent = new Intent(G.context, ActivityContactsProfile.class);
        intent.putExtra("peerId", parseLong(messageInfo.senderID));
        intent.putExtra("RoomId", mRoomId);
        intent.putExtra("enterFrom", GROUP.toString());
        startActivity(intent);
    }

    private void showImage(final StructMessageInfo messageInfo) {

        // for gone app bar
        FragmentShowImage.appBarLayout = appBarLayout;

        String selectedFileToken = messageInfo.forwardedFrom != null ? messageInfo.forwardedFrom.getAttachment().getToken() : messageInfo.attachment.token;

        android.app.Fragment fragment = FragmentShowImage.newInstance();
        Bundle bundle = new Bundle();
        bundle.putLong("RoomId", mRoomId);
        bundle.putString("SelectedImage", selectedFileToken);
        fragment.setArguments(bundle);

        getFragmentManager().beginTransaction().replace(R.id.ac_ll_parent, fragment, "ShowImageMessage").commit();


    }

    @Override
    public void onChatClearMessage(long roomId, long clearId, ProtoResponse.Response response) {

        boolean clearMessage = false;

        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
        for (final RealmRoomMessage realmRoomMessage : realmRoomMessages) {
            if (!clearMessage && realmRoomMessage.getMessageId() == clearId) {
                clearMessage = true;
            }

            if (clearMessage) {
                final long messageId = realmRoomMessage.getMessageId();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realmRoomMessage.deleteFromRealm();
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // remove deleted message from adapter

                        mAdapter.removeMessage(messageId);

                        // remove tag from edtChat if the message has deleted
                        if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                            if (Long.toString(messageId).equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
                                edtChat.setTag(null);
                            }
                        }
                    }
                });
            }
        }
        realm.close();
    }

    private void scrollToEnd() {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
            }
        }, 300);
    }

    @Override
    public void onChatUpdateStatus(long roomId, final long messageId, final ProtoGlobal.RoomMessageStatus status, long statusVersion) {

        // I'm in the room
        if (mRoomId == roomId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // so update the message status ina adapter
                    mAdapter.updateMessageStatus(messageId, status);
                    Log.i(ActivityChat.class.getSimpleName(), status.toString());
                }
            });
        }
    }

    @Override
    public void onChatMessageSelectionChanged(int selectedCount, Set<AbstractMessage> selectedItems) {
        //   Toast.makeText(ActivityChat.this, "selected: " + Integer.toString(selectedCount), Toast.LENGTH_SHORT).show();
        if (selectedCount > 0) {
            toolbar.setVisibility(View.GONE);

            txtNumberOfSelected.setText(Integer.toString(selectedCount));

            if (selectedCount > 1) {
                btnReplaySelected.setVisibility(View.INVISIBLE);
            } else {

                if (chatType == CHANNEL) {
                    if (channelRole == ChannelChatRole.MEMBER) {
                        btnReplaySelected.setVisibility(View.INVISIBLE);
                    }
                } else {
                    btnReplaySelected.setVisibility(View.VISIBLE);
                }
            }

            ll_AppBarSelected.setVisibility(View.VISIBLE);
            findViewById(R.id.ac_green_line).setVisibility(View.GONE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
            ll_AppBarSelected.setVisibility(View.GONE);
            findViewById(R.id.ac_green_line).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPreChatMessageRemove(final StructMessageInfo messageInfo, int position) {
        if (mAdapter.getAdapterItemCount() > 1 && position == mAdapter.getAdapterItemCount() - 1) {
            // if was last message removed
            // update room last message
            final Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();

                    if (mAdapter.getAdapterItemCount() > 0) {
                        AbstractMessage lastMessageBeforeDeleted = mAdapter.getAdapterItem(mAdapter.getAdapterItemCount() - 1);
                        if (lastMessageBeforeDeleted != null) {
                            RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(lastMessageBeforeDeleted.mMessage.messageID)).findFirst();
                            if (realmRoom != null && realmRoomMessage != null) {
                                realmRoom.setLastMessage(realmRoomMessage);
                                realmRoom.setUpdatedTime(realmRoomMessage.getUpdateOrCreateTime() / 1000);
                            }
                        }
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    realm.close();
                }
            });
        }
    }

    @Override
    public void onMessageUpdate(long roomId, final long messageId, final ProtoGlobal.RoomMessageStatus status, final String identity, ProtoGlobal.RoomMessage roomMessage) {
        // I'm in the room
        if (roomId == mRoomId) {
            // update message status in telegram
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.updateMessageIdAndStatus(messageId, identity, status);
                }
            });
        }
    }

    //private void addTimeToList(Long messageId) {
    //
    //    isNeedAddTime = false;
    //
    //    String timeString = getTimeSettingMessage(TimeUtils.currentLocalTime());
    //    if (!TextUtils.isEmpty(timeString)) {
    //
    //        RealmRoomMessage timeMessage = new RealmRoomMessage();
    //        timeMessage.setMessageId(messageId);
    //        // -1 means time message
    //        timeMessage.setUserId(-1);
    //        timeMessage.setMessage(timeString);
    //        timeMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
    //        switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(timeMessage))), false);
    //    }
    //}

    @Override
    public void onMessageReceive(final long roomId, String message, ProtoGlobal.RoomMessageType messageType, final ProtoGlobal.RoomMessage roomMessage, final ProtoGlobal.Room.Type roomType) {
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Realm realm = Realm.getDefaultInstance();
                        final RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, roomMessage.getMessageId()).findFirst();

                        if (realmRoomMessage != null) {
                            if (roomMessage.getAuthor().getUser() != null) {
                                if (roomMessage.getAuthor().getUser().getUserId() != G.userId) {
                                    // I'm in the room
                                    if (roomId == mRoomId) {
                                        // I'm in the room, so unread messages count is 0. it means, I read all messages
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                                                if (room != null) {
                                                    room.setUnreadCount(0);
                                                }
                                            }
                                        });

                                        // when user receive message, I send update status as SENT to the message sender
                                        // but imagine user is not in the room (or he is in another room) and received
                                        // some messages
                                        // when came back to the room with new messages, I make new update status request
                                        // as SEEN to
                                        // the message sender
                                        //Start ClientCondition OfflineSeen
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, mRoomId).findFirst();

                                                if (realmRoomMessage != null) {
                                                    if (!realmRoomMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SEEN.toString())) {
                                                        realmRoomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SEEN.toString());

                                                        RealmOfflineSeen realmOfflineSeen = realm.createObject(RealmOfflineSeen.class, SUID.id().get());
                                                        realmOfflineSeen.setOfflineSeen(realmRoomMessage.getMessageId());
                                                        realm.copyToRealmOrUpdate(realmOfflineSeen);
                                                        realmClientCondition.getOfflineSeen().add(realmOfflineSeen);
                                                    }
                                                }

                                                // make update status to message sender that i've read his message
                                                if (chatType == CHAT) {
                                                    G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                                                } else if (chatType == GROUP && (roomMessage.getStatus() == ProtoGlobal.RoomMessageStatus.SENT || roomMessage.getStatus() == ProtoGlobal.RoomMessageStatus.DELIVERED)) {
                                                    G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                                                }
                                            }
                                        });

                                        //// if need to add time befor insert new message
                                        //if (isNeedAddTime) {
                                        //    addTimeToList(SUID.id().get());
                                        //}
                                        switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(realmRoomMessage))), false);
                                        setBtnDownVisible();
                                    } else {
                                        // user has received the message, so I make a new delivered update status request
                                        if (roomType == CHAT) {
                                            G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
                                        } else if (roomType == GROUP && roomMessage.getStatus() == ProtoGlobal.RoomMessageStatus.SENT) {
                                            G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
                                        }
                                    }
                                } else {

                                    if (roomId == mRoomId) {
                                        // I'm sender . but another account sent this message and i received it.
                                        switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(realmRoomMessage))), false);
                                        setBtnDownVisible();
                                    }
                                }
                            }
                        }

                        realm.close();
                    }
                });
            }
        }, 400);
    }

    private void setBtnDownVisible() {

        LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();

        if (llm.findLastVisibleItemPosition() + 5 > recyclerView.getAdapter().getItemCount()) {
            scrollToEnd();
        } else {
            countNewMessage++;
            llScrollNavigate.setVisibility(View.VISIBLE);
            txtNewUnreadMessage.setText(countNewMessage + "");
            txtNewUnreadMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMessageFailed(long roomId, RealmRoomMessage message) {
        if (roomId == mRoomId) {
            mAdapter.updateMessageStatus(message.getMessageId(), ProtoGlobal.RoomMessageStatus.FAILED);
        }
    }


    @Override
    public void onFileTimeOut(String identity) {
        //empty
    }

    private RealmRoomMessage voiceLastMessage = null;

    @Override
    public void onVoiceRecordDone(final String savedPath) {
        Realm realm = Realm.getDefaultInstance();
        final long messageId = SUID.id().get();
        final long updateTime = TimeUtils.currentLocalTime();
        final long senderID = realm.where(RealmUserInfo.class).findFirst().getUserId();
        final long duration = AndroidUtils.getAudioDuration(getApplicationContext(), savedPath) / 1000;

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, messageId);
                roomMessage.setMessageType(ProtoGlobal.RoomMessageType.VOICE);
                roomMessage.setMessage(getWrittenMessage());
                roomMessage.setRoomId(mRoomId);
                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                roomMessage.setAttachment(messageId, savedPath, 0, 0, 0, null, duration, LocalFileType.FILE);
                roomMessage.setUserId(senderID);
                roomMessage.setCreateTime(updateTime);
                voiceLastMessage = roomMessage;

                // TODO: 9/26/2016 [Alireza Eskandarpour Shoferi] user may wants to send a file
                // in response to a message as replay, so after server done creating replay and
                // forward options, modify this section and sending message as well.
            }
        });

        new UploadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, savedPath, messageId, ProtoGlobal.RoomMessageType.VOICE, mRoomId, getWrittenMessage());

        StructMessageInfo messageInfo;

        if (userTriesReplay()) {
            messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.
                    RoomMessageType.VOICE, MyType.SendType.send, null, savedPath, updateTime, parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
        } else {
            if (isMessageWrote()) {
                messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.
                        RoomMessageType.VOICE, MyType.SendType.send, null, savedPath, updateTime);
            } else {
                messageInfo = new StructMessageInfo(mRoomId, Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.
                        RoomMessageType.VOICE, MyType.SendType.send, null, savedPath, updateTime);
            }
        }

        messageInfo.attachment.duration = duration;

        StructChannelExtra structChannelExtra = new StructChannelExtra();
        structChannelExtra.messageId = messageId;
        structChannelExtra.thumbsUp = "0";
        structChannelExtra.thumbsDown = "0";
        structChannelExtra.viewsLabel = "1";
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (voiceLastMessage != null) {
                    realmRoom.setLastMessage(voiceLastMessage);
                }
            }
        });
        if (realmRoom != null && realmRoom.getChannelRoom() != null && realmRoom.getChannelRoom().isSignature()) {
            structChannelExtra.signature = realm.where(RealmUserInfo.class).findFirst().getUserInfo().getDisplayName();
        } else {
            structChannelExtra.signature = "";
        }
        messageInfo.channelExtra = structChannelExtra;
        mAdapter.add(new VoiceItem(chatType, this).setMessage(messageInfo));
        realm.close();
        scrollToEnd();

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (mReplayLayout != null) {
                    mReplayLayout.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onVoiceRecordCancel() {

    }

    @Override
    public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {

        setAvatar();

        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, user.getId()).findFirst();
                if (realmRegisteredInfo != null) {
                    mAdapter.updateChatAvatar(user.getId(), realmRegisteredInfo);
                }
                realm.close();
            }
        });*/
    }

    @Override
    public void onUserInfoTimeOut() {

    }

    @Override
    public void onUserInfoError(int majorCode, int minorCode) {

    }
    //
    //@Override
    //public void onGetRoomHistory(final long roomId, final List<ProtoGlobal.RoomMessage> messages, final int count) {
    //    // I'm in the room
    //
    //    if (roomId == mRoomId) {
    //
    //        isSendingRequestClientGetHistory = false;
    //
    //
    //        for (int i = 0; i < messages.size(); i++) {
    //            G.chatUpdateStatusUtil.sendUpdateStatus(chatType, roomId, messages.get(i).getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
    //        }
    //
    //        runOnUiThread(new Runnable() {
    //            @Override
    //            public void run() {
    //
    //                if (mAdapter.getItemCount() > 0) if (mAdapter.getAdapterItem(0) instanceof ProgressWaiting) mAdapter.remove(0);
    //
    //                if (count < 1) {
    //                    isThereAnyMoreItemToLoadFromServer = false;
    //                    return;
    //                }
    //
    //
    //                Realm realm = Realm.getDefaultInstance();
    //                RealmResults<RealmRoomMessage> results = realm.where(RealmRoomMessage.class).notEqualTo(RealmRoomMessageFields.CREATE_TIME, 0).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).equalTo(RealmRoomMessageFields.DELETED, false).findAllSorted(RealmRoomMessageFields.CREATE_TIME);
    //
    //                //if (results.size() > 0) lastMessageId = results.get(0).getMessageId();
    //                lastDateCalendar.clear();
    //
    //                List<RealmRoomMessage> lastResultMessages = new ArrayList<>();
    //
    //                for (RealmRoomMessage message : results) {
    //                    String timeString = getTimeSettingMessage(message.getCreateTime());
    //                    if (!TextUtils.isEmpty(timeString)) {
    //                        RealmRoomMessage timeMessage = new RealmRoomMessage();
    //                        timeMessage.setMessageId(message.getMessageId() - 1L);
    //                        // -1 means time message
    //                        timeMessage.setUserId(-1);
    //                        timeMessage.setMessage(timeString);
    //                        timeMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
    //                        lastResultMessages.add(timeMessage);
    //                    }
    //
    //                    lastResultMessages.add(message);
    //                }
    //
    //                Collections.sort(lastResultMessages, SortMessages.DESC);
    //
    //                if (mAdapter.getItemCount() > 0 && mAdapter.getAdapterItem(0) instanceof TimeItem) mAdapter.remove(0);
    //
    //                String topID = "";
    //                if (mAdapter.getItemCount() > 0) {
    //                    topID = mAdapter.getAdapterItem(0) instanceof UnreadMessage ? mAdapter.getItem(1).mMessage.messageID : mAdapter.getItem(0).mMessage.messageID;
    //                }
    //
    //
    //                for (RealmRoomMessage realmRoomMessage : lastResultMessages) {
    //
    //                    if (realmRoomMessage == null || (topID.compareTo(realmRoomMessage.getMessageId() + "") <= 0 && topID.length() > 0)) {
    //                        continue;
    //                    }
    //                    switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(realmRoomMessage))), true);
    //                }
    //
    //                recyclerView.scrollToPosition(count);
    //
    //
    //                realm.close();
    //
    //
    //
    //            }
    //        });
    //    }
    //}
    //
    //@Override
    //public void onGetRoomHistoryError(int majorCode, int minorCode) {
    //    runOnUiThread(new Runnable() {
    //        @Override
    //        public void run() {
    //            isSendingRequestClientGetHistory = false;
    //            if (mAdapter.getItemCount() > 0) if (mAdapter.getAdapterItem(0) instanceof ProgressWaiting) mAdapter.remove(0);
    //        }
    //    });
    //}

    @Override
    public void onFileUploaded(final FileUploadStructure uploadStructure, final String identity) {

        HelperSetAction.sendCancel(uploadStructure.messageId);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmAttachment.updateToken(uploadStructure.messageId, uploadStructure.token);
                    }
                });
                realm.close();
                mAdapter.updateProgress(parseLong(identity), 100);
                mAdapter.updateToken(uploadStructure.messageId, uploadStructure.token);
            }
        });

        /**
         * this code should exist in under of other codes in this block
         */
        new ChatSendMessageUtil().newBuilder(chatType, uploadStructure.messageType, uploadStructure.roomId).attachment(uploadStructure.token).message(uploadStructure.text).sendMessage(Long.toString(uploadStructure.messageId));
    }

    @Override
    public void onFileUploading(FileUploadStructure uploadStructure, final String identity, final double progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.updateProgress(parseLong(identity), (int) progress);
            }
        });
    }

    @Override
    public void onUploadStarted(final FileUploadStructure struct) {
        Log.i("ZZZ", "onUploadStarted UploadTask 5");
        Realm realm = Realm.getDefaultInstance();
        RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, struct.messageId).findFirst();
        if (roomMessage != null) {
            AbstractMessage message = mAdapter.getItemByFileIdentity(struct.messageId);
            // message doesn't exists
            if (message == null) {
                switchAddItem(new ArrayList<>(Collections.singletonList(StructMessageInfo.convert(roomMessage))), false);
                if (!G.userLogin) {
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            makeFailed(struct.messageId);
                        }
                    }, 200);
                }
            }
        }
        realm.close();
    }

    @Override
    public void onBadDownload(String token) {

    }

    private void onSelectRoomMenu(String message, int item) {
        switch (message) {
            case "txtMuteNotification":
                muteNotification(item);
                break;
            case "txtClearHistory":
                clearHistory(item);
                break;
            case "txtDeleteChat":
                deleteChat(item);
                break;
        }
    }

    private void muteNotification(final int item) {
        Realm realm = Realm.getDefaultInstance();

        isMuteNotification = !isMuteNotification;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, item).findFirst().setMute(isMuteNotification);
            }
        });

        if (isMuteNotification) {
            ((TextView) findViewById(R.id.chl_txt_mute_channel)).setText(R.string.unmute);
            iconMute.setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.chl_txt_mute_channel)).setText(R.string.mute);
            iconMute.setVisibility(View.GONE);
        }



        realm.close();
    }
    //    delete & clear History & mutNotification

    public void clearHistory(long item) {
        final long chatId = item;

        llScrollNavigate.setVisibility(View.GONE);

        // make request for clearing messages
        final Realm realm = Realm.getDefaultInstance();

        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, chatId).findFirstAsync();
        realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
            @Override
            public void onChange(final RealmClientCondition element) {
                if (element.isLoaded() && element.isValid()) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, chatId).findFirst();

                            if (realmRoom.isLoaded() && realmRoom.isValid() && realmRoom.getLastMessage() != null) {
                                element.setClearId(realmRoom.getLastMessage().getMessageId());

                                G.clearMessagesUtil.clearMessages(realmRoom.getType(), chatId, realmRoom.getLastMessage().getMessageId());
                            }

                            RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, chatId).findAll();
                            for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                                if (realmRoomMessage != null) {
                                    // delete chat history message
                                    realmRoomMessage.deleteFromRealm();
                                }
                            }

                            RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, chatId).findFirst();
                            if (room != null) {
                                room.setUnreadCount(0);
                                room.setLastMessage(null);

                            }
                            // finally delete whole chat history
                            realmRoomMessages.deleteAllFromRealm();
                        }
                    });

                    element.removeChangeListeners();

                    G.onClearChatHistory.onClearChatHistory();
                }
                realm.close();
            }
        });
    }

    private void deleteChat(final int itemff) {

        final long chatId = itemff;

        G.onChatDelete = new OnChatDelete() {
            @Override
            public void onChatDelete(long roomId) {

            }

            @Override
            public void onChatDeleteError(int majorCode, int minorCode) {
            }
        };

        final Realm realm = Realm.getDefaultInstance();
        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, chatId).findFirstAsync();
        realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
            @Override
            public void onChange(final RealmClientCondition element) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(final Realm realm) {
                        if (realm.where(RealmOfflineDelete.class).equalTo(RealmOfflineDeleteFields.OFFLINE_DELETE, chatId).findFirst() == null) {
                            RealmOfflineDelete realmOfflineDelete = realm.createObject(RealmOfflineDelete.class, SUID.id().get());
                            realmOfflineDelete.setOfflineDelete(chatId);

                            element.getOfflineDeleted().add(realmOfflineDelete);

                            realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, chatId).findFirst().deleteFromRealm();
                            realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, chatId).findAll().deleteAllFromRealm();

                            new RequestChatDelete().chatDelete(chatId);
                        }
                    }
                });
                element.removeChangeListeners();
                realm.close();
                finish();
            }
        });
    }

    private void showDraftLayout() {
        ll_attach_text.setVisibility(View.VISIBLE);
        layoutAttachBottom.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                layoutAttachBottom.setVisibility(View.GONE);
            }
        }).start();
        imvSendButton.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imvSendButton.clearAnimation();
                        imvSendButton.setVisibility(View.VISIBLE);
                    }
                });

            }
        }).start();
    }

    private void setDraft() {

        if (mReplayLayout != null && mReplayLayout.getVisibility() == View.VISIBLE) {
            StructMessageInfo info = ((StructMessageInfo) mReplayLayout.getTag());
            replyToMessageId = parseLong(info.messageID);
        } else {
            replyToMessageId = 0;
        }

        if (ll_attach_text.getVisibility() == View.VISIBLE) {
            //draftForFile();
        } else {
            final String message = edtChat.getText().toString();
            if (!message.trim().isEmpty() || ((mReplayLayout != null && mReplayLayout.getVisibility() == View.VISIBLE))) {

                hasDraft = true;

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                        if (realmRoom != null) {

                            RealmRoomDraft draft = realm.createObject(RealmRoomDraft.class);
                            draft.setMessage(message);
                            draft.setReplyToMessageId(replyToMessageId);

                            realmRoom.setDraft(draft);

                            if (chatType == CHAT) {
                                new RequestChatUpdateDraft().chatUpdateDraft(mRoomId, message, replyToMessageId);
                            } else if (chatType == GROUP) {
                                new RequestGroupUpdateDraft().groupUpdateDraft(mRoomId, message, replyToMessageId);
                            } else if (chatType == CHANNEL) {
                                new RequestChannelUpdateDraft().channelUpdateDraft(mRoomId, message, replyToMessageId);
                            }
                            if (G.onDraftMessage != null) { // zamani ke mostaghim varede chat beshim bedune vorud be list room ha onDraftMessage null mishe
                                G.onDraftMessage.onDraftMessage(mRoomId, message);
                            }
                        }
                    }
                });
                realm.close();
            } else {
                clearDraftRequest();
            }
        }
    }

    private void getDraft() {
        Realm realm = Realm.getDefaultInstance();

        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();

        if (realmRoom != null) {
            //            if (realmRoom.getDraftFile() != null) {
            //                getDraftFile(realmRoom,realm);
            //            } else {
            RealmRoomDraft draft = realmRoom.getDraft();
            if (draft != null) {
                hasDraft = true;
                edtChat.setText(draft.getMessage());

                if (draft.getReplyToMessageId() != 0) {

                    RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, draft.getReplyToMessageId()).findFirst();
                    if (realmRoomMessage != null) {
                        StructMessageInfo struct = new StructMessageInfo();
                        struct.messageText = realmRoomMessage.getMessage();
                        struct.senderID = realmRoomMessage.getUserId() + "";
                        struct.messageID = draft.getReplyToMessageId() + "";
                        inflateReplayLayoutIntoStub(struct);
                    }
                }
            }
            //            }
        }
        realm.close();

        clearLocalDraft();
    }

    private void clearLocalDraft() {

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.setDraft(null);
                    realmRoom.setDraftFile(null);
                }
            }
        });
        realm.close();
    }

    private void clearDraftRequest() {
        if (hasDraft) {
            hasDraft = false;
            if (chatType == CHAT) {
                new RequestChatUpdateDraft().chatUpdateDraft(mRoomId, "", 0);
            } else if (chatType == GROUP) {
                new RequestGroupUpdateDraft().groupUpdateDraft(mRoomId, "", 0);
            }

            clearLocalDraft();
        }
    }

    private void draftForFile() { // this method not used because we don't have draft for file
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();

                RealmDraftFile realmDraftFile = realm.createObject(RealmDraftFile.class);

                if (AttachFile.request_code_TAKE_PICTURE == latestRequestCode) {
                    realmDraftFile.setUri(null);
                    realmDraftFile.setFilePath(latestFilePath);
                } else {
                    realmDraftFile.setUri(latestUri.toString());
                    realmDraftFile.setFilePath("");
                }

                realmDraftFile.setRequestCode(latestRequestCode);

                if (isMessageWrote()) {

                    hasDraft = true;

                    RealmRoomDraft draft = realm.createObject(RealmRoomDraft.class);
                    draft.setMessage(edtChat.getText().toString());
                    draft.setReplyToMessageId(replyToMessageId);

                    realmRoom.setDraft(draft);

                    if (chatType == CHAT) {
                        new RequestChatUpdateDraft().chatUpdateDraft(mRoomId, edtChat.getText().toString(), replyToMessageId);
                    } else if (chatType == GROUP) {
                        new RequestGroupUpdateDraft().groupUpdateDraft(mRoomId, edtChat.getText().toString(), replyToMessageId);
                    }

                    if (G.onDraftMessage != null) {
                        G.onDraftMessage.onDraftMessage(mRoomId, edtChat.getText().toString());
                    }
                } else {

                    clearDraftRequest();
                    if (G.onDraftMessage != null) {
                        G.onDraftMessage.onDraftMessage(mRoomId, "");
                    }
                }

                realmRoom.setDraftFile(realmDraftFile);
            }
        });
        realm.close();
    }

    private void getDraftFile(RealmRoom realmRoom, Realm realm) { // this method not used because we don't have draft for file
        RealmDraftFile realmDraftFile = realmRoom.getDraftFile();

        latestRequestCode = realmDraftFile.getRequestCode();

        String filePath = "";
        if (AttachFile.request_code_TAKE_PICTURE == latestRequestCode) {
            latestFilePath = realmDraftFile.getFilePath();
            AttachFile.imagePath = latestFilePath;
            latestUri = null;
        } else {
            latestUri = Uri.parse(realmDraftFile.getUri());
            filePath = getFilePathFromUri(latestUri);
            latestFilePath = "";
        }

        //&& new File(latestUri.toString()).exists()
        if ((latestUri != null && new File(filePath).exists()) || (latestFilePath != null && !latestFilePath.isEmpty() && new File(latestFilePath).exists())) {
            showDraftLayout();
            RealmRoomDraft draft = realmRoom.getDraft();

            if (draft != null) {
                hasDraft = true;
                edtChat.setText(draft.getMessage());

                if (draft.getReplyToMessageId() != 0) {
                    RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, draft.getReplyToMessageId()).findFirst();

                    if (realmRoomMessage != null) {

                        StructMessageInfo struct = new StructMessageInfo();
                        struct.messageText = realmRoomMessage.getMessage();

                        replay(struct);
                    }
                }
            }
            setDraftMessage(latestRequestCode);
        }
    }


    @Override
    protected void onDestroy() {
        // room id have to be set to default, otherwise I'm in the room always!
        mRoomId = -1;
        super.onDestroy();
    }

    @Override
    public void onOpenClick(View view, StructMessageInfo message, int pos) {
        ProtoGlobal.RoomMessageType messageType = message.forwardedFrom != null ? message.forwardedFrom.getMessageType() : message.messageType;
        Realm realm = Realm.getDefaultInstance();
        if (messageType == ProtoGlobal.RoomMessageType.IMAGE || messageType == IMAGE_TEXT) {
            showImage(message);
        } else if (messageType == ProtoGlobal.RoomMessageType.FILE || messageType == ProtoGlobal.RoomMessageType.FILE_TEXT || messageType == ProtoGlobal.RoomMessageType.VIDEO || messageType == VIDEO_TEXT) {
            Intent intent = HelperMimeType.appropriateProgram(realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, message.forwardedFrom != null ? message.forwardedFrom.getAttachment().getToken() : message.attachment.token).findFirst().getLocalFilePath());
            if (intent != null) {
                try {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } catch (Exception e) {
                    // to prevent from 'No Activity found to handle Intent'
                    e.printStackTrace();
                }
            }
        }
        realm.close();
    }

    @Override
    public void onDownloadAllEqualCashId(String token, String messageID) {

        for (int i = 0; i < mAdapter.getItemCount(); i++) {

            try {
                AbstractMessage item = mAdapter.getAdapterItem(i);

                if (item.mMessage.hasAttachment()) {
                    if (item.mMessage.getAttachment().token.equals(token) && (!item.mMessage.messageID.equals(messageID))) {
                        mAdapter.notifyItemChanged(i);
                    }
                }


            } catch (Exception e) {
                Log.e("dddddd", "activity chat    onDownloadAllEqualCashid  " + e);
            }
        }
    }

    @Override
    public void onContainerClick(View view, final StructMessageInfo message, int pos) {
        @ArrayRes int itemsRes = 0;
        switch (message.messageType) {
            case TEXT:
                itemsRes = R.array.textMessageDialogItems;
                break;
            case FILE_TEXT:
            case IMAGE_TEXT:
            case VIDEO_TEXT:
            case GIF_TEXT:
                itemsRes = R.array.fileTextMessageDialogItems;
                break;
            case FILE:
            case IMAGE:
            case VIDEO:
            case AUDIO:
            case VOICE:
            case GIF:
                itemsRes = R.array.fileMessageDialogItems;
                break;
            case LOCATION:
            case CONTACT:
            case LOG:
                itemsRes = R.array.otherMessageDialogItems;
                break;
        }

        if (itemsRes != 0) {
            // Arrays.asList returns fixed size, doing like this fixes remove object
            // UnsupportedOperationException exception
            List<String> items = new LinkedList<>(Arrays.asList(getResources().getStringArray(itemsRes)));

            Realm realm = Realm.getDefaultInstance();
            // if user clicked on any message which he wasn't its sender, remove edit mList option
            if (chatType == CHANNEL) {
                if (channelRole == ChannelChatRole.MEMBER) {
                    items.remove(getString(R.string.edit_item_dialog));
                    items.remove(getString(R.string.replay_item_dialog));
                    items.remove(getString(R.string.delete_item_dialog));
                }
                final long senderId = realm.where(RealmUserInfo.class).findFirst().getUserId();
                ChannelChatRole roleSenderMessage = null;
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                RealmList<RealmMember> realmMembers = realmChannelRoom.getMembers();
                for (RealmMember rm : realmMembers) {
                    if (rm.getPeerId() == Long.parseLong(message.senderID)) {
                        roleSenderMessage = ChannelChatRole.valueOf(rm.getRole());
                    }
                }
                if (senderId != Long.parseLong(message.senderID)) {  // if message dose'nt belong to owner
                    if (channelRole == ChannelChatRole.MEMBER) {
                        items.remove(getString(R.string.delete_item_dialog));
                    } else if (channelRole == ChannelChatRole.MODERATOR) {
                        if (roleSenderMessage == ChannelChatRole.MODERATOR || roleSenderMessage == ChannelChatRole.ADMIN || roleSenderMessage == ChannelChatRole.OWNER) {
                            items.remove(getString(R.string.delete_item_dialog));
                        }
                    } else if (channelRole == ChannelChatRole.ADMIN) {
                        if (roleSenderMessage == ChannelChatRole.OWNER || roleSenderMessage == ChannelChatRole.ADMIN) {
                            items.remove(getString(R.string.delete_item_dialog));
                        }
                    }
                    items.remove(getString(R.string.edit_item_dialog));
                }

            } else if (chatType == GROUP) {

                final long senderId = realm.where(RealmUserInfo.class).findFirst().getUserId();

                GroupChatRole roleSenderMessage = null;
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                RealmList<RealmMember> realmMembers = realmGroupRoom.getMembers();
                for (RealmMember rm : realmMembers) {
                    if (rm.getPeerId() == Long.parseLong(message.senderID)) {
                        roleSenderMessage = GroupChatRole.valueOf(rm.getRole());
                    }
                }
                if (senderId != Long.parseLong(message.senderID)) {  // if message dose'nt belong to owner
                    if (groupRole == GroupChatRole.MEMBER) {
                        items.remove(getString(R.string.delete_item_dialog));
                    } else if (groupRole == GroupChatRole.MODERATOR) {
                        if (roleSenderMessage == GroupChatRole.MODERATOR || roleSenderMessage == GroupChatRole.ADMIN || roleSenderMessage == GroupChatRole.OWNER) {
                            items.remove(getString(R.string.delete_item_dialog));
                        }
                    } else if (groupRole == GroupChatRole.ADMIN) {
                        if (roleSenderMessage == GroupChatRole.OWNER || roleSenderMessage == GroupChatRole.ADMIN) {
                            items.remove(getString(R.string.delete_item_dialog));
                        }
                    }
                    items.remove(getString(R.string.edit_item_dialog));
                }
            } else {
                if (!message.senderID.equalsIgnoreCase(Long.toString(realm.where(RealmUserInfo.class).findFirst().getUserId()))) {
                    items.remove(getString(R.string.edit_item_dialog));
                }
            }

            realm.close();

            new MaterialDialog.Builder(this).title(getString(R.string.messages)).negativeText(getString(R.string.cancel)).items(items).itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                    if (text.toString().equalsIgnoreCase(getString(R.string.copy_item_dialog))) {
                        // copy message
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        String _text = message.forwardedFrom != null ? message.forwardedFrom.getMessage() : message.messageText;
                        if (_text != null && _text.length() > 0) {
                            ClipData clip = ClipData.newPlainText("Copied Text", _text);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(G.context, R.string.text_copied, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(G.context, R.string.text_is_empty, Toast.LENGTH_SHORT).show();
                        }

                    } else if (text.toString().equalsIgnoreCase(getString(R.string.delete_item_dialog))) {
                        final Realm realmCondition = Realm.getDefaultInstance();
                        final RealmClientCondition realmClientCondition = realmCondition.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, mRoomId).findFirstAsync();
                        realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
                            @Override
                            public void onChange(final RealmClientCondition element) {
                                realmCondition.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        if (element != null) {
                                            if (realmCondition.where(RealmOfflineDelete.class).equalTo(RealmOfflineDeleteFields.OFFLINE_DELETE, parseLong(message.messageID)).findFirst() == null) {

                                                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, parseLong(message.messageID)).findFirst();
                                                if (roomMessage != null) {
                                                    // delete message from database
                                                    //roomMessage.deleteFromRealm();
                                                    roomMessage.setDeleted(true);
                                                }

                                                RealmOfflineDelete realmOfflineDelete = realmCondition.createObject(RealmOfflineDelete.class, SUID.id().get());
                                                realmOfflineDelete.setOfflineDelete(parseLong(message.messageID));
                                                element.getOfflineDeleted().add(realmOfflineDelete);

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // remove deleted message from adapter
                                                        mAdapter.removeMessage(parseLong(message.messageID));

                                                        // remove tag from edtChat if the
                                                        // message has deleted
                                                        if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                                                            if (Long.toString(parseLong(message.messageID)).equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
                                                                edtChat.setTag(null);
                                                            }
                                                        }
                                                    }
                                                });
                                                // delete message
                                                if (chatType == GROUP) {
                                                    new RequestGroupDeleteMessage().groupDeleteMessage(mRoomId, parseLong(message.messageID));
                                                } else if (chatType == CHAT) {
                                                    new RequestChatDeleteMessage().chatDeleteMessage(mRoomId, parseLong(message.messageID));
                                                } else if (chatType == CHANNEL) {
                                                    new RequestChannelDeleteMessage().channelDeleteMessage(mRoomId, parseLong(message.messageID));
                                                }
                                            }
                                            element.removeChangeListeners();
                                        }
                                    }
                                });

                                realmCondition.close();
                            }
                        });
                    } else if (text.toString().equalsIgnoreCase(getString(R.string.edit_item_dialog))) {
                        // edit message
                        // put message text to EditText
                        if (message.messageText != null && !message.messageText.isEmpty()) {
                            edtChat.setText(message.messageText);
                            edtChat.setSelection(0, edtChat.getText().length());
                            // put message object to edtChat's tag to obtain it later and
                            // found is user trying to edit a message
                            edtChat.setTag(message);
                        }
                    } else if (text.toString().equalsIgnoreCase(getString(R.string.replay_item_dialog))) {
                        replay(message);
                    } else if (text.toString().equalsIgnoreCase(getString(R.string.forward_item_dialog))) {
                        // forward selected messages to room list for selecting room
                        if (mAdapter != null) {
                            finish();
                            startActivity(makeIntentForForwardMessages(message));
                        }
                    } else if (text.toString().equalsIgnoreCase(getString(R.string.share_item_dialog))) {
                        shearedDataToOtherProgram(message);
                    }
                }
            }).show();
        }
    }

    @Override
    public void onUploadCancel(View view, final StructMessageInfo message, final int pos) {

        HelperSetAction.sendCancel(Long.parseLong(message.messageID));

        if (HelperCancelDownloadUpload.cancelUpload(Long.parseLong(message.messageID))) {
            // empty tag if selected message has been set
            if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                if (Long.toString(parseLong(message.messageID)).equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
                    edtChat.setTag(null);
                }
            }

            mAdapter.removeMessage(pos);

            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(message.messageID)).findFirst();
                    if (roomMessage != null) {
                        // delete message from database
                        roomMessage.deleteFromRealm();
                    }
                }
            });
            realm.close();
        }
    }

    @Override
    public void onFailedMessageClick(View view, final StructMessageInfo message, final int pos) {
        final List<StructMessageInfo> failedMessages = mAdapter.getFailedMessages();
        new ResendMessage(this, new IResendMessage() {
            @Override
            public void deleteMessage() {
                mAdapter.remove(pos);
            }

            @Override
            public void resendMessage() {
                mAdapter.updateMessageStatus(parseLong(message.messageID), ProtoGlobal.RoomMessageStatus.SENDING);
            }

            @Override
            public void resendAllMessages() {
                for (StructMessageInfo message : failedMessages) {
                    mAdapter.updateMessageStatus(parseLong(message.messageID), ProtoGlobal.RoomMessageStatus.SENDING);
                }
            }
        }, parseLong(message.messageID), failedMessages);
    }

    @Override
    public void onReplyClick(RealmRoomMessage replyMessage) {
        recyclerView.scrollToPosition(mAdapter.findPositionByMessageId(replyMessage.getMessageId()));
    }

    @Override
    public void onSetAction(final long roomId, final long userId, final ProtoGlobal.ClientAction clientAction) {

        final boolean isCloudRoom = RealmRoom.isCloudRoom(mRoomId);
        if (mRoomId == roomId && (this.userId != userId || (isCloudRoom))) {
            Realm realm = Realm.getDefaultInstance();
            final String action = HelperGetAction.getAction(roomId, chatType, clientAction);

            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                realmRoom.setActionState(action, userId);
            }
            realm.close();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (action != null) {
                        avi.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
                            //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        }
                        txtLastSeen.setText(action);
                    } else if (chatType == CHAT) {
                        if (isCloudRoom) {
                            txtLastSeen.setText(getResources().getString(R.string.chat_with_yourself));
                        } else {
                            if (userStatus != null) {
                                if (userStatus.equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                                    txtLastSeen.setText(LastSeenTimeUtil.computeTime(chatPeerId, userTime, true, false));
                                } else {
                                    txtLastSeen.setText(userStatus);
                                }
                            }
                        }
                        avi.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                            //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        }
                        //txtLastSeen.setText(userStatus);
                    } else if (chatType == GROUP) {
                        avi.setVisibility(View.GONE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                            //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                        }
                        txtLastSeen.setText(groupParticipantsCountLabel + " " + getString(member));
                    }

                    // change english number to persian number
                    if (HelperCalander.isLanguagePersian) txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                }
            });
        }
    }

    @Override
    public void onUserUpdateStatus(long userId, final long time, final String status) {
        if (chatType == CHAT && chatPeerId == userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    userStatus = AppUtils.getStatsForUser(status);
                    setUserStatus(userStatus, time);
                }
            });
        }
    }

    @Override
    public void onLastSeenUpdate(final long userIdR, final String showLastSeen) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (chatType == CHAT && userIdR == chatPeerId && userId != userIdR) { // userId != userIdR means that , this isn't update status for own user
                    txtLastSeen.setText(showLastSeen);
                    avi.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        viewGroupLastSeen.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        //txtLastSeen.setTextDirection(View.TEXT_DIRECTION_LTR);
                    }
                    // change english number to persian number
                    if (HelperCalander.isLanguagePersian) txtLastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(txtLastSeen.getText().toString()));
                }
            }
        });
    }

    public static class UploadTask extends AsyncTask<Object, FileUploadStructure, FileUploadStructure> {
        @Override
        protected FileUploadStructure doInBackground(Object... params) {
            try {
                String filePath = (String) params[0];
                long messageId = (long) params[1];
                ProtoGlobal.RoomMessageType messageType = (ProtoGlobal.RoomMessageType) params[2];
                long roomId = (long) params[3];
                String messageText = (String) params[4];
                File file = new File(filePath);
                String fileName = file.getName();
                long fileSize = file.length();
                FileUploadStructure fileUploadStructure = new FileUploadStructure(fileName, fileSize, filePath, messageId, messageType, roomId);
                fileUploadStructure.openFile(filePath);
                fileUploadStructure.text = messageText;

                byte[] fileHash = AndroidUtils.getFileHashFromPath(filePath);
                fileUploadStructure.setFileHash(fileHash);

                return fileUploadStructure;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(FileUploadStructure result) {
            super.onPostExecute(result);

            if (result != null) {
                if (MessagesAdapter.uploading != null) {
                    MessagesAdapter.uploading.put(result.messageId, 0);
                    G.uploaderUtil.startUploading(result, Long.toString(result.messageId));
                    HelperSetAction.setActionFiles(mRoomIdStatic, result.messageId, getAction(result.messageType), chatTypeStatic);
                }
            }
        }
    }

    private static ProtoGlobal.ClientAction getAction(ProtoGlobal.RoomMessageType type) {

        //TODO [Saeed Mozaffari] [2016-11-14 11:14 AM] - some actions need to detect

        ProtoGlobal.ClientAction action = null;

        if ((type == ProtoGlobal.RoomMessageType.IMAGE) || (type == IMAGE_TEXT)) {
            action = SENDING_IMAGE;
        } else if ((type == ProtoGlobal.RoomMessageType.VIDEO) || (type == VIDEO_TEXT)) {
            action = SENDING_VIDEO;
        } else if ((type == ProtoGlobal.RoomMessageType.AUDIO) || (type == ProtoGlobal.RoomMessageType.AUDIO_TEXT)) {
            action = SENDING_AUDIO;
        } else if (type == ProtoGlobal.RoomMessageType.VOICE) {
            action = SENDING_VOICE;
        } else if ((type == ProtoGlobal.RoomMessageType.GIF) || type == GIF_TEXT) {
            action = SENDING_GIF;
        } else if ((type == ProtoGlobal.RoomMessageType.FILE) || (type == ProtoGlobal.RoomMessageType.FILE_TEXT)) {
            action = SENDING_FILE;
        } else if (type == ProtoGlobal.RoomMessageType.LOCATION) {
            action = SENDING_LOCATION;
        } else if (type == CONTACT) {
            action = CHOOSING_CONTACT;
        }

        return action;
    }

    /**
     * change message status from sending to failed
     *
     * @param fakeMessageId messageId that create when created this message
     */
    private void makeFailed(final long fakeMessageId) {
        // message failed
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, fakeMessageId).findFirst();
                        if (message != null && message.getStatus().equals(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                            message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, fakeMessageId).findFirst();
                        if (message != null && message.getStatus().equals(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                            G.chatSendMessageUtil.onMessageFailed(message.getRoomId(), message);
                        }
                    }
                });
            }
        });
    }

    private class SearchHash {

        public int currentSelectedPosition = 0;
        private String hashString = "";
        private int currentHashPosition = 0;

        private ArrayList<Integer> hashList = new ArrayList<>();

        public void setHashString(String hashString) {
            this.hashString = "#" + hashString;
        }

        public void setPosition(String messageId) {

            try {
                if (mAdapter.getItem(searchHash.currentSelectedPosition).mMessage.view != null) {
                    ((FrameLayout) mAdapter.getItem(searchHash.currentSelectedPosition).mMessage.view).setForeground(null);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            currentHashPosition = 0;
            hashList.clear();

            for (int i = 0; i < mAdapter.getAdapterItemCount(); i++) {

                if (mAdapter.getItem(i).mMessage.messageID.equals(messageId)) {
                    currentHashPosition = hashList.size() + 1;
                }

                if (mAdapter.getItem(i).mMessage.messageText.contains(hashString)) {
                    hashList.add(i);
                }
            }

            txtHashCounter.setText(currentHashPosition + " / " + hashList.size());

            currentSelectedPosition = hashList.get(currentHashPosition - 1);

            if (mAdapter.getItem(currentSelectedPosition).mMessage.view != null) {
                ((FrameLayout) mAdapter.getItem(currentSelectedPosition).mMessage.view).setForeground(new ColorDrawable(getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
            }
        }

        public void downHash() {

            if (currentHashPosition < hashList.size()) {
                goToSelectedPosition(hashList.get(currentHashPosition));
                currentHashPosition++;
                txtHashCounter.setText(currentHashPosition + " / " + hashList.size());
            }
        }

        public void upHash() {

            if (currentHashPosition > 1) {
                currentHashPosition--;
                goToSelectedPosition(hashList.get(currentHashPosition - 1));
                txtHashCounter.setText(currentHashPosition + " / " + hashList.size());
            }
        }

        private void goToSelectedPosition(int position) {

            ((FrameLayout) mAdapter.getItem(currentSelectedPosition).mMessage.view).setForeground(null);

            recyclerView.scrollToPosition(position);

            currentSelectedPosition = position;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter.getItem(currentSelectedPosition).mMessage.view != null) {
                        ((FrameLayout) mAdapter.getItem(currentSelectedPosition).mMessage.view).setForeground(new ColorDrawable(getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
                    }
                }
            }, 150);
        }

    }

    //******* GroupAvatar and ChannelAvatar

    @Override
    public void onAvatarAdd(final long roomId, ProtoGlobal.Avatar avatar) {

        HelperAvatar.getAvatar(roomId, HelperAvatar.AvatarType.ROOM, new OnAvatarGet() {
            @Override
            public void onAvatarGet(final String avatarPath, long ownerId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(avatarPath), imvUserPicture);
                    }
                });
            }

            @Override
            public void onShowInitials(final String initials, final String color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imvUserPicture.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvUserPicture.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });
    }

    @Override
    public void onAvatarAddError() {

    }

    //****** Channel Message Reaction

    @Override
    public void onChannelAddMessageReaction(final long roomId, final long messageId, final String reactionCounterLabel, final ProtoGlobal.RoomMessageReaction reaction, final long forwardedMessageId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.updateVote(roomId, messageId, reactionCounterLabel, reaction, forwardedMessageId);
            }
        });
    }

    @Override
    public void onError(int majorCode, int minorCode) {

    }

    @Override
    public void onChannelGetMessagesStats(final List<ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Stats> statsList) {

        if (mAdapter != null) {
            for (final ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Stats stats : statsList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.updateMessageState(stats.getMessageId(), stats.getThumbsUpLabel(), stats.getThumbsDownLabel(), stats.getViewsLabel());
                    }
                });
            }
        }
    }

    public static ArrayList<StructBottomSheet> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data = 0, column_index_folder_name;
        ArrayList<StructBottomSheet> listOfAllImages = new ArrayList<StructBottomSheet>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };

        cursor = activity.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        }
        if (cursor != null) {
            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        }

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            StructBottomSheet item = new StructBottomSheet();
            item.setPath(absolutePathOfImage);
            item.isSelected = true;
            listOfAllImages.add(0, item);
        }

        return listOfAllImages;
    }

    public void itemAdapterBottomSheet() {
        listPathString.clear();
        fastItemAdapter.clear();
        itemGalleryList = getAllShownImagesPath(ActivityChat.this);
        int itemSize = itemGalleryList.size();
        for (int i = 0; i < itemSize; i++) {
            fastItemAdapter.add(new AdapterBottomSheet(itemGalleryList.get(i)).withIdentifier(100 + i));
        }

        itemGalleryList.clear();

    }
}

