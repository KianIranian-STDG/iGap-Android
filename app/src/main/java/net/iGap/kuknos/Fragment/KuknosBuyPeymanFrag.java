package net.iGap.kuknos.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentKuknosBuyPeymanBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.HelperUrl;
import net.iGap.kuknos.Model.KuknosPaymentResponse;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.kuknos.viewmodel.KuknosBuyPeymanVM;

import org.jetbrains.annotations.NotNull;

public class KuknosBuyPeymanFrag extends BaseAPIViewFrag<KuknosBuyPeymanVM> {

    private FragmentKuknosBuyPeymanBinding binding;

    public static KuknosBuyPeymanFrag newInstance() {
        return new KuknosBuyPeymanFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosBuyPeymanVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_buy_peyman, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        Bundle b = getArguments();
        viewModel.setCurrentAssetInfo(b.getString("balanceClientInfo"));

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

        LinearLayout toolbarLayout = binding.fragKuknosBuyPToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        viewModel.getAssetValue();

        String t = String.format(getString(R.string.terms_and_condition), getString(R.string.terms_and_condition_clickable));
        SpannableString ss = new SpannableString(t);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NotNull View textView) {
                HelperUrl.openWebBrowser(getContext(), viewModel.getRegulationsAddress());
            }

            @Override
            public void updateDrawState(@NotNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, t.indexOf(getString(R.string.terms_and_condition_clickable)), t.indexOf(getString(R.string.terms_and_condition_clickable)) + getString(R.string.terms_and_condition_clickable).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.termsAndConditionText.setText(ss);
        binding.termsAndConditionText.setMovementMethod(LinkMovementMethod.getInstance());
        binding.termsAndConditionText.setHighlightColor(Color.TRANSPARENT);

        binding.fragKuknosBuyPTitle.setText(getResources().getString(R.string.kuknos_buyP_title, viewModel.getCurrentAssetType()));
        binding.fragKuknosBuyPMessage.setText(getResources().getString(R.string.kuknos_buyP_message, viewModel.getCurrentAssetType()));
        binding.fragKuknosBuyPAmountHolder.setHint(getResources().getString(R.string.kuknos_buyP_amount, viewModel.getCurrentAssetType()));

        onSumVisibility();
//        onBankPage();
        onError();
        onProgress();
        entryListener();
        goToPaymentListener();
        onTermsDownload();
        onPaymentDialog();
//        goToPin();
    }

    private void onPaymentDialog() {
        viewModel.getPaymentData().observe(getViewLifecycleOwner(), new Observer<KuknosPaymentResponse>() {
            @Override
            public void onChanged(KuknosPaymentResponse kuknosPaymentResponse) {
                if (kuknosPaymentResponse != null)
                    showDialog(getResources().getString(R.string.kuknos_payment_title),
                            getResources().getString(R.string.kuknos_payment_assetCode) + " " + kuknosPaymentResponse.getAssetCode()
                                    + "\n" + getResources().getString(R.string.kuknos_payment_assetCount) + " " + kuknosPaymentResponse.getAssetCount()
                                    + "\n" + getResources().getString(R.string.kuknos_payment_assetPrice) + " " + kuknosPaymentResponse.getAssetPrice() + " " + getResources().getString(R.string.rial)
                                    + "\n" + getResources().getString(R.string.kuknos_payment_totalAmount) + " " + kuknosPaymentResponse.getTotalAmount() + " " + getResources().getString(R.string.rial)
                                    + "\n" + getResources().getString(R.string.kuknos_payment_hash) + " " + kuknosPaymentResponse.getHash());
            }
        });
    }

    private void onTermsDownload() {
        viewModel.getTandCAgree().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null)
                    showDialogTermAndCondition(s);
            }
        });
    }

    private void showDialogTermAndCondition(String message) {
        if (getActivity() != null) {
            Dialog dialogTermsAndCondition = new Dialog(getActivity());
            dialogTermsAndCondition.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogTermsAndCondition.setContentView(R.layout.terms_condition_dialog);
            AppCompatTextView termsText = dialogTermsAndCondition.findViewById(R.id.termAndConditionTextView);
            termsText.setText(message);
            dialogTermsAndCondition.findViewById(R.id.okButton).setOnClickListener(v -> dialogTermsAndCondition.dismiss());
            dialogTermsAndCondition.show();
        }
    }


    private void onError() {
        viewModel.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState() && errorM.getMessage().equals("0")) {
                binding.fragKuknosBuyPAmountHolder.setError(getResources().getString(errorM.getResID(), viewModel.getCurrentAssetType()));
                binding.fragKuknosBuyPAmountHolder.requestFocus();
            } else if (errorM.getState() && errorM.getMessage().equals("1")) {
                showDialog(errorM.getResID());
            } else if (errorM.getState() && errorM.getMessage().equals("2")) {
                binding.fragKuknosBuyPPrice.setText(getResources().getString(errorM.getResID()));
            } else {
                showDialog(getResources().getString(R.string.kuknos_viewRecoveryEP_failTitle), errorM.getMessage());
            }
        });
    }

    private void showDialog(int messageResource) {
        new MaterialDialog.Builder(getContext())
                .title(R.string.kuknos_viewRecoveryEP_failTitle)
                .positiveText(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack))
                .content(getResources().getString(messageResource)).show();
    }

    private void showDialog(String title, String message) {
        new MaterialDialog.Builder(getContext())
                .title(title)
                .positiveText(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack))
                .content(message)
                .show();
    }

    private void onProgress() {
        viewModel.getProgressState().observe(getViewLifecycleOwner(), integer -> {
            if (integer == 0) {
                binding.fragKuknosBuyPProgressV.setVisibility(View.GONE);
                binding.fragKuknosBuyPSubmit.setText(getResources().getText(R.string.kuknos_buyP_btn));
            } else if (integer == 1) {
                binding.fragKuknosBuyPProgressV.setVisibility(View.VISIBLE);
                viewModel.setAmountEnable(false);
                binding.fragKuknosBuyPAmount.setEnabled(false);
                binding.fragKuknosBuyPSubmit.setText(getResources().getText(R.string.kuknos_buyP_btn_server));
            } else if (integer == 2) {
                binding.fragKuknosBuyPSubmit.setText(getResources().getText(R.string.kuknos_buyP_btn_server2));
            } else if (integer == 3) {
                binding.fragKuknosBuyPSubmit.setText(getResources().getText(R.string.kuknos_buyP_btn_server3));
            }
        });
    }

    private void onSumVisibility() {
        viewModel.getSumState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                binding.fragKuknosBuyPSumGroup.setVisibility(View.VISIBLE);
            else
                binding.fragKuknosBuyPSumGroup.setVisibility(View.GONE);
        });
    }

    private void entryListener() {
        binding.fragKuknosBuyPAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosBuyPAmountHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (viewModel.updateSum())
                    binding.fragKuknosBuyPSumGroup.setVisibility(View.VISIBLE);
                else
                    binding.fragKuknosBuyPSumGroup.setVisibility(View.GONE);
            }
        });
    }

    private void onBankPage() {
        viewModel.getNextPage().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                // go to Bank
            }
        });
    }

    private void goToPaymentListener() {
        viewModel.getGoToPaymentPage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String token) {
                if (getActivity() != null && token != null) {
                    new HelperFragment(getActivity().getSupportFragmentManager()).loadPayment(getString(R.string.kuknos_buyAsset), token, result -> {
                        if (getActivity() != null && result.isSuccess()) {
                            viewModel.getPaymentData("" + result.getRRN());
                        }
                    });
                }
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
}
