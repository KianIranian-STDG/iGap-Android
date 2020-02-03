package net.iGap.kuknos.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosEnterPinBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.viewmodel.KuknosEnterPinVM;

public class KuknosEnterPinFrag extends BaseFragment {

    private FragmentKuknosEnterPinBinding binding;
    private KuknosEnterPinVM kuknosViewRecoveryEPVM;
    private OnPinEntered pinEntered;

    public static KuknosEnterPinFrag newInstance(OnPinEntered pinEntered) {
        return new KuknosEnterPinFrag(pinEntered);
    }

    public KuknosEnterPinFrag(OnPinEntered pinEntered) {
        this.pinEntered = pinEntered;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosViewRecoveryEPVM = ViewModelProviders.of(this).get(KuknosEnterPinVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_enter_pin, container, false);
        binding.setViewmodel(kuknosViewRecoveryEPVM);
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


    private void onError() {
        kuknosViewRecoveryEPVM.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState() && errorM.getMessage().equals("0")) {
                binding.fragKuknosVRPassHolder.setError(getResources().getString(errorM.getResID()));
                binding.fragKuknosVRPassHolder.requestFocus();
            } else if (errorM.getState() && errorM.getMessage().equals("1")) {
                showDialog(errorM.getResID());
            }
        });
    }

    private void showDialog(int messageResource) {
        DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
        defaultRoundDialog.setTitle(getResources().getString(R.string.kuknos_viewRecoveryEP_failTitle));
        defaultRoundDialog.setMessage(getResources().getString(messageResource));
        defaultRoundDialog.setPositiveButton(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack), (dialog, id) -> {

        });
        defaultRoundDialog.show();
    }

    private void onProgress() {
        kuknosViewRecoveryEPVM.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
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
        kuknosViewRecoveryEPVM.getNextPage().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                popBackStackFragment();
                pinEntered.correctPin();
            }
        });
    }

    public interface OnPinEntered {
        void correctPin();
    }

}
