package net.iGap.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.G;
import net.iGap.interfaces.OnUserIVandGetScore;
import net.iGap.proto.ProtoUserIVandGetScore;
import net.iGap.request.RequestUserIVandGetScore;

import java.util.List;

public class UserScoreViewModel extends ViewModel {

    public static final int REQUEST_CODE_QR_IVAND_CODE = 543;
    public MutableLiveData<List<ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore>> ivandScore = new MutableLiveData<>();
    //ui
    public MutableLiveData<Integer> userScorePointer = new MutableLiveData<>();
    public MutableLiveData<Integer> userRankPointer = new MutableLiveData<>();
    private MutableLiveData<String> userScore = new MutableLiveData<>();
    private MutableLiveData<String> userRank = new MutableLiveData<>();
    private MutableLiveData<String> totalRank = new MutableLiveData<>();
    private String of;

    public UserScoreViewModel() {

        of = "of ";
        if (G.selectedLanguage.equals("fa"))
            of = "از ";

        userRank.setValue("0");
        totalRank.setValue(of + "0");
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
                userScorePointer.postValue((score.getScore() % 1000) / 360);
                userRankPointer.postValue((score.getUserRank() * 360) / score.getTotalRank());
                userScore.postValue(String.valueOf(score.getScore()));
                totalRank.postValue(of + score.getTotalRank());
                userRank.postValue(String.valueOf(score.getUserRank()));
                ivandScore.postValue(score.getScoresList());
            }

            @Override
            public void onError(int major, int minor) {
                userScorePointer.postValue(0);
                userRankPointer.postValue(0);
                userScore.postValue(String.valueOf(-1));
                if (major == 5 && minor == 1) {
                    getUserIVandScore();
                }
            }
        });
    }
}
