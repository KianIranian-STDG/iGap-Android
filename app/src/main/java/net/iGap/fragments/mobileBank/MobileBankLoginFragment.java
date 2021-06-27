package net.iGap.fragments.mobileBank;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.MobileBankLoginFragmentBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.mobileBank.MobileBankLoginViewModel;
import net.iGap.module.SHP_SETTING;

import static android.content.Context.MODE_PRIVATE;

public class MobileBankLoginFragment extends BaseMobileBankFragment<MobileBankLoginViewModel> {

    private static final String PARSIAN_USERNAME = "parsian_username";
    private MobileBankLoginFragmentBinding binding;
    private boolean isOpenFloatble;

    public static MobileBankLoginFragment newInstance(boolean isOpenFloatble) {
        MobileBankLoginFragment fragment = new MobileBankLoginFragment();
        fragment.isOpenFloatble = isOpenFloatble;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_login_fragment, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
        binding.loadingView.getIndeterminateDrawable().setColorFilter(0XFFB6774E, android.graphics.PorterDuff.Mode.MULTIPLY);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankLoginViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        setUsernameFromCache();
        setupListeners();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupListeners() {

        binding.visiblePassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                binding.edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }else if (event.getAction() == MotionEvent.ACTION_UP){
                binding.edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            return true;
        });

        viewModel.getOnLoginResponse().observe(getViewLifecycleOwner(), state -> {
            if (getActivity() != null && state != null) {

                if (binding.edtUserName.getText() != null)
                    saveUsernameToPref(binding.edtUserName.getText().toString());

                if (isOpenFloatble) {
                    popBackStackFragment();
                    return;
                }

                new HelperFragment(getActivity().getSupportFragmentManager() , this).remove();
                new HelperFragment(getActivity().getSupportFragmentManager(), new MobileBankHomeFragment()).setReplace(false).load();
            }
        });

        viewModel.getShowErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && getContext() != null) {
                HelperError.showSnackMessage(getContext().getString(errorMessage), false);
            }
        });
    }

    private void setUsernameFromCache() {
        String username = getUsernameFromPrefIfExists();
        if (username != null) {
            binding.edtUserName.setText(username);
        }
    }

    private void saveUsernameToPref(String username) {
        if (getActivity() == null) return;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        sharedPreferences.edit().putString(PARSIAN_USERNAME , username).apply();

    }

    private String getUsernameFromPrefIfExists() {
        if (getActivity() == null) return null;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        return sharedPreferences.getString(PARSIAN_USERNAME , null);
    }

    private void setupToolbar() {

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setRoundBackground(false)
                .setLeftIcon(R.string.icon_back)
                .setLifecycleOwner(getViewLifecycleOwner())
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        binding.toolbar.addView(toolbar.getView());
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
