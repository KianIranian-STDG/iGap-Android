package com.iGap.activitys;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.fragments.FragmentNotification;
import com.iGap.fragments.FragmentShowImage;
import com.iGap.interface_package.OnChatDelete;
import com.iGap.interface_package.OnChatGetRoom;
import com.iGap.interface_package.OnUserContactEdit;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.StructSharedMedia;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmOfflineDelete;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.request.RequestChatDelete;
import com.iGap.request.RequestChatGetRoom;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ActivityContactsProfile extends ActivityEnhanced {
    private long userId = 0;
    private long roomId;
    private long phone = 912123456;
    private String displayName = "Alexander Smith";
    private String username = "Alexander Smith";
    private long lastSeen;
    private String initials;
    private String color;
    private String enterFrom;

    private boolean showNumber = true;

    private AppBarLayout appBarLayout;

    private TextView txtLastSeen, txtUserName, titleToolbar, titleLastSeen, txtBlockContact, txtClearChat, txtBack, txtPhoneNumber, txtNotifyAndSound, txtNickname;
    private ViewGroup vgPhoneNumber, vgSharedMedia, layoutNickname;
    private CircleImageView imgUser;
    private MaterialDesignTextView imgMenu;

    private FloatingActionButton fab;
    private PopupWindow popupWindow;

    private String avatarPath;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_profile);
        final Realm realm = Realm.getDefaultInstance();

        Bundle extras = getIntent().getExtras();
        userId = extras.getLong("peerId");
        roomId = extras.getLong("RoomId");
        enterFrom = extras.getString("enterFrom");

        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo("id", userId).findFirst();
        if (realmRegisteredInfo.getLastAvatar() != null) {
            avatarPath = realmRegisteredInfo.getLastAvatar().getFile().getLocalThumbnailPath();
            Log.i("XXX", "realmRegisteredInfo.getAvatar().getFile().getLocalThumbnailPath() : " + realmRegisteredInfo.getLastAvatar().getFile().getLocalThumbnailPath());
            Log.i("XXX", "realmRegisteredInfo.getAvatar().getFile().getLocalFilePath() : " + realmRegisteredInfo.getLastAvatar().getFile().getLocalFilePath());
        }

        RealmContacts realmUser = realm.where(RealmContacts.class).equalTo("id", userId).findFirst();

        if (realmUser != null) {
            phone = realmUser.getPhone();
            displayName = realmUser.getDisplay_name();
            username = realmUser.getUsername();
            lastSeen = realmUser.getLast_seen();
            color = realmUser.getColor();
            initials = realmUser.getInitials();
        }

        RealmContacts realmContacts = realm.where(RealmContacts.class).equalTo("phone", phone).findFirst();

        // agar ba click roye karbar dar safheye goruh vared in ghesmat shodim va karbar dar list contact haye ma vojud nadasht shomareye karbar namyesh dade nemishavad
        if (realmContacts == null && enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString())) {
            showNumber = false;
        }

        imgUser = (CircleImageView) findViewById(R.id.chi_img_circleImage);

        //Set ContactAvatar
        if (avatarPath != null) {
            File imgFile = new File(avatarPath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgUser.setImageBitmap(myBitmap);
            } else {
                imgUser.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgUser.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
            }
        } else {
            imgUser.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imgUser.getContext().getResources().getDimension(R.dimen.dp100), initials, color));
        }

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File file = new File(G.DIR_ALL_IMAGE_USER_CONTACT + "/" + username);
                if (!file.exists()) {
                    file.mkdirs();
                }
                Fragment fragment = FragmentShowImage.newInstance();
                Bundle bundle = new Bundle();
                bundle.putSerializable("listPic", setItem(file));
                bundle.putInt("SelectedImage", 0);
                fragment.setArguments(bundle);

                ActivityContactsProfile.this.getFragmentManager().beginTransaction().replace(R.id.chi_layoutParent, fragment).commit();

            }
        });

        txtBack = (TextView) findViewById(R.id.chi_txt_back);
        txtBack.setTypeface(G.fontawesome);
        RippleView rippleBack = (RippleView) findViewById(R.id.chi_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.chi_fab_setPic);
        fab.setOnClickListener(new View.OnClickListener() { //fab button
            @Override
            public void onClick(View view) {

                if (enterFrom.equals(ProtoGlobal.Room.Type.GROUP.toString())) {

                    final Realm realm = Realm.getDefaultInstance();
                    final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("chat_room.peer_id", userId).findFirst();

                    if (realmRoom != null) {
                        Intent intent = new Intent(G.context, ActivityChat.class);
                        intent.putExtra("RoomId", realmRoom.getId());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        G.context.startActivity(intent);
                        finish();

                    } else {
                        G.onChatGetRoom = new OnChatGetRoom() {
                            @Override
                            public void onChatGetRoom(final long roomId) {
                                G.currentActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Realm realm = Realm.getDefaultInstance();
                                        Intent intent = new Intent(G.context, ActivityChat.class);
                                        intent.putExtra("peerId", userId);
                                        intent.putExtra("RoomId", roomId);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        realm.close();
                                        G.context.startActivity(intent);
                                        finish();

                                    }
                                });
                            }
                        };

                        new RequestChatGetRoom().chatGetRoom(userId);
                    }
                    realm.close();

                } else {
                    finish();
                }


            }
        });

        txtNickname = (TextView) findViewById(R.id.chi_txt_nikName);//set nickname
        if (displayName != null && !displayName.equals("")) {
            txtNickname.setText(displayName);
        } else {
            txtNickname.setText("info not exist");
        }


        layoutNickname = (ViewGroup) findViewById(R.id.chi_layout_nickname);
        layoutNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(ActivityContactsProfile.this)
                        .title("Contact Name")
                        .positiveText("SAVE")
                        .alwaysCallInputCallback()
                        .widgetColor(getResources().getColor(R.color.toolbar_background))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                G.onUserContactEdit = new OnUserContactEdit() {
                                    @Override
                                    public void onContactEdit() {

                                    }
                                };

                                //change Nickname on realm
                                final RealmContacts realmUser = realm.where(RealmContacts.class).equalTo("id", userId).findFirst();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realmUser.setDisplay_name(txtNickname.toString());
                                    }
                                });
                            }
                        })
                        .negativeText("CANCEL")
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
                        .input("please Enter a NickName", txtNickname.getText().toString(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                View positive = dialog.getActionButton(DialogAction.POSITIVE);

                                if (!input.toString().equals(txtNickname.getText().toString())) {

                                    positive.setClickable(true);
                                    positive.setAlpha(1.0f);
                                } else {
                                    positive.setClickable(false);
                                    positive.setAlpha(0.5f);
                                }

                            }
                        }).show();

            }
        });
//        layoutNickname.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {


        txtLastSeen = (TextView) findViewById(R.id.chi_txt_lastSeen_title);
        txtLastSeen.setText("Last seen at " + lastSeen);

        txtUserName = (TextView) findViewById(R.id.chi_txt_userName);
        txtUserName.setText(username);

        txtPhoneNumber = (TextView) findViewById(R.id.chi_txt_phoneNumber);
        txtPhoneNumber.setText("" + phone);

        vgPhoneNumber = (ViewGroup) findViewById(R.id.chi_layout_phoneNumber);
        if (!showNumber) {
            vgPhoneNumber.setVisibility(View.GONE);
        }

        vgSharedMedia = (ViewGroup) findViewById(R.id.chi_layout_SharedMedia);

        titleToolbar = (TextView) findViewById(R.id.chi_txt_titleToolbar_DisplayName);
        titleToolbar.setText("nickname");

        titleLastSeen = (TextView) findViewById(R.id.chi_txt_titleToolbar_LastSeen);
        titleLastSeen.setText("Last Seen at " + lastSeen);

        appBarLayout = (AppBarLayout) findViewById(R.id.chi_appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (verticalOffset < -17) {

                    titleToolbar.setVisibility(View.VISIBLE);
                    titleToolbar.animate().alpha(1).setDuration(300);
                    titleLastSeen.setVisibility(View.VISIBLE);
                    titleLastSeen.animate().alpha(1).setDuration(300);

                } else {
                    titleToolbar.setVisibility(View.GONE);
                    titleToolbar.animate().alpha(0).setDuration(500);
                    titleLastSeen.setVisibility(View.GONE);
                    titleLastSeen.animate().alpha(0).setDuration(500);
                }
            }
        });

        final int screenWidth = (int) (getResources().getDisplayMetrics().widthPixels / 1.7);
        imgMenu = (MaterialDesignTextView) findViewById(R.id.chi_img_menuPopup);
        RippleView rippleMenu = (RippleView) findViewById(R.id.chi_ripple_menuPopup);


        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
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
                popupWindow.showAsDropDown(rippleView);


                TextView txtSearch = (TextView) popupView.findViewById(R.id.popup_txtItem1);
                txtSearch.setTypeface(G.arial);
                txtSearch.setText("Share");
                txtSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                TextView txtDelete = (TextView) popupView.findViewById(R.id.popup_txtItem2);
                txtDelete.setTypeface(G.arial);
                txtDelete.setText("Delete");
                txtDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        deleteChat();
                    }
                });

                TextView txtDeleteChat = (TextView) popupView.findViewById(R.id.popup_txtItem3);
                txtDeleteChat.setTypeface(G.arial);
                txtDeleteChat.setText("Add Shortcut");
                txtDeleteChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                TextView txtMutNotification = (TextView) popupView.findViewById(R.id.popup_txtItem4);
                txtMutNotification.setVisibility(View.GONE);
            }
        });
        vgPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUpMenu(R.menu.chi_popup_phone_number, v);
            }
        });

        vgSharedMedia = (ViewGroup) findViewById(R.id.chi_layout_SharedMedia);
        vgSharedMedia.setOnClickListener(new View.OnClickListener() {// go to the ActivityMediaChanel
            @Override
            public void onClick(View view) {

//                startActivity(new Intent(ActivityContactsProfile.this , ActivitySharedMedia.class));
//                finish();

                // TODO: 9/3/2016 (molareza) go to MediaShared page

            }
        });

        txtBlockContact = (TextView) findViewById(R.id.chi_txt_blockContact);
        txtBlockContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog("Block This Contact?", "BLOCK", "CANCEL");

            }
        });

        txtClearChat = (TextView) findViewById(R.id.chi_txt_clearChat);
        txtClearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog("clear This chat?", "ClEAR", "CANCEL");
            }
        });

        txtNotifyAndSound = (TextView) findViewById(R.id.chi_txtNotifyAndSound);
        txtNotifyAndSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: 9/3/2016 (molareza) go to NotifyAndSound page

                FragmentNotification fragmentNotification = new FragmentNotification();
                Bundle bundle = new Bundle();
                bundle.putString("PAGE", "Contact");
                fragmentNotification.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.chi_layoutParent, fragmentNotification).commit();


            }
        });

        realm.close();

    }

    private void popUpMenu(final int layout, View v) {

        PopupMenu popupMenu = new PopupMenu(ActivityContactsProfile.this, v, Gravity.BOTTOM);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (layout == R.menu.chi_popup_menu) {

                    switch (item.getItemId()) {
                        case R.id.chi_popUpMenu0:

                            // TODO: 9/3/2016 (molareza) popupMenu share
                            Toast.makeText(ActivityContactsProfile.this, "1", Toast.LENGTH_SHORT).show();
                            return true;

                        case R.id.chi_popUpMenu1:
                            // TODO: 9/3/2016 (molareza) popupMenu delete
                            Toast.makeText(ActivityContactsProfile.this, "2", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.chi_popUpMenu2:
                            // TODO: 9/3/2016 (molareza) popupMenu add shortcut
                            Toast.makeText(ActivityContactsProfile.this, "3", Toast.LENGTH_SHORT).show();
                            return true;
                    }

                } else if (layout == R.menu.chi_popup_phone_number) {

                    switch (item.getItemId()) { // insert new user to = contact mobile
                        case R.id.chi_popUpAddContact:
                            ArrayList<ContentValues> data = new ArrayList<ContentValues>();

//                            ContentValues row1 = new ContentValues();
//                            row1.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE);
//                            row1.put(ContactsContract.CommonDataKinds.Organization.COMPANY, "jhgujjj");
//                            data.add(row1);

//                            ContentValues row2 = new ContentValues();
//                            row2.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
//                            row2.put(ContactsContract.Contacts.DISPLAY_NAME, "am");
//                            data.add(row2);
//
//                            ContentValues row3 = new ContentValues();
//                            row3.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
//                            row3.put(ContactsContract.CommonDataKinds.Phone.NUMBER, Long.parseLong(txtPhoneNumber.getText().toString()));
//                            data.add(row3);
//
////                            ContentValues row4 = new ContentValues();
////                            row4.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
////                            row4.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM);
////                            row4.put(ContactsContract.CommonDataKinds.Email.LABEL, "Green Bot");
////                            row4.put(ContactsContract.CommonDataKinds.Email.ADDRESS, "android@android.com");
////                            data.add(row4);
//
//                            ContentValues row5 = new ContentValues();
//                            row5.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
//                            row5.put(ContactsContract.CommonDataKinds.Nickname.NAME, txtNickname.getText().toString());
//                            data.add(row5);
//
                            Bitmap bmImage = ((BitmapDrawable) imgUser.getDrawable()).getBitmap();
//                            Bitmap bmImage = BitmapFactory.decodeFile(G.imageFile.toString());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                            byte[] b = baos.toByteArray();
                            ContentValues row6 = new ContentValues();
                            row6.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
                            row6.put(ContactsContract.CommonDataKinds.Phone.DATA15, b);
                            data.add(row6);


                            Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                            intent.putExtra(ContactsContract.Intents.Insert.NAME, displayName);
                            intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
                            intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
                            startActivity(intent);


                            Toast.makeText(ActivityContactsProfile.this, "1", Toast.LENGTH_SHORT).show();
                            return true;

                        case R.id.chi_popUpCall: // call to user

                            long call = Long.parseLong(txtPhoneNumber.getText().toString());
                            try {
                                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                                phoneIntent.setData(Uri.parse("tel:" + call));
                                //startActivity(phoneIntent); //TODO [Saeed Mozaffari] [2016-09-07 11:31 AM] - phone intent permission

                            } catch (Exception ex) {

                                ex.getStackTrace();
                                Log.i("TAG", "onMenuItemClick: " + ex.getMessage());
                            }


                            Toast.makeText(ActivityContactsProfile.this, "2", Toast.LENGTH_SHORT).show();
                            return true;

                        case R.id.chi_popUpCopy: // copy phone number

                            String copy;
                            copy = txtPhoneNumber.getText().toString();

                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("PHONE_NUMBER", copy);
                            clipboard.setPrimaryClip(clip);

                            Toast.makeText(ActivityContactsProfile.this, "3", Toast.LENGTH_SHORT).show();
                            return true;
                    }
                }

                return true;
            }
        });

        popupMenu.inflate(layout);
        popupMenu.show();
    }

    private void showAlertDialog(String message, String positive, String negitive) { // alert dialog for block or clear user

        final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityContactsProfile.this);

        builder.setMessage(message);
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clearHistory();
                dialogInterface.dismiss();
            }
        });

        builder.setMessage(message);
        builder.setNegativeButton(negitive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button nButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nButton.setTextColor(getResources().getColor(R.color.toolbar_background));
        nButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        Button pButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pButton.setTextColor(getResources().getColor(R.color.toolbar_background));
        pButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

    }

    public ArrayList<StructSharedMedia> setItem(File f) {
        ArrayList<StructSharedMedia> items = new ArrayList<>();
//        File addFile = new File(G.DIR_ALL_IMAGE_USER_CONTACT);
        File file[] = f.listFiles();
        for (int i = 0; i < file.length; i++) {
            if (!file[i].getPath().equals(G.chatBackground.toString())) {
                StructSharedMedia item = new StructSharedMedia();
                item.filePath = file[i].getPath();
                items.add(item);
            }
        }
        return items;
    }

    //TODO [Saeed Mozaffari] [2016-10-15 3:31 PM] - clearHistory , DeleteChat , use in ActivityMain , ActivityChat , ActivityContactsProfile . mitunim method ha ro tekrar nakonim va ye ja bashe va az chand ja farakhani konim
    private void clearHistory() {

        // make request for clearing messages
        final Realm realm = Realm.getDefaultInstance();

        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo("roomId", roomId).findFirstAsync();
        realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
            @Override
            public void onChange(final RealmClientCondition element) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();

                        if (realmRoom.getLastMessageId() != -1) {
                            element.setClearId(realmRoom.getLastMessageId());
                            G.clearMessagesUtil.clearMessages(roomId, realmRoom.getLastMessageId());
                        }

                        RealmResults<RealmChatHistory> realmChatHistories = realm.where(RealmChatHistory.class).equalTo("roomId", roomId).findAll();
                        for (RealmChatHistory chatHistory : realmChatHistories) {
                            RealmRoomMessage roomMessage = chatHistory.getRoomMessage();
                            if (roomMessage != null) {
                                // delete chat history message
                                chatHistory.getRoomMessage().deleteFromRealm();
                            }
                        }

                        RealmRoom room = realm.where(RealmRoom.class).equalTo("id", roomId).findFirst();
                        if (room != null) {
                            room.setUnreadCount(0);
                            room.setLastMessageId(0);
                            room.setLastMessageTime(0);
                            room.setLastMessage("");

                            realm.copyToRealmOrUpdate(room);
                        }
                        // finally delete whole chat history
                        realmChatHistories.deleteAllFromRealm();
                    }
                });

                element.removeChangeListeners();
                realm.close();
            }
        });

        if (G.onClearChatHistory != null) {
            G.onClearChatHistory.onClearChatHistory();
        }
    }

    private void deleteChat() {
        G.onChatDelete = new OnChatDelete() {
            @Override
            public void onChatDelete(long roomId) {
            }

            @Override
            public void onChatDeleteError(int majorCode, int minorCode) {

                if (majorCode == 218 && minorCode == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016 Error 218 - CHAT_DELETE_BAD_PAYLOAD
                            //Invalid roomId

                        }
                    });
                } else if (majorCode == 219) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016 Error 219 - CHAT_DELETE_INTERNAL_SERVER_ERROR
                            //Invalid roomId

                        }
                    });
                } else if (majorCode == 220) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: 9/25/2016 Error 220 - CHAT_DELETE_FORBIDDEN
                            //Invalid roomId

                        }
                    });
                }
            }
        };
        final Realm realm = Realm.getDefaultInstance();
        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo("roomId", roomId).findFirstAsync();
        realmClientCondition.addChangeListener(new RealmChangeListener<RealmClientCondition>() {
            @Override
            public void onChange(final RealmClientCondition element) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(final Realm realm) {
                        if (realm.where(RealmOfflineDelete.class).equalTo("offlineDelete", roomId).findFirst() == null) {
                            RealmOfflineDelete realmOfflineDelete = realm.createObject(RealmOfflineDelete.class);
                            realmOfflineDelete.setId(System.nanoTime());
                            realmOfflineDelete.setOfflineDelete(userId);

                            element.getOfflineDeleted().add(realmOfflineDelete);


                            realm.where(RealmRoom.class).equalTo("id", roomId).findFirst().deleteFromRealm();
                            realm.where(RealmChatHistory.class).equalTo("roomId", roomId).findAll().deleteAllFromRealm();

                            new RequestChatDelete().chatDelete(roomId);
                        }
                    }
                });


                element.removeChangeListeners();
                realm.close();
                finish();
                // call this for finish activity chat when delete chat
                if (G.onDeleteChatFinishActivity != null) {
                    G.onDeleteChatFinishActivity.onFinish();
                }
            }
        });
    }


}


