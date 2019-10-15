package net.iGap.electricity_bill.viewmodel;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.helper.HelperNumerical;

public class ElectricityBillMainVM extends BaseAPIViewModel {

    private ObservableField<String> billID;
    private ObservableField<Integer> billIDError;
    private ObservableField<Boolean> billIDErrorEnable;
    private ObservableField<Integer> progressVisibility;

    private MutableLiveData<Boolean> goToBillDetailFrag;

    private String billPayID = null;
    private String billPrice = null;

    public ElectricityBillMainVM() {

        billID = new ObservableField<>();
        billIDError = new ObservableField<>();
        billIDErrorEnable = new ObservableField<>(false);
        progressVisibility = new ObservableField<>(View.GONE);
        goToBillDetailFrag = new MutableLiveData<>(false);

    }

    public void inquiryBill() {
        if (billID.get() == null || billID.get().isEmpty() || billID.get().length() < 13) {
            activateError(R.string.elecBill_Entry_lengthError);
            return;
        }
        goToBillDetailFrag.setValue(true);
    }

    public void setDataFromBarcodeScanner(String result) {
        if (result != null) {
            if (result.length() == 26) {
                String billId = result.substring(0, 13);
                String payId = result.substring(13, 26);
                String company_type = result.substring(11, 12);
                String price = result.substring(13, 21);
                while (payId.startsWith("0")) {
                    payId = payId.substring(1);
                }
                if (!company_type.equals("2")) {
                    activateError(R.string.elecBill_Entry_companyError);
                    return;
                }
                billID.set(billId);
                billPayID = payId;
                billPrice = new HelperNumerical().getCommaSeparatedPrice((Integer.parseInt(price) * 1000));
                goToBillDetailFrag.setValue(true);
            }
            else {
                activateError(R.string.elecBill_Entry_barcodeError);
            }
        }
    }

    private void activateError(int errorID) {
        billIDErrorEnable.set(true);
        billIDError.set(errorID);
    }

    public ObservableField<String> getBillID() {
        return billID;
    }

    public void setBillID(ObservableField<String> billID) {
        this.billID = billID;
    }

    public ObservableField<Integer> getBillIDError() {
        return billIDError;
    }

    public void setBillIDError(ObservableField<Integer> billIDError) {
        this.billIDError = billIDError;
    }

    public ObservableField<Boolean> getBillIDErrorEnable() {
        return billIDErrorEnable;
    }

    public void setBillIDErrorEnable(ObservableField<Boolean> billIDErrorEnable) {
        this.billIDErrorEnable = billIDErrorEnable;
    }

    public ObservableField<Integer> getProgressVisibility() {
        return progressVisibility;
    }

    public void setProgressVisibility(ObservableField<Integer> progressVisibility) {
        this.progressVisibility = progressVisibility;
    }

    public String getBillPayID() {
        return billPayID;
    }

    public String getBillPrice() {
        return billPrice;
    }

    public MutableLiveData<Boolean> getGoToBillDetailFrag() {
        return goToBillDetailFrag;
    }
}
