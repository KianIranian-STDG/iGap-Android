package net.iGap.electricity_bill.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentElecBillPayBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.electricity_bill.repository.model.Bill;
import net.iGap.electricity_bill.repository.model.LastBillData;
import net.iGap.electricity_bill.viewmodel.ElectricityBillPayVM;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperMimeType;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.ToolbarListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static net.iGap.G.context;
import static net.iGap.module.AttachFile.request_code_image_from_gallery_single_select;

public class ElectricityBillPayFrag extends BaseAPIViewFrag {

    public enum btnActions {BRANCH_INFO, ADD_LIST}

    private FragmentElecBillPayBinding binding;
    private ElectricityBillPayVM elecBillVM;
    private Bill bill;
    private boolean editMode;
    private static final String TAG = "ElectricityBillPayFrag";

    public static ElectricityBillPayFrag newInstance(String billID, String billPayID, String billPrice, boolean editMode) {
        return new ElectricityBillPayFrag(new Bill(billID, billPayID, billPrice, null), editMode);
    }

    public static ElectricityBillPayFrag newInstance(String billID, boolean editMode) {
        return new ElectricityBillPayFrag(new Bill(billID, null, null, null), editMode);
    }

    private ElectricityBillPayFrag(Bill bill, boolean editMode) {
        this.bill = bill;
        this.editMode = editMode;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        elecBillVM = ViewModelProviders.of(this).get(ElectricityBillPayVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_elec_bill_pay, container, false);
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

        elecBillVM.getBillImage().observe(getViewLifecycleOwner(), data -> downloadFile());
        elecBillVM.getErrorM().observe(getViewLifecycleOwner(), errorModel -> {
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
        if (editMode)
            binding.addToList.setText(getResources().getString(R.string.elecBill_edit_Btn));

        getIntent();
        elecBillVM.getData();
    }

    private void getIntent() {
        if (HelperCalander.isPersianUnicode) {
            elecBillVM.getBillID().set(HelperCalander.convertToUnicodeFarsiNumber(bill.getID()));
        }
        else
            elecBillVM.getBillID().set(bill.getID());
        if (bill.getPayID() != null) {
            if (HelperCalander.isPersianUnicode) {
                elecBillVM.getBillPayID().set(HelperCalander.convertToUnicodeFarsiNumber(bill.getPayID()));
            }
            else
                elecBillVM.getBillPayID().set(bill.getPayID());
        }
        if (bill.getPrice() != null) {
            if (HelperCalander.isPersianUnicode) {
                elecBillVM.getBillPrice().set(HelperCalander.convertToUnicodeFarsiNumber(bill.getPrice()));
            }
            else
                elecBillVM.getBillPrice().set(bill.getPrice());
            elecBillVM.getProgressVisibilityData().set(View.GONE);
        }

        elecBillVM.setDebit(new Bill(bill.getID(), bill.getPayID(), bill.getPrice(), "-"));
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
                new HelperFragment(getFragmentManager(), ElectricityBranchInfoListFrag.newInstance(bill.getID())).setReplace(false).load();
                break;
            case ADD_LIST:
                new HelperFragment(getFragmentManager(), ElectricityBillAddFrag.newInstance(bill.getID(), editMode)).setReplace(false).load();
                break;
        }
    }

    private void downloadFile() {
        LastBillData data = elecBillVM.getBillImage().getValue();
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
        elecBillVM.getProgressVisibilityDownload().set(View.GONE);
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
