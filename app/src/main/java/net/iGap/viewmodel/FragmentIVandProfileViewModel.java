package net.iGap.viewmodel;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.OnUserIVandGetScore;
import net.iGap.proto.ProtoUserIVandGetScore;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserIVandGetScore;

import io.realm.Realm;

public class FragmentIVandProfileViewModel {
    public static final int REQUEST_CODE_QR_IVAND_CODE = 543;
    public MutableLiveData<Boolean> goToIVandPage = new MutableLiveData<>();
    public ObservableField<String> profileNameTv = new ObservableField<>("");
    public ObservableField<String> referralTv = new ObservableField<>("0");
    public ObservableField<String> pointsTv = new ObservableField<>("-");
    private RealmUserInfo realmUserInfo;
    private Realm mRealm;


    public FragmentIVandProfileViewModel() {
    }

    private void initData() {
        realmUserInfo = getRealm().where(RealmUserInfo.class).findFirst();
        if (getRealm() != null) {
            profileNameTv.set(realmUserInfo.getUserInfo().getDisplayName());
            referralTv.set(G.context.getString(R.string.ra_title) + " " + realmUserInfo.getRepresentPhoneNumber());
        }
    }

    private Realm getRealm() {
        if (mRealm == null || mRealm.isClosed()) {
            mRealm = Realm.getDefaultInstance();
        }
        return mRealm;
    }

    public int saleVisibility() {
        realmUserInfo = getRealm().where(RealmUserInfo.class).findFirst();
        if (realmUserInfo.getRepresentPhoneNumber() == null || realmUserInfo.getRepresentPhoneNumber().equals("")) {
            return View.GONE;
        }
        return View.VISIBLE;
    }

    public void onOrderHistoryClick() {
        goToIVandPage.setValue(true);
    }

    public static void scanBarCode(Activity activity) {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setRequestCode(REQUEST_CODE_QR_IVAND_CODE);
        integrator.setBeepEnabled(false);
        integrator.setPrompt("");
        integrator.initiateScan();
    }

    public void onDestroy() {
        mRealm.close();
    }

    public void onResume() {
        initData();
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
            public void onError() {

            }
        });
    }
}
