package net.iGap.repository;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.IgashtApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.helper.HelperError;
import net.iGap.model.igasht.BaseIGashtResponse;
import net.iGap.model.igasht.IGashtLocationItem;
import net.iGap.model.igasht.IGashtLocationService;
import net.iGap.model.igasht.IGashtProvince;
import net.iGap.model.igasht.IGashtTicketDetail;
import net.iGap.model.igasht.IGashtVouchers;
import net.iGap.model.igasht.TicketHistoryListResponse;
import net.iGap.model.igasht.TicketQRCodeResponse;
import net.iGap.model.igasht.purchaseResponse;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.realm.RealmUserInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IGashtRepository {

    //singleton
    private static IGashtRepository instance;
    private IgashtApi igashtApi;

    private IGashtProvince selectedProvince;
    private IGashtLocationItem selectedLocation;
    private List<IGashtVouchers> selectedServiceList;
    private List<IGashtProvince> provinceList;
    private List<IGashtLocationService> locationServices;
    private int count;
    private int voucherId;
    private Object serviceTime;

    public static IGashtRepository getInstance() {
        if (instance == null) {
            instance = new IGashtRepository();
        }
        return instance;
    }

    public void clearInstance() {
        instance = null;
    }

    private IGashtRepository() {
        igashtApi = new RetrofitFactory().getIgashtRetrofit();
        selectedServiceList = new ArrayList<>();
    }

    public void setSelectedProvince(IGashtProvince selectedProvince) {
        this.selectedProvince = selectedProvince;
    }

    public void setLocationServices(List<IGashtLocationService> locationServices) {
        this.locationServices = locationServices;
    }

    public List<IGashtLocationService> getLocationServices() {
        return locationServices;
    }

    public IGashtProvince getSelectedProvince() {
        return selectedProvince;
    }

    public List<IGashtProvince> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(List<IGashtProvince> provinceList) {
        this.provinceList = provinceList;
    }

    public IGashtLocationItem getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(IGashtLocationItem selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public void getProvinceList(HandShakeCallback handShakeCallback, ResponseCallback<BaseIGashtResponse<IGashtProvince>> callback) {
        new ApiInitializer<BaseIGashtResponse<IGashtProvince>>().initAPI(igashtApi.requestGetProvinceList(), handShakeCallback, callback);
    }

    public void getLocationListWithProvince(HandShakeCallback handShakeCallback, ResponseCallback<BaseIGashtResponse<IGashtLocationItem>> callback) {
        new ApiInitializer<BaseIGashtResponse<IGashtLocationItem>>().initAPI(igashtApi.requestGetLocationList(selectedProvince.getId()), handShakeCallback, callback);
    }

    public void getServiceList(HandShakeCallback handShakeCallback, ResponseCallback<BaseIGashtResponse<IGashtLocationService>> callback) {
        new ApiInitializer<BaseIGashtResponse<IGashtLocationService>>().initAPI(igashtApi.requestGetServiceList(selectedLocation.getId()), handShakeCallback, callback);
    }

    public void getHistoryList(int offset, int limit, HandShakeCallback handShakeCallback, ResponseCallback<TicketHistoryListResponse<IGashtTicketDetail>> callback) {
        new ApiInitializer<TicketHistoryListResponse<IGashtTicketDetail>>().initAPI(igashtApi.requestGetTicketList(offset, limit), handShakeCallback, callback);
    }

    public void getTicketQRCode(String voucherNumber, HandShakeCallback handShakeCallback, ResponseCallback<TicketQRCodeResponse> callback) {
        new ApiInitializer<TicketQRCodeResponse>().initAPI(igashtApi.requestGetTicketQRCode(voucherNumber), handShakeCallback, callback);
    }

    public void createVoucherList(@NotNull List<IGashtLocationService> data) {
            for (int i = 0; i < data.size(); i++) {
                selectedServiceList.add(new IGashtVouchers(data.get(i).getPersianTicket().getVoucherinfoId(), data.get(i).getCount()));
                count = getLocationServices().get(i).getCount();
                voucherId = getLocationServices().get(i).getAmounts().get(i).getVoucherinfoId();
            }
    }

    public void getRegisteredOrder(HandShakeCallback handShakeCallback, ResponseCallback<purchaseResponse> callback) {
        DbManager.getInstance().doRealmTask(realm -> {
            JsonObject jsonObject1 = new JsonObject();
            jsonObject1.addProperty("voucher_id", voucherId);
            jsonObject1.addProperty("count", count);
            //   jsonObject1.addProperty("service_time_id", serviceTime.toString());
            JsonArray vouchers = new JsonArray();
            vouchers.add(jsonObject1);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("phone_number", Objects.requireNonNull(realm.where(RealmUserInfo.class).findFirst()).getUserInfo().getPhoneNumber().replaceAll("^98", "0"));
            jsonObject.addProperty("province_id", selectedProvince.getId());
            jsonObject.add("vouchers", vouchers);

            new ApiInitializer<purchaseResponse>().initAPI(igashtApi.requestRegisterOrder(jsonObject), handShakeCallback, callback);

        });
    }

    public void clearSelectedServiceList() {
        selectedServiceList.clear();
    }

    public boolean hasVoucher() {
        return selectedServiceList.size() != 0;
    }
}
