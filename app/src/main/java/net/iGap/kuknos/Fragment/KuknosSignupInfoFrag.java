package net.iGap.kuknos.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentKuknosSignupInfoBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.kuknos.viewmodel.KuknosSignupInfoVM;

import org.jetbrains.annotations.NotNull;

public class KuknosSignupInfoFrag extends BaseAPIViewFrag<KuknosSignupInfoVM> {

    private FragmentKuknosSignupInfoBinding binding;

    public static KuknosSignupInfoFrag newInstance() {
        return new KuknosSignupInfoFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosSignupInfoVM.class);
        /*viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new KuknosSignupInfoVM();
            }
        }).get(KuknosSignupInfoVM.class);*/
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_signup_info, container, false);
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
                .setLeftIcon(R.string.icon_back)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosSIToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        String t = String.format(getString(R.string.terms_and_condition), getString(R.string.terms_and_condition_clickable));
        SpannableString ss = new SpannableString(t);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NotNull View textView) {
                viewModel.getTermsAndCond();
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

        onError();
        onGoNextPage();
        onTermsDownload();
        onCheckUsernameState();
        onCheckUsernameETFocus();
        onEmailTextChange();
        progressSubmitVisibility();
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

    private void saveRegisterInfo() {
        SharedPreferences sharedpreferences = getContext().getSharedPreferences("KUKNOS_REGISTER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("RegisterInfo", new Gson().toJson(viewModel.getKuknosSignupM()));
        editor.apply();
    }

    private void onError() {

        viewModel.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                switch (errorM.getMessage()) {
                    case "0":
                        binding.fragKuknosSIUsernameHolder.setError("" + getString(errorM.getResID()));
                        break;
                    case "1":
                        binding.fragKuknosSIEmailHolder.setError("" + getString(errorM.getResID()));
                        break;
                    case "2":
                        binding.fragKuknosSINameHolder.setError("" + getString(errorM.getResID()));
                        break;
                    case "3":
                        binding.fragKuknosSINIDHolder.setError("" + getString(errorM.getResID()));
                        break;
                    case "4":
                        Snackbar.make(binding.pageContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG)
                                .setAction(R.string.ok, v -> {

                                }).show();
                        break;
                    default:
                        Snackbar.make(binding.pageContainer, errorM.getMessage(), Snackbar.LENGTH_LONG)
                                .setAction(R.string.ok, v -> {

                                }).show();
                        break;
                }
            }
        });

    }

    private void onGoNextPage() {

        viewModel.getNextPage().observe(getViewLifecycleOwner(), nextPage -> {
            if (nextPage) {
                saveRegisterInfo();
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(KuknosPanelFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosPanelFrag.newInstance();
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });

    }

    private void onCheckUsernameState() {
        viewModel.getCheckUsernameState().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer == 0) {
                    progressCheckUserVisibility(true);
                } else if (integer == 1) {
                    // success
                    usernameStateVisibility(true);
                    binding.fragKuknosSICheckIcon.setText(getString(R.string.icon_valid));
                    binding.fragKuknosSICheckIcon.setTextColor(getResources().getColor(R.color.green));
                    binding.fragKuknosSICheckUsername.setText(getString(R.string.kuknos_SignupInfo_ValidUsername));
                    binding.fragKuknosSICheckUsername.setTextColor(getResources().getColor(R.color.green));

                } else if (integer == 2) {
                    // error
                    usernameStateVisibility(true);
                    binding.fragKuknosSICheckIcon.setText(getString(R.string.icon_error));
                    binding.fragKuknosSICheckIcon.setTextColor(getResources().getColor(R.color.red));
                    binding.fragKuknosSICheckUsername.setText(getString(R.string.kuknos_SignupInfo_errorUsernameInvalid));
                    binding.fragKuknosSICheckUsername.setTextColor(getResources().getColor(R.color.red));
                } else {
                    usernameStatusGone();
                }
            }
        });
    }

    private void onCheckUsernameETFocus() {
        binding.fragKuknosSIUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosSIUsernameHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setUsernameIsValid(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.fragKuknosSIUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                viewModel.cancelUsernameServer();
            } else {
                if (!viewModel.getProgressSendDServerState().getValue())
                    viewModel.isUsernameValid(false);
            }
        });
        binding.fragKuknosSIName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosSINameHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.fragKuknosSINID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosSINIDHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        binding.fragKuknosSIEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosSIEmailHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void onEmailTextChange() {
        binding.fragKuknosSIEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosSIEmailHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void progressCheckUserVisibility(boolean active) {
        if (active) {
            binding.fragKuknosSIProgress.setVisibility(View.VISIBLE);
            binding.fragKuknosSICheckUsername.setVisibility(View.VISIBLE);
            binding.fragKuknosSICheckUsername.setText(getText(R.string.kuknos_SignupInfo_checkUsername));
            binding.fragKuknosSICheckUsername.setTextColor(getResources().getColor(R.color.black));
            binding.fragKuknosSICheckIcon.setVisibility(View.GONE);
        } else {
            usernameStatusGone();
        }
    }

    private void progressSubmitVisibility() {
        viewModel.getProgressSendDServerState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.fragKuknosSISubmit.setText(getString(R.string.kuknos_SignupInfo_submitConnecting));
                binding.fragKuknosSIUsername.setEnabled(false);
                binding.fragKuknosSIEmail.setEnabled(false);
                binding.fragKuknosSIProgressV.setVisibility(View.VISIBLE);
            } else {
                binding.fragKuknosSISubmit.setText(getString(R.string.kuknos_SignupInfo_submitBtn));
                binding.fragKuknosSIUsername.setEnabled(true);
                binding.fragKuknosSIEmail.setEnabled(true);
                binding.fragKuknosSIProgressV.setVisibility(View.GONE);
            }
        });
    }

    private void usernameStatusGone() {
        binding.fragKuknosSIProgress.setVisibility(View.GONE);
        binding.fragKuknosSICheckUsername.setVisibility(View.GONE);
        binding.fragKuknosSICheckIcon.setVisibility(View.GONE);
    }

    private void usernameStateVisibility(boolean active) {
        if (active) {
            binding.fragKuknosSIProgress.setVisibility(View.GONE);
            binding.fragKuknosSICheckUsername.setVisibility(View.VISIBLE);
            binding.fragKuknosSICheckIcon.setVisibility(View.VISIBLE);
        } else
            usernameStatusGone();
    }

}
