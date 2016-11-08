package com.iGap.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.interfaces.OnUserProfileSetSelfRemove;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.SHP_SETTING;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestUserProfileSetSelfRemove;

import io.realm.Realm;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPrivacyAndSecurity extends Fragment {

    int poSelfRemove;
    private SharedPreferences sharedPreferences;
    private int poRbDialogSelfDestruction = 0;
    private int selfRemove;

    public FragmentPrivacyAndSecurity() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_privacy_and_security, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Realm realm = Realm.getDefaultInstance();
        selfRemove = realm.where(RealmUserInfo.class).findFirst().getSelfRemove();

        RelativeLayout parentPrivacySecurity = (RelativeLayout) view.findViewById(R.id.parentPrivacySecurity);

        parentPrivacySecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        RippleView rippleBack = (RippleView) view.findViewById(R.id.stps_ripple_back);

        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentPrivacyAndSecurity.this).commit();
            }
        });

        final TextView txtDestruction = (TextView) view.findViewById(R.id.stps_txt_Self_destruction);
        if (selfRemove != 0) {
            switch (selfRemove) {
                case 1:
                    txtDestruction.setText(getResources().getString(R.string.month_1));
                    break;
                case 3:
                    txtDestruction.setText(getResources().getString(R.string.month_3));
                    break;
                case 6:
                    txtDestruction.setText(getResources().getString(R.string.month_6));
                    break;
                case 12:
                    txtDestruction.setText(getResources().getString(R.string.year_1));
                    break;
            }
        } else {
            txtDestruction.setText(getResources().getString(R.string.month_6));
        }

        sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        poSelfRemove = sharedPreferences.getInt(SHP_SETTING.KEY_POSITION_SELF_REMOVE, 2);
        ViewGroup ltSelfDestruction = (ViewGroup) view.findViewById(R.id.stps_layout_Self_destruction);
        ltSelfDestruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getActivity()).title("Language")
                        .titleGravity(GravityEnum.START)
                        .titleColor(getResources().getColor(android.R.color.black))
                        .items(R.array.account_self_destruct).itemsCallbackSingleChoice(poSelfRemove, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        switch (which) {
                            case 0: {
                                txtDestruction.setText(getResources().getString(R.string.month_1));
                                new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(1);
                                sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt(SHP_SETTING.KEY_POSITION_SELF_REMOVE, 0);
                                editor.apply();
                                poSelfRemove = 0;
                                break;
                            }
                            case 1: {
                                txtDestruction.setText(getResources().getString(R.string.month_3));
                                new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(3);
                                sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt(SHP_SETTING.KEY_POSITION_SELF_REMOVE, 1);
                                editor.apply();
                                poSelfRemove = 1;
                                break;
                            }
                            case 2: {

                                txtDestruction.setText(getResources().getString(R.string.month_6));
                                new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(6);
                                sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt(SHP_SETTING.KEY_POSITION_SELF_REMOVE, 2);
                                editor.apply();
                                poSelfRemove = 2;
                                break;
                            }
                            case 3: {

                                txtDestruction.setText(getResources().getString(R.string.year_1));
                                new RequestUserProfileSetSelfRemove().userProfileSetSelfRemove(12);
                                sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt(SHP_SETTING.KEY_POSITION_SELF_REMOVE, 3);
                                editor.apply();
                                poSelfRemove = 3;
                                break;
                            }
                        }
                        return false;
                    }
                })
                        .positiveText("OK")
                        .negativeText("CANCEL")
                        .show();

                G.onUserProfileSetSelfRemove = new OnUserProfileSetSelfRemove() {
                    @Override
                    public void onUserSetSelfRemove(final int numberOfMonth) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Realm realm1 = Realm.getDefaultInstance();
                                realm1.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.where(RealmUserInfo.class).findFirst().setSelfRemove(numberOfMonth);
                                    }
                                });

                                realm1.close();
                            }
                        });
                    }

                    @Override
                    public void Error(int majorCode, int minorCode) {

                    }
                };
            }
        });

        realm.close();
    }
}
