package net.iGap.fragments.mobileBank;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import net.iGap.R;
import net.iGap.adapter.mobileBank.AccountSpinnerAdapter;
import net.iGap.databinding.MobileBankPayLoanBsFragmentBinding;
import net.iGap.module.dialog.BaseBottomSheet;
import net.iGap.realm.RealmMobileBankAccounts;
import net.iGap.viewmodel.mobileBank.MobileBankPayLoanBsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MobileBankPayLoanBsFragment extends BaseBottomSheet {

    private final String LOAN_NUMBER_KEY = "LOAN";
    private final String AMOUNT_KEY = "AMOUNT";
    private MobileBankPayLoanBsViewModel mViewModel;
    private MobileBankPayLoanBsFragmentBinding binding;
    private List<String> items = new ArrayList<>();
    private int mAmount ;
    private String mLoanNumber ;

    public static MobileBankPayLoanBsFragment newInstance( String loan , int amount) {
        MobileBankPayLoanBsFragment fragment = new MobileBankPayLoanBsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(fragment.AMOUNT_KEY, amount);
        bundle.putString(fragment.LOAN_NUMBER_KEY , loan);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater , @Nullable ViewGroup container , @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater , R.layout.mobile_bank_pay_loan_bs_fragment , container , false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(mViewModel);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MobileBankPayLoanBsViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view , @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view , savedInstanceState);
        if(getArguments() != null){
            mAmount = getArguments().getInt(AMOUNT_KEY);
            mLoanNumber = getArguments().getString(LOAN_NUMBER_KEY);
        }
        setupAccountsSpinner();
    }

    private void setupAccountsSpinner() {

        List<RealmMobileBankAccounts> accounts = RealmMobileBankAccounts.getAccounts();
        for (RealmMobileBankAccounts account : accounts) {
            items.add(account.getAccountNumber());
        }

        AccountSpinnerAdapter adapter = new AccountSpinnerAdapter(items, false);
        binding.spDeposits.setAdapter(adapter);
        binding.spDeposits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String number = items.get(position);
                mViewModel.getCustomDeposit().set(number);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

}
