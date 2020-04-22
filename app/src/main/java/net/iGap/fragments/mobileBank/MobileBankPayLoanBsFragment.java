package net.iGap.fragments.mobileBank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

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
    private PayLoanListener listener;

    public static MobileBankPayLoanBsFragment newInstance( String loan , int amount , PayLoanListener loanListener) {
        MobileBankPayLoanBsFragment fragment = new MobileBankPayLoanBsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(fragment.AMOUNT_KEY, amount);
        bundle.putString(fragment.LOAN_NUMBER_KEY , loan);
        fragment.listener = loanListener;
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
            mViewModel.setLoanNumber(mLoanNumber);
            mViewModel.setMaxAmount(mAmount);
        }
        setupAccountsSpinner();
        setupListeners();
    }

    private void setupListeners() {
        mViewModel.getShowRequestErrorMessage().observe(getViewLifecycleOwner() , message->{
            if(message == null) return;

            if(message.equals("-1")){
                Toast.makeText(getContext() , R.string.complete_correct , Toast.LENGTH_SHORT).show();
            }else if(message.equals("-2")){
                Toast.makeText(getContext() , R.string.amount_not_valid , Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getContext() , message , Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.getShowLoader().observe(getViewLifecycleOwner() , state->{
            if(state == null) return;
            binding.btnInquiry.setText(state ? R.string.inquiry : R.string.retry);
        });

        mViewModel.getResponseListener().observe(getViewLifecycleOwner() , msg ->{
            if(msg == null) return;
            dismiss();
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            if (listener != null) listener.updateList();
        });
    }

    private void setupAccountsSpinner() {

        List<RealmMobileBankAccounts> accounts = RealmMobileBankAccounts.getAccounts();
        for (RealmMobileBankAccounts account : accounts) {
            items.add(account.getAccountNumber());
        }

        if(items.size() > 0) mViewModel.getCustomDeposit().set(items.get(0));

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

    public interface PayLoanListener{
        void updateList();
    }

}
