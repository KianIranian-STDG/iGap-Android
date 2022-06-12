package net.iGap.repository;

import com.google.gson.JsonObject;

import net.iGap.api.ai.AiApi;
import net.iGap.api.apiService.ApiInitializer;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.model.ai.AiTokenModel;
import net.iGap.model.ai.VoiceToTextModel;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AIMessageRepository {

    private final AiApi aiApi;

    public AIMessageRepository() {
        aiApi = new RetrofitFactory().getAiApi();
    }

    public void getAiToken(ResponseCallback<AiTokenModel> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("username", "igap");
        params.put("password", "Zvbifr5jD8KC6ty");
        params.put("grant_type", "password");
        params.put("client_id", "AOiVyGdhud8dLZuBRfnK9to3ndhKfetMffRXFnWu");
        aiApi.getAiToken(params).clone().enqueue(new Callback<AiTokenModel>() {
            @Override
            public void onResponse(Call<AiTokenModel> call, Response<AiTokenModel> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<AiTokenModel> call, Throwable t) {
                callback.onFailed();
            }
        });
    }

    public void getAiVoiceToText(String vadMode, String asrMode, String langBoost, String token, MultipartBody.Part file, ResponseCallback<VoiceToTextModel> callback) {

        aiApi.aiVoiceToText(vadMode, asrMode, langBoost, "Bearer " + token, file).clone().enqueue(new Callback<VoiceToTextModel>() {
            @Override
            public void onResponse(Call<VoiceToTextModel> call, Response<VoiceToTextModel> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    if (response.code() == 401) {
                        callback.onError(String.valueOf(response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<VoiceToTextModel> call, Throwable t) {
                callback.onFailed();
            }
        });
    }

    public void getAiTextToVoice(String text, String token, ResponseCallback<ResponseBody> callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("input_text", text);

        new RetrofitFactory().getAiApi().aiTextToVoice(jsonObject, "Bearer " + token).clone().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    if (response.code() == 401) {
                        callback.onError(String.valueOf(response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailed();
            }
        });
    }
}
