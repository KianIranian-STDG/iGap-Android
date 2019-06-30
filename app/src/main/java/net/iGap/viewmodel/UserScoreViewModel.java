package net.iGap.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.interfaces.OnUserIVandGetScore;
import net.iGap.proto.ProtoUserIVandGetScore;
import net.iGap.request.RequestUserIVandGetScore;

public class UserScoreViewModel extends ViewModel {

    private MutableLiveData<String> inviteFriendsScore = new MutableLiveData<>();
    private MutableLiveData<String> paymentScore = new MutableLiveData<>();
    private MutableLiveData<String> qrCodeScore = new MutableLiveData<>();
    private MutableLiveData<String> botsScore = new MutableLiveData<>();
    private MutableLiveData<String> userScore = new MutableLiveData<>();
    private MutableLiveData<String> userRank = new MutableLiveData<>();
    private MutableLiveData<String> totalRank = new MutableLiveData<>();
    //ui
    public MutableLiveData<Integer> userScorePointer = new MutableLiveData<>();

    public UserScoreViewModel() {
        inviteFriendsScore.setValue("20 points");
        paymentScore.setValue("10 points");
        qrCodeScore.setValue("20 points");
        botsScore.setValue("10 points");
        userRank.setValue("0");
        totalRank.setValue("of 20.000.000");
        //Todo:move to repository
        new RequestUserIVandGetScore().userIVandGetScore(new OnUserIVandGetScore() {
            @Override
            public void getScore(ProtoUserIVandGetScore.UserIVandGetScoreResponse.Builder score) {
                G.handler.post(() -> {
                    userScorePointer.setValue((score.getScore() % 1000) / 360);
                    userScore.setValue(String.valueOf(score.getScore()));
                });
            }

            @Override
            public void onError() {
                userScorePointer.setValue(0);
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

    public LiveData<String> getBotsScore() {
        return botsScore;
    }

    public LiveData<String> getQrCodeScore() {
        return qrCodeScore;
    }

    public LiveData<String> getPaymentScore() {
        return paymentScore;
    }

    public LiveData<String> getInviteFriendsScore() {
        return inviteFriendsScore;
    }

    public void onGiftsButtonClick() {

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
