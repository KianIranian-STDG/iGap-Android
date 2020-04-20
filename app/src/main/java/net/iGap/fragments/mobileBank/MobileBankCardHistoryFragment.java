package net.iGap.fragments.mobileBank;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;

import net.iGap.R;
import net.iGap.adapter.mobileBank.AccountSpinnerAdapter;
import net.iGap.adapter.mobileBank.BankHomeItemAdapter;
import net.iGap.adapter.mobileBank.MobileBankDateAdapter;
import net.iGap.adapter.mobileBank.MobileBankHistoryAdapter;
import net.iGap.databinding.MobileBankHistoryBinding;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.mobileBank.BankHistoryModel;
import net.iGap.model.mobileBank.MobileBankHomeItemsModel;
import net.iGap.module.dialog.DialogParsian;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.realm.RealmMobileBankAccounts;
import net.iGap.realm.RealmMobileBankCards;
import net.iGap.viewmodel.mobileBank.MobileBankCardHistoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class MobileBankCardHistoryFragment extends BaseMobileBankFragment<MobileBankCardHistoryViewModel> {

    private MobileBankHistoryBinding binding;
    private LinearSnapHelper snapHelper;

    private int currentPage = 0;
    private boolean isLastPage = false;
    private int totalPage = 20;
    private boolean isLoading = false;
    private MobileBankHistoryAdapter adapter;
    private String mCurrentNumber;
    private boolean isCard;
    private DialogParsian mDialogWait;
    private List<Pair<String, String>> items = new ArrayList<>();
    private ViewTreeObserver.OnScrollChangedListener listener;
    private int lastPos = -1;

    public static MobileBankCardHistoryFragment newInstance(String accountNumber, boolean isCard) {
        MobileBankCardHistoryFragment frag = new MobileBankCardHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("accountNum", accountNumber);
        bundle.putBoolean("isCard", isCard);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankCardHistoryViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_history, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        return attachToSwipeBack(binding.getRoot());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() == null) popBackStackFragment();
        mCurrentNumber = getArguments().getString("accountNum");
        isCard = getArguments().getBoolean("isCard");

        setupToolbar();
        initial();
        setupListener();
        setupRecyclerItems();
        setupSpinner();
    }

    private void setupSpinner() {
        List<Pair<String, String>> allAccounts = getAccountsItem();
        List<String> itemsSpinner = new ArrayList<>();
        for (int i = 0; i < allAccounts.size(); i++) {
            itemsSpinner.add(allAccounts.get(i).first);
        }

        AccountSpinnerAdapter adapter = new AccountSpinnerAdapter(itemsSpinner, isCard);
        binding.spAccounts.setAdapter(adapter);
        getCheckAndSetCurrentSelection(itemsSpinner);
        binding.spAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (isCard) {
                    if (items.get(position).second != null && items.get(position).second.equals("HOT")) {
                        HelperError.showSnackMessage(getString(R.string.card_is_disable), false);
                        return;
                    }
                } else {
                    if (items.get(position).second != null && !items.get(position).second.equals("OPEN") && !items.get(position).second.equals("OPENING")) {
                        HelperError.showSnackMessage(getString(R.string.card_is_disable), false);
                        return;
                    }
                }
                String number = items.get(position).first;
                binding.tvNumber.setText(checkAndSetPersianNumberIfNeeded(number, isCard));
                viewModel.getBalance().set("...");
                if (getArguments() != null) getArguments().putString("accountNum", number);
                mCurrentNumber = number;
                if (isCard) {
                    showProgress();
                    viewModel.getCardDeposits(number);
                    return;
                }
                reloadPage(number);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.lytAccounts.setOnClickListener(v -> binding.spAccounts.performClick());

    }

    private void getCheckAndSetCurrentSelection(List<String> accounts) {
        for (int i = 0; i < accounts.size(); i++) {
            if (mCurrentNumber.equals(accounts.get(i))) {
                binding.spAccounts.setSelection(i);
                break;
            }
        }
    }

    private void reloadPage(String number) {
        resetMainRecycler();
        viewModel.setDepositNumber(number);
        viewModel.getAccountDataForMonth(0);
    }

    private List<Pair<String, String>> getAccountsItem() {
        items = new ArrayList<>();
        if (isCard) {
            List<RealmMobileBankCards> cards = RealmMobileBankCards.getCards();
            for (RealmMobileBankCards card : cards) {
                items.add(new Pair<>(card.getCardNumber(), card.getStatus()));
            }
        } else {
            List<RealmMobileBankAccounts> accounts = RealmMobileBankAccounts.getAccounts();
            for (RealmMobileBankAccounts account : accounts) {
                items.add(new Pair<>(account.getAccountNumber(), account.getStatus()));
            }
        }

        return items;
    }

    private void setupToolbar() {

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setRoundBackground(false)
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.Toolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

    }

    private void initial() {

        viewModel.setDepositNumber(mCurrentNumber);
        viewModel.setCard(isCard);
        viewModel.init();

        //set current account data
        binding.tvNumber.setText(checkAndSetPersianNumberIfNeeded(mCurrentNumber, isCard));
        binding.tvType.setText(isCard ? R.string.card_number : R.string.account);

        // recycler view for date
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.timeRecycler.setHasFixedSize(true);
        binding.timeRecycler.setLayoutManager(layoutManager);
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.timeRecycler);

        // recycler view for bills of month
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager2.setAutoMeasureEnabled(true);
        binding.billsRecycler.setLayoutManager(layoutManager2);
        binding.billsRecycler.setNestedScrollingEnabled(false);
        adapter = new MobileBankHistoryAdapter(new ArrayList<>(), position -> {
            // show detail in dialog
            showMessage(getResources().getString(R.string.info), adapter.getItem(position).getDescription());
        });
        binding.billsRecycler.setAdapter(adapter);
        resetMainRecycler();

        // scroll listener for recycler view lazy loading
        listener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = binding.container.getChildAt(binding.container.getChildCount() - 1);
                int diff = (view.getBottom() - (binding.container.getHeight() + binding.container.getScrollY()));
                if (diff == 0 && binding.container.getScrollY() != 0 && lastPos != view.getBottom()) {
                    isLoading = true;
                    currentPage = currentPage + 1;
                    viewModel.getAccountDataForMonth(currentPage * 30);
                    lastPos = view.getBottom();
                }
            }
        };
        binding.container.getViewTreeObserver().addOnScrollChangedListener(listener);

        // open page for reports
        binding.reportBtn.setOnClickListener(v -> {
            showMessage(binding.reportBtn.getContext().getString(R.string.financial_report), binding.reportBtn.getContext().getString(R.string.soon));
        });

        onDateChangedListener();
    }

    private void showMessage(String title, String message) {
        new DialogParsian()
                .setContext(getContext())
                .setTitle(title)
                .setButtonsText(getString(R.string.ok), null)
                .showSimpleMessage(message);
    }

    private void initMainRecycler(List<BankHistoryModel> data) {

        if (data == null)
            return;

        adapter.removeLoading();

        if (data.size() == 0)
            return;

        adapter.addItems(data);

        // check weather is last page or not
        if (currentPage < totalPage && data.size() >= 30) {
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
        lastPos = -1;
        isLastPage = false;

    }

    private void onDateChangedListener() {
        viewModel.getCalender().observe(getViewLifecycleOwner(), bankDateModels -> {
            MobileBankDateAdapter adapter = new MobileBankDateAdapter(viewModel.getCalender().getValue(), position -> {
                int centerOfScreen = binding.timeRecycler.getWidth() / 2 - snapHelper.findSnapView(binding.timeRecycler.getLayoutManager()).getWidth() / 2;
                ((LinearLayoutManager) binding.timeRecycler.getLayoutManager()).scrollToPositionWithOffset(position, centerOfScreen);
                // get data from server
                viewModel.setDatePosition(position);
                viewModel.getAccountDataForMonth(0);
                resetMainRecycler();
            });
            binding.timeRecycler.setAdapter(adapter);
        });

        viewModel.getBills().observe(getViewLifecycleOwner(), this::initMainRecycler);

        viewModel.getShowRequestErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg == null) return;
            HelperError.showSnackMessage(msg, false);
        });
    }

    private String checkAndSetPersianNumberIfNeeded(String cardNumber, boolean isCard) {
        String number = cardNumber;
        if (HelperCalander.isPersianUnicode)
            number = HelperCalander.convertToUnicodeFarsiNumber(cardNumber);
        return number;
     /*   if (isCard) {
            try {
                String[] tempArray = Iterables.toArray(Splitter.fixedLength(4).split(cardNumber), String.class);
                return HelperCalander.convertToUnicodeFarsiNumber( tempArray[0] + " - " + tempArray[1] + " - " + tempArray[2] + " - " + tempArray[3]);
            } catch (Exception e) {
                return number;
            }
        } else {
            return number;
        }*/
    }


    private void setupRecyclerItems() {
        List<MobileBankHomeItemsModel> items;
        items = isCard ? getCardRecyclerItems() : getDepositRecyclerItems();

        BankHomeItemAdapter adapter = new BankHomeItemAdapter();
        adapter.setItems(items);
        adapter.setListener(this::handleItemsAdapterClick);

        binding.rvItems.setNestedScrollingEnabled(false);
        binding.rvItems.setAdapter(adapter);

    }

    private void handleItemsAdapterClick(int position, int title) {
        switch (title) {
            case R.string.transfer_mony:
            case R.string.cardToCardBtnText:
                showComingSoon();
                //onTransferMoneyClicked();
                break;

            case R.string.sheba_number:
                onShebaClicked();
                break;

            case R.string.temporary_password:
                if (isCard)
                    getOTP(mCurrentNumber);
                break;

            case R.string.cheque:
                openChequeListPage(mCurrentNumber);
                break;

            case R.string.mobile_bank_hotCard:
                openHotCard(mCurrentNumber);
                break;
        }
    }

    private void openChequeListPage(String deposit) {
        if (getActivity() != null) {
            new HelperFragment(getActivity().getSupportFragmentManager(), MobileBankChequesBookListFragment.newInstance(deposit)).setReplace(false).load();
        }
    }

    private List<MobileBankHomeItemsModel> getCardRecyclerItems() {
        List<MobileBankHomeItemsModel> items = new ArrayList<>();
        items.add(new MobileBankHomeItemsModel(R.string.cardToCardBtnText, R.drawable.ic_mb_card_to_card));
        //items.add(new MobileBankHomeItemsModel(R.string.sheba_number, R.drawable.ic_mb_sheba));
        items.add(new MobileBankHomeItemsModel(R.string.temporary_password, R.drawable.ic_mb_pooya_pass));
        items.add(new MobileBankHomeItemsModel(R.string.mobile_bank_hotCard, R.drawable.ic_mb_block));
        return items;
    }

    private List<MobileBankHomeItemsModel> getDepositRecyclerItems() {
        List<MobileBankHomeItemsModel> items = new ArrayList<>();
        items.add(new MobileBankHomeItemsModel(R.string.transfer_mony, R.drawable.ic_mb_transfer));
        items.add(new MobileBankHomeItemsModel(R.string.sheba_number, R.drawable.ic_mb_sheba));
        items.add(new MobileBankHomeItemsModel(R.string.cheque, R.drawable.ic_mb_cheque));
        return items;
    }

    private void setupListener() {

        viewModel.getOTPmessage().observe(getViewLifecycleOwner(), message -> {
            if (message.equals("-1")) {
                mDialogWait.dismiss();
            } else {
                showMessage(getString(R.string.attention), message);
            }
        });


        viewModel.getCardDepositResponse().observe(getViewLifecycleOwner(), deposit -> {
            mDialogWait.dismiss();
            if (deposit != null && !deposit.equals("-1")) {
                reloadPage(deposit);
            }
        });
    }

    private void showProgress() {
        if (getActivity() != null) {
            if (mDialogWait == null) {
                mDialogWait = new DialogParsian()
                        .setContext(getActivity())
                        .setTitle(getString(R.string.please_wait) + "..")
                        .setButtonsText(null, getString(R.string.cancel))
                        .setListener(new DialogParsian.ParsianDialogListener() {
                            @Override
                            public void onDeActiveButtonClicked(Dialog dialog) {
                                mDialogWait.dismiss();
                            }
                        });
            }
            mDialogWait.showLoaderDialog(false);
        }
    }

    private void getOTP(String cardNumber) {
        if (cardNumber == null)
            return;
        showProgress();
        viewModel.getOTP(cardNumber);
    }

    private void onTransferMoneyClicked() {

        if (getActivity() != null && !isCard) {
            new DialogParsian()
                    .setContext(getActivity())
                    .setTitle(getString(R.string.transfer_mony))
                    .setButtonsText(getString(R.string.ok), getString(R.string.cancel))
                    .setListener(new DialogParsian.ParsianDialogListener() {
                        @Override
                        public void onActiveButtonClicked(Dialog dialog) {

                        }

                        @Override
                        public void onTransferMoneyTypeSelected(Dialog dialog, String type) {
                            dialog.dismiss();
                        }
                    }).showMoneyTransferDialog();
        } else {
            openTransferMoneyPage();
        }

    }

    private void openTransferMoneyPage() {
        if (getActivity() == null) return;
        new HelperFragment(getActivity().getSupportFragmentManager(), MobileBankTransferCTCStepOneFragment.newInstance(mCurrentNumber))
                .setReplace(false)
                .load();
    }

    private void onShebaClicked() {

        if (getActivity() != null) {
            MobileBankBottomSheetFragment fragment = MobileBankBottomSheetFragment.newInstance(mCurrentNumber, isCard);
            fragment.show(getActivity().getSupportFragmentManager(), "Sheba");
        }

    }

    private void openHotCard(String cardNumber) {
        MobileBankCardBalanceBottomSheetFrag frag = MobileBankCardBalanceBottomSheetFrag.newInstance(cardNumber, "HOT_CARD");
        frag.setCompleteListener((cardNumber1, balance) -> {
            String message;
            if (balance.equals("success"))
                message = getResources().getString(R.string.mobile_bank_hotCard_success);
            else
                message = getResources().getString(R.string.mobile_bank_hotCard_fail);
            new DialogParsian()
                    .setContext(getContext())
                    .setTitle(getString(R.string.mobile_bank_hotCard))
                    .setButtonsText(getString(R.string.ok), null)
                    .showSimpleMessage(message);
        });
        frag.show(getParentFragmentManager(), "CardBalanceBottomSheet");
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.container.getViewTreeObserver().removeOnScrollChangedListener(listener);
    }
}
