package net.iGap.viewmodel;

import android.databinding.ObservableField;

import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentCustomerClubProfile;
import net.iGap.realm.RealmUserInfo;

import io.realm.Realm;

public class CustomerClubViewModel {
    public static final int REQUEST_CODE_QR_CODE = 200;
    public ObservableField<String> profileNameTv = new ObservableField<>("");
    public ObservableField<String> referralTv = new ObservableField<>("0");
    public ObservableField<String> pointsTv = new ObservableField<>("300");
    private RealmUserInfo realmUserInfo;
    private Realm mRealm;
    private FragmentCustomerClubProfile fragmentCustomerClubProfile;


    public CustomerClubViewModel(FragmentCustomerClubProfile fragmentCustomerClubProfile) {
        this.fragmentCustomerClubProfile = fragmentCustomerClubProfile;
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
        IntentIntegrator integrator = new IntentIntegrator(fragmentCustomerClubProfile.getActivity());
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setRequestCode(REQUEST_CODE_QR_CODE);
        integrator.setBeepEnabled(false);
        integrator.setPrompt("");
        integrator.initiateScan();
    }

    public void onDestroy() {
        mRealm.close();
    }
}
