package net.iGap.repository;

import net.iGap.api.ElecBillApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.model.electricity_bill.BillData;
import net.iGap.model.electricity_bill.BillRegister;
import net.iGap.model.electricity_bill.BranchData;
import net.iGap.model.electricity_bill.BranchDebit;
import net.iGap.model.electricity_bill.CompanyList;
import net.iGap.model.electricity_bill.ElectricityResponseList;
import net.iGap.model.electricity_bill.ElectricityResponseModel;
import net.iGap.model.electricity_bill.LastBillData;
import net.iGap.model.electricity_bill.PaidBill;
import net.iGap.model.electricity_bill.SaleBill;
import net.iGap.realm.RealmElectricityBill;

public class ElectricityBillAPIRepository {

    private ElecBillApi apiService =  new RetrofitFactory().getElecBillRetrofit();
    private RealmElectricityBill realmRepo = new RealmElectricityBill();
    private String phone = null;

    public ElectricityBillAPIRepository() {
        if (realmRepo.getUserNum().startsWith("98")) {
            phone = realmRepo.getUserNum().replaceFirst("98", "0");
        }
    }

    public void getCompanies(HandShakeCallback handShakeCallback, ResponseCallback<CompanyList> apiResponse) {
        new ApiInitializer<CompanyList>().initAPI(apiService.getCompanies(), handShakeCallback, apiResponse);
    }

    public void addBill(BillRegister info, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<String>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<String>>().initAPI(apiService.addBill(info.getID(), phone,
                info.getNID(), info.getEmail(), info.getTitle(), info.isSMSEnable(), info.isPrintEnable(), info.isEmailEnable(),
                info.isAppEnable()), handShakeCallback, apiResponse);
    }

    public void editBill(BillRegister info, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<String>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<String>>().initAPI(apiService.editBill(info.getID(), phone,
                null/*info.getNID()*/, info.getEmail(), info.getTitle(), info.isSMSEnable(), info.isPrintEnable(), info.isEmailEnable(),
                info.isAppEnable()), handShakeCallback, apiResponse);
    }

    public void deleteBill(BillRegister info, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<String>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<String>>().initAPI(apiService.deleteBill(info.getID(), phone,
                info.getNID()), handShakeCallback, apiResponse);
    }

    public void getBillList(HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<BillData>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<BillData>>().initAPI(apiService.getBillList(phone), handShakeCallback, apiResponse);
    }

    public void deleteAccount(String NID, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<String>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<String>>().initAPI(apiService.deleteAccount(phone, NID), handShakeCallback, apiResponse);
    }

    public void getBranchInfo(String billID, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<BranchData>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<BranchData>>().initAPI(apiService.getBranchInfo(billID, phone), handShakeCallback, apiResponse);
    }

    public void getBranchDebit(String billID, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<BranchDebit>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<BranchDebit>>().initAPI(apiService.getBranchDebit(billID, phone), handShakeCallback, apiResponse);
    }

    public void searchBills(String serialNum, String companyCode, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseList<BranchData>> apiResponse) {
        new ApiInitializer<ElectricityResponseList<BranchData>>().initAPI(apiService.searchBills(serialNum, companyCode), handShakeCallback, apiResponse);
    }

    public void getSaleBills(String billID, String fromYear, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseList<SaleBill>> apiResponse) {
        new ApiInitializer<ElectricityResponseList<SaleBill>>().initAPI(apiService.getSaleBills(billID, phone, fromYear), handShakeCallback, apiResponse);
    }

    public void getPaiedBills(String billID, String fromYear, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseList<PaidBill>> apiResponse) {
        new ApiInitializer<ElectricityResponseList<PaidBill>>().initAPI(apiService.getPaiedBills(billID, phone, fromYear), handShakeCallback, apiResponse);
    }

    public void getLastBill(String billID, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<LastBillData>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<LastBillData>>().initAPI(apiService.getLastBill(billID, phone), handShakeCallback, apiResponse);
    }
}