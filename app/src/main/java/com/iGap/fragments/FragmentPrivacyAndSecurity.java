package com.iGap.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.R;
import com.iGap.libs.rippleeffect.RippleView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPrivacyAndSecurity extends Fragment {

    private int poRbDialogSelfDestruction = 0;

    public FragmentPrivacyAndSecurity() {
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_privacy_and_security, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RelativeLayout parentPrivacySecurity =
            (RelativeLayout) view.findViewById(R.id.parentPrivacySecurity);

        parentPrivacySecurity.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

            }
        });

        RippleView rippleBack = (RippleView) view.findViewById(R.id.stps_ripple_back);

        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .remove(FragmentPrivacyAndSecurity.this)
                    .commit();
            }
        });

        ViewGroup ltSelfDestruction =
            (ViewGroup) view.findViewById(R.id.stps_layout_Self_destruction);
        ltSelfDestruction.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                new MaterialDialog.Builder(getActivity()).title("Language")
                    .titleGravity(GravityEnum.START)
                    .titleColor(getResources().getColor(android.R.color.black))
                    .items(R.array.account_self_destruct)
                    .itemsCallbackSingleChoice(poRbDialogSelfDestruction,
                        new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which,
                                CharSequence text) {

                                switch (which) {
                                    case 0:

                                        break;
                                    case 1:

                                        break;
                                    case 2:

                                        break;
                                    case 3:

                                        break;
                                }

                                return false;
                            }
                        })
                    .positiveText("OK")
                    .negativeText("CANCEL")
                    .show();
            }
        });
    }
}
