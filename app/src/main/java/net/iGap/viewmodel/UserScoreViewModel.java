package net.iGap.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.G;
import net.iGap.helper.HelperCalander;
import net.iGap.interfaces.OnUserIVandGetScore;
import net.iGap.proto.ProtoUserIVandGetScore;
import net.iGap.request.RequestUserIVandGetScore;

import java.util.List;

public class UserScoreViewModel extends ViewModel {

    public static final int REQUEST_CODE_QR_IVAND_CODE = 543;
    public MutableLiveData<List<ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore>> ivandScore = new MutableLiveData<>();
    //ui
    public MutableLiveData<Integer> userRankPointer = new MutableLiveData<>();
    private MutableLiveData<String> userScore = new MutableLiveData<>();
    private MutableLiveData<String> userRank = new MutableLiveData<>();
    private MutableLiveData<String> totalRank = new MutableLiveData<>();
    private String of;

    public UserScoreViewModel() {

        of = "of ";
        if (G.selectedLanguage.equals("fa"))
            of = "از ";

        userRank.setValue(checkPersianNumber("0"));
        totalRank.setValue(of + checkPersianNumber("0"));
        //Todo:move to repository
        getUserIVandScore();
    }

    public LiveData<String> getTotalRank() {
        return totalRank;
    }

    public LiveData<String> getUserRank() {
        return userRank;
    }

    public LiveData<String> getUserScore() {
        return userScore;
    }

    private String checkPersianNumber(String text){
        if (HelperCalander.isPersianUnicode){
            return HelperCalander.convertToUnicodeFarsiNumber(text);
        }else {
            return text;
        }
    }

    public void onScanBarcodeButtonClick() {
        IntentIntegrator integrator = new IntentIntegrator(G.fragmentActivity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setRequestCode(REQUEST_CODE_QR_IVAND_CODE);
        integrator.setBeepEnabled(false);
        integrator.setPrompt("");
        integrator.initiateScan();
    }

    public void onBotsAndChannelClick() {

    }

    public void onQrCodeClick() {

    }

    public void onPaymentClick() {

    }

    public void onInviteClick() {

    }

    private void getUserIVandScore() {
        new RequestUserIVandGetScore().userIVandGetScore(new OnUserIVandGetScore() {
            @Override
            public void getScore(ProtoUserIVandGetScore.UserIVandGetScoreResponse.Builder score) {
                userRankPointer.postValue((score.getUserRank() * 360) / score.getTotalRank());
                userScore.postValue(checkPersianNumber(String.valueOf(score.getScore())));
                totalRank.postValue(of + checkPersianNumber(String.valueOf(score.getTotalRank())));
                userRank.postValue(checkPersianNumber(String.valueOf(score.getUserRank())));
                ivandScore.postValue(score.getScoresList());
            }

            @Override
            public void onError(int major, int minor) {
                userRankPointer.postValue(0);
                userScore.postValue(checkPersianNumber("-1"));
                if (major == 5 && minor == 1) {
                    getUserIVandScore();
                }
            }
        });
    }
}
