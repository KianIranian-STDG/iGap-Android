package net.iGap.kuknos.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosChangePassBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.viewmodel.KuknosChangePassVM;
import net.iGap.libs.bottomNavigation.Util.Utils;

public class KuknosChangePassFrag extends BaseFragment {

    private FragmentKuknosChangePassBinding binding;
    private KuknosChangePassVM kuknosChangePassVM;
    private HelperToolbar mHelperToolbar;

    public static KuknosChangePassFrag newInstance() {
        KuknosChangePassFrag kuknosLoginFrag = new KuknosChangePassFrag();
        return kuknosLoginFrag;
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

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosCPToolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        onError();
        onProgress();
        entryListener();
    }

    private void onError() {
        kuknosChangePassVM.getError().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true && errorM.getMessage().equals("0")) {
                    binding.fragKuknosCPOldPHolder.setError(getResources().getString(errorM.getResID()));
                    binding.fragKuknosCPOldPHolder.requestFocus();
                }
                else if (errorM.getState() == true && errorM.getMessage().equals("1")) {
                    binding.fragKuknosCPNewPHolder.setError(getResources().getString(errorM.getResID()));
                    binding.fragKuknosCPNewPHolder.requestFocus();
                }
                else if (errorM.getState() == true && errorM.getMessage().equals("2")) {
                    binding.fragKuknosCPENewPHolder.setError(getResources().getString(errorM.getResID()));
                    binding.fragKuknosCPENewPHolder.requestFocus();
                }
                else if (errorM.getMessage().equals("3")) {
                    showDialog(errorM.getState(), errorM.getResID());
                }
            }
        });
    }

    private void showDialog(boolean state, int messageResource) {
        DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
        if (state == false)
            defaultRoundDialog.setTitle(getResources().getString(R.string.kuknos_changePIN_successTitle));
        else
            defaultRoundDialog.setTitle(getResources().getString(R.string.kuknos_changePIN_failTitle));
        defaultRoundDialog.setMessage(getResources().getString(messageResource));
        defaultRoundDialog.setPositiveButton(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (state == false)
                    popBackStackFragment();
            }
        });
        defaultRoundDialog.show();
    }

    private void onProgress() {
        kuknosChangePassVM.getProgressState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == true) {
                    binding.fragKuknosCPProgressV.setVisibility(View.VISIBLE);
                    binding.fragKuknosCPNewP.setEnabled(false);
                    binding.fragKuknosCPENewP.setEnabled(false);
                    binding.fragKuknosCPOldP.setEnabled(false);
                    binding.fragKuknosCPSubmit.setText(getResources().getText(R.string.kuknos_changePIN_load));
                }
                else {
                    binding.fragKuknosCPProgressV.setVisibility(View.GONE);
                    binding.fragKuknosCPNewP.setEnabled(true);
                    binding.fragKuknosCPENewP.setEnabled(true);
                    binding.fragKuknosCPOldP.setEnabled(true);
                    binding.fragKuknosCPSubmit.setText(getResources().getText(R.string.kuknos_changePIN_submit));
                }
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
