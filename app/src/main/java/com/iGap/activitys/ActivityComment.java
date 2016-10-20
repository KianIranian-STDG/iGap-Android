package com.iGap.activitys;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AdapterComment;
import com.iGap.helper.Emojione;
import com.iGap.interface_package.IEmojiBackspaceClick;
import com.iGap.interface_package.IEmojiClickListener;
import com.iGap.interface_package.IEmojiLongClickListener;
import com.iGap.interface_package.IEmojiStickerClick;
import com.iGap.interface_package.IEmojiViewCreate;
import com.iGap.interface_package.IRecentsLongClick;
import com.iGap.interface_package.ISoftKeyboardOpenClose;
import com.iGap.module.CircleImageView;
import com.iGap.module.EmojiEditText;
import com.iGap.module.EmojiPopup;
import com.iGap.module.EmojiRecentsManager;
import com.iGap.module.StructCommentInfo;

import java.util.ArrayList;

/**
 * Created by android3 on 8/31/2016.
 */
public class ActivityComment extends ActivityEnhanced implements IEmojiViewCreate, IRecentsLongClick {


    private int numberOfComment = 0;
    private ArrayList<StructCommentInfo> list;
    private AdapterComment mAdapter;
    private FragmentSubLayoutReplay layoutReplay;

    private Button btnSend;
    private EmojiEditText edtChat;
    private ImageButton btnSmile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_show);

        String messageID = null;
        Bundle bundle = getIntent().getExtras();

//        if (bundle != null) {
//            messageID = bundle.getString("MessageID");
//            if (messageID == null)
//                finish();
//        }

        getCommentList(messageID);

        initComponent();

        initRecycleView();


    }


    private void getCommentList(String messageID) {


        list = new ArrayList<>();

        StructCommentInfo info = new StructCommentInfo();
        info.date = "agust 24";
        info.message = "this is a sample comment andu i ma goin gto the steori an dwer at egoid  he steori an dwer at egoid goin gto the ster wh goin o the steori an dwer at egoid goin gto ";
        info.senderName = "ali";
        info.senderID = " ali@kjfkd.com";
        info.time = "10:25";
        info.senderPicturePath = R.mipmap.a + "";

        info.replayMessageList = new ArrayList<>();
        info.replayMessageList.add(info);
        info.replayMessageList.add(info);
        info.replayMessageList.add(info);
        info.replayMessageList.add(info);
        info.replayMessageList.add(info);
        info.replayMessageList.add(info);
        info.replayMessageList.add(info);

        StructCommentInfo info2 = new StructCommentInfo();
        info2.date = "agust 24";
        info2.message = "this is a sample comment and hwo aare you i ma goin gto the steori an dwer at egoid goin gto the ster what is uout yout your name  ";
        info2.senderName = "hasan";
        info2.senderID = " hasan@kjfkd.com";
        info2.time = "10:25";
        info2.senderPicturePath = R.mipmap.b + "";


        list.add(info2);
        list.add(info2);

        list.add(info);

        list.add(info2);
        list.add(info2);
        list.add(info2);
        list.add(info2);

        numberOfComment = list.size();

    }

    private void initComponent() {

        initLayoutAttachText();

        layoutReplay = new FragmentSubLayoutReplay(findViewById(R.id.acs_ll_replay));

        Button btnBack = (Button) findViewById(R.id.acs_btn_back);
        btnBack.setTypeface(G.fontawesome);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


        Button btnMenu = (Button) findViewById(R.id.acs_btn_menu);
        btnMenu.setTypeface(G.fontawesome);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnMenu  ");
            }
        });


        TextView txtNumberOfComment = (TextView) findViewById(R.id.acs_txt_number_of_comment);
        if (numberOfComment > 0)
            txtNumberOfComment.setText(getString(R.string.comment) + " (" + numberOfComment + ")");
        else
            txtNumberOfComment.setText(R.string.no_comment);

    }

    private void initRecycleView() {

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.acs_recycler_view_comment);
        mAdapter = new AdapterComment(ActivityComment.this, list, layoutReplay);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ActivityComment.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }


    private void initLayoutAttachText() {


        btnSend = (Button) findViewById(R.id.acs_btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ddd", "btnSend  ");
            }
        });

        btnSmile = (ImageButton) findViewById(R.id.acs_btn_smile);
        edtChat = (EmojiEditText) findViewById(R.id.acs_edt_chat);


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
                changeEmojiButtonImageResource(R.drawable.emoticon_with_happy_face);
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

                if (edtChat.getText().length() > 0 && mAdapter.replayCommentNumber >= 0) {
                    btnSend.setBackgroundDrawable(getResources().getDrawable(R.drawable.send_button_blue));
                } else {
                    btnSend.setBackgroundDrawable(getResources().getDrawable(R.drawable.send_button_gray));

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


    public class FragmentSubLayoutReplay {

        View subLayoutReplay;


        private CircleImageView imvReplayPicture;
        private TextView txtReplayFrom;
        private TextView txtReplayMessage;
        private Button btnCloseLayout;

        FragmentSubLayoutReplay(View subLayoutReplay) {
            this.subLayoutReplay = subLayoutReplay;
            initView();
        }

        private void initView() {

            imvReplayPicture = (CircleImageView) subLayoutReplay.findViewById(R.id.acs_imv_replay_pic);
            txtReplayFrom = (TextView) subLayoutReplay.findViewById(R.id.acs_txt_replay_from);
            txtReplayMessage = (TextView) subLayoutReplay.findViewById(R.id.acs_txt_replay_message);

            btnCloseLayout = (Button) subLayoutReplay.findViewById(R.id.acs_btn_close);
            btnCloseLayout.setTypeface(G.fontawesome);
            btnCloseLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setLayoutVisible(false);
                    mAdapter.closeLayoutReplay();
                }
            });

        }


        public void setLayoutVisible(boolean visible) {

            if (visible) {
                subLayoutReplay.setVisibility(View.VISIBLE);
                if (edtChat.getText().length() > 0) {
                    btnSend.setBackgroundDrawable(getResources().getDrawable(R.drawable.send_button_blue));
                }
            } else {
                subLayoutReplay.setVisibility(View.GONE);
                btnSend.setBackgroundDrawable(getResources().getDrawable(R.drawable.send_button_gray));
            }


        }

        public void setLayoutParameter(String imagePath, String replayFrom, String replayMessage) {

            if (imagePath != null) {
                imvReplayPicture.setVisibility(View.VISIBLE);
                imvReplayPicture.setImageResource(Integer.parseInt(imagePath));

            } else {
                imvReplayPicture.setVisibility(View.GONE);
            }

            if (replayFrom != null) {
                txtReplayFrom.setText(replayFrom);
            } else {
                txtReplayFrom.setText("");
            }

            if (replayMessage != null) {
                txtReplayMessage.setText(replayMessage);
            } else {
                txtReplayMessage.setText("");
            }

        }

    }


}
