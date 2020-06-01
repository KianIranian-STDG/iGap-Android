package net.iGap.api;

import net.iGap.model.bill.BillList;
import net.iGap.model.bill.GasBranchData;
import net.iGap.model.bill.MobileDebit;
import net.iGap.model.electricity_bill.CompanyList;
import net.iGap.model.electricity_bill.ElectricityBranchData;
import net.iGap.model.electricity_bill.ElectricityResponseModel;
import net.iGap.model.electricity_bill.LastBillData;
import net.iGap.model.electricity_bill.ServiceDebit;

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
                                                   @Field("bill_identifier") String elecBillID,
                                                   @Field("subscription_code") String gasSubsID,
                                                   @Field("phone_number") String phoneNumber,
                                                   @Field("area_code") String areaCode);

    @FormUrlEncoded
    @POST("edit-bill/{billServerID}")
    Call<ElectricityResponseModel<String>> editBill(@Path("billServerID") String billServerID,
                                                    @Field("bill_type") String billType,
                                                    @Field("bill_title") String billTitle,
                                                    @Field("bill_identifier") String elecBillID,
                                                    @Field("subscription_code") String gasSubsID,
                                                    @Field("phone_number") String phoneNumber,
                                                    @Field("area_code") String areaCode);

    @FormUrlEncoded
    @POST("delete-bill/{billServerID}")
    Call<ElectricityResponseModel<String>> deleteBill(@Path("billServerID") String billServerID,
                                                      @Field("bill_type") String billType);

    @GET("get-bills")
    Call<BillList> getBillsList(@Query("skip") int offset,
                                @Query("limit") int limit);

    @FormUrlEncoded
    @POST("get-inquiry")
    Call<ElectricityResponseModel<ServiceDebit>> inquiryElectricityBill(@Field("bill_type") String billType,
                                                                        @Field("bill_identifier") String billID,
                                                                        @Field("mobile_number") String phoneNumber);

    @FormUrlEncoded
    @POST("get-inquiry")
    Call<ElectricityResponseModel<MobileDebit>> inquiryMobileBill(@Field("bill_type") String billType,
                                                                  @Field("phone_number") String phoneNumber);

    @FormUrlEncoded
    @POST("get-inquiry")
    Call<ElectricityResponseModel<MobileDebit>> inquiryPhoneBill(@Field("bill_type") String billType,
                                                                 @Field("phone_number") String phoneNumber,
                                                                 @Field("area_code") String areaCode);

    @FormUrlEncoded
    @POST("get-inquiry")
    Call<ElectricityResponseModel<ServiceDebit>> inquiryGasBill(@Field("bill_type") String billType,
                                                                @Field("subscription_code") String billID);

    @FormUrlEncoded
    @POST("get-details")
    Call<ElectricityResponseModel<ElectricityBranchData>> getElectricityBranchInfo(@Field("bill_type") String billType,
                                                                                   @Field("bill_identifier") String billID);

    @FormUrlEncoded
    @POST("get-details")
    Call<ElectricityResponseModel<GasBranchData>> getGasBranchInfo(@Field("bill_type") String billType,
                                                                   @Field("subscription_code") String billID);

    @FormUrlEncoded
    @POST("get-last-bill-image")
    Call<ElectricityResponseModel<LastBillData>> getLastBill(@Field("bill_type") String billType,
                                                             @Field("bill_identifier") String billID);

}
