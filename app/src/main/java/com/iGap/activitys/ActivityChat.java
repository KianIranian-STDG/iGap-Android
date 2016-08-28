package com.iGap.activitys;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterChat;
import com.iGap.helper.Emojione;
import com.iGap.interface_package.IEmojiBackspaceClick;
import com.iGap.interface_package.IEmojiClickListener;
import com.iGap.interface_package.IEmojiLongClickListener;
import com.iGap.interface_package.IEmojiStickerClick;
import com.iGap.interface_package.IEmojiViewCreate;
import com.iGap.interface_package.IRecentsLongClick;
import com.iGap.interface_package.ISoftKeyboardOpenClose;
import com.iGap.module.EmojiEditText;
import com.iGap.module.EmojiPopup;
import com.iGap.module.EmojiRecentsManager;
import com.iGap.module.MyType;
import com.iGap.module.StructChatInfo;

import java.util.ArrayList;

/**
 * Created by android3 on 8/5/2016.
 */
public class ActivityChat extends ActivityEnhanced implements IEmojiViewCreate, IRecentsLongClick {

    private EmojiEditText edtChat;
    private Button btnSend;
    private Button btnAttachFile;
    private Button btnMic;
    private TextView txtName;
    private TextView txtLastSeen;
    private TextView txt_mute;
    private ImageView imvUserPicture;
    private RecyclerView recyclerView;
    ImageButton btnSmile;

    AdapterChat mAdapter;
    private MyType.ChatType chatType;
    private String contactId;
    private boolean isMute = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();  // get chat type and contact id and  finish activity if value equal null
        if (bundle != null) {
            chatType = (MyType.ChatType) bundle.getSerializable("chattype");
            contactId = bundle.getString("contactid");
            isMute = bundle.getBoolean("ismute");
        }
        if (chatType == null || contactId == null) {
            finish();
        }


        setContentView(R.layout.activity_chat);

        initComponent();
    }

    private void initComponent() {

        Button btnBack = (Button) findViewById(R.id.chl_btn_back);
        btnBack.setTypeface(G.fontawesome);

        txtName = (TextView) findViewById(R.id.chl_txt_name);
        txtLastSeen = (TextView) findViewById(R.id.chl_txt_last_seen);

        if (isMute) {
            txt_mute = (TextView) findViewById(R.id.chl_txt_mute);
            txt_mute.setTypeface(G.fontawesome);
            txt_mute.setVisibility(View.VISIBLE);
        }


        imvUserPicture = (ImageView) findViewById(R.id.chl_imv_user_picture);

        Button btnMenu = (Button) findViewById(R.id.chl_btn_menu);
        btnMenu.setTypeface(G.fontawesome);


        btnSmile = (ImageButton) findViewById(R.id.chl_btn_smile);

        edtChat = (EmojiEditText) findViewById(R.id.chl_edt_chat);


        btnSend = (Button) findViewById(R.id.chl_btn_send);
        btnSend.setTypeface(G.fontawesome);

        btnAttachFile = (Button) findViewById(R.id.chl_btn_attach);
        btnAttachFile.setTypeface(G.fontawesome);

        btnMic = (Button) findViewById(R.id.chl_btn_mic);
        btnMic.setTypeface(G.fontawesome);


        recyclerView = (RecyclerView) findViewById(R.id.chl_recycler_view_chat);
        mAdapter = new AdapterChat(ActivityChat.this, chatType, getChatList());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityChat.this);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        imvUserPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", " imvUserPicture ");
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnMenu  ");
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", " btnSend ");
            }
        });

        btnAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", " btnAttachFile ");
            }
        });

        btnMic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.e("ddd", " btnMic  ");
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


    @Override
    public void onBackPressed() {

        if (!mAdapter.resetSelected()) {
            super.onBackPressed();
        }
    }

    private ArrayList<StructChatInfo> getChatList() {

        ArrayList<StructChatInfo> list = new ArrayList<>();

        StructChatInfo c = new StructChatInfo();
        c.messageType = MyType.MessageType.image;
        c.sendType = MyType.SendType.send;
        c.senderAvatar = R.mipmap.a + "";
        c.filePath = R.mipmap.a + "";
        c.channelLink = "@igap";
        c.seen = "122k";
        list.add(c);


        StructChatInfo c9 = new StructChatInfo();
        c9.messageType = MyType.MessageType.files;
        c9.forwardMessageFrom = "ali";
        c9.replayFrom = "ali";
        c9.replayMessage = "where are you djkdj kjf kdj";
        c9.sendType = MyType.SendType.send;
        c9.senderAvatar = R.mipmap.a + "";
        list.add(c9);

        StructChatInfo c10 = new StructChatInfo();
        c10.messageType = MyType.MessageType.files;
        c10.forwardMessageFrom = "hasan";
        c10.replayFrom = "mehdi";
        c10.replayMessage = "i am fine";
        c10.replayPicturePath = " fd";
        c10.sendType = MyType.SendType.recvive;
        c10.senderAvatar = R.mipmap.a + "";
        list.add(c10);

        StructChatInfo c12 = new StructChatInfo();
        c12.messageType = MyType.MessageType.gif;
        c12.sendType = MyType.SendType.send;
        list.add(c12);

        StructChatInfo c16 = new StructChatInfo();
        c16.sendType = MyType.SendType.timeLayout;
        list.add(c16);

        StructChatInfo c13 = new StructChatInfo();
        c13.messageType = MyType.MessageType.audio;
        c13.sendType = MyType.SendType.send;
        list.add(c13);

        StructChatInfo c14 = new StructChatInfo();
        c14.messageType = MyType.MessageType.audio;
        c14.sendType = MyType.SendType.recvive;
        c14.messag = "the good media";
        list.add(c14);

        StructChatInfo c15 = new StructChatInfo();
        c15.messageType = MyType.MessageType.sticker;
        c15.sendType = MyType.SendType.recvive;
        c15.filePath = R.mipmap.sd + "";
        list.add(c15);

        StructChatInfo c11 = new StructChatInfo();
        c11.messageType = MyType.MessageType.files;
        c11.sendType = MyType.SendType.recvive;
        c11.senderAvatar = R.mipmap.a + "";
        list.add(c11);

        StructChatInfo c1 = new StructChatInfo();
        c1.messageType = MyType.MessageType.message;
        c1.messag = "how";

        c1.sendType = MyType.SendType.send;
        c1.senderAvatar = R.mipmap.b + "";
        list.add(c1);

        StructChatInfo c2 = new StructChatInfo();
        c2.messageType = MyType.MessageType.message;
        c2.senderAvatar = R.mipmap.c + "";
        c2.messag = "i am fine";
        c2.forwardMessageFrom = "Android cade";
        c2.sendType = MyType.SendType.recvive;
        list.add(c2);

        StructChatInfo c3 = new StructChatInfo();
        c3.messageType = MyType.MessageType.message;
        c3.messag = "where are you  going";
        c3.sendType = MyType.SendType.send;
        list.add(c3);

        StructChatInfo c4 = new StructChatInfo();
        c4.messageType = MyType.MessageType.image;
        c4.forwardMessageFrom = "ali";
        c4.messag = "the good picture for all the word";
        c4.sendType = MyType.SendType.recvive;
        c4.senderAvatar = R.mipmap.d + "";
        c4.filePath = R.mipmap.d + "";
        list.add(c4);

        StructChatInfo c5 = new StructChatInfo();
        c5.messageType = MyType.MessageType.image;
        c5.forwardMessageFrom = "ali";
        c5.sendType = MyType.SendType.send;
        c5.senderAvatar = R.mipmap.e + "";
        c5.filePath = R.mipmap.e + "";
        list.add(c5);

        StructChatInfo c6 = new StructChatInfo();
        c6.messageType = MyType.MessageType.image;
        c6.sendType = MyType.SendType.recvive;
        c6.senderAvatar = R.mipmap.f + "";
        c6.filePath = R.mipmap.f + "";
        list.add(c6);

        StructChatInfo c7 = new StructChatInfo();
        c7.messageType = MyType.MessageType.image;
        c7.sendType = MyType.SendType.recvive;
        c7.senderAvatar = R.mipmap.g + "";
        c7.filePath = R.mipmap.g + "";
        list.add(c7);

        StructChatInfo c8 = new StructChatInfo();
        c8.messageType = MyType.MessageType.image;
        c8.sendType = MyType.SendType.send;
        c8.filePath = R.mipmap.h + "";
        list.add(c8);


        for (int i = 0; i < 100; i++) {

            list.add(c8);
        }

        return list;
    }

}
