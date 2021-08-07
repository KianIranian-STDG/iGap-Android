package net.iGap.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperError;
import net.iGap.module.AndroidUtils;
import net.iGap.module.Theme;
import net.iGap.adapter.AdapterDialog;
import net.iGap.databinding.FragmentEditProfileBinding;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.SoftKeyboard;
import net.iGap.viewmodel.UserProfileViewModel;

public class FragmentEditProfile extends BaseFragment {
    private UserProfileViewModel viewModel;
    private FragmentEditProfileBinding binding;

    public static FragmentEditProfile newInstance() {

        Bundle args = new Bundle();

        FragmentEditProfile fragment = new FragmentEditProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getParentFragment(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new UserProfileViewModel(avatarHandler);
            }
        }).get(UserProfileViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        /*viewModel.init();*/
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.showEditError.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                HelperError.showSnackMessage(getString(message), false);
            }
        });

        viewModel.getShowDialogSelectCountry().observe(getViewLifecycleOwner(), isShow -> {
            if (isShow != null && isShow) {
                showCountryDialog();
            }
        });
        viewModel.getCancelIconClick().observe(getViewLifecycleOwner(), isClick -> {
            if (viewModel.getUserInfo() != null) {
                String currentName = viewModel.getUserInfo().getUserInfo().getDisplayName() != null ? viewModel.getUserInfo().getUserInfo().getDisplayName() : "";
                String currentUserName = viewModel.getUserInfo().getUserInfo().getUsername() != null ? viewModel.getUserInfo().getUserInfo().getUsername() : "";
                String currentBio = viewModel.getUserInfo().getUserInfo().getBio() != null ? viewModel.getUserInfo().getUserInfo().getBio() : "";
                binding.nameEditText.setText(currentName);
                binding.userNameEditText.setText(currentUserName);
                binding.bioEditText.setText(currentBio);
            }
        });

        binding.userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.usernameTextChangeListener(binding.userNameEditText.getText().toString());

            }
        });

        binding.emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            viewModel.emailTextChangeListener(binding.emailEditText.getText().toString());
        });

        binding.countryCode.setOnFocusChangeListener((v, hasFocus) -> {
            viewModel.onCountryCodeClick();
        });

        binding.referralEditText.setOnFocusChangeListener((v, hasFocus) -> {
            viewModel.referralTextChangeListener(binding.referralEditText.getText().toString());
        });
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
            LinearLayout rootView = dialogChooseCountry.findViewById(R.id.country_root);
            rootView.setBackground(new Theme().tintDrawable(getResources().getDrawable(R.drawable.dialog_background), getContext(), R.attr.rootBackgroundColor));

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
    public void onResume() {
        super.onResume();
        AndroidUtils.requestAdjustResize(getActivity(), getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AndroidUtils.removeAdjustResize(getActivity(), getClass().getSimpleName());
    }
}
