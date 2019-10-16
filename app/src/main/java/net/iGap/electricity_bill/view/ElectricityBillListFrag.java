package net.iGap.electricity_bill.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentElecBillListBinding;
import net.iGap.electricity_bill.repository.model.Bill;
import net.iGap.electricity_bill.view.adapter.ElectricityBillListAdapter;
import net.iGap.electricity_bill.viewmodel.ElectricityBillListVM;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

import java.util.List;

public class ElectricityBillListFrag extends BaseAPIViewFrag {

    public enum btnActions {ADD_NEW_BILL, DELETE_ACCOUNT}

    private FragmentElecBillListBinding binding;
    private ElectricityBillListVM elecBillVM;
    private static final String TAG = "ElectricityBillListFrag";

    public static ElectricityBillListFrag newInstance() {
        ElectricityBillListFrag kuknosLoginFrag = new ElectricityBillListFrag();
        return kuknosLoginFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        elecBillVM = ViewModelProviders.of(this).get(ElectricityBillListVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_elec_bill_list, container, false);
        binding.setViewmodel(elecBillVM);
        binding.setFragment(this);
        binding.setLifecycleOwner(this);
        this.viewModel = elecBillVM;

        return attachToSwipeBack(binding.getRoot());

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

        LinearLayout toolbarLayout = binding.Toolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        binding.billRecycler.setHasFixedSize(true);
        onDataChangedListener();
        elecBillVM.getData();
    }

    private void onDataChangedListener() {
        elecBillVM.getmData().observe(getViewLifecycleOwner(), this::initRecycler);
    }

    private void initRecycler(List<Bill> bills) {
        ElectricityBillListAdapter adapter = new ElectricityBillListAdapter(getContext(), bills, new ElectricityBillListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, Actoin btnAction) {
                Log.d(TAG, "onClick: ");
            }
        });
        binding.billRecycler.setAdapter(adapter);
    }

    public void onAddNewBillBtnClick() {
        onBtnClickManger(btnActions.ADD_NEW_BILL);
    }

    public void onDeleteAccountBtnClick() {
        onBtnClickManger(btnActions.DELETE_ACCOUNT);
    }


    private void onBtnClickManger(btnActions actions) {
        switch (actions) {
            case ADD_NEW_BILL:
                break;
            case DELETE_ACCOUNT:
                break;
        }
    }

}
