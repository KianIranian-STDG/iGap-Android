package net.iGap.mobileBank.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import net.iGap.R;
import net.iGap.databinding.MobileBankHistoryBinding;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.mobileBank.repository.model.BankHistoryModel;
import net.iGap.mobileBank.view.adapter.MobileBankDateAdapter;
import net.iGap.mobileBank.view.adapter.MobileBankHistoryAdapter;
import net.iGap.mobileBank.viewmodel.MobileBankCardHistoryViewModel;

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

    private static final String TAG = "Amini";

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

        initial();
    }

    private void initial() {
        String number = getArguments().getString("accountNum");
        boolean isCard = getArguments().getBoolean("isCard");

        viewModel.setDepositNumber(number);
        viewModel.setCard(isCard);
        viewModel.init();

        //set current account data
        binding.tvNumber.setText(checkAndSetPersianNumberIfNeeded(number, isCard));
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
        binding.container.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = binding.container.getChildAt(binding.container.getChildCount() - 1);
                int diff = (view.getBottom() - (binding.container.getHeight() + binding.container.getScrollY()));
                if (diff == 0) {
                    isLoading = true;
                    currentPage++;
                    Log.d(TAG, "loadMoreItems: Load more Bitch!!!");
                    viewModel.getAccountDataForMonth(currentPage * 30);
                }
            }
        });

        // open page for reports
        binding.reportBtn.setOnClickListener(v -> {
            viewModel.getShowRequestErrorMessage().setValue("Will be added SOON!");
            showDateSelectorDialog();
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

    private void showDateSelectorDialog() {
        /*Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "IRANSansMobile.ttf");
        PersianCalendar initDate = new PersianCalendar();
        PersianDatePickerDialog picker = new PersianDatePickerDialog(getContext())
                .setMinYear(1300)
                .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
                .setTypeFace(typeface)
                .setPositiveButtonResource(R.string.select)
                .setNegativeButtonResource(R.string.cancel)
                .setTodayButtonResource(R.string.today)
                .setTodayButtonVisible(true)
                .setInitDate(initDate)
                .setTitleColor(new Theme().getButtonTextColor(getContext()))
                .setActionTextColor(new Theme().getButtonTextColor(getContext()))
                .setBackgroundColor(new Theme().getRootColor(getContext()))
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)
                .setListener(new Listener() {
                    @Override
                    public void onDateSelected(PersianCalendar persianCalendar) {
                    }

                    @Override
                    public void onDismissed() {
                    }
                });
        picker.show();*/
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
    }

    private String checkAndSetPersianNumberIfNeeded(String cardNumber, boolean isCard) {
        String number = cardNumber;
        if (HelperCalander.isPersianUnicode)
            number = HelperCalander.convertToUnicodeFarsiNumber(cardNumber);
        if (isCard) {
            try {
                String[] tempArray = Iterables.toArray(Splitter.fixedLength(4).split(number), String.class);
                return tempArray[0] + " - " + tempArray[1] + " - " + tempArray[2] + " - " + tempArray[3];
            } catch (Exception e) {
                return number;
            }
        } else {
            return number;
        }
    }

}
