package net.iGap.kuknos.view;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosBuyPeymanBinding;
import net.iGap.module.dialog.DefaultRoundDialog;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.kuknos.viewmodel.KuknosBuyPeymanVM;

import org.jetbrains.annotations.NotNull;

public class KuknosBuyPeymanFrag extends BaseFragment {

    private FragmentKuknosBuyPeymanBinding binding;
    private KuknosBuyPeymanVM kuknosBuyPeymanVM;

    public static KuknosBuyPeymanFrag newInstance() {
        return new KuknosBuyPeymanFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosBuyPeymanVM = ViewModelProviders.of(this).get(KuknosBuyPeymanVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_buy_peyman, container, false);
        binding.setViewmodel(kuknosBuyPeymanVM);
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

        LinearLayout toolbarLayout = binding.fragKuknosBuyPToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        kuknosBuyPeymanVM.getAssetValue();

        String t = String.format(getString(R.string.terms_and_condition), getString(R.string.terms_and_condition_clickable));
        SpannableString ss = new SpannableString(t);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NotNull View textView) {
                kuknosBuyPeymanVM.getTermsAndCond();
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

        onSumVisibility();
        onBankPage();
        onError();
        onProgress();
        entryListener();
        goToPaymentListener();
        onTermsDownload();
        goToPin();
    }

    private void onTermsDownload() {
        kuknosBuyPeymanVM.getTandCAgree().observe(getViewLifecycleOwner(), new Observer<String>() {
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
        kuknosBuyPeymanVM.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState() && errorM.getMessage().equals("0")) {
                binding.fragKuknosBuyPAmountHolder.setError(getResources().getString(errorM.getResID()));
                binding.fragKuknosBuyPAmountHolder.requestFocus();
            } else if (errorM.getState() && errorM.getMessage().equals("1")) {
                showDialog(errorM.getResID());
            } else {
                showDialog(errorM.getMessage());
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

    private void showDialog(String message) {
        DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
        defaultRoundDialog.setTitle(getResources().getString(R.string.kuknos_viewRecoveryEP_failTitle));
        defaultRoundDialog.setMessage(message);
        defaultRoundDialog.setPositiveButton(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack), (dialog, id) -> {

        });
        defaultRoundDialog.show();
    }

    private void onProgress() {
        kuknosBuyPeymanVM.getProgressState().observe(getViewLifecycleOwner(), integer -> {
            if (integer == 0) {
                binding.fragKuknosBuyPProgressV.setVisibility(View.GONE);
                binding.fragKuknosBuyPAmount.setEnabled(true);
                binding.fragKuknosBuyPSubmit.setText(getResources().getText(R.string.kuknos_buyP_btn));
            } else if (integer == 1) {
                binding.fragKuknosBuyPProgressV.setVisibility(View.VISIBLE);
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
        kuknosBuyPeymanVM.getSumState().observe(getViewLifecycleOwner(), aBoolean -> {
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
                if (kuknosBuyPeymanVM.updateSum())
                    binding.fragKuknosBuyPSumGroup.setVisibility(View.VISIBLE);
                else
                    binding.fragKuknosBuyPSumGroup.setVisibility(View.GONE);
            }
        });
    }

    private void onBankPage() {
        kuknosBuyPeymanVM.getNextPage().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                // go to Bank
            }
        });
    }

    private void goToPaymentListener() {
        kuknosBuyPeymanVM.getGoToPaymentPage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String token) {
                if (getActivity() != null && token != null) {
                    new HelperFragment(getActivity().getSupportFragmentManager()).loadPayment(getString(R.string.kuknos_buyAsset), token, result -> {
                        if (getActivity() != null && result.isSuccess()) {
                            Toast.makeText(getContext(), "Payment Done. ", Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }
                    });
                }
            }
        });
    }

    private void goToPin() {
        kuknosBuyPeymanVM.getGoToPin().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(KuknosEnterPinFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosEnterPinFrag.newInstance(() -> kuknosBuyPeymanVM.sendDataServer());
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });
    }
}
