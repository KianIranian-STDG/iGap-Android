package com.iGap.fragments;


import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperLogout;
import com.iGap.helper.HelperPermision;
import com.iGap.helper.HelperString;
import com.iGap.interfaces.OnGetPermision;
import com.iGap.interfaces.OnSmsReceive;
import com.iGap.interfaces.OnUserDelete;
import com.iGap.interfaces.OnUserGetDeleteToken;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.EditTextAdjustPan;
import com.iGap.module.IncomingSms;
import com.iGap.proto.ProtoUserDelete;
import com.iGap.request.RequestUserDelete;
import com.iGap.request.RequestUserGetDeleteToken;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDeleteAccount extends Fragment {

    private String regex;
    private String smsMessage;
    private IncomingSms smsReceiver;
    private EditTextAdjustPan edtDeleteAccount;
    private RippleView txtSet;
    private CountDownTimer countDownTimer;
    private String phone;
    private ViewGroup ltTime;

    public FragmentDeleteAccount() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {

        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        smsReceiver = new IncomingSms(new OnSmsReceive() {

            @Override
            public void onSmsReceive(final String message) {
                try {
                    if (message != null && !message.isEmpty() && !message.equals("null") && !message.equals("")) {
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getSms(message);
                            }
                        }, 500);

                    }
                } catch (Exception e1) {
                    e1.getStackTrace();
                }
            }
        });

        HelperPermision.getSmsPermision(getActivity(), new OnGetPermision() {
            @Override
            public void Allow() {
                getActivity().registerReceiver(smsReceiver, filter);
            }
        });
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_delete_account, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().getString("PHONE") != null) {

            phone = getArguments().getString("PHONE");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        G.onUserGetDeleteToken = new OnUserGetDeleteToken() {
            @Override
            public void onUserGetDeleteToken(int resendDelay, String tokenRegex, String tokenLength) {
                regex = tokenRegex;
            }
        };

        new RequestUserGetDeleteToken().userGetDeleteToken();

        ViewGroup rootDeleteAccount = (ViewGroup) view.findViewById(R.id.rootDeleteAccount);
        rootDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        RippleView txtBack = (RippleView) view.findViewById(R.id.stda_ripple_back);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentDeleteAccount.this).commit();
            }
        });


        ltTime = (ViewGroup) view.findViewById(R.id.stda_layout_time);

        TextView txtPhoneNumber = (TextView) view.findViewById(R.id.stda_txt_phoneNumber);
        if (phone != null) txtPhoneNumber.setText("" + phone);

        txtSet = (RippleView) view.findViewById(R.id.stda_ripple_set);
        txtSet.setEnabled(false);
        txtSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtDeleteAccount.getText().length() > 0) {

                    new MaterialDialog.Builder(getActivity())
                            .title(getResources().getString(R.string.delete_account))
                            .titleColor(getResources().getColor(android.R.color.black))
                            .content(R.string.sure_delete_account)
                            .positiveText(getResources().getString(R.string.B_ok))
                            .negativeText(getResources().getString(R.string.B_cancel))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

//                                    String verificationCode = HelperString.regexExtractValue(smsMessage, regex);
                                    String verificationCode = edtDeleteAccount.getText().toString();
                                    if (verificationCode != null && !verificationCode.isEmpty()) {

                                        G.onUserDelete = new OnUserDelete() {
                                            @Override
                                            public void onUserDeleteResponse() {
                                                HelperLogout.logout();
                                            }

                                            @Override
                                            public void Error(int majorCode, final int minorCode) {
                                                if (majorCode == 154) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            if (minorCode == 1) {
                                                                final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_154_1), Snackbar.LENGTH_LONG);
                                                                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        snack.dismiss();
                                                                    }
                                                                });
                                                                snack.show();
                                                            } else {
                                                                final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_154_2), Snackbar.LENGTH_LONG);
                                                                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        snack.dismiss();
                                                                    }
                                                                });
                                                                snack.show();
                                                            }


                                                        }
                                                    });
                                                } else if (majorCode == 155) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_155), Snackbar.LENGTH_LONG);

                                                            snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    snack.dismiss();
                                                                }
                                                            });
                                                            snack.show();
                                                        }
                                                    });
                                                } else if (majorCode == 156) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_156), Snackbar.LENGTH_LONG);

                                                            snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    snack.dismiss();
                                                                }
                                                            });
                                                            snack.show();
                                                        }
                                                    });
                                                } else if (majorCode == 157) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_157), Snackbar.LENGTH_LONG);

                                                            snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    snack.dismiss();
                                                                }
                                                            });
                                                            snack.show();
                                                        }
                                                    });
                                                } else if (majorCode == 158) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.E_158), Snackbar.LENGTH_LONG);

                                                            snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    snack.dismiss();
                                                                }
                                                            });
                                                            snack.show();
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void TimeOut() {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.time_out), Snackbar.LENGTH_LONG);

                                                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                snack.dismiss();
                                                            }
                                                        });
                                                        snack.show();
                                                    }
                                                });
                                            }
                                        };

                                        new RequestUserDelete().userDelete(verificationCode, ProtoUserDelete.UserDelete.Reason.OTHER);
                                    }
                                }
                            })
                            .show();
                } else {

                    final Snackbar snack =
                            Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.please_enter_code_for_verify,
                                    Snackbar.LENGTH_LONG);

                    snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();

                }
            }
        });

        edtDeleteAccount = (EditTextAdjustPan) view.findViewById(R.id.stda_edt_dleteAccount);
        edtDeleteAccount.setEnabled(false);


        final View viewLineBottom = view.findViewById(R.id.stda_line_below_editText);
        txtSet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewLineBottom.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    viewLineBottom.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        final TextView txtTimerLand = (TextView) view.findViewById(R.id.stda_txt_time);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                countDownTimer = new CountDownTimer(1000 * 60, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) ((millisUntilFinished) / 1000);
                        int minutes = seconds / 60;
                        seconds = seconds % 60;
                        txtTimerLand.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                    }

                    @Override
                    public void onFinish() {
                        edtDeleteAccount.setEnabled(true);
                        txtSet.setEnabled(true);
                        ltTime.setVisibility(View.GONE);
                    }
                };
                countDownTimer.start();
            }
        });


    }

    private void getSms(String message) {

        countDownTimer.cancel();
        String verificationCode = HelperString.regexExtractValue(message, regex);
        if (verificationCode.length() > 0) {
            edtDeleteAccount.setEnabled(true);
            txtSet.setEnabled(true);
            edtDeleteAccount.setText("" + verificationCode);
        }

//
    }

    @Override
    public void onPause() {
        try {
            getActivity().unregisterReceiver(smsReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }
}
