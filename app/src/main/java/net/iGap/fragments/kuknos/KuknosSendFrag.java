package net.iGap.fragments.kuknos;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentKuknosSendBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.kuknos.KuknosSendVM;

import java.io.IOException;

public class KuknosSendFrag extends BaseAPIViewFrag<KuknosSendVM> {

    private FragmentKuknosSendBinding binding;

    public static KuknosSendFrag newInstance() {
        return new KuknosSendFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosSendVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_send, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        Bundle b = getArguments();
        viewModel.setBalanceInfoM(b.getString("balanceClientInfo"));

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
        binding.modeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onModeChangeView(checkedId);
            }
        });

        onError();
        onTransfer();
        progressSubmitVisibility();
        resetEdittextAfterError();
        openQrScanner();
        goToPin();
        changeHint();
    }

    private void openQrScanner() {
        viewModel.getOpenQrScanner().observe(getViewLifecycleOwner(), aBoolean -> {
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
        viewModel.getWalletID().set(code);
        binding.modePublicKey.setChecked(true);
    }

    private void onError() {

        viewModel.getErrorM().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                switch (errorM.getMessage()) {
                    case "0":
                        //Wallet ID Problem
                        binding.fragKuknosSWalletAddressHolder.setError("" + getString(errorM.getResID()));
//                        binding.fragKuknosSWalletAddressHolder.requestFocus();
                        break;
                    case "1":
                        //Amount enough
                        binding.fragKuknosSAmountHolder.setError("" + getString(errorM.getResID()));
//                        binding.fragKuknosSAmountHolder.requestFocus();
                        break;
                    case "2":
                        //memo
                        binding.fragKuknosSTextHolder.setError("" + getString(errorM.getResID()));
//                        binding.fragKuknosSTextHolder.requestFocus();
                        break;
                    default:
                        //Wallet ID Problem
                        binding.fragKuknosSWalletAddressHolder.setError(errorM.getMessage());
                }
            }
        });
    }

    private void onTransfer() {
        viewModel.getPayResult().observe(getViewLifecycleOwner(), errorM -> {

            MaterialDialog.Builder dialog = new MaterialDialog.Builder(getContext())
                    .title(getResources().getString(R.string.kuknos_send_dialogTitle))
                    .positiveText(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack));

            if (errorM.getResID() == 0)
                dialog.content(errorM.getMessage());
            else
                dialog.content(getResources().getString(errorM.getResID()));
            if (!errorM.getState()) {
                // success
                dialog.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //close frag
                        popBackStackFragment();
                    }
                });
            }
            dialog.show();

        });
    }

    private void progressSubmitVisibility() {
        viewModel.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
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
                viewModel.setWalletIdVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.fragKuknosSWalletAddressET.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                viewModel.cancelFederation();
                binding.fragKuknosSWalletAddressET.setHint(viewModel.getChangeHint().getValue());
            } else {
                binding.fragKuknosSWalletAddressET.setHint("");
                if (viewModel.getFederationProgressVisibility().get() != View.VISIBLE)
                    viewModel.checkWalletID(false);
            }
        });
    }

    private void goToPin() {
        viewModel.getGoToPin().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(KuknosEnterPinFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosEnterPinFrag.newInstance(() -> viewModel.sendDataServer(), false);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });
    }

    public void onModeChangeView(int view) {
        switch (view) {
            case R.id.mode_publicKey:
                viewModel.onModeChange(0);
                break;
            case R.id.mode_kuknosID:
                viewModel.onModeChange(1);
                break;
        }
    }

    private void changeHint() {
        viewModel.getChangeHint().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer stringRes) {
                binding.fragKuknosSWalletAddressET.setHint(stringRes);
            }
        });

    }
}
