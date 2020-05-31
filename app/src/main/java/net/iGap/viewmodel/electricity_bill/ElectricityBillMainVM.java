package net.iGap.viewmodel.electricity_bill;

import android.view.View;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.helper.HelperNumerical;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

public class ElectricityBillMainVM extends BaseAPIViewModel {

    public enum BillType {ELECTRICITY, GAS, PHONE, MOBILE}

    private ObservableField<String> billID;
    private ObservableField<Integer> billIDError;
    private ObservableField<Boolean> billIDErrorEnable;
    private ObservableField<Integer> progressVisibility;

    private MutableLiveData<Boolean> goToBillDetailFrag;

    private BillType type = BillType.ELECTRICITY;

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
        if (billID.get() == null || billID.get().isEmpty()) {
            activateError(R.string.elecBill_Entry_lengthError);
            return;
        }
        if (type == BillType.ELECTRICITY || type == BillType.GAS) {
            if (billID.get().length() < 13) {
                activateError(R.string.elecBill_EntryService_lengthError);
                return;
            }
        }
        if (type == BillType.PHONE || type == BillType.MOBILE) {
            if (billID.get().length() < 11) {
                activateError(R.string.elecBill_EntryPhone_lengthError);
                return;
            }
        }
        progressVisibility.set(View.VISIBLE);
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
                billPrice = new HelperNumerical().getCommaSeparatedPrice((Integer.parseInt(price) * 1000)) + " ریال";
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

    public void setType(BillType type) {
        this.type = type;
    }

    public BillType getType() {
        return type;
    }
}
