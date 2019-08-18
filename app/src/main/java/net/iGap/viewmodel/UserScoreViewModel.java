package net.iGap.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;

import net.iGap.helper.HelperCalander;
import net.iGap.interfaces.OnUserIVandGetScore;
import net.iGap.proto.ProtoUserIVandGetScore;
import net.iGap.request.RequestUserIVandGetScore;

import java.util.List;

public class UserScoreViewModel extends ViewModel {

    private MutableLiveData<List<ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore>> ivandScore = new MutableLiveData<>();
    private MutableLiveData<Integer> userRankPointer = new MutableLiveData<>();
    private MutableLiveData<Boolean> goToScannerPage = new MutableLiveData<>();
    private MutableLiveData<String> userScore = new MutableLiveData<>();
    private MutableLiveData<String> userRank = new MutableLiveData<>();
    private MutableLiveData<String> totalRank = new MutableLiveData<>();

    public UserScoreViewModel() {
        //Todo:move to repository
        getUserIVandScore();
    }

    public MutableLiveData<String> getTotalRank() {
        if (totalRank.getValue() == null) {
            totalRank.setValue(checkPersianNumber("0"));
        }
        return totalRank;
    }

    public MutableLiveData<String> getUserRank() {
        if (userRank.getValue() == null){
            userRank.setValue(checkPersianNumber("0"));
        }
        return userRank;
    }

    public MutableLiveData<String> getUserScore() {
        if (userScore.getValue() == null){
            userScore.setValue(checkPersianNumber("0"));
        }
        return userScore;
    }

    public MutableLiveData<Boolean> getGoToScannerPage() {
        return goToScannerPage;
    }

    public MutableLiveData<List<ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore>> getIvandScore() {
        return ivandScore;
    }

    public MutableLiveData<Integer> getUserRankPointer() {
        return userRankPointer;
    }

    private String checkPersianNumber(String text){
        if (HelperCalander.isPersianUnicode){
            return HelperCalander.convertToUnicodeFarsiNumber(text);
        }else {
            return text;
        }
    }

    public void onScanBarcodeButtonClick() {
        goToScannerPage.setValue(true);
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
                totalRank.postValue(checkPersianNumber(String.valueOf(score.getTotalRank())));
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
