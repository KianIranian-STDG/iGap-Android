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

    private MutableLiveData<String> userScore = new MutableLiveData<>();
    private MutableLiveData<String> userRank = new MutableLiveData<>();
    private MutableLiveData<String> totalRank = new MutableLiveData<>();
    public MutableLiveData<List<ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore>> ivandScore = new MutableLiveData<>();
    //ui
    public MutableLiveData<Integer> userScorePointer = new MutableLiveData<>();
    public MutableLiveData<Integer> userRankPointer = new MutableLiveData<>();
    private String of ;

    public UserScoreViewModel() {

        of = "of ";
        if (G.selectedLanguage.equals("fa"))
            of ="از " ;

        userRank.setValue("0");
        totalRank.setValue(of + "0");
        //Todo:move to repository
        new RequestUserIVandGetScore().userIVandGetScore(new OnUserIVandGetScore() {
            @Override
            public void getScore(ProtoUserIVandGetScore.UserIVandGetScoreResponse.Builder score) {
                G.handler.post(() -> {
                    userScorePointer.setValue((score.getScore() % 1000) / 360);
                    userRankPointer.setValue((score.getUserRank() * 360) / score.getTotalRank());
                    userScore.setValue(String.valueOf(score.getScore()));
                    totalRank.setValue(of + score.getTotalRank());
                    userRank.setValue(String.valueOf(score.getUserRank()));
                    ivandScore.setValue(score.getScoresList());
                });
            }

            @Override
            public void onError() {
                userScorePointer.setValue(0);
                userRankPointer.setValue(0);
                userScore.setValue(String.valueOf(-1));
            }
        });
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
}
