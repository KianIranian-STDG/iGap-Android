package net.iGap.kuknos.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosWHistoryBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosWHistoryM;
import net.iGap.kuknos.service.model.KuknosWalletsAccountM;
import net.iGap.kuknos.view.adapter.WalletHistoryRAdapter;
import net.iGap.kuknos.view.adapter.WalletSpinnerAdapter;
import net.iGap.kuknos.viewmodel.KuknosWHistoryVM;
import net.iGap.libs.bottomNavigation.Util.Utils;

import java.util.List;

public class KuknosWHistoryFrag extends BaseFragment {

    private FragmentKuknosWHistoryBinding binding;
    private KuknosWHistoryVM kuknosWHistoryVM;
    private HelperToolbar mHelperToolbar;

    public static KuknosWHistoryFrag newInstance() {
        KuknosWHistoryFrag kuknosLoginFrag = new KuknosWHistoryFrag();
        return kuknosLoginFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosWHistoryVM = ViewModelProviders.of(this).get(KuknosWHistoryVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_w_history, container, false);
        binding.setViewmodel(kuknosWHistoryVM);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosWHToolbar;
        Utils.darkModeHandler(toolbarLayout);
        toolbarLayout.addView(mHelperToolbar.getView());

        binding.kuknosWHistoryRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.kuknosWHistoryRecycler.setLayoutManager(layoutManager);

        kuknosWHistoryVM.getDataFromServer();

        onError();
        onProgressVisibility();
        onDataChanged();
    }

    private void onDataChanged() {
        kuknosWHistoryVM.getListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<KuknosWHistoryM>>() {
            @Override
            public void onChanged(@Nullable List<KuknosWHistoryM> kuknosWHistoryMS) {
                if (kuknosWHistoryMS.size() != 0) {
                    WalletHistoryRAdapter mAdapter = new WalletHistoryRAdapter(kuknosWHistoryVM.getListMutableLiveData().getValue(), getContext());
                    binding.kuknosWHistoryRecycler.setAdapter(mAdapter);
                }
            }
        });
    }

    private void onError() {
        kuknosWHistoryVM.getErrorM().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
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
        kuknosWHistoryVM.getProgressState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == true) {
                    binding.kuknosWHistoryProgressV.setVisibility(View.VISIBLE);
                }
                else {
                    binding.kuknosWHistoryProgressV.setVisibility(View.GONE);
                }
            }
        });
    }

}
