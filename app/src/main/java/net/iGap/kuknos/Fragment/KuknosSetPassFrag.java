package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosSetpasswordBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.kuknos.viewmodel.KuknosSetPassVM;

public class KuknosSetPassFrag extends BaseFragment {

    private FragmentKuknosSetpasswordBinding binding;
    private KuknosSetPassVM kuknosSetPassVM;

    /**
     * @param mode is for after setting the pass
     *             0: make new wallet
     *             1: -
     *             2: account already exists
     *             3: need to sign up
     * @return
     */
    public static KuknosSetPassFrag newInstance(int mode) {
        KuknosSetPassFrag frag = new KuknosSetPassFrag();
        Bundle data = new Bundle();
        data.putInt("mode", mode);
        frag.setArguments(data);
        return frag;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosSetPassVM = ViewModelProviders.of(this).get(KuknosSetPassVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_setpassword, container, false);
        binding.setViewmodel(kuknosSetPassVM);
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

        LinearLayout toolbarLayout = binding.fragKuknosSPToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);

        onNext();
        onError();
        textInputManager();
    }

    private void onNext() {
        kuknosSetPassVM.getNextPage().observe(getViewLifecycleOwner(), nextPage -> {
            if (nextPage) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(KuknosSetPassConfirmFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosSetPassConfirmFrag.newInstance();
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                Bundle bundle = new Bundle();
                bundle.putString("selectedPIN", kuknosSetPassVM.getPIN());
                bundle.putInt("mode", getArguments().getInt("mode"));
                fragment.setArguments(bundle);
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });
    }

    private void onError() {
        kuknosSetPassVM.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                if (errorM.getMessage().equals("0")) {
                    Snackbar snackbar = Snackbar.make(binding.fragKuknosSPContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG);
                    snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
                    snackbar.show();
                }
            }
        });
    }

    private void textInputManager() {
        // Pin 1
        binding.fragKuknosSPNum1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    binding.fragKuknosSPNum2.setEnabled(true);
                    binding.fragKuknosSPNum2.requestFocus();
                }
            }
        });

        // Pin 2
        binding.fragKuknosSPNum2.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.fragKuknosSPNum1.requestFocus();
                    binding.fragKuknosSPNum2.setEnabled(false);
                    return true;
                }
            }
            return false;
        });
        binding.fragKuknosSPNum2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    binding.fragKuknosSPNum3.setEnabled(true);
                    binding.fragKuknosSPNum3.requestFocus();
                }
            }
        });

        // Pin 3
        binding.fragKuknosSPNum3.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.fragKuknosSPNum2.requestFocus();
                    binding.fragKuknosSPNum3.setEnabled(false);
                    return true;
                }
            }
            return false;
        });
        binding.fragKuknosSPNum3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    binding.fragKuknosSPNum4.setEnabled(true);
                    binding.fragKuknosSPNum4.requestFocus();
                }
            }
        });

        // Pin 4
        binding.fragKuknosSPNum4.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                kuknosSetPassVM.setCompletePin(false);
                if (((AppCompatEditText) v).getEditableText().length() == 0) {
                    binding.fragKuknosSPNum3.requestFocus();
                    binding.fragKuknosSPNum4.setEnabled(false);
                    return true;
                }
            }
            return false;
        });
        binding.fragKuknosSPNum4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    // build the Pin Code
                    kuknosSetPassVM.setCompletePin(true);
                }
            }
        });
    }

}
