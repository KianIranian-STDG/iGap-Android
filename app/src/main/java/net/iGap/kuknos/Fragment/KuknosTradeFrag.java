package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.adapter.kuknos.WalletHistorySpinnerAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentKuknosTradeBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.kuknos.viewmodel.KuknosTradeVM;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class KuknosTradeFrag extends BaseAPIViewFrag<KuknosTradeVM> {

    private FragmentKuknosTradeBinding binding;
    private Spinner originSpinner;
    private Spinner destSpinner;

    public static KuknosTradeFrag newInstance() {
        return new KuknosTradeFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosTradeVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_trade, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        originSpinner = binding.fragKuknosTranTAmountSpinner;
        destSpinner = binding.fragKuknosTranExchangeSpinner;

        originSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("amini", "onItemSelected: in item selected");
                viewModel.originSpinnerSelect(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        destSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setDestPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        viewModel.getDataFromServer();

        onErrorObserver();
        onDataChanged();
        onProgress();
        entryListener();
        goToPin();
    }

    private void onDataChanged() {
        viewModel.getKuknosOriginWalletsM().observe(getViewLifecycleOwner(), balances -> {
            if (balances.size() != 0) {
                WalletHistorySpinnerAdapter adapter = new WalletHistorySpinnerAdapter(getContext(), balances);
                originSpinner.setAdapter(adapter);
            }
        });
        viewModel.getKuknosDestinationWalletsM().observe(getViewLifecycleOwner(), balances -> {
            if (balances.size() != 0) {
                WalletHistorySpinnerAdapter adapter = new WalletHistorySpinnerAdapter(getContext(), balances);
                destSpinner.setAdapter(adapter);
            }
        });
    }

    private void onErrorObserver() {

        viewModel.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getMessage().equals("0") && errorM.getState()) {
                //origin Problem
                binding.fragKuknosTranTAmountHolder.setError("" + getString(errorM.getResID()));
                binding.fragKuknosTranTAmountHolder.requestFocus();
            } else if (errorM.getMessage().equals("1") && errorM.getState()) {
                //origin Problem
                binding.fragKuknosTranExchangeHolder.setError("" + getString(errorM.getResID()));
                binding.fragKuknosTranExchangeHolder.requestFocus();
            } else if (errorM.getMessage().equals("2")) {
                showDialog(errorM.getState(), errorM.getResID());
            } else {
                showDialog(errorM.getMessage());
            }
        });
    }

    private void showDialog(boolean state, int messageResource) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getContext())
                .positiveText(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack))
                .content(getResources().getString(messageResource))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (!state)
                            popBackStackFragment();
                    }
                });
        if (!state)
            dialog.title(getResources().getString(R.string.kuknos_changePIN_successTitle));
        else
            dialog.title(getResources().getString(R.string.kuknos_changePIN_failTitle));
        dialog.show();
    }

    private void showDialog(String messageResource) {
        new MaterialDialog.Builder(getContext())
                .title(getResources().getString(R.string.kuknos_changePIN_failTitle))
                .positiveText(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack))
                .content(messageResource)
                .show();
    }

    private void onProgress() {
        viewModel.getSendProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.fragKuknosTranProgressV.setVisibility(View.VISIBLE);
                binding.fragKuknosTranExchange.setEnabled(false);
                binding.fragKuknosTranTAmount.setEnabled(false);
                binding.fragKuknosTranSubmit.setText(getResources().getText(R.string.kuknos_trade_server));
            } else {
                binding.fragKuknosTranProgressV.setVisibility(View.GONE);
                binding.fragKuknosTranExchange.setEnabled(true);
                binding.fragKuknosTranTAmount.setEnabled(true);
                binding.fragKuknosTranSubmit.setText(getResources().getText(R.string.kuknos_trade_btn));
            }
        });

        viewModel.getFetchProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean)
                binding.fragKuknosPProgressV.setVisibility(View.VISIBLE);
            else
                binding.fragKuknosPProgressV.setVisibility(View.GONE);
        });
    }

    private void entryListener() {
        binding.fragKuknosTranTAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosTranTAmountHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.fragKuknosTranExchange.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.fragKuknosTranExchangeHolder.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

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
