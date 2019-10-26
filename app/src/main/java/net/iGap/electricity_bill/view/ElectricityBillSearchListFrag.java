package net.iGap.electricity_bill.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentElecSearchListBinding;
import net.iGap.electricity_bill.repository.model.BranchData;
import net.iGap.electricity_bill.repository.model.CompanyList;
import net.iGap.electricity_bill.view.adapter.ElectricityBillSearchCompanySpinnerAdapter;
import net.iGap.electricity_bill.view.adapter.ElectricityBillSearchListAdapter;
import net.iGap.electricity_bill.viewmodel.ElectricityBillSearchListVM;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

import java.util.List;

public class ElectricityBillSearchListFrag extends BaseAPIViewFrag {

    private FragmentElecSearchListBinding binding;
    private ElectricityBillSearchListVM elecBillVM;
    private ElectricityBillSearchListAdapter adapter;
    private ElectricityBillSearchCompanySpinnerAdapter companyAdapter;
    private static final String TAG = "ElectricityBillSearchLi";

    public static ElectricityBillSearchListFrag newInstance() {
        ElectricityBillSearchListFrag kuknosLoginFrag = new ElectricityBillSearchListFrag();
        return kuknosLoginFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        elecBillVM = ViewModelProviders.of(this).get(ElectricityBillSearchListVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_elec_search_list, container, false);
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
        binding.billCompanySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                elecBillVM.setCompanyPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        onDataChangedListener();
        elecBillVM.getCompanyData();
    }

    private void onDataChangedListener() {
        elecBillVM.getmCompanyData().observe(getViewLifecycleOwner(), this::initSpinner);
        elecBillVM.getmBranchData().observe(getViewLifecycleOwner(), this::initRecycler);
    }

    private void initRecycler(List<BranchData> bills) {
        adapter = new ElectricityBillSearchListAdapter(getContext(), bills,
                position -> new HelperFragment(getFragmentManager(),
                        ElectricityBillPayFrag.newInstance(elecBillVM.getmBranchData().getValue().get(position).getBillID(), false))
                        .setReplace(false).load());
        binding.billRecycler.setAdapter(adapter);
    }

    private void initSpinner(CompanyList companyList) {
        companyAdapter = new ElectricityBillSearchCompanySpinnerAdapter(getContext(), companyList.getCompaniesList());
        binding.billCompanySpinner.setAdapter(companyAdapter);
    }

}
