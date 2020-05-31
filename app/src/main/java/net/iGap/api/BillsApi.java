package net.iGap.api;

import net.iGap.model.bill.BillList;
import net.iGap.model.electricity_bill.BranchData;
import net.iGap.model.electricity_bill.BranchDebit;
import net.iGap.model.electricity_bill.CompanyList;
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
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BillsApi {

    @GET("get-companies")
    Call<CompanyList> getCompanies();

    @FormUrlEncoded
    @POST("add-bill")
    Call<ElectricityResponseModel<String>> addBill(@Field("bill_type") String billType,
                                                   @Field("bill_title") String billTitle,
                                                   @Field("mobile_number") String mobileNumber,
                                                   @Field("bill_identifier") String billID,
                                                   @Field("phone_number") String phoneNumber,
                                                   @Field("area_code") String areaCode);

    @FormUrlEncoded
    @POST("edit-bill/{billServerID}")
    Call<ElectricityResponseModel<String>> editBill(@Path("billServerID") String billServerID,
                                                    @Field("bill_type") String billType,
                                                    @Field("bill_title") String billTitle,
                                                    @Field("bill_identifier") String billID,
                                                    @Field("phone_number") String phoneNumber,
                                                    @Field("area_code") String areaCode);

    @FormUrlEncoded
    @POST("delete-bill/{billServerID}")
    Call<ElectricityResponseModel<String>> deleteBill(@Path("billServerID") String billServerID,
                                                      @Field("bill_type") String billType);

    @FormUrlEncoded
    @POST("get-bills")
    Call<BillList> getBillsList(@Query("skip") int offset,
                                @Query("limit") int limit);

    @FormUrlEncoded
    @POST("get-inquiry")
    Call<ElectricityResponseModel<String>> inquiryBill(@Field("mobile_number") String mobileNum, @Field("national_code") String userNID);

    @FormUrlEncoded
    @POST("get-branch-info")
    Call<ElectricityResponseModel<BranchData>> getBranchInfo(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum);

    @FormUrlEncoded
    @POST("get-branch-debit")
    Call<ElectricityResponseModel<BranchDebit>> getBranchDebit(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum);

    @FormUrlEncoded
    @POST("search-bill")
    Call<ElectricityResponseList<BranchData>> searchBills(@Field("serial_number") String serialNum, @Field("company_code") String companyCode);

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
