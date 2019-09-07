package net.iGap.igasht;

import net.iGap.api.IgashtApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.api.errorhandler.ErrorHandler;
import net.iGap.api.errorhandler.ResponseCallback;
import net.iGap.igasht.barcodescaner.TicketQRCodeResponse;
import net.iGap.igasht.historylocation.IGashtTicketDetail;
import net.iGap.igasht.historylocation.TicketHistoryListResponse;
import net.iGap.igasht.locationdetail.RegisterTicketResponse;
import net.iGap.igasht.locationdetail.buyticket.IGashtLocationService;
import net.iGap.igasht.locationdetail.buyticket.IGashtOrder;
import net.iGap.igasht.locationdetail.buyticket.IGashtVouchers;
import net.iGap.igasht.locationlist.IGashtLocationItem;
import net.iGap.igasht.provinceselect.IGashtProvince;
import net.iGap.realm.RealmUserInfo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IGashtRepository {

    //singleton
    private static IGashtRepository instance;
    private IgashtApi igashtApi;

    private IGashtProvince selectedProvince;
    private IGashtLocationItem selectedLocation;
    private List<IGashtVouchers> selectedServiceList;
    private List<IGashtProvince> provinceList;

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
        igashtApi = new RetrofitFactory().getIgashtRetrofit().create(IgashtApi.class);
        selectedServiceList = new ArrayList<>();
    }

    public void setSelectedProvince(IGashtProvince selectedProvince) {
        this.selectedProvince = selectedProvince;
    }

    public IGashtProvince getSelectedProvince() {
        return selectedProvince;
    }

    public List<IGashtProvince> getProvinceList() {
        return provinceList;
    }

    public IGashtLocationItem getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(IGashtLocationItem selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public void getProvinceList(ResponseCallback<BaseIGashtResponse<IGashtProvince>> callback) {
        igashtApi.requestGetProvinceList().enqueue(new Callback<BaseIGashtResponse<IGashtProvince>>() {
            @Override
            public void onResponse(@NotNull Call<BaseIGashtResponse<IGashtProvince>> call, @NotNull Response<BaseIGashtResponse<IGashtProvince>> response) {
                if (response.code() == 200) {
                    provinceList = response.body().getData();
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError(new ErrorHandler().getError(response.code(), response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<BaseIGashtResponse<IGashtProvince>> call, @NotNull Throwable t) {
                t.printStackTrace();
                callback.onFailed(new ErrorHandler().checkHandShakeFailure(t));
            }
        });
    }

    public void getLocationListWithProvince(ResponseCallback<BaseIGashtResponse<IGashtLocationItem>> callback) {
        igashtApi.requestGetLocationList(selectedProvince.getId()).enqueue(new Callback<BaseIGashtResponse<IGashtLocationItem>>() {
            @Override
            public void onResponse(@NotNull Call<BaseIGashtResponse<IGashtLocationItem>> call, @NotNull Response<BaseIGashtResponse<IGashtLocationItem>> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError(new ErrorHandler().getError(response.code(), response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<BaseIGashtResponse<IGashtLocationItem>> call, @NotNull Throwable t) {
                t.printStackTrace();
                callback.onFailed(new ErrorHandler().checkHandShakeFailure(t));
            }
        });
    }

    public void getServiceList(ResponseCallback<BaseIGashtResponse<IGashtLocationService>> callback) {
        igashtApi.requestGetServiceList(selectedLocation.getId()).enqueue(new Callback<BaseIGashtResponse<IGashtLocationService>>() {
            @Override
            public void onResponse(@NotNull Call<BaseIGashtResponse<IGashtLocationService>> call, @NotNull Response<BaseIGashtResponse<IGashtLocationService>> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError(new ErrorHandler().getError(response.code(), response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<BaseIGashtResponse<IGashtLocationService>> call, @NotNull Throwable t) {
                t.printStackTrace();
                callback.onFailed(new ErrorHandler().checkHandShakeFailure(t));
            }
        });
    }

    public void getHistoryList(int offset, int limit, ResponseCallback<TicketHistoryListResponse<IGashtTicketDetail>> callback) {
        igashtApi.requestGetTicketList(offset, limit).enqueue(new Callback<TicketHistoryListResponse<IGashtTicketDetail>>() {
            @Override
            public void onResponse(@NotNull Call<TicketHistoryListResponse<IGashtTicketDetail>> call, @NotNull Response<TicketHistoryListResponse<IGashtTicketDetail>> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError(new ErrorHandler().getError(response.code(), response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<TicketHistoryListResponse<IGashtTicketDetail>> call, @NotNull Throwable t) {
                t.printStackTrace();
                callback.onFailed(new ErrorHandler().checkHandShakeFailure(t));
            }
        });
    }

    public void registeredOrder(ResponseCallback<RegisterTicketResponse> callback) {
        try (Realm realm = Realm.getDefaultInstance()) {
            igashtApi.registerOrder(new IGashtOrder(realm.where(RealmUserInfo.class).findFirst().getUserInfo().getPhoneNumber(),
                    1,
                    selectedProvince.getId(),
                    selectedLocation.getId(),
                    selectedServiceList
            )).enqueue(new Callback<RegisterTicketResponse>() {
                @Override
                public void onResponse(@NotNull Call<RegisterTicketResponse> call, @NotNull Response<RegisterTicketResponse> response) {
                    if (response.code() == 200) {
                        callback.onSuccess(response.body());
                    } else {
                        try {
                            callback.onError(new ErrorHandler().getError(response.code(), response.errorBody().string()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<RegisterTicketResponse> call, @NotNull Throwable t) {
                    t.printStackTrace();
                    callback.onFailed(new ErrorHandler().checkHandShakeFailure(t));
                }
            });
        }
    }

    public void getTicketQRCode(String voucherNumber, ResponseCallback<String> callback) {
        igashtApi.requestGetTicketQRCode(voucherNumber).enqueue(new Callback<TicketQRCodeResponse>() {
            @Override
            public void onResponse(@NotNull Call<TicketQRCodeResponse> call, @NotNull Response<TicketQRCodeResponse> response) {
                if (response.code() == 200) {
                    callback.onSuccess(response.body().getQrCode());
                } else {
                    try {
                        callback.onError(new ErrorHandler().getError(response.code(), response.errorBody().string()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<TicketQRCodeResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
                callback.onFailed(new ErrorHandler().checkHandShakeFailure(t));
            }
        });
    }

    public void createVoucherList(@NotNull List<IGashtLocationService> data) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getCount() > 0) {
                selectedServiceList.add(new IGashtVouchers(data.get(i).getPersianTicket().getVoucherinfoId(), data.get(i).getCount()));
            }
        }
    }

    public void clearSelectedServiceList() {
        selectedServiceList.clear();
    }

    public boolean hasVoucher() {
        return selectedServiceList.size() != 0;
    }
}
