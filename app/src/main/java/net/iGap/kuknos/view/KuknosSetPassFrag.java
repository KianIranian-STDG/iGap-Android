package net.iGap.kuknos.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosSetpasswordBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.viewmodel.KuknosSetPassVM;
import net.iGap.libs.bottomNavigation.Util.Utils;

public class KuknosSetPassFrag extends BaseFragment {

    private FragmentKuknosSetpasswordBinding binding;
    private KuknosSetPassVM kuknosSetPassVM;
    private HelperToolbar mHelperToolbar;

    public static KuknosSetPassFrag newInstance() {
        KuknosSetPassFrag kuknosLoginFrag = new KuknosSetPassFrag();
        return kuknosLoginFrag;
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

        LinearLayout toolbarLayout = binding.fragKuknosSPToolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        onNext();
        onError();
        textInputManager();
    }

    private void onNext() {
        kuknosSetPassVM.getNextPage().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean nextPage) {
                if (nextPage == true) {
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment fragment = fragmentManager.findFragmentByTag(KuknosSetPassConfirmFrag.class.getName());
                    if (fragment == null) {
                        fragment = KuknosSetPassConfirmFrag.newInstance();
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("selectedPIN", kuknosSetPassVM.getPIN());
                    fragment.setArguments(bundle);
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                }
            }
        });
    }

    private void onError() {
        kuknosSetPassVM.getError().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true) {
                    //TODO clear Log
                    if (errorM.getMessage().equals("0")) {
                        Snackbar snackbar = Snackbar.make(binding.fragKuknosSPContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG);
                        snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                    }
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
