package net.iGap.fragments.mobileBank;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.mobileBank.MobileBankChequeListAdapter;
import net.iGap.databinding.MobileBankChequeListFragmentBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.mobileBank.BankChequeSingle;
import net.iGap.module.dialog.DialogParsian;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.mobileBank.MobileBankChequesListViewModel;

import java.util.List;

public class MobileBankChequesListFragment extends BaseMobileBankFragment<MobileBankChequesListViewModel> {

    private MobileBankChequeListFragmentBinding binding;
    private MobileBankChequeListAdapter adapter;
    private DialogParsian mDialogWait;

    public static MobileBankChequesListFragment newInstance(String depositNumber, String bookNumber) {
        MobileBankChequesListFragment fragment = new MobileBankChequesListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("depositNumber", depositNumber);
        bundle.putString("bookNumber", bookNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.mobile_bank_cheque_list_fragment, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankChequesListViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView();
        setupListeners();
        viewModel.setBookNumber(getArguments().getString("bookNumber"));
        viewModel.setDeposit(getArguments().getString("depositNumber"));
        viewModel.init();
    }

    private void setupView() {
        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(binding.Toolbar.getContext())
                .setLogoShown(true)
                .setRoundBackground(false)
                .setDefaultTitle(getString(R.string.cheque))
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });
        binding.Toolbar.addView(toolbar.getView());

        binding.pullToRefresh.setOnRefreshListener(() -> {
            adapter.removeAll();
            viewModel.init();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.rcCheque.setLayoutManager(layoutManager);

        adapter = new MobileBankChequeListAdapter(null, new MobileBankChequeListAdapter.OnItemClickListener() {
            @Override
            public void onBlock(int position) {
                viewModel.blockCheques(adapter.getItem(position).getNumber());
            }

            @Override
            public void onCustomizeClicked(int position) {
                showDialogInputAmount(adapter.getItem(position).getNumber(), position);
            }
        });
        binding.rcCheque.setAdapter(adapter);
    }

    private void showDialogInputAmount(String number, int position) {
        final int pos = position;
        if (getActivity() == null) return;
        new DialogParsian()
                .setContext(getActivity())
                .setTitle(getString(R.string.inter_cheque_amount))
                .setButtonsText(getString(R.string.confirm), getString(R.string.cancel))
                .setListener(new DialogParsian.ParsianDialogListener() {
                    @Override
                    public void onDeActiveButtonClicked(Dialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onInputDialogListener(Dialog dialog, String input) {
                        if (input != null && !input.isEmpty() && Long.valueOf(input) > 1) {
                            dialog.dismiss();
                            viewModel.getRegisterCheque(number, Long.valueOf(input), pos);
                        } else {
                            viewModel.getShowRequestErrorMessage().postValue(getString(R.string.amount_not_valid));
                        }
                    }
                }).showInputDialog(getString(R.string.amount), InputType.TYPE_CLASS_NUMBER);
    }

    private void showMessage(String title, String message) {
        mDialogWait.dismiss();
        new DialogParsian()
                .setContext(getContext())
                .setTitle(title)
                .setButtonsText(getString(R.string.ok), null)
                .showSimpleMessage(message);
    }

    private void showDialogLoading() {
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

    private void setupListeners() {

        viewModel.getResponseListener().observe(getViewLifecycleOwner(), this::setupRecyclerView);

        viewModel.getRegisterChequeLoader().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            if (state) {
                showDialogLoading();
            } else {
                if (mDialogWait != null) mDialogWait.dismiss();
            }
        });

        viewModel.getRegisterChequeListener().observe(getViewLifecycleOwner(), msg -> {
            if (msg == null) return;
            if (mDialogWait != null) mDialogWait.dismiss();
            showMessage(getString(R.string.attention), msg.first);
            if (msg.second >= 0) {
                adapter.removeAll();
                viewModel.init();
            }
        });
    }

    private void setupRecyclerView(List<BankChequeSingle> chequeModels) {
        binding.pullToRefresh.setRefreshing(false);
        adapter.addItems(chequeModels);
    }
}
