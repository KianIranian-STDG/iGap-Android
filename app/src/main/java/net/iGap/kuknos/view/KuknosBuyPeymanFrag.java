package net.iGap.kuknos.view;

import android.content.DialogInterface;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosBuyPeymanBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.viewmodel.KuknosBuyPeymanVM;
import net.iGap.libs.bottomNavigation.Util.Utils;

public class KuknosBuyPeymanFrag extends BaseFragment {

    private FragmentKuknosBuyPeymanBinding binding;
    private KuknosBuyPeymanVM kuknosBuyPeymanVM;
    private HelperToolbar mHelperToolbar;

    public static KuknosBuyPeymanFrag newInstance() {
        KuknosBuyPeymanFrag kuknosLoginFrag = new KuknosBuyPeymanFrag();
        return kuknosLoginFrag;
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

        LinearLayout toolbarLayout = binding.fragKuknosBuyPToolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        onSumVisibility();
        onBankPage();
        onError();
        onProgress();
        entryListener();
    }


    private void onError() {
        kuknosBuyPeymanVM.getError().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true && errorM.getMessage().equals("0")) {
                    binding.fragKuknosBuyPAmountHolder.setError(getResources().getString(errorM.getResID()));
                    binding.fragKuknosBuyPAmountHolder.requestFocus();
                } else if (errorM.getState() == true && errorM.getMessage().equals("1")) {
                    showDialog(errorM.getResID());
                }
            }
        });
    }

    private void showDialog(int messageResource) {
        DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
        defaultRoundDialog.setTitle(getResources().getString(R.string.kuknos_viewRecoveryEP_failTitle));
        defaultRoundDialog.setMessage(getResources().getString(messageResource));
        defaultRoundDialog.setPositiveButton(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        defaultRoundDialog.show();
    }

    private void onProgress() {
        kuknosBuyPeymanVM.getProgressState().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
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
                }
            }
        });
    }

    private void onSumVisibility() {
        kuknosBuyPeymanVM.getSumState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean)
                    binding.fragKuknosBuyPSumGroup.setVisibility(View.VISIBLE);
                else
                    binding.fragKuknosBuyPSumGroup.setVisibility(View.GONE);
            }
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
        kuknosBuyPeymanVM.getNextPage().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == true) {
                    // go to Bank
                }
            }
        });
    }

}
