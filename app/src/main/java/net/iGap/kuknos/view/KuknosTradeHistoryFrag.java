package net.iGap.kuknos.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosTraceHistoryBinding;
import net.iGap.databinding.FragmentKuknosWHistoryBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosTradeHistoryM;
import net.iGap.kuknos.service.model.KuknosWHistoryM;
import net.iGap.kuknos.view.adapter.WalletHistoryRAdapter;
import net.iGap.kuknos.view.adapter.WalletTradeHistoryAdapter;
import net.iGap.kuknos.viewmodel.KuknosTradeHistoryVM;
import net.iGap.kuknos.viewmodel.KuknosWHistoryVM;
import net.iGap.libs.bottomNavigation.Util.Utils;

import java.util.List;

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
        kuknosTradeHistoryVM.setMode(0);
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
        kuknosTradeHistoryVM.getListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<KuknosTradeHistoryM>>() {
            @Override
            public void onChanged(@Nullable List<KuknosTradeHistoryM> kuknosWHistoryMS) {
                if (kuknosWHistoryMS.size() != 0) {
                    WalletTradeHistoryAdapter mAdapter = new WalletTradeHistoryAdapter(kuknosTradeHistoryVM.getListMutableLiveData().getValue(), 0, getContext());
                    binding.kuknosTradeHistoryRecycler.setAdapter(mAdapter);
                }
            }
        });
    }

    private void onError() {
        kuknosTradeHistoryVM.getErrorM().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true) {
                    DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
                    defaultRoundDialog.setTitle(R.string.kuknos_wHistory_dialogTitle).setMessage(R.string.kuknos_wHistory_error);
                    defaultRoundDialog.setPositiveButton(R.string.kuknos_RecoverySK_Error_Snack, new DialogInterface.OnClickListener() {
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
                }
                else {
                    binding.kuknosTradeHistoryProgressV.setVisibility(View.GONE);
                }
            }
        });
    }

}
