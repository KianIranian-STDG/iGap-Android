package net.iGap.fragments.kuknos;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosShowRecoveryBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.kuknos.KuknosShowRecoveryKeySVM;

public class KuknosShowRecoveryKeySFrag extends BaseFragment {

    private FragmentKuknosShowRecoveryBinding binding;
    private KuknosShowRecoveryKeySVM kuknosShowRecoveryKeyVM;

    public static KuknosShowRecoveryKeySFrag newInstance() {
        return new KuknosShowRecoveryKeySFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosShowRecoveryKeyVM = ViewModelProviders.of(this).get(KuknosShowRecoveryKeySVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_show_recovery, container, false);
        binding.setViewmodel(kuknosShowRecoveryKeyVM);
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

        LinearLayout toolbarLayout = binding.fragKuknosSRToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        binding.fragKuknosSRkeysET.setOnClickListener(v -> {
            copyWalletID(getResources().getString(R.string.kuknos_accountInfo_recoveryKey_title), kuknosShowRecoveryKeyVM.getRecoveryKeys().get());
        });

        binding.fragKuknosSRPrivatekeyET.setOnClickListener(v -> {
            copyWalletID(getResources().getString(R.string.kuknos_accountInfo_privateKey_title), kuknosShowRecoveryKeyVM.getPrivateKey().get());
        });

        binding.fragKuknosSRpublickeyET.setOnClickListener(v -> {
            copyWalletID(getResources().getString(R.string.kuknos_accountInfo_publicKey_title), kuknosShowRecoveryKeyVM.getPublicKey().get());
        });

        onErrorObserver();
//        onNextObserver();
    }

    private void copyWalletID(String label, String desc) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, desc);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), R.string.copied, Toast.LENGTH_SHORT).show();
    }

    private void onErrorObserver() {
        kuknosShowRecoveryKeyVM.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                if (errorM.getMessage().equals("1")) {
                    Snackbar snackbar = Snackbar.make(binding.fragKuknosSRContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG);
                    snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
                    snackbar.show();
                }
            }
        });
    }

    /*private void onNextObserver() {
        kuknosShowRecoveryKeyVM.getNextPage().observe(getViewLifecycleOwner(), nextPage -> {
            if (nextPage) {
                popBackStackFragment();
            }
        });
    }*/

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
