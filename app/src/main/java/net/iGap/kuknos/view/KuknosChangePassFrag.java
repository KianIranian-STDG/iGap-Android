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
import net.iGap.databinding.FragmentKuknosChangePassBinding;
import net.iGap.module.dialog.DefaultRoundDialog;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.kuknos.viewmodel.KuknosChangePassVM;

public class KuknosChangePassFrag extends BaseFragment {

    private FragmentKuknosChangePassBinding binding;
    private KuknosChangePassVM kuknosChangePassVM;

    public static KuknosChangePassFrag newInstance() {
        return new KuknosChangePassFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosChangePassVM = ViewModelProviders.of(this).get(KuknosChangePassVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_change_pass, container, false);
        binding.setViewmodel(kuknosChangePassVM);
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

        LinearLayout toolbarLayout = binding.fragKuknosCPToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        onError();
        onProgress();
        entryListener();
    }

    private void onError() {
        kuknosChangePassVM.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState() && errorM.getMessage().equals("0")) {
                binding.fragKuknosCPOldPHolder.setError(getResources().getString(errorM.getResID()));
                binding.fragKuknosCPOldPHolder.requestFocus();
            } else if (errorM.getState() && errorM.getMessage().equals("1")) {
                binding.fragKuknosCPNewPHolder.setError(getResources().getString(errorM.getResID()));
                binding.fragKuknosCPNewPHolder.requestFocus();
            } else if (errorM.getState() && errorM.getMessage().equals("2")) {
                binding.fragKuknosCPENewPHolder.setError(getResources().getString(errorM.getResID()));
                binding.fragKuknosCPENewPHolder.requestFocus();
            } else if (errorM.getMessage().equals("3")) {
                showDialog(errorM.getState(), errorM.getResID());
            }
        });
    }

    private void showDialog(boolean state, int messageResource) {
        DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
        if (!state)
            defaultRoundDialog.setTitle(getResources().getString(R.string.kuknos_changePIN_successTitle));
        else
            defaultRoundDialog.setTitle(getResources().getString(R.string.kuknos_changePIN_failTitle));
        defaultRoundDialog.setMessage(getResources().getString(messageResource));
        defaultRoundDialog.setPositiveButton(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack), (dialog, id) -> {
            if (!state)
                popBackStackFragment();
        });
        defaultRoundDialog.show();
    }

    private void onProgress() {
        kuknosChangePassVM.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.fragKuknosCPProgressV.setVisibility(View.VISIBLE);
                binding.fragKuknosCPNewP.setEnabled(false);
                binding.fragKuknosCPENewP.setEnabled(false);
                binding.fragKuknosCPOldP.setEnabled(false);
                binding.fragKuknosCPSubmit.setText(getResources().getText(R.string.kuknos_changePIN_load));
            } else {
                binding.fragKuknosCPProgressV.setVisibility(View.GONE);
                binding.fragKuknosCPNewP.setEnabled(true);
                binding.fragKuknosCPENewP.setEnabled(true);
                binding.fragKuknosCPOldP.setEnabled(true);
                binding.fragKuknosCPSubmit.setText(getResources().getText(R.string.kuknos_changePIN_submit));
            }
        });
    }

    private void entryListener() {
        binding.fragKuknosCPOldP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosCPOldPHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.fragKuknosCPNewP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosCPNewPHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.fragKuknosCPENewP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosCPENewPHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
