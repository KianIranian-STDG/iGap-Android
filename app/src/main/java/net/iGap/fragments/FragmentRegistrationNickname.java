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
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.AdapterDialog;
import net.iGap.databinding.FragmentRegistrationNicknameBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperGetDataFromOtherApp;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperTracker;
import net.iGap.helper.ImageHelper;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SoftKeyboard;
import net.iGap.viewmodel.FragmentRegistrationNicknameViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class FragmentRegistrationNickname extends BaseFragment {

    public final static String ARG_USER_ID = "arg_user_id";
    private FragmentRegistrationNicknameViewModel viewModel;
    private FragmentRegistrationNicknameBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration_nickname, container, false);
        viewModel = new FragmentRegistrationNicknameViewModel(getArguments() != null ? getArguments().getLong(ARG_USER_ID, -1) : -1, avatarHandler,getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE));
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProgressBar prgWait = binding.prg;
        AppUtils.setProgresColler(prgWait);

        AndroidUtils.setBackgroundShapeColor(binding.puProfileCircleImage, Color.parseColor(G.appBarColor));

        viewModel.progressValue.observe(this, integer -> {
            if (integer != null) {
                binding.prg.setProgress(integer);
            }
        });

        viewModel.showErrorName.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                binding.name.setErrorEnabled(true);
                binding.name.setError(G.fragmentActivity.getResources().getString(R.string.Toast_Write_NickName));
                binding.name.setHintTextAppearance(R.style.error_appearance);
            } else {
                binding.name.setErrorEnabled(false);
                binding.name.setError("");
            }
        });

        viewModel.showErrorLastName.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                binding.lastName.setErrorEnabled(true);
                binding.lastName.setError(G.fragmentActivity.getResources().getString(R.string.Toast_Write_NickName));
                binding.lastName.setHintTextAppearance(R.style.error_appearance);
            } else {
                binding.lastName.setErrorEnabled(false);
                binding.lastName.setError("");
            }
        });

        viewModel.showDialog.observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                startDialog();
            }
        });

        viewModel.showDialogSelectCountry.observe(this, isShow -> {
            if (isShow != null && isShow) {
                showCountryDialog();
            }
        });

        viewModel.hideKeyboard.observe(this, isHide -> {
            if (isHide != null) {
                if (isHide) {
                    hideKeyboard();
                } else {
                    openKeyBoard();
                }
            }
        });

        viewModel.showReagentPhoneNumberError.observe(this, isError -> {
            if (isError != null && isError) {
                Toast.makeText(getContext(), R.string.reagent_error_message, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.showReagentPhoneNumberStartWithZeroError.observe(this, showError -> {
            if (showError != null && showError) {
                Toast.makeText(getContext(), R.string.Toast_First_0, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.goToMain.observe(this, userId -> {
            HelperTracker.sendTracker(HelperTracker.TRACKER_REGISTRATION_NEW_USER);

            /*if (getActivity() != null && userId != null) {
                Intent intent = new Intent(getActivity(), ActivityMain.class);
                intent.putExtra(ARG_USER_ID, userId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                getActivity().finish();
            }*/

            if (getArguments() != null && getActivity() != null) {
                FragmentSyncRegisteredContacts fragment = new FragmentSyncRegisteredContacts();
                Bundle bundle = new Bundle();
                bundle.putLong(FragmentSyncRegisteredContacts.ARG_USER_ID, userId);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentRegistrationNickname.this).commitAllowingStateLoss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (FragmentEditImage.textImageList != null)
                FragmentEditImage.textImageList.clear();
            if (FragmentEditImage.itemGalleryList != null)
                FragmentEditImage.itemGalleryList.clear();

            switch (requestCode) {
                case AttachFile.request_code_TAKE_PICTURE:
                    String path;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        path = AttachFile.mCurrentPhotoPath;
                    } else {
                        path = AttachFile.imagePath;
                    }
                    ImageHelper.correctRotateImage(path, true); //rotate image

                    FragmentEditImage.insertItemList(path, false);
                    G.fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, FragmentEditImage.newInstance(path, false, true, 0))
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                    break;
                case AttachFile.request_code_image_from_gallery_single_select:
                    if (data != null) {
                        if (data.getData() == null) {
                            return;
                        }
                        FragmentEditImage.insertItemList(AttachFile.getFilePathFromUriAndCheckForAndroid7(data.getData(), HelperGetDataFromOtherApp.FileType.image), false);
                        G.fragmentActivity.getSupportFragmentManager().beginTransaction().
                                add(R.id.ar_layout_root, FragmentEditImage.newInstance(null, false, true, 0))
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                    }
                    break;
            }
        }
    }

    public void useCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                new AttachFile(G.fragmentActivity).dispatchTakePictureIntent(FragmentRegistrationNickname.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                new AttachFile(G.fragmentActivity).requestTakePicture(FragmentRegistrationNickname.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void useGallery() {
        try {
            HelperPermission.getStoragePermision(G.fragmentActivity, new OnGetPermission() {
                @Override
                public void Allow() {
                    try {
                        new AttachFile(G.fragmentActivity).requestOpenGalleryForImageSingleSelect(FragmentRegistrationNickname.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void deny() {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startDialog() {
        MaterialDialog.Builder imageDialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.choose_picture))
                .negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel))
                .items(R.array.profile)
                .itemsCallback((dialog, view, which, text) -> {
                    switch (which) {
                        case 0:
                            useGallery();
                            dialog.dismiss();
                            break;
                        case 1:
                            if (G.context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                                try {
                                    HelperPermission.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
                                        @Override
                                        public void Allow() {
                                            // this dialog show 2 way for choose image : gallery and camera
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

                                HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.please_check_your_camera), false);
                            }
                            break;
                    }
                });
        if (!(G.fragmentActivity).isFinishing()) {
            imageDialog.show();
        }
    }

    private void showCountryDialog() {
        Dialog dialogChooseCountry = new Dialog(G.fragmentActivity);
        dialogChooseCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChooseCountry.setContentView(R.layout.rg_dialog);
        dialogChooseCountry.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int setWidth = (int) (G.context.getResources().getDisplayMetrics().widthPixels * 0.9);
        int setHeight = (int) (G.context.getResources().getDisplayMetrics().heightPixels * 0.9);
        dialogChooseCountry.getWindow().setLayout(setWidth, setHeight);
        //
        final TextView txtTitle = dialogChooseCountry.findViewById(R.id.rg_txt_titleToolbar);
        SearchView edtSearchView = dialogChooseCountry.findViewById(R.id.rg_edtSearch_toolbar);

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

        final ListView listView = dialogChooseCountry.findViewById(R.id.lstContent);
        AdapterDialog adapterDialog = new AdapterDialog(G.fragmentActivity, viewModel.getStructCountryArrayList());
        listView.setAdapter(adapterDialog);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            viewModel.setCountry(adapterDialog.getItem(position));
            dialogChooseCountry.dismiss();
        });

        final ViewGroup root = dialogChooseCountry.findViewById(android.R.id.content);
        InputMethodManager im = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
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

        final View border = dialogChooseCountry.findViewById(R.id.rg_borderButton);
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

        /*AdapterDialog.mSelectedVariation = positionRadioButton;*/

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

        if (!(G.fragmentActivity).isFinishing()) {
            dialogChooseCountry.show();
        }
    }
}
