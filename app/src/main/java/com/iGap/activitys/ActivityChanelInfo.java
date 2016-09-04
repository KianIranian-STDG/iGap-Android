package com.iGap.activitys;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.iGap.realm.RealmContacts;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class ActivityChanelInfo extends ActivityEnhanced {
    private int userId = 0;
    private long phone = 912123456;
    private String displayName = "Alexander Smith";
    private String username = "Alexander Smith";
    private long lastSeen;

    private AppBarLayout appBarLayout;
    private EditText edtNikName;
    private TextView txtLastSeen, txtUserName, titleToolbar, titleLastSeen, txtBlockContact, txtClearChat, txtBack, txtPhoneNumber, txtNotifyAndSound;
    private ViewGroup vgPhoneNumber, vgSharedMedia;
    private CircleImageView imgUser;
    private ImageView imgMenu;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chanel_info);


        // TODO: 9/3/2016 (molareza) this class need user id

        try {
            if (getIntent().getExtras() != null) {
                userId = getIntent().getExtras().getInt("USER_ID");
            }
        } catch (NullPointerException e) {

            e.getStackTrace();

        }

        RealmContacts realmUser = G.realm.where(RealmContacts.class)
                .equalTo("id", userId)
                .findFirst();

        if (realmUser != null) {
            phone = realmUser.getPhone();
            displayName = realmUser.getDisplay_name();
            username = realmUser.getUsername();
            lastSeen = realmUser.getLast_seen();
        }

        imgUser = (CircleImageView) findViewById(R.id.chi_img_circleImage);
        txtBack = (TextView) findViewById(R.id.chi_txt_back);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// button back

                startActivity(new Intent(ActivityChanelInfo.this, ActivityMain.class));
                finish();

            }
        });

        txtBack.setTypeface(G.fontawesome);

        fab = (FloatingActionButton) findViewById(R.id.chi_fab_setPic);
        fab.setOnClickListener(new View.OnClickListener() { //fab button
            @Override
            public void onClick(View view) {

                // TODO: 9/3/2016 (molareza) action for fab button

            }
        });

        edtNikName = (EditText) findViewById(R.id.chi_edt_nikName);
        edtNikName.setText(displayName);
        final String nikName = edtNikName.getText().toString();
        edtNikName.addTextChangedListener(new TextWatcher() { // save change NicName on Realm
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {

                // TODO: 9/3/2016 (molareze)  Should be stored within server or not

                //change Nickname on realm
                final RealmContacts realmUser = G.realm.where(RealmContacts.class)
                        .equalTo("id", userId)
                        .findFirst();

                G.realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        realmUser.setDisplay_name(editable.toString());

                    }
                });

            }
        });

        txtLastSeen = (TextView) findViewById(R.id.chi_txt_lastSeen_title);
        txtLastSeen.setText("Last seen at " + lastSeen);

        txtUserName = (TextView) findViewById(R.id.chi_txt_userName);
        txtUserName.setText(username);

        txtPhoneNumber = (TextView) findViewById(R.id.chi_txt_phoneNumber);
        txtPhoneNumber.setText("" + phone);

        vgPhoneNumber = (ViewGroup) findViewById(R.id.chi_layout_phoneNumber);
        vgSharedMedia = (ViewGroup) findViewById(R.id.chi_layout_SharedMedia);

        titleToolbar = (TextView) findViewById(R.id.chi_txt_titleToolbar_DisplayName);
        titleToolbar.setText(nikName);

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

        imgMenu = (ImageView) findViewById(R.id.chi_img_menuPopup);
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUpMenu(R.menu.chi_popup_menu, v);
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

//                startActivity(new Intent(ActivityChanelInfo.this , ActivitySharedMedia.class));
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
                showAlertDialog("clear This chat?", "BLOCK", "CANCEL");
            }
        });

        txtNotifyAndSound = (TextView) findViewById(R.id.chi_txtNotifyAndSound);
        txtNotifyAndSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO: 9/3/2016 (molareza) go to NotifyAndSound page


            }
        });

    }

    private void popUpMenu(final int layout, View v) {

        PopupMenu popupMenu = new PopupMenu(ActivityChanelInfo.this, v, Gravity.BOTTOM);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (layout == R.menu.chi_popup_menu) {

                    switch (item.getItemId()) {
                        case R.id.chi_popUpMenu0:

                            // TODO: 9/3/2016 (molareza) popupMenu share
                            Toast.makeText(ActivityChanelInfo.this, "1", Toast.LENGTH_SHORT).show();
                            return true;

                        case R.id.chi_popUpMenu1:
                            // TODO: 9/3/2016 (molareza) popupMenu delete
                            Toast.makeText(ActivityChanelInfo.this, "2", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.chi_popUpMenu2:
                            // TODO: 9/3/2016 (molareza) popupMenu add shortcut
                            Toast.makeText(ActivityChanelInfo.this, "3", Toast.LENGTH_SHORT).show();
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
                            ContentValues row3 = new ContentValues();
                            row3.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                            row3.put(ContactsContract.CommonDataKinds.Phone.NUMBER, Long.parseLong(txtPhoneNumber.getText().toString()));
                            data.add(row3);
//
////                            ContentValues row4 = new ContentValues();
////                            row4.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
////                            row4.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM);
////                            row4.put(ContactsContract.CommonDataKinds.Email.LABEL, "Green Bot");
////                            row4.put(ContactsContract.CommonDataKinds.Email.ADDRESS, "android@android.com");
////                            data.add(row4);
//
                            ContentValues row5 = new ContentValues();
                            row5.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE);
                            row5.put(ContactsContract.CommonDataKinds.Nickname.NAME, edtNikName.getText().toString());
                            data.add(row5);
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


                            Toast.makeText(ActivityChanelInfo.this, "1", Toast.LENGTH_SHORT).show();
                            return true;

                        case R.id.chi_popUpCall: // call to user

                            long call = Long.parseLong(txtPhoneNumber.getText().toString());
                            try {
                                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                                phoneIntent.setData(Uri.parse("tel:" + call));
                                startActivity(phoneIntent);

                            } catch (Exception ex) {

                                ex.getStackTrace();
                                Log.i("TAG", "onMenuItemClick: " + ex.getMessage());
                            }


                            Toast.makeText(ActivityChanelInfo.this, "2", Toast.LENGTH_SHORT).show();
                            return true;

                        case R.id.chi_popUpCopy: // copy phone number

                            String copy;
                            copy = txtPhoneNumber.getText().toString();

                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("PHONE_NUMBER", copy);
                            clipboard.setPrimaryClip(clip);

                            Toast.makeText(ActivityChanelInfo.this, "3", Toast.LENGTH_SHORT).show();
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

        final AlertDialog.Builder builder = new AlertDialog.Builder(ActivityChanelInfo.this);

        builder.setMessage(message);
        builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
}
