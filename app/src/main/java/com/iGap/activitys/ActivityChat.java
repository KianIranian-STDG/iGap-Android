package com.iGap.activitys;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterChatMessage;
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
import com.iGap.interface_package.OnChatSendMessageResponse;
import com.iGap.interface_package.OnChatUpdateStatusResponse;
import com.iGap.interface_package.OnMessageClick;
import com.iGap.module.EmojiEditText;
import com.iGap.module.EmojiPopup;
import com.iGap.module.EmojiRecentsManager;
import com.iGap.module.MyType;
import com.iGap.module.OnComplete;
import com.iGap.module.StructMessageInfo;
import com.iGap.proto.ProtoChatSendMessage;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestChatDeleteMessage;
import com.iGap.request.RequestChatEditMessage;
import com.iGap.request.RequestChatSendMessage;
import com.iGap.request.RequestChatUpdateStatus;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by android3 on 8/5/2016.
 */
public class ActivityChat extends ActivityEnhanced implements IEmojiViewCreate, IRecentsLongClick, OnMessageClick, OnChatClearMessageResponse {

    private EmojiEditText edtChat;
    private Button btnSend;
    private Button btnAttachFile;
    private Button btnMic;

    private Button btnCloseAppBarSelected;
    private Button btnReplaySelected;
    private Button btnCopySelected;
    private Button btnForwardSelected;
    private Button btnDeleteSelected;
    private TextView txtNumberOfSelected;
    private LinearLayout ll_AppBarSelected;

    private TextView txtName;
    private TextView txtLastSeen;
    private TextView txt_mute;
    private ImageView imvUserPicture;
    private RecyclerView recyclerView;
    private ImageButton btnSmile;

    private AdapterChatMessage mAdapter;
    private MyType.ChatType chatType;
    private String contactId;
    private boolean isMute = false;
    private boolean newChatRoom = false;
    private MyType.OwnerShip ownerShip;

    private String contactName;
    private String memberCount;
    private String lastSeen;
    private long mRoomId;


    private Button btnUp;
    private Button btnDown;
    private TextView txtChannelMute;

    private AppBarLayout appBarLayout;

    private OnComplete complete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        G.clearMessagesUtil.setOnChatClearMessageResponse(this);

        Bundle bundle = getIntent().getExtras();  // get chat type and contact id and  finish activity if value equal null
        if (bundle != null) {
//            chatType = (MyType.ChatType) bundle.getSerializable("ChatType"); //TODO [Saeed Mozaffari] [2016-09-03 12:10 PM] - change type to ProtoGlobal.Room.Type
            String type = bundle.getString("ChatType");
            if (type.equals("CHAT")) {
                chatType = MyType.ChatType.singleChat;
            }
            contactId = bundle.getString("ContactID");
            isMute = bundle.getBoolean("IsMute");
            ownerShip = (MyType.OwnerShip) bundle.getSerializable("OwnerShip");
            contactName = bundle.getString("ContactName");
            memberCount = bundle.getString("MemberCount");
            lastSeen = Long.toString(bundle.getLong("LastSeen"));
            mRoomId = bundle.getLong("RoomId");
            newChatRoom = bundle.getBoolean("NewChatRoom");
            Log.i("MMM", "roomId : " + mRoomId);
        }

        initComponent();
        initAppbarSelected();
        initCallbacks();

        if (chatType == MyType.ChatType.channel && ownerShip == MyType.OwnerShip.member)
            initLayotChannelFooter();

    }

    public void initCallbacks() {
        G.onChatSendMessageResponse = new OnChatSendMessageResponse() {
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
                    Log.i(ActivityChat.class.getSimpleName(), "onReceiveChatMessage 1");
                    // make update status to message sender that i've read his message
                    new RequestChatUpdateStatus().updateStatus(roomMessage.getRoomId(), roomMessage.getRoomMessage().getMessageId(), ProtoGlobal.RoomMessageStatus.SEEN);
                } else {
                    Log.i(ActivityChat.class.getSimpleName(), "onReceiveChatMessage 2");
                    // I'm not in the room (or another room), so I just received the message (the message has delivered)
                    new RequestChatUpdateStatus().updateStatus(roomMessage.getRoomId(), roomMessage.getRoomMessage().getMessageId(), ProtoGlobal.RoomMessageStatus.DELIVERED);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.insert(HelperProtoBuilder.convert(roomMessage));

                        int position = recyclerView.getAdapter().getItemCount();
                        if (position > 0)
                            recyclerView.scrollToPosition(position - 1);
                    }
                });
            }
        };

        G.onChatUpdateStatusResponse = new OnChatUpdateStatusResponse() {
            @Override
            public void onChatUpdateStatus(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, int statusVersion) {
                Log.i(ActivityChat.class.getSimpleName(), "onChatUpdateStatus called");

                // I'm in the room
                if (mRoomId == roomId) {
                    // so update the message status ina adapter
                    mAdapter.updateMessageStatus(messageId, status);
                }
            }
        };

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

    private void initComponent() {

        appBarLayout = (AppBarLayout) findViewById(R.id.chl_appbar_main);

        Button btnBack = (Button) findViewById(R.id.chl_btn_back);
        btnBack.setTypeface(G.fontawesome);

        txtName = (TextView) findViewById(R.id.chl_txt_name);
        txtName.setTypeface(G.arialBold);
        if (contactName != null)
            txtName.setText(contactName);


        txtLastSeen = (TextView) findViewById(R.id.chl_txt_last_seen);

        if (chatType == MyType.ChatType.channel || chatType == MyType.ChatType.groupChat) {
            if (memberCount != null)
                txtLastSeen.setText(memberCount + " member");
        } else {
            if (lastSeen != null)
                txtLastSeen.setText(lastSeen);
        }

        txt_mute = (TextView) findViewById(R.id.chl_txt_mute);
        txt_mute.setTypeface(G.fontawesome);

        if (isMute) {
            txt_mute.setVisibility(View.VISIBLE);
        }


        imvUserPicture = (ImageView) findViewById(R.id.chl_imv_user_picture);

        Button btnMenu = (Button) findViewById(R.id.chl_btn_menu);
        btnMenu.setTypeface(G.fontawesome);


        btnSmile = (ImageButton) findViewById(R.id.chl_btn_smile);

        edtChat = (EmojiEditText) findViewById(R.id.chl_edt_chat);
        edtChat.requestFocus();


        btnSend = (Button) findViewById(R.id.chl_btn_send);
        btnSend.setTypeface(G.fontawesome);

        btnAttachFile = (Button) findViewById(R.id.chl_btn_attach);
        btnAttachFile.setTypeface(G.fontawesome);

        btnMic = (Button) findViewById(R.id.chl_btn_mic);
        btnMic.setTypeface(G.fontawesome);


        complete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                int whatAction = 0;
                String number = "0";

                if (messageOne != null)
                    if (messageOne.length() > 0)
                        whatAction = Integer.parseInt(messageOne);

                if (MessageTow != null)
                    if (MessageTow.length() > 0)
                        number = MessageTow;

                callBack(result, whatAction, number);
            }
        };

        recyclerView = (RecyclerView) findViewById(R.id.chl_recycler_view_chat);
        mAdapter = new AdapterChatMessage(ActivityChat.this, chatType, getChatList(), complete, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityChat.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        int position = recyclerView.getAdapter().getItemCount();
        if (position > 0)
            recyclerView.scrollToPosition(position - 1);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imvUserPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
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

                        new RequestChatSendMessage()
                                .newBuilder(ProtoGlobal.RoomMessageType.TEXT, mRoomId)
                                .message(message)
                                .sendMessage(identity);

                        realm.executeTransactionAsync(new Realm.Transaction() {
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
                                chatHistory.setRoomId(mRoomId);
                                chatHistory.setRoomMessage(roomMessage);
                                realm.copyToRealm(chatHistory);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        StructMessageInfo messageInfo = new StructMessageInfo();
                                        messageInfo.messageText = message;
                                        messageInfo.messageID = identity;
                                        messageInfo.messageType = ProtoGlobal.RoomMessageType.TEXT;
                                        messageInfo.senderID = Long.toString(senderId);
                                        messageInfo.sendType = MyType.SendType.send;
                                        mAdapter.insert(messageInfo);

                                        realm.close();
                                    }
                                });
                            }
                        });

                        edtChat.setText("");
                    } else {
                        Toast.makeText(G.context, "Please Write Your Messge!", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        btnAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        btnMic.setOnLongClickListener(new View.OnLongClickListener() {
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
                changeEmojiButtonImageResource(R.drawable.ic_emoticon);
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
        btnSmile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // if popup is not showing => emoji keyboard is not visible, we need to show it
                if (!emojiPopup.isShowing()) {
                    // if keyboard is visible, simply show the emoji popup
                    if (emojiPopup.isKeyboardOpen()) {
                        emojiPopup.showAtBottom();
                        changeEmojiButtonImageResource(R.drawable.ic_keyboard);
                    }
                    // else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        edtChat.setFocusableInTouchMode(true);
                        edtChat.requestFocus();

                        emojiPopup.showAtBottomPending();

                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(edtChat, InputMethodManager.SHOW_IMPLICIT);

                        changeEmojiButtonImageResource(R.drawable.ic_keyboard);
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
                    btnAttachFile.setVisibility(View.GONE);
                    btnMic.setVisibility(View.GONE);
                    btnSend.setVisibility(View.VISIBLE);
                } else {
                    btnSend.setVisibility(View.GONE);
                    btnAttachFile.setVisibility(View.VISIBLE);
                    btnMic.setVisibility(View.VISIBLE);
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

    private void changeEmojiButtonImageResource(@DrawableRes int drawableResourceId) {
        btnSmile.setImageResource(drawableResourceId);
    }

    @Override
    public void onEmojiViewCreate(View view, EmojiPopup emojiPopup) {

    }

    @Override
    public boolean onRecentsLongClick(View view, EmojiRecentsManager recentsManager) {
        // TODO useful for clearing recents
        return false;
    }

    private void initAppbarSelected() {

        btnCloseAppBarSelected = (Button) findViewById(R.id.chl_btn_close_layout);
        btnCloseAppBarSelected.setTypeface(G.fontawesome);
        btnCloseAppBarSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.resetSelected();
            }
        });

        btnReplaySelected = (Button) findViewById(R.id.chl_btn_replay_selected);
        btnReplaySelected.setTypeface(G.fontawesome);
        btnReplaySelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnReplaySelected");
            }
        });

        btnCopySelected = (Button) findViewById(R.id.chl_btn_copy_selected);
        btnCopySelected.setTypeface(G.fontawesome);
        btnCopySelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnCopySelected");
            }
        });


        btnForwardSelected = (Button) findViewById(R.id.chl_btn_forward_selected);
        btnForwardSelected.setTypeface(G.fontawesome);
        btnForwardSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnForwardSelected");
            }
        });

        btnDeleteSelected = (Button) findViewById(R.id.chl_btn_delete_selected);
        btnDeleteSelected.setTypeface(G.fontawesome);
        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnDeleteSelected");
                for (String messageID : mAdapter.getSelectedMessages()) {
                    new RequestChatDeleteMessage().chatDeleteMessage(mRoomId, Long.parseLong(messageID));
                }
            }
        });

        txtNumberOfSelected = (TextView) findViewById(R.id.chl_txt_number_of_selected);
        txtNumberOfSelected.setTypeface(G.fontawesome);


        ll_AppBarSelected = (LinearLayout) findViewById(R.id.chl_ll_appbar_selelected);

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

                appBarLayout.setExpanded(true, true);
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

    private void callBack(boolean result, int whatAction, String number) {

        switch (whatAction) {

            case 1://for show or gone layout appBar selected
                if (result) {
                    ll_AppBarSelected.setVisibility(View.VISIBLE);
                    txtNumberOfSelected.setText(number);

                    int x = Integer.parseInt(number);
                    if (x > 1)
                        btnReplaySelected.setVisibility(View.INVISIBLE);
                    else
                        btnReplaySelected.setVisibility(View.VISIBLE);
                } else {
                    ll_AppBarSelected.setVisibility(View.GONE);
                    appBarLayout.setExpanded(true, true);
                }
                break;

        }


    }


    @Override
    public void onBackPressed() {

        if (!mAdapter.resetSelected()) {
            super.onBackPressed();
        }
    }

    private ArrayList<StructMessageInfo> getChatList() {
        Realm realm = Realm.getDefaultInstance();
        long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        ArrayList<StructMessageInfo> messageList = new ArrayList<>();
        for (RealmChatHistory realmChatHistory : G.realm.where(RealmChatHistory.class).equalTo("roomId", mRoomId).findAll()) {
            RealmRoomMessage roomMessage = realmChatHistory.getRoomMessage();
            if (roomMessage != null) {
                StructMessageInfo structMessageInfo = new StructMessageInfo();
                structMessageInfo.messageText = roomMessage.getMessage();
                structMessageInfo.status = roomMessage.getStatus();
                structMessageInfo.messageID = Long.toString(roomMessage.getMessageId());
                structMessageInfo.senderID = Long.toString(roomMessage.getUserId());
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
        return messageList;
    }


    /*private ArrayList<StructMessageInfo> getChatList() {

        ArrayList<StructMessageInfo> list = new ArrayList<>();

        StructMessageInfo c = new StructMessageInfo();
        c.messageType = MyType.MessageType.image;
        c.sendType = MyType.SendType.send;
        c.status = ProtoGlobal.RoomMessageStatus.SENDING;
        c.senderAvatar = R.mipmap.a + "";
        c.messageID = "123";
        c.filePath = R.mipmap.a + "";
        c.messageText = "where are you  going hgf hgf hgf hgf  good good";
        c.channelLink = "@igap";
        c.seen = "122k";
        list.add(c);


        StructMessageInfo c9 = new StructMessageInfo();
        c9.messageType = MyType.MessageType.files;
        c9.forwardMessageFrom = "ali";
        c9.replayFrom = "ali";
        c9.status = ProtoGlobal.RoomMessageStatus.DELIVERED;
        c9.messageID = "123";
        c9.replayMessage = "where are you djkdj kjf kdj";
        c9.sendType = MyType.SendType.send;
        c9.senderAvatar = R.mipmap.a + "";
        list.add(c9);

        StructMessageInfo c10 = new StructMessageInfo();
        c10.messageType = MyType.MessageType.files;
        c10.forwardMessageFrom = "hasan";
        c10.replayFrom = "mehdi";
        c10.status = ProtoGlobal.RoomMessageStatus.FAILED;
        c10.messageID = "123";
        c10.replayMessage = "i am fine";
        c10.replayPicturePath = " fd";
        c10.sendType = MyType.SendType.recvive;
        c10.senderAvatar = R.mipmap.a + "";
        list.add(c10);

        StructMessageInfo c12 = new StructMessageInfo();
        c12.messageType = MyType.MessageType.gif;
        c12.status = ProtoGlobal.RoomMessageStatus.SEEN;
        c12.messageID = "123";
        c12.sendType = MyType.SendType.send;
        list.add(c12);

        StructMessageInfo c16 = new StructMessageInfo();
        c16.sendType = MyType.SendType.timeLayout;
        c16.status = ProtoGlobal.RoomMessageStatus.SENT;
        c16.messageID = "123";
        list.add(c16);

        StructMessageInfo c13 = new StructMessageInfo();
        c13.messageType = MyType.MessageType.audio;
        c13.status = ProtoGlobal.RoomMessageStatus.SENT;
        c13.messageID = "123";
        c13.sendType = MyType.SendType.send;
        list.add(c13);

        StructMessageInfo c14 = new StructMessageInfo();
        c14.messageType = MyType.MessageType.audio;
        c14.status = ProtoGlobal.RoomMessageStatus.SEEN;
        c14.messageID = "123";
        c14.sendType = MyType.SendType.recvive;
        c14.messageText = "بهترین موسیقی منتخب" +
                "\n" + " how are you fghg hgfh fhgf hgf hgf hgf hgf fjhg jhg jhgjhg jhgjh";
        c14.senderAvatar = R.mipmap.a + "";
        list.add(c14);

        StructMessageInfo c15 = new StructMessageInfo();
        c15.messageType = MyType.MessageType.sticker;
        c15.sendType = MyType.SendType.recvive;
        c15.status = ProtoGlobal.RoomMessageStatus.SEEN;
        c15.messageID = "123";
        c15.filePath = R.mipmap.sd + "";
        list.add(c15);

        StructMessageInfo c11 = new StructMessageInfo();
        c11.messageType = MyType.MessageType.files;
        c11.sendType = MyType.SendType.recvive;
        c11.messageID = "123";
        c11.status = ProtoGlobal.RoomMessageStatus.SEEN;
        c11.senderAvatar = R.mipmap.a + "";
        list.add(c11);

        StructMessageInfo c1 = new StructMessageInfo();
        c1.messageType = MyType.MessageType.message;
        c1.status = ProtoGlobal.RoomMessageStatus.SEEN;
        c1.messageID = "123";
        c1.messageText = "how";
        c1.sendType = MyType.SendType.send;
        c1.senderAvatar = R.mipmap.b + "";
        c1.messageID = "123";
        list.add(c1);

        StructMessageInfo c2 = new StructMessageInfo();
        c2.messageType = MyType.MessageType.message;
        c2.status = ProtoGlobal.RoomMessageStatus.SEEN;
        c2.senderAvatar = R.mipmap.c + "";
        c2.messageText = "i am fine";
        c2.messageID = "123";
        c2.forwardMessageFrom = "Android cade";
        c2.sendType = MyType.SendType.recvive;
        list.add(c2);

        StructMessageInfo c3 = new StructMessageInfo();
        c3.messageType = MyType.MessageType.video;
        c3.messageText = "where are you  going hgf hgf hgf hgf  good good";
        c3.sendType = MyType.SendType.send;
        c3.fileName = "good video";
        c3.status = ProtoGlobal.RoomMessageStatus.SEEN;
        c3.fileMime = ".mpv";
        c3.messageID = "123";
        c3.fileState = MyType.FileState.notUpload;
        c3.fileInfo = "3:20 (2.4 MB)";
        c3.filePic = R.mipmap.a + "";
        list.add(c3);

        StructMessageInfo c4 = new StructMessageInfo();
        c4.messageType = MyType.MessageType.image;
        c4.forwardMessageFrom = "ali";
        c4.messageText = "the good picture for all the word";
        c4.status = ProtoGlobal.RoomMessageStatus.SEEN;
        c4.sendType = MyType.SendType.recvive;
        c4.messageID = "123";
        c4.senderAvatar = R.mipmap.d + "";
        c4.filePath = R.mipmap.c + "";
        list.add(c4);

        StructMessageInfo c5 = new StructMessageInfo();
        c5.messageType = MyType.MessageType.image;
        c5.forwardMessageFrom = "ali";
        c5.sendType = MyType.SendType.send;
        c5.status = ProtoGlobal.RoomMessageStatus.SEEN;
        c5.senderAvatar = R.mipmap.e + "";
        c5.messageID = "123";
        c5.filePath = R.mipmap.e + "";
        list.add(c5);

        StructMessageInfo c6 = new StructMessageInfo();
        c6.messageType = MyType.MessageType.image;
        c6.sendType = MyType.SendType.recvive;
        c6.messageID = "123";
        c6.status = ProtoGlobal.RoomMessageStatus.SEEN;
        c6.senderAvatar = R.mipmap.f + "";
        c6.filePath = R.mipmap.f + "";
        list.add(c6);

        StructMessageInfo c7 = new StructMessageInfo();
        c7.messageType = MyType.MessageType.image;
        c7.sendType = MyType.SendType.recvive;
        c7.messageID = "123";
        c7.status = ProtoGlobal.RoomMessageStatus.SEEN;
        c7.senderAvatar = R.mipmap.g + "";
        c7.filePath = R.mipmap.g + "";
        list.add(c7);

        StructMessageInfo c8 = new StructMessageInfo();
        c8.messageType = MyType.MessageType.image;
        c8.messageID = "123";
        c8.status = ProtoGlobal.RoomMessageStatus.SEEN;
        c8.sendType = MyType.SendType.send;
        c8.filePath = R.mipmap.h + "";
        list.add(c8);

        return list;
    }*/

    @Override
    public void onMessageClick(View view, StructMessageInfo messageInfo) {
        Log.i(ActivityChat.class.getSimpleName(), "Message clicked");
        // put message text to EditText
        if (!messageInfo.messageText.isEmpty()) {
            edtChat.setText(messageInfo.messageText);
            edtChat.setSelection(0, edtChat.getText().length());
            // put message object to edtChat's tag to obtain it later and
            // found is user trying to edit a message
            edtChat.setTag(messageInfo);
        }
    }

    @Override
    public void onChatClearMessage(long roomId, long clearId, ProtoResponse.Response response) {
        Log.i(ActivityChat.class.getSimpleName(), "onChatClearMessage called");
        // TODO
    }
}
