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
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityRegistration;
import net.iGap.adapter.AdapterDialog;
import net.iGap.databinding.FragmentRegistrationNicknameBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.PermissionHelper;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.CountryReader;
import net.iGap.module.SoftKeyboard;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.viewmodel.FragmentRegistrationNicknameViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static net.iGap.helper.HelperPermission.showDeniedPermissionMessage;

public class FragmentRegistrationNickname extends BaseFragment implements FragmentEditImage.OnImageEdited {

    private FragmentRegistrationNicknameViewModel viewModel;
    private FragmentRegistrationNicknameBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new FragmentRegistrationNicknameViewModel(
                        avatarHandler,
                        new CountryReader().readFromAssetsTextFile("country.txt", getContext())
                );
            }
        }).get(FragmentRegistrationNicknameViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration_nickname, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.name.setDefaultHintTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_title_text)));
        binding.nameEditText.setTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_title_text)));
        binding.lastNameEditText.setTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_title_text)));
        binding.reagentCountryCode.setTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_title_text)));
        binding.reagentPhoneNumber.setTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_title_text)));
        binding.lastName.setDefaultHintTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_title_text)));
        binding.reagentPhoneNumber.setHintTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_title_text)));
        AppUtils.setProgresColler(binding.prg);

        AndroidUtils.setBackgroundShapeColor(binding.puProfileCircleImage, Theme.getColor(Theme.key_theme_color));

        viewModel.progressValue.observe(getViewLifecycleOwner(), integer -> {
            if (integer != null) {
                binding.prg.setProgress(integer);
            }
        });

        viewModel.showDialog.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean != null && aBoolean) {
                startDialog();
            }
        });

        viewModel.showDialogSelectCountry.observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null && isShow) {
                showCountryDialog();
            }
        });

        viewModel.hideKeyboard.observe(getViewLifecycleOwner(), isHide -> {
            if (isHide != null) {
                if (isHide) {
                    hideKeyboard();
                } else {
                    openKeyBoard();
                }
            }
        });

//        viewModel.showReagentPhoneNumberError.observe(getViewLifecycleOwner(), isError -> {
//            if (isError != null && isError) {
//                Toast.makeText(getContext(), R.string.reagent_error_message, Toast.LENGTH_LONG).show();
//            }
//        });
//
//        viewModel.showReagentPhoneNumberStartWithZeroError.observe(getViewLifecycleOwner(), showError -> {
//            if (showError != null && showError) {
//                Toast.makeText(getContext(), R.string.Toast_First_0, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case AttachFile.request_code_TAKE_PICTURE:
                    String path;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        path = AttachFile.mCurrentPhotoPath;
                    } else {
                        path = AttachFile.imagePath;
                    }
                    ImageHelper.correctRotateImage(path, true); //rotate image
                    if (getActivity() instanceof ActivityRegistration) {
                        FragmentEditImage.checkItemGalleryList();
                        FragmentEditImage.insertItemList(path, false);
                        FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(path, false, true, 0);
                        fragmentEditImage.setOnProfileImageEdited(this);
                        ((ActivityRegistration) getActivity()).loadFragment(fragmentEditImage, true);
                    }
                    break;
                case AttachFile.request_code_image_from_gallery_single_select:
                    if (data != null) {
                        if (data.getData() == null) {
                            return;
                        }
                        if (getActivity() instanceof ActivityRegistration) {
                            FragmentEditImage.checkItemGalleryList();
                            FragmentEditImage.insertItemList(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), false);
                            FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, false, true, 0);
                            fragmentEditImage.setOnProfileImageEdited(this);
                            ((ActivityRegistration) getActivity()).loadFragment(fragmentEditImage, true);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionHelper.StoragePermissionRequestCode) {
            boolean t = true;
            for (int grantResult : grantResults) {
                t = t && grantResult == PackageManager.PERMISSION_GRANTED;
            }
            if (t) {
                openGallery();
            }
        }
    }

    public void useCamera() {
        if (getActivity() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                new AttachFile(getActivity()).dispatchTakePictureIntent(FragmentRegistrationNickname.this);
            } else {
                try {
                    new AttachFile(getActivity()).requestTakePicture(FragmentRegistrationNickname.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void useGallery() {
        if (getActivity() != null) {
            if (new PermissionHelper(getActivity()).grantReadAndRightStoragePermission()) {
                openGallery();
            }
        }
    }

    private void openGallery() {
        try {
            new AttachFile(getActivity()).requestOpenGalleryForImageSingleSelect(FragmentRegistrationNickname.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startDialog() {
        if (getActivity() != null) {
            MaterialDialog.Builder imageDialog = new MaterialDialog.Builder(getActivity()).title(R.string.choose_picture)
                    .negativeText(R.string.B_cancel)
                    .items(R.array.profile)
                    .negativeColor(Theme.getColor(Theme.key_button_background))
                    .positiveColor(Theme.getColor(Theme.key_button_background))
                    .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                    .itemsCallback((dialog, view, which, text) -> {
                        switch (which) {
                            case 0:
                                useGallery();
                                dialog.dismiss();
                                break;
                            case 1:
                                if (FragmentRegistrationNickname.this.isAdded() && getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                                    try {
                                        HelperPermission.getCameraPermission(getActivity(), new OnGetPermission() {
                                            @Override
                                            public void Allow() {
                                                // this dialog show 2 way for choose image : gallery and camera
                                                dialog.dismiss();
                                                useCamera();
                                            }

                                            @Override
                                            public void deny() {
                                                showDeniedPermissionMessage(G.context.getString(R.string.permission_camera));
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    HelperError.showSnackMessage(getString(R.string.please_check_your_camera), false);
                                }
                                break;
                        }
                    });
            imageDialog.show();
        }
    }

    private void showCountryDialog() {
        if (getActivity() != null) {
            Dialog dialogChooseCountry = new Dialog(getActivity());
            dialogChooseCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogChooseCountry.setContentView(R.layout.rg_dialog);
            dialogChooseCountry.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            int setWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            int setHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);
            dialogChooseCountry.getWindow().setLayout(setWidth, setHeight);
            //
            final TextView txtTitle = dialogChooseCountry.findViewById(R.id.rg_txt_titleToolbar);
            SearchView edtSearchView = dialogChooseCountry.findViewById(R.id.rg_edtSearch_toolbar);
            txtTitle.setTextColor(Theme.getColor(Theme.key_icon));
            txtTitle.setOnClickListener(view -> {
                edtSearchView.setIconified(false);
                edtSearchView.setIconifiedByDefault(true);
                txtTitle.setVisibility(View.GONE);
            });

            // close SearchView and show title again
            edtSearchView.setOnCloseListener(() -> {
                txtTitle.setVisibility(View.VISIBLE);
                return false;
            });

            ListView listView = dialogChooseCountry.findViewById(R.id.lstContent);
            AdapterDialog adapterDialog = new AdapterDialog(viewModel.getStructCountryArrayList());
            listView.setAdapter(adapterDialog);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                viewModel.setCountry(adapterDialog.getItem(position));
                dialogChooseCountry.dismiss();
            });

            ViewGroup root = dialogChooseCountry.findViewById(android.R.id.content);
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            SoftKeyboard softKeyboard = new SoftKeyboard(root, im);
            softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
                @Override
                public void onSoftKeyboardHide() {
                    G.handler.post(() -> {
                        if (edtSearchView.getQuery().toString().length() > 0) {
                            edtSearchView.setIconified(false);
                            edtSearchView.clearFocus();
                            txtTitle.setVisibility(View.GONE);
                        } else {
                            edtSearchView.setIconified(true);
                            txtTitle.setVisibility(View.VISIBLE);
                        }
                        adapterDialog.notifyDataSetChanged();
                    });
                }

                @Override
                public void onSoftKeyboardShow() {
                    G.handler.post(() -> txtTitle.setVisibility(View.GONE));
                }
            });

            View border = dialogChooseCountry.findViewById(R.id.rg_borderButton);
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                    if (i > 0) {
                        border.setVisibility(View.VISIBLE);
                    } else {
                        border.setVisibility(View.GONE);
                    }
                }
            });

            adapterDialog.notifyDataSetChanged();

            edtSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    adapterDialog.getFilter().filter(s);
                    return false;
                }
            });

            dialogChooseCountry.findViewById(R.id.rg_txt_okDialog).setOnClickListener(v -> dialogChooseCountry.dismiss());
            dialogChooseCountry.show();
        }
    }

    @Override
    public void profileImageAdd(String path) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack(FragmentRegistrationNickname.class.getName(), 0);
        }
        viewModel.uploadAvatar(path);
    }
}
