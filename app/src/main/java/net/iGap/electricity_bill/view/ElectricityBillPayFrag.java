package net.iGap.electricity_bill.view;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentElecBillPayBinding;
import net.iGap.dialog.DefaultRoundDialog;
import net.iGap.electricity_bill.repository.model.Bill;
import net.iGap.electricity_bill.repository.model.LastBillData;
import net.iGap.electricity_bill.viewmodel.ElectricityBillPayVM;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperMimeType;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static net.iGap.G.context;

public class ElectricityBillPayFrag extends BaseAPIViewFrag {

    private int gone;

    public enum btnActions {BRANCH_INFO, ADD_LIST}

    private FragmentElecBillPayBinding binding;
    private ElectricityBillPayVM elecBillVM;
    private Bill bill;
    private static final String TAG = "ElectricityBillPayFrag";

    public static ElectricityBillPayFrag newInstance(String billID, String billPayID, String billPrice) {
        ElectricityBillPayFrag Frag = new ElectricityBillPayFrag(new Bill(billID, billPayID, billPrice, null));
        return Frag;
    }

    public static ElectricityBillPayFrag newInstance(String billID) {
        ElectricityBillPayFrag Frag = new ElectricityBillPayFrag(new Bill(billID, null, null, null));
        return Frag;
    }

    private ElectricityBillPayFrag(Bill bill) {
        this.bill = bill;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        elecBillVM = ViewModelProviders.of(this).get(ElectricityBillPayVM.class);
        elecBillVM.getBillID().set(bill.getID());
        if (bill.getPayID() != null)
            elecBillVM.getBillPayID().set(bill.getPayID());
        if (bill.getPrice() != null) {
            elecBillVM.getBillPrice().set(bill.getPrice());
            elecBillVM.getProgressVisibilityData().set(View.GONE);
        }
        elecBillVM.getData();
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
                break;
            case ADD_LIST:
                new HelperFragment(getFragmentManager(), ElectricityBillAddFrag.newInstance(elecBillVM.getBillID().get())).setReplace(false).load();
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
            final File dwldsPath = new File(dir, "ElectricityBill" + data.getPaymentDeadLine() + ".pdf");
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
        Snackbar.make(binding.Container, R.string.elecBill_image_success, Snackbar.LENGTH_LONG)
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
                }).show();
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
        DefaultRoundDialog defaultRoundDialog = new DefaultRoundDialog(getContext());
        defaultRoundDialog.setTitle(getResources().getString(titleRes));
        defaultRoundDialog.setMessage(getResources().getString(messageRes));
        defaultRoundDialog.setPositiveButton(getResources().getString(btnRes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mode == 1) {
                    downloadFile();
                }
            }
        });
        defaultRoundDialog.show();
    }

}
