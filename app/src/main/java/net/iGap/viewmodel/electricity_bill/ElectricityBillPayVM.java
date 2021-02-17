package net.iGap.viewmodel.electricity_bill;

import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.helper.HelperMobileBank;
import net.iGap.helper.HelperNumerical;
import net.iGap.helper.HelperTracker;
import net.iGap.model.bill.BillInfo;
import net.iGap.model.bill.Debit;
import net.iGap.model.bill.MobileDebit;
import net.iGap.model.bill.ServiceDebit;
import net.iGap.model.electricity_bill.ElectricityResponseModel;
import net.iGap.model.electricity_bill.LastBillData;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.proto.ProtoMplGetBillToken;
import net.iGap.repository.BillsAPIRepository;
import net.iGap.request.RequestMplGetBillToken;

public class ElectricityBillPayVM extends BaseAPIViewModel {

    private ObservableField<String> billID;
    private ObservableField<String> billPayID;
    private ObservableField<String> billPrice;
    private ObservableField<String> billTime;

    private ObservableInt progressVisibilityData;
    private ObservableInt progressVisibilityPay;
    private ObservableInt progressVisibilityDownload;
    private ObservableInt progressVisibilityRetry;
    private ObservableBoolean payBtnEnable;
    private ObservableBoolean pay2BtnEnable;

    private MutableLiveData<LastBillData> billImage;
    private MutableLiveData<Integer> showRequestFailedError;
    private BillInfo info;
    private Debit debit;
    private MutableLiveData<ErrorModel> errorM;

    public ElectricityBillPayVM() {

        billID = new ObservableField<>("-");
        billPayID = new ObservableField<>("-");
        billPrice = new ObservableField<>("-");
        billTime = new ObservableField<>("-");
        billImage = new MutableLiveData<>();
        errorM = new MutableLiveData<>();
        info = new BillInfo();

        progressVisibilityData = new ObservableInt(View.VISIBLE);
        progressVisibilityPay = new ObservableInt(View.GONE);
        progressVisibilityDownload = new ObservableInt(View.GONE);
        progressVisibilityRetry = new ObservableInt(View.GONE);
        payBtnEnable = new ObservableBoolean(true);
        pay2BtnEnable = new ObservableBoolean(true);
        showRequestFailedError = new MutableLiveData<>();

    }

    public void getData() {
        progressVisibilityData.set(View.VISIBLE);
        progressVisibilityRetry.set(View.GONE);
        switch (info.getBillType()) {
            case PHONE:
            case MOBILE:
                getPhoneData();
                break;
            case ELECTRICITY:
            case GAS:
                getServiceData();
                break;
        }
    }

    private void getPhoneData() {
        new BillsAPIRepository().phoneAndMobileInquiry(info, this, new ResponseCallback<ElectricityResponseModel<MobileDebit>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<MobileDebit> data) {
                progressVisibilityData.set(View.GONE);
                debit = new Debit<MobileDebit>();
                debit.setData(data.getData());

                try {
                    billPayID.set(HelperMobileBank.checkNumbersInMultiLangs((info.getAreaCode() != null ? info.getAreaCode() : "") + info.getPhoneNum()));
                    billID.set(HelperMobileBank.checkNumbersInMultiLangs(data.getData().getLastTerm().getBillID()));
                    billPrice.set(HelperMobileBank.checkNumbersInMultiLangs(
                            new HelperNumerical().getCommaSeparatedPrice(Long.parseLong(
                                    data.getData().getLastTerm().getAmount())) + " ریال"));
                    billTime.set(HelperMobileBank.checkNumbersInMultiLangs(
                            new HelperNumerical().getCommaSeparatedPrice(Long.parseLong(
                                    data.getData().getMidTerm().getAmount())) + " ریال"));
                } catch (Exception e) {

                }
            }

            @Override
            public void onError(String error) {
                errorM.setValue(new ErrorModel("", error));
                progressVisibilityData.set(View.GONE);
                progressVisibilityRetry.set(View.VISIBLE);
            }

            @Override
            public void onFailed() {
                progressVisibilityData.set(View.GONE);
                showRequestFailedError.setValue(R.string.connection_error);
                progressVisibilityRetry.set(View.VISIBLE);
            }
        });
    }

    private void getServiceData() {
        new BillsAPIRepository().serviceInquiry(info, this, new ResponseCallback<ElectricityResponseModel<ServiceDebit>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<ServiceDebit> data) {
                progressVisibilityData.set(View.GONE);
                debit = new Debit<ServiceDebit>();
                debit.setData(data.getData());

                billID.set(HelperMobileBank.checkNumbersInMultiLangs(data.getData().getBillID()));
                billPayID.set(HelperMobileBank.checkNumbersInMultiLangs(data.getData().getPaymentIDConverted()));
                if (info.getBillType() == BillInfo.BillType.ELECTRICITY)
                    billPrice.set(
                            HelperMobileBank.checkNumbersInMultiLangs(
                                    new HelperNumerical().getCommaSeparatedPrice(Long.parseLong(
                                            data.getData().getTotalBillDebtConverted())) + " ریال"));
                else
                    billPrice.set(HelperMobileBank.checkNumbersInMultiLangs(
                            new HelperNumerical().getCommaSeparatedPrice(Long.parseLong(
                                    data.getData().getTotalGasBillDebt())) + " ریال"));
                billTime.set(HelperMobileBank.checkNumbersInMultiLangs(data.getData().getPaymentDeadLineDate()));
            }

            @Override
            public void onError(String error) {
                errorM.setValue(new ErrorModel("", error));
                progressVisibilityData.set(View.GONE);
            }

            @Override
            public void onFailed() {
                progressVisibilityData.set(View.GONE);
                showRequestFailedError.setValue(R.string.connection_error);
            }
        });
    }

    public void payBtn(int buttonID) {
        if (debit == null)
            return;
        if (buttonID == 0) {
            // main Btn for pay
            progressVisibilityPay.set(View.VISIBLE);
            payBtnEnable.set(false);
            switch (info.getBillType()) {
                case ELECTRICITY:
                case GAS:
                    ServiceDebit temp = (ServiceDebit) debit.getData();
                    if (info.getBillType() == BillInfo.BillType.ELECTRICITY)
                        payBill(temp.getBillID(), temp.getPaymentID(), temp.getTotalElectricityBillDebt());
                    else
                        payBill(temp.getBillID(), temp.getPaymentID(), temp.getTotalGasBillDebt());
                    break;
                case MOBILE:
                case PHONE:
                    MobileDebit temp2 = (MobileDebit) debit.getData();
                    payBill(temp2.getLastTerm().getBillID(), temp2.getLastTerm().getPayID(), temp2.getLastTerm().getAmount(), ProtoMplGetBillToken.MplGetBillToken.Type.LAST_TERM_VALUE);
                    break;
            }
        } else {
            // secondary Btn for phone pay
            progressVisibilityPay.set(View.VISIBLE);
            pay2BtnEnable.set(false);
            MobileDebit temp2 = (MobileDebit) debit.getData();
            payBill(temp2.getMidTerm().getBillID(), temp2.getMidTerm().getPayID(), temp2.getMidTerm().getAmount(), ProtoMplGetBillToken.MplGetBillToken.Type.MID_TERM_VALUE);
        }
        switch (info.getBillType()) {
            case MOBILE:
                HelperTracker.sendTracker(HelperTracker.TRACKER_MOBILE_BILL_PAY);
                break;
            case PHONE:
                HelperTracker.sendTracker(HelperTracker.TRACKER_PHONE_BILL_PAY);
                break;
            case ELECTRICITY:
                HelperTracker.sendTracker(HelperTracker.TRACKER_ELECTRIC_BILL_PAY);
                break;
            case GAS:
                HelperTracker.sendTracker(HelperTracker.TRACKER_GAS_BILL_PAY);
                break;
        }
    }

    private void payBill(String billID, String payID, String price) {
        payBill(billID, payID, price, ProtoMplGetBillToken.MplGetBillToken.Type.NONE_VALUE);
    }

    private void payBill(String billID, String payID, String price, int billType) {

        if (payID == null || payID.equals("") || payID.equals("null")) {
            errorM.setValue(new ErrorModel("", "001"));
            getData();
            progressVisibilityPay.set(View.GONE);
            payBtnEnable.set(true);
            pay2BtnEnable.set(true);
            return;
        }

        if (Long.parseLong(price) < 10000) {
            errorM.setValue(new ErrorModel("", "002"));
            progressVisibilityPay.set(View.GONE);
            payBtnEnable.set(true);
            pay2BtnEnable.set(true);
            return;
        }

        G.onMplResult = error -> {
            progressVisibilityPay.set(View.GONE);
            payBtnEnable.set(true);
            pay2BtnEnable.set(true);
            if (error) {
                errorM.setValue(new ErrorModel("", "003"));
            }
        };

        RequestMplGetBillToken requestMplGetBillToken = new RequestMplGetBillToken();
        requestMplGetBillToken.mplGetBillToken(Long.parseLong(billID), Long.parseLong(payID), billType);
    }

    public void showBillImage() {
        progressVisibilityDownload.set(View.VISIBLE);
        new BillsAPIRepository().getLastBill(info, this, new ResponseCallback<ElectricityResponseModel<LastBillData>>() {
            @Override
            public void onSuccess(ElectricityResponseModel<LastBillData> data) {
                billImage.setValue(data.getData());
                progressVisibilityDownload.set(View.GONE);
            }

            @Override
            public void onError(String error) {
                progressVisibilityDownload.set(View.GONE);
                errorM.setValue(new ErrorModel("", error));
            }

            @Override
            public void onFailed() {
                progressVisibilityDownload.set(View.GONE);
                showRequestFailedError.setValue(R.string.connection_error);
            }
        });
    }

    public ObservableField<String> getBillID() {
        return billID;
    }

    public void setBillID(ObservableField<String> billID) {
        this.billID = billID;
    }

    public ObservableField<String> getBillPayID() {
        return billPayID;
    }

    public void setBillPayID(ObservableField<String> billPayID) {
        this.billPayID = billPayID;
    }

    public ObservableField<String> getBillPrice() {
        return billPrice;
    }

    public void setBillPrice(ObservableField<String> billPrice) {
        this.billPrice = billPrice;
    }

    public ObservableField<String> getBillTime() {
        return billTime;
    }

    public void setBillTime(ObservableField<String> billTime) {
        this.billTime = billTime;
    }

    public ObservableInt getProgressVisibilityData() {
        return progressVisibilityData;
    }

    public void setProgressVisibilityData(ObservableInt progressVisibilityData) {
        this.progressVisibilityData = progressVisibilityData;
    }

    public ObservableInt getProgressVisibilityPay() {
        return progressVisibilityPay;
    }

    public void setProgressVisibilityPay(ObservableInt progressVisibilityPay) {
        this.progressVisibilityPay = progressVisibilityPay;
    }

    public ObservableBoolean getPayBtnEnable() {
        return payBtnEnable;
    }

    public void setPayBtnEnable(ObservableBoolean payBtnEnable) {
        this.payBtnEnable = payBtnEnable;
    }

    public MutableLiveData<LastBillData> getBillImage() {
        return billImage;
    }

    public void setBillImage(MutableLiveData<LastBillData> billImage) {
        this.billImage = billImage;
    }

    public ObservableInt getProgressVisibilityDownload() {
        return progressVisibilityDownload;
    }

    public void setProgressVisibilityDownload(ObservableInt progressVisibilityDownload) {
        this.progressVisibilityDownload = progressVisibilityDownload;
    }

    public MutableLiveData<ErrorModel> getErrorM() {
        return errorM;
    }

    public void setErrorM(MutableLiveData<ErrorModel> errorM) {
        this.errorM = errorM;
    }

    public BillInfo getInfo() {
        return info;
    }

    public void setInfo(BillInfo info) {
        this.info = info;
    }

    public MutableLiveData<Integer> getShowRequestFailedError() {
        return showRequestFailedError;
    }

    public ObservableBoolean getPay2BtnEnable() {
        return pay2BtnEnable;
    }

    public void setPay2BtnEnable(ObservableBoolean pay2BtnEnable) {
        this.pay2BtnEnable = pay2BtnEnable;
    }

    public Debit getDebit() {
        return debit;
    }

    public void setDebit(Debit debit) {
        this.debit = debit;
    }

    public ObservableInt getProgressVisibilityRetry() {
        return progressVisibilityRetry;
    }
}
