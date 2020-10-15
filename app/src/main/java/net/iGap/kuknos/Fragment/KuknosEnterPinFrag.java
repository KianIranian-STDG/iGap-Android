package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentKuknosEnterPinBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.kuknos.viewmodel.KuknosEnterPinVM;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

public class KuknosEnterPinFrag extends BaseAPIViewFrag<KuknosEnterPinVM> {

    private FragmentKuknosEnterPinBinding binding;
    private OnPinEntered pinEntered;
    private boolean isLogin;

    public static KuknosEnterPinFrag newInstance(OnPinEntered pinEntered, boolean isLogin) {
        return new KuknosEnterPinFrag(pinEntered, isLogin);
    }

    public KuknosEnterPinFrag(OnPinEntered pinEntered, boolean isLogin) {
        this.pinEntered = pinEntered;
        this.isLogin = isLogin;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosEnterPinVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_enter_pin, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (isLogin)
                            ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                        else
                            popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosVRToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        onNextPage();
        onError();
        onProgress();
        entryListener();
    }

    @Override
    public boolean onBackPressed() {
        if (isLogin) {
            ((ActivityMain) getActivity()).removeAllFragmentFromMain();
            return true;
        } else
            return super.onBackPressed();
    }

    private void onError() {
        viewModel.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState() && errorM.getMessage().equals("0")) {
                binding.fragKuknosVRPassHolder.setError(getResources().getString(errorM.getResID()));
                binding.fragKuknosVRPassHolder.requestFocus();
            } else if (errorM.getState() && errorM.getMessage().equals("1")) {
                showDialog(errorM.getResID());
            }
        });
    }

    private void showDialog(int messageResource) {
        new MaterialDialog.Builder(getContext())
                .title(getResources().getString(R.string.kuknos_viewRecoveryEP_failTitle))
                .positiveText(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack))
                .content(getResources().getString(messageResource))
                .show();
    }

    private void onProgress() {
        viewModel.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.fragKuknosVRProgressV.setVisibility(View.VISIBLE);
                binding.fragKuknosVRPass.setEnabled(false);
                binding.fragKuknosVRSubmit.setText(getResources().getText(R.string.kuknos_enterPin_btnProgress));
            } else {
                binding.fragKuknosVRProgressV.setVisibility(View.GONE);
                binding.fragKuknosVRPass.setEnabled(true);
                binding.fragKuknosVRSubmit.setText(getResources().getText(R.string.kuknos_enterPin_btnNormal));
            }
        });
    }

    private void entryListener() {
        binding.fragKuknosVRPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosVRPassHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void onNextPage() {
        viewModel.getNextPage().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                if (!isLogin)
                    popBackStackFragment();
                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pinEntered.correctPin();
                    }
                }, 500);
            }
        });
    }

    public interface OnPinEntered {
        void correctPin();
    }

}
