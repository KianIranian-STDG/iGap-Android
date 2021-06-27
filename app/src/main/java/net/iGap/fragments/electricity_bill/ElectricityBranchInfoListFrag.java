package net.iGap.fragments.electricity_bill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.adapter.electricity_bill.ElectricityBranchInfoListAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentElecBranchInfoListBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.bill.BillInfo;
import net.iGap.model.bill.GasBranchData;
import net.iGap.model.electricity_bill.ElectricityBranchData;
import net.iGap.model.electricity_bill.ElectricityResponseModel;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.electricity_bill.ElectricityBranchInfoListVM;

public class ElectricityBranchInfoListFrag extends BaseAPIViewFrag<ElectricityBranchInfoListVM> {

    private FragmentElecBranchInfoListBinding binding;
    private ElectricityBranchInfoListAdapter adapter;
    private String billID;
    private BillInfo.BillType type;
    private static final String TAG = "ElectricityBranchInfoLi";

    public static ElectricityBranchInfoListFrag newInstance(BillInfo.BillType type, String billID) {
        return new ElectricityBranchInfoListFrag(type, billID);
    }

    private ElectricityBranchInfoListFrag(BillInfo.BillType type, String billID) {
        this.billID = billID;
        this.type = type;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ElectricityBranchInfoListVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_elec_branch_info_list, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        return attachToSwipeBack(binding.getRoot());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
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
        viewModel.setBillID(billID);

        switch (type) {
            case GAS:
                viewModel.getDataGas();
                break;
            case ELECTRICITY:
                viewModel.getDataElec();
                break;
        }
    }

    private void onDataChangedListener() {
        viewModel.getmDataElec().observe(getViewLifecycleOwner(), this::initRecyclerElec);
        viewModel.getmDataGas().observe(getViewLifecycleOwner(), this::initRecyclerGas);
        viewModel.getShowRequestFailedError().observe(getViewLifecycleOwner(), errorMessageResId -> {
            if (errorMessageResId != null) {
                HelperError.showSnackMessage(getString(errorMessageResId), false);
            }
        });
    }

    private void initRecyclerElec(ElectricityResponseModel<ElectricityBranchData> data) {
        adapter = new ElectricityBranchInfoListAdapter(getContext(), data, type);
        binding.billRecycler.setAdapter(adapter);
    }

    private void initRecyclerGas(ElectricityResponseModel<GasBranchData> data) {
        adapter = new ElectricityBranchInfoListAdapter(getContext(), data.getData(), type);
        binding.billRecycler.setAdapter(adapter);
    }

    private void updateRecycler() {
        adapter.notifyDataSetChanged();
    }

}
