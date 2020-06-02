package net.iGap.fragments.electricity_bill;

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
import net.iGap.adapter.electricity_bill.ElectricityBillListAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentElecBillListBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.bill.BillInfo;
import net.iGap.model.bill.BillList;
import net.iGap.model.bill.Debit;
import net.iGap.model.bill.MobileDebit;
import net.iGap.model.bill.ServiceDebit;
import net.iGap.module.Theme;
import net.iGap.module.dialog.topsheet.TopSheetDialog;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.electricity_bill.ElectricityBillListVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElectricityBillListFrag extends BaseAPIViewFrag<ElectricityBillListVM> {

    public enum btnActions {ADD_NEW_BILL, DELETE_ACCOUNT}

    private FragmentElecBillListBinding binding;
    private ElectricityBillListAdapter adapter;
    private static final String TAG = "ElectricityBillListFrag";

    public static ElectricityBillListFrag newInstance() {
        return new ElectricityBillListFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ElectricityBillListVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_elec_bill_list, container, false);
        binding.setViewmodel(viewModel);
        binding.setFragment(this);
        binding.setLifecycleOwner(this);

        return attachToSwipeBack(binding.getRoot());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
//                .setRightIcons(R.string.more_icon)
                .setLifecycleOwner(getViewLifecycleOwner())
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
                                        .onPositive((dialog1, which) -> viewModel.deleteAccount())
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
        viewModel.getBillsList();
    }

    private void onDataChangedListener() {
        viewModel.getmMapData().observe(getViewLifecycleOwner(), billDataModelBranchDebitMap -> initRecycler(billDataModelBranchDebitMap));
        viewModel.getGoBack().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                popBackStackFragment();
            }
        });

        viewModel.getErrorM().observe(getViewLifecycleOwner(), errorModel -> {
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

        viewModel.getShowRequestFailedError().observe(getViewLifecycleOwner(), errorMessageRes -> {
            if (errorMessageRes != null) {
                HelperError.showSnackMessage(getString(errorMessageRes), false);
            }
        });
    }


    private void showDialog(String title, String message, String btnRes) {
        new MaterialDialog.Builder(getContext()).title(title).positiveText(btnRes).content(message).show();
    }


    private void initRecycler(Map<BillList.Bill, Debit> bills) {
        adapter = new ElectricityBillListAdapter(getContext(), bills, (item, btnAction) -> {
//            ServiceDebit temp = viewModel.getmMapData().getValue().get(item);
            switch (btnAction) {
                case PAY:
                    ServiceDebit tempService = (ServiceDebit) bills.get(item).getData();
                    viewModel.payServiceBill(tempService.getBillID(), tempService.getPaymentID(),
                            tempService.getTotalElectricityBillDebt() != null ? tempService.getTotalElectricityBillDebt() : tempService.getTotalGasBillDebt());
                    break;
                case EDIT:
                    if (item.getBillTitle() == null) {
                        showDialog(getResources().getString(R.string.elecBill_error_title), getResources().getString(R.string.elecBill_error_notPossible), getResources().getString(R.string.ok));
                        return;
                    }
                    ElectricityBillAddFrag frag = ElectricityBillAddFrag.newInstance(viewModel.getBillInfo(item), true);
                    frag.setCompleteListener(new ElectricityBillAddFrag.CompleteListener() {
                        @Override
                        public void loadAgain() {
                            viewModel.getBillsList();
                        }
                    });
                    frag.show(getFragmentManager(), "BillAddEdit");
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
                                viewModel.deleteItem(item);
                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            })
                            .build();
                    dialog.show();
                    break;
                case SHOW_DETAIL:
                    if (item.getBillTitle() == null) {
                        showDialog(getResources().getString(R.string.elecBill_error_title), getResources().getString(R.string.elecBill_error_notPossible), getResources().getString(R.string.ok));
                        return;
                    }
                    BillInfo temp = viewModel.getBillInfo(item);
                    new HelperFragment(getFragmentManager(),
                            ElectricityBillPayFrag.newInstance(temp.getBillType(), temp.getBillID(), temp.getTitle(), true))
                            .setReplace(false).load();
                    break;
                case MID_PAY:
                    MobileDebit tempMid = (MobileDebit) bills.get(item).getData();
                    viewModel.payServiceBill(tempMid.getMidTerm().getBillID(), tempMid.getMidTerm().getPayID(),
                            tempMid.getMidTerm().getAmount());
                    break;
                case LAST_PAY:
                    MobileDebit tempLast = (MobileDebit) bills.get(item).getData();
                    viewModel.payServiceBill(tempLast.getLastTerm().getBillID(), tempLast.getLastTerm().getPayID(),
                            tempLast.getLastTerm().getAmount());
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
        viewModel.getBillsList();
    }
}
