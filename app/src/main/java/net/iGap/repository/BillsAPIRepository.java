package net.iGap.repository;

import net.iGap.api.BillsApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.model.bill.BillInfo;
import net.iGap.model.bill.BillList;
import net.iGap.model.bill.GasBranchData;
import net.iGap.model.bill.MobileDebit;
import net.iGap.model.bill.ServiceDebit;
import net.iGap.model.electricity_bill.ElectricityBranchData;
import net.iGap.model.electricity_bill.ElectricityResponseModel;
import net.iGap.model.electricity_bill.LastBillData;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.realm.RealmElectricityBill;

public class BillsAPIRepository {

    private BillsApi apiService = new RetrofitFactory().getBillsRetrofit();
    private RealmElectricityBill realmRepo = new RealmElectricityBill();
    private String phone = null;

    public BillsAPIRepository() {
        if (realmRepo.getUserNum().startsWith("98")) {
            phone = realmRepo.getUserNum().replaceFirst("98", "0");
        }
    }

    public void addBill(BillInfo info, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<String>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<String>>().initAPI(apiService.addBill(
                info.getBillTypeString(), info.getTitle(), phone, info.getBillID(),
                info.getGasID(), info.getPhoneNum(), info.getAreaCode()), handShakeCallback, apiResponse);
    }

    public void editBill(BillInfo info, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<String>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<String>>().initAPI(apiService.editBill(
                info.getServerID(), info.getBillTypeString(), info.getTitle(), info.getBillID(),
                info.getGasID(), info.getPhoneNum(), info.getAreaCode()), handShakeCallback, apiResponse);
    }

    public void deleteBill(BillInfo info, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<String>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<String>>().initAPI(apiService.deleteBill(info.getServerID(), info.getBillTypeString()), handShakeCallback, apiResponse);
    }

    public void getBillList(HandShakeCallback handShakeCallback, ResponseCallback<BillList> apiResponse) {
        new ApiInitializer<BillList>().initAPI(apiService.getBillsList(0, 100), handShakeCallback, apiResponse);
    }

    public void serviceInquiry(BillInfo info, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<ServiceDebit>> apiResponse) {
        if (info.getBillTypeString().equals("ELECTRICITY")) {
            new ApiInitializer<ElectricityResponseModel<ServiceDebit>>().initAPI(apiService.inquiryElectricityBill(info.getBillTypeString(), info.getBillID(), info.getMobileNum() != null ? info.getMobileNum() : phone), handShakeCallback, apiResponse);
        } else if (info.getBillTypeString().equals("GAS")) {
            new ApiInitializer<ElectricityResponseModel<ServiceDebit>>().initAPI(apiService.inquiryGasBill(info.getBillTypeString(), info.getGasID()), handShakeCallback, apiResponse);
        }

    }

    public void phoneAndMobileInquiry(BillInfo info, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<MobileDebit>> apiResponse) {
        if (info.getBillTypeString().equals("PHONE")) {
            new ApiInitializer<ElectricityResponseModel<MobileDebit>>().initAPI(apiService.inquiryPhoneBill(info.getBillTypeString(), info.getPhoneNum(), info.getAreaCode()), handShakeCallback, apiResponse);
        } else if (info.getBillTypeString().equals("MOBILE_MCI")) {
            new ApiInitializer<ElectricityResponseModel<MobileDebit>>().initAPI(apiService.inquiryMobileBill(info.getBillTypeString(), info.getPhoneNum()), handShakeCallback, apiResponse);
        }

    }

    public void getElectricityBranchInfo(BillInfo info, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<ElectricityBranchData>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<ElectricityBranchData>>().initAPI(apiService.getElectricityBranchInfo(info.getBillTypeString(), info.getBillID()), handShakeCallback, apiResponse);
    }

    public void getGasBranchInfo(BillInfo info, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<GasBranchData>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<GasBranchData>>().initAPI(apiService.getGasBranchInfo(info.getBillTypeString(), info.getGasID()), handShakeCallback, apiResponse);
    }

    public void getLastBill(BillInfo info, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<LastBillData>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<LastBillData>>().initAPI(apiService.getLastBill(info.getBillTypeString(), info.getBillID()), handShakeCallback, apiResponse);
    }
}
