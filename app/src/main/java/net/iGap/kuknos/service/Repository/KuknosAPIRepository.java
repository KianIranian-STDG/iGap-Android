package net.iGap.kuknos.service.Repository;

import net.iGap.api.KuknosApi;
import net.iGap.api.apiService.RetrofitFactory;

public class KuknosAPIRepository {
    private KuknosApi apiService = new RetrofitFactory().getKuknosRetrofit();
    //private KuknosHorizenApi apiHorizenService = ApiServiceProvider.getKuknosHorizonClient();

    /*public void getUserAuthentication(String phoneNum, String nID, ApiResponse<KuknosLoginM> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getUserVerfication(phoneNum, nID).enqueue(new Callback<KuknosLoginM>() {
            @Override
            public void onResponse(Call<KuknosLoginM> call, Response<KuknosLoginM> response) {
                if (response.isSuccessful())
                    apiResponse.onResponse(response.body());
                else if (response.code() == 400) {
                    apiResponse.onFailed("registeredBefore");
                }
                apiResponse.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<KuknosLoginM> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.setProgressIndicator(false);
            }
        });
    }*/

    /*public void getUserInfo(String publicKey, ApiResponse<KuknosInfoM> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.getUserInfo(publicKey).enqueue(new Callback<KuknosInfoM>() {
            @Override
            public void onResponse(Call<KuknosInfoM> call, Response<KuknosInfoM> response) {
                if (response.isSuccessful())
                    apiResponse.onResponse(response.body());
                else if (response.code() == 400)
                    apiResponse.onFailed("notRegisteredBefore");
                apiResponse.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<KuknosInfoM> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.setProgressIndicator(false);
            }
        });
    }*/

    /*public void checkUser(String phoneNum, String nID, ApiResponse<KuknoscheckUserM> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.checkUser(phoneNum, nID).enqueue(new Callback<KuknoscheckUserM>() {
            @Override
            public void onResponse(Call<KuknoscheckUserM> call, Response<KuknoscheckUserM> response) {
                apiResponse.onResponse(response.body());
                apiResponse.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<KuknoscheckUserM> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.setProgressIndicator(false);
            }
        });
    }*/

    /*public void registerUser(String token, String publicKey, String friendlyID, ApiResponse<KuknosSubmitM> apiResponse) {
        apiResponse.setProgressIndicator(true);
        apiService.registerUser(token, publicKey, friendlyID + "*igap.net").enqueue(new Callback<KuknosSubmitM>() {
            @Override
            public void onResponse(Call<KuknosSubmitM> call, Response<KuknosSubmitM> response) {
                if (response.isSuccessful())
                    apiResponse.onResponse(response.body());
                else if (response.code() == 400) {
                    apiResponse.onFailed("Server Error");
                }
                apiResponse.setProgressIndicator(false);
            }

            @Override
            public void onFailure(Call<KuknosSubmitM> call, Throwable t) {
                apiResponse.onFailed(t.getMessage());
                apiResponse.setProgressIndicator(false);
            }
        });
    }*/

    /*public void getUserAccount(String userID, ApiResponse<AccountResponse> apiResponse) {
        KuknosAPIAsync<AccountResponse> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.USER_ACCOUNT);
        temp.execute(userID);
    }*/

    /*public void paymentUser(KuknosSendM model, ApiResponse<SubmitTransactionResponse> apiResponse) {
        KuknosAPIAsync<SubmitTransactionResponse> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.PAYMENT_SEND);
        temp.execute(model.getSrc(), model.getDest(), model.getAmount(), model.getMemo());
    }*/

    /*public void getUserHistory(String userID, ApiResponse<Page<OperationResponse>> apiResponse) {
        KuknosAPIAsync<Page<OperationResponse>> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.PAYMENTS_ACCOUNT);
        temp.execute(userID);
    }*/

    /*public void getAssets(ApiResponse<Page<AssetResponse>> apiResponse) {
        KuknosAPIAsync<Page<AssetResponse>> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.ASSETS);
        temp.execute();
    }*/

    /*public void changeTrust(String accountSeed, String code, String issuer, ApiResponse<SubmitTransactionResponse> apiResponse) {
        KuknosAPIAsync<SubmitTransactionResponse> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.CHANGE_TRUST);
        temp.execute(accountSeed, code, issuer);
    }*/

    /*public void getOffersList(String userID, ApiResponse<Page<OfferResponse>> apiResponse) {
        KuknosAPIAsync<Page<OfferResponse>> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.OFFERS_LIST);
        temp.execute(userID);
    }*/

    /*public void getTradesList(String userID, ApiResponse<Page<OfferResponse>> apiResponse) {
        KuknosAPIAsync<Page<OfferResponse>> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.TRADES_LIST);
        temp.execute(userID);
    }*/

    /*public void manageOffer(String accountSeed, String sourceCode, String sourceIssuer,
                            String counterCode, String counterIssuer, ApiResponse<SubmitTransactionResponse> apiResponse) {
        KuknosAPIAsync<SubmitTransactionResponse> temp = new KuknosAPIAsync(apiResponse, KuknosAPIAsync.API.MANAGE_OFFER);
        temp.execute(accountSeed, sourceCode, sourceIssuer, counterCode, counterIssuer);
    }*/
}
