package net.iGap.fragments.mobileBank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import net.iGap.R;
import net.iGap.databinding.MobileBankLoanDetailBinding;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.model.mobileBank.BankServiceLoanDetailModel;
import net.iGap.adapter.mobileBank.MobileBankServiceLoanDetailAdapter;
import net.iGap.viewmodel.mobileBank.MobileBankServiceLoanDetailViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MobileBankServiceLoanDetailFragment extends BaseMobileBankFragment<MobileBankServiceLoanDetailViewModel> {

    private MobileBankLoanDetailBinding binding;
    private MobileBankServiceLoanDetailAdapter adapter;

    private int currentPage = 0;
    private boolean isLastPage = false;
    private int totalPage = 20;
    private boolean isLoading = false;
    private String mLoanNumber;

    public static MobileBankServiceLoanDetailFragment newInstance(String loanNumber) {
        MobileBankServiceLoanDetailFragment frag = new MobileBankServiceLoanDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("loanNumber", loanNumber);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankServiceLoanDetailViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_loan_detail, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        return attachToSwipeBack(binding.getRoot());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        initial();
    }

    private void setupToolbar() {

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setRoundBackground(false)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.Toolbar;
        toolbarLayout.addView(toolbar.getView());

    }

    private void initial() {
        mLoanNumber = getArguments().getString("loanNumber");
        viewModel.setLoanNumber(mLoanNumber);
        viewModel.init();

        // recycler view for bills of month
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager2.setAutoMeasureEnabled(true);
        binding.loansRecycler.setLayoutManager(layoutManager2);
        binding.loansRecycler.setNestedScrollingEnabled(false);
        adapter = new MobileBankServiceLoanDetailAdapter(new ArrayList<>() , new MobileBankServiceLoanDetailAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //show detail
            }

            @Override
            public void onPayLoanClicked(int position , BankServiceLoanDetailModel.LoanItem item) {
                showPayLoanDialog(item);
            }
        });
        binding.loansRecycler.setAdapter(adapter);
        resetMainRecycler();

        // scroll listener for recycler view lazy loading
        binding.container.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = binding.container.getChildAt(binding.container.getChildCount() - 1);
                int diff = (view.getBottom() - (binding.container.getHeight() + binding.container.getScrollY()));
                if (diff == 0) {
                    isLoading = true;
                    currentPage++;
                    viewModel.getLoanDetail(currentPage * 30);
                }
            }
        });

        onDateChangedListener();
    }

    private void showPayLoanDialog(BankServiceLoanDetailModel.LoanItem item) {
        if(getActivity() == null) return;
        MobileBankPayLoanBsFragment fragment = MobileBankPayLoanBsFragment.newInstance(mLoanNumber , item.getUnpaidAmount() , ()->{
            adapter.removeAll();
            viewModel.init();
        });
        fragment.show(getActivity().getSupportFragmentManager() , "PayLoanFragment");
    }

    private void initMainRecycler(List<BankServiceLoanDetailModel.LoanItem> data) {

        adapter.removeLoading();

        if (data == null || data.size() == 0)
            return;

        adapter.addItems(data);

        // check weather is last page or not
        if (currentPage < totalPage) {
            adapter.addLoading();
        } else {
            isLastPage = true;
        }
        isLoading = false;
    }

    private void resetMainRecycler() {

        adapter.removeAll();
        adapter.addLoading();
        currentPage = 0;
        isLastPage = false;

    }

    private void onDateChangedListener() {
        viewModel.getLoan().observe(getViewLifecycleOwner(), new Observer<BankServiceLoanDetailModel>() {
            @Override
            public void onChanged(BankServiceLoanDetailModel data) {
                if (data == null)
                    return;
                initMainRecycler(data.getLoanItems());

                binding.bankCode.setText(getResources().getString(
                        R.string.mobile_bank_load_detail_bankCode,
                        CompatibleUnicode(data.getCustomersInfo().get(0).getBankCode())));
                binding.customerCode.setText(getResources().getString(
                        R.string.mobile_bank_load_detail_customerCode,
                        CompatibleUnicode(data.getCustomersInfo().get(0).getCif())));
                binding.customerName.setText(getResources().getString(
                        R.string.mobile_bank_load_detail_customerName,
                        data.getCustomersInfo().get(0).getTitle() + " " + data.getCustomersInfo().get(0).getName()));

                binding.amount.setText(getResources().getString(
                        R.string.mobile_bank_load_detail_amount,
                        CompatibleUnicode(decimalFormatter(Double.parseDouble("" + data.getAmount()))),
                        getResources().getString(R.string.rial)));
                binding.discount.setText(getResources().getString(
                        R.string.mobile_bank_load_detail_discount,
                        CompatibleUnicode("" + data.getDiscount()) + "%"));
                binding.penalty.setText(getResources().getString(
                        R.string.mobile_bank_load_detail_penalty,
                        CompatibleUnicode("" + data.getPenalty())));

                binding.MUCount.setText(getResources().getString(
                        R.string.mobile_bank_load_detail_MUCount,
                        CompatibleUnicode("" + data.getCountOfMaturedUnpaid())));
                binding.MUAmount.setText(getResources().getString(
                        R.string.mobile_bank_load_detail_MUAmount,
                        CompatibleUnicode(decimalFormatter(Double.parseDouble("" + data.getTotalMaturedUnpaidAmount()))),
                        getResources().getString(R.string.rial)));

                binding.MUCount.setText(getResources().getString(
                        R.string.mobile_bank_load_detail_MUCount,
                        CompatibleUnicode("" + data.getCountOfMaturedUnpaid())));
                binding.MUAmount.setText(getResources().getString(
                        R.string.mobile_bank_load_detail_MUAmount,
                        CompatibleUnicode(decimalFormatter(Double.parseDouble("" + data.getTotalMaturedUnpaidAmount()))),
                        getResources().getString(R.string.rial)));

                binding.paidCount.setText(getResources().getString(
                        R.string.mobile_bank_load_detail_paidCount,
                        CompatibleUnicode("" + data.getCountOfPaid())));
                binding.paidAmount.setText(getResources().getString(
                        R.string.mobile_bank_load_detail_paidAmount,
                        CompatibleUnicode(decimalFormatter(Double.parseDouble("" + data.getTotalPaidAmount()))),
                        getResources().getString(R.string.rial)));

                binding.unpaidCount.setText(getResources().getString(
                        R.string.mobile_bank_load_detail_unpaidCount,
                        CompatibleUnicode("" + data.getCountOfUnpaid())));
                binding.unpaidAmount.setText(getResources().getString(
                        R.string.mobile_bank_load_detail_unpaidAmount,
                        CompatibleUnicode(decimalFormatter(Double.parseDouble("" + data.getTotalUnpaidAmount()))),
                        getResources().getString(R.string.rial)));
            }
        });
    }

    private String decimalFormatter(Double entry) {
        DecimalFormat df = new DecimalFormat(",###");
        return df.format(entry);
    }

    private String CompatibleUnicode(String entry) {
        return HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(entry)) : entry;
    }
}
