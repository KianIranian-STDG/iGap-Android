package net.iGap.kuknos.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosWHistoryBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.view.adapter.WalletHistoryRAdapter;
import net.iGap.kuknos.viewmodel.KuknosWHistoryVM;

public class KuknosWHistoryFrag extends BaseFragment {

    private FragmentKuknosWHistoryBinding binding;
    private KuknosWHistoryVM kuknosWHistoryVM;

    public static KuknosWHistoryFrag newInstance() {
        return new KuknosWHistoryFrag();
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

        HelperToolbar mHelperToolbar = HelperToolbar.create()
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
        kuknosWHistoryVM.getListMutableLiveData().observe(getViewLifecycleOwner(), operationResponsePage -> {
            if (operationResponsePage.getOperations().size() != 0) {
                WalletHistoryRAdapter mAdapter = new WalletHistoryRAdapter(kuknosWHistoryVM.getListMutableLiveData().getValue(), getContext());
                binding.kuknosWHistoryRecycler.setAdapter(mAdapter);
            }
        });
    }

    private void onError() {
        kuknosWHistoryVM.getErrorM().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
                defaultRoundDialog.setTitle(getResources().getString(R.string.kuknos_wHistory_dialogTitle))
                        .setMessage(getResources().getString(R.string.kuknos_wHistory_error));
                defaultRoundDialog.setPositiveButton(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack), (dialog, id) -> {
                    //close frag
                    popBackStackFragment();
                });
                defaultRoundDialog.show();
            }
        });
    }

    private void onProgressVisibility() {
        kuknosWHistoryVM.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.kuknosWHistoryProgressV.setVisibility(View.VISIBLE);
            } else {
                binding.kuknosWHistoryProgressV.setVisibility(View.GONE);
            }
        });
    }

}
