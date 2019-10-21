package net.iGap.api;

import net.iGap.electricity_bill.repository.model.Bill;
import net.iGap.news.repository.model.NewsGroup;
import net.iGap.news.repository.model.NewsList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ElecBillApi {

    @GET("get-companies")
    Call<Bill> getCompanies();

    @POST("add-bill")
    Call<Bill> addBill(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum,
                       @Field("national_code") String userNID, @Field("email") String userEmail,
                       @Field("bill_title") String billTitle, @Field("via_sms") boolean smsEnable,
                       @Field("via_print") boolean printEnable, @Field("via_email") boolean emailEnable,
                       @Field("via_app") boolean appEnable);

    @POST("edit-bill")
    Call<Bill> editBill(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum,
                       @Field("national_code") String userNID, @Field("email") String userEmail,
                       @Field("bill_title") String billTitle, @Field("via_sms") boolean smsEnable,
                       @Field("via_print") boolean printEnable, @Field("via_email") boolean emailEnable,
                       @Field("via_app") boolean appEnable);

    @POST("delete-bill")
    Call<Bill> deleteBill(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum,
                        @Field("national_code") String userNID);

    @POST("get-bills")
    Call<Bill> getBillList(@Field("mobile_number") String mobileNum);

    @POST("delete-account")
    Call<Bill> deleteAccount(@Field("mobile_number") String mobileNum, @Field("national_code") String userNID);

    @POST("get-branch-info")
    Call<Bill> getBranchInfo(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum);

    @POST("get-branch-info")
    Call<Bill> getBranchDebit(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum);

    @POST("search-bill")
    Call<Bill> searchBills(@Field("serial_number") String serialNum, @Field("company_code") String companyCode);

    @POST("get-sale-bills")
    Call<Bill> getSaleBills(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum,
                          @Field("from_year") String fromYear);

    @POST("get-paid-bills")
    Call<Bill> getPaiedBills(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum,
                            @Field("from_year") String fromYear);

    @POST("get-last-bill")
    Call<Bill> getLastBill(@Field("bill_identifier") String billID, @Field("mobile_number") String mobileNum);

    @POST("pay-bill")
    Call<Bill> getLastBill(@Field("bill_identifier") String billID, @Field("payment_identifier") String payID,
                           @Field("amount") String amount);
}
