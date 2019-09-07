package net.iGap.kuknos.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosTraceHistoryBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.fragments.BaseFragment;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.view.adapter.WalletTradeHistoryAdapter;
import net.iGap.kuknos.viewmodel.KuknosTradeHistoryVM;

public class KuknosTradeHistoryFrag extends BaseFragment {

    private FragmentKuknosTraceHistoryBinding binding;
    private KuknosTradeHistoryVM kuknosTradeHistoryVM;

    public static KuknosTradeHistoryFrag newInstance() {
        KuknosTradeHistoryFrag kuknosLoginFrag = new KuknosTradeHistoryFrag();
        return kuknosLoginFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosTradeHistoryVM = ViewModelProviders.of(this).get(KuknosTradeHistoryVM.class);
        kuknosTradeHistoryVM.setMode(KuknosTradeHistoryVM.API.TRADES_LIST);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_trace_history, container, false);
        binding.setViewmodel(kuknosTradeHistoryVM);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        binding.kuknosTradeHistoryRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.kuknosTradeHistoryRecycler.setLayoutManager(layoutManager);

        kuknosTradeHistoryVM.getDataFromServer();

        onError();
        onProgressVisibility();
        onDataChanged();
    }

    private void onDataChanged() {
        kuknosTradeHistoryVM.getListMutableLiveData().observe(getViewLifecycleOwner(), offerResponsePage -> {
            if (offerResponsePage.getRecords().size() != 0) {
                WalletTradeHistoryAdapter mAdapter = new WalletTradeHistoryAdapter(offerResponsePage.getRecords(), 0, getContext());
                binding.kuknosTradeHistoryRecycler.setAdapter(mAdapter);
            } else {
                binding.kuknosTradeHistoryNOitem.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onError() {
        kuknosTradeHistoryVM.getErrorM().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true) {
                    DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
                    defaultRoundDialog.setTitle(getResources().getString(R.string.kuknos_wHistory_dialogTitle))
                            .setMessage(getResources().getString(R.string.kuknos_wHistory_error));
                    defaultRoundDialog.setPositiveButton(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //close frag
                            popBackStackFragment();
                        }
                    });
                    defaultRoundDialog.show();
                }
            }
        });
    }

    private void onProgressVisibility() {
        kuknosTradeHistoryVM.getProgressState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == true) {
                    binding.kuknosTradeHistoryProgressV.setVisibility(View.VISIBLE);
                } else {
                    binding.kuknosTradeHistoryProgressV.setVisibility(View.GONE);
                }
            }
        });
    }

}
