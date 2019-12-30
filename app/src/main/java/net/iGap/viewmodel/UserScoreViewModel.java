package net.iGap.viewmodel;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.DbManager;
import net.iGap.helper.HelperCalander;
import net.iGap.interfaces.OnUserIVandGetScore;
import net.iGap.proto.ProtoUserIVandGetScore;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserIVandGetScore;

import java.util.List;

import io.realm.Realm;

public class UserScoreViewModel extends ViewModel {

    public static final int REQUEST_CODE_QR_IVAND_CODE = 543;
    private MutableLiveData<List<ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore>> ivandScore = new MutableLiveData<>();
    private MutableLiveData<Integer> userRankPointer = new MutableLiveData<>();
    private MutableLiveData<Boolean> goToScannerPage = new MutableLiveData<>();
    private MutableLiveData<String> userScore = new MutableLiveData<>();
    private MutableLiveData<String> userRank = new MutableLiveData<>();
    private MutableLiveData<String> totalRank = new MutableLiveData<>();

    private RealmUserInfo userInfo;

    public UserScoreViewModel() {
        //Todo:move to repository
        userInfo = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmUserInfo.class).findFirst();
        });

        if (userInfo != null){
            userInfo.addChangeListener(realmModel -> {
                userInfo = (RealmUserInfo) realmModel;

            });
        }
        getUserIVandScore();
    }


    public static void scanBarCode(Activity activity) {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setRequestCode(REQUEST_CODE_QR_IVAND_CODE);
        integrator.setBeepEnabled(false);
        integrator.setPrompt("");
        integrator.initiateScan();
    }

    public MutableLiveData<String> getTotalRank() {
        if (totalRank.getValue() == null) {
            totalRank.setValue(checkPersianNumber("0"));
        }
        return totalRank;
    }

    public MutableLiveData<String> getUserRank() {
        if (userRank.getValue() == null) {
            userRank.setValue(checkPersianNumber("0"));
        }
        return userRank;
    }

    public MutableLiveData<String> getUserScore() {
        if (userScore.getValue() == null) {
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

    private String checkPersianNumber(String text) {
        if (HelperCalander.isPersianUnicode) {
            return HelperCalander.convertToUnicodeFarsiNumber(text);
        } else {
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

        userRankPointer.setValue(0);

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
