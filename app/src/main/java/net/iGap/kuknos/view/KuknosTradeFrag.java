package net.iGap.kuknos.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosTradeBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.fragments.BaseFragment;
import net.iGap.kuknos.view.adapter.WalletHistorySpinnerAdapter;
import net.iGap.kuknos.viewmodel.KuknosTradeVM;

public class KuknosTradeFrag extends BaseFragment {

    private FragmentKuknosTradeBinding binding;
    private KuknosTradeVM kuknosTradeVM;
    private Spinner originSpinner;
    private Spinner destSpinner;

    public static KuknosTradeFrag newInstance() {
        return new KuknosTradeFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosTradeVM = ViewModelProviders.of(this).get(KuknosTradeVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_trade, container, false);
        binding.setViewmodel(kuknosTradeVM);
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
                kuknosTradeVM.originSpinnerSelect(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        destSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kuknosTradeVM.setDestPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        kuknosTradeVM.getDataFromServer();

        onErrorObserver();
        onDataChanged();
        onProgress();
        entryListener();
    }

    private void onDataChanged() {
        kuknosTradeVM.getKuknosOriginWalletsM().observe(getViewLifecycleOwner(), balances -> {
            if (balances.size() != 0) {
                WalletHistorySpinnerAdapter adapter = new WalletHistorySpinnerAdapter(getContext(), balances);
                originSpinner.setAdapter(adapter);
            }
        });
        kuknosTradeVM.getKuknosDestinationWalletsM().observe(getViewLifecycleOwner(), balances -> {
            if (balances.size() != 0) {
                WalletHistorySpinnerAdapter adapter = new WalletHistorySpinnerAdapter(getContext(), balances);
                destSpinner.setAdapter(adapter);
            }
        });
    }

    private void onErrorObserver() {

        kuknosTradeVM.getError().observe(getViewLifecycleOwner(), errorM -> {
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
        DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
        if (!state)
            defaultRoundDialog.setTitle(getResources().getString(R.string.kuknos_changePIN_successTitle));
        else
            defaultRoundDialog.setTitle(getResources().getString(R.string.kuknos_changePIN_failTitle));
        defaultRoundDialog.setMessage(getResources().getString(messageResource));
        defaultRoundDialog.setPositiveButton(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack), (dialog, id) -> {
            if (!state)
                popBackStackFragment();
        });
        defaultRoundDialog.show();
    }

    private void showDialog(String messageResource) {
        DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
        defaultRoundDialog.setTitle(getResources().getString(R.string.kuknos_changePIN_failTitle));
        defaultRoundDialog.setMessage(messageResource);
        defaultRoundDialog.setPositiveButton(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack), (dialog, id) -> {
        });
        defaultRoundDialog.show();
    }

    private void onProgress() {
        kuknosTradeVM.getSendProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
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

        kuknosTradeVM.getFetchProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
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
}
