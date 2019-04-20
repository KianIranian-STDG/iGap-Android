package net.iGap.viewmodel;

import android.app.Activity;
import android.databinding.ObservableField;

import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentIVandActivities;
import net.iGap.fragments.FragmentIVandProfile;
import net.iGap.helper.HelperFragment;
import net.iGap.interfaces.OnUserIVandGetScore;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserIVandGetScore;

import io.realm.Realm;

public class FragmentIVandProfileViewModel implements OnUserIVandGetScore {
    public static final int REQUEST_CODE_QR_IVAND_CODE = 543;
    public ObservableField<String> profileNameTv = new ObservableField<>("");
    public ObservableField<String> referralTv = new ObservableField<>("0");
    public ObservableField<String> pointsTv = new ObservableField<>("300");
    private RealmUserInfo realmUserInfo;
    private Realm mRealm;
    private FragmentIVandProfile fragmentIVandProfile;


    public FragmentIVandProfileViewModel(FragmentIVandProfile fragmentIVandProfile) {
        this.fragmentIVandProfile = fragmentIVandProfile;
        G.onUserIVandGetScore = this;
        initData();
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

    public void onOrderHistoryClick() {
        new HelperFragment(FragmentIVandActivities.newInstance()).setReplace(false).load();
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

    @Override
    public void getScore(int score) {
        pointsTv.set(String.valueOf(score));
    }

    public void onStart() {
        new RequestUserIVandGetScore().userIVandGetScore();
    }
}
