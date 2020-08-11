/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;


import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperError;
import net.iGap.observers.interfaces.OnUserDelete;
import net.iGap.observers.interfaces.OnUserGetDeleteToken;
import net.iGap.proto.ProtoUserDelete;
import net.iGap.request.RequestUserDelete;
import net.iGap.request.RequestUserGetDeleteToken;

public class FragmentDeleteAccount extends FragmentOtpAuthentication {

    public static FragmentDeleteAccount getInstance(String phone) {
        FragmentDeleteAccount frg = new FragmentDeleteAccount();
        Bundle bundle = new Bundle();
        bundle.putString("phone", phone);
        bundle.putString("title", G.context.getString(R.string.Destruction_Code));
        bundle.putString("editTextHint", G.context.getString(R.string.destroy_enter_code));
        bundle.putString("description", G.context.getString(R.string.destroy_description));
        frg.setArguments(bundle);
        return frg;
    }

    @Override
    public void onRightIconClickListener(View view) {
        if (getContext() == null)
            return;

        if (smsCodeEditText.getText() == null || smsCodeEditText.getText().length() == 0) {
            HelperError.showSnackMessage(G.context.getString(R.string.please_enter_code_for_verify), false);
            return;
        }

        new MaterialDialog.Builder(getContext())
                .title(R.string.delete_account)
                .titleColorRes(android.R.color.black)
                .content(R.string.sure_delete_account)
                .positiveText(R.string.B_ok)
                .negativeText(R.string.B_cancel)
                .onPositive((dialog, which) -> {
                    dialog.getActionButton(which).setEnabled(false);
                    showProgressBar();
                    new RequestUserDelete().userDelete(smsCodeEditText.getText().toString(), ProtoUserDelete.UserDelete.Reason.OTHER, new OnUserDelete() {
                        @Override
                        public void onUserDeleteResponse() {
                            hideProgressBar();
                        }

                        @Override
                        public void Error(int majorCode, int minorCode, int time) {
                            dialog.getActionButton(which).setEnabled(true);
                            G.handler.post(() -> {
                                hideProgressBar();
                                if (dialog.isShowing()) dialog.dismiss();
                                if (majorCode == 158) {
                                    dialogWaitTime(R.string.USER_DELETE_MAX_TRY_LOCK, time, majorCode);
                                } else if (majorCode == 5 && minorCode == 1) {
                                    HelperError.showSnackMessage(G.context.getString(R.string.time_out), false);
                                }
                            });
                        }

                    });
                })
                .show();
    }

    @Override
    protected void getAuthenticationToken() {

        new RequestUserGetDeleteToken().userGetDeleteToken(new OnUserGetDeleteToken() {
            @Override
            public void onUserGetDeleteToken(int resendDelay, String tokenRegex, String tokenLength) {
                regex = tokenRegex;
                trySetCode();
            }

            @Override
            public void onUserGetDeleteError(int majorCode, int minorCode, int time) {
                G.handler.post(() -> {
                    switch (majorCode) {
                        case 152:
                            dialogWaitTime(R.string.USER_GET_DELETE_TOKEN_MAX_TRY_LOCK, time, majorCode);
                            break;
                        case 153:
                            dialogWaitTime(R.string.USER_GET_DELETE_TOKEN_MAX_SEND, time, majorCode);
                            break;
                    }
                });
            }
        });

    }
}
