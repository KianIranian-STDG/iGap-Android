package net.iGap.fragments;



import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
                        getActivity().getSupportFragmentManager().popBackStack();

                }
                closeKeyboard(v);
            }
        });

        TextView txtSetAdditionPassword = (TextView) view.findViewById(R.id.tsv_txtSetAdditionPassword);
        txtSetAdditionPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentSetSecurityPassword fragmentSetSecurityPassword = new FragmentSetSecurityPassword();
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.parentPrivacySecurity, fragmentSetSecurityPassword).commit();
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(getActivity()).title(R.string.set_recovery_question).items(R.array.securityRecoveryPassword).itemsCallback(new MaterialDialog.ListCallback() {
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
                        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.parentPrivacySecurity, fragmentSecurityRecovery).commit();

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
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.parentPrivacySecurity, fragmentSetSecurityPassword).commit();
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
                new MaterialDialog.Builder(getActivity()).title(R.string.turn_Password_off).content(R.string.turn_Password_off_desc).positiveText(getResources().getString(R.string.B_ok)).onPositive(new MaterialDialog.SingleButtonCallback() {
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
                getActivity().runOnUiThread(new Runnable() {
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rootSetPassword.setVisibility(View.VISIBLE);
                        rootCheckPassword.setVisibility(View.GONE);
                        rippleOk.setVisibility(View.GONE);
                    }
                });

            }

            @Override
            public void unSetPassword() {
                getActivity().runOnUiThread(new Runnable() {
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
                getActivity().runOnUiThread(new Runnable() {
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
        };



        // check password for enter on page
        rippleOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                password = edtCheckPassword.getText().toString();

                if (rootCheckPassword.getVisibility() == View.VISIBLE) {
                    if (password.length() > 1) {
                        new RequestUserTwoStepVerificationCheckPassword().checkPassword(password);
                        closeKeyboard(v);
                        edtCheckPassword.setText("");
                    } else {
                        error(getString(R.string.Password_has_to_mor_than_character));
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

                        error(getString(R.string.Please_complete_all_Item));
                    }
                }

                //change email
                if (rootChangeEmail.getVisibility() == View.VISIBLE) {
                    if (edtSetEmail.length() > 0) {
                        new RequestUserTwoStepVerificationChangeRecoveryEmail().changeRecoveryEmail(password, edtSetEmail.getText().toString());
                        closeKeyboard(v);
                        edtSetEmail.setText("");
                    } else {
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
                            error(getString(R.string.hint_can_same_password));
                        }
                    } else {
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
                        error(getString(R.string.please_enter_code));
                    }
                }
            }
        });

    }

    private void viewConfirmEmail() {
        page = 0;
        rootSetPassword.setVisibility(View.VISIBLE);
        rootConfirmedEmail.setVisibility(View.GONE);
        rippleOk.setVisibility(View.GONE);
    }

    private void viewChangeEmail() {
        page = 0;
        rootSetPassword.setVisibility(View.VISIBLE);
        rootChangeEmail.setVisibility(View.GONE);
        rippleOk.setVisibility(View.GONE);
    }

    private void viewChangeHint() {
        page = 0;
        rootSetPassword.setVisibility(View.VISIBLE);
        rootChangeHint.setVisibility(View.GONE);
        rippleOk.setVisibility(View.GONE);
    }

    private void viewChangeRecoveryQuestion() {
        page = 0;
        rootSetPassword.setVisibility(View.VISIBLE);
        rootQuestionPassword.setVisibility(View.GONE);
        rippleOk.setVisibility(View.GONE);
    }

    private void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void error(String error) {
        Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
        vShort.vibrate(200);
        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }


}
