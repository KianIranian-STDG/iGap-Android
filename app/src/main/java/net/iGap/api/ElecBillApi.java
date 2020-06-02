package net.iGap.api;

import net.iGap.model.bill.ServiceDebit;
import net.iGap.model.electricity_bill.BillData;
import net.iGap.model.electricity_bill.CompanyList;
import net.iGap.model.electricity_bill.ElectricityBranchData;
import net.iGap.model.electricity_bill.ElectricityResponseList;
import net.iGap.model.electricity_bill.ElectricityResponseModel;
import net.iGap.model.electricity_bill.LastBillData;
import net.iGap.model.electricity_bill.PaidBill;
import net.iGap.model.electricity_bill.SaleBill;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ElecBillApi {

    @GET("get-companies")
    Call<CompanyList> getCompanies();

    @FormUrlEncoded
    @POST("add-bill")
    Call<ElectricityResponseModel<String>> addBill(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum,
                                           @Field("national_code") String userNID, @Field("email") String userEmail,
                                           @Field("bill_title") String billTitle, @Field("via_sms") boolean smsEnable,
                                           @Field("via_print") boolean printEnable, @Field("via_email") boolean emailEnable,
                                           @Field("via_app") boolean appEnable);

    @FormUrlEncoded
    @POST("edit-bill")
    Call<ElectricityResponseModel<String>> editBill(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum,
                       @Field("national_code") String userNID, @Field("email") String userEmail,
                       @Field("bill_title") String billTitle, @Field("via_sms") boolean smsEnable,
                       @Field("via_print") boolean printEnable, @Field("via_email") boolean emailEnable,
                       @Field("via_app") boolean appEnable);

    @FormUrlEncoded
    @POST("delete-bill")
    Call<ElectricityResponseModel<String>> deleteBill(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum,
                        @Field("national_code") String userNID);

    @FormUrlEncoded
    @POST("get-bills")
    Call<ElectricityResponseModel<BillData>> getBillList(@Field("mobile_number") String mobileNum);

    @FormUrlEncoded
    @POST("delete-account")
    Call<ElectricityResponseModel<String>> deleteAccount(@Field("mobile_number") String mobileNum, @Field("national_code") String userNID);

    @FormUrlEncoded
    @POST("get-branch-info")
    Call<ElectricityResponseModel<ElectricityBranchData>> getBranchInfo(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum);

    @FormUrlEncoded
    @POST("get-branch-debit")
    Call<ElectricityResponseModel<ServiceDebit>> getBranchDebit(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum);

    @FormUrlEncoded
    @POST("search-bill")
    Call<ElectricityResponseList<ElectricityBranchData>> searchBills(@Field("serial_number") String serialNum, @Field("company_code") String companyCode);

    @FormUrlEncoded
    @POST("get-sale-bills")
    Call<ElectricityResponseList<SaleBill>> getSaleBills(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum,
                                                         @Field("from_year") String fromYear);
    @FormUrlEncoded
    @POST("get-paid-bills")
    Call<ElectricityResponseList<PaidBill>> getPaiedBills(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum,
                                                          @Field("from_year") String fromYear);
    @FormUrlEncoded
    @POST("get-last-bill")
    Call<ElectricityResponseModel<LastBillData>> getLastBill(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum);

}
