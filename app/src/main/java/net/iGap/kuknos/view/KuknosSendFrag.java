package net.iGap.kuknos.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentKuknosSendBinding;
import net.iGap.module.dialog.DefaultRoundDialog;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.viewmodel.KuknosSendVM;

import java.io.IOException;

public class KuknosSendFrag extends BaseFragment {

    private FragmentKuknosSendBinding binding;
    private KuknosSendVM kuknosSignupInfoVM;

    public static KuknosSendFrag newInstance() {
        return new KuknosSendFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosSignupInfoVM = ViewModelProviders.of(this).get(KuknosSendVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_send, container, false);
        binding.setViewmodel(kuknosSignupInfoVM);
        binding.setLifecycleOwner(this);

        Bundle b = getArguments();
        kuknosSignupInfoVM.setBalanceInfoM(b.getString("balanceClientInfo"));

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

        LinearLayout toolbarLayout = binding.fragKuknosSToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        binding.fragKuknosSWalletAddressET.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        binding.fragKuknosSWalletAddressET.setRawInputType(InputType.TYPE_CLASS_TEXT);

        onError();
        onTransfer();
        progressSubmitVisibility();
        resetEdittextAfterError();
        openQrScanner();
        goToPin();
    }

    private void openQrScanner() {
        kuknosSignupInfoVM.getOpenQrScanner().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                try {
                    HelperPermission.getCameraPermission(getActivity(), new OnGetPermission() {
                        @Override
                        public void Allow() throws IllegalStateException {
                            IntentIntegrator integrator = new IntentIntegrator(getActivity());
                            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                            integrator.setRequestCode(ActivityMain.kuknosRequestCodeQrCode);
                            integrator.setBeepEnabled(false);
                            integrator.setPrompt("");
                            integrator.initiateScan();
                        }

                        @Override
                        public void deny() {
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setWalletIDQrCode(String code) {
        kuknosSignupInfoVM.getWalletID().set(code);
    }

    private void onError() {

        kuknosSignupInfoVM.getErrorM().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                switch (errorM.getMessage()) {
                    case "0":
                        //Wallet ID Problem
                        binding.fragKuknosSWalletAddressHolder.setError("" + getString(errorM.getResID()));
                        binding.fragKuknosSWalletAddressHolder.requestFocus();
                        break;
                    case "1":
                        //Amount enough
                        binding.fragKuknosSAmountHolder.setError("" + getString(errorM.getResID()));
                        binding.fragKuknosSAmountHolder.requestFocus();
                        break;
                    case "2":
                        //server related errors

                        break;
                }
            }
        });
    }

    private void onTransfer() {
        kuknosSignupInfoVM.getPayResult().observe(getViewLifecycleOwner(), errorM -> {
            DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
            defaultRoundDialog.setTitle(getResources().getString(R.string.kuknos_send_dialogTitle));
            if (errorM.getResID() == 0)
                defaultRoundDialog.setMessage(errorM.getMessage());
            else
                defaultRoundDialog.setMessage(getResources().getString(errorM.getResID()));
            if (!errorM.getState()) {
                // success
                defaultRoundDialog.setPositiveButton(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack), (dialog, id) -> {
                    //close frag
                    popBackStackFragment();
                });
            } else {
                // error
                defaultRoundDialog.setPositiveButton(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack), null);
            }
            defaultRoundDialog.show();
        });
    }

    private void progressSubmitVisibility() {
        kuknosSignupInfoVM.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.fragKuknosSSubmit.setText(getString(R.string.kuknos_send_connectingServer));
                binding.fragKuknosSAmountET.setEnabled(false);
                binding.fragKuknosSWalletAddressET.setEnabled(false);
                binding.fragKuknosSTextET.setEnabled(false);
                binding.fragKuknosSProgressV.setVisibility(View.VISIBLE);
            } else {
                binding.fragKuknosSSubmit.setText(getString(R.string.kuknos_send_sendBtn));
                binding.fragKuknosSAmountET.setEnabled(true);
                binding.fragKuknosSWalletAddressET.setEnabled(true);
                binding.fragKuknosSTextET.setEnabled(true);
                binding.fragKuknosSProgressV.setVisibility(View.GONE);
            }
        });
    }

    private void resetEdittextAfterError() {
        binding.fragKuknosSTextET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosSTextHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.fragKuknosSAmountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosSAmountHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.fragKuknosSWalletAddressET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosSWalletAddressHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void goToPin() {
        kuknosSignupInfoVM.getGoToPin().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(KuknosEnterPinFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosEnterPinFrag.newInstance(() -> kuknosSignupInfoVM.sendDataServer());
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });
    }
}
