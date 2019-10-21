package net.iGap.electricity_bill.repository.api;

import net.iGap.api.ElecBillApi;
import net.iGap.api.NewsApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.api.apiService.HandShakeCallback;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.electricity_bill.repository.model.BillData;
import net.iGap.electricity_bill.repository.model.BillRegister;
import net.iGap.electricity_bill.repository.model.BranchData;
import net.iGap.electricity_bill.repository.model.BranchDebit;
import net.iGap.electricity_bill.repository.model.CompanyList;
import net.iGap.electricity_bill.repository.model.ElectricityResponseList;
import net.iGap.electricity_bill.repository.model.ElectricityResponseModel;
import net.iGap.electricity_bill.repository.model.LastBillData;
import net.iGap.electricity_bill.repository.model.PaidBill;
import net.iGap.electricity_bill.repository.model.SaleBill;
import net.iGap.news.repository.model.NewsGroup;
import net.iGap.news.repository.model.NewsList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ElectricityBillAPIRepository {

    private ElecBillApi apiService = ApiServiceProvider.getElecBillClient();

    public void getNewsGroup(HandShakeCallback handShakeCallback, ResponseCallback<CompanyList> apiResponse) {
        new ApiInitializer<CompanyList>().initAPI(apiService.getCompanies(), handShakeCallback, apiResponse);
    }

    public void addBill(BillRegister info, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<String>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<String>>().initAPI(apiService.addBill(info.getID(), info.getMobileNum(),
                info.getNID(), info.getEmail(), info.getTitle(), info.isSMSEnable(), info.isPrintEnable(), info.isEmailEnable(),
                info.isAppEnable()), handShakeCallback, apiResponse);
    }

    public void editBill(BillRegister info, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<String>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<String>>().initAPI(apiService.editBill(info.getID(), info.getMobileNum(),
                info.getNID(), info.getEmail(), info.getTitle(), info.isSMSEnable(), info.isPrintEnable(), info.isEmailEnable(),
                info.isAppEnable()), handShakeCallback, apiResponse);
    }

    public void deleteBill(BillRegister info, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<String>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<String>>().initAPI(apiService.deleteBill(info.getID(), info.getMobileNum(),
                info.getNID()), handShakeCallback, apiResponse);
    }

    public void getBillList(String phoneNum, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<BillData>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<BillData>>().initAPI(apiService.getBillList(phoneNum), handShakeCallback, apiResponse);
    }

    public void deleteAccount(String phoneNum, String NID, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<String>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<String>>().initAPI(apiService.deleteAccount(phoneNum, NID), handShakeCallback, apiResponse);
    }

    public void getBranchInfo(String billID, String phoneNum, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<BranchData>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<BranchData>>().initAPI(apiService.getBranchInfo(billID, phoneNum), handShakeCallback, apiResponse);
    }

    public void getBranchDebit(String billID, String phoneNum, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<BranchDebit>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<BranchDebit>>().initAPI(apiService.getBranchDebit(billID, phoneNum), handShakeCallback, apiResponse);
    }

    public void searchBills(String serialNum, String companyCode, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseList<BranchData>> apiResponse) {
        new ApiInitializer<ElectricityResponseList<BranchData>>().initAPI(apiService.searchBills(serialNum, companyCode), handShakeCallback, apiResponse);
    }

    public void getSaleBills(String billID, String phoneNum, String fromYear, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseList<SaleBill>> apiResponse) {
        new ApiInitializer<ElectricityResponseList<SaleBill>>().initAPI(apiService.getSaleBills(billID, phoneNum, fromYear), handShakeCallback, apiResponse);
    }

    public void getPaiedBills(String billID, String phoneNum, String fromYear, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseList<PaidBill>> apiResponse) {
        new ApiInitializer<ElectricityResponseList<PaidBill>>().initAPI(apiService.getPaiedBills(billID, phoneNum, fromYear), handShakeCallback, apiResponse);
    }

    public void getLastBill(String billID, String phoneNum, HandShakeCallback handShakeCallback, ResponseCallback<ElectricityResponseModel<LastBillData>> apiResponse) {
        new ApiInitializer<ElectricityResponseModel<LastBillData>>().initAPI(apiService.getLastBill(billID, phoneNum), handShakeCallback, apiResponse);
    }
}
