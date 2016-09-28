package com.iGap.activitys;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.ChatMessagesFastAdapter;
import com.iGap.adapter.items.chat.AbstractChatItem;
import com.iGap.adapter.items.chat.ChannelFileItem;
import com.iGap.adapter.items.chat.ChannelImageItem;
import com.iGap.adapter.items.chat.ChannelMessageItem;
import com.iGap.adapter.items.chat.ChannelVideoItem;
import com.iGap.adapter.items.chat.ChannelVoiceItem;
import com.iGap.adapter.items.chat.FileItem;
import com.iGap.adapter.items.chat.ImageItem;
import com.iGap.adapter.items.chat.ImageWithTextItem;
import com.iGap.adapter.items.chat.LocationItem;
import com.iGap.adapter.items.chat.MessageItem;
import com.iGap.adapter.items.chat.TimeItem;
import com.iGap.adapter.items.chat.VideoItem;
import com.iGap.adapter.items.chat.VideoWithTextItem;
import com.iGap.adapter.items.chat.VoiceItem;
import com.iGap.helper.Emojione;
import com.iGap.interface_package.IEmojiBackspaceClick;
import com.iGap.interface_package.IEmojiClickListener;
import com.iGap.interface_package.IEmojiLongClickListener;
import com.iGap.interface_package.IEmojiStickerClick;
import com.iGap.interface_package.IEmojiViewCreate;
import com.iGap.interface_package.IRecentsLongClick;
import com.iGap.interface_package.ISoftKeyboardOpenClose;
import com.iGap.interface_package.OnChatClearMessageResponse;
import com.iGap.interface_package.OnChatDeleteMessageResponse;
import com.iGap.interface_package.OnChatEditMessageResponse;
import com.iGap.interface_package.OnChatMessageRemove;
import com.iGap.interface_package.OnChatMessageSelectionChanged;
import com.iGap.interface_package.OnChatSendMessageResponse;
import com.iGap.interface_package.OnChatUpdateStatusResponse;
import com.iGap.interface_package.OnFileUpload;
import com.iGap.interface_package.OnFileUploadStatusResponse;
import com.iGap.interface_package.OnMessageClick;
import com.iGap.module.AttachFile;
import com.iGap.module.ChatSendMessageUtil;
import com.iGap.module.EmojiEditText;
import com.iGap.module.EmojiPopup;
import com.iGap.module.EmojiRecentsManager;
import com.iGap.module.EndlessRecyclerOnScrollListener;
import com.iGap.module.FileUploadStructure;
import com.iGap.module.FileUtils;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.MyType;
import com.iGap.module.OnComplete;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.ShouldScrolledBehavior;
import com.iGap.module.SortMessages;
import com.iGap.module.StructMessageInfo;
import com.iGap.module.TimeUtils;
import com.iGap.module.Utils;
import com.iGap.module.VoiceRecord;
import com.iGap.proto.ProtoFileUploadStatus;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmChannelRoom;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmChatRoom;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmOfflineDelete;
import com.iGap.realm.RealmOfflineEdited;
import com.iGap.realm.RealmOfflineSeen;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmUserInfo;
import com.iGap.realm.enums.ChannelChatRole;
import com.iGap.realm.enums.GroupChatRole;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestChatDeleteMessage;
import com.iGap.request.RequestChatEditMessage;
import com.iGap.request.RequestFileUpload;
import com.iGap.request.RequestFileUploadInit;
import com.iGap.request.RequestFileUploadStatus;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by android3 on 8/5/2016.
 */
public class ActivityChat extends ActivityEnhanced implements IEmojiViewCreate, IRecentsLongClick, OnMessageClick, OnChatClearMessageResponse, OnChatSendMessageResponse, OnChatUpdateStatusResponse, OnChatMessageSelectionChanged<AbstractChatItem>, OnChatMessageRemove, OnFileUpload, OnFileUploadStatusResponse {

    private RelativeLayout parentLayout;
    private SharedPreferences sharedPreferences;

    private EmojiEditText edtChat;
    private MaterialDesignTextView imvSendButton;
    private MaterialDesignTextView imvAttachFileButton;
    private LinearLayout layoutAttachBottom;
    private MaterialDesignTextView imvMicButton;

    private Button btnCloseAppBarSelected;
    private Button btnReplaySelected;
    private Button btnCopySelected;
    private Button btnForwardSelected;
    private Button btnDeleteSelected;
    private TextView txtNumberOfSelected;
    private LinearLayout ll_AppBarSelected;
    private LinearLayout toolbar;

    private TextView txtName;
    private TextView txtLastSeen;
    private TextView txt_mute;
    private ImageView imvUserPicture;
    private RecyclerView recyclerView;
    private MaterialDesignTextView imvSmileButton;

    AttachFile attachFile;
    private LocationManager locationManager;
    private OnComplete complete;
    BoomMenuButton boomMenuButton;
    private View viewAttachFile;
    private View viewMicRecorder;
    private VoiceRecord voiceRecord;
    private boolean sendByEnter = false;

    public static ActivityChat activityChat;

    private ChatMessagesFastAdapter<AbstractChatItem> mAdapter;
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

    //group
    private GroupChatRole groupRole;
    private String groupParticipantsCountLabel;

    //channel
    private ChannelChatRole channelRole;
    private String channelParticipantsCountLabel;

    private PopupWindow popupWindow;

    @Override
    protected void onStart() {
        super.onStart();

        // when user receive message, I send update status as SENT to the message sender
        // but imagine user is not in the room (or he is in another room) and received some messages
        // when came back to the room with new messages, I make new update status request as SEEN to
        // the message sender
        final Realm chatHistoriesRealm = Realm.getDefaultInstance();
        final RealmResults<RealmChatHistory> realmChatHistories = chatHistoriesRealm.where(RealmChatHistory.class).equalTo("roomId", mRoomId).findAllAsync();
        realmChatHistories.addChangeListener(new RealmChangeListener<RealmResults<RealmChatHistory>>() {
            @Override
            public void onChange(final RealmResults<RealmChatHistory> element) {
                //Start ClientCondition OfflineSeen
                chatHistoriesRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final RealmClientCondition realmClientCondition = chatHistoriesRealm.where(RealmClientCondition.class).equalTo("roomId", mRoomId).findFirst();

                        final ArrayList<Long> offlineSeenId = new ArrayList<>();

                        for (RealmChatHistory history : element) {
                            final RealmRoomMessage realmRoomMessage = history.getRoomMessage();
                            if (realmRoomMessage != null) {
                                if (realmRoomMessage.getUserId() != realm.where(RealmUserInfo.class).findFirst().getUserId() && !realmRoomMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SEEN.toString())) {

                                    realmRoomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SEEN.toString());

                                    RealmOfflineSeen realmOfflineSeen = realm.createObject(RealmOfflineSeen.class);
                                    realmOfflineSeen.setId(System.nanoTime());
                                    realmOfflineSeen.setOfflineSeen(realmRoomMessage.getMessageId());
                                    realm.copyToRealmOrUpdate(realmOfflineSeen);

                                    Log.i(RealmClientCondition.class.getSimpleName(), "before size: " + realmClientCondition.getOfflineSeen().size());
                                    realmClientCondition.getOfflineSeen().add(realmOfflineSeen);
                                    Log.i(RealmClientCondition.class.getSimpleName(), "after size: " + realmClientCondition.getOfflineSeen().size());
                                    offlineSeenId.add(realmRoomMessage.getMessageId());
                                }
                            }
                        }
                        for (long seenId : offlineSeenId) {
                            G.chatUpdateStatusUtil.sendUpdateStatus(chatType, mRoomId, seenId, ProtoGlobal.RoomMessageStatus.SEEN);
                        }
                    }
                });

                element.removeChangeListeners();
                chatHistoriesRealm.close();
            }
        });

        final Realm updateUnreadCountRealm = Realm.getDefaultInstance();
        updateUnreadCountRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom room = realm.where(RealmRoom.class).equalTo("id", mRoomId).findFirst();
                if (room != null) {
                    room.setUnreadCount(0);
                    realm.copyToRealmOrUpdate(room);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                updateUnreadCountRealm.close();
            }
        });
    }

    private Calendar lastDateCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        activityChat = this;

        // get sendByEnter action from setting value
        SharedPreferences sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        int checkedSendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
        if (checkedSendByEnter == 1) {
            sendByEnter = true;
        } else {
            sendByEnter = false;
        }


        String backGroundPath = sharedPreferences.getString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
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
        voiceRecord = new VoiceRecord(this, viewMicRecorder, viewAttachFile);

        lastDateCalendar.clear();

        attachFile = new AttachFile(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        initAttach();
        complete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                Log.e("ddd", messageOne);
            }
        };

        G.clearMessagesUtil.setOnChatClearMessageResponse(this);
        G.uploaderUtil.setActivityCallbacks(this);
        G.onFileUploadStatusResponse = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mRoomId = extras.getLong("RoomId");

            Realm realm = Realm.getDefaultInstance();

            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", mRoomId).findFirst();

            if (realmRoom != null) { // room exist

                title = realmRoom.getTitle();
                initialize = realmRoom.getInitials();
                color = realmRoom.getColor();

                if (realmRoom.getType() == RoomType.CHAT) {

                    chatType = ProtoGlobal.Room.Type.CHAT;
                    RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                    chatPeerId = realmChatRoom.getPeerId();
                    RealmContacts realmContacts = realm.where(RealmContacts.class).equalTo("id", chatPeerId).findFirst();
                    if (realmContacts != null) {
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
                    chatType = ProtoGlobal.Room.Type.GROUP;
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    groupRole = realmGroupRoom.getRole();
                    groupParticipantsCountLabel = realmGroupRoom.getParticipantsCountLabel();

                } else if (realmRoom.getType() == RoomType.CHANNEL) {

                    chatType = ProtoGlobal.Room.Type.CHANNEL;
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    channelRole = realmChannelRoom.getRole();
                    channelParticipantsCountLabel = realmChannelRoom.getParticipantsCountLabel();
                }
            } else {
                chatPeerId = extras.getLong("peerId");
                chatType = ProtoGlobal.Room.Type.CHAT;
                RealmContacts realmContacts = realm.where(RealmContacts.class).equalTo("id", chatPeerId).findFirst();
                title = realmContacts.getDisplay_name();
                initialize = realmContacts.getInitials();
                color = realmContacts.getColor();
                lastSeen = Long.toString(realmContacts.getLast_seen());

            }
            realm.close();
        }
        initComponent();
        initAppbarSelected();
        initCallbacks();
        if (chatType == ProtoGlobal.Room.Type.CHANNEL && channelRole == ChannelChatRole.MEMBER)
            initLayotChannelFooter();

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getParcelableArrayList(ActivitySelectChat.ARG_FORWARD_MESSAGE) != null) {
            ArrayList<StructMessageInfo> messageInfos = getIntent().getExtras().getParcelableArrayList(ActivitySelectChat.ARG_FORWARD_MESSAGE);

            for (StructMessageInfo messageInfo : messageInfos) {
                sendForwardedMessage(messageInfo);
            }
        }
    }

    private void sendForwardedMessage(StructMessageInfo messageInfo) {
        // TODO: 9/10/2016 [Alireza Eskandarpour Shoferi] vaghti kare forward server anjam shod, injaro por kon
    }

    public void initCallbacks() {
        G.chatSendMessageUtil.setOnChatSendMessageResponse(this);
        G.chatUpdateStatusUtil.setOnChatUpdateStatusResponse(this);

        G.onChatEditMessageResponse = new OnChatEditMessageResponse() {
            @Override
            public void onChatEditMessage(long roomId, final long messageId, int messageVersion, final String message, ProtoResponse.Response response) {
                Log.i(ActivityMain.class.getSimpleName(), "onChatEditMessage called");
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
        };

        G.onChatDeleteMessageResponse = new OnChatDeleteMessageResponse() {
            @Override
            public void onChatDeleteMessage(long deleteVersion, final long messageId, long roomId, ProtoResponse.Response response) {
                Log.i(ActivityMain.class.getSimpleName(), "onChatDeleteMessage called");
            }
        };
    }

    @Override
    protected void onDestroy() {
        // room id have to be set to default, otherwise I'm in the room always!
        mRoomId = -1;

        super.onDestroy();
    }

    private void switchAddItem(ArrayList<StructMessageInfo> messageInfos, boolean addTop) {
        long identifier = 100;
        for (StructMessageInfo messageInfo : messageInfos) {
            if (!messageInfo.isTimeMessage()) {
                switch (messageInfo.messageType) {
                    case TEXT:
                        if (chatType != ProtoGlobal.Room.Type.CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new MessageItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new MessageItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        } else {
                            if (!addTop) {
                                mAdapter.add(new ChannelMessageItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new ChannelMessageItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        }
                        break;
                    case IMAGE:
                        if (chatType != ProtoGlobal.Room.Type.CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new ImageItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new ImageItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        } else {
                            if (!addTop) {
                                mAdapter.add(new ChannelImageItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new ChannelImageItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        }
                        break;
                    case IMAGE_TEXT:
                        if (chatType != ProtoGlobal.Room.Type.CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new ImageWithTextItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new ImageWithTextItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        } else {
                            if (!addTop) {
                                mAdapter.add(new ChannelImageItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new ChannelImageItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        }
                        break;
                    case VIDEO:
                        if (chatType != ProtoGlobal.Room.Type.CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new VideoItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new VideoItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        } else {
                            if (!addTop) {
                                mAdapter.add(new ChannelVideoItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new ChannelVideoItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        }
                        break;
                    case VIDEO_TEXT:
                        if (chatType != ProtoGlobal.Room.Type.CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new VideoWithTextItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new VideoWithTextItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        } else {
                            if (!addTop) {
                                mAdapter.add(new ChannelVideoItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new ChannelVideoItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        }
                        break;
                    case LOCATION:
                        // TODO: 9/15/2016 [Alireza Eskandarpour Shoferi] fill
                        if (chatType != ProtoGlobal.Room.Type.CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new LocationItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new LocationItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        } /*else {
                        mAdapter.add(new ChannelVideoItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                    }*/
                        break;
                    case FILE:
                    case FILE_TEXT:
                        if (chatType == ProtoGlobal.Room.Type.CHAT) {
                            if (!addTop) {
                                mAdapter.add(new FileItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new FileItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        } else if (chatType == ProtoGlobal.Room.Type.CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new ChannelFileItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new ChannelFileItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        }
                        break;
                    case VOICE:
                        if (chatType == ProtoGlobal.Room.Type.CHAT) {
                            if (!addTop) {
                                mAdapter.add(new VoiceItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new VoiceItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        } else if (chatType == ProtoGlobal.Room.Type.CHANNEL) {
                            if (!addTop) {
                                mAdapter.add(new ChannelVoiceItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            } else {
                                mAdapter.add(0, new ChannelVoiceItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                            }
                        }
                        break;
                    // TODO: 9/15/2016 [Alireza Eskandarpour Shoferi] implement new message types (contact, audio)
                }
            } else {
                if (!addTop) {
                    mAdapter.add(new TimeItem().setMessage(messageInfo).withIdentifier(identifier));
                } else {
                    mAdapter.add(0, new TimeItem().setMessage(messageInfo).withIdentifier(identifier));
                }
            }

            identifier++;
        }
    }

    private void initComponent() {
        toolbar = (LinearLayout) findViewById(R.id.toolbar);
        ImageView imvBackButton = (ImageView) findViewById(R.id.chl_imv_back_Button);

        final Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", mRoomId).findFirst();
        if (realmRoom != null) {
            findViewById(R.id.imgMutedRoom).setVisibility(realmRoom.getMute() ? View.VISIBLE : View.GONE);
        }
        realm.close();

        txtName = (TextView) findViewById(R.id.chl_txt_name);
        txtName.setTypeface(G.arialBold);
        if (title != null)
            txtName.setText(title);


        txtLastSeen = (TextView) findViewById(R.id.chl_txt_last_seen);

        if (chatType == ProtoGlobal.Room.Type.CHAT) {

            if (lastSeen != null) {
                txtLastSeen.setText(lastSeen);
            }

        } else if (chatType == ProtoGlobal.Room.Type.GROUP) {

            if (groupParticipantsCountLabel != null) {
                txtLastSeen.setText(groupParticipantsCountLabel + " member");
            }

        } else if (chatType == ProtoGlobal.Room.Type.CHANNEL) {

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
        ImageView imvMenuButton = (ImageView) findViewById(R.id.chl_imv_menu_button);
        imvMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.popup_window, null);
                popupWindow = new PopupWindow(popupView, screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);

                if (popupWindow.isOutsideTouchable()) {
                    popupWindow.dismiss();
                    Log.i("CCVVBB", "rr: ");
                }
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //TODO do sth here on dismiss
                    }
                });

                popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
                popupWindow.showAtLocation(popupView, Gravity.RIGHT | Gravity.TOP, 10, 30);
                popupWindow.showAsDropDown(v);


                TextView txtSearch = (TextView) popupView.findViewById(R.id.popup_txtItem1);
                txtSearch.setTypeface(G.arial);
                txtSearch.setText("Search");
                txtSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(ActivityChat.this, "1", Toast.LENGTH_SHORT).show();

                    }
                });

                TextView txtClearHistory = (TextView) popupView.findViewById(R.id.popup_txtItem2);
                txtClearHistory.setTypeface(G.arial);
                txtClearHistory.setText("Clear History");
                txtClearHistory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(ActivityChat.this, "2", Toast.LENGTH_SHORT).show();

                    }
                });

                TextView txtDeleteChat = (TextView) popupView.findViewById(R.id.popup_txtItem3);
                txtDeleteChat.setTypeface(G.arial);
                txtDeleteChat.setText("Delete Chat");
                txtDeleteChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(ActivityChat.this, "3", Toast.LENGTH_SHORT).show();

                    }
                });

                TextView txtMutNotification = (TextView) popupView.findViewById(R.id.popup_txtItem4);
                txtMutNotification.setTypeface(G.arial);
                txtMutNotification.setText("Mut Notification");
                txtMutNotification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(ActivityChat.this, "4", Toast.LENGTH_SHORT).show();

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

        mAdapter = new ChatMessagesFastAdapter<>(this, this, this);

        switchAddItem(getChatList(), true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ActivityChat.this);
        // make start messages from bottom, this is exatly what Telegram and other messengers do for their messages list
        layoutManager.setStackFromEnd(true);
        // set behavior to RecyclerView
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) recyclerView.getLayoutParams();
        params.setBehavior(new ShouldScrolledBehavior(layoutManager, mAdapter));
        recyclerView.setLayoutParams(params);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        int position = recyclerView.getAdapter().getItemCount();
        if (position > 0)
            recyclerView.scrollToPosition(position - 1);


        imvBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imvUserPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chatType == ProtoGlobal.Room.Type.CHAT && chatPeerId != 134) {//TODO [Saeed Mozaffari] [2016-09-07 11:46 AM] -  in if eshtebah ast check for iGap message ==> chatPeerId == 134(alan baraye check kardane) , waiting for userDetail proto
                    Intent intent = new Intent(G.context, ActivityContactsProfile.class);
                    intent.putExtra("peerId", chatPeerId);
                    intent.putExtra("enterFrom", ProtoGlobal.Room.Type.CHAT.toString());
                    startActivity(intent);
                } else if (chatType == ProtoGlobal.Room.Type.GROUP) {
                    Intent intent = new Intent(G.context, ActivityGroupProfile.class);
                    intent.putExtra("RoomId", mRoomId);
                    startActivity(intent);
                }

            }
        });

        //TODO [Saeed Mozaffari] [2016-09-07 5:12 PM] - if user have image set image otherwise set alphabet
        imvUserPicture.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvUserPicture.getContext().getResources().getDimension(R.dimen.dp60), initialize, color));

        imvSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("MMM", "Send Message Start");

                // if use click on a message, the message's text will be put to the EditText
                // i set the message object for that view's tag to obtain it here
                // request message edit only if there is any changes to the message text
                if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                    final StructMessageInfo messageInfo = (StructMessageInfo) edtChat.getTag();
                    final String message = edtChat.getText().toString().trim();
                    if (!message.equals(messageInfo.messageText)) {

                        //TODO [Saeed Mozaffari] [2016-09-17 3:12 PM] - FORCE - check this code for update edited list
                        //Start new Changes for clientCondition ==> remove comment after check this code
                        final Realm realm1 = Realm.getDefaultInstance();
                        realm1.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoomMessage roomMessage = realm1.where(RealmRoomMessage.class).equalTo("messageId", Long.parseLong(messageInfo.messageID)).findFirst();

                                RealmClientCondition realmClientCondition = realm1.where(RealmClientCondition.class).equalTo("roomId", mRoomId).findFirst();

                                RealmOfflineEdited realmOfflineEdited = realm.createObject(RealmOfflineEdited.class);
                                realmOfflineEdited.setId(System.nanoTime());
                                realmOfflineEdited.setMessageId(Long.parseLong(messageInfo.messageID));
                                realmOfflineEdited.setMessage(messageInfo.messageText);
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
                            @Override
                            public void run() {
                                // update message text in adapter
                                mAdapter.updateMessageText(Long.parseLong(messageInfo.messageID), message);
                            }
                        });

                        // should be null after requesting
                        edtChat.setTag(null);
                        edtChat.setText("");

                        // send edit message request
                        new RequestChatEditMessage().chatEditMessage(mRoomId, Long.parseLong(messageInfo.messageID), message);
                    }
                } else {
                    // new message has written
                    final String message = edtChat.getText().toString().trim();
                    final Realm realm = Realm.getDefaultInstance();
                    final long senderId = realm.where(RealmUserInfo.class).findFirst().getUserId();
                    if (!message.isEmpty()) {
                        final String identity = Long.toString(System.currentTimeMillis());

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmChatHistory chatHistory = realm.createObject(RealmChatHistory.class);
                                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class);

                                roomMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT.toString());
                                roomMessage.setMessage(message);
                                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                                roomMessage.setMessageId(Long.parseLong(identity));
                                roomMessage.setUserId(senderId);
                                roomMessage.setUpdateTime((int) (System.currentTimeMillis() / DateUtils.SECOND_IN_MILLIS));

                                // user wants to replay to a message
                                if (mReplayLayout != null && mReplayLayout.getTag() instanceof StructMessageInfo) {
                                    // TODO: 9/10/2016 [Alireza Eskandarpour Shoferi] after server done creating replay, uncomment following lines
                                    /*messageInfo.replayFrom = ((StructMessageInfo) mReplayLayout.getTag()).senderName;
                                    messageInfo.replayMessage = ((StructMessageInfo) mReplayLayout.getTag()).messageText;
                                    messageInfo.replayPicturePath = ((StructMessageInfo) mReplayLayout.getTag()).filePic;*/
                                }

                                chatHistory.setId(System.currentTimeMillis());
                                chatHistory.setRoomId(mRoomId);
                                chatHistory.setRoomMessage(roomMessage);
                            }
                        });

                        RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", Long.parseLong(identity)).findFirst();

                        // user wants to replay to a message
                        if (mReplayLayout != null && mReplayLayout.getTag() instanceof StructMessageInfo) {
                            mAdapter.add(new MessageItem(chatType).setMessage(StructMessageInfo.convert(roomMessage, ((StructMessageInfo) mReplayLayout.getTag()).senderName, ((StructMessageInfo) mReplayLayout.getTag()).messageText, ((StructMessageInfo) mReplayLayout.getTag()).filePic)));
                        } else {
                            mAdapter.add(new MessageItem(chatType).setMessage(StructMessageInfo.convert(roomMessage)));
                        }

                        realm.close();

                        scrollToEnd();

                        new ChatSendMessageUtil()
                                .newBuilder(ProtoGlobal.RoomMessageType.TEXT, mRoomId)
                                .message(message)
                                .sendMessage(chatType, identity);

                        edtChat.setText("");

                        // if replay layout is visible, gone it
                        if (mReplayLayout != null) {
                            mReplayLayout.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(G.context, "Please Write Your Messge!", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        imvAttachFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boomMenuButton.boom();
            }
        });

        imvMicButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                voiceRecord.setItemTag("ivVoice");
                viewAttachFile.setVisibility(View.GONE);
                viewMicRecorder.setVisibility(View.VISIBLE);
                voiceRecord.startVoiceRecord();

                return true;
            }
        });

        // init emoji popup
        // give the topmost view of your activity layout hierarchy. this will be used to measure soft keyboard height
        final EmojiPopup emojiPopup = new EmojiPopup(getWindow().findViewById(android.R.id.content), getApplicationContext(), this);
        emojiPopup.setRecentsLongClick(this);
        emojiPopup.setAnimationStyle(R.style.EmojiPopupAnimation);
        emojiPopup.setBackgroundDrawable(new ColorDrawable());
        // will automatically set size according to the soft keyboard size
        emojiPopup.setSizeForSoftKeyboard();
        emojiPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // if the emoji popup is dismissed, change emoji image resource to smiley icon
                changeEmojiButtonImageResource(R.string.md_emoticon_with_happy_face);
            }
        });
        emojiPopup.setEmojiStickerClickListener(new IEmojiStickerClick() {
            @Override
            public void onEmojiStickerClick(View view) {
                // TODO useful for showing stickers panel
            }
        });
        emojiPopup.setOnSoftKeyboardOpenCloseListener(new ISoftKeyboardOpenClose() {
            @Override
            public void onKeyboardOpen(int keyboardHeight) {
            }

            @Override
            public void onKeyboardClose() {
                // if the keyboard closed, also dismiss the emoji popup
                if (emojiPopup.isShowing()) {
                    emojiPopup.dismiss();
                }
            }
        });
        emojiPopup.setEmojiLongClickListener(new IEmojiLongClickListener() {
            @Override
            public boolean onEmojiLongClick(View view, String emoji) {
                // TODO useful for showing a PopupWindow to select emoji in different colors
                return false;
            }
        });
        emojiPopup.setOnEmojiClickListener(new IEmojiClickListener() {

            @Override
            public void onEmojiClick(View view, String emoji) {
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
                    edtChat.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojiUnicode, 0,
                            emojiUnicode.length());
                }
            }
        });
        emojiPopup.setOnEmojiBackspaceClickListener(new IEmojiBackspaceClick() {
            @Override
            public void onEmojiBackspaceClick(View v) {
                // on backspace clicked, emulate the KEYCODE_DEL key event
                edtChat.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            }
        });

        // to toggle between keyboard and emoji popup
        imvSmileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // if popup is not showing => emoji keyboard is not visible, we need to show it
                if (!emojiPopup.isShowing()) {
                    // if keyboard is visible, simply show the emoji popup
                    if (emojiPopup.isKeyboardOpen()) {
                        emojiPopup.showAtBottom();
                        changeEmojiButtonImageResource(R.string.md_black_keyboard_with_white_keys);
                    }
                    // else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        edtChat.setFocusableInTouchMode(true);
                        edtChat.requestFocus();

                        emojiPopup.showAtBottomPending();

                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
            @Override
            public void onClick(View view) {
                if (emojiPopup.isShowing()) {
                    emojiPopup.dismiss();
                }
            }
        });
        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {

                // if in the seeting page send by enter is on message send by enter key
                if (text.toString().endsWith(System.getProperty("line.separator"))) {
                    if (sendByEnter)
                        imvSendButton.performClick();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
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
                            imvSendButton.setVisibility(View.VISIBLE);
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
                            imvSendButton.setVisibility(View.GONE);
                        }
                    }).start();
                }

                // android emojione doesn't support common space unicode
                // to support space character, a new unicode will be replaced.
                if (editable.toString().contains("\u0020")) {
                    Editable ab = new SpannableStringBuilder(editable.toString().replace("\u0020", "\u2000"));
                    editable.replace(0, editable.length(), ab);
                }
            }
        });
    }

    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        imvSmileButton.setText(drawableResourceId);
    }

    @Override
    public void onBackPressed() {
        if (mAdapter != null && mAdapter.getSelections().size() > 0) {
            mAdapter.deselect();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        voiceRecord.dispatchTouchEvent(event);

        return super.dispatchTouchEvent(event);
    }

    private void initAttach() {


        boomMenuButton = (BoomMenuButton) findViewById(R.id.am_boom);


        Drawable[] subButtonDrawables = new Drawable[3];
        int[] drawablesResource = new int[]{
                R.mipmap.am_camera,
                R.mipmap.am_music,
                R.mipmap.am_paint,
                R.mipmap.am_picture,
                R.mipmap.am_document,
                R.mipmap.am_location,
                R.mipmap.am_video,
                R.mipmap.am_file,
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

        bb.addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_camera), subButtonColors[0], getResources().getString(R.string.am_camera))
                .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_picture), subButtonColors[0], getResources().getString(R.string.am_picture))
                .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_video), subButtonColors[0], getResources().getString(R.string.am_video))
                .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_music), subButtonColors[0], getResources().getString(R.string.am_music))
                .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_document), subButtonColors[0], getResources().getString(R.string.am_document))
                .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_file), subButtonColors[0], getResources().getString(R.string.am_file))
                .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_paint), subButtonColors[0], getResources().getString(R.string.am_paint))
                .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_location), subButtonColors[0], getResources().getString(R.string.am_location))
                .addSubButton(ContextCompat.getDrawable(G.context, R.mipmap.am_contact), subButtonColors[0], getResources().getString(R.string.am_contact))
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
            @Override
            public void onClick(int buttonIndex) {

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

        boomMenuButton.setTextViewColor(ContextCompat.getColor(G.context, R.color.am_iconFab_black));
    }

    private boolean userTriesReplay() {
        return mReplayLayout != null && mReplayLayout.getTag() instanceof StructMessageInfo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AttachFile.request_code_position && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            attachFile.requestGetPosition(complete);
        }

        if (resultCode == Activity.RESULT_CANCELED) {

            Log.e("ddd", "result cancel");

        } else if (resultCode == Activity.RESULT_OK) {
            Log.e("ddd", "result ok");
            final long messageId = System.currentTimeMillis();
            String filePath = null;
            final long updateTime = System.currentTimeMillis();
            ProtoGlobal.RoomMessageType messageType = null;
            Realm realm = Realm.getDefaultInstance();
            final long senderID = realm.where(RealmUserInfo.class).findFirst().getUserId();

            switch (requestCode) {
                case AttachFile.request_code_TAKE_PICTURE:
                    filePath = AttachFile.imagePath;
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                    // user wants to replay to a message
                    if (userTriesReplay()) {
                        mAdapter.add(new ImageItem(chatType).setMessage(new StructMessageInfo(Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.RoomMessageType.IMAGE, MyType.SendType.send, MyType.FileState.uploading, filePath, updateTime, ((StructMessageInfo) mReplayLayout.getTag()))));
                    } else {
                        mAdapter.add(new ImageItem(chatType).setMessage(new StructMessageInfo(Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.RoomMessageType.IMAGE, MyType.SendType.send, MyType.FileState.uploading, filePath, updateTime)));
                    }
                    Log.e("ddd", filePath + "     image path");
                    break;
                case AttachFile.request_code_media_from_gallary:
                    filePath = AttachFile.getFilePathFromUri(data.getData());
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                    if (userTriesReplay()) {
                        mAdapter.add(new ImageItem(chatType).setMessage(new StructMessageInfo(Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.RoomMessageType.IMAGE, MyType.SendType.send, MyType.FileState.uploading, filePath, updateTime, ((StructMessageInfo) mReplayLayout.getTag()))));

                    } else {
                        mAdapter.add(new ImageItem(chatType).setMessage(new StructMessageInfo(Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.RoomMessageType.IMAGE, MyType.SendType.send, MyType.FileState.uploading, filePath, updateTime)));
                    }
                    Log.e("ddd", filePath + "    gallary file path");
                    break;
                case AttachFile.request_code_VIDEO_CAPTURED:
                    filePath = AttachFile.getFilePathFromUri(data.getData());
                    messageType = ProtoGlobal.RoomMessageType.VIDEO;
                    File videoFile = new File(filePath);
                    String videoFileName = videoFile.getName();
                    String videoFileMime = FileUtils.getMimeType(videoFile);
                    if (userTriesReplay()) {
                        mAdapter.add(new VideoItem(chatType).setMessage(new StructMessageInfo(Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.RoomMessageType.VIDEO, MyType.SendType.send, MyType.FileState.uploading, videoFileName, videoFileMime, filePath, filePath, videoFile.length(), null, updateTime, ((StructMessageInfo) mReplayLayout.getTag()))));
                    } else {
                        mAdapter.add(new VideoItem(chatType).setMessage(new StructMessageInfo(Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.RoomMessageType.VIDEO, MyType.SendType.send, MyType.FileState.uploading, videoFileName, videoFileMime, filePath, filePath, videoFile.length(), null, updateTime)));
                    }
                    Log.e("ddd", AttachFile.getFilePathFromUri(data.getData()) + "    video capture path");
                    break;
                case AttachFile.request_code_pic_audi:
                    filePath = AttachFile.getFilePathFromUri(data.getData());
                    messageType = ProtoGlobal.RoomMessageType.AUDIO;
                    // TODO: 9/15/2016 [Alireza Eskandarpour Shoferi] implement
                    Log.e("ddd", filePath + "    audio  path");
                    break;
                case AttachFile.request_code_pic_file:
                    filePath = data.getData().getPath();
                    messageType = ProtoGlobal.RoomMessageType.FILE;
                    File fileFile = new File(filePath);
                    String fileFileName = fileFile.getName();
                    String fileFileMime = FileUtils.getMimeType(fileFile);
                    if (userTriesReplay()) {
                        mAdapter.add(new FileItem(chatType).setMessage(new StructMessageInfo(Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.RoomMessageType.FILE, MyType.SendType.send, MyType.FileState.uploading, fileFileName, fileFileMime, filePath, filePath, fileFile.length(), null, updateTime, ((StructMessageInfo) mReplayLayout.getTag()))));
                    } else {
                        mAdapter.add(new FileItem(chatType).setMessage(new StructMessageInfo(Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.RoomMessageType.FILE, MyType.SendType.send, MyType.FileState.uploading, fileFileName, fileFileMime, filePath, filePath, fileFile.length(), null, updateTime)));
                    }
                    Log.e("ddd", data.getData() + "    pic file path");
                    break;
                case AttachFile.request_code_contact_phone:
                    filePath = data.getData().getPath();
                    messageType = ProtoGlobal.RoomMessageType.CONTACT;
                    // TODO: 9/15/2016 [Alireza Eskandarpour Shoferi] implement
                    Log.e("ddd", filePath + "   contact phone");
                    break;
                case AttachFile.request_code_paint:
                    filePath = data.getData().getPath();
                    messageType = ProtoGlobal.RoomMessageType.IMAGE;
                    if (userTriesReplay()) {
                        mAdapter.add(new ImageItem(chatType).setMessage(new StructMessageInfo(Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.RoomMessageType.IMAGE, MyType.SendType.send, MyType.FileState.uploading, filePath, updateTime, ((StructMessageInfo) mReplayLayout.getTag()))));
                    } else {
                        mAdapter.add(new ImageItem(chatType).setMessage(new StructMessageInfo(Long.toString(messageId), Long.toString(senderID), ProtoGlobal.RoomMessageStatus.SENDING.toString(), ProtoGlobal.RoomMessageType.IMAGE, MyType.SendType.send, MyType.FileState.uploading, filePath, updateTime)));
                    }
                    Log.e("ddd", filePath + "   pain path");
                    break;
            }

            final ProtoGlobal.RoomMessageType finalMessageType = messageType;
            final String finalFilePath = filePath;
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmChatHistory chatHistory = realm.createObject(RealmChatHistory.class);
                    RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class);

                    roomMessage.setMessageType(finalMessageType.toString());
                    roomMessage.setMessage(null);
                    roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                    roomMessage.setAttachmentForLocalPath(messageId, finalFilePath);
                    roomMessage.setMessageId(messageId);
                    roomMessage.setUserId(senderID);
                    roomMessage.setUpdateTime((int) (updateTime / DateUtils.SECOND_IN_MILLIS));

                    // TODO: 9/26/2016 [Alireza Eskandarpour Shoferi] user may wants to send a file in response to a message as replay, so after server done creating replay and forward options, modify this section and sending message as well.

                    chatHistory.setId(System.currentTimeMillis());
                    chatHistory.setRoomId(mRoomId);
                    chatHistory.setRoomMessage(roomMessage);
                }
            });

            realm.close();

            new AsyncTask<Object, FileUploadStructure, FileUploadStructure>() {
                @Override
                protected FileUploadStructure doInBackground(Object... params) {
                    try {
                        String filePath = (String) params[0];
                        long messageId = (long) params[1];
                        ProtoGlobal.RoomMessageType messageType = (ProtoGlobal.RoomMessageType) params[2];
                        File file = new File(filePath);
                        String fileName = file.getName();
                        long fileSize = file.length();
                        FileUploadStructure fileUploadStructure = new FileUploadStructure(fileName, fileSize, filePath, messageId, messageType, mRoomId);
                        fileUploadStructure.openFile(filePath);

                        byte[] fileHash = Utils.getFileHash(fileUploadStructure);
                        fileUploadStructure.setFileHash(fileHash);

                        return fileUploadStructure;
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(FileUploadStructure result) {
                    super.onPostExecute(result);
                    mSelectedFiles.add(result);
                    G.uploaderUtil.startUploading(result.fileSize, Long.toString(result.messageId));
                }
            }.execute(filePath, messageId, messageType);

            scrollToEnd();
        }
    }

    private LinearLayout mReplayLayout;

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

    @Override
    public void onEmojiViewCreate(View view, EmojiPopup emojiPopup) {

    }

    @Override
    public boolean onRecentsLongClick(View view, EmojiRecentsManager recentsManager) {
        // TODO useful for clearing recents
        return false;
    }

    private Intent makeIntentForForwardMessages(ArrayList<StructMessageInfo> messageInfos) {
        Intent intent = new Intent(ActivityChat.this, ActivitySelectChat.class);
        intent.putParcelableArrayListExtra(ActivitySelectChat.ARG_FORWARD_MESSAGE, messageInfos);

        return intent;
    }

    private Intent makeIntentForForwardMessages(StructMessageInfo messageInfos) {
        ArrayList<StructMessageInfo> infos = new ArrayList<>();
        infos.add(messageInfos);

        return makeIntentForForwardMessages(infos);
    }

    private void replay(StructMessageInfo item) {
        if (mAdapter != null) {
            Set<AbstractChatItem> messages = mAdapter.getSelectedItems();
            // replay works if only one message selected
            inflateReplayLayoutIntoStub(item == null ? messages.iterator().next().mMessage : item);

            ll_AppBarSelected.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);

            mAdapter.deselect();
        }
    }

    private void initAppbarSelected() {

        btnCloseAppBarSelected = (Button) findViewById(R.id.chl_btn_close_layout);
        btnCloseAppBarSelected.setTypeface(G.fontawesome);
        btnCloseAppBarSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.deselect();
                toolbar.setVisibility(View.VISIBLE);
                ll_AppBarSelected.setVisibility(View.GONE);
                // gone replay layout
                if (mReplayLayout != null) {
                    mReplayLayout.setVisibility(View.GONE);
                }
            }
        });

        btnReplaySelected = (Button) findViewById(R.id.chl_btn_replay_selected);
        btnReplaySelected.setTypeface(G.fontawesome);
        btnReplaySelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnReplaySelected");
                replay(null);
            }
        });

        btnCopySelected = (Button) findViewById(R.id.chl_btn_copy_selected);
        btnCopySelected.setTypeface(G.fontawesome);
        btnCopySelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (AbstractChatItem messageID : mAdapter.getSelectedItems()) {////TODO [Saeed Mozaffari] [2016-09-13 6:39 PM] - code is wrong
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied Text", messageID.mMessage.messageID);
                    clipboard.setPrimaryClip(clip);
                }
            }
        });


        btnForwardSelected = (Button) findViewById(R.id.chl_btn_forward_selected);
        btnForwardSelected.setTypeface(G.fontawesome);
        btnForwardSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnForwardSelected");
                // forward selected messages to room list for selecting room
                if (mAdapter != null && mAdapter.getSelectedItems().size() > 0) {
                    startActivity(makeIntentForForwardMessages(getMessageStructFromSelectedItems()));
                }
            }
        });

        btnDeleteSelected = (Button) findViewById(R.id.chl_btn_delete_selected);
        btnDeleteSelected.setTypeface(G.fontawesome);

        btnDeleteSelected.setOnClickListener(new View.OnClickListener() { //TODO [Saeed Mozaffari] [2016-09-17 2:58 PM] - FORCE - add item to delete list
            @Override
            public void onClick(View view) {

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // get offline delete list , add new deleted list and update in client condition , then send request for delete message to server
                        RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo("roomId", mRoomId).findFirst();

                        for (final AbstractChatItem messageID : mAdapter.getSelectedItems()) {
                            RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", Long.parseLong(messageID.mMessage.messageID)).findFirst();
                            if (roomMessage != null) {
                                // delete message from database
                                roomMessage.deleteFromRealm();
                            }

                            RealmOfflineDelete realmOfflineDelete = realm.createObject(RealmOfflineDelete.class);
                            realmOfflineDelete.setId(System.nanoTime());
                            realmOfflineDelete.setOfflineDelete(Long.parseLong(messageID.mMessage.messageID));

                            realmClientCondition.getOfflineDeleted().add(realmOfflineDelete);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // remove deleted message from adapter
                                    mAdapter.removeMessage(Long.parseLong(messageID.mMessage.messageID));

                                    // remove tag from edtChat if the message has deleted
                                    if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                                        if (messageID.mMessage.messageID.equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
                                            edtChat.setTag(null);
                                        }
                                    }
                                }
                            });
                            new RequestChatDeleteMessage().chatDeleteMessage(mRoomId, Long.parseLong(messageID.mMessage.messageID));
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

    private ArrayList<StructMessageInfo> getMessageStructFromSelectedItems() {
        ArrayList<StructMessageInfo> messageInfos = new ArrayList<>(mAdapter.getSelectedItems().size());
        for (AbstractChatItem item : mAdapter.getSelectedItems()) {
            messageInfos.add(item.mMessage);
        }
        return messageInfos;
    }

    private void initLayotChannelFooter() {


        LinearLayout layoutAttach = (LinearLayout) findViewById(R.id.chl_ll_attach);
        RelativeLayout layoutChannelFooter = (RelativeLayout) findViewById(R.id.chl_ll_channel_footer);

        layoutAttach.setVisibility(View.GONE);
        layoutChannelFooter.setVisibility(View.VISIBLE);

        btnUp = (Button) findViewById(R.id.chl_btn_up);
        btnUp.setTypeface(G.fontawesome);
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "up click");
                int position = recyclerView.getAdapter().getItemCount();
                if (position > 0)
                    recyclerView.scrollToPosition(0);
            }
        });


        btnDown = (Button) findViewById(R.id.chl_btn_down);
        btnDown.setTypeface(G.fontawesome);
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnDown");
                int position = recyclerView.getAdapter().getItemCount();
                if (position > 0)
                    recyclerView.scrollToPosition(position - 1);
            }
        });


        txtChannelMute = (TextView) findViewById(R.id.chl_txt_mute_channel);
        txtChannelMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        for (RealmChatHistory realmChatHistory : realm.where(RealmChatHistory.class).equalTo("roomId", mRoomId).findAll()) {
            RealmRoomMessage roomMessage = realmChatHistory.getRoomMessage();
            if (roomMessage != null) {
                realmRoomMessages.add(roomMessage);
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
                timeMessage.setUpdateTime((int) ((message.getUpdateTime() / DateUtils.SECOND_IN_MILLIS) - 1L));
                timeMessage.setMessage(timeString);
                timeMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT.toString());
                lastResultMessages.add(timeMessage);
            }

            lastResultMessages.add(message);
        }

        Collections.sort(lastResultMessages, SortMessages.DESC);

        realm.close();

        EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(lastResultMessages, mAdapter, ImageLoader.getInstance(), false, true) {
            @Override
            public void onLoadMore(EndlessRecyclerOnScrollListener listener, int page) {
                List<RealmRoomMessage> roomMessages = listener.loadMore(page);
                for (RealmRoomMessage roomMessage : roomMessages) {
                    StructMessageInfo messageInfo = StructMessageInfo.convert(roomMessage);
                    switchAddItem(new ArrayList<>(Arrays.asList(messageInfo)), true);
                }
            }

            @Override
            public void onNoMore(EndlessRecyclerOnScrollListener listener) {

            }
        };

        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);

        ArrayList<StructMessageInfo> messageInfos = new ArrayList<>();
        for (RealmRoomMessage realmRoomMessage : endlessRecyclerOnScrollListener.loadMore(0)) {
            messageInfos.add(StructMessageInfo.convert(realmRoomMessage));
        }

        return messageInfos;
    }

    private String getTimeSettingMessage(long comingDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(comingDate);

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        long diff = Math.abs(calendar.getTimeInMillis() - lastDateCalendar.getTimeInMillis());

        if (diff + 1000 > DateUtils.DAY_IN_MILLIS) {
            lastDateCalendar.setTimeInMillis(calendar.getTimeInMillis());
            return TimeUtils.getChatSettingsTimeAgo(this, calendar.getTime());
        }

        return null;
    }


    @Override
    public void onMessageClick(View view, final StructMessageInfo messageInfo, int position) {
        Log.i(ActivityChat.class.getSimpleName(), "Message clicked");

        @ArrayRes
        int itemsRes = 0;
        switch (messageInfo.messageType) {
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
            case VOICE:
                itemsRes = R.array.fileMessageDialogItems;
                break;
            case LOCATION:
            case LOG:
                itemsRes = R.array.otherMessageDialogItems;
                break;
        }

        // Arrays.asList returns fixed size, doing like this fixes remove object UnsupportedOperationException exception
        List<String> items = new LinkedList<>(Arrays.asList(getResources().getStringArray(itemsRes)));

        Realm realm = Realm.getDefaultInstance();
        // if user clicked on any message which he wasn't its sender, remove edit item option
        if (!messageInfo.senderID.equalsIgnoreCase(Long.toString(realm.where(RealmUserInfo.class).findFirst().getUserId()))) {
            items.remove(getString(R.string.edit_item_dialog));
        }
        realm.close();

        new MaterialDialog.Builder(this)
                .title("Message")
                .negativeText("CANCEL")
                .items(items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        // TODO: 9/14/2016 [Alireza Eskandarpour Shoferi] implement other items
                        if (text.toString().equalsIgnoreCase(getString(R.string.copy_item_dialog))) {
                            // copy message
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Copied Text", messageInfo.messageText);
                            clipboard.setPrimaryClip(clip);
                        } else if (text.toString().equalsIgnoreCase(getString(R.string.delete_item_dialog))) {
                            final Realm realmCondition = Realm.getDefaultInstance();
                            final RealmClientCondition realmClientCondition = realmCondition.where(RealmClientCondition.class).equalTo("roomId", mRoomId).findFirstAsync();
                            realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
                                @Override
                                public void onChange(final RealmClientCondition element) {
                                    realmCondition.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            if (element != null) {
                                                if (realmCondition.where(RealmOfflineDelete.class).equalTo("offlineDelete", Long.parseLong(messageInfo.messageID)).findFirst() == null) {
                                                    RealmOfflineDelete realmOfflineDelete = realmCondition.createObject(RealmOfflineDelete.class);
                                                    realmOfflineDelete.setId(System.nanoTime());
                                                    realmOfflineDelete.setOfflineDelete(Long.parseLong(messageInfo.messageID));
                                                    element.getOfflineDeleted().add(realmOfflineDelete);

                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            // remove deleted message from adapter
                                                            mAdapter.removeMessage(Long.parseLong(messageInfo.messageID));

                                                            // remove tag from edtChat if the message has deleted
                                                            if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                                                                if (Long.toString(Long.parseLong(messageInfo.messageID)).equals(((StructMessageInfo) edtChat.getTag()).messageID)) {
                                                                    edtChat.setTag(null);
                                                                }
                                                            }
                                                        }
                                                    });
                                                    // delete message
                                                    new RequestChatDeleteMessage().chatDeleteMessage(mRoomId, Long.parseLong(messageInfo.messageID));
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
                            if (!messageInfo.messageText.isEmpty()) {
                                edtChat.setText(messageInfo.messageText);
                                edtChat.setSelection(0, edtChat.getText().length());
                                // put message object to edtChat's tag to obtain it later and
                                // found is user trying to edit a message
                                edtChat.setTag(messageInfo);
                            }
                        } else if (text.toString().equalsIgnoreCase(getString(R.string.replay_item_dialog))) {
                            replay(messageInfo);
                        } else if (text.toString().equalsIgnoreCase(getString(R.string.forward_item_dialog))) {
                            // forward selected messages to room list for selecting room
                            if (mAdapter != null) {
                                startActivity(makeIntentForForwardMessages(messageInfo));
                            }
                        }
                    }
                })
                .show();
    }

    @Override
    public void onChatClearMessage(long roomId, long clearId, ProtoResponse.Response response) {
        Log.i(ActivityChat.class.getSimpleName(), "onChatClearMessage called");
        // TODO
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
    public void onChatUpdateStatus(long roomId, final long messageId, final ProtoGlobal.RoomMessageStatus status, int statusVersion) {
        Log.i(ActivityChat.class.getSimpleName(), "onChatUpdateStatus called");

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
    public void onChatMessageSelectionChanged(int selectedCount, Set<AbstractChatItem> selectedItems) {
        Toast.makeText(ActivityChat.this, "selected: " + Integer.toString(selectedCount), Toast.LENGTH_SHORT).show();
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
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", mRoomId).findFirst();
                    AbstractChatItem lastMessageBeforeDeleted = mAdapter.getAdapterItem(mAdapter.getAdapterItemCount() - 1);
                    if (lastMessageBeforeDeleted != null) {
                        realmRoom.setLastMessageId(Long.parseLong(lastMessageBeforeDeleted.mMessage.messageID));
                        realmRoom.setLastMessageTime((int) (lastMessageBeforeDeleted.mMessage.time / DateUtils.SECOND_IN_MILLIS));

                        realm.copyToRealmOrUpdate(realmRoom);
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

    @Override
    public void onMessageReceive(final long roomId, String message, String messageType, final ProtoGlobal.RoomMessage roomMessage) {
        Log.i(ActivityChat.class.getSimpleName(), "onMessageReceive called for group");
        // I'm in the room
        if (roomId == mRoomId) {
            // I'm in the room, so unread messages count is 0. it means, I read all messages
            final Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom room = realm.where(RealmRoom.class).equalTo("id", mRoomId).findFirst();
                    if (room != null) {
                        room.setUnreadCount(0);
                        realm.copyToRealmOrUpdate(room);
                    }
                }
            });

            // when user receive message, I send update status as SENT to the message sender
            // but imagine user is not in the room (or he is in another room) and received some messages
            // when came back to the room with new messages, I make new update status request as SEEN to
            // the message sender
            final RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", roomMessage.getMessageId()).findFirst();
            //Start ClientCondition OfflineSeen
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo("roomId", mRoomId).findFirst();

                    if (realmRoomMessage != null) {
                        if (!realmRoomMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SEEN.toString())) {
                            realmRoomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SEEN.toString());

                            RealmOfflineSeen realmOfflineSeen = realm.createObject(RealmOfflineSeen.class);
                            realmOfflineSeen.setId(System.nanoTime());
                            realmOfflineSeen.setOfflineSeen(realmRoomMessage.getMessageId());
                            realm.copyToRealmOrUpdate(realmOfflineSeen);

                            Log.i(RealmClientCondition.class.getSimpleName(), "before size: " + realmClientCondition.getOfflineSeen().size());

                            realmClientCondition.getOfflineSeen().add(realmOfflineSeen);

                            Log.i(RealmClientCondition.class.getSimpleName(), "after size: " + realmClientCondition.getOfflineSeen().size());
                        }
                    }

                    // make update status to message sender that i've read his message
                    if (chatType == ProtoGlobal.Room.Type.CHAT) {
                        G.chatUpdateStatusUtil.sendUpdateStatus(chatType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                    } else if (chatType == ProtoGlobal.Room.Type.GROUP && (roomMessage.getStatus() == ProtoGlobal.RoomMessageStatus.SENT || roomMessage.getStatus() == ProtoGlobal.RoomMessageStatus.DELIVERED)) {
                        G.chatUpdateStatusUtil.sendUpdateStatus(chatType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                    }
                }
            });

            realm.close();


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.add(new MessageItem(chatType).setMessage(StructMessageInfo.convert(roomMessage)));
                    scrollToEnd();
                }
            });
        } else {
            // user has received the message, so I make a new delivered update status request
            if (chatType == ProtoGlobal.Room.Type.CHAT) {
                G.chatUpdateStatusUtil.sendUpdateStatus(chatType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);

            } else if (chatType == ProtoGlobal.Room.Type.GROUP && roomMessage.getStatus() == ProtoGlobal.RoomMessageStatus.SENT) {
                G.chatUpdateStatusUtil.sendUpdateStatus(chatType, roomId, roomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
            }
            // I'm not in the room, but I have to add 1 to unread messages count
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom room = realm.where(RealmRoom.class).equalTo("id", mRoomId).findFirst();
                    if (room != null) {
                        room.setUnreadCount(room.getUnreadCount() + 1);
                        realm.copyToRealmOrUpdate(room);
                    }
                }
            });
            realm.close();
        }
    }

    /**
     * get file with hash string
     *
     * @param identity file hash
     * @return FileUploadStructure
     */
    @Nullable
    private FileUploadStructure getSelectedFile(String identity) {
        for (FileUploadStructure structure : mSelectedFiles) {
            if (structure.messageId == Long.parseLong(identity)) {
                return structure;
            }
        }
        return null;
    }

    @Override
    public void OnFileUploadOption(int firstBytesLimit, int lastBytesLimit, int maxConnection, String fileHashAsIdentity, ProtoResponse.Response response) {
        try {
            FileUploadStructure fileUploadStructure = getSelectedFile(fileHashAsIdentity);
            // getting bytes from file as server said
            byte[] bytesFromFirst = Utils.getBytesFromStart(fileUploadStructure, firstBytesLimit);
            byte[] bytesFromLast = Utils.getBytesFromEnd(fileUploadStructure, lastBytesLimit);
            // make second request
            new RequestFileUploadInit().fileUploadInit(bytesFromFirst, bytesFromLast, fileUploadStructure.fileSize, fileUploadStructure.fileHash, Long.toString(fileUploadStructure.messageId), fileUploadStructure.fileName);
        } catch (IOException e) {
            Log.i("BreakPoint", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void OnFileUploadInit(String token, final double progress, long offset, int limit, final String identity, ProtoResponse.Response response) {
        // token needed for requesting upload
        // updating structure with new token
        FileUploadStructure fileUploadStructure = getSelectedFile(identity);
        fileUploadStructure.token = token;

        // not already uploaded
        if (progress != 100.0) {
            try {
                byte[] bytes = Utils.getNBytesFromOffset(fileUploadStructure, (int) offset, limit);
                // make third request for first time
                new RequestFileUpload().fileUpload(token, offset, bytes, identity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // update progress
                        mAdapter.updateProgress(Long.parseLong(identity), (int) progress);
                    }
                });

                if (isFileExistInList(Long.parseLong(identity))) {
                    // handle when the file has already uploaded
                    onFileUploadComplete(identity, response);
                }
            } catch (Exception e) {
                Log.i("BreakPoint", e.getMessage());
            }
        }
    }

    /**
     * does file exist in the list
     * useful for preventing from calling onFileUploadComplete() multiple for a file
     *
     * @param messageId file hash
     * @return boolean
     */
    private boolean isFileExistInList(long messageId) {
        for (FileUploadStructure fileUploadStructure : mSelectedFiles) {
            if (fileUploadStructure.messageId == messageId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFileUpload(final double progress, long nextOffset, int nextLimit, final String identity, ProtoResponse.Response response) {
        final long startOnFileUploadTime = System.currentTimeMillis();

        // for specific views, tags must be set with files hashes
        // get the view which has provided hash string
        // then do anything you want to do wit that view

        try {
            // update progress
            Log.i("SOC", "************************************ identity : " + identity + "  ||  progress : " + progress);
            Log.i("BreakPoint", identity + " > bad az update progress");

            if (progress != 100.0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.updateProgress(Long.parseLong(identity), (int) progress);
                    }
                });

                Log.i("BreakPoint", identity + " > 100 nist");
                FileUploadStructure fileUploadStructure = getSelectedFile(identity);
                Log.i("BreakPoint", identity + " > fileUploadStructure");
                final long startGetNBytesTime = System.currentTimeMillis();
                byte[] bytes = Utils.getNBytesFromOffset(fileUploadStructure, (int) nextOffset, nextLimit);

                fileUploadStructure.getNBytesTime += System.currentTimeMillis() - startGetNBytesTime;

                Log.i("BreakPoint", identity + " > after bytes");
                // make request till uploading has finished
                final long startSendReqTime = System.currentTimeMillis();

                new RequestFileUpload().fileUpload(fileUploadStructure.token, nextOffset, bytes, identity);
                fileUploadStructure.sendRequestsTime += System.currentTimeMillis() - startSendReqTime;
                Log.i("BreakPoint", identity + " > after fileUpload request");

                fileUploadStructure.elapsedInOnFileUpload += System.currentTimeMillis() - startOnFileUploadTime;
            } else {
                if (isFileExistInList(Long.parseLong(identity))) {
                    // handle when the file has already uploaded
                    onFileUploadComplete(identity, response);
                }
            }
        } catch (IOException e) {
            Log.i("BreakPoint", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onFileUploadComplete(final String fileHashAsIdentity, ProtoResponse.Response response) {
        final FileUploadStructure fileUploadStructure = getSelectedFile(fileHashAsIdentity);

        new RequestFileUploadStatus().fileUploadStatus(fileUploadStructure.token, fileHashAsIdentity);
    }

    // selected files (paths)
    private CopyOnWriteArrayList<FileUploadStructure> mSelectedFiles = new CopyOnWriteArrayList<>();

    @Override
    public void onFileUploadStatus(ProtoFileUploadStatus.FileUploadStatusResponse.Status status, double progress, int recheckDelayMS, final String identity, ProtoResponse.Response response) {
        final FileUploadStructure fileUploadStructure = getSelectedFile(identity);
        if (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.PROCESSED && progress == 100D) {
            new ChatSendMessageUtil()
                    .newBuilder(fileUploadStructure.messageType, fileUploadStructure.roomId)
                    .attachment(fileUploadStructure.token)
                    .sendMessage(chatType, Long.toString(fileUploadStructure.messageId));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.updateProgress(Long.parseLong(identity), 100);
                }
            });

            // remove from selected files to prevent calling this method multiple times
            // multiple calling may occurs because of the server
            try {
                // FIXME: 9/19/2016 [Alireza Eskandarpour Shoferi] uncomment plz
                //removeFromSelectedFiles(identity);
            } catch (Exception e) {
                Log.i("BreakPoint", e.getMessage());
                e.printStackTrace();
            }

            // close file into structure
            try {
                if (fileUploadStructure != null) {
                    fileUploadStructure.closeFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (status == ProtoFileUploadStatus.FileUploadStatusResponse.Status.PROCESSING) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new RequestFileUploadStatus().fileUploadStatus(fileUploadStructure.token, identity);
                }
            }, recheckDelayMS);
        } else {
            G.uploaderUtil.startUploading(fileUploadStructure.fileSize, Long.toString(fileUploadStructure.messageId));
        }
    }
}
