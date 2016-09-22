package com.iGap.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.fragments.ContactGroupFragment;
import com.iGap.interface_package.OnClientGetRoomResponse;
import com.iGap.interface_package.OnGroupCreate;
import com.iGap.module.CircleImageView;
import com.iGap.module.HelperDecodeFile;
import com.iGap.module.LinedEditText;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoGlobal;
import com.iGap.request.RequestClientGetRoom;
import com.iGap.request.RequestGroupCreate;

import java.io.File;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityNewGroup extends ActivityEnhanced {

    private MaterialDesignTextView txtBack;
    private CircleImageView imgCircleImageView;
    private int myResultCodeCamera = 1;
    private int myResultCodeGallery = 0;
    private Uri uriIntent;
    public static Bitmap decodeBitmapProfile = null;
    private TextView txtNextStep, txtCancel, txtTitleToolbar;
    private String prefix = "NewGroup";
    private String path;

    private EditText edtGroupName;
    private LinedEditText edtDescription;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);


        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            prefix = bundle.getString("TYPE");
        }

        txtTitleToolbar = (TextView) findViewById(R.id.ng_txt_titleToolbar);
        txtTitleToolbar.setTypeface(G.arial);
        if (prefix.equals("NewChanel")) {
            txtTitleToolbar.setText("New Chanel");
        } else {
            txtTitleToolbar.setText("New Group");
        }

        //=======================back on toolbar


        txtBack = (MaterialDesignTextView) findViewById(R.id.ng_txt_back);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (G.IMAGE_NEW_GROUP.exists()) {
                    G.IMAGE_NEW_GROUP.delete();
                } else {
                    G.IMAGE_NEW_CHANEL.delete();
                }
                finish();
            }
        });
        //=======================set image for group
        imgCircleImageView = (CircleImageView) findViewById(R.id.ng_profile_circle_image);
        imgCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(ActivityNewGroup.this)
                        .title("Choose Picture")
                        .negativeText("CANCEL")
                        .items(R.array.profile)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                if (text.toString().equals("From Camera")) {

                                    if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        if (prefix.equals("NewChanel")) {
                                            uriIntent = Uri.fromFile(G.IMAGE_NEW_CHANEL);
                                        } else {
                                            uriIntent = Uri.fromFile(G.IMAGE_NEW_GROUP);
                                        }

                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                                        startActivityForResult(intent, myResultCodeCamera);
                                        dialog.dismiss();

                                    } else {
                                        Toast.makeText(ActivityNewGroup.this, "Please check your Camera", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(intent, myResultCodeGallery);
                                    dialog.dismiss();
                                }
                            }
                        }).show();
            }
        });

        if (prefix.equals("NewChanel")) {
            path = G.DIR_NEW_CHANEL;
        } else {
            path = G.DIR_NEW_GROUP;
        }

        if (G.IMAGE_NEW_GROUP.exists()) {
            decodeBitmapProfile = HelperDecodeFile.decodeFile(G.IMAGE_NEW_GROUP);
            imgCircleImageView.setImageBitmap(decodeBitmapProfile);
            imgCircleImageView.setPadding(0, 0, 0, 0);
            imgCircleImageView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        } else if (G.IMAGE_NEW_CHANEL.exists()) {
            decodeBitmapProfile = HelperDecodeFile.decodeFile(G.IMAGE_NEW_CHANEL);
            imgCircleImageView.setImageBitmap(decodeBitmapProfile);
            imgCircleImageView.setPadding(0, 0, 0, 0);
            imgCircleImageView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }

        //=======================name of group
        edtGroupName = (EditText) findViewById(R.id.ng_edt_newGroup);
        if (prefix.equals("NewChanel")) {
            edtGroupName.setHint("New Chanel");
        } else {
            edtGroupName.setHint("New Group");
        }

//        edtGroupName.setTypeface(G.arial);
        //=======================description group
        edtDescription = (LinedEditText) findViewById(R.id.ng_edt_description);

        edtDescription.setTypeface(G.arial);
        edtDescription.setSingleLine(false);
        edtDescription.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        edtDescription.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        edtDescription.setLines(4);
        edtDescription.setMaxLines(4);
        //=======================button next step

        txtNextStep = (TextView) findViewById(R.id.ng_txt_nextStep);
        txtNextStep.setTypeface(G.arial);
        txtNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imgCircleImageView != null) {
                    if (edtDescription.getText().toString().length() > 0) {
                        if (edtGroupName.getText().toString().length() > 0) {
                            boolean success;
                            String newName = edtGroupName.getText().toString().replace(" ", "_");
                            File file2 = new File(path, prefix + "_" + newName + Math.random() * 10000 + 1 + ".png");
                            if (prefix.equals("NewChanel")) {
                                success = G.IMAGE_NEW_CHANEL.renameTo(file2);
                            } else {
                                success = G.IMAGE_NEW_GROUP.renameTo(file2);
                            }

                            if (success) {

                                if (prefix.equals("NewChanel")) {
                                    startActivity(new Intent(ActivityNewGroup.this, ActivityNewChanelFinish.class));
                                    finish();
                                } else {
                                    createGroup();
//                                    Fragment fragment = ContactGroupFragment.newInstance();
//                                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.ng_fragmentContainer, fragment).commit();
//                                    ActivityMain.mLeftDrawerLayout.closeDrawer();
//                                    finish();
                                }
                            }
                        } else {
                            Toast.makeText(ActivityNewGroup.this, "Please Description tour Group", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ActivityNewGroup.this, "Please Enter Your Name Group", Toast.LENGTH_SHORT).show();
                    }
                }
//                }
            }
        });
        //=======================button cancel
        txtCancel = (TextView) findViewById(R.id.ng_txt_cancel);
        txtCancel.setTypeface(G.arial);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (G.IMAGE_NEW_GROUP.exists()) {
                    G.IMAGE_NEW_GROUP.delete();
                } else {
                    G.IMAGE_NEW_CHANEL.delete();
                }
                finish();
            }
        });
    }

    private void createGroup() {
        G.onGroupCreate = new OnGroupCreate() {
            @Override
            public void onGroupCreate(long roomId) {
                getRoom(roomId);
            }
        };

        new RequestGroupCreate().groupCreate(edtGroupName.getText().toString(), edtDescription.getText().toString());
    }

    private void getRoom(final long roomId) {

        G.onClientGetRoomResponse = new OnClientGetRoomResponse() {
            @Override
            public void onClientGetRoomResponse(ProtoGlobal.Room room, ProtoClientGetRoom.ClientGetRoomResponse.Builder builder) {
                getFragmentManager().popBackStack();
                Log.e("ddd", "ContactGroupFragment");
                Fragment fragment = ContactGroupFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putLong("RoomId", roomId);
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.ng_fragmentContainer, fragment).commit();
                ActivityMain.mLeftDrawerLayout.closeDrawer();
                finish();
            }
        };

        new RequestClientGetRoom().clientGetRoom(roomId);
    }


    //=======================result for picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == myResultCodeCamera && resultCode == RESULT_OK) {// result for camera

            Intent intent = new Intent(ActivityNewGroup.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
            intent.putExtra("TYPE", "camera");
            intent.putExtra("PAGE", prefix);
            startActivity(intent);
            finish();

        } else if (requestCode == myResultCodeGallery && resultCode == RESULT_OK) {// result for gallery
            Intent intent = new Intent(ActivityNewGroup.this, ActivityCrop.class);
            intent.putExtra("IMAGE_CAMERA", data.getData().toString());
            intent.putExtra("TYPE", "gallery");
            intent.putExtra("PAGE", prefix);
            startActivity(intent);
            finish();
        }
    }
}
