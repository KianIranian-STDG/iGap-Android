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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import net.iGap.helper.ImageHelper;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.helper.upload.OnUploadListener;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.model.PassCode;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.CircleImageView;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.structs.StructContactInfo;
import net.iGap.module.upload.UploadObject;
import net.iGap.module.upload.Uploader;
import net.iGap.observers.interfaces.OnAvatarAdd;
import net.iGap.observers.interfaces.OnChannelAvatarAdd;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.OnGroupAddMember;
import net.iGap.observers.interfaces.OnGroupAvatarResponse;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestGroupAddMember;
import net.iGap.viewmodel.FragmentNewGroupViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static net.iGap.G.context;
import static net.iGap.module.AttachFile.isInAttach;
import static net.iGap.module.AttachFile.request_code_image_from_gallery_single_select;

public class FragmentNewGroup extends BaseFragment implements OnGroupAvatarResponse, OnChannelAvatarAdd, ToolbarListener, FragmentEditImage.OnImageEdited {

    public static RemoveSelectedContact removeSelectedContact;
    public static long avatarId = 0;
    public static OnRemoveFragmentNewGroup onRemoveFragmentNewGroup;
    //  private String path;
    private static ProtoGlobal.Room.Type type;
    FragmentNewGroupViewModel fragmentNewGroupViewModel;
    ActivityNewGroupBinding fragmentNewGroupBinding;
    private CircleImageView imgCircleImageView;
    private ImageView imgProfileHelper;
    private long groomId = 0;
    private EditText edtGroupName;
    private AppCompatEditText edtDescription;
    private int lastSpecialRequestsCursorPosition = 0;
    private String specialRequests;
    private String pathSaveImage;

    private int countAddMemberResponse = 0;
    private int countMember = 0;
    private int countAddMemberRequest = 0;

    private long createdRoomId = 0;
    private HelperToolbar mHelperToolbar;
    private boolean isGroup = false;

    public static FragmentNewGroup newInstance() {
        return new FragmentNewGroup();
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentNewGroupBinding = DataBindingUtil.inflate(inflater, R.layout.activity_new_group, container, false);
        return attachToSwipeBack(fragmentNewGroupBinding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        initComponent(view);

        Bundle bundle = getArguments();

        if (bundle.getString("TYPE") != null && bundle.getString("TYPE").equals("NewGroup")) {
            isGroup = true;
            initGroupMembersRecycler();
        }

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

        fragmentNewGroupViewModel.goToContactGroupPage.observe(this, data -> {
            if (getActivity() != null && data != null) {
                if (!getActivity().isFinishing()) {
                    getActivity().onBackPressed();
                }
                if (G.iTowPanModDesinLayout != null) {
                    G.iTowPanModDesinLayout.onLayout(ActivityMain.chatLayoutMode.none);
                }
                Fragment fragment = ContactGroupFragment.newInstance();
                Bundle b = new Bundle();
                b.putLong("RoomId", data.getRoomId());
                b.putString("LIMIT", data.getLimit());
                b.putString("TYPE", data.getType());
                b.putBoolean("NewRoom", data.isNewRoom());
                fragment.setArguments(b);
                /*if (FragmentNewGroup.onRemoveFragmentNewGroup != null)
                    FragmentNewGroup.onRemoveFragmentNewGroup.onRemove();*/
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });

        fragmentNewGroupViewModel.goToCreateChannelPage.observe(this, data -> {
            if (getActivity() != null && data != null) {
                /*if (!getActivity().isFinishing()) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }*/
                if (G.iTowPanModDesinLayout != null) {
                    G.iTowPanModDesinLayout.onLayout(ActivityMain.chatLayoutMode.none);
                }
                FragmentCreateChannel fragmentCreateChannel = new FragmentCreateChannel();
                Bundle bdle = new Bundle();
                bdle.putLong("ROOMID", data.getRoomId());
                bdle.putString("INVITE_LINK", data.getInviteLink());
                bdle.putString("TOKEN", data.getToken());
                fragmentCreateChannel.setArguments(bdle);

                /*if (FragmentNewGroup.onRemoveFragmentNewGroup != null)
                    FragmentNewGroup.onRemoveFragmentNewGroup.onRemove();*/

                new HelperFragment(getActivity().getSupportFragmentManager(), fragmentCreateChannel).setReplace(false).load();
            }
        });
    }

    private void initGroupMembersRecycler() {

        if (ContactGroupFragment.selectedContacts.size() != 0) {
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
                createdRoomId = aLong;
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
                if (photoFile != null && getActivity() != null) {
                    new AttachFile(getActivity()).dispatchTakePictureIntent(FragmentNewGroup.this);
                }
            }
        } else {
            try {
                if (getActivity() != null) {
                    new AttachFile(getActivity()).requestTakePicture(FragmentNewGroup.this);
                }
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
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.check_icon)
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
        imgProfileHelper = fragmentNewGroupBinding.ngProfileCircleImageHolder;
        //AndroidUtils.setBackgroundShapeColor(imgCircleImageView, Color.parseColor(G.appBarColor));

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
        DbManager.getInstance().doRealmTask(realm -> {
            RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
            imgProfileHelper.setVisibility(View.GONE);
            imgCircleImageView.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imgCircleImageView.getContext().getResources().getDimension(R.dimen.dp100), realmUserInfo.getUserInfo().getInitials(), realmUserInfo.getUserInfo().getColor()));
        });
    }

    private void setImage(final String imagePath) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (imagePath != null && new File(imagePath).exists()) {
                    imgCircleImageView.setPadding(0, 0, 0, 0);
                    imgProfileHelper.setVisibility(View.GONE);
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
                            if (isGroup) {
                                createdRoomId = roomId;
                                addMembersToGroup();
                            } else {
                                startRoom(roomId);
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onAvatarAddError() {
        fragmentNewGroupViewModel.hideProgressBar();
    }

    private void startRoom(long roomId) {
        if (getActivity() != null) {
            Fragment fragment = ContactGroupFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putLong("RoomId", roomId);

            if (FragmentNewGroupViewModel.prefix.equals("NewChanel")) {
                bundle.putString("TYPE", ProtoGlobal.Room.Type.CHANNEL.toString());
            } else {
                bundle.putString("TYPE", ProtoGlobal.Room.Type.GROUP.toString());
            }

            bundle.putBoolean("NewRoom", true);
            fragment.setArguments(bundle);
            getActivity().onBackPressed();
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load(true);
        }
    }

    private void startChannelRoom(long roomId) {
        fragmentNewGroupViewModel.hideProgressBar();
        FragmentCreateChannel fragmentCreateChannel = new FragmentCreateChannel();
        Bundle bundle = new Bundle();
        bundle.putLong("ROOMID", roomId);
        bundle.putString("INVITE_LINK", fragmentNewGroupViewModel.mInviteLink);
        bundle.putString("TOKEN", fragmentNewGroupViewModel.token);
        fragmentCreateChannel.setArguments(bundle);
        popBackStackFragment();
        popBackStackFragment();
        if (getActivity() != null) {
            new HelperFragment(getActivity().getSupportFragmentManager(), fragmentCreateChannel).load();
        }
    }

    //=======================result for picture
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * If it's in the app and the screen lock is activated after receiving the result of the camera and .... The page code is displayed.
         * The wizard will  be set ActivityMain.isUseCamera = true to prevent the page from being opened....
         */
        if (PassCode.getInstance().isPassCode()) ActivityMain.isUseCamera = true;

        if (FragmentEditImage.textImageList != null) FragmentEditImage.textImageList.clear();
        if (FragmentEditImage.itemGalleryList != null) FragmentEditImage.itemGalleryList.clear();

        if (requestCode == AttachFile.request_code_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {// result for camera
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true); //rotate image
                FragmentEditImage.insertItemList(AttachFile.mCurrentPhotoPath, false);
            } else {
                ImageHelper.correctRotateImage(AttachFile.imagePath, true); //rotate image
                FragmentEditImage.insertItemList(AttachFile.imagePath, false);
            }
            if (getActivity() != null) {
                FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, false, false, 0);
                fragmentEditImage.setOnProfileImageEdited(this);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
            }
        } else if (requestCode == request_code_image_from_gallery_single_select && resultCode == Activity.RESULT_OK) {// result for gallery
            if (data != null) {
                ImageHelper.correctRotateImage(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), true);
                FragmentEditImage.insertItemList(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), false);
                if (getActivity() != null) {
                    FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, false, false, 0);
                    fragmentEditImage.setOnProfileImageEdited(this);
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
                }
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
        FragmentNewGroupViewModel.mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void profileImageAdd(String path) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack(FragmentNewGroup.class.getName(), 0);
        }
        pathSaveImage = path;
        avatarId = System.nanoTime();

        fragmentNewGroupViewModel.showProgressBar();
        //showProgressBar();

        Uploader.getInstance().upload(UploadObject.createForAvatar(avatarId, pathSaveImage, fragmentNewGroupViewModel.token, ProtoGlobal.RoomMessageType.IMAGE, new OnUploadListener() {
            @Override
            public void onProgress(String id, int progress) {
                fragmentNewGroupBinding.ngPrgWaiting.setProgress(progress);
            }

            @Override
            public void onFinish(String id, String token) {
                fragmentNewGroupViewModel.hideProgressBar();
                fragmentNewGroupViewModel.existAvatar = true;
                fragmentNewGroupViewModel.token = token;
                setImage(pathSaveImage);
            }

            @Override
            public void onError(String id) {
                fragmentNewGroupViewModel.hideProgressBar();

            }

        }));
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

    private void addMembersToGroup() {

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

            if (getActivity() instanceof ActivityMain && isAdded()) {
                Log.wtf(this.getClass().getName(), "addMembersToGroup is done");
               /* if (FragmentNewGroup.onRemoveFragmentNewGroup != null)
                    FragmentNewGroup.onRemoveFragmentNewGroup.onRemove();*/
                G.refreshRealmUi();
                /*popBackStackFragment();
                popBackStackFragment();
                removeFromBaseFragment(FragmentNewGroup.this);*/
                ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                new GoToChatActivity(createdRoomId).startActivity(getActivity());
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
        RealmRoom.updateMemberCount(roomId, roomType, ContactGroupFragment.selectedContacts.size() + 1); // plus with 1 , for own account
        if (getActivity() != null && isAdded()) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    G.refreshRealmUi();
                    ((ActivityMain) getActivity()).removeAllFragmentFromMain();
//                    popBackStackFragment();
//                    popBackStackFragment();
                    ContactGroupFragment.selectedContacts.clear();
//                    removeFromBaseFragment(FragmentNewGroup.this);
                    new GoToChatActivity(roomId).startActivity(getActivity());
                }
            });
        }
    }

    private class SelectedContactAdapter extends RecyclerView.Adapter<SelectedContactAdapter.ViewHolderSelectedContact> {

        private List<StructContactInfo> mItems = new ArrayList<>();

        public SelectedContactAdapter() {
        }

        @NonNull
        @Override
        public ViewHolderSelectedContact onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact_chat, viewGroup, false);

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

        public class ViewHolderSelectedContact extends RecyclerView.ViewHolder {

            private TextView txtName;
            private TextView txtPhone;
            private CircleImageView imgAvatar;
            private TextView btnRemove;
            private CheckBox chSelected;

            public ViewHolderSelectedContact(@NonNull View itemView) {
                super(itemView);

                txtName = itemView.findViewById(R.id.tv_itemContactChat_userName);
                txtPhone = itemView.findViewById(R.id.tv_itemContactChat_userPhoneNumber);
                btnRemove = itemView.findViewById(R.id.tv_itemContactChat_remove);
                imgAvatar = itemView.findViewById(R.id.iv_itemContactChat_profileImage);
                chSelected = itemView.findViewById(R.id.iv_itemContactChat_checkBox);
            }

            public void bindData(final StructContactInfo data) {

                if (!G.isAppRtl) {
                    txtName.setGravity(Gravity.LEFT);
                    txtPhone.setGravity(Gravity.LEFT);
                } else {
                    txtName.setGravity(Gravity.RIGHT);
                    txtPhone.setGravity(Gravity.RIGHT);
                }

                txtPhone.setVisibility(View.INVISIBLE);
                chSelected.setVisibility(View.GONE);
                btnRemove.setVisibility(View.VISIBLE);
                btnRemove.setTypeface(ResourcesCompat.getFont(btnRemove.getContext(), R.font.main_font));
                txtName.setText(EmojiManager.getInstance().replaceEmoji(data.displayName, txtName.getPaint().getFontMetricsInt()));
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

    public interface RemoveSelectedContact {
        void onRemoved(StructContactInfo item);
    }

}
