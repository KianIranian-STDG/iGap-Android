package net.iGap.fragments.kuknos;

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

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosLoginBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.kuknos.KuknosLoginVM;


public class KuknosLoginFrag extends BaseFragment {

    private FragmentKuknosLoginBinding binding;
    private KuknosLoginVM kuknosLoginVM;


    public static KuknosLoginFrag newInstance() {
        return new KuknosLoginFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosLoginVM = ViewModelProviders.of(this).get(KuknosLoginVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_login, container, false);
        binding.setViewmodel(kuknosLoginVM);
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
                        kuknosLoginVM.getNextPage().setValue(false);
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosIdToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        if (kuknosLoginVM.loginStatus()) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag(KuknosPanelFrag.class.getName());
            if (fragment == null) {
                fragment = KuknosPanelFrag.newInstance();
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
            }
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            popBackStackFragment();
        }

        onErrorObserver();
        onNext();
        progressState();
        onUserIDTextChange();
    }

    private void saveToken() {
        SharedPreferences sharedpreferences = getContext().getSharedPreferences("KUKNOS_REGISTER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("Token", kuknosLoginVM.getKuknoscheckUserM().getToken());
        editor.apply();
    }

    private void onErrorObserver() {

        kuknosLoginVM.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                //TODO clear Log
                if (errorM.getMessage().equals("0")) {
                    binding.fragKuknosIdUserIDHolder.setError("" + getString(errorM.getResID()));
                    binding.fragKuknosIdUserID.requestFocus();
                    binding.fragKuknosIdUserID.setEnabled(true);
                } else if (errorM.getMessage().equals("1")) {
                    Snackbar snackbar = Snackbar.make(binding.fragKuknosLoginContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG);
                    snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
                    snackbar.show();
                }
            }
        });
    }

    private void onNext() {

        kuknosLoginVM.getNextPage().observe(getViewLifecycleOwner(), nextPage -> {
            if (nextPage) {
                saveToken();

                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(KuknosEntryOptionFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosEntryOptionFrag.newInstance();
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                //fragmentTransaction.add(R.id.viewpager, fragment, fragment.getClass().getName()).commit();
            }
        });

    }

    private void progressState() {
        kuknosLoginVM.getProgressState().observe(getViewLifecycleOwner(), integer -> {
            switch (integer) {
                case 0:
                    binding.fragKuknosIdSubmit.setText(getString(R.string.kuknos_login_progress_next));
                    binding.fragKuknosIdSubmit.setEnabled(true);
                    binding.fragKuknosIdUserID.setEnabled(false);
                    binding.fragKuknosLProgressV.setVisibility(View.GONE);
                    break;
                case 1:
                    binding.fragKuknosIdSubmit.setText(getString(R.string.kuknos_login_progress_str));
                    binding.fragKuknosIdSubmit.setEnabled(false);
                    binding.fragKuknosIdUserID.setEnabled(false);
                    binding.fragKuknosLProgressV.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    private void onUserIDTextChange() {
        binding.fragKuknosIdUserID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosIdUserIDHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
