package com.iGap.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iGap.G;
import com.iGap.R;
import com.iGap.module.HelperDecodeFile;
import com.iGap.realm.RealmUserInfo;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class ActivitySetting extends ActivityEnhanced {

    private TextView txtBack, txtMenu;
    private ImageView imgMenu;

    private EditText edtNickName, edtUserName, edtPhoneNumber;

    private AppBarLayout appBarLayout;

    private int myResultCodeCamera = 1;
    private int myResultCodeGallery = 0;
    private Uri uriIntent;

    private FloatingActionButton fab;
    private CircleImageView circleImageView;
    public static Bitmap decodeBitmapProfile = null;

    private String nickName;
    private String userName;
    private String phoneName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        final Realm realm = Realm.getDefaultInstance();

        final TextView txtNickNameTitle = (TextView) findViewById(R.id.ac_txt_nickname_title);

        edtNickName = (EditText) findViewById(R.id.st_edt_nikName);
        edtUserName = (EditText) findViewById(R.id.st_edt_userName);
        edtPhoneNumber = (EditText) findViewById(R.id.st_edt_phoneNumber);

        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        nickName = realmUserInfo.getNickName();
        userName = realmUserInfo.getUserName();
        phoneName = realmUserInfo.getPhoneNumber();

        if (nickName != null) {
            edtNickName.setText(nickName);
            txtNickNameTitle.setText(nickName);
        }
        if (userName != null) edtUserName.setText(userName);
        if (phoneName != null) edtPhoneNumber.setText(phoneName);

        edtNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        if (!editable.toString().equals("")) {
                            RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
                            userInfo.setNickName(editable.toString());
                            txtNickNameTitle.setText(nickName);
                        }

                    }
                });


            }
        });

        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(final Editable editable) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                    }
                });


            }
        });

        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(final Editable editable) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                    }
                });

            }
        });

        appBarLayout = (AppBarLayout) findViewById(R.id.st_appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                TextView titleToolbar = (TextView) findViewById(R.id.st_txt_titleToolbar);
                if (verticalOffset < -1) {

                    titleToolbar.animate().alpha(1).setDuration(300);
                    titleToolbar.setVisibility(View.VISIBLE);

                } else {
                    titleToolbar.animate().alpha(0).setDuration(500);
                    titleToolbar.setVisibility(View.GONE);
                }
            }
        });

        // button back in toolbar
        txtBack = (TextView) findViewById(R.id.st_txt_back);
        txtBack.setTypeface(G.fontawesome);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ActivitySetting.this, ActivityMain.class);
                startActivity(intent);

            }
        });

        // button popupMenu in toolbar
        imgMenu = (ImageView) findViewById(R.id.st_img_menuPopup);
        assert txtMenu != null;
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(ActivitySetting.this, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.album_overflow_rename:
                                Toast.makeText(ActivitySetting.this, "1", Toast.LENGTH_SHORT).show();
                                return true;

                            case R.id.album_overflow_lock:
                                Toast.makeText(ActivitySetting.this, "2", Toast.LENGTH_SHORT).show();
                                return true;
                        }
                        return true;
                    }
                });

                popupMenu.inflate(R.menu.sc_popup_menu);
                popupMenu.show();
            }
        });

        //fab button for set pic
        fab = (FloatingActionButton) findViewById(R.id.st_fab_setPic);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog();
            }
        });

        circleImageView = (CircleImageView) findViewById(R.id.st_img_circleImage);
        if (G.imageFile.exists()) {
            decodeBitmapProfile = HelperDecodeFile.decodeFile(G.imageFile); //TODO [Saeed Mozaffari] [2016-08-30 9:43 AM] - (Saeed Mozaffari to Mr Mollareza) 1-user photo should decode for first time and reuse again and again , 2- read and write file from iGap directory
            circleImageView.setImageBitmap(decodeBitmapProfile);

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                    realmUserInfo.setAvatarPath(G.imageFile.toString());
                }
            });

        } else {
            circleImageView.setImageResource(R.mipmap.b);

        }

        realm.close();
    }

    //dialog for choose pic from gallery or camera
    private void startDialog() {
        final Dialog dialog = new Dialog(ActivitySetting.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_profile);
        TextView picCamera = (TextView) dialog.findViewById(R.id.pu_layout_dialog_picCamera);
        picCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    uriIntent = Uri.fromFile(G.imageFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                    startActivityForResult(intent, myResultCodeCamera);
                    dialog.dismiss();

                } else {
                    Toast.makeText(ActivitySetting.this, "Please check your Camera", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView picGallery = (TextView) dialog.findViewById(R.id.pu_layout_dialog_picGallery);
        picGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, myResultCodeGallery);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //=====================================================================================result from camera , gallery and crop
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == myResultCodeCamera && resultCode == RESULT_OK) {// result for camera

            Intent intent = new Intent(ActivitySetting.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
            intent.putExtra("TYPE", "camera");
            intent.putExtra("PAGE", "setting");
            startActivity(intent);
            finish();

        } else if (requestCode == myResultCodeGallery && resultCode == RESULT_OK) {// result for gallery
            Intent intent = new Intent(ActivitySetting.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", data.getData().toString());
            intent.putExtra("TYPE", "gallery");
            intent.putExtra("PAGE", "setting");
            startActivity(intent);
            finish();
        }
    }
}
