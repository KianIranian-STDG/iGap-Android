package com.iGap.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activitys.ActivityCrop;
import com.iGap.activitys.ActivityMain;
import com.iGap.activitys.ActivityNewChanelFinish;
import com.iGap.interface_package.OnClientGetRoomResponse;
import com.iGap.interface_package.OnGroupCreate;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.CircleImageView;
import com.iGap.module.HelperDecodeFile;
import com.iGap.module.LinedEditText;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoGlobal;
import com.iGap.request.RequestClientGetRoom;
import com.iGap.request.RequestGroupCreate;

import java.io.File;

/**
 * Created by android3 on 9/5/2016.
 */
public class FragmentNewGroup extends android.support.v4.app.Fragment {

    private MaterialDesignTextView txtBack;
    private CircleImageView imgCircleImageView;
    private int myResultCodeCamera = 1;
    private int myResultCodeGallery = 0;
    private int myResultFragment = 2;
    private Uri uriIntent;
    public static Bitmap decodeBitmapProfile = null;
    private TextView txtNextStep, txtCancel, txtTitleToolbar;
    private String prefix = "NewGroup";
    private String path;
    private RelativeLayout parent;

    private EditText edtGroupName;
    private LinedEditText edtDescription;

    public static FragmentNewGroup newInstance() {
        return new FragmentNewGroup();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_new_group, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getIntentData(this.getArguments());
        initComponent(view);
    }


    private void getIntentData(Bundle bundle) {

        if (bundle != null) { // get a list of image
            prefix = bundle.getString("TYPE");
        }
    }

    public void initComponent(View view) {


        txtBack = (MaterialDesignTextView) view.findViewById(R.id.ng_txt_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.ng_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Log.i("ddd", "close");
                if (G.IMAGE_NEW_GROUP.exists()) {
                    G.IMAGE_NEW_GROUP.delete();
                } else {
                    G.IMAGE_NEW_CHANEL.delete();
                }
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
            }
        });
//        txtBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Log.i("ddd", "close");
//                if (G.IMAGE_NEW_GROUP.exists()) {
//                    G.IMAGE_NEW_GROUP.delete();
//                } else {
//                    G.IMAGE_NEW_CHANEL.delete();
//                }
//                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
//            }
//        });

        txtTitleToolbar = (TextView) view.findViewById(R.id.ng_txt_titleToolbar);
        txtTitleToolbar.setTypeface(G.arial);
        if (prefix.equals("NewChanel")) {
            txtTitleToolbar.setText("New Chanel");
        } else {
            txtTitleToolbar.setText("New Group");
        }

        parent = (RelativeLayout) view.findViewById(R.id.ng_fragmentContainer);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //=======================set image for group
        imgCircleImageView = (CircleImageView) view.findViewById(R.id.ng_profile_circle_image);
//        imgCircleImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                new MaterialDialog.Builder(getActivity())
//                        .title("Choose Picture")
//                        .negativeText("CANCEL")
//                        .items(R.array.profile)
//                        .itemsCallback(new MaterialDialog.ListCallback() {
//                            @Override
//                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//
//                                if (text.toString().equals("From Camera")) {
//
//                                    if (G.context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
//
//                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                        if (prefix.equals("NewChanel")) {
//                                            uriIntent = Uri.fromFile(G.IMAGE_NEW_CHANEL);
//                                        } else {
//                                            uriIntent = Uri.fromFile(G.IMAGE_NEW_GROUP);
//                                        }
//
//                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
//                                        startActivityForResult(intent, myResultCodeCamera);
//                                        dialog.dismiss();
//
//                                    } else {
//                                        Toast.makeText(G.context, "Please check your Camera", Toast.LENGTH_SHORT).show();
//                                    }
//
//                                } else {
//                                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                    startActivityForResult(intent, myResultCodeGallery);
//                                    dialog.dismiss();
//                                }
//                            }
//                        }).show();
//            }
//        });

        RippleView rippleCircleImage = (RippleView) view.findViewById(R.id.ng_ripple_circle_image);
        rippleCircleImage.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                new MaterialDialog.Builder(getActivity())
                        .title("Choose Picture")
                        .negativeText("CANCEL")
                        .items(R.array.profile)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                if (text.toString().equals("From Camera")) {

                                    if (G.context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {

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
                                        Toast.makeText(G.context, "Please check your Camera", Toast.LENGTH_SHORT).show();
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

        //=======================name of group
        TextInputLayout txtInputNewGroup = (TextInputLayout) view.findViewById(R.id.ng_txtInput_newGroup);

        edtGroupName = (EditText) view.findViewById(R.id.ng_edt_newGroup);
        if (prefix.equals("NewChanel")) {
            txtInputNewGroup.setHint("New Chanel");
        } else {
            txtInputNewGroup.setHint("New Group");
        }

        //=======================description group
        edtDescription = (LinedEditText) view.findViewById(R.id.ng_edt_description);

        edtDescription.setTypeface(G.arial);
        edtDescription.setSingleLine(false);
        edtDescription.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        edtDescription.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        edtDescription.setLines(4);
        edtDescription.setMaxLines(4);
        //=======================button next step

        txtNextStep = (TextView) view.findViewById(R.id.ng_txt_nextStep);
        txtNextStep.setTypeface(G.arial);
        txtNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtDescription.getText().toString().length() > 0) {
                    if (edtGroupName.getText().toString().length() > 0) {
                        boolean success;
                        String newName = edtGroupName.getText().toString().replace(" ", "_");
                        File file2 = new File(path, prefix + "_" + newName + Math.random() * 10000 + 1 + ".png");
                        if (prefix.equals("NewChanel")) {
                            success = G.IMAGE_NEW_CHANEL.renameTo(file2);
                            startActivity(new Intent(G.context, ActivityNewChanelFinish.class));
                            getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
                        } else {
                            success = G.IMAGE_NEW_GROUP.renameTo(file2);
                            createGroup();
                        }

                    } else {
                        Toast.makeText(G.context, "Please Enter Description For Group", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(G.context, "Please Enter Your Name Group", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //=======================button cancel
        txtCancel = (TextView) view.findViewById(R.id.ng_txt_cancel);
        txtCancel.setTypeface(G.arial);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (G.IMAGE_NEW_GROUP.exists()) {
                    G.IMAGE_NEW_GROUP.delete();
                } else {
                    G.IMAGE_NEW_CHANEL.delete();
                }
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
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
                android.support.v4.app.Fragment fragment = ContactGroupFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putLong("RoomId", roomId);
                bundle.putBoolean("NewRoom", true);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
                ActivityMain.mLeftDrawerLayout.closeDrawer();
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
            }
        };

        new RequestClientGetRoom().clientGetRoom(roomId);
    }

    //=======================result for picture

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == myResultCodeCamera && resultCode == getActivity().RESULT_OK) {// result for camera

            Intent intent = new Intent(getActivity(), ActivityCrop.class);
            if (uriIntent != null) {
                intent.putExtra("IMAGE_CAMERA", uriIntent.toString());
                intent.putExtra("TYPE", "camera");
                intent.putExtra("PAGE", prefix);
                startActivityForResult(intent, myResultFragment);
            } else {
                Toast.makeText(G.context, "can't save picture,pleas try again.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == myResultCodeGallery && resultCode == getActivity().RESULT_OK) {// result for gallery
            if (data != null) {
                Intent intent = new Intent(getActivity(), ActivityCrop.class);
                intent.putExtra("IMAGE_CAMERA", data.getData().toString());
                intent.putExtra("TYPE", "gallery");
                intent.putExtra("PAGE", prefix);
                startActivityForResult(intent, myResultFragment);
//            getActivity().getFragmentManager().beginTransaction().remove(FragmentNewGroup.this).commit();
            }
        } else if (requestCode == myResultFragment) {

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

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Toast.makeText(getActivity(), "ddd", Toast.LENGTH_SHORT).show();
    }
}
