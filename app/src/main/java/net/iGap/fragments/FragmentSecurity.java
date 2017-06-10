package net.iGap.fragments;



import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import java.util.regex.Pattern;
import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.OnTwoStepPassword;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.enums.Security;
import net.iGap.request.RequestUserTwoStepVerificationChangeHint;
import net.iGap.request.RequestUserTwoStepVerificationChangeRecoveryEmail;
import net.iGap.request.RequestUserTwoStepVerificationChangeRecoveryQuestion;
import net.iGap.request.RequestUserTwoStepVerificationCheckPassword;
import net.iGap.request.RequestUserTwoStepVerificationGetPasswordDetail;
import net.iGap.request.RequestUserTwoStepVerificationUnsetPassword;
import net.iGap.request.RequestUserTwoStepVerificationVerifyRecoveryEmail;

import static net.iGap.R.id.tsv_setConfirmedEmail;
import static net.iGap.R.id.tsv_setRecoveryEmail;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSecurity extends Fragment {

    private static String password;
    private boolean isChabgePassword = false;
    private String txtQuestionOne = "";
    private String txtQuestionTwo = "";
    private boolean isRecoveryByEmail = false;
    private ViewGroup rootSetPassword;
    private ViewGroup rootCheckPassword;
    private ViewGroup rootSetAdditionPassword;
    private ViewGroup root_ChangePassword;
    private ViewGroup rootQuestionPassword;
    private ViewGroup rootChangeEmail;
    private ViewGroup rootChangeHint;
    private ViewGroup rootConfirmedEmail;
    private RippleView rippleOk;
    private int page;
    private static final int CHANGE_HINT = 1;
    private static final int CHANGE_EMAIL = 2;
    private static final int CONFIRM_EMAIL = 3;
    private static final int CHANGE_QUESTION = 4;
    private FragmentActivity mActivity;
    public FragmentSecurity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_security, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new RequestUserTwoStepVerificationGetPasswordDetail().getPasswordDetail();

        view.findViewById(R.id.stps_backgroundToolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        rootSetPassword = (ViewGroup) view.findViewById(R.id.rootSetPassword);
        rootCheckPassword = (ViewGroup) view.findViewById(R.id.rootCheckPassword);
        rootSetAdditionPassword = (ViewGroup) view.findViewById(R.id.rootSetAdditionPassword);
        root_ChangePassword = (ViewGroup) view.findViewById(R.id.root_ChangePassword);
        rootQuestionPassword = (ViewGroup) view.findViewById(R.id.tsv_rootQuestionPassword);
        rootChangeEmail = (ViewGroup) view.findViewById(R.id.tsv_rootChangeEmail);
        rootChangeHint = (ViewGroup) view.findViewById(R.id.tsv_rootChangeHint);
        rootConfirmedEmail = (ViewGroup) view.findViewById(R.id.tsv_rootConfirmedEmail);

        rippleOk = (RippleView) view.findViewById(R.id.verifyPassword_rippleOk);
        final EditText edtCheckPassword = (EditText) view.findViewById(R.id.setPassword_edtCheckPassword);
        final EditText edtSetQuestionPassOne = (EditText) view.findViewById(R.id.tsv_edtSetQuestionPassOne);
        final EditText edtSetAnswerPassOne = (EditText) view.findViewById(R.id.tsv_edtSetAnswerPassOne);
        final EditText edtSetQuestionPassTwo = (EditText) view.findViewById(R.id.tsv_edtSetQuestionPassTwo);
        final EditText edtSetAnswerPassTwo = (EditText) view.findViewById(R.id.tsv_edtSetAnswerPassTwo);
        final EditText edtSetEmail = (EditText) view.findViewById(R.id.tsv_edtSetEmail);
        final EditText edtChangeHint = (EditText) view.findViewById(R.id.tsv_edtChangeHint);
        final EditText edtConfirmedEmail = (EditText) view.findViewById(R.id.tsv_edtConfirmedEmail);

        TextView txtChangePassword = (TextView) view.findViewById(R.id.tsv_changePassword);
        TextView txtTurnPasswordOff = (TextView) view.findViewById(R.id.tsv_turnPasswordOff);
        final TextView txtSetRecoveryEmail = (TextView) view.findViewById(tsv_setRecoveryEmail);
        final TextView txtSetConfirmedEmail = (TextView) view.findViewById(tsv_setConfirmedEmail);
        TextView txtSetRecoveryQuestion = (TextView) view.findViewById(R.id.tsv_setRecoveryQuestion);
        TextView txtChangeHint = (TextView) view.findViewById(R.id.tsv_changeHint);
        TextView txtForgotPassword = (TextView) view.findViewById(R.id.txtForgotPassword);

        view.findViewById(R.id.rootFragmentSecurity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        RippleView btnBack = (RippleView) view.findViewById(R.id.tsv_ripple_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (page) {
                    case CHANGE_HINT:
                        viewChangeHint();
                        break;
                    case CHANGE_EMAIL:
                        viewChangeEmail();
                        break;
                    case CONFIRM_EMAIL:
                        viewConfirmEmail();
                        break;
                    case CHANGE_QUESTION:
                        viewChangeRecoveryQuestion();
                        break;
                    default:
                        mActivity.getSupportFragmentManager().popBackStack();

                }
                closeKeyboard(v);
            }
        });

        TextView txtSetAdditionPassword = (TextView) view.findViewById(R.id.tsv_txtSetAdditionPassword);
        txtSetAdditionPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentSetSecurityPassword fragmentSetSecurityPassword = new FragmentSetSecurityPassword();
                mActivity.getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.st_layoutParent, fragmentSetSecurityPassword).commit();
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(mActivity).title(R.string.set_recovery_question).items(R.array.securityRecoveryPassword).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                isRecoveryByEmail = true;
                                break;
                            case 1:
                                isRecoveryByEmail = false;
                                break;
                        }

                        FragmentSecurityRecovery fragmentSecurityRecovery = new FragmentSecurityRecovery();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("PAGE", Security.SETTING);
                        bundle.putString("QUESTION_ONE", txtQuestionOne);
                        bundle.putString("QUESTION_TWO", txtQuestionOne);
                        bundle.putString("QUESTION_TWO", txtQuestionOne);
                        bundle.putString("QUESTION_TWO", txtQuestionOne);
                        bundle.putBoolean("IS_EMAIL", isRecoveryByEmail);

                        fragmentSecurityRecovery.setArguments(bundle);
                        mActivity.getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.st_layoutParent, fragmentSecurityRecovery).commit();

                    }
                }).show();
            }
        });

        //page 1
        txtChangeHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                page = CHANGE_HINT;
                rootSetPassword.setVisibility(View.GONE);
                rootSetAdditionPassword.setVisibility(View.GONE);
                rootChangeHint.setVisibility(View.VISIBLE);
                rippleOk.setVisibility(View.VISIBLE);

            }
        });

        txtSetConfirmedEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                page = CONFIRM_EMAIL;
                rootSetPassword.setVisibility(View.GONE);
                rootSetAdditionPassword.setVisibility(View.GONE);
                rootConfirmedEmail.setVisibility(View.VISIBLE);
                rippleOk.setVisibility(View.VISIBLE);
            }
        });

        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentSetSecurityPassword fragmentSetSecurityPassword = new FragmentSetSecurityPassword();
                Bundle bundle = new Bundle();
                bundle.putString("OLD_PASSWORD", password);
                fragmentSetSecurityPassword.setArguments(bundle);
                mActivity.getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.st_layoutParent, fragmentSetSecurityPassword).commit();
            }
        });

        txtSetRecoveryEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                page = CHANGE_EMAIL;
                rootSetPassword.setVisibility(View.GONE);
                rootSetAdditionPassword.setVisibility(View.GONE);
                rootChangeEmail.setVisibility(View.VISIBLE);
                rippleOk.setVisibility(View.VISIBLE);

            }
        });

        txtTurnPasswordOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(mActivity).title(R.string.turn_Password_off).content(R.string.turn_Password_off_desc).positiveText(getResources().getString(R.string.B_ok)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        new RequestUserTwoStepVerificationUnsetPassword().unsetPassword(password);
                    }
                }).negativeText(getResources().getString(R.string.B_cancel)).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        txtSetRecoveryQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = CHANGE_QUESTION;
                rootSetPassword.setVisibility(View.GONE);
                rootSetAdditionPassword.setVisibility(View.GONE);
                rootQuestionPassword.setVisibility(View.VISIBLE);
                rippleOk.setVisibility(View.VISIBLE);
            }
        });

        G.onTwoStepPassword = new OnTwoStepPassword() {
            @Override
            public void getPasswordDetail(final String questionOne, final String questionTwo, final String hint, final boolean hasConfirmedRecoveryEmail, final String unconfirmedEmailPattern) {

                txtQuestionOne = questionOne;
                txtQuestionTwo = questionTwo;
                //this.hasConfirmedRecoveryEmail = hasConfirmedRecoveryEmail;
                //this.unconfirmedEmailPattern = unconfirmedEmailPattern;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (questionOne.length() > 0 && questionTwo.length() > 0) {

                            rootSetPassword.setVisibility(View.GONE);
                            rootSetAdditionPassword.setVisibility(View.GONE);
                            root_ChangePassword.setVisibility(View.VISIBLE);
                            rootCheckPassword.setVisibility(View.VISIBLE);
                            rippleOk.setVisibility(View.VISIBLE);
                            edtCheckPassword.setHint(hint);

                            if (hasConfirmedRecoveryEmail || unconfirmedEmailPattern.length() == 0) {
                                txtSetRecoveryEmail.setVisibility(View.VISIBLE);
                                txtSetConfirmedEmail.setVisibility(View.GONE);
                                view.findViewById(R.id.tsv_viewConfirmedEmail).setVisibility(View.GONE);
                            } else {
                                txtSetRecoveryEmail.setVisibility(View.GONE);
                                view.findViewById(R.id.tsv_viewRecoveryEmail).setVisibility(View.GONE);
                                txtSetConfirmedEmail.setVisibility(View.VISIBLE);

                            }

                        } else {//دوبار اجرا شده
                            rootSetPassword.setVisibility(View.VISIBLE);
                            rootSetAdditionPassword.setVisibility(View.VISIBLE);
                            root_ChangePassword.setVisibility(View.GONE);
                            rootCheckPassword.setVisibility(View.GONE);
                            rippleOk.setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void checkPassword() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rootSetPassword.setVisibility(View.VISIBLE);
                        rootCheckPassword.setVisibility(View.GONE);
                        rippleOk.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void errorCheckPassword(final int getWait) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogWaitTime(getWait);
                    }
                });
            }

            @Override
            public void unSetPassword() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        rootSetPassword.setVisibility(View.VISIBLE);
                        rootSetAdditionPassword.setVisibility(View.VISIBLE);
                        root_ChangePassword.setVisibility(View.GONE);
                        rootCheckPassword.setVisibility(View.GONE);
                        rippleOk.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void changeRecoveryQuestion() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewChangeRecoveryQuestion();
                    }
                });
            }

            @Override
            public void changeHint() {

                viewChangeHint();
            }

            @Override
            public void changeEmail() {

                viewChangeEmail();
            }

            @Override
            public void confirmEmail() {
                viewConfirmEmail();
            }

            @Override
            public void errorInvalidPassword() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        error(getString(R.string.invalid_password));
                    }
                });
            }
        };



        // check password for enter on page
        rippleOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rootCheckPassword.getVisibility() == View.VISIBLE) {
                    if (edtCheckPassword.length() > 1) {
                        password = edtCheckPassword.getText().toString();
                        new RequestUserTwoStepVerificationCheckPassword().checkPassword(password);
                        closeKeyboard(v);
                        edtCheckPassword.setText("");
                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.please_set_password));
                    }
                }

                //change Question

                if (rootQuestionPassword.getVisibility() == View.VISIBLE) {
                    if (edtSetQuestionPassOne.length() > 0 && edtSetAnswerPassOne.length() > 0 && edtSetQuestionPassTwo.length() > 0 && edtSetAnswerPassTwo.length() > 0) {
                        new RequestUserTwoStepVerificationChangeRecoveryQuestion().changeRecoveryQuestion(password, edtSetQuestionPassOne.getText().toString(), edtSetAnswerPassOne.getText().toString(), edtSetQuestionPassTwo.getText().toString(), edtSetAnswerPassTwo.getText().toString());
                        closeKeyboard(v);
                        edtSetQuestionPassOne.setText("");
                        edtSetAnswerPassOne.setText("");
                        edtSetQuestionPassTwo.setText("");
                        edtSetAnswerPassTwo.setText("");
                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.Please_complete_all_Item));
                    }
                }

                //change email
                if (rootChangeEmail.getVisibility() == View.VISIBLE) {
                    if (edtSetEmail.length() > 0) {

                        Pattern EMAIL_ADDRESS = patternEmail();
                        if (EMAIL_ADDRESS.matcher(edtSetEmail.getText().toString()).matches()) {
                            new RequestUserTwoStepVerificationChangeRecoveryEmail().changeRecoveryEmail(password, edtSetEmail.getText().toString());
                            closeKeyboard(v);
                            edtSetEmail.setText("");
                        } else {
                            closeKeyboard(v);
                            error(getString(R.string.invalid_email));
                        }
                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.Please_enter_your_email));
                    }
                }


                //hint
                if (rootChangeHint.getVisibility() == View.VISIBLE) {
                    if (edtChangeHint.length() > 0) {
                        if (!password.equals(edtChangeHint.getText().toString())) {
                            new RequestUserTwoStepVerificationChangeHint().changeHint(password, edtChangeHint.getText().toString());
                            closeKeyboard(v);
                            edtChangeHint.setText("");
                        } else {
                            closeKeyboard(v);
                            error(getString(R.string.hint_can_same_password));
                        }
                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.Please_enter_your_hint));
                    }
                }


                //confirm
                if (rootConfirmedEmail.getVisibility() == View.VISIBLE) {
                    if (edtConfirmedEmail.length() > 0) {
                        new RequestUserTwoStepVerificationVerifyRecoveryEmail().recoveryEmail(edtConfirmedEmail.getText().toString());
                        edtConfirmedEmail.setText("");
                        closeKeyboard(v);
                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.please_enter_code));
                    }
                }
            }
        });

    }

    private void viewConfirmEmail() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                page = 0;
                rootSetPassword.setVisibility(View.VISIBLE);
                rootConfirmedEmail.setVisibility(View.GONE);
                rippleOk.setVisibility(View.GONE);
            }
        });
    }

    private void viewChangeEmail() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                page = 0;
                rootSetPassword.setVisibility(View.VISIBLE);
                rootChangeEmail.setVisibility(View.GONE);
                rippleOk.setVisibility(View.GONE);
            }
        });
    }

    private void viewChangeHint() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                page = 0;
                rootSetPassword.setVisibility(View.VISIBLE);
                rootChangeHint.setVisibility(View.GONE);
                rippleOk.setVisibility(View.GONE);
            }
        });

    }

    private void viewChangeRecoveryQuestion() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                page = 0;
                rootSetPassword.setVisibility(View.VISIBLE);
                rootQuestionPassword.setVisibility(View.GONE);
                rippleOk.setVisibility(View.GONE);
            }
        });

    }

    private void closeKeyboard(View v) {
        if (mActivity != null) {
            try {
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    private void error(String error) {
        if (mActivity != null) {
            try {
                Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                vShort.vibrate(200);
                final Snackbar snack = Snackbar.make(mActivity.findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG);
                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snack.dismiss();
                    }
                });
                snack.show();
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    private void dialogWaitTime(long time) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialogWait = new MaterialDialog.Builder(mActivity).title(R.string.error_check_password).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(true).canceledOnTouchOutside(true).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                dialog.dismiss();
            }
        }).show();

        View v = dialogWait.getCustomView();

        final TextView remindTime = (TextView) v.findViewById(R.id.remindTime);
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }


    private Pattern patternEmail() {
        return Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{2,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,25}" + ")+");
    }

}
