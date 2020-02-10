package net.iGap.mobileBank.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import net.iGap.R;
import net.iGap.databinding.MobileBankHomeTabFragmentBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.mobileBank.repository.db.RealmMobileBankAccounts;
import net.iGap.mobileBank.repository.db.RealmMobileBankCards;
import net.iGap.mobileBank.repository.model.BankAccountModel;
import net.iGap.mobileBank.repository.model.BankCardModel;
import net.iGap.mobileBank.repository.model.MobileBankHomeItemsModel;
import net.iGap.mobileBank.view.adapter.BankAccountsAdapter;
import net.iGap.mobileBank.view.adapter.BankCardsAdapter;
import net.iGap.mobileBank.view.adapter.BankHomeItemAdapter;
import net.iGap.mobileBank.viewmodel.MobileBankHomeTabViewModel;

import java.util.ArrayList;
import java.util.List;

public class MobileBankHomeTabFragment extends BaseMobileBankFragment<MobileBankHomeTabViewModel> {

    private MobileBankHomeTabFragmentBinding binding;
    private HomeTabMode mode;
    private DialogParsian mDialogWait;

    public static MobileBankHomeTabFragment newInstance(HomeTabMode mode) {
        MobileBankHomeTabFragment fragment = new MobileBankHomeTabFragment();
        fragment.mode = mode;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_home_tab_fragment, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankHomeTabViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.setFragmentState(mode);
        disableViewPagerIfNeed();
        setupListener();
        if (mode == HomeTabMode.SERVICE) setupRecyclerItems();
    }

    private void setupRecyclerItems() {
        List<MobileBankHomeItemsModel> items = new ArrayList<>();
        switch (mode) {
            case CARD:
                items = getCardRecyclerItems();
                break;

            case DEPOSIT:
                items = getDepositRecyclerItems();
                break;

            case SERVICE:
                items = getServiceRecyclerItems();
                break;
        }

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
                onTransferMoneyClicked();
                break;

            case R.string.Inventory:
            case R.string.transactions:
                onTransactionsClicked();
                break;

            case R.string.sheba_number:
                onShebaClicked();
                break;

            case R.string.temporary_password:
                if (mode == HomeTabMode.CARD)
                    getOTP(getCurrentAccount());
                break;

            case R.string.cheque:
                openChequeListPage(getCurrentAccount());
                break;

            case R.string.facilities:
                onFacilitiesClick();
                break;

            /*case R.string.Inventory:
                if (mode == HomeTabMode.CARD)
                    openGetCardInfo(getCurrentAccount());
                else {
                    getAcountBalance(getCurrentAccount());
                }
                break;*/

            case R.string.mobile_bank_hotCard:
                openHotCard(getCurrentAccount());
                break;
        }
    }

    private void onFacilitiesClick() {
        if (getActivity() != null) {
            new HelperFragment(getActivity().getSupportFragmentManager(), MobileBankLoansFragment.newInstance())
                    .setReplace(false)
                    .load();

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
        items.add(new MobileBankHomeItemsModel(R.string.Inventory, R.drawable.ic_mb_balance));
        items.add(new MobileBankHomeItemsModel(R.string.transactions, R.drawable.ic_mb_transaction));
        items.add(new MobileBankHomeItemsModel(R.string.sheba_number, R.drawable.ic_mb_sheba));
        items.add(new MobileBankHomeItemsModel(R.string.temporary_password, R.drawable.ic_mb_pooya_pass));
        items.add(new MobileBankHomeItemsModel(R.string.mobile_bank_hotCard, R.drawable.ic_mb_block));
        return items;
    }

    private List<MobileBankHomeItemsModel> getDepositRecyclerItems() {
        List<MobileBankHomeItemsModel> items = new ArrayList<>();
        items.add(new MobileBankHomeItemsModel(R.string.transfer_mony, R.drawable.ic_mb_transfer));
        items.add(new MobileBankHomeItemsModel(R.string.Inventory, R.drawable.ic_mb_balance));
        items.add(new MobileBankHomeItemsModel(R.string.transactions, R.drawable.ic_mb_transaction));
        items.add(new MobileBankHomeItemsModel(R.string.sheba_number, R.drawable.ic_mb_sheba));
        items.add(new MobileBankHomeItemsModel(R.string.cheque, R.drawable.ic_mb_cheque));
        return items;
    }

    private List<MobileBankHomeItemsModel> getServiceRecyclerItems() {
        List<MobileBankHomeItemsModel> items = new ArrayList<>();
        items.add(new MobileBankHomeItemsModel(R.string.facilities, R.drawable.ic_mb_loan));
        return items;
    }

    private void disableViewPagerIfNeed() {
        if (mode == HomeTabMode.SERVICE) {
            binding.vpCards.setVisibility(View.GONE);
            binding.lytIndicators.setVisibility(View.GONE);
        }
    }

    private void setupListener() {

        viewModel.getCardsData().observe(getViewLifecycleOwner(), bankCardModels -> {
            if (bankCardModels != null) saveCardsTodb(bankCardModels);
        });

        viewModel.getAccountsData().observe(getViewLifecycleOwner(), bankAccountModels -> {
            if (bankAccountModels != null) saveAccountsTodb(bankAccountModels);
        });

        viewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {
            if (balance.equals("-1")) {
                mDialogWait.dismiss();
                viewModel.getShowRequestErrorMessage().setValue(getResources().getString(R.string.mobile_bank_balance_error_no_tran));
            } else {
                showMessage(getString(R.string.mobile_bank_balance_title),
                        getString(R.string.mobile_bank_balance_message, balance + " " + getString(R.string.rial)));
            }
        });

        viewModel.getOTPmessage().observe(getViewLifecycleOwner(), message -> {
            if (message.equals("-1")) {
                mDialogWait.dismiss();
            } else {
                showMessage(getString(R.string.attention), message);
            }
        });
    }

    private void showMessage(String title, String message) {
        mDialogWait.dismiss();
        new DialogParsian()
                .setContext(getContext())
                .setTitle(title)
                .setButtonsText(getString(R.string.ok), null)
                .showSimpleMessage(message);
    }

    private void showProgress() {
        if (getActivity() != null) {
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
            mDialogWait.showLoaderDialog(false);
        }
    }

    private void getAcountBalance(String depositNumber) {
        if (depositNumber == null)
            return;
        showProgress();
        viewModel.getAccountBalance(depositNumber);
    }

    private void getOTP(String cardNumber) {
        if (cardNumber == null)
            return;
        showProgress();
        viewModel.getOTP(cardNumber);
    }

    private void onTransferMoneyClicked() {

        if (getActivity() != null && mode == HomeTabMode.DEPOSIT) {
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
        new HelperFragment(getActivity().getSupportFragmentManager(), MobileBankTransferCTCStepOneFragment.newInstance(getCurrentAccount()))
                .setReplace(false)
                .load();
    }

    private void onTransactionsClicked() {

        if (getActivity() != null) {
            new HelperFragment(getActivity().getSupportFragmentManager(), MobileBankCardHistoryFragment.newInstance(getCurrentAccount(), mode == HomeTabMode.CARD))
                    .setReplace(false)
                    .load();
        }

    }

    private void onLoanClicked() {

        if (getActivity() != null) {
            new HelperFragment(getActivity().getSupportFragmentManager(),
                    MobileBankServiceLoanDetailFragment.newInstance("58000001529602"))
                    .setReplace(false)
                    .load();
        }

    }

    private void onShebaClicked() {
        if (getActivity() != null) {
            MobileBankBottomSheetFragment fragment = MobileBankBottomSheetFragment.newInstance(getCurrentAccount(), mode == HomeTabMode.CARD);
            fragment.show(getActivity().getSupportFragmentManager(), "Sheba");
        }
    }

    private String getCurrentAccount() {
        if (mode == HomeTabMode.CARD) {
            if (viewModel.cards != null && viewModel.cards.size() > 0) {
                int current = binding.vpCards.getCurrentItem();
                if (current < viewModel.cards.size()) {
                    return viewModel.cards.get(current).getPan();
                }
            } else {
                Toast.makeText(getActivity(), R.string.no_item_selected, Toast.LENGTH_SHORT).show();
            }
        } else if (mode == HomeTabMode.DEPOSIT) {
            if (viewModel.accounts != null && viewModel.accounts.size() > 0) {
                int current = binding.vpCards.getCurrentItem();
                if (current < viewModel.accounts.size()) {
                    return viewModel.accounts.get(current).getAccountNumber();
                }
            } else {
                Toast.makeText(getActivity(), R.string.no_item_selected, Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    private void saveCardsTodb(List<BankCardModel> bankCardModels) {
        RealmMobileBankCards.putOrUpdate(bankCardModels, this::setupViewPagerCards);
    }

    private void saveAccountsTodb(List<BankAccountModel> bankAccountModels) {
        RealmMobileBankAccounts.putOrUpdate(bankAccountModels, this::setupViewPagerDeposits);
    }

    private void setupViewPagerDeposits() {
        List<RealmMobileBankAccounts> accounts = new ArrayList<>(RealmMobileBankAccounts.getAccounts());
        if (accounts.size() == 0) return;
        binding.vpCards.setAdapter(new BankAccountsAdapter(accounts));
        binding.vpCards.setOffscreenPageLimit(accounts.size() - 1);
        initViewPager(accounts.size());
        setupRecyclerItems();
    }

    private void setupViewPagerCards() {
        List<RealmMobileBankCards> cards = new ArrayList<>(RealmMobileBankCards.getCards());
        if (cards.size() == 0) return;
        binding.vpCards.setAdapter(new BankCardsAdapter(cards));
        binding.vpCards.setOffscreenPageLimit(cards.size() - 1);
        initViewPager(cards.size());
        setupRecyclerItems();
    }

    private void initViewPager(int size) {

        binding.vpCards.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setEnableIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        createViewPagerIndicators(size);
    }

    private void createViewPagerIndicators(int size) {
        for (int i = 0; i < size; i++) {
            ImageView iv = new ImageView(binding.lytIndicators.getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(24, 24);
            lp.setMargins(8, 8, 8, 8);
            iv.setLayoutParams(lp);
            iv.setBackgroundResource(R.drawable.indicator_slider);
            if (i == 0) iv.setSelected(true);
            binding.lytIndicators.addView(iv);
        }
    }

    private void setEnableIndicator(int position) {
        int count = binding.lytIndicators.getChildCount();
        if (position > count) return;
        for (int i = 0; i < count; i++) {
            binding.lytIndicators.getChildAt(i).setSelected(i == position);
        }
    }

    private void openGetCardInfo(String cardNumber) {
        MobileBankCardBalanceBottomSheetFrag frag = MobileBankCardBalanceBottomSheetFrag.newInstance(cardNumber, "BALANCE");
        frag.setCompleteListener((cardNumber1, balance) ->
                new DialogParsian()
                        .setContext(getContext())
                        .setTitle(getString(R.string.mobile_bank_balance_title))
                        .setButtonsText(getString(R.string.ok), null)
                        .showSimpleMessage(getString(R.string.mobile_bank_balance_message, balance + " " + getString(R.string.rial))));
        frag.show(getFragmentManager(), "CardBalanceBottomSheet");
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
        frag.show(getFragmentManager(), "CardBalanceBottomSheet");
    }

    public enum HomeTabMode {
        CARD, DEPOSIT, SERVICE
    }
}
