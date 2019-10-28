package net.iGap.electricity_bill.view;

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
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentElecBranchInfoListBinding;
import net.iGap.electricity_bill.repository.model.BranchData;
import net.iGap.electricity_bill.repository.model.ElectricityResponseModel;
import net.iGap.electricity_bill.view.adapter.ElectricityBranchInfoListAdapter;
import net.iGap.electricity_bill.viewmodel.ElectricityBranchInfoListVM;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

public class ElectricityBranchInfoListFrag extends BaseAPIViewFrag {

    private FragmentElecBranchInfoListBinding binding;
    private ElectricityBranchInfoListVM elecBillVM;
    private ElectricityBranchInfoListAdapter adapter;
    private String billID;
    private static final String TAG = "ElectricityBranchInfoLi";

    public static ElectricityBranchInfoListFrag newInstance(String billID) {
        return new ElectricityBranchInfoListFrag(billID);
    }

    private ElectricityBranchInfoListFrag(String billID) {
        this.billID = billID;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        elecBillVM = ViewModelProviders.of(this).get(ElectricityBranchInfoListVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_elec_branch_info_list, container, false);
        binding.setViewmodel(elecBillVM);
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
        elecBillVM.setBillID(billID);
        elecBillVM.getData();
    }

    private void onDataChangedListener() {
        elecBillVM.getmData().observe(getViewLifecycleOwner(), this::initRecycler);
    }

    private void initRecycler(ElectricityResponseModel<BranchData> data) {
        adapter = new ElectricityBranchInfoListAdapter(getContext(), data);
        binding.billRecycler.setAdapter(adapter);
    }

    private void updateRecycler() {
        adapter.notifyDataSetChanged();
    }

}
