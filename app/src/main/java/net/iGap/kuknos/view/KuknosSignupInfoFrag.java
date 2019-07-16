package net.iGap.kuknos.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosSignupInfoBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.viewmodel.KuknosSignupInfoVM;
import net.iGap.libs.bottomNavigation.Util.Utils;

public class KuknosSignupInfoFrag extends BaseFragment {

    private FragmentKuknosSignupInfoBinding binding;
    private KuknosSignupInfoVM kuknosSignupInfoVM;
    private HelperToolbar mHelperToolbar;
    private boolean usernameFocusState = false;

    public static KuknosSignupInfoFrag newInstance() {
        KuknosSignupInfoFrag kuknosLoginFrag = new KuknosSignupInfoFrag();
        return kuknosLoginFrag;
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

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosSIToolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        onError();
        onNext();
        onAdvSecurity();
        onCheckUsernameState();
        onCheckUsernameFocus();
    }

    private void onError() {

        kuknosSignupInfoVM.getError().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true) {
                    if (errorM.getMessage().equals("1")) {
                        binding.fragKuknosSIEmail.setError("" + getString(errorM.getResID()));
                        binding.fragKuknosSIEmail.requestFocus();
                    }
                    else if (errorM.getMessage().equals("0")){
                        binding.fragKuknosSIUsername.setError("" + getString(errorM.getResID()));
                        binding.fragKuknosSIUsername.requestFocus();
                    }
                }
            }
        });

    }

    private void onNext() {

        kuknosSignupInfoVM.getNextPage().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean nextPage) {
                if (nextPage == true) {
                    /*FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment fragment = fragmentManager.findFragmentByTag(KuknosEntryOptionFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosEntryOptionFrag.newInstance();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();*/
                }
            }
        });

    }

    private void onAdvSecurity() {

        kuknosSignupInfoVM.getAdvancedPage().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                //TODO
            }
        });

    }

    private void onCheckUsernameState() {
        kuknosSignupInfoVM.getCheckUsernameState().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer == 0) {
                    progressVisibility(true);
                }
                else if (integer == 1) {
                    // success
                    usernameStateVisibility(true);
                    binding.fragKuknosSICheckIcon.setText(getString(R.string.valid_icon));
                    binding.fragKuknosSICheckIcon.setTextColor(getResources().getColor(R.color.green));
                    binding.fragKuknosSICheckUsername.setText(getString(R.string.kuknos_SignupInfo_ValidUsername));
                    binding.fragKuknosSICheckUsername.setTextColor(getResources().getColor(R.color.green));

                }
                else if (integer == 2) {
                    // error
                    usernameStateVisibility(true);
                    binding.fragKuknosSICheckIcon.setText(getString(R.string.error_icon));
                    binding.fragKuknosSICheckIcon.setTextColor(getResources().getColor(R.color.red));
                    binding.fragKuknosSICheckUsername.setText(getString(R.string.kuknos_SignupInfo_errorUsernameInvalid));
                    binding.fragKuknosSICheckUsername.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    usernameStatusGone();
                }
            }
        });
    }

    private void onCheckUsernameFocus() {
        binding.fragKuknosSIUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (usernameFocusState) {
                    //TODO delete log
                    Log.d("amini" , "true in here");
                    kuknosSignupInfoVM.getCheckUsernameState().setValue(0);
                    usernameFocusState = false;
                }
                else {
                    Log.d("amini" , "false in here");
                    kuknosSignupInfoVM.getCheckUsernameState().setValue(-1);
                    usernameFocusState = true;
                }
            }
        });
    }

    private void progressVisibility(boolean active) {
        if (active == true) {
            binding.fragKuknosSIProgress.setVisibility(View.VISIBLE);
            binding.fragKuknosSICheckUsername.setVisibility(View.VISIBLE);
            binding.fragKuknosSICheckUsername.setText(getText(R.string.kuknos_SignupInfo_checkUsername));
            binding.fragKuknosSICheckIcon.setVisibility(View.GONE);
        }
        else {
            usernameStatusGone();
        }
    }

    private void usernameStatusGone() {
        binding.fragKuknosSIProgress.setVisibility(View.GONE);
        binding.fragKuknosSICheckUsername.setVisibility(View.GONE);
        binding.fragKuknosSICheckIcon.setVisibility(View.GONE);
    }

    private void usernameStateVisibility(boolean active) {
        if (active == true) {
            binding.fragKuknosSIProgress.setVisibility(View.GONE);
            binding.fragKuknosSICheckUsername.setVisibility(View.VISIBLE);
            binding.fragKuknosSICheckIcon.setVisibility(View.VISIBLE);
        }
        else
            usernameStatusGone();
    }

}
