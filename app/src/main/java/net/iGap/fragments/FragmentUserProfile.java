package net.iGap.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentUserProfileBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.StatusBarUtil;
import net.iGap.viewmodel.UserProfileViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static net.iGap.module.AttachFile.request_code_image_from_gallery_single_select;

public class FragmentUserProfile extends BaseMainFragments implements FragmentEditImage.OnImageEdited {

    private FragmentUserProfileBinding binding;
    private UserProfileViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserProfileViewModel(getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE), avatarHandler);
            }
        }).get(UserProfileViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_profile, container, false);
        viewModel.init();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtil.setColor(getActivity(), new Theme().getPrimaryDarkColor(getContext()), 50);
        }

        viewModel.setCurrentFragment.observe(getViewLifecycleOwner(), isEdit -> {
            if (isEdit != null) {
                Log.wtf(this.getClass().getName(), "setCurrentFragment, isEditMode: " + isEdit);
                if (isEdit) {
                    FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                    Fragment fragment = getChildFragmentManager().findFragmentByTag(FragmentEditProfile.class.getName());
                    if (fragment == null) {
                        fragment = FragmentEditProfile.newInstance();
                        fragmentTransaction.addToBackStack(FragmentEditProfile.class.getName());
                    }
                    fragmentTransaction.replace(R.id.frame_edit, fragment, FragmentEditProfile.class.getName()).commit();
                    binding.addAvatar.setVisibility(View.VISIBLE);
                } else {
                    FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                    Fragment fragment = getChildFragmentManager().findFragmentByTag(FragmentProfile.class.getName());
                    if (fragment == null) {
                        fragment = FragmentProfile.newInstance();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    fragmentTransaction.replace(R.id.frame_edit, fragment, fragment.getClass().getName()).commit();
                    binding.addAvatar.setVisibility(View.GONE);
                }
            }
        });

        viewModel.changeUserProfileWallpaper.observe(getViewLifecycleOwner(), drawable -> {
            if (drawable != null) {
                binding.fupBgAvatar.setImageDrawable(drawable);
            } else {
                binding.fupBgAvatar.setImageResource(R.drawable.test_bg);
            }
        });

        viewModel.goToShowAvatarPage.observe(getViewLifecycleOwner(), userId -> {
            if (getActivity() != null && userId != null) {
                FragmentShowAvatars fragment = FragmentShowAvatars.newInstance(userId, FragmentShowAvatars.From.setting);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });

        viewModel.setUserAvatar.observe(getViewLifecycleOwner(), userId -> {
            if (userId != null) {
                avatarHandler.getAvatar(new ParamWithAvatarType(binding.fupUserImage, userId).avatarType(AvatarHandler.AvatarType.USER).showMain());
            }
        });

        viewModel.deleteAvatar.observe(getViewLifecycleOwner(), deleteAvatarModel -> {
            if (deleteAvatarModel != null) {
                avatarHandler.avatarDelete(new ParamWithAvatarType(binding.fupUserImage, deleteAvatarModel.getUserId())
                        .avatarType(AvatarHandler.AvatarType.USER), deleteAvatarModel.getAvatarId());
            }
        });

        viewModel.setUserAvatarPath.observe(getViewLifecycleOwner(), changeImageModel -> {
            if (changeImageModel != null) {
                if (changeImageModel.getImagePath() == null || !new File(changeImageModel.getImagePath()).exists()) {
                    binding.fupUserImage.setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) binding.fupUserImage.getContext().getResources().getDimension(R.dimen.dp100), changeImageModel.getInitials(), changeImageModel.getColor()));
                } else {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(changeImageModel.getImagePath()), binding.fupUserImage);
                }
            }
        });

        viewModel.showDialogChooseImage.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                startDialog();
            }
        });

        viewModel.showError.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getUpdateTwoPaneView().observe(getViewLifecycleOwner(), isUpdate -> {
            if (getActivity() != null && isUpdate != null && isUpdate) {
                Fragment frg;
                frg = getActivity().getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.detach(frg);
                ft.attach(frg);
                ft.commit();
            }
        });

        viewModel.getPopBackStack().observe(getViewLifecycleOwner(), isPopBackStack -> {
            if (isPopBackStack != null && isPopBackStack) {
                getChildFragmentManager().popBackStack();
            }
        });

        getChildFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.wtf(this.getClass().getName(), "-------------------------------------------");
                for (int i = 0; i < getChildFragmentManager().getBackStackEntryCount(); i++) {
                    Log.wtf(this.getClass().getName(), "fragment: " + getChildFragmentManager().getBackStackEntryAt(i).getName());
                }
                Log.wtf(this.getClass().getName(), "-------------------------------------------");
            }
        });

        Log.wtf(this.getClass().getName(), "onViewCreated");

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
            } else {
                ImageHelper.correctRotateImage(viewModel.pathSaveImage, true); //rotate image
                FragmentEditImage.insertItemList(viewModel.pathSaveImage, false);
            }
            if (getActivity() != null) {
                FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, false, false, 0);
                fragmentEditImage.setOnProfileImageEdited(this);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
            }
        }
    }

    private void startDialog() {
        new MaterialDialog.Builder(getActivity()).title(R.string.choose_picture).negativeText(R.string.B_cancel).items(R.array.profile).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(final MaterialDialog dialog, View view, int which, CharSequence text) {
                if (text.toString().equals(getString(R.string.array_From_Camera))) { // camera
                    try {
                        HelperPermission.getCameraPermission(getActivity(), new OnGetPermission() {
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
                        HelperPermission.getStoragePermision(getActivity(), new OnGetPermission() {
                            @Override
                            public void Allow() {
                                if (getActivity() == null) return;
                                Fragment fragment = FragmentGallery.newInstance(FragmentGallery.GalleryMode.PHOTO , true,getString(R.string.gallery) ,"-1" , new FragmentGallery.GalleryFragmentListener() {
                                    @Override
                                    public void openOsGallery() {
                                    }

                                    @Override
                                    public void onGalleryResult(String path) {
                                        popBackStackFragment();
                                        handleGalleryImageResult(path);
                                    }
                                });
                                new HelperFragment(getActivity().getSupportFragmentManager() , fragment).load();
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

    private void handleGalleryImageResult(String path) {
        if (getActivity() != null) {
            ImageHelper.correctRotateImage(path, true);
            FragmentEditImage.insertItemList(path, false);
            FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, false, false, 0);
            fragmentEditImage.setOnProfileImageEdited(this);
            new HelperFragment(getActivity().getSupportFragmentManager(), fragmentEditImage).setReplace(false).load();
        }
    }

    private void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(FragmentUserProfile.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (getActivity() != null && getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(viewModel.getImageFile()));
                startActivityForResult(intent, AttachFile.request_code_TAKE_PICTURE);

            } else {
                Toast.makeText(getContext(), R.string.please_check_your_camera, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean isAllowToBackPressed() {
        return viewModel.checkEditModeForOnBackPressed();
    }

    @Override
    public void scrollToTopOfList() {
        //no thing -> its for scroll list if available
    }

    @Override
    public void profileImageAdd(String path) {
        viewModel.uploadAvatar(path);
    }
}
