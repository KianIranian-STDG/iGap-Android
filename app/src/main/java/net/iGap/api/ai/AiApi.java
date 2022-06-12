package net.iGap.api.ai;

import com.google.gson.JsonObject;

import net.iGap.model.ai.AiTokenModel;
import net.iGap.model.ai.VoiceToTextModel;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface AiApi {


    @POST("auth/token/")
    @FormUrlEncoded
    Call<AiTokenModel> getAiToken(@FieldMap Map<String,String> params);

    @Multipart
    @POST("api/v1/voice/asr/")
    Call<VoiceToTextModel> aiVoiceToText(@Query("vad_mode") String vadMode,
                                         @Query("asr_mode") String asrMode,
                                         @Query("lang_boost") String langBoost,
                                         @Header("authorization") String token,
                                         @Part MultipartBody.Part file);

    @POST("api/v1/voice/tts/")
    Call<ResponseBody> aiTextToVoice(@Body JsonObject jsonObject,
                                     @Header("authorization") String token);


}
