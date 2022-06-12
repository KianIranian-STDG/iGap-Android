package net.iGap.fragments.electricity_bill;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.adapter.electricity_bill.ElectricityBillSearchCompanySpinnerAdapter;
import net.iGap.adapter.electricity_bill.ElectricityBillSearchListAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentElecSearchListBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.messenger.theme.Theme;
import net.iGap.model.electricity_bill.CompanyList;
import net.iGap.model.electricity_bill.ElectricityBranchData;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.electricity_bill.ElectricityBillSearchListVM;

import java.util.List;

public class ElectricityBillSearchListFrag extends BaseAPIViewFrag<ElectricityBillSearchListVM> {

    private FragmentElecSearchListBinding binding;
    private static final String TAG = "ElectricityBillSearchLi";

    public static ElectricityBillSearchListFrag newInstance() {
        return new ElectricityBillSearchListFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ElectricityBillSearchListVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_elec_search_list, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        return attachToSwipeBack(binding.getRoot());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        binding.billSerial.setDefaultHintTextColor(ColorStateList.valueOf(Theme.getColor(Theme.key_theme_color)));
        binding.billSerialET.setTextColor(Theme.getColor(Theme.key_title_text));
        binding.billCompanyTitle.setTextColor(Theme.getColor(Theme.key_title_text));
        binding.errorView.setTextColor(Theme.getColor(Theme.key_title_text));
        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.icon_back)
                .setLifecycleOwner(getViewLifecycleOwner())
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
        binding.billRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.billCompanySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setCompanyPosition(position - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        onDataChangedListener();
        resetEditTextLisener();
        viewModel.getCompanyData();
    }

    private void resetEditTextLisener() {
        binding.billSerialET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.getBillSerialErrorEnable().set(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void onDataChangedListener() {
        viewModel.getmCompanyData().observe(getViewLifecycleOwner(), this::initSpinner);
        viewModel.getmBranchData().observe(getViewLifecycleOwner(), this::initRecycler);
        viewModel.getErrorM().observe(getViewLifecycleOwner(), errorModel -> {
            if (errorModel.getMessage().equals("001")) {
                Snackbar.make(binding.Container, getResources().getString(R.string.elecBill_error_company), Snackbar.LENGTH_LONG)
                        .setAction(R.string.elecBill_error_openCompanySpinner, v -> binding.billCompanySpinner.performClick()).show();
            } else {
                Snackbar.make(binding.Container, errorModel.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction(R.string.ok, v -> {
                        }).show();
            }
        });
        viewModel.getShowRequestFailedError().observe(getViewLifecycleOwner(), errorMessageResId -> {
            if (errorMessageResId != null) {
                HelperError.showSnackMessage(getString(errorMessageResId), false);
            }
        });
    }

    private void initRecycler(List<ElectricityBranchData> bills) {
        ElectricityBillSearchListAdapter adapter = new ElectricityBillSearchListAdapter(getContext(), bills,
                position -> new HelperFragment(getFragmentManager(),
                        ElectricityBillPayFrag.newInstance(viewModel.getmBranchData().getValue().get(position).getBillID(), false))
                        .setReplace(false).load());
        binding.billRecycler.setAdapter(adapter);
    }

    private void initSpinner(CompanyList companyList) {
        ElectricityBillSearchCompanySpinnerAdapter companyAdapter = new ElectricityBillSearchCompanySpinnerAdapter(getContext(), companyList.getCompaniesList());
        binding.billCompanySpinner.setAdapter(companyAdapter);
    }

}
