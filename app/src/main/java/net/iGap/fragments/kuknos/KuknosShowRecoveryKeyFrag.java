package net.iGap.fragments.kuknos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentKuknosRecoveryKeyBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.kuknos.KuknosShowRecoveryKeyVM;

import java.util.Arrays;

public class KuknosShowRecoveryKeyFrag extends BaseFragment {

    private FragmentKuknosRecoveryKeyBinding binding;
    private KuknosShowRecoveryKeyVM kuknosShowRecoveryKeyVM;

    public static KuknosShowRecoveryKeyFrag newInstance() {
        return new KuknosShowRecoveryKeyFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosShowRecoveryKeyVM = ViewModelProviders.of(this).get(KuknosShowRecoveryKeyVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_recovery_key, container, false);
        binding.setViewmodel(kuknosShowRecoveryKeyVM);
        binding.setLifecycleOwner(this);
        isNeedResume = true;
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        binding.spinnerLanguage.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_item_custom, Arrays.asList(getResources().getString(R.string.kuknos_recoveryKey_en), getResources().getString(R.string.kuknos_recoveryKey_fa))));
        binding.spinnerLength.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_item_custom, Arrays.asList(CompatibleUnicode("12"), CompatibleUnicode("24"))));

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

        LinearLayout toolbarLayout = binding.fragKuknosRKSToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        onErrorObserver();
        onNextObserver();
        progressState();
    }

    private String CompatibleUnicode(String entry) {
        return HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(entry)) : entry;
    }

    private void onErrorObserver() {
        kuknosShowRecoveryKeyVM.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                /*Snackbar snackbar = Snackbar.make(binding.fragKuknosRKSContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG);
                snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
                snackbar.show();
                binding.fragKuknosIdSubmit.setEnabled(false);*/
                showDialog(errorM.getResID());
            }
        });
    }

    private void showDialog(int messageResource) {
        new MaterialDialog.Builder(getContext())
                .title(R.string.kuknos_viewRecoveryEP_failTitle)
                .positiveText(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack))
                .content(getResources().getString(messageResource))
                .onPositive((dialog, which) -> ((ActivityMain) getActivity()).removeAllFragmentFromMain()).show();
    }

    private void onNextObserver() {
        kuknosShowRecoveryKeyVM.getNextPage().observe(getViewLifecycleOwner(), nextPage -> {
            if (nextPage) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment;
                fragment = fragmentManager.findFragmentByTag(KuknosSetPassFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosSetPassFrag.newInstance(0);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });
    }

    private void progressState() {
        kuknosShowRecoveryKeyVM.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.fragKuknosIdSubmit.setText(getString(R.string.kuknos_login_progress_str));
                binding.fragKuknosIdSubmit.setEnabled(false);
                binding.fragKuknosRKSProgressV.setVisibility(View.VISIBLE);
            } else {
                binding.fragKuknosIdSubmit.setText(getString(R.string.kuknos_RecoverySK_Btn));
                binding.fragKuknosIdSubmit.setEnabled(true);
                binding.fragKuknosRKSProgressV.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // disable screenshot.
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

}
