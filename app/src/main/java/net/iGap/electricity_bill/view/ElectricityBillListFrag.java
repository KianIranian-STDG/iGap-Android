package net.iGap.electricity_bill.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.Theme;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentElecBillListBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.dialog.topsheet.TopSheetDialog;
import net.iGap.electricity_bill.repository.model.BillData;
import net.iGap.electricity_bill.repository.model.BranchDebit;
import net.iGap.electricity_bill.view.adapter.ElectricityBillListAdapter;
import net.iGap.electricity_bill.viewmodel.ElectricityBillListVM;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElectricityBillListFrag extends BaseAPIViewFrag {

    public enum btnActions {ADD_NEW_BILL, DELETE_ACCOUNT}

    private FragmentElecBillListBinding binding;
    private ElectricityBillListVM elecBillVM;
    private ElectricityBillListAdapter adapter;
    private static final String TAG = "ElectricityBillListFrag";

    public static ElectricityBillListFrag newInstance() {
        return new ElectricityBillListFrag();
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
                .setRightIcons(R.string.more_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        List<String> items = new ArrayList<>();
                        items.add(getString(R.string.elecBill_cell_deleteAccount));
                        new TopSheetDialog(getContext()).setListData(items, -1, position -> {
                            if (position == 0) {
                                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                        .title(R.string.elecBill_deleteAccount_title)
                                        .content(R.string.elecBill_deleteAccount_desc)
                                        .positiveText(R.string.elecBill_deleteAccount_pos)
                                        .negativeText(R.string.elecBill_deleteAccount_neg)
                                        .positiveColor(getContext().getResources().getColor(R.color.red))
                                        .widgetColor(new Theme().getAccentColor(getContext()))
                                        .onPositive((dialog1, which) -> elecBillVM.deleteAccount())
                                        .build();
                                dialog.show();
                            }
                        }).show();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.Toolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        binding.billRecycler.setHasFixedSize(true);
        onDataChangedListener();
        elecBillVM.getBranchData();
    }

    private void onDataChangedListener() {
        elecBillVM.getmMapData().observe(getViewLifecycleOwner(), billDataModelBranchDebitMap -> initRecycler(billDataModelBranchDebitMap));
        elecBillVM.getGoBack().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                popBackStackFragment();
            }
        });

        elecBillVM.getErrorM().observe(getViewLifecycleOwner(), errorModel -> {
            switch (errorModel.getMessage()) {
                case "001":
                    showDialog(getResources().getString(R.string.elecBill_error_title), getResources().getString(R.string.elecBill_error_MPLError), getResources().getString(R.string.ok));
                    break;
                case "002":
                    showDialog(getResources().getString(R.string.elecBill_success_title), getResources().getString(R.string.elecBill_success_pay), getResources().getString(R.string.ok));
                    break;
                case "003":
                    showDialog(getResources().getString(R.string.elecBill_error_title), getResources().getString(R.string.elecBill_error_billMissing), getResources().getString(R.string.ok));
                    break;
                case "004":
                    showDialog(getResources().getString(R.string.elecBill_error_title), getResources().getString(R.string.elecBill_error_bellowMin), getResources().getString(R.string.ok));
                    break;
                default:
                    Snackbar.make(binding.Container, errorModel.getMessage(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.ok, v -> {
                            }).show();
                    break;
            }
        });
    }


    private void showDialog(String title, String message, String btnRes) {
        new MaterialDialog.Builder(getContext()).title(title).positiveText(btnRes).content(message).show();
    }


    private void initRecycler(Map<BillData.BillDataModel, BranchDebit> bills) {
        adapter = new ElectricityBillListAdapter(getContext(), bills, (item, btnAction) -> {
            BranchDebit temp = elecBillVM.getmMapData().getValue().get(item);
            switch (btnAction) {
                case PAY:
                    elecBillVM.payBill(item);
                    break;
                case EDIT:
                    new HelperFragment(getFragmentManager(),
                            ElectricityBillAddFrag.newInstance(temp.getBillID(), item.getBillTitle(),
                                    String.valueOf(elecBillVM.getNationalID()), true)).setReplace(false).load();
                    break;
                case DELETE:
                    final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                            .title(R.string.elecBill_deleteBill_title)
                            .content(R.string.elecBill_deleteBill_desc)
                            .positiveText(R.string.elecBill_deleteBill_pos)
                            .negativeText(R.string.elecBill_deleteBill_neg)
                            .positiveColor(getContext().getResources().getColor(R.color.red))
                            .widgetColor(new Theme().getAccentColor(getContext()))
                            .onPositive((dialog1, which) -> {
                                elecBillVM.deleteItem(item);
                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            })
                            .build();
                    dialog.show();
                    break;
                case SHOW_DETAIL:
                    new HelperFragment(getFragmentManager(),
                            ElectricityBillPayFrag.newInstance(item.getBillTitle(), temp.getBillID(), temp.getPaymentIDConverted(), temp.getTotalBillDebtConverted(), true))
                            .setReplace(false).load();
                    break;
            }
        });
        binding.billRecycler.setAdapter(adapter);
    }

    private void updateRecycler() {
        adapter.notifyDataSetChanged();
    }

    public void onAddNewBillBtnClick() {
        onBtnClickManger(btnActions.ADD_NEW_BILL);
    }

    private void onBtnClickManger(btnActions actions) {
        if (actions == btnActions.ADD_NEW_BILL) {
            new HelperFragment(getFragmentManager(), ElectricityBillAddFrag.newInstance()).setReplace(false).load();
        }
    }

    public void refreshData() {
        elecBillVM.getBranchData();
    }
}
