package com.iGap.activitys;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.ViewStubCompat;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.iGap.adapter.items.chat.MessageItem;
import com.iGap.adapter.items.chat.VideoItem;
import com.iGap.adapter.items.chat.VideoWithTextItem;
import com.iGap.adapter.items.chat.VoiceItem;
import com.iGap.helper.Emojione;
import com.iGap.helper.HelperProtoBuilder;
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
import com.iGap.interface_package.OnChatMessageSelectionChanged;
import com.iGap.interface_package.OnChatSendMessageResponse;
import com.iGap.interface_package.OnChatUpdateStatusResponse;
import com.iGap.interface_package.OnMessageClick;
import com.iGap.module.AttachFile;
import com.iGap.module.ChatSendMessageUtil;
import com.iGap.module.EmojiEditText;
import com.iGap.module.EmojiPopup;
import com.iGap.module.EmojiRecentsManager;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.MyType;
import com.iGap.module.OnComplete;
import com.iGap.module.RecyclerViewPauseOnScrollListener;
import com.iGap.module.StructMessageInfo;
import com.iGap.proto.ProtoChatSendMessage;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmChannelRoom;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmChatRoom;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmUserInfo;
import com.iGap.realm.enums.ChannelChatRole;
import com.iGap.realm.enums.GroupChatRole;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestChatDeleteMessage;
import com.iGap.request.RequestChatEditMessage;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by android3 on 8/5/2016.
 */
public class ActivityChat extends ActivityEnhanced implements IEmojiViewCreate, IRecentsLongClick, OnMessageClick, OnChatClearMessageResponse, OnChatSendMessageResponse, OnChatUpdateStatusResponse, OnChatMessageSelectionChanged<AbstractChatItem> {

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

    @Override
    protected void onStart() {
        super.onStart();

        // when user receive message, I send update status as SENT to the message sender
        // but imagine user is not in the room (or he is in another room) and received some messages
        // when came back to the room with new messages, I make new update status request as SEEN to
        // the message sender
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmChatHistory> realmChatHistories = realm.where(RealmChatHistory.class).equalTo("roomId", mRoomId).findAll();
        for (RealmChatHistory history : realmChatHistories) {
            RealmRoomMessage realmRoomMessage = history.getRoomMessage();
            if (realmRoomMessage != null) {
                //if (realmRoomMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.DELIVERED.toString())) {//TODO [Saeed Mozaffari] [2016-09-13 5:44 PM] - clear comment
                G.chatUpdateStatusUtil.sendUpdateStatus(mRoomId, realmRoomMessage.getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                //}
            }
        }

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

        realm.close();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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
                    Log.i("XXX", "chatPeerId : " + chatPeerId);
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

                if (mRoomId == roomId) {
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
        };
    }

    @Override
    protected void onDestroy() {
        // room id have to be set to default, otherwise I'm in the room always!
        mRoomId = -1;
        super.onDestroy();
    }

    private void initComponent() {
        toolbar = (LinearLayout) findViewById(R.id.toolbar);
        ImageView imvBackButton = (ImageView) findViewById(R.id.chl_imv_back_Button);

        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", mRoomId).findFirst();
        findViewById(R.id.imgMutedRoom).setVisibility(realmRoom.getMute() ? View.VISIBLE : View.GONE);
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

        ImageView imvMenuButton = (ImageView) findViewById(R.id.chl_imv_menu_button);


        imvSmileButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_smile_button);

        edtChat = (EmojiEditText) findViewById(R.id.chl_edt_chat);
        edtChat.requestFocus();


        imvSendButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_send_button);


        imvAttachFileButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_attach_button);
        layoutAttachBottom = (LinearLayout) findViewById(R.id.layoutAttachBottom);


        imvMicButton = (MaterialDesignTextView) findViewById(R.id.chl_imv_mic_button);


        recyclerView = (RecyclerView) findViewById(R.id.chl_recycler_view_chat);
        recyclerView.addOnScrollListener(new RecyclerViewPauseOnScrollListener(ImageLoader.getInstance(), false, true));
        // remove notifying fancy animation
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        // following lines make scrolling smoother
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);

        mAdapter = new ChatMessagesFastAdapter<>(this, this);

        long identifier = 100;
        for (StructMessageInfo messageInfo : getChatList()) {
            switch (messageInfo.messageType) {
                // TODO: 9/7/2016 [Alireza Eskandarpour Shoferi] add group items
                case TEXT:
                    if (chatType == ProtoGlobal.Room.Type.CHAT) {
                        mAdapter.add(new MessageItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                    } else if (chatType == ProtoGlobal.Room.Type.CHANNEL) {
                        mAdapter.add(new ChannelMessageItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                    }
                    break;
                case IMAGE:
                    if (chatType == ProtoGlobal.Room.Type.CHAT) {
                        mAdapter.add(new ImageItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                    } else if (chatType == ProtoGlobal.Room.Type.CHANNEL) {
                        mAdapter.add(new ChannelImageItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                    }
                    break;
                case IMAGE_TEXT:
                    if (chatType == ProtoGlobal.Room.Type.CHAT) {
                        mAdapter.add(new ImageWithTextItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                    } else if (chatType == ProtoGlobal.Room.Type.CHANNEL) {
                        mAdapter.add(new ChannelImageItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                    }
                    break;
                case VIDEO:
                    if (chatType == ProtoGlobal.Room.Type.CHAT) {
                        mAdapter.add(new VideoItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                    } else if (chatType == ProtoGlobal.Room.Type.CHANNEL) {
                        mAdapter.add(new ChannelVideoItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                    }
                    break;
                case VIDEO_TEXT:
                    if (chatType == ProtoGlobal.Room.Type.CHAT) {
                        mAdapter.add(new VideoWithTextItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                    } else if (chatType == ProtoGlobal.Room.Type.CHANNEL) {
                        mAdapter.add(new ChannelVideoItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                    }
                    break;
                case FILE:
                case FILE_TEXT:
                    if (chatType == ProtoGlobal.Room.Type.CHAT) {
                        mAdapter.add(new FileItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                    } else if (chatType == ProtoGlobal.Room.Type.CHANNEL) {
                        mAdapter.add(new ChannelFileItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                    }
                    break;
                case VOICE:
                case VOICE_TEXT:
                    if (chatType == ProtoGlobal.Room.Type.CHAT) {
                        mAdapter.add(new VoiceItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                    } else if (chatType == ProtoGlobal.Room.Type.CHANNEL) {
                        mAdapter.add(new ChannelVoiceItem(chatType).setMessage(messageInfo).withIdentifier(identifier));
                    }
                    break;
            }
            identifier++;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(ActivityChat.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
                }

            }
        });

        //TODO [Saeed Mozaffari] [2016-09-07 5:12 PM] - if user have image set image otherwise set alphabet
        imvUserPicture.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvUserPicture.getContext().getResources().getDimension(R.dimen.dp60), initialize, color));

        imvMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btn click");
            }
        });

        imvSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("MMM", "Send Message Start");

                // if use click on a message, the message's text will be put to the EditText
                // i set the message object for that view's tag to obtain it here
                // request message edit only if there is any changes to the message text
                if (edtChat.getTag() != null && edtChat.getTag() instanceof StructMessageInfo) {
                    StructMessageInfo messageInfo = (StructMessageInfo) edtChat.getTag();
                    final String message = edtChat.getText().toString().trim();
                    if (!message.equals(messageInfo.messageText)) {
                        // send edit message request
                        new RequestChatEditMessage().chatEditMessage(mRoomId, Long.parseLong(messageInfo.messageID), message);

                        // should be null after requesting
                        edtChat.setTag(null);
                        edtChat.setText("");
                    }
                } else {
                    // new message has written
                    final String message = edtChat.getText().toString().trim();
                    final Realm realm = Realm.getDefaultInstance();
                    final long senderId = realm.where(RealmUserInfo.class).findFirst().getUserId();
                    if (!message.isEmpty()) {
                        final String identity = Long.toString(System.currentTimeMillis());

                        new ChatSendMessageUtil()
                                .newBuilder(ProtoGlobal.RoomMessageType.TEXT, mRoomId)
                                .message(message)
                                .sendMessage(identity);

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmChatHistory chatHistory = new RealmChatHistory();
                                RealmRoomMessage roomMessage = new RealmRoomMessage();

                                roomMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT.toString());
                                roomMessage.setMessage(message);
                                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                                roomMessage.setMessageId(Long.parseLong(identity));
                                roomMessage.setUserId(senderId);
                                roomMessage.setUpdateTime((int) System.currentTimeMillis());

                                // user wants to replay to a message
                                if (mReplayLayout != null && mReplayLayout.getTag() instanceof StructMessageInfo) {
                                    // TODO: 9/10/2016 [Alireza Eskandarpour Shoferi] after server done creating replay, uncomment following lines
                                    /*messageInfo.replayFrom = ((StructMessageInfo) mReplayLayout.getTag()).senderName;
                                    messageInfo.replayMessage = ((StructMessageInfo) mReplayLayout.getTag()).messageText;
                                    messageInfo.replayPicturePath = ((StructMessageInfo) mReplayLayout.getTag()).filePic;*/
                                }

                                chatHistory.setRoomId(mRoomId);
                                chatHistory.setRoomMessage(roomMessage);
                                realm.copyToRealm(chatHistory);
                            }
                        });
                        realm.close();

                        StructMessageInfo messageInfo = new StructMessageInfo();
                        messageInfo.messageText = message;
                        messageInfo.messageID = identity;
                        messageInfo.messageType = ProtoGlobal.RoomMessageType.TEXT;
                        messageInfo.time = System.currentTimeMillis();
                        messageInfo.senderID = Long.toString(senderId);
                        messageInfo.sendType = MyType.SendType.send;
                        // user wants to replay to a message
                        if (mReplayLayout != null && mReplayLayout.getTag() instanceof StructMessageInfo) {
                            messageInfo.replayFrom = ((StructMessageInfo) mReplayLayout.getTag()).senderName;
                            messageInfo.replayMessage = ((StructMessageInfo) mReplayLayout.getTag()).messageText;
                            messageInfo.replayPicturePath = ((StructMessageInfo) mReplayLayout.getTag()).filePic;
                        }

                        mAdapter.add(new MessageItem(chatType).setMessage(messageInfo));

                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                            }
                        }, 300);

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

                return false;
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
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
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

    private void changStatusBarColor(String color) {

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor(color));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //status bar height
            int statusBarHeight = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }
            if (statusBarHeight > 0) {
                View view1 = new View(ActivityChat.this);
                view1.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                view1.getLayoutParams().height = statusBarHeight;
                ((ViewGroup) w.getDecorView()).addView(view1);
                view1.setBackgroundColor(Color.parseColor(color));
            }
        }

    }

    private StructMessageInfo buildStructForImage(long messageId, String filePath, MyType.FileState fileState) {
        return buildStruct(messageId, null, 0, null, null, filePath, null, fileState, ProtoGlobal.RoomMessageType.IMAGE);
    }

    private StructMessageInfo buildStructForVideo(long messageId, String fileInfo, long fileSize, String fileMime, String fileName, String filePic, String filePath, MyType.FileState fileState) {
        return buildStruct(messageId, fileInfo, fileSize, fileMime, fileName, filePath, filePic, fileState, ProtoGlobal.RoomMessageType.VIDEO);
    }

    private StructMessageInfo buildStructForFile(long messageId, String fileInfo, long fileSize, String fileMime, String fileName, String filePic, String filePath, MyType.FileState fileState) {
        return buildStruct(messageId, fileInfo, fileSize, fileMime, fileName, filePath, filePic, fileState, ProtoGlobal.RoomMessageType.FILE);
    }

    private StructMessageInfo buildStruct(long messageId, String fileInfo, long fileSize, String fileMime, String fileName, String filePath, String filePic, MyType.FileState fileState, ProtoGlobal.RoomMessageType messageType) {
        StructMessageInfo messageInfo = new StructMessageInfo();
        messageInfo.time = System.currentTimeMillis();
        messageInfo.sendType = MyType.SendType.send;
        messageInfo.messageID = Long.toString(messageId);
        messageInfo.fileInfo = fileInfo;
        messageInfo.fileMime = fileMime;
        messageInfo.fileName = fileName;
        messageInfo.filePath = filePath;
        messageInfo.filePic = filePic;
        messageInfo.fileState = fileState;
        messageInfo.fileSize = fileSize;
        messageInfo.messageType = messageType;
        Realm realm = Realm.getDefaultInstance();
        messageInfo.senderID = Long.toString(realm.where(RealmUserInfo.class).findFirst().getUserId());

        return messageInfo;
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
            // TODO: 9/15/2016 [Alireza Eskandarpour Shoferi] null haye paeen bayad por shavand va hamintor 0 ha
            long messageId = System.currentTimeMillis();
            switch (requestCode) {

                case AttachFile.request_code_TAKE_PICTURE:
                    mAdapter.add(new ImageItem(chatType).setMessage(buildStructForImage(messageId, AttachFile.imagePath, MyType.FileState.uploading)));
                    Log.e("ddd", AttachFile.imagePath + "     image path");
                    break;
                case AttachFile.request_code_media_from_gallary:
                    mAdapter.add(new ImageItem(chatType).setMessage(buildStructForImage(messageId, AttachFile.getFilePathFromUri(data.getData()), MyType.FileState.uploading)));
                    Log.e("ddd", AttachFile.getFilePathFromUri(data.getData()) + "    gallary file path");
                    break;
                case AttachFile.request_code_VIDEO_CAPTURED:
                    mAdapter.add(new VideoItem(chatType).setMessage(buildStructForVideo(messageId, null, 0, null, null, null, AttachFile.imagePath, MyType.FileState.uploading)));
                    Log.e("ddd", AttachFile.getFilePathFromUri(data.getData()) + "    video capture path");
                    break;
                case AttachFile.request_code_pic_audi:
                    // TODO
                    Log.e("ddd", AttachFile.getFilePathFromUri(data.getData()) + "    audio  path");
                    break;
                case AttachFile.request_code_pic_file:
                    // TODO
                    mAdapter.add(new FileItem(chatType).setMessage(buildStructForFile(messageId, null, 0, null, null, null, AttachFile.imagePath, MyType.FileState.uploading)));
                    Log.e("ddd", data.getData() + "    pic file path");
                    break;
                case AttachFile.request_code_contact_phone:
                    // TODO
                    Log.e("ddd", data.getData() + "   contact phone");
                    break;
                case AttachFile.request_code_paint:
                    mAdapter.add(new ImageItem(chatType).setMessage(buildStructForImage(messageId, data.getData().getPath(), MyType.FileState.uploading)));
                    Log.e("ddd", data.getData() + "   pain path");
                    break;
            }
        }
    }

    private LinearLayout mReplayLayout;

    private void inflateReplayLayoutIntoStub(AbstractChatItem chatItem) {
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
            replayTo.setText(chatItem.mMessage.messageText);
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
                if (mAdapter != null) {
                    Set<AbstractChatItem> messages = mAdapter.getSelectedItems();
                    // replay works if only one message selected
                    if (messages.size() > 0 && messages.size() <= 1) {
                        inflateReplayLayoutIntoStub(messages.iterator().next());

                        ll_AppBarSelected.setVisibility(View.GONE);
                        toolbar.setVisibility(View.VISIBLE);

                        mAdapter.deselect();
                    }
                }
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
        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnDeleteSelected");
                for (AbstractChatItem messageID : mAdapter.getSelectedItems()) {
                    new RequestChatDeleteMessage().chatDeleteMessage(mRoomId, Long.parseLong(messageID.mMessage.messageID));
                }
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
        LinearLayout layoutChannelFooter = (LinearLayout) findViewById(R.id.chl_ll_channel_footer);

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
        long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        ArrayList<StructMessageInfo> messageList = new ArrayList<>();
        for (RealmChatHistory realmChatHistory : realm.where(RealmChatHistory.class).equalTo("roomId", mRoomId).findAll()) {
            RealmRoomMessage roomMessage = realmChatHistory.getRoomMessage();
            if (roomMessage != null) {
                StructMessageInfo structMessageInfo = new StructMessageInfo();
                structMessageInfo.messageText = roomMessage.getMessage();
                structMessageInfo.status = roomMessage.getStatus();
                structMessageInfo.messageID = Long.toString(roomMessage.getMessageId());
                structMessageInfo.time = roomMessage.getUpdateTime();
                structMessageInfo.senderID = Long.toString(roomMessage.getUserId());
                structMessageInfo.isEdited = roomMessage.isEdited();
                if (roomMessage.getUserId() == userId) {
                    structMessageInfo.sendType = MyType.SendType.send;
                } else if (roomMessage.getUserId() != userId) {
                    structMessageInfo.sendType = MyType.SendType.recvive;
                }
                // TODO: 9/5/2016 [Alireza Eskandarpour Shoferi] add timeLayouts to adapter
                if (realmChatHistory.getRoomMessage().getMessageType().equals("TEXT")) {
                    structMessageInfo.messageType = ProtoGlobal.RoomMessageType.TEXT;
                }
                messageList.add(structMessageInfo);
            }
        }
        realm.close();
        return messageList;
    }


    /*   private ArrayList<StructMessageInfo> getChatList() {

           ArrayList<StructMessageInfo> list = new ArrayList<>();

           StructMessageInfo c = new StructMessageInfo();
           c.messageType = ProtoGlobal.RoomMessageType.IMAGE_TEXT;
           c.sendType = MyType.SendType.send;
           c.status = ProtoGlobal.RoomMessageStatus.SENDING.toString();
           c.senderAvatar = R.mipmap.a + "";
           c.messageID = "123";
           c.filePath = R.mipmap.a + "";
           c.messageText = "where are you  going hgf hgf hgf hgf  good good";
           c.channelLink = "@igap";
           list.add(c);


           StructMessageInfo c9 = new StructMessageInfo();
           c9.messageType = ProtoGlobal.RoomMessageType.FILE;
           c9.forwardMessageFrom = "ali";
           c9.replayFrom = "ali";
           c9.status = ProtoGlobal.RoomMessageStatus.DELIVERED.toString();
           c9.messageID = "123";
           c9.replayMessage = "where are you djkdj kjf kdj";
           c9.sendType = MyType.SendType.send;
           c9.senderAvatar = R.mipmap.a + "";
           list.add(c9);

           StructMessageInfo c10 = new StructMessageInfo();
           c10.messageType = ProtoGlobal.RoomMessageType.FILE;
           c10.forwardMessageFrom = "hasan";
           c10.replayFrom = "mehdi";
           c10.status = ProtoGlobal.RoomMessageStatus.FAILED.toString();
           c10.messageID = "123";
           c10.replayMessage = "i am fine";
           c10.replayPicturePath = " fd";
           c10.sendType = MyType.SendType.recvive;
           c10.senderAvatar = R.mipmap.a + "";
           list.add(c10);



           StructMessageInfo c16 = new StructMessageInfo();
           c16.sendType = MyType.SendType.timeLayout;
           c16.status = ProtoGlobal.RoomMessageStatus.FAILED.toString();
           c16.messageID = "123";
           list.add(c16);

           StructMessageInfo c13 = new StructMessageInfo();
           c13.messageType = ProtoGlobal.RoomMessageType.VOICE;
           c13.status = ProtoGlobal.RoomMessageStatus.SENT.toString();
           c13.messageID = "123";
           c13.sendType = MyType.SendType.send;
           list.add(c13);

           StructMessageInfo c14 = new StructMessageInfo();
           c14.messageType =ProtoGlobal.RoomMessageType.VOICE;
           c14.status = ProtoGlobal.RoomMessageStatus.SEEN.toString();
           c14.messageID = "123";
           c14.sendType = MyType.SendType.recvive;
           c14.messageText = "بهترین موسیقی منتخب" +
                   "\n" + " how are you fghg hgfh fhgf hgf hgf hgf hgf fjhg jhg jhgjhg jhgjh";
           c14.senderAvatar = R.mipmap.a + "";
           list.add(c14);



           StructMessageInfo c11 = new StructMessageInfo();
           c11.messageType = ProtoGlobal.RoomMessageType.FILE;
           c11.sendType = MyType.SendType.recvive;
           c11.messageID = "123";
           c11.status = ProtoGlobal.RoomMessageStatus.SEEN.toString();
           c11.senderAvatar = R.mipmap.a + "";
           list.add(c11);

           StructMessageInfo c1 = new StructMessageInfo();
           c1.messageType = ProtoGlobal.RoomMessageType.TEXT;
           c1.status = ProtoGlobal.RoomMessageStatus.SEEN.toString();
           c1.messageID = "123";
           c1.messageText = "how";
           c1.sendType = MyType.SendType.send;
           c1.senderAvatar = R.mipmap.b + "";
           c1.messageID = "123";
           list.add(c1);

           StructMessageInfo c2 = new StructMessageInfo();
           c2.messageType = ProtoGlobal.RoomMessageType.TEXT;
           c2.status = ProtoGlobal.RoomMessageStatus.SEEN.toString();
           c2.senderAvatar = R.mipmap.c + "";
           c2.messageText = "i am fine";
           c2.messageID = "123";
           c2.forwardMessageFrom = "Android cade";
           c2.sendType = MyType.SendType.recvive;
           list.add(c2);

           StructMessageInfo c3 = new StructMessageInfo();
           c3.messageType = ProtoGlobal.RoomMessageType.VIDEO_TEXT;
           c3.messageText = "where are you  going hgf hgf hgf hgf  good good";
           c3.sendType = MyType.SendType.send;
           c3.fileName = "good video";
           c3.status = ProtoGlobal.RoomMessageStatus.SEEN.toString();
           c3.fileMime = ".mpv";
           c3.messageID = "123";
           c3.fileState = MyType.FileState.notUpload;
           c3.fileInfo = "3:20 (2.4 MB)";
           c3.filePic = R.mipmap.a + "";
           list.add(c3);

           StructMessageInfo c4 = new StructMessageInfo();
           c4.messageType =ProtoGlobal.RoomMessageType.IMAGE_TEXT;
           c4.forwardMessageFrom = "ali";
           c4.messageText = "the good picture for all the word";
           c4.status = ProtoGlobal.RoomMessageStatus.SEEN.toString();
           c4.sendType = MyType.SendType.recvive;
           c4.messageID = "123";
           c4.senderAvatar = R.mipmap.d + "";
           c4.filePath = R.mipmap.c + "";
           list.add(c4);

           StructMessageInfo c5 = new StructMessageInfo();
           c5.messageType = ProtoGlobal.RoomMessageType.IMAGE;
           c5.forwardMessageFrom = "ali";
           c5.sendType = MyType.SendType.send;
           c5.status = ProtoGlobal.RoomMessageStatus.SEEN.toString();
           c5.senderAvatar = R.mipmap.e + "";
           c5.messageID = "123";
           c5.filePath = R.mipmap.e + "";
           list.add(c5);

           StructMessageInfo c6 = new StructMessageInfo();
           c6.messageType = ProtoGlobal.RoomMessageType.IMAGE;
           c6.sendType = MyType.SendType.recvive;
           c6.messageID = "123";
           c6.status = ProtoGlobal.RoomMessageStatus.SEEN.toString();
           c6.senderAvatar = R.mipmap.f + "";
           c6.filePath = R.mipmap.f + "";
           list.add(c6);

           StructMessageInfo c7 = new StructMessageInfo();
           c7.messageType = ProtoGlobal.RoomMessageType.IMAGE;
           c7.sendType = MyType.SendType.recvive;
           c7.messageID = "123";
           c7.status = ProtoGlobal.RoomMessageStatus.SEEN.toString();
           c7.senderAvatar = R.mipmap.g + "";
           c7.filePath = R.mipmap.g + "";
           list.add(c7);

           StructMessageInfo c8 = new StructMessageInfo();
           c8.messageType = ProtoGlobal.RoomMessageType.IMAGE;
           c8.messageID = "123";
           c8.status = ProtoGlobal.RoomMessageStatus.SENDING.toString();
           c8.sendType = MyType.SendType.send;
           c8.filePath = R.mipmap.h + "";
           list.add(c8);

           return list;
       }
   */
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
            case VOICE_TEXT:
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
                            // delete message
                            new RequestChatDeleteMessage().chatDeleteMessage(mRoomId, Long.parseLong(messageInfo.messageID));
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

    @Override
    public void onMessageUpdated(final long messageId, final ProtoGlobal.RoomMessageStatus status, final String identity, ProtoChatSendMessage.ChatSendMessageResponse.Builder roomMessage) {
        // I'm in the room
        if (roomMessage.getRoomId() == mRoomId) {
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
    public void onReceiveChatMessage(String message, String messageType, final ProtoChatSendMessage.ChatSendMessageResponse.Builder roomMessage) {
        Log.i(ActivityChat.class.getSimpleName(), "onReceiveChatMessage called");
        // I'm in the room
        if (roomMessage.getRoomId() == mRoomId) {
            // I'm in the room, so unread messages count is 0. it means, I read all messages
            Realm realm = Realm.getDefaultInstance();
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
            realm.close();
            // make update status to message sender that i've read his message
            G.chatUpdateStatusUtil.sendUpdateStatus(roomMessage.getRoomId(), roomMessage.getRoomMessage().getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.add(new MessageItem(chatType).setMessage(HelperProtoBuilder.convert(roomMessage)));
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                        }
                    }, 300);
                }
            });
        } else {
            // user has received the message, so I make a new delivered update status request
            G.chatUpdateStatusUtil.sendUpdateStatus(roomMessage.getRoomId(), roomMessage.getRoomMessage().getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
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
}
