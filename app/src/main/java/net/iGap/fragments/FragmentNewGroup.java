/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.ActivityNewGroupBinding;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnChannelAvatarAdd;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnGroupAddMember;
import net.iGap.interfaces.OnGroupAvatarResponse;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.CircleImageView;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.LinedEditText;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestGroupAddMember;
import net.iGap.viewmodel.FragmentNewGroupViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

import static net.iGap.G.context;
import static net.iGap.module.AttachFile.isInAttach;
import static net.iGap.module.AttachFile.request_code_image_from_gallery_single_select;

public class FragmentNewGroup extends BaseFragment implements OnGroupAvatarResponse, OnChannelAvatarAdd, ToolbarListener {

    public static RemoveSelectedContact removeSelectedContact ;
    public static long avatarId = 0;
    public static OnRemoveFragmentNewGroup onRemoveFragmentNewGroup;
    //  private String path;
    private static ProtoGlobal.Room.Type type;
    FragmentNewGroupViewModel fragmentNewGroupViewModel;
    ActivityNewGroupBinding fragmentNewGroupBinding;
    private CircleImageView imgCircleImageView;
    private long groomId = 0;
    private EditText edtGroupName;
    private AppCompatEditText edtDescription;
    private int lastSpecialRequestsCursorPosition = 0;
    private String specialRequests;
    private String pathSaveImage;

    private int countAddMemberResponse = 0;
    private int countMember = 0;
    private int countAddMemberRequest = 0;

    private long createdRoomId = 0 ;
    private HelperToolbar mHelperToolbar;

    public static FragmentNewGroup newInstance() {
        return new FragmentNewGroup();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentNewGroupBinding = DataBindingUtil.inflate(inflater, R.layout.activity_new_group, container, false);
        return attachToSwipeBack(fragmentNewGroupBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        initComponent(view);

        Bundle bundle = getArguments();

        if ( bundle.getString("TYPE") != null && bundle.getString("TYPE").equals("NewGroup")){
            initGroupMembersRecycler();
        }

        FragmentEditImage.completeEditImage = new FragmentEditImage.CompleteEditImage() {
            @Override
            public void result(String path, String message, HashMap<String, StructBottomSheet> textImageList) {

                pathSaveImage = path;
                avatarId = System.nanoTime();

                fragmentNewGroupViewModel.showProgressBar();
                //showProgressBar();
                HelperUploadFile.startUploadTaskAvatar(pathSaveImage, avatarId, new HelperUploadFile.UpdateListener() {
                    @Override
                    public void OnProgress(int progress, FileUploadStructure struct) {
                        if (progress < 100) {
                            fragmentNewGroupBinding.ngPrgWaiting.setProgress(progress);
                        } else {
                            fragmentNewGroupViewModel.hideProgressBar();
                            fragmentNewGroupViewModel.existAvatar = true;
                            fragmentNewGroupViewModel.token = struct.token;
                            setImage(pathSaveImage);
                        }
                    }

                    @Override
                    public void OnError() {
                        fragmentNewGroupViewModel.hideProgressBar();
                    }
                });
            }
        };

        onRemoveFragmentNewGroup = new OnRemoveFragmentNewGroup() {
                    @Override
                    public void onRemove() {
                        try {
                            popBackStackFragment();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                };
    }

    private void initGroupMembersRecycler() {

        if (ContactGroupFragment.selectedContacts.size() != 0 ){
            fragmentNewGroupBinding.angLayoutMembers.setVisibility(View.VISIBLE);
        }
        RecyclerView rv = fragmentNewGroupBinding.angRecyclerViewSelectedContact;
        rv.setLayoutManager(new LinearLayoutManager(context));
        SelectedContactAdapter adapter = new SelectedContactAdapter();
        adapter.setData(ContactGroupFragment.selectedContacts);
        rv.setAdapter(adapter);

    }

    private void initDataBinding() {
        fragmentNewGroupViewModel = new FragmentNewGroupViewModel(this.getArguments());
        fragmentNewGroupBinding.setFragmentNewGroupVieModel(fragmentNewGroupViewModel);

        fragmentNewGroupViewModel.createdRoomId.observe(this, new Observer<Long>() {
            @Override
            public void onChanged(@Nullable Long aLong) {
                createdRoomId = aLong ;
                addMembersToGroup();
            }
        });
    }

    private void showDialogSelectGallery() {
        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.choose_picture)).negativeText(G.fragmentActivity.getResources().getString(R.string.cancel)).items(R.array.profile).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0: {

                        try {
                            HelperPermission.getStoragePermision(context, new OnGetPermission() {
                                @Override
                                public void Allow() {

                                    if (isAdded()) { // boolean isAdded () Return true if the fragment is currently added to its activity.
                                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        intent.setType("image/*");
                                        startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_picture_en)), request_code_image_from_gallery_single_select);
                                        isInAttach = true;
                                    }
                                }

                                @Override
                                public void deny() {

                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case 1: {

                        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                            try {

                                HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                                    @Override
                                    public void Allow() throws IOException {
                                        HelperPermission.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
                                            @Override
                                            public void Allow() {
                                                // this dialog show 2 way for choose image : gallery and camera

                                                if (isAdded()) {
                                                    useCamera();
                                                }
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void deny() {

                                            }
                                        });
                                    }

                                    @Override
                                    public void deny() {

                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, R.string.please_check_your_camera, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
            }
        }).show();
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                //                                            new AttachFile(FragmentNewGroup.this.G.fragmentActivity).dispatchTakePictureIntent();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(G.fragmentActivity.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        return;
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(FragmentNewGroup.this);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            try {
                new AttachFile(G.fragmentActivity).requestTakePicture(FragmentNewGroup.this);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void initComponent(View view) {
        G.onGroupAvatarResponse = this;
        G.onChannelAvatarAdd = this;

        AppUtils.setProgresColler(fragmentNewGroupBinding.ngPrgWaiting);

        mHelperToolbar = HelperToolbar.create()
                .setContext(context)
                .setLogoShown(true)
                .setLeftIcon(R.drawable.ic_back_btn)
                .setRightIcons(R.drawable.ic_checked)
                .setListener(this);

        LinearLayout toollbarLayout = view.findViewById(R.id.ng_layout_toolbar);
        toollbarLayout.addView(mHelperToolbar.getView());

        fragmentNewGroupViewModel.titleToolbar.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mHelperToolbar.setDefaultTitle(s);
            }
        });

        G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        //=======================set image for group
        imgCircleImageView = fragmentNewGroupBinding.ngProfileCircleImage;
        AndroidUtils.setBackgroundShapeColor(imgCircleImageView, Color.parseColor(G.appBarColor));

        RippleView rippleCircleImage = fragmentNewGroupBinding.ngRippleCircleImage;
        rippleCircleImage.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) throws IOException {

                HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                    @Override
                    public void Allow() {
                        showDialogSelectGallery();
                    }

                    @Override
                    public void deny() {

                    }
                });
            }
        });

        //=======================name of group
        edtGroupName = fragmentNewGroupBinding.ngEdtNewGroup;
        final View ViewGroupName = fragmentNewGroupBinding.ngViewNewGroup;
        edtGroupName.setPadding(0, 8, 0, 8);
        edtGroupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    ViewGroupName.setBackgroundColor(Color.parseColor(G.appBarColor));
                } else {
                    ViewGroupName.setBackgroundColor(Color.parseColor(G.lineBorder));
                }
            }
        });

        //=======================description group
        edtDescription = fragmentNewGroupBinding.ngEdtDescription;
        edtDescription.setPadding(0, 8, 0, 8);

        edtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastSpecialRequestsCursorPosition = edtDescription.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edtDescription.removeTextChangedListener(this);

                if (edtDescription.getLineCount() > 2) {
                    edtDescription.setText(specialRequests);
                    edtDescription.setSelection(lastSpecialRequestsCursorPosition);
                } else {
                    specialRequests = edtDescription.getText().toString();
                }

                edtDescription.addTextChangedListener(this);
            }
        });
    }

    private void showInitials() {
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        imgCircleImageView.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp100), realmUserInfo.getUserInfo().getInitials(), realmUserInfo.getUserInfo().getColor()));

        realm.close();
    }

    private void setImage(final String imagePath) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (imagePath != null && new File(imagePath).exists()) {
                    imgCircleImageView.setPadding(0, 0, 0, 0);
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(imagePath), imgCircleImageView);
                } else {
                    showInitials();
                    imgCircleImageView.setPadding(0, 0, 0, 0);
                }
            }
        });
    }

    @Override
    public void onAvatarAdd(final long roomId, final ProtoGlobal.Avatar avatar) {

        avatarHandler.avatarAdd(roomId, pathSaveImage, avatar, new OnAvatarAdd() {
            @Override
            public void onAvatarAdd(final String avatarPath) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        fragmentNewGroupViewModel.hideProgressBar();
                        setImage(avatarPath);

                        if (fragmentNewGroupViewModel.isChannel) {
                            startChannelRoom(roomId);
                        } else {
                            startRoom(roomId);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onAvatarAddError() {
        fragmentNewGroupViewModel.hideProgressBar();
        ;
    }

    private void startRoom(long roomId) {
        Fragment fragment = ContactGroupFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putLong("RoomId", roomId);

        if (fragmentNewGroupViewModel.prefix.equals("NewChanel")) {
            bundle.putString("TYPE", ProtoGlobal.Room.Type.CHANNEL.toString());
        } else {
            bundle.putString("TYPE", ProtoGlobal.Room.Type.GROUP.toString());
        }

        bundle.putBoolean("NewRoom", true);
        fragment.setArguments(bundle);

        popBackStackFragment();
        new HelperFragment(fragment).load();
    }

    private void startChannelRoom(long roomId) {
        fragmentNewGroupViewModel.hideProgressBar();
        ;
        FragmentCreateChannel fragmentCreateChannel = new FragmentCreateChannel();
        Bundle bundle = new Bundle();
        bundle.putLong("ROOMID", roomId);
        bundle.putString("INVITE_LINK", fragmentNewGroupViewModel.mInviteLink);
        bundle.putString("TOKEN", fragmentNewGroupViewModel.token);
        fragmentCreateChannel.setArguments(bundle);

        popBackStackFragment();
        new HelperFragment(fragmentCreateChannel).load();
    }

    //=======================result for picture
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * If it's in the app and the screen lock is activated after receiving the result of the camera and .... The page code is displayed.
         * The wizard will  be set ActivityMain.isUseCamera = true to prevent the page from being opened....
         */
        if (G.isPassCode) ActivityMain.isUseCamera = true;

        if (FragmentEditImage.textImageList != null) FragmentEditImage.textImageList.clear();
        if (FragmentEditImage.itemGalleryList != null) FragmentEditImage.itemGalleryList.clear();

        if (requestCode == AttachFile.request_code_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {// result for camera

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true); //rotate image

                FragmentEditImage.insertItemList(AttachFile.mCurrentPhotoPath, false);
                new HelperFragment(FragmentEditImage.newInstance(null, false, false, 0)).setReplace(false).load();


            } else {
                ImageHelper.correctRotateImage(AttachFile.imagePath, true); //rotate image
                FragmentEditImage.insertItemList(AttachFile.imagePath, false);
                new HelperFragment(FragmentEditImage.newInstance(null, false, false, 0)).setReplace(false).load();
            }
        } else if (requestCode == request_code_image_from_gallery_single_select && resultCode == Activity.RESULT_OK) {// result for gallery
            if (data != null) {
                ImageHelper.correctRotateImage(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), true);
                FragmentEditImage.insertItemList(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), false);
                new HelperFragment(FragmentEditImage.newInstance(null, false, false, 0)).setReplace(false).load();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */);

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        fragmentNewGroupViewModel.mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public interface OnRemoveFragmentNewGroup {
        void onRemove();
    }

    @Override
    public void onLeftIconClickListener(View view) {
/*

        AppUtils.closeKeyboard(view);
        if (G.IMAGE_NEW_GROUP.exists()) {
            G.IMAGE_NEW_GROUP.delete();
        } else {
            G.IMAGE_NEW_CHANEL.delete();
        }
*/

         fragmentNewGroupViewModel.onClickCancel(view);
        //G.fragmentActivity.onBackPressed();
    }

    @Override
    public void onRightIconClickListener(View view) {
        fragmentNewGroupViewModel.onClickNextStep(view);
    }

    private void addMembersToGroup(){

        G.onGroupAddMember = new OnGroupAddMember() {
            @Override
            public void onGroupAddMember(Long roomId, Long UserId) {
                countAddMemberResponse++;
                countMember++;
                if (countAddMemberResponse == countAddMemberRequest) {
                    addMember(roomId, ProtoGlobal.Room.Type.GROUP);
                }
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                countAddMemberResponse++;
                if (countAddMemberResponse == countAddMemberRequest) {
                    addMember(createdRoomId, ProtoGlobal.Room.Type.GROUP);
                }
            }
        };


        /**
         * request add member for group
         *
         */
        //countAddMemberRequest = ContactGroupFragment.selectedContacts.size() ;
        ArrayList<Long> list = getSelectedList();
        if (list.size() > 0) {
            for (long peerId : list) {
                new RequestGroupAddMember().groupAddMember(createdRoomId, peerId, 0);
            }
        } else {

            if (isAdded()) {
               /* if (FragmentNewGroup.onRemoveFragmentNewGroup != null)
                    FragmentNewGroup.onRemoveFragmentNewGroup.onRemove();*/
               popBackStackFragment();
               popBackStackFragment();
                removeFromBaseFragment(FragmentNewGroup.this);
                new GoToChatActivity(createdRoomId).startActivity();
            }

        }
    }

    private ArrayList<Long> getSelectedList() {
        ArrayList<Long> list = new ArrayList<>();

        for (int i = 0; i < ContactGroupFragment.selectedContacts.size(); i++) {
            if (ContactGroupFragment.selectedContacts.get(i).isSelected) {
                countAddMemberRequest++;
                list.add(ContactGroupFragment.selectedContacts.get(i).peerId);
            }
        }

        return list;
    }

    private void addMember(long roomId, ProtoGlobal.Room.Type roomType) {
        RealmRoom.addOwnerToDatabase(roomId);
        RealmRoom.updateMemberCount(roomId, roomType, ContactGroupFragment.selectedContacts.size()+ 1); // plus with 1 , for own account
        if (isAdded()) {
            popBackStackFragment();
            popBackStackFragment();
            ContactGroupFragment.selectedContacts.clear();
            removeFromBaseFragment(FragmentNewGroup.this);
            new GoToChatActivity(roomId).startActivity();
        }
    }

    private class SelectedContactAdapter extends RecyclerView.Adapter<SelectedContactAdapter.ViewHolderSelectedContact>{

        private List<StructContactInfo> mItems = new ArrayList<>();
        private LayoutInflater inflater;

        public SelectedContactAdapter() {
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public ViewHolderSelectedContact onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = inflater.inflate(R.layout.item_contact_chat , viewGroup , false);

            return new ViewHolderSelectedContact(view);
        }

        public void setData(List<StructContactInfo> mItems) {
            this.mItems.addAll(mItems);
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderSelectedContact holder, int i) {
            holder.bindData(mItems.get(i));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolderSelectedContact extends RecyclerView.ViewHolder{

            private TextView txtName , txtPhone ;
            private de.hdodenhof.circleimageview.CircleImageView imgAvatar ;
            private TextView btnRemove;
            private CheckBox chSelected ;

            public ViewHolderSelectedContact(@NonNull View itemView) {
                super(itemView);

                txtName = itemView.findViewById(R.id.tv_itemContactChat_userName);
                txtPhone = itemView.findViewById(R.id.tv_itemContactChat_userPhoneNumber);
                btnRemove = itemView.findViewById(R.id.tv_itemContactChat_remove);
                imgAvatar = itemView.findViewById(R.id.iv_itemContactChat_profileImage);
                chSelected = itemView.findViewById(R.id.iv_itemContactChat_checkBox);
            }

            public void bindData(final StructContactInfo data){

                txtPhone.setVisibility(View.INVISIBLE);
                chSelected.setVisibility(View.GONE);
                btnRemove.setVisibility(View.VISIBLE);
                txtName.setText(data.displayName);
                avatarHandler.getAvatar(new ParamWithAvatarType(imgAvatar, data.peerId).avatarType(AvatarHandler.AvatarType.USER));

                btnRemove.setOnClickListener(v -> {
                    mItems.remove(data);
                    notifyDataSetChanged();

                    if (removeSelectedContact != null)
                        removeSelectedContact.onRemoved(data);
                });
            }
        }
    }

    public interface RemoveSelectedContact{
        void onRemoved(StructContactInfo item);
    }

}
