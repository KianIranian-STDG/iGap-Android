package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosViewRecoveryEpBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.kuknos.viewmodel.KuknosViewRecoveryEPVM;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class KuknosViewRecoveryEPFrag extends BaseFragment {

    private FragmentKuknosViewRecoveryEpBinding binding;
    private KuknosViewRecoveryEPVM kuknosViewRecoveryEPVM;

    public static KuknosViewRecoveryEPFrag newInstance() {
        return new KuknosViewRecoveryEPFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosViewRecoveryEPVM = ViewModelProviders.of(this).get(KuknosViewRecoveryEPVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_view_recovery_ep, container, false);
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
                .setLeftIcon(R.string.icon_back)
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
        new MaterialDialog.Builder(getContext())
                .title(getResources().getString(R.string.kuknos_viewRecoveryEP_failTitle))
                .positiveText(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack))
                .content(getResources().getString(messageResource))
                .show();
    }

    private void onProgress() {
        kuknosViewRecoveryEPVM.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.fragKuknosVRProgressV.setVisibility(View.VISIBLE);
                binding.fragKuknosVRPass.setEnabled(false);
                binding.fragKuknosVRSubmit.setText(getResources().getText(R.string.kuknos_viewRecoveryEP_load));
            } else {
                binding.fragKuknosVRProgressV.setVisibility(View.GONE);
                binding.fragKuknosVRPass.setEnabled(true);
                binding.fragKuknosVRSubmit.setText(getResources().getText(R.string.kuknos_viewRecoveryEP_btn));
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
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(KuknosShowRecoveryKeySFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosShowRecoveryKeySFrag.newInstance();
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });
    }

}
