package com.iGap.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewStubCompat;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.IntentRequests;
import com.iGap.R;
import com.iGap.adapter.MessagesAdapter;
import com.iGap.adapter.items.chat.AbstractMessage;
import com.iGap.adapter.items.chat.AudioItem;
import com.iGap.adapter.items.chat.ChannelAudioItem;
import com.iGap.adapter.items.chat.ChannelContactItem;
import com.iGap.adapter.items.chat.ChannelFileItem;
import com.iGap.adapter.items.chat.ChannelGifItem;
import com.iGap.adapter.items.chat.ChannelImageItem;
import com.iGap.adapter.items.chat.ChannelTextItem;
import com.iGap.adapter.items.chat.ChannelVideoItem;
import com.iGap.adapter.items.chat.ChannelVoiceItem;
import com.iGap.adapter.items.chat.ContactItem;
import com.iGap.adapter.items.chat.FileItem;
import com.iGap.adapter.items.chat.GifItem;
import com.iGap.adapter.items.chat.ImageItem;
import com.iGap.adapter.items.chat.ImageWithTextItem;
import com.iGap.adapter.items.chat.LocationItem;
import com.iGap.adapter.items.chat.TextItem;
import com.iGap.adapter.items.chat.TimeItem;
import com.iGap.adapter.items.chat.VideoItem;
import com.iGap.adapter.items.chat.VideoWithTextItem;
import com.iGap.adapter.items.chat.VoiceItem;
import com.iGap.fragments.FragmentShowImageMessages;
import com.iGap.helper.Emojione;
import com.iGap.helper.HelperGetDataFromOtherApp;
import com.iGap.helper.HelperMimeType;
import com.iGap.helper.HelperNotificationAndBadge;
import com.iGap.interfaces.IEmojiBackspaceClick;
import com.iGap.interfaces.IEmojiClickListener;
import com.iGap.interfaces.IEmojiLongClickListener;
import com.iGap.interfaces.IEmojiStickerClick;
import com.iGap.interfaces.IEmojiViewCreate;
import com.iGap.interfaces.IMessageItem;
import com.iGap.interfaces.IRecentsLongClick;
import com.iGap.interfaces.IResendMessage;
import com.iGap.interfaces.ISoftKeyboardOpenClose;
import com.iGap.interfaces.OnChatClearMessageResponse;
import com.iGap.interfaces.OnChatDelete;
import com.iGap.interfaces.OnChatDeleteMessageResponse;
import com.iGap.interfaces.OnChatEditMessageResponse;
import com.iGap.interfaces.OnChatMessageRemove;
import com.iGap.interfaces.OnChatMessageSelectionChanged;
import com.iGap.interfaces.OnChatSendMessageResponse;
import com.iGap.interfaces.OnChatUpdateStatusResponse;
import com.iGap.interfaces.OnClearChatHistory;
import com.iGap.interfaces.OnClientGetRoomHistoryResponse;
import com.iGap.interfaces.OnDeleteChatFinishActivity;
import com.iGap.interfaces.OnFileDownloadResponse;
import com.iGap.interfaces.OnFileUploadForActivities;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.interfaces.OnVoiceRecord;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AttachFile;
import com.iGap.module.ChatSendMessageUtil;
import com.iGap.module.ContactUtils;
import com.iGap.module.EmojiEditText;
import com.iGap.module.EmojiPopup;
import com.iGap.module.EmojiRecentsManager;
import com.iGap.module.EndlessRecyclerOnScrollListener;
import com.iGap.module.FileUploadStructure;
import com.iGap.module.FileUtils;
import com.iGap.module.HelperDecodeFile;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.MusicPlayer;
import com.iGap.module.MyType;
import com.iGap.module.OnComplete;
import com.iGap.module.RecyclerViewPauseOnScrollListener;
import com.iGap.module.ResendMessage;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.ShouldScrolledBehavior;
import com.iGap.module.SortMessages;
import com.iGap.module.StructMessageAttachment;
import com.iGap.module.StructMessageInfo;
import com.iGap.module.TimeUtils;
import com.iGap.module.VoiceRecord;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmChannelRoom;
import com.iGap.realm.RealmChatRoom;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmContactsFields;
import com.iGap.realm.RealmDraftFile;
import com.iGap.realm.RealmGroupRoom;
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
import com.iGap.realm.RealmUserInfo;
import com.iGap.realm.enums.ChannelChatRole;
import com.iGap.realm.enums.GroupChatRole;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestChatDelete;
import com.iGap.request.RequestChatDeleteMessage;
import com.iGap.request.RequestChatEditMessage;
import com.iGap.request.RequestChatUpdateDraft;
import com.iGap.request.RequestFileDownload;
import com.iGap.request.RequestGroupUpdateDraft;
import com.mikepenz.fastadapter.IItemAdapter;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.parceler.Parcels;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static com.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static com.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class ActivityChat extends ActivityEnhanced
    implements IEmojiViewCreate, IRecentsLongClick, IMessageItem, OnChatClearMessageResponse,
    OnChatSendMessageResponse, OnChatUpdateStatusResponse,
    OnChatMessageSelectionChanged<AbstractMessage>, OnChatMessageRemove, OnFileDownloadResponse,
    OnVoiceRecord, OnUserInfoResponse, OnClientGetRoomHistoryResponse, OnFileUploadForActivities {

    public static ActivityChat activityChat;
    public static OnComplete hashListener;
    AttachFile attachFile;
    BoomMenuButton boomMenuButton;
    LinearLayout mediaLayout;
    MusicPlayer musicPlayer;
    LinearLayout ll_Search;
    Button btnCloseLayoutSearch;
    EditText edtSearchMessage;
    long messageId;
    int scroolPosition = 0;
    private RelativeLayout parentLayout;
    private SharedPreferences sharedPreferences;
    private EmojiEditText edtChat;
    private MaterialDesignTextView imvSendButton;
    private MaterialDesignTextView imvAttachFileButton;
    private LinearLayout layoutAttachBottom;
    private MaterialDesignTextView imvMicButton;
    private MaterialDesignTextView btnCloseAppBarSelected;
    private MaterialDesignTextView btnReplaySelected;
    private MaterialDesignTextView btnCopySelected;
    private MaterialDesignTextView btnForwardSelected;
    private MaterialDesignTextView btnDeleteSelected;
    private Button btnCancelSeningFile;
    private TextView txtFileNameForSend;
    private LinearLayout ll_attach_text;
    private TextView txtNumberOfSelected;
    private LinearLayout ll_AppBarSelected;
    private LinearLayout toolbar;
    private TextView txtName;
    private TextView txtLastSeen;
    private TextView txt_mute;
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
    private Button btnUpMessage;
    private Button btnDownMessage;
    private TextView txtMessageCounter;
    private int messageCounter = 0;
    private int selectedPosition = 0;
    private LinearLayout ll_navigateHash;
    private Button btnUpHash;
    private Button btnDownHash;
    private TextView txtHashCounter;
    private Button btnHashLayoutClose;
    private SearhHash searhHash;
    private MessagesAdapter<AbstractMessage> mAdapter;
    private ProtoGlobal.Room.Type chatType;
    private String lastSeen;
    private long mRoomId;
    private Button btnUp;
    private Button btnDown;
    private TextView txtChannelMute;
    //popular (chat , group , channel)
    private String title;
    private String initialize;
    private String color;
    private boolean isMute = false;
    //chat
    private long chatPeerId;
    private boolean isMuteNotification;
    //group
    private GroupChatRole groupRole;
    private String groupParticipantsCountLabel;
    //channel
    private ChannelChatRole channelRole;
    private String channelParticipantsCountLabel;
    private PopupWindow popupWindow;
    private String avatarPath;
    // save latest intent data and requestCode from result activity for set draft if not send
    // file yet
    private Uri latestUri;
    private int latestRequestCode;
    private String latestFilePath;
    private Calendar lastDateCalendar = Calendar.getInstance();
    private LinearLayout mReplayLayout;

    private MaterialDesignTextView iconMute;

    private boolean hasDraft = false;
    private long replyToMessageId = 0;

    @Override protected void onStart() {
        super.onStart();

        // when user receive message, I send update status as SENT to the message sender
        // but imagine user is not in the room (or he is in another room) and received some messages
        // when came back to the room with new messages, I make new update status request as SEEN to
        // the message sender
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class)
            .equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId)
            .findAllAsync();
        realmRoomMessages.addChangeListener(
            new RealmChangeListener<RealmResults<RealmRoomMessage>>() {
                @Override public void onChange(final RealmResults<RealmRoomMessage> element) {
                    //Start ClientCondition OfflineSeen
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override public void execute(Realm realm) {
                            final RealmClientCondition realmClientCondition =
                                realm.where(RealmClientCondition.class)
                                    .equalTo(RealmClientConditionFields.ROOM_ID, mRoomId)
                                    .findFirst();

                            final ArrayList<Long> offlineSeenId = new ArrayList<>();

                            long id = System.nanoTime();

                            for (RealmRoomMessage roomMessage : element) {
                                if (roomMessage != null) {
                                    if (roomMessage.getUserId() != realm.where(RealmUserInfo.class)
                                        .findFirst()
                                        .getUserId() && !roomMessage.getStatus()
                                        .equalsIgnoreCase(
                                            ProtoGlobal.RoomMessageStatus.SEEN.toString())) {

                                        roomMessage.setStatus(
                                            ProtoGlobal.RoomMessageStatus.SEEN.toString());

                                        RealmOfflineSeen realmOfflineSeen =
                                            realm.createObject(RealmOfflineSeen.class);
                                        realmOfflineSeen.setId(id++);
                                        realmOfflineSeen.setOfflineSeen(roomMessage.getMessageId());
                                        realm.copyToRealmOrUpdate(realmOfflineSeen);

                                        realmClientCondition.getOfflineSeen().add(realmOfflineSeen);
                                        offlineSeenId.add(roomMessage.getMessageId());
                                    }
                                }
                            }
                            for (long seenId : offlineSeenId) {
                                G.chatUpdateStatusUtil.sendUpdateStatus(chatType, mRoomId, seenId,
                                    ProtoGlobal.RoomMessageStatus.SEEN);
                            }

                            if (chatType != null) {

                                if (chatType == CHAT) {
                                    G.helperNotificationAndBadge.checkAlert(false, CHAT, mRoomId);
                                } else if (chatType == GROUP) {
                                    G.helperNotificationAndBadge.checkAlert(false, GROUP, mRoomId);
                                } else if (chatType == CHANNEL) {
                                    G.helperNotificationAndBadge.checkAlert(false, CHANNEL,
                                        mRoomId);
                                }
                            }
                        }
                    });

                    element.removeChangeListeners();
                    realm.close();
                }
            });

        final Realm updateUnreadCountRealm = Realm.getDefaultInstance();
        updateUnreadCountRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmRoom room =
                    realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                if (room != null) {
                    room.setUnreadCount(0);
                    realm.copyToRealmOrUpdate(room);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override public void onSuccess() {
                updateUnreadCountRealm.close();
            }
        });
    }

    @Override protected void onResume() {
        super.onResume();

        HelperNotificationAndBadge.isChatRoomNow = true;

        if (MusicPlayer.mp != null) {
            MusicPlayer.initLayoutTripMusic(mediaLayout);
        }
    }

    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mediaLayout = (LinearLayout) findViewById(R.id.ac_ll_music_layout);
        musicPlayer = new MusicPlayer(mediaLayout);

        activityChat = this;
        G.helperNotificationAndBadge.cancelNotification();

        // get sendByEnter action from setting value
        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        int checkedSendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
        sendByEnter = checkedSendByEnter == 1;

        String backGroundPath =
            sharedPreferences.getString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
        if (backGroundPath.length() > 0) {

            File f = new File(backGroundPath);
            if (f.exists()) {
                Drawable d = Drawable.createFromPath(f.getAbsolutePath());
                View chat = findViewById(R.id.ac_ll_parent);
                chat.setBackgroundDrawable(d);
            }
        }

        viewAttachFile = findViewById(R.id.layout_attach_file);
        viewMicRecorder = findViewById(R.id.layout_mic_recorde);
        voiceRecord = new VoiceRecord(this, viewMicRecorder, viewAttachFile, this);

        lastDateCalendar.clear();

        attachFile = new AttachFile(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        initAttach();
        complete = new OnComplete() {
            @Override public void complete(boolean result, String messageOne, String MessageTow) {

                Log.e("ddd", messageOne);
            }
        };

        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.uploaderUtil.setActivityCallbacks(this);
        G.onFileDownloadResponse = this;
        G.onUserInfoResponse = this;
        G.onClientGetRoomHistoryResponse = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mRoomId = extras.getLong("RoomId");
            isMuteNotification = extras.getBoolean("MUT");
            Log.i("CCC", "mRoomId : " + mRoomId);

            messageId = extras.getLong("MessageId");

            Realm realm = Realm.getDefaultInstance();

            final RealmRoom realmRoom =
                realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();

            if (realmRoom != null) { // room exist

                title = realmRoom.getTitle();
                initialize = realmRoom.getInitials();
                color = realmRoom.getColor();

                if (realmRoom.getType() == RoomType.CHAT) {

                    chatType = CHAT;
                    RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                    chatPeerId = realmChatRoom.getPeerId();
                    RealmContacts realmContacts = realm.where(RealmContacts.class)
                        .equalTo(RealmContactsFields.ID, chatPeerId)
                        .findFirst();

                    RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
                        .equalTo(RealmRegisteredInfoFields.ID, chatPeerId)
                        .findFirst();

                    if (realmRegisteredInfo != null) {
                        title = realmRegisteredInfo.getDisplayName();
                        initialize = realmRegisteredInfo.getInitials();
                        color = realmRegisteredInfo.getColor();
                        lastSeen = Long.toString(realmRegisteredInfo.getLastSeen());
                    } else if (realmContacts != null) {
                        title = realmContacts.getDisplay_name();
                        initialize = realmContacts.getInitials();
                        color = realmContacts.getColor();
                        lastSeen = Long.toString(realmContacts.getLast_seen());
                    } else {
                        title = realmRoom.getTitle();
                        initialize = realmRoom.getInitials();
                        color = realmRoom.getColor();
                        lastSeen = "last seen";
                    }
                } else if (realmRoom.getType() == RoomType.GROUP) {
                    chatType = GROUP;
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    groupRole = realmGroupRoom.getRole();
                    groupParticipantsCountLabel = realmGroupRoom.getParticipantsCountLabel();
                } else if (realmRoom.getType() == RoomType.CHANNEL) {

                    chatType = CHANNEL;
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    channelRole = realmChannelRoom.getRole();
                    channelParticipantsCountLabel = realmChannelRoom.getParticipantsCountLabel();
                }
            } else {
                chatPeerId = extras.getLong("peerId");
                chatType = CHAT;
                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
                    .equalTo(RealmRegisteredInfoFields.ID, chatPeerId)
                    .findFirst();
                if (realmRegisteredInfo != null) {
                    title = realmRegisteredInfo.getDisplayName();
                    initialize = realmRegisteredInfo.getInitials();
                    color = realmRegisteredInfo.getColor();
                    lastSeen = Long.toString(realmRegisteredInfo.getLastSeen());
                }
            }
            realm.close();
        }
        initComponent();
        initAppbarSelected();
        initCallbacks();
        if (chatType == CHANNEL && channelRole == ChannelChatRole.MEMBER) {
            initLayotChannelFooter();
        }

        if (getIntent() != null
            && getIntent().getExtras() != null
            && getIntent().getExtras()
            .getParcelableArrayList(ActivitySelectChat.ARG_FORWARD_MESSAGE) != null) {
            ArrayList<Parcelable> messageInfos =
                getIntent().getParcelableArrayListExtra(ActivitySelectChat.ARG_FORWARD_MESSAGE);

            for (Parcelable messageInfo : messageInfos) {
                sendForwardedMessage((StructMessageInfo) Parcels.unwrap(messageInfo));
            }
        }
        clearHistoryFromContactsProfileInterface();
        onDeleteChatFinishActivityInterface();

        getDraft();

        if (chatType == CHAT) {
            setAvatar();
        } else {
            setAvatarGroup();
        }

    }

    private void clearHistoryFromContactsProfileInterface() {
        G.onClearChatHistory = new OnClearChatHistory() {
            @Override public void onClearChatHistory() {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        mAdapter.clear();
                    }
                });
            }

            @Override public void onError(int majorCode, int minorCode) {

            }
        };
    }

    private void onDeleteChatFinishActivityInterface() {
        G.onDeleteChatFinishActivity = new OnDeleteChatFinishActivity() {
            @Override public void onFinish() {
                runOnUiThread(new Runnable() {
                    @Override public void run() { //TODO [Saeed Mozaffari] [2016-10-15 4:19 PM] -
                        // runOnUiThread need here???
                        finish();
                    }
                });
            }
        };
    }

    private void sendForwardedMessage(final StructMessageInfo messageInfo) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class)
                    .equalTo(RealmRoomMessageFields.MESSAGE_ID,
                        Long.parseLong(messageInfo.messageID))
                    .findFirst();

                if (roomMessage != null) {
                    RealmRoomMessage forwardedMessage = realm.createObject(RealmRoomMessage.class);
                    forwardedMessage.setMessageId(System.nanoTime());
                    forwardedMessage.setMessage(roomMessage.getMessage());
                    forwardedMessage.setAttachment(roomMessage.getAttachment());
                    forwardedMessage.setForwardMessage(roomMessage);
                    forwardedMessage.setCreateTime(System.currentTimeMillis());
                    forwardedMessage.setLocation(roomMessage.getLocation());
                    forwardedMessage.setLog(roomMessage.getLog());
                    forwardedMessage.setMessageType(roomMessage.getMessageType());
                    forwardedMessage.setRoomId(mRoomId);
                    forwardedMessage.setRoomMessageContact(roomMessage.getRoomMessageContact());
                    forwardedMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                    forwardedMessage.setUserId(roomMessage.getUserId());

                    switchAddItem(new ArrayList<>(
                            Collections.singletonList(StructMessageInfo.convert(forwardedMessage))),
                        false);
                    G.chatSendMessageUtil.build(chatType, forwardedMessage.getRoomId(),
                        forwardedMessage);
                }
            }
        });
        realm.close();
    }

    public void initCallbacks() {
        G.chatSendMessageUtil.setOnChatSendMessageResponse(this);
        G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);

        G.onChatEditMessageResponse = new OnChatEditMessageResponse() {
            @Override
            public void onChatEditMessage(long roomId, final long messageId, long messageVersion,
                final String message, ProtoResponse.Response response) {
                Log.i(ActivityMain.class.getSimpleName(), "onChatEditMessage called");
                if (mRoomId == roomId) {
                    // I'm in the room
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            // update message text in adapter
                            mAdapter.updateMessageText(messageId, message);
                        }
                    });
                }
            }

            @Override public void onError(int majorCode, int minorCode) {
                if (majorCode == 209 && minorCode == 1) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_209_1), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 209 && minorCode == 2) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_209_2), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 209 && minorCode == 3) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_209_3), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 210) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_210), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 211) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_211), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                }
            }
        };

        G.onChatDeleteMessageResponse = new OnChatDeleteMessageResponse() {
            @Override
            public void onChatDeleteMessage(long deleteVersion, final long messageId, long roomId,
                ProtoResponse.Response response) {
                Log.i("CLI_DELETE", "response.getId() 4 : " + response.getId());
                if (response.getId().isEmpty()) { // another account deleted this message

                    Realm realm = Realm.getDefaultInstance();
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class)
                        .equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId)
                        .findFirst();
                    if (roomMessage != null) {
                        // delete message from database
                        roomMessage.deleteFromRealm();
                    }
                    realm.close();

                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            Log.i("CLI_DELETE",
                                "onChatDeleteMessageResponse 5 messageId : " + messageId);
                            // remove deleted message from adapter
                            mAdapter.removeMessage(messageId);

                            // remove tag from edtChat if the message has deleted
                            if (edtChat.getTag() != null
                                && edtChat.getTag() instanceof StructMessageInfo) {
                                if (Long.toString(messageId)
                                    .equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
                                    edtChat.setTag(null);
                                }
                            }
                        }
                    });
                }
            }

            @Override public void onError(int majorCode, int minorCode) {
                if (majorCode == 212 && minorCode == 1) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_212_1), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 212 && minorCode == 2) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_212_1), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 213) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_213), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                } else if (majorCode == 214) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_214), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                }
            }
        };
    }

    private void switchAddItem(ArrayList<StructMessageInfo> messageInfos, boolean addTop) {
        long identifier = System.nanoTime();
        for (StructMessageInfo messageInfo : messageInfos) {
            if (!messageInfo.isTimeMessage()) {
                switch (messageInfo.messageType) {
                    case TEXT:
                        if (chatType != CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new TextItem(chatType, this).setMessage(messageInfo)
                                    .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new TextItem(chatType, this).setMessage(messageInfo)
                                    .withIdentifier(identifier));
                            }
                        } else {
                            if (!addTop) {
                                mAdapter.add(
                                    new ChannelTextItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new ChannelTextItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        }
                        break;
                    case IMAGE:
                        if (chatType != CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new ImageItem(chatType, this).setMessage(messageInfo)
                                    .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new ImageItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        } else {
                            if (!addTop) {
                                mAdapter.add(
                                    new ChannelImageItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new ChannelImageItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        }
                        break;
                    case IMAGE_TEXT:
                        if (chatType != CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(
                                    new ImageWithTextItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new ImageWithTextItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        } else {
                            if (!addTop) {
                                mAdapter.add(
                                    new ChannelImageItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new ChannelImageItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        }
                        break;
                    case VIDEO:
                        if (chatType != CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new VideoItem(chatType, this).setMessage(messageInfo)
                                    .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new VideoItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        } else {
                            if (!addTop) {
                                mAdapter.add(
                                    new ChannelVideoItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new ChannelVideoItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        }
                        break;
                    case VIDEO_TEXT:
                        if (chatType != CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(
                                    new VideoWithTextItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new VideoWithTextItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        } else {
                            if (!addTop) {
                                mAdapter.add(
                                    new ChannelVideoItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new ChannelVideoItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        }
                        break;
                    case LOCATION:
                        // TODO: 9/15/2016 [Alireza Eskandarpour Shoferi] fill
                        if (chatType != CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(
                                    new LocationItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new LocationItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        } /*else {
                        mAdapter.add(new ChannelVideoItem(chatType,this).setMessage(messageInfo)
                        .withIdentifier(identifier));
                    }*/
                        break;
                    case FILE:
                    case FILE_TEXT:
                        if (chatType != CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new FileItem(chatType, this).setMessage(messageInfo)
                                    .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new FileItem(chatType, this).setMessage(messageInfo)
                                    .withIdentifier(identifier));
                            }
                        } else if (chatType == CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(
                                    new ChannelFileItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new ChannelFileItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        }
                        break;
                    case VOICE:
                        if (chatType != CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new VoiceItem(chatType, this).setMessage(messageInfo)
                                    .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new VoiceItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        } else if (chatType == CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(
                                    new ChannelVoiceItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new ChannelVoiceItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        }
                        break;
                    case AUDIO:
                    case AUDIO_TEXT:
                        if (chatType != CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new AudioItem(chatType, this).setMessage(messageInfo)
                                    .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new AudioItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        } else if (chatType == CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(
                                    new ChannelAudioItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new ChannelAudioItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        }
                        break;
                    case CONTACT:
                        if (chatType != CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new ContactItem(chatType, this).setMessage(messageInfo)
                                    .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new ContactItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        } else if (chatType == CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(
                                    new ChannelContactItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new ChannelContactItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        }
                        break;
                    case GIF:
                        if (chatType != CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new GifItem(chatType, this).setMessage(messageInfo)
                                    .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new GifItem(chatType, this).setMessage(messageInfo)
                                    .withIdentifier(identifier));
                            }
                        } else if (chatType == CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(
                                    new ChannelGifItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            } else {
                                mAdapter.add(0,
                                    new ChannelGifItem(chatType, this).setMessage(messageInfo)
                                        .withIdentifier(identifier));
                            }
                        }
                        break;
                    case LOG:
                        // TODO: 9/15/2016 [Alireza Eskandarpour Shoferi] implement
                        break;
                }
            } else {
                if (!addTop) {
                    mAdapter.add(
                        new TimeItem(this).setMessage(messageInfo).withIdentifier(identifier));
                } else {
                    mAdapter.add(0,
                        new TimeItem(this).setMessage(messageInfo).withIdentifier(identifier));
                }
            }

            identifier++; //required
        }
    }

    private void selectMessage(int position) {
        //  mAdapter.select(position, true);// TODO: 10/16/2016  work on blue back ground
        // selected item
    }

    private void deSelectMessage(int position) {
        // mAdapter.deselect(position);
    }

    private void initComponent() {

        initLayoutSearchNavigation();
        initLayoutHashNavigation();

        toolbar = (LinearLayout) findViewById(R.id.toolbar);
        MaterialDesignTextView imvBackButton =
            (MaterialDesignTextView) findViewById(R.id.chl_imv_back_Button);

        iconMute = (MaterialDesignTextView) findViewById(R.id.imgMutedRoom);
        RippleView rippleBackButton = (RippleView) findViewById(R.id.chl_ripple_back_Button);

        final Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom =
            realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null) {

            iconMute.setVisibility(realmRoom.getMute() ? View.VISIBLE : View.GONE);
        }
        realm.close();

        ll_attach_text = (LinearLayout) findViewById(R.id.ac_ll_attach_text);
        txtFileNameForSend = (TextView) findViewById(R.id.ac_txt_file_neme_for_sending);
        btnCancelSeningFile = (Button) findViewById(R.id.ac_btn_cancel_sending_file);
        btnCancelSeningFile.setTypeface(G.flaticon);
        btnCancelSeningFile.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                ll_attach_text.setVisibility(View.GONE);

                if (edtChat.getText().length() == 0) {

                    layoutAttachBottom.animate()
                        .alpha(1F)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                layoutAttachBottom.setVisibility(View.VISIBLE);
                            }
                        })
                        .start();
                    imvSendButton.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                        @Override public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            imvSendButton.setVisibility(View.GONE);
                        }
                    }).start();
                }
            }
        });

        txtName = (TextView) findViewById(R.id.chl_txt_name);
        txtName.setTypeface(G.arialBold);
        if (title != null) txtName.setText(title);

        txtLastSeen = (TextView) findViewById(R.id.chl_txt_last_seen);

        if (chatType == CHAT) {

            if (lastSeen != null) {
                txtLastSeen.setText(lastSeen);
            }
        } else if (chatType == GROUP) {

            if (groupParticipantsCountLabel != null) {
                txtLastSeen.setText(groupParticipantsCountLabel + " member");
            }
        } else if (chatType == CHANNEL) {

            if (channelParticipantsCountLabel != null) {
                txtLastSeen.setText(channelParticipantsCountLabel + " member");
            }
        }

        txt_mute = (TextView) findViewById(R.id.chl_txt_mute);
        txt_mute.setTypeface(G.fontawesome);

        if (isMute) {
            txt_mute.setVisibility(View.VISIBLE);
        }

        imvUserPicture = (ImageView) findViewById(R.id.chl_imv_user_picture);

        final int screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.7);
        MaterialDesignTextView imvMenuButton =
            (MaterialDesignTextView) findViewById(R.id.chl_imv_menu_button);

        RippleView rippleMenuButton = (RippleView) findViewById(R.id.chl_ripple_menu_button);
        rippleMenuButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {

                LinearLayout layoutDialog = new LinearLayout(ActivityChat.this);
                ViewGroup.LayoutParams params =
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutDialog.setOrientation(LinearLayout.VERTICAL);
                layoutDialog.setBackgroundColor(getResources().getColor(android.R.color.white));
                TextView text1 = new TextView(ActivityChat.this);
                TextView text2 = new TextView(ActivityChat.this);
                TextView text3 = new TextView(ActivityChat.this);
                final TextView text4 = new TextView(ActivityChat.this);

                text1.setTextColor(getResources().getColor(android.R.color.black));
                text2.setTextColor(getResources().getColor(android.R.color.black));
                text3.setTextColor(getResources().getColor(android.R.color.black));
                text4.setTextColor(getResources().getColor(android.R.color.black));

                text1.setText(getResources().getString(R.string.Search));
                text2.setText(getResources().getString(R.string.clear_history));
                text3.setText(getResources().getString(R.string.delete_chat));
                text4.setText(getResources().getString(R.string.mute_notification));

                int dim20 = (int) getResources().getDimension(R.dimen.dp20);
                int dim16 = (int) getResources().getDimension(R.dimen.dp16);
                int dim12 = (int) getResources().getDimension(R.dimen.dp12);
                int sp16 = (int) getResources().getDimension(R.dimen.sp12);

                text1.setTextSize(16);
                text2.setTextSize(16);
                text3.setTextSize(16);
                text4.setTextSize(16);

                text1.setPadding(dim20, dim12, dim12, dim20);
                text2.setPadding(dim20, 0, dim12, dim20);
                text3.setPadding(dim20, 0, dim12, dim20);
                text4.setPadding(dim20, 0, dim12, (dim16));

                layoutDialog.addView(text1, params);
                layoutDialog.addView(text2, params);
                layoutDialog.addView(text3, params);
                layoutDialog.addView(text4, params);

                final Realm realm = Realm.getDefaultInstance();
                RealmRoom realmRoom =
                    realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                if (realmRoom != null) {

                    if (realmRoom.getMute()) {
                        iconMute.setVisibility(View.VISIBLE);
                        text4.setText(getResources().getString(R.string.unmute_notification));
                    } else {
                        iconMute.setVisibility(View.GONE);
                        text4.setText(getResources().getString(R.string.mute_notification));
                    }
                }
                realm.close();

                popupWindow =
                    new PopupWindow(layoutDialog, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT,
                        true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.setBackgroundDrawable(
                        getResources().getDrawable(R.mipmap.shadow2, ActivityChat.this.getTheme()));
                } else {
                    popupWindow.setBackgroundDrawable(
                        (getResources().getDrawable(R.mipmap.shadow2)));
                }
                if (popupWindow.isOutsideTouchable()) {
                    popupWindow.dismiss();
                }
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override public void onDismiss() {
                        //TODO do sth here on dismiss
                    }
                });

                popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
                popupWindow.showAtLocation(rippleView, Gravity.RIGHT | Gravity.TOP,
                    (int) getResources().getDimension(R.dimen.dp16),
                    (int) getResources().getDimension(R.dimen.dp32));
                //                popupWindow.showAsDropDown(v);

                text1.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        popupWindow.dismiss();
                        findViewById(R.id.toolbarContainer).setVisibility(View.GONE);
                        ll_Search.setVisibility(View.VISIBLE);
                        popupWindow.dismiss();
                        ll_navigate_Message.setVisibility(View.VISIBLE);
                        viewAttachFile.setVisibility(View.GONE);
                        edtSearchMessage.requestFocus();
                    }
                });
                text2.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        onSelectRoomMenu("txtClearHistory", (int) mRoomId);

                        new MaterialDialog.Builder(ActivityChat.this).title(R.string.clear_history)
                            .content(R.string.clear_history_content)
                            .positiveText(R.string.B_ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override public void onClick(@NonNull MaterialDialog dialog,
                                    @NonNull DialogAction which) {
                                    onSelectRoomMenu("txtClearHistory", (int) mRoomId);
                                }
                            })
                            .negativeText(R.string.B_cancel)
                            .show();
                        popupWindow.dismiss();
                    }
                });
                text3.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {

                        new MaterialDialog.Builder(ActivityChat.this).title(R.string.delete_chat)
                            .content(R.string.delete_chat_content)
                            .positiveText(R.string.B_ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override public void onClick(@NonNull MaterialDialog dialog,
                                    @NonNull DialogAction which) {
                                    onSelectRoomMenu("txtDeleteChat", (int) mRoomId);
                                }
                            })
                            .negativeText(R.string.B_cancel)
                            .show();
                        popupWindow.dismiss();
                    }
                });
                text4.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {

                        onSelectRoomMenu("txtMuteNotification", (int) mRoomId);

                        final Realm realm = Realm.getDefaultInstance();
                        final RealmRoom realmRoom = realm.where(RealmRoom.class)
                            .equalTo(RealmRoomFields.ID, mRoomId)
                            .findFirst();
                        if (realmRoom != null) {
                            if (realmRoom.getMute()) {
                                iconMute.setVisibility(View.VISIBLE);
                                text4.setText(
                                    getResources().getString(R.string.unmute_notification));
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override public void execute(Realm realm) {

                                        realm.where(RealmRoom.class)
                                            .equalTo(RealmRoomFields.ID, mRoomId)
                                            .findFirst()
                                            .setMute(true);
                                    }
                                });
                            } else {
                                iconMute.setVisibility(View.GONE);
                                text4.setText(getResources().getString(R.string.mute_notification));
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override public void execute(Realm realm) {

                                        realm.where(RealmRoom.class)
                                            .equalTo(RealmRoomFields.ID, mRoomId)
                                            .findFirst()
                                            .setMute(false);
                                    }
                                });
                            }
                        }
                        realm.close();
                        popupWindow.dismiss();
                    }
                });
            }
        });

        imvSmileButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_smile_button);

        edtChat = (EmojiEditText) findViewById(R.id.chl_edt_chat);
        edtChat.requestFocus();

        imvSendButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_send_button);

        imvAttachFileButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_attach_button);
        layoutAttachBottom = (LinearLayout) findViewById(R.id.layoutAttachBottom);

        imvMicButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_mic_button);

        recyclerView = (RecyclerView) findViewById(R.id.chl_recycler_view_chat);
        // remove blinking for updates on items
        recyclerView.setItemAnimator(null);
        // following lines make scrolling smoother
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);

        mAdapter = new MessagesAdapter<>(this, this, this);

        mAdapter.withFilterPredicate(new IItemAdapter.Predicate<AbstractMessage>() {
            @Override public boolean filter(AbstractMessage item, CharSequence constraint) {
                return !item.mMessage.messageText.toLowerCase()
                    .contains(constraint.toString().toLowerCase());
            }
        });

        switchAddItem(getChatList(), true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ActivityChat.this);
        // make start messages from bottom, this is exatly what Telegram and other messengers do
        // for their messages list
        layoutManager.setStackFromEnd(true);
        // set behavior to RecyclerView
        CoordinatorLayout.LayoutParams params =
            (CoordinatorLayout.LayoutParams) recyclerView.getLayoutParams();
        params.setBehavior(new ShouldScrolledBehavior(layoutManager, mAdapter));
        recyclerView.setLayoutParams(params);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        if (messageId > 0) {
            // TODO: 10/15/2016  if list biger then 50 item list should load some data we need
            scroolPosition = 0;
            for (AbstractMessage chatItem : mAdapter.getAdapterItems()) {
                if (chatItem.mMessage.messageID.equals(messageId + "")) {
                    break;
                }
                scroolPosition++;
            }
            recyclerView.postDelayed(new Runnable() {
                @Override public void run() {
                    recyclerView.scrollToPosition(scroolPosition);
                }
            }, 1500);
        } else {
            int position = recyclerView.getAdapter().getItemCount();
            if (position > 0) recyclerView.scrollToPosition(position - 1);
        }

        //        imvBackButton.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                finish();
        //            }
        //        });
        rippleBackButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        imvUserPicture.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (chatType == CHAT
                    && chatPeerId
                    != 134) {//TODO [Saeed Mozaffari] [2016-09-07 11:46 AM] -  in if eshtebah ast
                    // check for iGap message ==> chatPeerId == 134(alan baraye check kardane) ,
                    // waiting for userDetail proto
                    Intent intent = new Intent(G.context, ActivityContactsProfile.class);
                    intent.putExtra("peerId", chatPeerId);
                    intent.putExtra("RoomId", mRoomId);
                    intent.putExtra("enterFrom", CHAT.toString());
                    startActivity(intent);
                } else if (chatType == GROUP) {
                    Intent intent = new Intent(G.context, ActivityGroupProfile.class);
                    intent.putExtra("RoomId", mRoomId);
                    startActivity(intent);
                }
            }
        });

        imvSendButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                clearDraftRequest();

                if (ll_attach_text.getVisibility() == View.VISIBLE) {
                    sendMessage(latestRequestCode, latestUri);
                    ll_attach_text.setVisibility(View.GONE);
                    edtChat.setText("");
                    return;
                }

                // if use click on edit message, the message's text will be put to the EditText
                // i set the message object for that view's tag to obtain it here
                // request message edit only if there is any changes to the message text
                if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                    final StructMessageInfo messageInfo = (StructMessageInfo) edtChat.getTag();
                    final String message = getWrittenMessage();
                    if (!message.equals(messageInfo.messageText)) {

                        final Realm realm1 = Realm.getDefaultInstance();
                        realm1.executeTransaction(new Realm.Transaction() {
                            @Override public void execute(Realm realm) {
                                RealmRoomMessage roomMessage = realm1.where(RealmRoomMessage.class)
                                    .equalTo(RealmRoomMessageFields.MESSAGE_ID,
                                        Long.parseLong(messageInfo.messageID))
                                    .findFirst();

                                RealmClientCondition realmClientCondition =
                                    realm1.where(RealmClientCondition.class)
                                        .equalTo(RealmClientConditionFields.ROOM_ID, mRoomId)
                                        .findFirst();

                                RealmOfflineEdited realmOfflineEdited =
                                    realm.createObject(RealmOfflineEdited.class);
                                realmOfflineEdited.setId(System.nanoTime());
                                realmOfflineEdited.setMessageId(
                                    Long.parseLong(messageInfo.messageID));
                                realmOfflineEdited.setMessage(message);
                                realmOfflineEdited = realm.copyToRealm(realmOfflineEdited);

                                realmClientCondition.getOfflineEdited().add(realmOfflineEdited);

                                if (roomMessage != null) {
                                    // update message text in database
                                    roomMessage.setMessage(message);
                                    roomMessage.setEdited(true);
                                }
                            }
                        });

                        realm1.close();
                        //End

                        // I'm in the room
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                // update message text in adapter
                                mAdapter.updateMessageText(Long.parseLong(messageInfo.messageID),
                                    message);
                            }
                        });

                        // should be null after requesting
                        edtChat.setTag(null);
                        edtChat.setText("");

                        // send edit message request
                        new RequestChatEditMessage().chatEditMessage(mRoomId,
                            Long.parseLong(messageInfo.messageID), message);
                    }
                } else {
                    // new message has written
                    final String message = getWrittenMessage();
                    final Realm realm = Realm.getDefaultInstance();
                    final long senderId = realm.where(RealmUserInfo.class).findFirst().getUserId();
                    if (!message.isEmpty()) {
                        final String identity = Long.toString(System.currentTimeMillis());

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override public void execute(Realm realm) {
                                RealmRoomMessage roomMessage =
                                    realm.createObject(RealmRoomMessage.class);

                                roomMessage.setMessageType(
                                    ProtoGlobal.RoomMessageType.TEXT.toString());
                                roomMessage.setRoomId(mRoomId);
                                roomMessage.setMessage(message);
                                roomMessage.setStatus(
                                    ProtoGlobal.RoomMessageStatus.SENDING.toString());
                                roomMessage.setMessageId(Long.parseLong(identity));
                                roomMessage.setUserId(senderId);
                                roomMessage.setUpdateTime(System.currentTimeMillis());

                                // user wants to replay to a message
                                if (mReplayLayout != null
                                    && mReplayLayout.getTag() instanceof StructMessageInfo) {
                                    RealmRoomMessage messageToReplay =
                                        realm.where(RealmRoomMessage.class)
                                            .equalTo(RealmRoomMessageFields.MESSAGE_ID,
                                                Long.parseLong(
                                                    ((StructMessageInfo) mReplayLayout.getTag()).messageID))
                                            .findFirst();
                                    if (messageToReplay != null) {
                                        roomMessage.setReplyTo(messageToReplay);
                                    }
                                }
                            }
                        });

                        RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class)
                            .equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identity))
                            .findFirst();

                        mAdapter.add(new TextItem(chatType, ActivityChat.this).setMessage(
                            StructMessageInfo.convert(roomMessage))
                            .withIdentifier(System.nanoTime()));

                        realm.close();

                        scrollToEnd();

                        new ChatSendMessageUtil().newBuilder(chatType,
                            ProtoGlobal.RoomMessageType.TEXT, mRoomId)
                            .message(message)
                            .sendMessage(identity);

                        edtChat.setText("");

                        // if replay layout is visible, gone it
                        if (mReplayLayout != null) {
                            mReplayLayout.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(G.context, "Please Write Your message!", Toast.LENGTH_LONG)
                            .show();
                    }
                }
            }
        });

        imvAttachFileButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                G.handler.postDelayed(new Runnable() {
                    @Override public void run() {
                        boomMenuButton.boom();
                    }
                }, 200);
            }
        });

        imvMicButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View view) {

                voiceRecord.setItemTag("ivVoice");
                viewAttachFile.setVisibility(View.GONE);
                viewMicRecorder.setVisibility(View.VISIBLE);
                voiceRecord.startVoiceRecord();

                return true;
            }
        });

        // init emoji popup
        // give the topmost view of your activity layout hierarchy. this will be used to measure
        // soft keyboard height
        final EmojiPopup emojiPopup =
            new EmojiPopup(getWindow().findViewById(android.R.id.content), getApplicationContext(),
                this);
        emojiPopup.setRecentsLongClick(this);
        emojiPopup.setAnimationStyle(R.style.EmojiPopupAnimation);
        emojiPopup.setBackgroundDrawable(new ColorDrawable());
        // will automatically set size according to the soft keyboard size
        emojiPopup.setSizeForSoftKeyboard();
        emojiPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override public void onDismiss() {
                // if the emoji popup is dismissed, change emoji image resource to smiley icon
                changeEmojiButtonImageResource(R.string.md_emoticon_with_happy_face);
            }
        });
        emojiPopup.setEmojiStickerClickListener(new IEmojiStickerClick() {
            @Override public void onEmojiStickerClick(View view) {
                // TODO useful for showing stickers panel
            }
        });
        emojiPopup.setOnSoftKeyboardOpenCloseListener(new ISoftKeyboardOpenClose() {
            @Override public void onKeyboardOpen(int keyboardHeight) {
            }

            @Override public void onKeyboardClose() {
                // if the keyboard closed, also dismiss the emoji popup
                if (emojiPopup.isShowing()) {
                    emojiPopup.dismiss();
                }
            }
        });
        emojiPopup.setEmojiLongClickListener(new IEmojiLongClickListener() {
            @Override public boolean onEmojiLongClick(View view, String emoji) {
                // TODO useful for showing a PopupWindow to select emoji in different colors
                return false;
            }
        });
        emojiPopup.setOnEmojiClickListener(new IEmojiClickListener() {

            @Override public void onEmojiClick(View view, String emoji) {
                // on emoji clicked, add to EditText
                if (edtChat == null || emoji == null) {
                    return;
                }

                String emojiUnicode = Emojione.shortnameToUnicode(emoji, false);
                int start = edtChat.getSelectionStart();
                int end = edtChat.getSelectionEnd();
                if (start < 0) {
                    edtChat.append(emojiUnicode);
                } else {
                    edtChat.getText()
                        .replace(Math.min(start, end), Math.max(start, end), emojiUnicode, 0,
                            emojiUnicode.length());
                }
            }
        });
        emojiPopup.setOnEmojiBackspaceClickListener(new IEmojiBackspaceClick() {
            @Override public void onEmojiBackspaceClick(View v) {
                // on backspace clicked, emulate the KEYCODE_DEL key event
                edtChat.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            }
        });

        // to toggle between keyboard and emoji popup
        imvSmileButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {

                // if popup is not showing => emoji keyboard is not visible, we need to show it
                if (!emojiPopup.isShowing()) {
                    // if keyboard is visible, simply show the emoji popup
                    if (emojiPopup.isKeyboardOpen()) {
                        emojiPopup.showAtBottom();
                        changeEmojiButtonImageResource(R.string.md_black_keyboard_with_white_keys);
                    }
                    // else, open the text keyboard first and immediately after that show the
                    // emoji popup
                    else {
                        edtChat.setFocusableInTouchMode(true);
                        edtChat.requestFocus();

                        emojiPopup.showAtBottomPending();

                        InputMethodManager inputMethodManager =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(edtChat, InputMethodManager.SHOW_IMPLICIT);

                        changeEmojiButtonImageResource(R.string.md_black_keyboard_with_white_keys);
                    }
                }
                // if popup is showing, simply dismiss it to show the underlying keyboard
                else {
                    emojiPopup.dismiss();
                }
            }
        });

        edtChat.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (emojiPopup.isShowing()) {
                    emojiPopup.dismiss();
                }
            }
        });
        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override public void onTextChanged(CharSequence text, int i, int i1, int i2) {

                // if in the seeting page send by enter is on message send by enter key
                if (text.toString().endsWith(System.getProperty("line.separator"))) {
                    if (sendByEnter) imvSendButton.performClick();
                }
            }

            @Override public void afterTextChanged(Editable editable) {

                if (ll_attach_text.getVisibility() == View.GONE) {

                    if (edtChat.getText().length() > 0) {
                        layoutAttachBottom.animate()
                            .alpha(0F)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    layoutAttachBottom.setVisibility(View.GONE);
                                }
                            })
                            .start();
                        imvSendButton.animate()
                            .alpha(1F)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    imvSendButton.setVisibility(View.VISIBLE);
                                }
                            })
                            .start();
                    } else {
                        layoutAttachBottom.animate()
                            .alpha(1F)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    layoutAttachBottom.setVisibility(View.VISIBLE);
                                }
                            })
                            .start();
                        imvSendButton.animate()
                            .alpha(0F)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    imvSendButton.setVisibility(View.GONE);
                                }
                            })
                            .start();
                    }
                }

                // android emoji one doesn't support common space unicode
                // to support space character, a new unicode will be replaced.
                if (editable.toString().contains("\u0020")) {
                    Editable ab =
                        new SpannableStringBuilder(editable.toString().replace("\u0020", "\u2000"));
                    editable.replace(0, editable.length(), ab);
                }
            }
        });
    }

    private void initLayoutSearchNavigation() {

        ll_navigate_Message = (LinearLayout) findViewById(R.id.ac_ll_message_navigation);
        btnUpMessage = (Button) findViewById(R.id.ac_btn_message_up);
        btnUpMessage.setTypeface(G.flaticon);
        btnDownMessage = (Button) findViewById(R.id.ac_btn_message_down);
        btnDownMessage.setTypeface(G.flaticon);
        txtMessageCounter = (TextView) findViewById(R.id.ac_txt_message_counter);

        btnUpMessage.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                if (selectedPosition > 0) {
                    deSelectMessage(selectedPosition);
                    selectedPosition--;
                    selectMessage(selectedPosition);
                    recyclerView.scrollToPosition(selectedPosition);
                    txtMessageCounter.setText(
                        selectedPosition + 1 + " " + getString(R.string.of) + messageCounter);
                }
            }
        });

        btnDownMessage.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (selectedPosition < messageCounter - 1) {
                    deSelectMessage(selectedPosition);
                    selectedPosition++;
                    selectMessage(selectedPosition);
                    recyclerView.scrollToPosition(selectedPosition);
                    txtMessageCounter.setText(
                        selectedPosition + 1 + " " + getString(R.string.of) + messageCounter);
                }
            }
        });
        RippleView rippleClose =
            (RippleView) findViewById(R.id.chl_btn_close_ripple_search_message);
        rippleClose.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                deSelectMessage(selectedPosition);
                edtSearchMessage.setText("");
            }
        });

        ll_Search = (LinearLayout) findViewById(R.id.ac_ll_search_message);
        RippleView rippleBack = (RippleView) findViewById(R.id.chl_ripple_back);
        rippleBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                deSelectMessage(selectedPosition);
                edtSearchMessage.setText("");
                ll_Search.setVisibility(View.GONE);
                findViewById(R.id.toolbarContainer).setVisibility(View.VISIBLE);
                ll_navigate_Message.setVisibility(View.GONE);
                viewAttachFile.setVisibility(View.VISIBLE);
                InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        //btnCloseLayoutSearch = (Button) findViewById(R.id.ac_btn_close_layout_search_message);
        //btnCloseLayoutSearch.setTypeface(G.flaticon);
        edtSearchMessage = (EditText) findViewById(R.id.chl_edt_search_message);
        edtSearchMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                mAdapter.filter(charSequence);

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        messageCounter = mAdapter.getAdapterItemCount();

                        if (messageCounter > 0) {
                            selectedPosition = messageCounter - 1;
                            recyclerView.scrollToPosition(selectedPosition);
                            txtMessageCounter.setText(
                                messageCounter + " " + getString(R.string.of) + messageCounter);
                            selectMessage(selectedPosition);
                        } else {
                            txtMessageCounter.setText(
                                "0 " + getString(R.string.of) + messageCounter);
                            selectedPosition = 0;
                        }
                    }
                }, 1000);
            }

            @Override public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initLayoutHashNavigation() {

        hashListener = new OnComplete() {
            @Override public void complete(boolean result, String text, String messageId) {

                searhHash.setHashString(text);
                searhHash.setPosition(messageId);
                ll_navigateHash.setVisibility(View.VISIBLE);
                viewAttachFile.setVisibility(View.GONE);
            }
        };

        ll_navigateHash = (LinearLayout) findViewById(R.id.ac_ll_hash_navigation);
        btnUpHash = (Button) findViewById(R.id.ac_btn_hash_up);
        btnUpHash.setTypeface(G.flaticon);
        btnDownHash = (Button) findViewById(R.id.ac_btn_hash_down);
        btnDownHash.setTypeface(G.flaticon);
        txtHashCounter = (TextView) findViewById(R.id.ac_txt_hash_counter);

        searhHash = new SearhHash();

        btnHashLayoutClose = (Button) findViewById(R.id.ac_btn_hash_close);
        btnHashLayoutClose.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                ll_navigateHash.setVisibility(View.GONE);
                viewAttachFile.setVisibility(View.VISIBLE);

                if (mAdapter.getItem(searhHash.curentSelectedPosition).mMessage.view != null) {
                    ((FrameLayout) mAdapter.getItem(searhHash.curentSelectedPosition).mMessage.view)
                        .setForeground(null);
                }
            }
        });

        btnUpHash.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                searhHash.upHash();
            }
        });

        btnDownHash.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                searhHash.downHash();
            }
        });
    }

    private void insertShearedData() {

        if (HelperGetDataFromOtherApp.hasSharedData) {
            HelperGetDataFromOtherApp.hasSharedData = false;

            if (HelperGetDataFromOtherApp.messageType
                == HelperGetDataFromOtherApp.FileType.message) {
                String message = HelperGetDataFromOtherApp.message;
            } else if (HelperGetDataFromOtherApp.messageType
                == HelperGetDataFromOtherApp.FileType.image) {

            } else if (HelperGetDataFromOtherApp.messageType
                == HelperGetDataFromOtherApp.FileType.video) {

            } else if (HelperGetDataFromOtherApp.messageType
                == HelperGetDataFromOtherApp.FileType.audio) {

            } else if (HelperGetDataFromOtherApp.messageType
                == HelperGetDataFromOtherApp.FileType.file) {

                for (int i = 0; i < HelperGetDataFromOtherApp.messageFileAddress.size(); i++) {

                    HelperGetDataFromOtherApp.FileType fileType =
                        HelperGetDataFromOtherApp.fileTypeArray.get(i);

                    if (fileType == HelperGetDataFromOtherApp.FileType.image) {

                    } else if (fileType == HelperGetDataFromOtherApp.FileType.video) {

                    } else if (fileType == HelperGetDataFromOtherApp.FileType.audio) {

                    } else if (fileType == HelperGetDataFromOtherApp.FileType.file) {

                    }
                }
            }
        }
    }

    private void sheareDataToOtherProgram(StructMessageInfo messageInfo) {

        if (messageInfo == null) return;

        Intent intent = new Intent(Intent.ACTION_SEND);
        String choserDialogText = "";

        if (messageInfo.messageType.toString().equals("TEXT")) {
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, messageInfo.messageText);
        } else if (messageInfo.messageType.toString().equals("CONTACT")) {
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_TEXT,
                messageInfo.userInfo.firstName + "\n" + messageInfo.userInfo.phone);
        } else if (messageInfo.messageType.toString().equals("VOICE")
            || messageInfo.messageType.toString().equals("AUDIO")
            || messageInfo.messageType.toString().equals("AUDIO_TEXT")) {
            intent.setType("audio/*");
            intent.putExtra(Intent.EXTRA_STREAM,
                Uri.fromFile(new File(messageInfo.getAttachment().getLocalFilePath())));
            choserDialogText = "Share audio file";
        } else if (messageInfo.messageType.toString().equals("IMAGE")
            || messageInfo.messageType.toString().equals("IMAGE_TEXT")) {
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM,
                Uri.fromFile(new File(messageInfo.getAttachment().getLocalFilePath())));
            choserDialogText = "Share image";
        } else if (messageInfo.messageType.toString().equals("VIDEO")
            || messageInfo.messageType.toString().equals("VIDEO_TEXT")) {
            intent.setType("video/*");
            intent.putExtra(Intent.EXTRA_STREAM,
                Uri.fromFile(new File(messageInfo.getAttachment().getLocalFilePath())));
            choserDialogText = "Share video file";
        } else if (messageInfo.messageType.toString().equals("FILE")
            || messageInfo.messageType.toString().equals("FILE_TEXT")) {
            Uri uri = Uri.fromFile(new File(messageInfo.getAttachment().getLocalFilePath()));

            String mimeType = null;
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                ContentResolver cr = getContentResolver();
                mimeType = cr.getType(uri);
            } else {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                mimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(fileExtension.toLowerCase());
            }

            if (mimeType == null || mimeType.length() < 1) {
                mimeType = "*/*";
            }

            intent.setType(mimeType);
            Log.e("ddd", mimeType + "");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            choserDialogText = "Share  file";
        }

        startActivity(Intent.createChooser(intent, choserDialogText));
    }

    private void setAvatar() {
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
            .equalTo(RealmRegisteredInfoFields.ID, chatPeerId)
            .findFirst();
        if (realmRegisteredInfo != null
            && realmRegisteredInfo.getAvatar() != null
            && realmRegisteredInfo.getLastAvatar() != null) {

            String mainFilePath = realmRegisteredInfo.getLastAvatar().getFile().getLocalFilePath();

            if (mainFilePath != null && new File(
                mainFilePath).exists()) { // if main image is exist showing that
                avatarPath = mainFilePath;
            } else {
                avatarPath = realmRegisteredInfo.getLastAvatar().getFile().getLocalThumbnailPath();
            }
        }

        //Set Avatar For Chat,Group,Channel
        if (avatarPath != null) {
            File imgFile = new File(avatarPath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imvUserPicture.setImageBitmap(myBitmap);
            } else {
                if (realmRegisteredInfo != null
                    && realmRegisteredInfo.getLastAvatar() != null
                    && realmRegisteredInfo.getLastAvatar().getFile() != null) {
                    onRequestDownloadAvatar(realmRegisteredInfo.getLastAvatar().getFile());
                }
                imvUserPicture.setImageBitmap(
                    com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture(
                        (int) imvUserPicture.getContext().getResources().getDimension(R.dimen.dp60),
                        initialize, color));
            }
        } else {
            if (realmRegisteredInfo != null
                && realmRegisteredInfo.getLastAvatar() != null
                && realmRegisteredInfo.getLastAvatar().getFile() != null) {
                onRequestDownloadAvatar(realmRegisteredInfo.getLastAvatar().getFile());
            }
            imvUserPicture.setImageBitmap(
                com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture(
                    (int) imvUserPicture.getContext().getResources().getDimension(R.dimen.dp60),
                    initialize, color));
        }
        realm.close();
    }

    private void setAvatarGroup() {

        Bitmap bitmap = null;
        RealmAttachment realmAttachment = null;

        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom =
            realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null) {

            if (realmRoom.getGroupRoom() != null) {
                if (realmRoom.getGroupRoom().getAvatar() != null) {
                    realmAttachment = realmRoom.getGroupRoom().getAvatar().getFile();
                }
            }

            Log.e("ddd", realmAttachment + "");

            if (realmAttachment != null) {

                String mainFilePath = realmAttachment.getLocalFilePath();

                if (mainFilePath != null) {
                    File file = new File(mainFilePath);
                    if (!file.exists()) {
                        mainFilePath = realmAttachment.getLocalThumbnailPath();
                    }
                } else {
                    mainFilePath = realmAttachment.getLocalThumbnailPath();
                }

                if (mainFilePath != null) {
                    File fileb = new File(mainFilePath);
                    if (fileb.exists()) {
                        bitmap = BitmapFactory.decodeFile(fileb.getPath());
                    }
                }
            }

            if (bitmap != null) {
                imvUserPicture.setImageBitmap(bitmap);
            } else {
                imvUserPicture.setImageBitmap(
                    com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture(
                        (int) imvUserPicture.getContext().getResources().getDimension(R.dimen.dp60),
                        initialize, color));
            }
        }

    }

    public void onRequestDownloadAvatar(RealmAttachment file) {
        ProtoFileDownload.FileDownload.Selector selector =
            ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;

        final String filepath = G.DIR_IMAGE_USER
            + "/"
            + file.getToken()
            + "_"
            + System.nanoTime()
            + "_"
            + selector.toString();

        /* if download was successful use this filepath and
         * show image , otherwise if download was not successfully
         * run setAvatar method for doing this process again.
         */

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
                    .equalTo(RealmRegisteredInfoFields.ID, chatPeerId)
                    .findFirst();
                realmRegisteredInfo.getLastAvatar().getFile().setLocalThumbnailPath(filepath);
            }
        });
        realm.close();

        // I don't use offset in getting thumbnail
        String identity = file.getToken()
            + '*'
            + selector.toString()
            + '*'
            + file.getSmallThumbnail().getSize()
            + '*'
            + filepath
            + '*'
            + file.getSmallThumbnail().getSize()
            + '*'
            + "true"
            + '*'
            + "0";// userId don't need here , so i set it with string
        new RequestFileDownload().download(file.getToken(), 0,
            (int) file.getSmallThumbnail().getSize(), selector, identity);
    }

    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        imvSmileButton.setText(drawableResourceId);
    }

    @Override public void onBackPressed() {
        if (mAdapter != null && mAdapter.getSelections().size() > 0) {
            mAdapter.deselect();
        } else if (boomMenuButton.isOpen()) {
            boomMenuButton.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Override public boolean dispatchTouchEvent(MotionEvent event) {

        voiceRecord.dispatchTouchEvent(event);

        return super.dispatchTouchEvent(event);
    }

    private void initAttach() {

        boomMenuButton = (BoomMenuButton) findViewById(R.id.am_boom);

        Drawable[] subButtonDrawables = new Drawable[3];
        int[] drawablesResource = new int[] {
            R.mipmap.am_camera, R.mipmap.am_music, R.mipmap.am_paint, R.mipmap.am_picture,
            R.mipmap.am_document, R.mipmap.am_location, R.mipmap.am_video, R.mipmap.am_file,
            R.mipmap.am_contact
        };
        for (int i = 0; i < 3; i++)
            subButtonDrawables[i] = ContextCompat.getDrawable(G.context, drawablesResource[i]);

        int[][] subButtonColors = new int[3][2];
        for (int i = 0; i < 3; i++) {
            subButtonColors[i][1] = ContextCompat.getColor(G.context, R.color.start_background);
            subButtonColors[i][0] = Util.getInstance().getPressedColor(subButtonColors[i][1]);
        }

        BoomMenuButton.Builder bb = new BoomMenuButton.Builder();

        bb.addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_camera),
            subButtonColors[0], getResources().getString(R.string.am_camera))
            .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_picture),
                subButtonColors[0], getResources().getString(R.string.am_picture))
            .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_video),
                subButtonColors[0], getResources().getString(R.string.am_video))
            .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_music),
                subButtonColors[0], getResources().getString(R.string.am_music))
            .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_document),
                subButtonColors[0], getResources().getString(R.string.am_document))
            .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_file),
                subButtonColors[0], getResources().getString(R.string.am_file))
            .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_paint),
                subButtonColors[0], getResources().getString(R.string.am_paint))
            .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_location),
                subButtonColors[0], getResources().getString(R.string.am_location))
            .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_contact),
                subButtonColors[0], getResources().getString(R.string.am_contact))
            .autoDismiss(true)
            .cancelable(true)
            .boomButtonShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
            .subButtonTextColor(ContextCompat.getColor(G.context, R.color.am_iconFab_black))
            .button(ButtonType.CIRCLE)
            .boom(BoomType.PARABOLA)
            .place(PlaceType.CIRCLE_9_1)
            .subButtonTextColor(ContextCompat.getColor(G.context, R.color.colorAccent))
            .subButtonsShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
            .init(boomMenuButton);
        boomMenuButton.setTextViewColor(getResources().getColor(R.color.am_iconFab_black));

        boomMenuButton.setOnSubButtonClickListener(new BoomMenuButton.OnSubButtonClickListener() {
            @Override public void onClick(int buttonIndex) {

                Log.i("TAG123", "onClick: " + buttonIndex);

                switch (buttonIndex) {

                    case 0:

                        attachFile.requestTakePicture();
                        break;
                    case 1:
                        attachFile.requestOpenGallery();
                        break;
                    case 2:
                        attachFile.requestVideoCapture();
                        break;
                    case 3:
                        attachFile.requestPickAudio();
                        break;
                    case 4:
                        Log.i("TAG12", "onClick: " + buttonIndex);
                        break;
                    case 5:
                        attachFile.requestPickFile();
                        break;
                    case 6:
                        attachFile.requestPaint();
                        break;
                    case 7:
                        attachFile.requestGetPosition(complete);
                        break;
                    case 8:
                        attachFile.requestPickContact();
                        break;
                }
            }
        });

        boomMenuButton.setTextViewColor(
            ContextCompat.getColor(G.context, R.color.am_iconFab_black));
    }

    private boolean userTriesReplay() {
        return mReplayLayout != null && mReplayLayout.getTag() instanceof StructMessageInfo;
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AttachFile.request_code_position && locationManager.isProviderEnabled(
            LocationManager.GPS_PROVIDER)) {
            attachFile.requestGetPosition(complete);
            return;
        }

        if (AttachFile.request_code_TAKE_PICTURE == requestCode) {
            latestFilePath = AttachFile.imagePath;
            latestUri = null;
        } else {
            latestUri = data.getData();
            latestFilePath = "";
        }
        latestRequestCode = requestCode;

        if (resultCode == Activity.RESULT_OK
            && sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 0) == 1
            && requestCode == AttachFile.request_code_media_from_gallery) {

            Intent intent = new Intent(ActivityChat.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", data.getData().toString());
            intent.putExtra("TYPE", "gallery");
            intent.putExtra("PAGE", "chat");
            startActivityForResult(intent, IntentRequests.REQ_CROP);

            return;
        } else if (resultCode == Activity.RESULT_OK
            && sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 0) == 1
            && requestCode == AttachFile.request_code_TAKE_PICTURE) {

            Intent intent = new Intent(ActivityChat.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", AttachFile.imagePath);
            intent.putExtra("TYPE", "camera");
            intent.putExtra("PAGE", "chat");
            startActivityForResult(intent, IntentRequests.REQ_CROP);
            return;
        } else if (resultCode == Activity.RESULT_OK
            && ll_attach_text.getVisibility() == View.GONE) {
            latestRequestCode = requestCode;

            showDraftLayout();

            setDraftMessage(requestCode);

            return;
        }
    }

    private void setDraftMessage(int requestCode) {
        switch (requestCode) {
            case AttachFile.request_code_TAKE_PICTURE:
                txtFileNameForSend.setText("Send Picture From Camera");
                break;
            case AttachFile.request_code_media_from_gallery:
                txtFileNameForSend.setText("Send Picture");
                break;
            //                case AttachFile.request_code_media_from_gallery:
            case AttachFile.request_code_VIDEO_CAPTURED:
            case AttachFile.request_code_pic_audi:
                txtFileNameForSend.setText("Send Media");
                break;
            case AttachFile.request_code_pic_file:
            case AttachFile.request_code_paint:
                txtFileNameForSend.setText("Send Paint");
                break;
            case AttachFile.request_code_contact_phone:
                txtFileNameForSend.setText("Send Phone Contact");
                break;
            case IntentRequests.REQ_CROP:
                txtFileNameForSend.setText("Crop Image");
                break;
        }
    }

    //TODO [Saeed Mozaffari] [2016-10-29 10:45 AM] - work on gps
    private void sendMessage(int requestCode, Uri uri) {
        // TODO: 10/30/2016 [Alireza]  test
        final long messageId = System.nanoTime();
        String filePath;
        if (AttachFile.request_code_TAKE_PICTURE == requestCode) {
            filePath = AttachFile.imagePath;
        } else {
            Log.i("YYY", "uri : " + uri);
            filePath = uri.toString();
            Log.i("YYY", "filePath uri: " + filePath);
        }
        final long updateTime = System.currentTimeMillis();
        ProtoGlobal.RoomMessageType messageType = null;
        String fileName = null;
        long duration = 0;
        long fileSize = 0;
        int[] imageDimens = { 0, 0 };
        Realm realm = Realm.getDefaultInstance();
        final long senderID = realm.where(RealmUserInfo.class).findFirst().getUserId();
        StructMessageInfo messageInfo = null;

        switch (requestCode) {
            case IntentRequests.REQ_CROP:
                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                }
                if (userTriesReplay()) {
                    messageInfo =
                        new StructMessageInfo(Long.toString(messageId), Long.toString(senderID),
                            ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType,
                            MyType.SendType.send, null, filePath, updateTime,
                            Long.parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo =
                        new StructMessageInfo(Long.toString(messageId), getWrittenMessage(),
                            Long.toString(senderID),
                            ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType,
                            MyType.SendType.send, null, filePath,
                            updateTime);
                }
                break;
            case AttachFile.request_code_TAKE_PICTURE:

                filePath = AttachFile.imagePath;
                resizeImage(filePath);

                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                }
                if (userTriesReplay()) {
                    messageInfo =
                        new StructMessageInfo(Long.toString(messageId), Long.toString(senderID),
                            ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType,
                            MyType.SendType.send, null, filePath, updateTime,
                            Long.parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo =
                        new StructMessageInfo(Long.toString(messageId), getWrittenMessage(),
                            Long.toString(senderID),
                            ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType,
                            MyType.SendType.send, null, filePath,
                            updateTime);
                }

                break;

            case AttachFile.request_code_media_from_gallery:
                filePath = AttachFile.getFilePathFromUri(uri);
                resizeImage(filePath);

                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                }
                if (userTriesReplay()) {
                    messageInfo =
                        new StructMessageInfo(Long.toString(messageId), Long.toString(senderID),
                            ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType,
                            MyType.SendType.send, null, filePath, updateTime,
                            Long.parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo =
                        new StructMessageInfo(Long.toString(messageId), getWrittenMessage(),
                            Long.toString(senderID),
                            ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType,
                            MyType.SendType.send, null, filePath,
                            updateTime);
                }
                break;

            case AttachFile.request_code_VIDEO_CAPTURED:
                filePath = AttachFile.getFilePathFromUri(uri);
                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                duration = AndroidUtils.getAudioDuration(getApplicationContext(), filePath);
                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.VIDEO_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.VIDEO;
                }
                File videoFile = new File(filePath);
                String videoFileName = videoFile.getName();
                String videoFileMime = FileUtils.getMimeType(videoFile);
                if (userTriesReplay()) {
                    messageInfo =
                        new StructMessageInfo(Long.toString(messageId), Long.toString(senderID),
                            ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType,
                            MyType.SendType.send, videoFileMime,
                            filePath, null, filePath, null, updateTime,
                            Long.parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo =
                        new StructMessageInfo(Long.toString(messageId), Long.toString(senderID),
                            getWrittenMessage(), ProtoGlobal.RoomMessageStatus.SENDING.toString(),
                            messageType, MyType.SendType.send, videoFileMime, filePath, null,
                            filePath, null, updateTime);
                }
                break;
            case AttachFile.request_code_pic_audi:
                filePath = AttachFile.getFilePathFromUri(uri);
                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                duration = AndroidUtils.getAudioDuration(getApplicationContext(), filePath);
                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.AUDIO_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.AUDIO;
                }
                String songArtist = AndroidUtils.getAudioArtistName(filePath);
                long songDuration =
                    AndroidUtils.getAudioDuration(getApplicationContext(), filePath);

                messageInfo = StructMessageInfo.buildForAudio(messageId, senderID,
                    ProtoGlobal.RoomMessageStatus.SENDING, messageType, MyType.SendType.send,
                    updateTime, getWrittenMessage(), null, filePath, songArtist, songDuration,
                    userTriesReplay() ? Long.parseLong(
                        ((StructMessageInfo) mReplayLayout.getTag()).messageID) : 0);
                break;
            case AttachFile.request_code_pic_file:
                fileName = new File(filePath).getName();
                fileSize = new File(filePath).length();
                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.FILE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.FILE;
                }
                File fileFile = new File(filePath);
                String fileFileName = fileFile.getName();
                String fileFileMime = FileUtils.getMimeType(fileFile);
                if (userTriesReplay()) {
                    messageInfo =
                        new StructMessageInfo(Long.toString(messageId), Long.toString(senderID),
                            ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType,
                            MyType.SendType.send, fileFileMime,
                            filePath, null, filePath, null, updateTime,
                            Long.parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo =
                        new StructMessageInfo(Long.toString(messageId), Long.toString(senderID),
                            getWrittenMessage(), ProtoGlobal.RoomMessageStatus.SENDING.toString(),
                            messageType, MyType.SendType.send, fileFileMime, filePath, null,
                            filePath, null, updateTime);
                }
                break;
            case AttachFile.request_code_contact_phone:
                ContactUtils contactUtils = new ContactUtils(getApplicationContext(), uri);
                String name = contactUtils.retrieveName();
                String number = contactUtils.retrieveNumber();
                // FIXME: 10/5/2016 [Alireza] get username
                String username = "username";
                Uri imageUri = contactUtils.getPhotoUri();
                String image = null;
                if (imageUri != null) {
                    image = imageUri.toString();
                }
                messageType = ProtoGlobal.RoomMessageType.CONTACT;
                // FIXME: 10/18/2016 [Alireza] lastName "" gozashtam jash, firstName esme kamele
                messageInfo =
                    StructMessageInfo.buildForContact(messageId, senderID, MyType.SendType.send,
                        updateTime, ProtoGlobal.RoomMessageStatus.SENDING, username, name,
                        "", number, userTriesReplay() ? Long.parseLong(
                            ((StructMessageInfo) mReplayLayout.getTag()).messageID) : -1);
                break;
            case AttachFile.request_code_paint:
                fileName = new File(filePath).getName();

                imageDimens = AndroidUtils.getImageDimens(filePath);
                if (isMessageWrote()) {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE_TEXT;
                } else {
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                }
                if (userTriesReplay()) {
                    messageInfo =
                        new StructMessageInfo(Long.toString(messageId), Long.toString(senderID),
                            ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType,
                            MyType.SendType.send, null, filePath, updateTime,
                            Long.parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID));
                } else {
                    messageInfo =
                        new StructMessageInfo(Long.toString(messageId), getWrittenMessage(),
                            Long.toString(senderID),
                            ProtoGlobal.RoomMessageStatus.SENDING.toString(), messageType,
                            MyType.SendType.send, null, filePath,
                            updateTime);
                }
                break;
        }

        final ProtoGlobal.RoomMessageType finalMessageType = messageType;
        final String finalFilePath = filePath;
        final String finalFileName = fileName;
        final long finalDuration = duration;
        final long finalFileSize = fileSize;
        final int[] finalImageDimens = imageDimens;

        final StructMessageInfo finalMessageInfo = messageInfo;
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class);

                roomMessage.setMessageType(finalMessageType.toString());
                roomMessage.setMessage(getWrittenMessage());
                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                roomMessage.setRoomId(mRoomId);
                roomMessage.setAttachment(messageId, finalFilePath, finalImageDimens[0],
                    finalImageDimens[1], finalFileSize, finalFileName, finalDuration,
                    LocalFileType.THUMBNAIL);
                roomMessage.setMessageId(messageId);
                roomMessage.setUserId(senderID);
                roomMessage.setUpdateTime(updateTime);

                if (finalMessageType == ProtoGlobal.RoomMessageType.CONTACT) {
                    RealmRoomMessageContact realmRoomMessageContact =
                        realm.createObject(RealmRoomMessageContact.class);
                    realmRoomMessageContact.setId(System.nanoTime());
                    realmRoomMessageContact.setFirstName(finalMessageInfo.userInfo.firstName);
                    realmRoomMessageContact.setLastName(finalMessageInfo.userInfo.lastName);
                    realmRoomMessageContact.addPhone(finalMessageInfo.userInfo.phone);
                    roomMessage.setRoomMessageContact(realmRoomMessageContact);
                }

                // TODO: 9/26/2016 [Alireza Eskandarpour Shoferi] user may wants to send a file
                // in response to a message as replay, so after server done creating replay and
                // forward options, modify this section and sending message as well.

                if (finalMessageType != ProtoGlobal.RoomMessageType.CONTACT) {
                    finalMessageInfo.attachment =
                        StructMessageAttachment.convert(roomMessage.getAttachment());
                }

                switchAddItem(new ArrayList<>(Arrays.asList(finalMessageInfo)), false);

                if (finalFilePath != null
                    && finalMessageType != ProtoGlobal.RoomMessageType.CONTACT) {
                    new UploadTask().execute(finalFilePath, messageId, finalMessageType, mRoomId,
                        getWrittenMessage());
                } else {
                    ChatSendMessageUtil messageUtil =
                        new ChatSendMessageUtil().newBuilder(chatType, finalMessageType, mRoomId)
                            .message(getWrittenMessage());
                    if (finalMessageType == ProtoGlobal.RoomMessageType.CONTACT) {
                        // FIXME: 10/5/2016 [Alireza] retrieve last name
                        messageUtil.contact(finalMessageInfo.userInfo.firstName,
                            finalMessageInfo.userInfo.lastName, finalMessageInfo.userInfo.phone);
                    }
                    messageUtil.sendMessage(Long.toString(messageId));
                }
            }
        });

        realm.close();

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
            stubView.setLayoutResource(R.layout.layout_chat_replay);
            stubView.inflate();

            inflateReplayLayoutIntoStub(chatItem);
        } else {
            mReplayLayout = (LinearLayout) findViewById(R.id.replayLayoutAboveEditText);
            mReplayLayout.setVisibility(View.VISIBLE);
            TextView replayTo = (TextView) mReplayLayout.findViewById(R.id.replayTo);
            replayTo.setText(chatItem.messageText);
            // I set tag to retrieve it later when sending message
            mReplayLayout.setTag(chatItem);
        }
    }

    @Override public void onEmojiViewCreate(View view, EmojiPopup emojiPopup) {

    }

    @Override public boolean onRecentsLongClick(View view, EmojiRecentsManager recentsManager) {
        // TODO useful for clearing recents
        return false;
    }

    private Intent makeIntentForForwardMessages(ArrayList<Parcelable> messageInfos) {
        Intent intent = new Intent(ActivityChat.this, ActivitySelectChat.class);
        intent.putParcelableArrayListExtra(ActivitySelectChat.ARG_FORWARD_MESSAGE, messageInfos);

        return intent;
    }

    private Intent makeIntentForForwardMessages(StructMessageInfo messageInfos) {
        return makeIntentForForwardMessages(
            new ArrayList<>(Arrays.asList(Parcels.wrap(messageInfos))));
    }

    private void replay(StructMessageInfo item) {
        if (mAdapter != null) {
            Set<AbstractMessage> messages = mAdapter.getSelectedItems();
            // replay works if only one message selected
            inflateReplayLayoutIntoStub(item == null ? messages.iterator().next().mMessage : item);

            ll_AppBarSelected.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);

            mAdapter.deselect();
        }
    }

    private void initAppbarSelected() {

        btnCloseAppBarSelected = (MaterialDesignTextView) findViewById(R.id.chl_btn_close_layout);
        RippleView rippleCloseAppBarSelected =
            (RippleView) findViewById(R.id.chl_ripple_close_layout);
        rippleCloseAppBarSelected.setOnRippleCompleteListener(
            new RippleView.OnRippleCompleteListener() {
                @Override public void onComplete(RippleView rippleView) {
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
        RippleView rippleReplaySelected = (RippleView) findViewById(R.id.chl_ripple_close_layout);
        rippleReplaySelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                Log.e("ddd", "btnReplaySelected");
                // FIXME: 10/31/2016 [Alireza] inja nabayad null bashe
                replay(null);
            }
        });
        btnCopySelected = (MaterialDesignTextView) findViewById(R.id.chl_btn_copy_selected);
        RippleView rippleCopySelected = (RippleView) findViewById(R.id.chl_ripple_copy_selected);
        rippleCopySelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                for (AbstractMessage messageID : mAdapter.getSelectedItems()) {////TODO [Saeed
                    // Mozaffari] [2016-09-13 6:39 PM] - code is wrong
                    ClipboardManager clipboard =
                        (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip =
                        ClipData.newPlainText("Copied Text", messageID.mMessage.messageText);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(G.context, "Text Copied", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnForwardSelected = (MaterialDesignTextView) findViewById(R.id.chl_btn_forward_selected);
        RippleView rippleForwardSelected =
            (RippleView) findViewById(R.id.chl_ripple_forward_selected);
        rippleForwardSelected.setOnRippleCompleteListener(
            new RippleView.OnRippleCompleteListener() {
                @Override public void onComplete(RippleView rippleView) {
                Log.e("ddd", "btnForwardSelected");
                // forward selected messages to room list for selecting room
                if (mAdapter != null && mAdapter.getSelectedItems().size() > 0) {
                    startActivity(
                        makeIntentForForwardMessages(getMessageStructFromSelectedItems()));
                }
            }
        });
        btnDeleteSelected = (MaterialDesignTextView) findViewById(R.id.chl_btn_delete_selected);
        RippleView rippleDeleteSelected =
            (RippleView) findViewById(R.id.chl_ripple_delete_selected);
        rippleDeleteSelected.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {

            }
        });
        btnDeleteSelected.setOnClickListener(
            new View.OnClickListener() { //TODO [Saeed Mozaffari] [2016-09-17 2:58 PM] - FORCE -
                // add item to delete list
                @Override public void onClick(View view) {

                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override public void execute(Realm realm) {
                            // get offline delete list , add new deleted list and update in
                            // client condition , then send request for delete message to server
                            RealmClientCondition realmClientCondition =
                                realm.where(RealmClientCondition.class)
                                    .equalTo(RealmClientConditionFields.ROOM_ID, mRoomId)
                                    .findFirst();

                            for (final AbstractMessage messageID : mAdapter.getSelectedItems()) {
                                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class)
                                    .equalTo(RealmRoomMessageFields.MESSAGE_ID,
                                        Long.parseLong(messageID.mMessage.messageID))
                                    .findFirst();
                                if (roomMessage != null) {
                                    // delete message from database
                                    roomMessage.deleteFromRealm();
                                }

                                RealmOfflineDelete realmOfflineDelete =
                                    realm.createObject(RealmOfflineDelete.class);
                                realmOfflineDelete.setId(System.nanoTime());
                                realmOfflineDelete.setOfflineDelete(
                                    Long.parseLong(messageID.mMessage.messageID));

                                realmClientCondition.getOfflineDeleted().add(realmOfflineDelete);

                                runOnUiThread(new Runnable() {
                                    @Override public void run() {
                                        // remove deleted message from adapter
                                        mAdapter.removeMessage(
                                            Long.parseLong(messageID.mMessage.messageID));

                                        // remove tag from edtChat if the message has deleted
                                        if (edtChat.getTag() != null
                                            && edtChat.getTag() instanceof StructMessageInfo) {
                                            if (messageID.mMessage.messageID.equals(
                                                ((StructMessageInfo) edtChat.getTag()).messageID)) {
                                                edtChat.setTag(null);
                                            }
                                        }
                                    }
                                });
                                new RequestChatDeleteMessage().chatDeleteMessage(mRoomId,
                                    Long.parseLong(messageID.mMessage.messageID));
                            }
                        }
                    });
                    realm.close();
                }
            });

        txtNumberOfSelected = (TextView) findViewById(R.id.chl_txt_number_of_selected);
        txtNumberOfSelected.setTypeface(G.fontawesome);

        ll_AppBarSelected = (LinearLayout) findViewById(R.id.chl_ll_appbar_selelected);
    }

    private ArrayList<Parcelable> getMessageStructFromSelectedItems() {
        ArrayList<Parcelable> messageInfos = new ArrayList<>(mAdapter.getSelectedItems().size());
        for (AbstractMessage item : mAdapter.getSelectedItems()) {
            messageInfos.add(Parcels.wrap(item.mMessage));
        }
        return messageInfos;
    }

    private void initLayotChannelFooter() {

        LinearLayout layoutAttach = (LinearLayout) findViewById(R.id.chl_ll_attach);
        RelativeLayout layoutChannelFooter =
            (RelativeLayout) findViewById(R.id.chl_ll_channel_footer);

        layoutAttach.setVisibility(View.GONE);
        layoutChannelFooter.setVisibility(View.VISIBLE);

        btnUp = (Button) findViewById(R.id.chl_btn_up);
        btnUp.setTypeface(G.fontawesome);
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Log.e("ddd", "up click");
                int position = recyclerView.getAdapter().getItemCount();
                if (position > 0) recyclerView.scrollToPosition(0);
            }
        });

        btnDown = (Button) findViewById(R.id.chl_btn_down);
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Log.e("ddd", "btnDown");
                int position = recyclerView.getAdapter().getItemCount();
                if (position > 0) recyclerView.scrollToPosition(position - 1);
            }
        });

        txtChannelMute = (TextView) findViewById(R.id.chl_txt_mute_channel);
        txtChannelMute.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                isMute = !isMute;
                if (isMute) {
                    txtChannelMute.setText("UnMute");
                    txt_mute.setVisibility(View.VISIBLE);
                } else {
                    txtChannelMute.setText("Mute");
                    txt_mute.setVisibility(View.GONE);
                }
            }
        });

        if (isMute) {
            txtChannelMute.setText("UnMute");
        } else {
            txtChannelMute.setText("Mute");
        }
    }

    private ArrayList<StructMessageInfo> getChatList() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<RealmRoomMessage> realmRoomMessages = new ArrayList<>();
        // get all RealmRoomMessages
        for (RealmRoomMessage realmRoomMessage : realm.where(RealmRoomMessage.class)
            .equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId)
            .findAll()) {
            if (realmRoomMessage != null) {
                realmRoomMessages.add(realmRoomMessage);
            }
        }

        Collections.sort(realmRoomMessages, SortMessages.ASC);

        List<RealmRoomMessage> lastResultMessages = new ArrayList<>();

        for (RealmRoomMessage message : realmRoomMessages) {
            String timeString = getTimeSettingMessage(message.getUpdateTime());
            if (timeString != null) {
                RealmRoomMessage timeMessage = new RealmRoomMessage();
                timeMessage.setMessageId(System.currentTimeMillis());
                // -1 means time message
                timeMessage.setUserId(-1);
                timeMessage.setUpdateTime(message.getUpdateTime() - 1L);
                timeMessage.setMessage(timeString);
                timeMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT.toString());
                lastResultMessages.add(timeMessage);
            }

            lastResultMessages.add(message);
        }

        Collections.sort(lastResultMessages, SortMessages.DESC);

        EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener =
            new EndlessRecyclerOnScrollListener(lastResultMessages, mAdapter) {
                @Override
                public void onLoadMore(EndlessRecyclerOnScrollListener listener, int page) {
                    List<RealmRoomMessage> roomMessages = listener.loadMore(page);
                    for (RealmRoomMessage roomMessage : roomMessages) {
                        StructMessageInfo messageInfo = StructMessageInfo.convert(roomMessage);
                        switchAddItem(new ArrayList<>(Arrays.asList(messageInfo)), true);
                    }
                }

                @Override public void onNoMore(EndlessRecyclerOnScrollListener listener) {
                    // find last item from adapter (not database for better performance!)
                    // TODO: 10/17/2016 [Alireza] todo something to not request everytime
                    for (int p = mAdapter.getAdapterItemCount() - 1; p >= 0; p--) {
                        AbstractMessage item = mAdapter.getAdapterItem(p);
                        // not time message
                        if (!item.mMessage.senderID.equalsIgnoreCase("-1")) {
                            // new RequestClientGetRoomHistory().getRoomHistory(mRoomId, Long
                            // .parseLong(item.mMessage.messageID), Long.toString(mRoomId));
                            break;
                        }
                    }
                }
            };

        recyclerView.addOnScrollListener(
            new RecyclerViewPauseOnScrollListener(ImageLoader.getInstance(), false, true,
                endlessRecyclerOnScrollListener));

        ArrayList<StructMessageInfo> messageInfos = new ArrayList<>();
        for (RealmRoomMessage realmRoomMessage : endlessRecyclerOnScrollListener.loadMore(0)) {
            messageInfos.add(StructMessageInfo.convert(realmRoomMessage));
        }

        realm.close();

        return messageInfos;
    }

    private String getTimeSettingMessage(long comingDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(comingDate);

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        long diff = Math.abs(calendar.getTimeInMillis() - lastDateCalendar.getTimeInMillis());

        if (diff + 1000 > DateUtils.DAY_IN_MILLIS) {
            lastDateCalendar.setTimeInMillis(calendar.getTimeInMillis());
            return TimeUtils.getChatSettingsTimeAgo(this, calendar.getTime());
        }

        return null;
    }

    @Override
    public void onSenderAvatarClick(View view, StructMessageInfo messageInfo, int position) {
        Intent intent = new Intent(G.context, ActivityContactsProfile.class);
        intent.putExtra("peerId", Long.parseLong(messageInfo.senderID));
        intent.putExtra("RoomId", mRoomId);
        intent.putExtra("enterFrom", GROUP.toString());
        startActivity(intent);
    }

    private void showImage(StructMessageInfo messageInfo) {
        FragmentShowImageMessages fragment =
            FragmentShowImageMessages.newInstance(mRoomId, messageInfo.attachment.token);
        getSupportFragmentManager().beginTransaction().replace(R.id.ac_ll_parent, fragment, null)
            .commit();
    }

    @Override
    public void onChatClearMessage(long roomId, long clearId, ProtoResponse.Response response) {

        boolean clearMessage = false;

        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class)
            .equalTo(RealmRoomMessageFields.ROOM_ID, roomId)
            .findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
        for (final RealmRoomMessage realmRoomMessage : realmRoomMessages) {
            if (!clearMessage && realmRoomMessage.getMessageId() == clearId) {
                clearMessage = true;
            }

            if (clearMessage) {
                final long messageId = realmRoomMessage.getMessageId();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        realmRoomMessage.deleteFromRealm();
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        // remove deleted message from adapter
                        Log.i("CLEAR", "mAdapter.removeMessage");
                        mAdapter.removeMessage(messageId);

                        // remove tag from edtChat if the message has deleted
                        if (edtChat.getTag() != null
                            && edtChat.getTag() instanceof StructMessageInfo) {
                            if (Long.toString(messageId)
                                .equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
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
            @Override public void run() {
                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
            }
        }, 300);
    }

    @Override public void onChatUpdateStatus(long roomId, final long messageId,
        final ProtoGlobal.RoomMessageStatus status, long statusVersion) {
        Log.i(ActivityChat.class.getSimpleName(), "onChatUpdateStatus called");

        // I'm in the room
        if (mRoomId == roomId) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    // so update the message status ina adapter
                    mAdapter.updateMessageStatus(messageId, status);
                    Log.i(ActivityChat.class.getSimpleName(), status.toString());
                }
            });
        }
    }

    @Override public void onChatMessageSelectionChanged(int selectedCount,
        Set<AbstractMessage> selectedItems) {
        Toast.makeText(ActivityChat.this, "selected: " + Integer.toString(selectedCount),
            Toast.LENGTH_SHORT).show();
        if (selectedCount > 0) {
            toolbar.setVisibility(View.GONE);

            txtNumberOfSelected.setText(Integer.toString(selectedCount));

            if (selectedCount > 1) {
                btnReplaySelected.setVisibility(View.INVISIBLE);
            } else {
                btnReplaySelected.setVisibility(View.VISIBLE);
            }

            ll_AppBarSelected.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.VISIBLE);
            ll_AppBarSelected.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPreChatMessageRemove(final StructMessageInfo messageInfo, int position) {
        if (mAdapter.getAdapterItemCount() > 1 && position == mAdapter.getAdapterItemCount() - 1) {
            // if was last message removed
            // update room last message
            final Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class)
                        .equalTo(RealmRoomFields.ID, mRoomId)
                        .findFirst();

                    AbstractMessage lastMessageBeforeDeleted =
                        mAdapter.getAdapterItem(mAdapter.getAdapterItemCount() - 1);
                    if (lastMessageBeforeDeleted != null) {
                        realmRoom.setLastMessageId(
                            Long.parseLong(lastMessageBeforeDeleted.mMessage.messageID));
                        realmRoom.setLastMessageTime((int) (lastMessageBeforeDeleted.mMessage.time
                            / DateUtils.SECOND_IN_MILLIS));

                        realm.copyToRealmOrUpdate(realmRoom);
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override public void onSuccess() {
                    realm.close();
                }
            });
        }
    }

    @Override public void onMessageUpdate(long roomId, final long messageId,
        final ProtoGlobal.RoomMessageStatus status, final String identity,
        ProtoGlobal.RoomMessage roomMessage) {
        // I'm in the room
        if (roomId == mRoomId) {
            // update message status in telegram
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    mAdapter.updateMessageIdAndStatus(messageId, identity, status);
                }
            });
        }
    }

    @Override public void onMessageReceive(final long roomId, String message, String messageType,
        final ProtoGlobal.RoomMessage roomMessage, final ProtoGlobal.Room.Type roomType) {
        Log.i(ActivityChat.class.getSimpleName(), "onMessageReceive called for group");

        final Realm realm = Realm.getDefaultInstance();

        if (roomMessage.getUserId() != realm.where(RealmUserInfo.class).findFirst().getUserId()) {
            // I'm in the room
            if (roomId == mRoomId) {
                // I'm in the room, so unread messages count is 0. it means, I read all messages
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        RealmRoom room = realm.where(RealmRoom.class)
                            .equalTo(RealmRoomFields.ID, mRoomId)
                            .findFirst();
                        if (room != null) {
                            room.setUnreadCount(0);
                            realm.copyToRealmOrUpdate(room);
                        }
                    }
                });

                // when user receive message, I send update status as SENT to the message sender
                // but imagine user is not in the room (or he is in another room) and received
                // some messages
                // when came back to the room with new messages, I make new update status request
                // as SEEN to
                // the message sender
                final RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class)
                    .equalTo(RealmRoomMessageFields.MESSAGE_ID, roomMessage.getMessageId())
                    .findFirst();
                //Start ClientCondition OfflineSeen
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        final RealmClientCondition realmClientCondition =
                            realm.where(RealmClientCondition.class)
                                .equalTo(RealmClientConditionFields.ROOM_ID, mRoomId)
                                .findFirst();

                        if (realmRoomMessage != null) {
                            if (!realmRoomMessage.getStatus()
                                .equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SEEN.toString())) {
                                realmRoomMessage.setStatus(
                                    ProtoGlobal.RoomMessageStatus.SEEN.toString());

                                RealmOfflineSeen realmOfflineSeen =
                                    realm.createObject(RealmOfflineSeen.class);
                                realmOfflineSeen.setId(System.nanoTime());
                                realmOfflineSeen.setOfflineSeen(realmRoomMessage.getMessageId());
                                realm.copyToRealmOrUpdate(realmOfflineSeen);

                                Log.i(RealmClientCondition.class.getSimpleName(),
                                    "before size: " + realmClientCondition.getOfflineSeen().size());

                                realmClientCondition.getOfflineSeen().add(realmOfflineSeen);

                                Log.i(RealmClientCondition.class.getSimpleName(),
                                    "after size: " + realmClientCondition.getOfflineSeen().size());
                            }
                        }

                        // make update status to message sender that i've read his message
                        if (roomType == CHAT) {
                            G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId,
                                roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                        } else if (roomType == GROUP && (roomMessage.getStatus()
                            == ProtoGlobal.RoomMessageStatus.SENT
                            || roomMessage.getStatus()
                            == ProtoGlobal.RoomMessageStatus.DELIVERED)) {
                            G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId,
                                roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                        }
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        switchAddItem(
                            new ArrayList<>(Arrays.asList(StructMessageInfo.convert(roomMessage))),
                            false);
                        scrollToEnd();
                    }
                });
            } else {
                // user has received the message, so I make a new delivered update status request
                if (roomType == CHAT) {
                    G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId,
                        roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
                } else if (roomType == GROUP
                    && roomMessage.getStatus() == ProtoGlobal.RoomMessageStatus.SENT) {
                    G.chatUpdateStatusUtil.sendUpdateStatus(roomType, roomId,
                        roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
                }
                // I'm not in the room, but I have to add 1 to unread messages count
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        RealmRoom room = realm.where(RealmRoom.class)
                            .equalTo(RealmRoomFields.ID, mRoomId)
                            .findFirst();
                        if (room != null) {
                            room.setUnreadCount(room.getUnreadCount() + 1);
                            realm.copyToRealmOrUpdate(room);
                        }
                    }
                });
            }
        } else {

            // I'm sender . but another account sent this message and i received it.
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    switchAddItem(
                        new ArrayList<>(Arrays.asList(StructMessageInfo.convert(roomMessage))),
                        false);
                    scrollToEnd();
                }
            });
        }

        realm.close();
    }

    @Override public void onMessageFailed(long roomId, RealmRoomMessage message,
        ProtoGlobal.Room.Type roomType) {
        if (roomId == mRoomId) {
            mAdapter.updateMessageStatus(message.getMessageId(),
                ProtoGlobal.RoomMessageStatus.FAILED);
        }
    }

    @Override public void onFileDownload(final String token, final int offset,
        final ProtoFileDownload.FileDownload.Selector selector, final int progress) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                // if thumbnail
                if (selector != ProtoFileDownload.FileDownload.Selector.FILE) {
                    mAdapter.updateThumbnail(token);
                } else {
                    // else file
                    mAdapter.updateDownloadFields(token, progress, offset);
                }
            }
        });
    }

    @Override public void onAvatarDownload(final String token, final int offset,
        final ProtoFileDownload.FileDownload.Selector selector, final int progress,
        final long userId, final RoomType roomType) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Realm realm = Realm.getDefaultInstance();

                RealmRegisteredInfo realmRegisteredInfo =
                    realm.where(RealmRegisteredInfo.class).equalTo("id", userId).findFirst();

                if (realmRegisteredInfo != null) {
                    mAdapter.downloadingAvatar(userId, progress, offset,
                        StructMessageAttachment.convert(
                            realmRegisteredInfo.getLastAvatar())); //TODO [Saeed Mozaffari]
                    // [2016-11-01 10:10 AM] -we have NullPointerException for
                    // realmRegisteredInfo check this
                }

                realm.close();
            }
        });
    }

    @Override public void onError(int majorCode, int minorCode) {
        if (majorCode == 713 && minorCode == 1) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                        getResources().getString(R.string.E_713_1), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 2) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                        getResources().getString(R.string.E_713_2), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 3) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                        getResources().getString(R.string.E_713_3), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 4) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                        getResources().getString(R.string.E_713_4), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 5) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                        getResources().getString(R.string.E_713_5), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 714) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                        getResources().getString(R.string.E_714), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 715) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                        getResources().getString(R.string.E_715), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        }
    }

    @Override public void onVoiceRecordDone(final String savedPath) {
        Realm realm = Realm.getDefaultInstance();
        final long messageId = System.nanoTime();
        final long updateTime = System.currentTimeMillis();
        final long senderID = realm.where(RealmUserInfo.class).findFirst().getUserId();
        final long duration = AndroidUtils.getAudioDuration(getApplicationContext(), savedPath);
        if (userTriesReplay()) {
            mAdapter.add(new VoiceItem(chatType, this).setMessage(
                new StructMessageInfo(Long.toString(messageId), Long.toString(senderID),
                    ProtoGlobal.RoomMessageStatus.SENDING.toString(),
                    ProtoGlobal.RoomMessageType.VOICE, MyType.SendType.send, null, savedPath,
                    updateTime,
                    Long.parseLong(((StructMessageInfo) mReplayLayout.getTag()).messageID)))
                .withIdentifier(System.nanoTime()));
        } else {
            if (isMessageWrote()) {
                mAdapter.add(new VoiceItem(chatType, this).setMessage(
                    new StructMessageInfo(Long.toString(messageId), Long.toString(senderID),
                        ProtoGlobal.RoomMessageStatus.SENDING.toString(),
                        ProtoGlobal.RoomMessageType.VOICE, MyType.SendType.send, null, savedPath,
                        updateTime))
                    .withIdentifier(System.nanoTime()));
            } else {
                mAdapter.add(new VoiceItem(chatType, this).setMessage(
                    new StructMessageInfo(Long.toString(messageId), Long.toString(senderID),
                        ProtoGlobal.RoomMessageStatus.SENDING.toString(),
                        ProtoGlobal.RoomMessageType.VOICE, MyType.SendType.send, null, savedPath,
                        updateTime))
                    .withIdentifier(System.nanoTime()));
            }
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class);

                roomMessage.setMessageType(ProtoGlobal.RoomMessageType.VOICE.toString());
                roomMessage.setMessage(getWrittenMessage());
                roomMessage.setRoomId(mRoomId);
                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                roomMessage.setAttachment(messageId, savedPath, 0, 0, 0, null, duration,
                    LocalFileType.FILE);
                roomMessage.setMessageId(messageId);
                roomMessage.setUserId(senderID);
                roomMessage.setUpdateTime(updateTime);

                // TODO: 9/26/2016 [Alireza Eskandarpour Shoferi] user may wants to send a file
                // in response to a message as replay, so after server done creating replay and
                // forward options, modify this section and sending message as well.
            }
        });

        new UploadTask().execute(savedPath, messageId, ProtoGlobal.RoomMessageType.VOICE, mRoomId,
            getWrittenMessage());

        scrollToEnd();

        realm.close();
    }

    @Override public void onVoiceRecordCancel() {

    }

    @Override
    public void onUserInfo(final ProtoGlobal.RegisteredUser user, ProtoResponse.Response response) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Realm realm = Realm.getDefaultInstance();
                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
                    .equalTo(RealmRegisteredInfoFields.ID, user.getId())
                    .findFirst();
                if (realmRegisteredInfo != null) {
                    mAdapter.updateChatAvatar(user.getId(), realmRegisteredInfo);
                }
                realm.close();
            }
        });
    }

    @Override public void onUserInfoTimeOut() {

    }

    @Override public void onUserInfoError(int majorCode, int minorCode) {

    }

    @Override public void onGetRoomHistory(final long roomId, String message, String messageType,
        final ProtoGlobal.RoomMessage roomMessage) {
        final Realm realm = Realm.getDefaultInstance();

        // I'm in the room
        if (roomId == mRoomId) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    switchAddItem(
                        new ArrayList<>(Arrays.asList(StructMessageInfo.convert(roomMessage))),
                        false);
                    scrollToEnd();
                }
            });
        }

        realm.close();
    }

    @Override public void onGetRoomHistoryError(int majorCode, int minorCode) {
        if (majorCode == 615 && minorCode == 1) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                        getResources().getString(R.string.E_615_1), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 615 && minorCode == 2) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                        getResources().getString(R.string.E_615_2), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 616) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                        getResources().getString(R.string.E_616), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 617) {
            runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                        getResources().getString(R.string.E_617), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        }
    }

    @Override
    public void onFileUploaded(final FileUploadStructure uploadStructure, final String identity) {
        new ChatSendMessageUtil().newBuilder(chatType, uploadStructure.messageType,
            uploadStructure.roomId)
            .attachment(uploadStructure.token)
            .message(uploadStructure.text)
            .sendMessage(Long.toString(uploadStructure.messageId));

        runOnUiThread(new Runnable() {
            @Override public void run() {
                mAdapter.updateProgress(Long.parseLong(identity), 100);
                mAdapter.updateToken(uploadStructure.messageId, uploadStructure.token);
            }
        });
    }

    @Override
    public void onFileUploading(FileUploadStructure uploadStructure, final String identity,
        final double progress) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                mAdapter.updateProgress(Long.parseLong(identity), (int) progress);
            }
        });
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
            @Override public void execute(Realm realm) {
                realm.where(RealmRoom.class)
                    .equalTo(RealmRoomFields.ID, item)
                    .findFirst()
                    .setMute(isMuteNotification);
            }
        });
        realm.close();
    }
    //    delete & clear History & mutNotification

    private void clearHistory(int item) {
        final long chatId = item;

        // make request for clearing messages
        final Realm realm = Realm.getDefaultInstance();

        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class)
            .equalTo(RealmClientConditionFields.ROOM_ID, chatId)
            .findFirstAsync();
        realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
            @Override public void onChange(final RealmClientCondition element) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        final RealmRoom realmRoom = realm.where(RealmRoom.class)
                            .equalTo(RealmRoomFields.ID, chatId)
                            .findFirst();

                        if (realmRoom.getLastMessageId() != -1) {
                            Log.i("CLI1", "CLEAR RoomId : "
                                + chatId
                                + "  ||  realmRoom.getLastMessageId() : "
                                + realmRoom.getLastMessageId());
                            element.setClearId(realmRoom.getLastMessageId());

                            G.clearMessagesUtil.clearMessages(chatId, realmRoom.getLastMessageId());
                        }

                        RealmResults<RealmRoomMessage> realmRoomMessages =
                            realm.where(RealmRoomMessage.class)
                                .equalTo(RealmRoomMessageFields.ROOM_ID, chatId)
                                .findAll();
                        for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                            if (realmRoomMessage != null) {
                                // delete chat history message
                                realmRoomMessage.deleteFromRealm();
                            }
                        }

                        RealmRoom room = realm.where(RealmRoom.class)
                            .equalTo(RealmRoomFields.ID, chatId)
                            .findFirst();
                        if (room != null) {
                            room.setUnreadCount(0);
                            room.setLastMessageId(0);
                            room.setLastMessageTime(0);
                            room.setLastMessage("");

                            realm.copyToRealmOrUpdate(room);
                        }
                        // finally delete whole chat history
                        realmRoomMessages.deleteAllFromRealm();

                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                if (mAdapter != null) {
                                    mAdapter.clear();
                                }
                            }
                        });
                    }
                });

                element.removeChangeListeners();
                realm.close();
            }
        });
    }

    private void deleteChat(final int item) {
        G.onChatDelete = new OnChatDelete() {
            @Override public void onChatDelete(long roomId) {
                Log.i(ActivityMain.class.getSimpleName(), "chat delete response > " + roomId);
            }

            @Override public void onChatDeleteError(int majorCode, int minorCode) {

                if (majorCode == 218 && minorCode == 1) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_218), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                        }
                    });
                } else if (majorCode == 219) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_219), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                        }
                    });
                } else if (majorCode == 220) {
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content),
                                getResources().getString(R.string.E_220), Snackbar.LENGTH_LONG);

                            snack.setAction("CANCEL", new View.OnClickListener() {
                                @Override public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        };
        Log.i("RRR", "onChatDelete 0 start delete");
        final Realm realm = Realm.getDefaultInstance();
        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class)
            .equalTo(RealmClientConditionFields.ROOM_ID, item)
            .findFirstAsync();
        realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
            @Override public void onChange(final RealmClientCondition element) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(final Realm realm) {
                        if (realm.where(RealmOfflineDelete.class)
                            .equalTo(RealmOfflineDeleteFields.OFFLINE_DELETE, item)
                            .findFirst() == null) {
                            RealmOfflineDelete realmOfflineDelete =
                                realm.createObject(RealmOfflineDelete.class);
                            realmOfflineDelete.setId(System.nanoTime());
                            realmOfflineDelete.setOfflineDelete(item);

                            element.getOfflineDeleted().add(realmOfflineDelete);

                            realm.where(RealmRoom.class)
                                .equalTo(RealmRoomFields.ID, item)
                                .findFirst()
                                .deleteFromRealm();
                            realm.where(RealmRoomMessage.class)
                                .equalTo(RealmRoomMessageFields.ROOM_ID, item)
                                .findAll()
                                .deleteAllFromRealm();

                            new RequestChatDelete().chatDelete(item);
                        }
                    }
                });
                element.removeChangeListeners();
                realm.close();
                finish();
            }
        });
    }

    private void resizeImage(String pathSaveImage) {
        Bitmap b = HelperDecodeFile.decodeFile(new File(pathSaveImage));
        try {
            FileOutputStream out = new FileOutputStream(pathSaveImage);

            if (b != null) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } else {
                Toast.makeText(ActivityChat.this, "", Toast.LENGTH_SHORT).show();
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDraftLayout() {
        ll_attach_text.setVisibility(View.VISIBLE);
        layoutAttachBottom.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                layoutAttachBottom.setVisibility(View.GONE);
            }
        }).start();
        imvSendButton.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                imvSendButton.setVisibility(View.VISIBLE);
            }
        }).start();
    }

    private void setDraft() {

        if (mReplayLayout != null && mReplayLayout.getVisibility() == View.VISIBLE) {
            StructMessageInfo info = ((StructMessageInfo) mReplayLayout.getTag());
            replyToMessageId = Long.parseLong(info.messageID);
        } else {
            replyToMessageId = 0;
        }

        if (ll_attach_text.getVisibility() == View.VISIBLE) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class)
                        .equalTo(RealmRoomFields.ID, mRoomId)
                        .findFirst();

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
                            new RequestChatUpdateDraft().chatUpdateDraft(mRoomId,
                                edtChat.getText().toString(), replyToMessageId);
                        } else if (chatType == GROUP) {
                            new RequestGroupUpdateDraft().groupUpdateDraft(mRoomId,
                                edtChat.getText().toString(), replyToMessageId);
                        }

                        G.onDraftMessage.onDraftMessage(mRoomId, edtChat.getText().toString());
                    } else {

                        clearDraftRequest();
                        G.onDraftMessage.onDraftMessage(mRoomId, "");
                    }

                    realmRoom.setDraftFile(realmDraftFile);
                }
            });
            realm.close();
        } else {
            final String message = edtChat.getText().toString();
            if (!message.isEmpty()) {

                hasDraft = true;

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        RealmRoom realmRoom = realm.where(RealmRoom.class)
                            .equalTo(RealmRoomFields.ID, mRoomId)
                            .findFirst();
                        if (realmRoom != null) {

                            RealmRoomDraft draft = realm.createObject(RealmRoomDraft.class);
                            draft.setMessage(message);
                            draft.setReplyToMessageId(replyToMessageId);

                            realmRoom.setDraft(draft);

                            if (chatType == CHAT) {
                                new RequestChatUpdateDraft().chatUpdateDraft(mRoomId, message,
                                    replyToMessageId);
                            } else if (chatType == GROUP) {
                                new RequestGroupUpdateDraft().groupUpdateDraft(mRoomId, message,
                                    replyToMessageId);
                            }

                            G.onDraftMessage.onDraftMessage(mRoomId, message);
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

        RealmRoom realmRoom =
            realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();

        if (realmRoom != null) {
            if (realmRoom.getDraftFile() != null) {

                RealmDraftFile realmDraftFile = realmRoom.getDraftFile();

                latestRequestCode = realmDraftFile.getRequestCode();

                String filePath = "";
                if (AttachFile.request_code_TAKE_PICTURE == latestRequestCode) {
                    latestFilePath = realmDraftFile.getFilePath();
                    AttachFile.imagePath = latestFilePath;
                    latestUri = null;
                } else {
                    latestUri = Uri.parse(realmDraftFile.getUri());
                    filePath = AttachFile.getFilePathFromUri(latestUri);
                    latestFilePath = "";
                }

                //&& new File(latestUri.toString()).exists()
                if ((latestUri != null && new File(filePath).exists()) || (!latestFilePath.isEmpty()
                    && new File(latestFilePath).exists())) {
                    showDraftLayout();
                    RealmRoomDraft draft = realmRoom.getDraft();

                    if (draft != null) {
                        hasDraft = true;
                        edtChat.setText(draft.getMessage());

                        if (draft.getReplyToMessageId() != 0) {
                            RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class)
                                .equalTo(RealmRoomMessageFields.MESSAGE_ID,
                                    draft.getReplyToMessageId())
                                .findFirst();

                            if (realmRoomMessage != null) {

                                StructMessageInfo struct = new StructMessageInfo();
                                struct.messageText = realmRoomMessage.getMessage();

                                replay(struct);
                            }
                        }
                    }
                    setDraftMessage(latestRequestCode);
                }
            } else {
                RealmRoomDraft draft = realmRoom.getDraft();
                if (draft != null) {
                    hasDraft = true;
                    edtChat.setText(draft.getMessage());

                    if (draft.getReplyToMessageId() != 0) {
                        //TODO [Saeed Mozaffari] [2016-10-31 4:24 PM] - set reply view for draft
                    }
                }
            }
        }
        realm.close();

        clearLocalDraft();
    }

    private void clearLocalDraft() {

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmRoom realmRoom =
                    realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.setDraft(null);
                    realmRoom.setDraftFile(null);
                }
            }
        });
        realm.close();
    }

    private void clearDraftRequest() {
        Log.i("UUU", "clearDraftRequest hasDraft : " + hasDraft);
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

    @Override protected void onStop() {
        setDraft();
        HelperNotificationAndBadge.isChatRoomNow = false;
        super.onStop();
    }

    @Override protected void onDestroy() {
        // room id have to be set to default, otherwise I'm in the room always!
        mRoomId = -1;
        super.onDestroy();
    }

    @Override public void onOpenClick(View view, StructMessageInfo message, int pos) {
        if (message.messageType == ProtoGlobal.RoomMessageType.VOICE
            || message.messageType == ProtoGlobal.RoomMessageType.AUDIO
            ||
            message.messageType == ProtoGlobal.RoomMessageType.AUDIO_TEXT) {
            MusicPlayer.startPlayer(message.getAttachment().getLocalFilePath(), title, mRoomId,
                true);
        } else if (message.messageType == ProtoGlobal.RoomMessageType.IMAGE
            || message.messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
            showImage(message);
        } else if (message.messageType == ProtoGlobal.RoomMessageType.FILE
            || message.messageType == ProtoGlobal.RoomMessageType.FILE_TEXT
            ||
            message.messageType == ProtoGlobal.RoomMessageType.VIDEO
            || message.messageType == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
            Intent intent =
                HelperMimeType.appropriateProgram(message.getAttachment().getLocalFilePath());
            if (intent != null) startActivity(intent);
        }
    }

    @Override public void onContainerClick(View view, final StructMessageInfo message, int pos) {
        @ArrayRes int itemsRes = 0;
        switch (message.messageType) {
            case TEXT:
                itemsRes = R.array.textMessageDialogItems;
                break;
            case FILE_TEXT:
            case IMAGE_TEXT:
            case VIDEO_TEXT:
                itemsRes = R.array.fileTextMessageDialogItems;
                break;
            case FILE:
            case IMAGE:
            case VIDEO:
            case AUDIO:
            case VOICE:
                itemsRes = R.array.fileMessageDialogItems;
                break;
            case LOCATION:
            case CONTACT:
            case LOG:
                itemsRes = R.array.otherMessageDialogItems;
                break;
        }

        // Arrays.asList returns fixed size, doing like this fixes remove object
        // UnsupportedOperationException exception
        List<String> items =
            new LinkedList<>(Arrays.asList(getResources().getStringArray(itemsRes)));

        Realm realm = Realm.getDefaultInstance();
        // if user clicked on any message which he wasn't its sender, remove edit item option
        if (!message.senderID.equalsIgnoreCase(
            Long.toString(realm.where(RealmUserInfo.class).findFirst().getUserId()))) {
            items.remove(getString(R.string.edit_item_dialog));
        }
        realm.close();

        new MaterialDialog.Builder(this).title("Message")
            .negativeText("CANCEL")
            .items(items)
            .itemsCallback(new MaterialDialog.ListCallback() {
                @Override public void onSelection(MaterialDialog dialog, View view, int which,
                    CharSequence text) {
                    // TODO: 9/14/2016 [Alireza Eskandarpour Shoferi] implement other items
                    if (text.toString().equalsIgnoreCase(getString(R.string.copy_item_dialog))) {
                        // copy message
                        ClipboardManager clipboard =
                            (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Copied Text", message.messageText);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(G.context, "Text Copied", Toast.LENGTH_SHORT).show();
                    } else if (text.toString()
                        .equalsIgnoreCase(getString(R.string.delete_item_dialog))) {
                        final Realm realmCondition = Realm.getDefaultInstance();
                        final RealmClientCondition realmClientCondition =
                            realmCondition.where(RealmClientCondition.class)
                                .equalTo(RealmClientConditionFields.ROOM_ID, mRoomId)
                                .findFirstAsync();
                        realmClientCondition.addChangeListener(
                            new RealmChangeListener<RealmClientCondition>() {
                                @Override public void onChange(final RealmClientCondition element) {
                                    realmCondition.executeTransaction(new Realm.Transaction() {
                                        @Override public void execute(Realm realm) {
                                            if (element != null) {
                                                if (realmCondition.where(RealmOfflineDelete.class)
                                                    .equalTo(
                                                        RealmOfflineDeleteFields.OFFLINE_DELETE,
                                                        Long.parseLong(message.messageID))
                                                    .findFirst() == null) {

                                                    RealmRoomMessage roomMessage =
                                                        realm.where(RealmRoomMessage.class)
                                                            .equalTo(
                                                                RealmRoomMessageFields.MESSAGE_ID,
                                                                Long.parseLong(message.messageID))
                                                            .findFirst();
                                                    if (roomMessage != null) {
                                                        // delete message from database
                                                        roomMessage.deleteFromRealm();
                                                    }

                                                    RealmOfflineDelete realmOfflineDelete =
                                                        realmCondition.createObject(
                                                            RealmOfflineDelete.class);
                                                    realmOfflineDelete.setId(System.nanoTime());
                                                    realmOfflineDelete.setOfflineDelete(
                                                        Long.parseLong(message.messageID));
                                                    element.getOfflineDeleted()
                                                        .add(realmOfflineDelete);

                                                    runOnUiThread(new Runnable() {
                                                        @Override public void run() {
                                                            // remove deleted message from adapter
                                                            mAdapter.removeMessage(
                                                                Long.parseLong(message.messageID));

                                                            // remove tag from edtChat if the
                                                            // message has deleted
                                                            if (edtChat.getTag() != null
                                                                && edtChat.getTag() instanceof StructMessageInfo) {
                                                                if (Long.toString(Long.parseLong(
                                                                    message.messageID))
                                                                    .equals(
                                                                        ((StructMessageInfo) edtChat
                                                                            .getTag()).messageID)) {
                                                                    edtChat.setTag(null);
                                                                }
                                                            }
                                                        }
                                                    });
                                                    // delete message
                                                    new RequestChatDeleteMessage().chatDeleteMessage(
                                                        mRoomId, Long.parseLong(message.messageID));
                                                }
                                                element.removeChangeListeners();
                                            }
                                        }
                                    });

                                    realmCondition.close();
                                }
                            });
                    } else if (text.toString()
                        .equalsIgnoreCase(getString(R.string.edit_item_dialog))) {
                        // edit message
                        // put message text to EditText
                        if (!message.messageText.isEmpty()) {
                            edtChat.setText(message.messageText);
                            edtChat.setSelection(0, edtChat.getText().length());
                            // put message object to edtChat's tag to obtain it later and
                            // found is user trying to edit a message
                            edtChat.setTag(message);
                        }
                    } else if (text.toString()
                        .equalsIgnoreCase(getString(R.string.replay_item_dialog))) {
                        replay(message);
                    } else if (text.toString()
                        .equalsIgnoreCase(getString(R.string.forward_item_dialog))) {
                        // forward selected messages to room list for selecting room
                        if (mAdapter != null) {
                            startActivity(makeIntentForForwardMessages(message));
                        }
                    } else if (text.toString()
                        .equalsIgnoreCase(getString(R.string.share_item_dialog))) {
                        sheareDataToOtherProgram(message);
                    }
                }
            })
            .show();
    }

    @Override public void onUploadCancel(View view, StructMessageInfo message, int pos) {
        // TODO: 10/29/2016 [Alireza] implement
    }

    @Override public void onDownloadCancel(View view, StructMessageInfo message, int pos) {
        // TODO: 10/29/2016 [Alireza] implement
    }

    @Override public void onDownloadStart(View view, StructMessageInfo message, int pos) {
        // TODO: 10/29/2016 [Alireza] implement
    }

    @Override
    public void onFailedMessageClick(View view, final StructMessageInfo message, final int pos) {
        final List<StructMessageInfo> failedMessages = mAdapter.getFailedMessages();
        new ResendMessage(this, new IResendMessage() {
            @Override public void deleteMessage() {
                mAdapter.remove(pos);
            }

            @Override public void resendMessage() {
                mAdapter.updateMessageStatus(Long.parseLong(message.messageID),
                    ProtoGlobal.RoomMessageStatus.SENDING);
            }

            @Override public void resendAllMessages() {
                for (StructMessageInfo message : failedMessages) {
                    mAdapter.updateMessageStatus(Long.parseLong(message.messageID),
                        ProtoGlobal.RoomMessageStatus.SENDING);
                }
            }
        }, Long.parseLong(message.messageID), failedMessages);
    }

    public static class UploadTask
        extends AsyncTask<Object, FileUploadStructure, FileUploadStructure> {
        @Override protected FileUploadStructure doInBackground(Object... params) {
            try {
                String filePath = (String) params[0];
                long messageId = (long) params[1];
                ProtoGlobal.RoomMessageType messageType = (ProtoGlobal.RoomMessageType) params[2];
                long roomId = (long) params[3];
                String messageText = (String) params[4];
                File file = new File(filePath);
                String fileName = file.getName();
                long fileSize = file.length();
                FileUploadStructure fileUploadStructure =
                    new FileUploadStructure(fileName, fileSize, filePath, messageId, messageType,
                        roomId);
                fileUploadStructure.openFile(filePath);
                fileUploadStructure.text = messageText;

                byte[] fileHash = AndroidUtils.getFileHash(fileUploadStructure);
                fileUploadStructure.setFileHash(fileHash);

                return fileUploadStructure;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override protected void onPostExecute(FileUploadStructure result) {
            super.onPostExecute(result);
            MessagesAdapter.uploading.put(result.messageId, 0);
            G.uploaderUtil.startUploading(result, Long.toString(result.messageId));
        }
    }

    private class SearhHash {

        private String hashString = "";
        public int curentSelectedPosition = 0;

        private int curentHashposition = 0;

        private ArrayList<Integer> hashList = new ArrayList<>();

        public void setHashString(String hashString) {
            this.hashString = "#" + hashString;
        }

        public void setPosition(String messageId) {

            curentHashposition = 0;
            hashList.clear();

            for (int i = 0; i < mAdapter.getAdapterItemCount(); i++) {

                if (mAdapter.getItem(i).mMessage.messageID.equals(messageId)) {
                    curentHashposition = hashList.size() + 1;
                }

                if (mAdapter.getItem(i).mMessage.messageText.contains(hashString)) {
                    hashList.add(i);
                }
            }

            txtHashCounter.setText(curentHashposition + " / " + hashList.size());

            curentSelectedPosition = hashList.get(curentHashposition - 1);

            if (mAdapter.getItem(curentSelectedPosition).mMessage.view != null) {
                ((FrameLayout) mAdapter.getItem(
                    curentSelectedPosition).mMessage.view).setForeground(new ColorDrawable(
                    getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
            }
        }

        public void downHash() {

            if (curentHashposition < hashList.size()) {
                goToSelectedPosition(hashList.get(curentHashposition));
                curentHashposition++;
                txtHashCounter.setText(curentHashposition + " / " + hashList.size());
            }
        }

        public void upHash() {

            if (curentHashposition > 1) {
                curentHashposition--;
                goToSelectedPosition(hashList.get(curentHashposition - 1));
                txtHashCounter.setText(curentHashposition + " / " + hashList.size());
            }
        }

        private void goToSelectedPosition(int position) {

            ((FrameLayout) mAdapter.getItem(curentSelectedPosition).mMessage.view).setForeground(
                null);

            recyclerView.scrollToPosition(position);

            curentSelectedPosition = position;

            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    if (mAdapter.getItem(curentSelectedPosition).mMessage.view != null) {
                        ((FrameLayout) mAdapter.getItem(
                            curentSelectedPosition).mMessage.view).setForeground(new ColorDrawable(
                            getResources().getColor(R.color.colorChatMessageSelectableItemBg)));
                    }
                }
            }, 100);
        }
    }
}
