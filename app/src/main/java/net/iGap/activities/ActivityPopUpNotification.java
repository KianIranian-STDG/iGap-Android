/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperNotification;
import net.iGap.helper.HelperRealm;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AppUtils;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;
import net.iGap.module.UploadService;
import net.iGap.module.VoiceRecord;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.IPopUpListener;
import net.iGap.observers.interfaces.OnVoiceRecord;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoomMessage;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;

import static net.iGap.G.updateResources;

public class ActivityPopUpNotification extends AppCompatActivity {

    public static boolean isPopUpVisible = false;
    public static IPopUpListener popUpListener;


    //////////////////////////////////////////   appbar component
    ViewPager viewPager;
    ArrayList<HelperNotification.StructNotification> mList;
    private TextView txtName;
    private TextView txtLastSeen;

    //////////////////////////////////////////
    private ImageView imvUserPicture;
    private Button btnMessageCounter;
    private View viewAttachFile;
    private View viewMicRecorder;

    //////////////////////////////////////////    attach layout
    private MaterialDesignTextView btnSmileButton;
    private EditText edtChat;
    private MaterialDesignTextView btnMic;

    //////////////////////////////////////////
    private MaterialDesignTextView btnSend;
    private VoiceRecord voiceRecord;
    private boolean sendByEnter = false;
    private AdapterViewPagerClass mAdapter;
    private int listSize = 0;

    private String initialize;
    private String color;
    private long userId;

    /////////////////////////////////////////////////////////////////////////////////////////
//    private EmojiPopup emojiPopup;

    @Override
    protected void onResume() {
        super.onResume();
        isPopUpVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPopUpVisible = false;
    }

    @Override
    public void onBackPressed() {
//        if (emojiPopup != null && emojiPopup.isShowing()) {
//            emojiPopup.dismiss();
//        } else {
        super.onBackPressed();
//        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(updateResources(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        super.onCreate(savedInstanceState);
        setThemeSetting();
        setContentView(R.layout.activity_popup_notification);

        mList = HelperNotification.getInstance().getMessageList();
        if (getIntent().getExtras() != null)
            userId = getIntent().getExtras().getLong(ActivityMain.userId);
        new InitComponent();
    }

    private void setThemeSetting() {
        this.setTheme(new Theme().getTheme(this));
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        btnSmileButton.setText(drawableResourceId);
    }

//    private void setUpEmojiPopup() {
//        setEmojiColor(new Theme().getRootColor(this), new Theme().getTitleTextColor(this), new Theme().getTitleTextColor(this));
//    }
//
//    private void setEmojiColor(int BackgroundColor, int iconColor, int dividerColor) {
//        emojiPopup = EmojiPopup.Builder.fromRootView(findViewById(R.id.ac_ll_parent_notification)).setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
//            @Override
//            public void onEmojiBackspaceClick(View v) {
//
//            }
//        }).setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
//            @Override
//            public void onEmojiPopupShown() {
//                changeEmojiButtonImageResource(R.string.md_black_keyboard_with_white_keys);
//            }
//        }).setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
//            @Override
//            public void onKeyboardOpen(final int keyBoardHeight) {
//
//            }
//        }).setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
//            @Override
//            public void onEmojiPopupDismiss() {
//                changeEmojiButtonImageResource(R.string.md_emoticon_with_happy_face);
//            }
//        }).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
//            @Override
//            public void onKeyboardClose() {
//                emojiPopup.dismiss();
//            }
//        })
//                .setBackgroundColor(BackgroundColor)
//                .setIconColor(iconColor)
//                .setDividerColor(dividerColor)
//                .build(edtChat);
//    }

    private void setImageAndTextAppBar(int position) {

        if (mList.size() == 0)
            return;

        initialize = mList.get(position).initialize;
        color = mList.get(position).color;
        txtName.setText(mList.get(position).name);

        DbManager.getInstance().doRealmTask(realm -> {
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, mList.get(position).senderId);
            if (realmRegisteredInfo != null) {
                if (realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                    txtLastSeen.setText(LastSeenTimeUtil.computeTime(txtLastSeen.getContext(), realmRegisteredInfo.getId(), realmRegisteredInfo.getLastSeen(), false));
                } else {
                    txtLastSeen.setText(realmRegisteredInfo.getStatus());
                }
            } else {
                txtLastSeen.setText("");
            }

            setAvatar(realmRegisteredInfo, realm);
        });
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    private void setAvatar(RealmRegisteredInfo realmRegisteredInfo, Realm realm) {

        String avatarPath = null;
        if (realmRegisteredInfo != null && realmRegisteredInfo.getAvatars(realm) != null && realmRegisteredInfo.getLastAvatar(realm) != null) {
            String mainFilePath = realmRegisteredInfo.getLastAvatar(realm).getFile().getLocalFilePath();
            if (mainFilePath != null && new File(mainFilePath).exists()) { // if main image is exist showing that
                avatarPath = mainFilePath;
            } else {
                avatarPath = realmRegisteredInfo.getLastAvatar(realm).getFile().getLocalThumbnailPath();
            }
        }

        //Set Avatar For Chat,Group,Channel
        if (avatarPath != null) {
            File imgFile = new File(avatarPath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imvUserPicture.setImageBitmap(myBitmap);
            } else {
//                if (realmRegisteredInfo != null && realmRegisteredInfo.getLastAvatar() != null && realmRegisteredInfo.getLastAvatar().getFile() != null) {
//                     onRequestDownloadAvatar(realmRegisteredInfo.getLastAvatar().getFile());
//                }
                imvUserPicture.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvUserPicture.getContext().getResources().getDimension(R.dimen.dp60), initialize, color));
            }
        } else {
//            if (realmRegisteredInfo != null && realmRegisteredInfo.getLastAvatar() != null && realmRegisteredInfo.getLastAvatar().getFile() != null) {
//                  onRequestDownloadAvatar(realmRegisteredInfo.getLastAvatar().getFile());
//            }
            imvUserPicture.setImageBitmap(net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvUserPicture.getContext().getResources().getDimension(R.dimen.dp60), initialize, color));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (voiceRecord != null) {
            voiceRecord.dispatchTouchEvent(event);
        }

        return super.dispatchTouchEvent(event);
    }

    public static void sendMessage(final String message, final long mRoomId, ProtoGlobal.Room.Type chatType) {
        RealmRoomMessage roomMessage = RealmRoomMessage.makeTextMessage(mRoomId, message);
        HelperRealm.copyOrUpdateToRealm(roomMessage);
        ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).newBuilder(chatType, ProtoGlobal.RoomMessageType.TEXT, mRoomId).message(message).sendMessage(roomMessage.getMessageId() + "");
    }

    private void goToChatActivity() {
        Intent intent = new Intent(ActivityPopUpNotification.this, ActivityMain.class);
        if (mList.size() != 0)
            intent.putExtra(ActivityMain.openChat, mList.get(viewPager.getCurrentItem()).roomId);
        intent.putExtra(ActivityMain.userId, userId);
        startActivity(intent);
        finish();
    }

    private class InitComponent {

        InitComponent() {
            initMethod();
            initAppbar();
            initViewPager();
            initLayoutAttach();
//            setUpEmojiPopup();
        }

        private void initMethod() {

            popUpListener = new IPopUpListener() {
                @Override
                public void onMessageReceive() {

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mList = HelperNotification.getInstance().getMessageList();
                            btnMessageCounter.setText(1 + "/" + mList.size());
                            listSize = mList.size();
                            mAdapter.notifyDataSetChanged();
                            setImageAndTextAppBar(viewPager.getCurrentItem());
                        }
                    });
                }
            };

            viewAttachFile = findViewById(R.id.apn_layout_attach_file);

            viewMicRecorder = findViewById(R.id.apn_layout_mic_recorde);
            findViewById(R.id.lmr_layout_bottom).setBackground(new Theme().tintDrawable(getResources().getDrawable(R.drawable.backround_chatroom_root_dark), ActivityPopUpNotification.this, R.attr.rootBackgroundColor));

            voiceRecord = new VoiceRecord(ActivityPopUpNotification.this, viewMicRecorder, viewAttachFile, new OnVoiceRecord() {
                @Override
                public void onVoiceRecordDone(String savedPath) {
                    Intent uploadService = new Intent(ActivityPopUpNotification.this, UploadService.class);
                    uploadService.putExtra("Path", savedPath);
                    uploadService.putExtra("Roomid", mList.get(viewPager.getCurrentItem()).roomId);
                    startService(uploadService);

                    // sendVoice(savedPath, unreadList.get(viewPager.getCurrentItem()).getRoomId());

                    finish();
                }

                @Override
                public void onVoiceRecordCancel() {

                }
            });

            // get sendByEnter action from setting value
            SharedPreferences sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            int checkedSendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
            sendByEnter = checkedSendByEnter == 1;
        }

        private void initAppbar() {

            RippleView rippleBackButton = findViewById(R.id.apn_ripple_back_Button);

            rippleBackButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    finish();
                }
            });


            txtName = findViewById(R.id.apn_txt_name);
            txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToChatActivity();
                }
            });

            txtLastSeen = findViewById(R.id.apn_txt_last_seen);

            imvUserPicture = findViewById(R.id.apn_imv_user_picture);
            imvUserPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToChatActivity();
                }
            });

            btnMessageCounter = findViewById(R.id.apn_btn_message_counter);
            btnMessageCounter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        private void initViewPager() {

            viewPager = findViewById(R.id.apn_view_pager);
            /** Hint : always read count of view pager with "listSize", for avoid from view pager get count error */
            listSize = mList.size();
            if (viewPager != null && mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            } else {
                mAdapter = new AdapterViewPagerClass();
                viewPager.setAdapter(mAdapter);
            }

            setImageAndTextAppBar(viewPager.getCurrentItem());

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    btnMessageCounter.setText(position + 1 + "/" + listSize);

                    setImageAndTextAppBar(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        private void initLayoutAttach() {

            btnSmileButton = findViewById(R.id.apn_btn_smile_button);
            btnSmileButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                    emojiPopup.toggle();
                }
            });

            edtChat = findViewById(R.id.apn_edt_chat);

            edtChat.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence text, int i, int i1, int i2) {

                    // if in the seeting page send by enter is on message send by enter key
                    if (text.toString().endsWith(System.getProperty("line.separator"))) {
                        if (sendByEnter) btnSend.performClick();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if (edtChat.getText().length() > 0) {
                        btnMic.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnMic.setVisibility(View.GONE);
                            }
                        }).start();
                        btnSend.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnSend.setVisibility(View.VISIBLE);
                            }
                        }).start();
                    } else {
                        btnMic.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnMic.setVisibility(View.VISIBLE);
                            }
                        }).start();
                        btnSend.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnSend.setVisibility(View.GONE);
                            }
                        }).start();
                    }

                    // android emoji one doesn't support common space unicode
                    // to support space character, a new unicode will be replaced.
                    if (editable.toString().contains("\u0020")) {
                        Editable ab = new SpannableStringBuilder(editable.toString().replace("\u0020", "\u2000"));
                        editable.replace(0, editable.length(), ab);
                    }
                }
            });

            btnMic = findViewById(R.id.apn_btn_mic);
            btnMic.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    voiceRecord.setItemTag("ivVoice");
                    viewAttachFile.setVisibility(View.INVISIBLE);
                    viewMicRecorder.setVisibility(View.VISIBLE);


                    AppUtils.setVibrator(50);
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            voiceRecord.startVoiceRecord();
                        }
                    }, 60);
                    return true;
                }
            });

            btnSend = findViewById(R.id.apn_btn_send);
            //  btnSend.setTextColor(Color.parseColor(G.attachmentColor));

            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = viewPager.getCurrentItem();

                    sendMessage(edtChat.getText().toString(), mList.get(position).roomId, ProtoGlobal.Room.Type.valueOf(mList.get(position).roomType.toString()));

                    edtChat.setText("");

                    finish();
                }
            });
        }
    }

    private class AdapterViewPagerClass extends PagerAdapter {

        @Override
        public int getCount() {
            /**
             * Hint : always read count of view pager with "listSize", for avoid from view pager get count error
             */
            return listSize;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LayoutInflater inflater = LayoutInflater.from(ActivityPopUpNotification.this);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.sub_layout_activity_popup_notification, container, false);

            TextView txtMessage = layout.findViewById(R.id.slapn_txt_message);
            txtMessage.setText(mList.get(position).message);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToChatActivity();
                }
            });

            container.addView(layout);

            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }
}
