package net.iGap.fragments.kuknos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentKuknosRestoreBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.kuknos.KuknosRestoreVM;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class KuknosRestoreFrag extends BaseAPIViewFrag<KuknosRestoreVM> {

    private FragmentKuknosRestoreBinding binding;

    public static KuknosRestoreFrag newInstance(boolean isSeedMode) {
        KuknosRestoreFrag frag = new KuknosRestoreFrag();
        Bundle data = new Bundle();
        data.putBoolean("mode", isSeedMode);
        frag.setArguments(data);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosRestoreVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_restore, container, false);
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
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosRToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());
        binding.fragKuknosIdPINCheck.setChecked(false);

        if (getArguments().getBoolean("mode")) {
            viewModel.setSeedMode(true);
            binding.fragKuknosRMessage.setText(getResources().getString(R.string.kuknos_RestoreSeed_Title));
            binding.fragKuknosRDescription.setText(getResources().getString(R.string.kuknos_RestoreSeed_Message));
            binding.fragKuknosRkeysET.setHint(getResources().getString(R.string.kuknos_RestoreSeed_Hint));
        }

        onErrorObserver();
        onNextObserver();
        progressState();
        onPINCheck();
    }

    private void onPINCheck() {
        viewModel.getPinCheck().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                binding.fragKuknosIdSubmit.setText(getResources().getString(R.string.kuknos_Restore_checkBtn));
            else
                binding.fragKuknosIdSubmit.setText(getResources().getString(R.string.kuknos_Restore_Btn));
        });
    }

    private void onErrorObserver() {
        viewModel.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                if (errorM.getMessage().equals("0")) {
                    binding.fragKuknosRkeysET.setError("" + getString(errorM.getResID()));
                    binding.fragKuknosRkeysET.requestFocus();
                } else if (errorM.getMessage().equals("1")) {
                    Snackbar snackbar = Snackbar.make(binding.fragKuknosRContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG);
                    snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
                    snackbar.show();
                }
            }
        });
    }

    private void onNextObserver() {
        viewModel.getNextPage().observe(getViewLifecycleOwner(), nextPageMode -> {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = null;
//            if (nextPage == 1) {
            fragment = fragmentManager.findFragmentByTag(KuknosSetPassFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosSetPassFrag.newInstance(nextPageMode);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                /*Bundle bundle = new Bundle();
                bundle.putString("key_phrase", viewModel.getKeys().get());
                fragment.setArguments(bundle);*/
            /*} else if (nextPage == 2) {
                saveRegisterInfo();
                fragment = fragmentManager.findFragmentByTag(KuknosPanelFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosPanelFrag.newInstance();
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
            } else if (nextPage == 3) {
                fragment = fragmentManager.findFragmentByTag(KuknosSignupInfoFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosSignupInfoFrag.newInstance();
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
            }*/
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        });
    }

    private void progressState() {
        viewModel.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.fragKuknosIdSubmit.setText(getString(R.string.kuknos_login_progress_str));
                binding.fragKuknosIdSubmit.setEnabled(false);
                binding.fragKuknosRkeysET.setEnabled(false);
                binding.fragKuknosRProgressV.setVisibility(View.VISIBLE);
            } else {
                binding.fragKuknosIdSubmit.setText(getString(R.string.kuknos_Restore_Btn));
                binding.fragKuknosIdSubmit.setEnabled(true);
                binding.fragKuknosRkeysET.setEnabled(true);
                binding.fragKuknosRProgressV.setVisibility(View.GONE);
            }
        });
    }
}
