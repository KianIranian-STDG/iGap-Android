package net.iGap.electricity_bill.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.util.Log;
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

import net.iGap.Config;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentElecBillListBinding;
import net.iGap.electricity_bill.repository.model.Bill;
import net.iGap.electricity_bill.view.adapter.ElectricityBillListAdapter;
import net.iGap.electricity_bill.viewmodel.ElectricityBillListVM;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

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
                Bill temp = elecBillVM.getmData().getValue().get(position);
                switch (btnAction) {
                    case PAY:
                        break;
                    case EDIT:
                        new HelperFragment(getFragmentManager(), ElectricityBillAddFrag.newInstance()).setReplace(false).load();
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
                                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                    elecBillVM.deleteItem(position);
                                })
                                .build();
                        dialog.show();
                        break;
                    case SHOW_DETAIL:
                        new HelperFragment(getFragmentManager(), ElectricityBillPayFrag.newInstance(temp.getID(), temp.getPayID(), temp.getPrice())).setReplace(false).load();
                        break;
                }
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
                new HelperFragment(getFragmentManager(), ElectricityBillAddFrag.newInstance()).setReplace(false).load();
                break;
            case DELETE_ACCOUNT:
                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title(R.string.elecBill_deleteAccount_title)
                        .content(R.string.elecBill_deleteAccount_desc)
                        .positiveText(R.string.elecBill_deleteAccount_pos)
                        .negativeText(R.string.elecBill_deleteAccount_neg)
                        .positiveColor(getContext().getResources().getColor(R.color.red))
                        .widgetColor(new Theme().getAccentColor(getContext()))
                        .onPositive((dialog1, which) -> Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show())
                        .build();
                dialog.show();
                break;
        }
    }

}
