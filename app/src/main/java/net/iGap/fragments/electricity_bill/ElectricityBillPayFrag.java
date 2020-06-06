package net.iGap.fragments.electricity_bill;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
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
import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentElecBillPayBinding;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperMimeType;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.bill.BillInfo;
import net.iGap.model.electricity_bill.Bill;
import net.iGap.model.electricity_bill.LastBillData;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.electricity_bill.ElectricityBillPayVM;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static net.iGap.G.context;

public class ElectricityBillPayFrag extends BaseAPIViewFrag<ElectricityBillPayVM> {

    public enum btnActions {BRANCH_INFO, ADD_LIST}

    private FragmentElecBillPayBinding binding;
    private BillInfo bill;
    private boolean editMode;
    private static final String TAG = "ElectricityBillPayFrag";

    public static ElectricityBillPayFrag newInstance(BillInfo.BillType billType, String billID, String billName, boolean editMode) {
        BillInfo info = new BillInfo();
        info.setBillType(billType);
        info.setTitle(billName);
        switch (billType) {
            case ELECTRICITY:
                info.setBillID(billID);
                break;
            case GAS:
                info.setGasID(billID);
                break;
            case MOBILE:
            case PHONE:
                info.setPhoneNum(billID);
                break;
        }
        return new ElectricityBillPayFrag(info, editMode);
    }

    @Deprecated
    public static ElectricityBillPayFrag newInstance(String billID, String billPayID, String billPrice, boolean editMode) {
        return new ElectricityBillPayFrag(new Bill(billID, billPayID, billPrice, null), editMode);
    }

    @Deprecated
    public static ElectricityBillPayFrag newInstance(String billName, String billID, String billPayID, String billPrice, boolean editMode) {
        return new ElectricityBillPayFrag(new Bill(billName, billID, billPayID, billPrice, null), editMode);
    }

    @Deprecated
    public static ElectricityBillPayFrag newInstance(String billID, boolean editMode) {
        return new ElectricityBillPayFrag(new Bill(billID, null, null, null), editMode);
    }

    @Deprecated
    private ElectricityBillPayFrag(Bill bill, boolean editMode) {
//        this.bill = bill;
        this.editMode = editMode;
    }

    private ElectricityBillPayFrag(BillInfo bill, boolean editMode) {
        this.bill = bill;
        this.editMode = editMode;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ElectricityBillPayVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_elec_bill_pay, container, false);
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
                .setLifecycleOwner(getViewLifecycleOwner())
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

        viewModel.getBillImage().observe(getViewLifecycleOwner(), data -> downloadFile());
        viewModel.getErrorM().observe(getViewLifecycleOwner(), errorModel -> {
            switch (errorModel.getMessage()) {
                case "001":
                    showDialog(getResources().getString(R.string.elecBill_error_title), getResources().getString(R.string.elecBill_error_billMissing), getResources().getString(R.string.ok));
                    break;
                case "002":
                    showDialog(getResources().getString(R.string.elecBill_error_title), getResources().getString(R.string.elecBill_error_bellowMin), getResources().getString(R.string.ok));
                    break;
                case "003":
                    showDialog(getResources().getString(R.string.elecBill_error_title), getResources().getString(R.string.elecBill_error_MPLError), getResources().getString(R.string.ok));
                    break;
                case "004":
                    showDialog(getResources().getString(R.string.elecBill_success_title), getResources().getString(R.string.elecBill_success_pay), getResources().getString(R.string.ok));
                    break;
                default:
                    Snackbar.make(binding.Container, errorModel.getMessage(), Snackbar.LENGTH_LONG)
                            .setAction(R.string.ok, v -> {
                            }).show();
                    break;
            }
        });
        viewModel.getShowRequestFailedError().observe(getViewLifecycleOwner(), errorMessageResId -> {
            if (errorMessageResId != null) {
                HelperError.showSnackMessage(getString(errorMessageResId), false);
            }
        });
        if (editMode)
            binding.addToList.setText(getResources().getString(R.string.elecBill_edit_Btn));

        makePage();
        viewModel.getData();
    }

    private void makePage() {
        switch (bill.getBillType()) {
            case ELECTRICITY:
                if (HelperCalander.isPersianUnicode)
                    viewModel.getBillID().set(HelperCalander.convertToUnicodeFarsiNumber(bill.getBillID()));
                else
                    viewModel.getBillID().set(bill.getBillID());
                binding.billImage.setImageDrawable(getResources().getDrawable(R.drawable.bill_elc_pec));
                break;
            case GAS:
                binding.showBillImage.setVisibility(View.GONE);
                binding.billImage.setImageDrawable(getResources().getDrawable(R.drawable.bill_gaz_pec));
                break;
            case MOBILE:
            case PHONE:
                binding.showBillImage.setVisibility(View.GONE);
                binding.KontorInfo.setVisibility(View.GONE);

                binding.billPayIDTitle.setText(getResources().getString(R.string.elecBill_pay_billPhoneNumber));
                binding.billPriceTitle.append(" " + context.getResources().getText(R.string.elecBill_cell_billPayLastTerm));
                binding.billTimeTitle.setText(context.getResources().getText(R.string.elecBill_pay_billPrice) + " " + context.getResources().getText(R.string.elecBill_cell_billPayMidTerm));

                binding.Pay2.setVisibility(View.VISIBLE);
                binding.Pay2.setText(context.getResources().getText(R.string.elecBill_cell_billPayPhoneMidBtn));

                binding.Pay.setText(context.getResources().getText(R.string.elecBill_cell_billPayPhoneLastBtn));

                if (bill.getBillType() == BillInfo.BillType.PHONE) {
                    String phoneTemp = bill.getPhoneNum();
                    bill.setPhoneNum(phoneTemp.substring(phoneTemp.length() - 8));
                    bill.setAreaCode(phoneTemp.substring(0, phoneTemp.length() - 8));
                    Log.d(TAG, "makePage: " + bill.getPhoneNum());
                    binding.billImage.setImageDrawable(context.getResources().getDrawable(R.drawable.bill_telecom_pec));
                } else {
                    binding.billImage.setImageDrawable(context.getResources().getDrawable(R.drawable.bill_mci_pec));
                }
                break;
        }
        viewModel.setInfo(bill);
    }

    private void showDialog(String title, String message, String btnRes) {

        new MaterialDialog.Builder(getContext()).title(title).positiveText(btnRes).content(message).show();

    }

    public void onBranchInfoBtnClick() {
        onBtnClickManger(btnActions.BRANCH_INFO);
    }

    public void onAddToListBtnClick() {
        onBtnClickManger(btnActions.ADD_LIST);
    }

    private void onBtnClickManger(btnActions actions) {
        switch (actions) {
            case BRANCH_INFO:
                switch (bill.getBillType()) {
                    case ELECTRICITY:
                        new HelperFragment(getFragmentManager(), ElectricityBranchInfoListFrag.newInstance(bill.getBillType(), bill.getBillID())).setReplace(false).load();
                        break;
                    case GAS:
                        new HelperFragment(getFragmentManager(), ElectricityBranchInfoListFrag.newInstance(bill.getBillType(), bill.getGasID())).setReplace(false).load();
                        break;
                }
                break;
            case ADD_LIST:
                ElectricityBillAddFrag frag = ElectricityBillAddFrag.newInstance(viewModel.getInfo(), editMode);
                frag.show(getFragmentManager(), "BillAddEdit");
                break;
        }
    }

    private void downloadFile() {
        LastBillData data = viewModel.getBillImage().getValue();
        if (!isExternalStorageReadable()) {
            showDialog(2, R.string.kuknos_setting_copySFailTitle, R.string.kuknos_setting_copySFailReadM, R.string.kuknos_setting_copySFailBtn);
            return;
        }
        if (!isExternalStorageWritable()) {
            showDialog(3, R.string.kuknos_setting_copySFailTitle, R.string.kuknos_setting_copySFailWriteM, R.string.kuknos_setting_copySFailBtn);
            return;
        }
        File dir = getDownloadStorageDir(getString(R.string.elecBill_image_Directory));
        try {
            final File dwldsPath = new File(dir, "ElectricityBill" + data.getPaymentDeadLine().split("T")[0] + ".pdf");
            byte[] pdfAsBytes = Base64.decode(data.getDocumentBase64(), 0);
            FileOutputStream os;
            os = new FileOutputStream(dwldsPath, false);
            os.write(pdfAsBytes);
            os.flush();
            os.close();
            showSuccessMessage(dwldsPath.getPath());
        } catch (IOException e) {
            Log.d(TAG, "File.toByteArray() error");
            e.printStackTrace();
        }
        viewModel.getProgressVisibilityDownload().set(View.GONE);
    }

    private void showSuccessMessage(String path) {
        Intent intent = HelperMimeType.appropriateProgram(path);
        if (intent != null) {
            try {
                startActivity(intent);
            } catch (Exception e) {
                // to prevent from 'No Activity found to handle Intent'
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, R.string.can_not_open_file, Toast.LENGTH_SHORT).show();
        }
        /*Snackbar.make(binding.Container, R.string.elecBill_image_success, Snackbar.LENGTH_LONG)
                .setAction(R.string.elecBill_image_open, v -> {
                    Intent intent = HelperMimeType.appropriateProgram(path);
                    if (intent != null) {
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            // to prevent from 'No Activity found to handle Intent'
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(context, R.string.can_not_open_file, Toast.LENGTH_SHORT).show();
                    }
                }).show();*/
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private File getDownloadStorageDir(String fileName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        if (file.isDirectory())
            return file;

        if (!file.mkdirs()) {
            showDialog(6, R.string.kuknos_setting_copySFailTitle, R.string.kuknos_setting_copySFailDir, R.string.kuknos_setting_copySFailBtn);
        }
        return file;
    }

    private void showDialog(int mode, int titleRes, int messageRes, int btnRes) {
        new MaterialDialog.Builder(getContext())
                .title(getResources().getString(titleRes))
                .positiveText(getResources().getString(btnRes))
                .onPositive((dialog, which) -> {
                    if (mode == 1) {
                        downloadFile();
                    }
                })
                .content(getResources().getString(messageRes))
                .show();
    }

}
