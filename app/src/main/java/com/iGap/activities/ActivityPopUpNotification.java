package com.iGap.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.Emojione;
import com.iGap.interfaces.IEmojiBackspaceClick;
import com.iGap.interfaces.IEmojiClickListener;
import com.iGap.interfaces.IEmojiLongClickListener;
import com.iGap.interfaces.IEmojiStickerClick;
import com.iGap.interfaces.IEmojiViewCreate;
import com.iGap.interfaces.IRecentsLongClick;
import com.iGap.interfaces.ISoftKeyboardOpenClose;
import com.iGap.interfaces.OnVoiceRecord;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.EmojiEditText;
import com.iGap.module.EmojiPopup;
import com.iGap.module.EmojiRecentsManager;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.VoiceRecord;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmChatHistoryFields;
import com.iGap.realm.RealmChatRoom;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmContactsFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmUserInfo;
import com.iGap.realm.enums.RoomType;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by android3 on 10/31/2016.
 */

public class ActivityPopUpNotification extends AppCompatActivity {

    // public static boolean isPopUpVisible=false;
    //public static OnComplete onComplete;

    //////////////////////////////////////////   appbar component

    private TextView txtName;
    private TextView txtLastSeen;
    private ImageView imvUserPicture;
    private Button btnMessageCounter;

    //////////////////////////////////////////

    ViewPager viewPager;
    private View viewAttachFile;
    private View viewMicRecorder;
    private EmojiPopup emojiPopup;

    //////////////////////////////////////////    attach layout

    private MaterialDesignTextView btnSmileButton;
    private EmojiEditText edtChat;
    private MaterialDesignTextView btnMic;
    private MaterialDesignTextView btnSend;

    //////////////////////////////////////////

    private VoiceRecord voiceRecord;
    private boolean sendByEnter = false;
    private AdapterViewPagerClass mAdapter;
    private int listSize = 0;
    ArrayList<RealmChatHistory> unreadList;
    private InitComponnet initComponnet;

    ////////////////////////////////////////////////////////////////////////////////////

    //@Override protected void onResume() {
    //    super.onResume();
    //    isPopUpVisible=true;
    //
    //}
    //
    //@Override protected void onPause() {
    //    super.onPause();
    //    isPopUpVisible=false;
    //
    //}

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_notification);

        //
        //onComplete=new OnComplete() {
        //    @Override public void complete(boolean result, String messageOne, String MessageTow) {
        //
        //        fillList();
        //
        //      //  mAdapter =new  AdapterViewPagerClass();
        //        viewPager.getAdapter().notifyDataSetChanged();
        //
        //    }
        //};

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                Log.e("ddd", "delay");

                fillList();

                initComponnet = new InitComponnet();
            }
        }, 300);
    }

    private void fillList() {

        unreadList = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();
        long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        RealmResults<RealmChatHistory> chatHistories = realm.where(RealmChatHistory.class)
            .findAllSorted(RealmChatHistoryFields.ID, Sort.DESCENDING);

        if (chatHistories != null) {
            for (RealmChatHistory realmChatHistory : chatHistories) {
                RealmRoomMessage roomMessage = realmChatHistory.getRoomMessage();
                if (roomMessage != null) {
                    if (roomMessage.getUserId() != userId) {
                        if (roomMessage.getStatus()
                            .equals(ProtoGlobal.RoomMessageStatus.SENT.toString())
                            || roomMessage.getStatus()
                            .equals(ProtoGlobal.RoomMessageStatus.DELIVERED.toString())) {

                            unreadList.add(realmChatHistory);
                        }
                    }
                }
            }
        }

        realm.close();

        Log.e("ddd", "size   " + unreadList.size());
    }

    private class InitComponnet {

        public InitComponnet() {

            initMethode();
            initAppbar();
            initViewPager();
            initImojiPopUp();
            initLayoutAttach();
        }

        private void initMethode() {

            viewAttachFile = findViewById(R.id.apn_layout_attach_file);

            viewMicRecorder = findViewById(R.id.apn_layout_mic_recorde);

            voiceRecord =
                new VoiceRecord(ActivityPopUpNotification.this, viewMicRecorder, viewAttachFile,
                    new OnVoiceRecord() {
                        @Override public void onVoiceRecordDone(String savedPath) {

                            sendVoice(savedPath);
                        }

                        @Override public void onVoiceRecordCancel() {

                        }
                    });

            // get sendByEnter action from setting value
            SharedPreferences sharedPreferences =
                getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            int checkedSendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
            sendByEnter = checkedSendByEnter == 1;
        }

        private void initAppbar() {

            RippleView rippleBackButton = (RippleView) findViewById(R.id.apn_ripple_back_Button);

            rippleBackButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override public void onComplete(RippleView rippleView) {
                    finish();
                }
            });

            txtName = (TextView) findViewById(R.id.apn_txt_name);
            txtName.setTypeface(G.arialBold);

            txtLastSeen = (TextView) findViewById(R.id.apn_txt_last_seen);

            imvUserPicture = (ImageView) findViewById(R.id.apn_imv_user_picture);

            btnMessageCounter = (Button) findViewById(R.id.apn_btn_message_counter);
            btnMessageCounter.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {

                }
            });
        }

        private void initViewPager() {

            viewPager = (ViewPager) findViewById(R.id.apn_view_pager);
            mAdapter = new AdapterViewPagerClass();
            viewPager.setAdapter(mAdapter);
            listSize = unreadList.size();

            btnMessageCounter.setText(1 + " " + getString(R.string.of) + " " + listSize);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {

                }

                @Override public void onPageSelected(int position) {
                    btnMessageCounter.setText(
                        position + 1 + " " + getString(R.string.of) + " " + listSize);
                }

                @Override public void onPageScrollStateChanged(int state) {

                }
            });
        }

        private void initImojiPopUp() {

            // init emoji popup
            // give the topmost view of your activity layout hierarchy. this will be used to measure soft keyboard height
            emojiPopup = new EmojiPopup(getWindow().findViewById(android.R.id.content),
                getApplicationContext(), new IEmojiViewCreate() {
                @Override public void onEmojiViewCreate(View view, EmojiPopup emojiPopup) {

                }
            });
            emojiPopup.setRecentsLongClick(new IRecentsLongClick() {
                @Override
                public boolean onRecentsLongClick(View view, EmojiRecentsManager recentsManager) {
                    return false;
                }
            });
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
                    edtChat.dispatchKeyEvent(
                        new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                }
            });
        }

        private void initLayoutAttach() {

            btnSmileButton = (MaterialDesignTextView) findViewById(R.id.apn_btn_smile_button);
            btnSmileButton.setOnClickListener(new View.OnClickListener() {

                @Override public void onClick(View v) {
                    toggleKeyboard();
                }
            });

            edtChat = (EmojiEditText) findViewById(R.id.apn_edt_chat);

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
                        if (sendByEnter) btnSend.performClick();
                    }
                }

                @Override public void afterTextChanged(Editable editable) {

                    if (edtChat.getText().length() > 0) {
                        btnMic.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                            @Override public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnMic.setVisibility(View.GONE);
                            }
                        }).start();
                        btnSend.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                            @Override public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnSend.setVisibility(View.VISIBLE);
                            }
                        }).start();
                    } else {
                        btnMic.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                            @Override public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnMic.setVisibility(View.VISIBLE);
                            }
                        }).start();
                        btnSend.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                            @Override public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnSend.setVisibility(View.GONE);
                            }
                        }).start();
                    }

                    // android emoji one doesn't support common space unicode
                    // to support space character, a new unicode will be replaced.
                    if (editable.toString().contains("\u0020")) {
                        Editable ab = new SpannableStringBuilder(
                            editable.toString().replace("\u0020", "\u2000"));
                        editable.replace(0, editable.length(), ab);
                    }
                }
            });

            btnMic = (MaterialDesignTextView) findViewById(R.id.apn_btn_mic);
            btnMic.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View view) {

                    voiceRecord.setItemTag("ivVoice");
                    viewAttachFile.setVisibility(View.GONE);
                    viewMicRecorder.setVisibility(View.VISIBLE);
                    voiceRecord.startVoiceRecord();

                    return true;
                }
            });

            btnSend = (MaterialDesignTextView) findViewById(R.id.apn_btn_send);

            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {

                    sendMessage();

                    edtChat.setText("");
                }
            });
        }

        // to toggle between keyboard and emoji popup
        private void toggleKeyboard() {

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

        private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
            btnSmileButton.setText(drawableResourceId);
        }
    }

    private class AdapterViewPagerClass extends PagerAdapter {

        private String initialize;
        private String color;
        private long chatPeerId;

        @Override public int getCount() {
            return unreadList.size();
        }

        @Override public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override public Object instantiateItem(View container, int position) {

            LayoutInflater inflater = LayoutInflater.from(ActivityPopUpNotification.this);
            ViewGroup layout =
                (ViewGroup) inflater.inflate(R.layout.sub_layout_activity_popup_notification,
                    (ViewGroup) container, false);

            TextView txtMessage = (TextView) layout.findViewById(R.id.slapn_txt_message);
            txtMessage.setText(unreadList.get(position).getRoomMessage().getMessage());

            Log.e("ddd", unreadList.get(position).getRoomMessage().getMessage() + "  " + position);

            Realm realm = Realm.getDefaultInstance();

            final RealmRoom realmRoom = realm.where(RealmRoom.class)
                .equalTo(RealmRoomFields.ID, unreadList.get(position).getRoomId())
                .findFirst();

            if (realmRoom != null) { // room exist
                initialize = realmRoom.getInitials();
                color = realmRoom.getColor();

                try {
                    txtName.setText(realmRoom.getTitle());
                    setLastSeen(realmRoom, realm);
                    setAvatar(realm);
                } catch (Exception e) {
                }
            }
            realm.close();

            ((ViewGroup) container).addView(layout);

            return layout;
        }

        @Override public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        private void setLastSeen(RealmRoom realmRoom, Realm realm) {

            String lastSeen = "";

            if (realmRoom.getType() == RoomType.CHAT) {

                RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                chatPeerId = realmChatRoom.getPeerId();

                RealmContacts realmContacts = realm.where(RealmContacts.class)
                    .equalTo(RealmContactsFields.ID, chatPeerId)
                    .findFirst();
                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
                    .equalTo(RealmRegisteredInfoFields.ID, chatPeerId)
                    .findFirst();
                if (realmRegisteredInfo != null) {
                    lastSeen = Long.toString(realmRegisteredInfo.getLastSeen());
                } else if (realmContacts != null) {
                    lastSeen = Long.toString(realmContacts.getLast_seen());
                } else {
                    lastSeen = "last seen";
                }
            }

            txtLastSeen.setText(lastSeen);
        }

        private void setAvatar(Realm realm) {

            String avatarPath = null;

            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
                .equalTo(RealmRegisteredInfoFields.ID, chatPeerId)
                .findFirst();
            if (realmRegisteredInfo != null
                && realmRegisteredInfo.getAvatar() != null
                && realmRegisteredInfo.getLastAvatar() != null) {

                String mainFilePath =
                    realmRegisteredInfo.getLastAvatar().getFile().getLocalFilePath();

                if (mainFilePath != null && new File(
                    mainFilePath).exists()) { // if main image is exist showing that
                    avatarPath = mainFilePath;
                } else {
                    avatarPath =
                        realmRegisteredInfo.getLastAvatar().getFile().getLocalThumbnailPath();
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
                        // onRequestDownloadAvatar(realmRegisteredInfo.getLastAvatar().getFile());
                    }
                    imvUserPicture.setImageBitmap(
                        com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture(
                            (int) imvUserPicture.getContext()
                                .getResources()
                                .getDimension(R.dimen.dp60), initialize, color));
                }
            } else {
                if (realmRegisteredInfo != null
                    && realmRegisteredInfo.getLastAvatar() != null
                    && realmRegisteredInfo.getLastAvatar().getFile() != null) {
                    //  onRequestDownloadAvatar(realmRegisteredInfo.getLastAvatar().getFile());
                }
                imvUserPicture.setImageBitmap(
                    com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture(
                        (int) imvUserPicture.getContext().getResources().getDimension(R.dimen.dp60),
                        initialize, color));
            }
        }
    }

    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        voiceRecord.dispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    private void sendMessage() {

    }

    private void sendVoice(String path) {

    }
}
