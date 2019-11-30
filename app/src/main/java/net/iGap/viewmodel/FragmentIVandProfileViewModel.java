package net.iGap.viewmodel;

import android.app.Activity;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.OnUserIVandGetScore;
import net.iGap.proto.ProtoUserIVandGetScore;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserIVandGetScore;

public class FragmentIVandProfileViewModel {
    public static final int REQUEST_CODE_QR_IVAND_CODE = 543;
    public MutableLiveData<Boolean> goToIVandPage = new MutableLiveData<>();
    public ObservableField<String> profileNameTv = new ObservableField<>("");
    public ObservableField<String> referralTv = new ObservableField<>("0");
    public ObservableField<String> pointsTv = new ObservableField<>("-");
    private RealmUserInfo realmUserInfo;


    public FragmentIVandProfileViewModel() {

    }

    public static void scanBarCode(Activity activity) {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setRequestCode(REQUEST_CODE_QR_IVAND_CODE);
        integrator.setBeepEnabled(false);
        integrator.setPrompt("");
        integrator.initiateScan();
    }

    private void initData() {
        realmUserInfo = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmUserInfo.class).findFirst();
        });
        if (realmUserInfo != null) {
            profileNameTv.set(realmUserInfo.getUserInfo().getDisplayName());
            referralTv.set(G.context.getString(R.string.ra_title) + " " + realmUserInfo.getRepresentPhoneNumber());
        }
    }

    public int saleVisibility() {
        realmUserInfo = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmUserInfo.class).findFirst();
        });
        if (realmUserInfo.getRepresentPhoneNumber() == null || realmUserInfo.getRepresentPhoneNumber().equals("")) {
            return View.GONE;
        }
        return View.VISIBLE;
    }

    public void onOrderHistoryClick() {
        goToIVandPage.setValue(true);
    }

    public void onResume() {
        initData();
        getIVandScore();
    }

    private void getIVandScore() {
        new RequestUserIVandGetScore().userIVandGetScore(new OnUserIVandGetScore() {
            @Override
            public void getScore(ProtoUserIVandGetScore.UserIVandGetScoreResponse.Builder score) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pointsTv.set(String.valueOf(score.getScore()));
                    }
                });
            }

            @Override
            public void onError(int major, int minor) {
                if (major == 5 && minor == 1) {
                    getIVandScore();
                }
            }
        });
    }
}
