package net.iGap.kuknos.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosLogoutBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.viewmodel.KuknosLogoutVM;
import net.iGap.libs.bottomNavigation.Util.Utils;

public class KuknosLogoutFrag extends BaseFragment {

    private FragmentKuknosLogoutBinding binding;
    private KuknosLogoutVM kuknoslogoutVM;
    private HelperToolbar mHelperToolbar;

    public static KuknosLogoutFrag newInstance() {
        KuknosLogoutFrag kuknosLoginFrag = new KuknosLogoutFrag();
        return kuknosLoginFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknoslogoutVM = ViewModelProviders.of(this).get(KuknosLogoutVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_logout, container, false);
        binding.setViewmodel(kuknoslogoutVM);
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

        LinearLayout toolbarLayout = binding.fragKuknosLogoutToolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        onNextPage();
        onError();
        onProgress();
        entryListener();
    }


    private void onError() {
        kuknoslogoutVM.getError().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true && errorM.getMessage().equals("0")) {
                    binding.fragKuknosLogoutPassHolder.setError(getResources().getString(errorM.getResID()));
                    binding.fragKuknosLogoutPassHolder.requestFocus();
                }
                else if (errorM.getState() == true && errorM.getMessage().equals("1")) {
                    showDialog(errorM.getResID());
                }
            }
        });
    }

    private void showDialog(int messageResource) {
        DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
        defaultRoundDialog.setTitle(R.string.kuknos_viewRecoveryEP_failTitle);
        defaultRoundDialog.setMessage(getResources().getString(messageResource));
        defaultRoundDialog.setPositiveButton(R.string.kuknos_RecoverySK_Error_Snack, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        defaultRoundDialog.show();
    }

    private void onProgress() {
        kuknoslogoutVM.getProgressState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == true) {
                    binding.fragKuknosLogoutProgressV.setVisibility(View.VISIBLE);
                    binding.fragKuknosLogoutPass.setEnabled(false);
                }
                else {
                    binding.fragKuknosLogoutProgressV.setVisibility(View.GONE);
                    binding.fragKuknosLogoutPass.setEnabled(true);
                }
            }
        });
    }

    private void entryListener() {
        binding.fragKuknosLogoutPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosLogoutPassHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void onNextPage() {
        kuknoslogoutVM.getNextPage().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == true) {
                    popBackStackFragment();
                    popBackStackFragment();
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment fragment = fragmentManager.findFragmentByTag(KuknosLoginFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosLoginFrag.newInstance();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                }
            }
        });
    }

}
