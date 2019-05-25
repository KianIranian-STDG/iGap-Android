package net.iGap.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityManageSpace;
import net.iGap.databinding.FragmentSettingBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnUserAvatarResponse;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.EmojiTextViewE;
import net.iGap.module.FileUploadStructure;
import net.iGap.module.SUID;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestUserAvatarAdd;
import net.iGap.viewmodel.FragmentSettingViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import static android.app.Activity.RESULT_OK;
import static net.iGap.module.AttachFile.request_code_image_from_gallery_single_select;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSetting extends BaseFragment implements OnUserAvatarResponse {

    public static String pathSaveImage;
    public static DateType dateType;
    /*public static onRemoveFragmentSetting onRemoveFragmentSetting;*/
    /*public static onClickBack onClickBack;*/
    public ProgressBar prgWait;
    private SharedPreferences sharedPreferences;
    private EmojiTextViewE txtNickName;
    private Uri uriIntent;
    private long idAvatar;

    private View submitButton;
    private FragmentSettingBinding binding;
    private FragmentSettingViewModel viewModel;

    public FragmentSetting() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        viewModel = new FragmentSettingViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HelperToolbar t = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.drawable.ic_back_btn)
                .setRightIcons(R.drawable.ic_checked)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.settings))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onRightIconClickListener(View view) {

                    }
                });
        binding.toolbar.addView(t.getView());
        submitButton = t.getRightButton();
        submitButton.setVisibility(View.GONE);

        viewModel.goToShowAvatar.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(viewModel.userId, FragmentShowAvatars.From.setting);
                new HelperFragment(fragment).setReplace(false).load();
            }
        });

        viewModel.showDialogDeleteAccount.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                showDeleteAccountDialog();
            }
        });

        viewModel.goToManageSpacePage.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                startActivity(new Intent(G.fragmentActivity, ActivityManageSpace.class));
            }
        });

        viewModel.showDialogLogout.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                showDialogLogout();
            }
        });

        viewModel.showError.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.error), false);
            }
        });

        viewModel.showSubmitButton.observe(this, aBoolean -> {
            if (aBoolean != null) {
                Log.wtf("fragment setting", "value of show visibility: " + aBoolean);
                submitButton.setVisibility(aBoolean ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.showDialogChooseImage.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                startDialog();
            }
        });

        AppUtils.setProgresColler(binding.loading);




        /*onClickBack = new onClickBack() {
            @Override
            public void back() {
                G.fragmentActivity.onBackPressed();
            }
        };*/


        FragmentShowAvatars.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {

                //                showImage();
                long mAvatarId = 0;
                if (messageOne != null && !messageOne.equals("")) {
                    mAvatarId = Long.parseLong(messageOne);
                }
                avatarHandler.avatarDelete(new ParamWithAvatarType(binding.userAvatar, viewModel.userId)
                        .avatarType(AvatarHandler.AvatarType.USER), mAvatarId);
            }
        };

        FragmentEditImage.completeEditImage = new FragmentEditImage.CompleteEditImage() {
            @Override
            public void result(String path, String message, HashMap<String, StructBottomSheet> textImageList) {

                pathSaveImage = path;
                long lastUploadedAvatarId = idAvatar + 1L;

                viewModel.showLoading.setValue(true);
                HelperUploadFile.startUploadTaskAvatar(pathSaveImage, lastUploadedAvatarId, new HelperUploadFile.UpdateListener() {
                    @Override
                    public void OnProgress(int progress, FileUploadStructure struct) {
                        if (progress < 100) {
                            binding.loading.setProgress(progress);
                        } else {
                            new RequestUserAvatarAdd().userAddAvatar(struct.token);
                        }
                    }

                    @Override
                    public void OnError() {
                        viewModel.showLoading.setValue(false);
                    }
                });
            }
        };

        setAvatar();


        /*onRemoveFragmentSetting = new onRemoveFragmentSetting() {
            @Override
            public void removeFragment() {
                removeFromBaseFragment(FragmentSetting.this);
            }
        };*/


    }

    @Override
    public void onAvatarAdd(final ProtoGlobal.Avatar avatar) {

        /**
         * if another account do this action we haven't avatar source and have
         *  to download avatars . for do this action call HelperAvatar.getAvatar
         */
        if (pathSaveImage == null) {
            setAvatar();
        } else {

            avatarHandler.avatarAdd(viewModel.userId, pathSaveImage, avatar, new OnAvatarAdd() {
                @Override
                public void onAvatarAdd(final String avatarPath) {
                    G.handler.post(() -> {
                        viewModel.showLoading.setValue(false);
                        setImage(avatarPath);
                    });
                }
            });
            pathSaveImage = null;
        }
    }

    @Override
    public void onAvatarAddTimeOut() {
        viewModel.showLoading.setValue(false);
    }

    @Override
    public void onAvatarError() {
        viewModel.showLoading.setValue(false);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.onResume();
        /*new RequestUserIVandGetScore().userIVandGetScore(new OnUserIVandGetScore() {
            @Override
            public void getScore(int score) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        viewModel.updateIvandScore(score);
                    }
                });
            }

            @Override
            public void onError() {

            }
        });*/
    }

    @Override
    public void onPause() {
        super.onPause();

        viewModel.onPause();

    }

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


        if (requestCode == AttachFile.request_code_TAKE_PICTURE && resultCode == RESULT_OK) {// result for camera


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ImageHelper.correctRotateImage(AttachFile.mCurrentPhotoPath, true); //rotate image

                FragmentEditImage.insertItemList(AttachFile.mCurrentPhotoPath, false);
                new HelperFragment(FragmentEditImage.newInstance(null, false, false, 0)).setReplace(false).load();
            } else {
                ImageHelper.correctRotateImage(pathSaveImage, true); //rotate image

                FragmentEditImage.insertItemList(pathSaveImage, false);
                new HelperFragment(FragmentEditImage.newInstance(null, false, false, 0)).setReplace(false).load();
            }
        } else if (requestCode == request_code_image_from_gallery_single_select && resultCode == RESULT_OK) {// result for gallery
            if (data != null) {
                if (data.getData() == null) {
                    return;
                }
                ImageHelper.correctRotateImage(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), true);

                FragmentEditImage.insertItemList(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), false);
                new HelperFragment(FragmentEditImage.newInstance(null, false, false, 0)).setReplace(false).load();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Override onSaveInstanceState method and comment 'super' from avoid from "Can not perform this action after onSaveInstanceState" error
        //super.onSaveInstanceState(outState);
    }

    private void startDialog() {

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.choose_picture)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).items(R.array.profile).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                if (text.toString().equals(G.fragmentActivity.getResources().getString(R.string.array_From_Camera))) { // camera
                    try {
                        HelperPermission.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
                            @Override
                            public void Allow() {
                                dialog.dismiss();
                                useCamera();
                            }

                            @Override
                            public void deny() {

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                            @Override
                            public void Allow() {
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_en)), request_code_image_from_gallery_single_select);
                            }

                            @Override
                            public void deny() {

                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            }
        }).show();
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(FragmentSetting.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            if (G.fragmentActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                idAvatar = SUID.id().get();
                pathSaveImage = G.imageFile.toString() + "_" + System.currentTimeMillis() + "_" + idAvatar + ".jpg";
                File nameImageFile = new File(pathSaveImage);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uriIntent = Uri.fromFile(nameImageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                startActivityForResult(intent, AttachFile.request_code_TAKE_PICTURE);

            } else {
                Toast.makeText(G.fragmentActivity, G.fragmentActivity.getResources().getString(R.string.please_check_your_camera), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setAvatar() {
        avatarHandler.getAvatar(new ParamWithAvatarType(binding.userAvatar, viewModel.userId).avatarSize(R.dimen.dp100).avatarType(AvatarHandler.AvatarType.USER).showMain());
    }

    private void setImage(String path) {
        if (path != null) {
            G.imageLoader.displayImage(AndroidUtils.suitablePath(path), binding.userAvatar);
            if (G.onChangeUserPhotoListener != null) {
                G.onChangeUserPhotoListener.onChangePhoto(path);
            }
        }
    }

    /*private void showProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (binding.stPrgWaitingAddContact != null) {
                    binding.stPrgWaitingAddContact.setVisibility(View.VISIBLE);
                    G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }*/

    /*private void hideProgressBar() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (binding.stPrgWaitingAddContact != null) {
                    binding.stPrgWaitingAddContact.setVisibility(View.GONE);
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }*/

    private void showDialogLogout() {
        final MaterialDialog inDialog = new MaterialDialog.Builder(getActivity()).customView(R.layout.dialog_content_custom, true).build();
        View v = inDialog.getCustomView();

        inDialog.show();

        TextView txtTitle = v.findViewById(R.id.txtDialogTitle);
        txtTitle.setText(getString(R.string.log_out));

        TextView iconTitle = v.findViewById(R.id.iconDialogTitle);
        iconTitle.setText(R.string.md_exit_app);

        TextView txtContent = v.findViewById(R.id.txtDialogContent);
        txtContent.setText(R.string.content_log_out);

        TextView txtCancel = v.findViewById(R.id.txtDialogCancel);
        TextView txtOk = v.findViewById(R.id.txtDialogOk);

        txtOk.setOnClickListener(v1 -> {
            inDialog.dismiss();
            viewModel.logout();
        });

        txtCancel.setOnClickListener(v12 -> inDialog.dismiss());
    }

    private void showDeleteAccountDialog() {
        final MaterialDialog inDialog = new MaterialDialog.Builder(getContext()).customView(R.layout.dialog_content_custom, true).build();
        View v = inDialog.getCustomView();

        inDialog.show();

        TextView txtTitle = v.findViewById(R.id.txtDialogTitle);
        txtTitle.setText(getString(R.string.delete_account));

        TextView iconTitle = v.findViewById(R.id.iconDialogTitle);
        iconTitle.setText(R.string.md_delete_acc);

        TextView txtContent = v.findViewById(R.id.txtDialogContent);
        String text = getString(R.string.delete_account_text) + "\n" + getString(R.string.delete_account_text_desc);
        txtContent.setText(text);

        TextView txtCancel = v.findViewById(R.id.txtDialogCancel);
        TextView txtOk = v.findViewById(R.id.txtDialogOk);


        txtOk.setOnClickListener(v1 -> {
            inDialog.dismiss();
            FragmentDeleteAccount fragmentDeleteAccount = new FragmentDeleteAccount();

            Bundle bundle = new Bundle();
            bundle.putString("PHONE", viewModel.phoneNumber);
            fragmentDeleteAccount.setArguments(bundle);
            new HelperFragment(fragmentDeleteAccount).setReplace(false).load();
        });

        txtCancel.setOnClickListener(v12 -> inDialog.dismiss());
    }

    public interface DateType {

        void dataName(String type);
    }

   /* public interface onRemoveFragmentSetting {

        void removeFragment();

    }*/

    public interface onClickBack {

        void back();

    }

}
