package net.iGap.kuknos.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosSignupInfoBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.viewmodel.KuknosSignupInfoVM;

public class KuknosSignupInfoFrag extends BaseFragment {

    private FragmentKuknosSignupInfoBinding binding;
    private KuknosSignupInfoVM kuknosSignupInfoVM;

    public static KuknosSignupInfoFrag newInstance() {
        return new KuknosSignupInfoFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosSignupInfoVM = ViewModelProviders.of(this).get(KuknosSignupInfoVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_signup_info, container, false);
        binding.setViewmodel(kuknosSignupInfoVM);
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

        LinearLayout toolbarLayout = binding.fragKuknosSIToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        /*if (kuknosSignupInfoVM.loginStatus()) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag(KuknosPanelFrag.class.getName());
            if (fragment == null) {
                fragment = KuknosPanelFrag.newInstance();
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
            }
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            popBackStackFragment();
        }*/

        onError();
        onGoNextPage();
        onCheckUsernameState();
        onCheckUsernameETFocus();
        onEmailTextChange();
        progressSubmitVisibility();
    }

    private void saveRegisterInfo() {
        SharedPreferences sharedpreferences = getContext().getSharedPreferences("KUKNOS_REGISTER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("RegisterInfo", new Gson().toJson(kuknosSignupInfoVM.getKuknosSignupM()));
        editor.apply();
    }

    private void onError() {

        kuknosSignupInfoVM.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                switch (errorM.getMessage()) {
                    case "0":
                        binding.fragKuknosSIUsernameHolder.setError("" + getString(errorM.getResID()));
                        binding.fragKuknosSIUsername.requestFocus();
                        break;
                    case "1":
                        binding.fragKuknosSIEmailHolder.setError("" + getString(errorM.getResID()));
                        binding.fragKuknosSIEmail.requestFocus();
                        break;
                    case "2":
                        binding.fragKuknosSINameHolder.setError("" + getString(errorM.getResID()));
                        binding.fragKuknosSIName.requestFocus();
                        break;
                    case "3":
                        binding.fragKuknosSINIDHolder.setError("" + getString(errorM.getResID()));
                        binding.fragKuknosSINID.requestFocus();
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

        kuknosSignupInfoVM.getNextPage().observe(getViewLifecycleOwner(), nextPage -> {
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
        /*kuknosSignupInfoVM.getCheckUsernameState().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer == 0) {
                    progressCheckUserVisibility(true);
                } else if (integer == 1) {
                    // success
                    usernameStateVisibility(true);
                    binding.fragKuknosSICheckIcon.setText(getString(R.string.valid_icon));
                    binding.fragKuknosSICheckIcon.setTextColor(getResources().getColor(R.color.green));
                    binding.fragKuknosSICheckUsername.setText(getString(R.string.kuknos_SignupInfo_ValidUsername));
                    binding.fragKuknosSICheckUsername.setTextColor(getResources().getColor(R.color.green));

                } else if (integer == 2) {
                    // error
                    usernameStateVisibility(true);
                    binding.fragKuknosSICheckIcon.setText(getString(R.string.error_icon));
                    binding.fragKuknosSICheckIcon.setTextColor(getResources().getColor(R.color.red));
                    binding.fragKuknosSICheckUsername.setText(getString(R.string.kuknos_SignupInfo_errorUsernameInvalid));
                    binding.fragKuknosSICheckUsername.setTextColor(getResources().getColor(R.color.red));
                } else {
                    usernameStatusGone();
                }
            }
        });*/
    }

    private void onCheckUsernameETFocus() {
        /*binding.fragKuknosSIUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosSIUsernameHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });*/
        /*binding.fragKuknosSIUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                kuknosSignupInfoVM.cancelUsernameServer();
            } else {
                //TODO delete log
                if (!kuknosSignupInfoVM.getProgressSendDServerState().getValue())
                    kuknosSignupInfoVM.isUsernameValid(false);
            }
        });*/
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
        kuknosSignupInfoVM.getProgressSendDServerState().observe(getViewLifecycleOwner(), aBoolean -> {
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
