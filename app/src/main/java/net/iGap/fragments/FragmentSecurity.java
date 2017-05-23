package net.iGap.fragments;



import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.OnTwoStepPassword;
import net.iGap.libs.rippleeffect.RippleView;
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

        final ViewGroup rootSetPassword = (ViewGroup) view.findViewById(R.id.rootSetPassword);
        final ViewGroup rootCheckPassword = (ViewGroup) view.findViewById(R.id.rootCheckPassword);
        final ViewGroup rootSetAdditionPassword = (ViewGroup) view.findViewById(R.id.rootSetAdditionPassword);
        final ViewGroup root_ChangePassword = (ViewGroup) view.findViewById(R.id.root_ChangePassword);
        final ViewGroup rootQuestionPassword = (ViewGroup) view.findViewById(R.id.tsv_rootQuestionPassword);
        final ViewGroup rootEmail = (ViewGroup) view.findViewById(R.id.tsv_rootEmail);
        final ViewGroup rootChangeHint = (ViewGroup) view.findViewById(R.id.tsv_rootChangeHint);
        final ViewGroup rootConfirmedEmail = (ViewGroup) view.findViewById(R.id.tsv_rootConfirmedEmail);

        final RippleView rippleOk = (RippleView) view.findViewById(R.id.verifyPassword_rippleOk);
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


        txtSetConfirmedEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        txtChangeHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rootSetPassword.setVisibility(View.GONE);
                rootSetAdditionPassword.setVisibility(View.GONE);
                rootChangeHint.setVisibility(View.VISIBLE);
                rippleOk.setVisibility(View.VISIBLE);

            }
        });

        txtSetRecoveryEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rootSetPassword.setVisibility(View.GONE);
                rootSetAdditionPassword.setVisibility(View.GONE);
                rootEmail.setVisibility(View.VISIBLE);
                rippleOk.setVisibility(View.VISIBLE);

            }
        });

        txtTurnPasswordOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity()).title(R.string.st_keepMedia).content(R.string.st_dialog_content_keepMedia).positiveText(getResources().getString(R.string.keep_media_forever)).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        new RequestUserTwoStepVerificationUnsetPassword().unsetPassword(password);
                    }
                }).negativeText(getResources().getString(R.string.keep_media_1week)).onNegative(new MaterialDialog.SingleButtonCallback() {
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
                rootSetPassword.setVisibility(View.GONE);
                rootSetAdditionPassword.setVisibility(View.GONE);
                rootQuestionPassword.setVisibility(View.VISIBLE);
                rippleOk.setVisibility(View.VISIBLE);
            }
        });

        G.onTwoStepPassword = new OnTwoStepPassword() {
            @Override
            public void getPasswordDetail(final String questionOne, final String questionTwo, final String hint, final boolean hasConfirmedRecoveryEmail, final String unconfirmedEmailPattern) {

                Log.i("CCCCCCC", "questionOne: " + questionOne);
                Log.i("CCCCCCC", "questionTwo: " + questionTwo);
                Log.i("CCCCCCC", "hint: " + hint);
                Log.i("CCCCCCC", "hasConfirmedRecoveryEmail: " + hasConfirmedRecoveryEmail);
                Log.i("CCCCCCC", "unconfirmedEmailPattern: " + unconfirmedEmailPattern);

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
                    public void run() {//دوبار اجرا شده
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
                        rootSetPassword.setVisibility(View.VISIBLE);
                        rootSetAdditionPassword.setVisibility(View.VISIBLE);
                        rootQuestionPassword.setVisibility(View.GONE);
                        rippleOk.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void changeHint() {

            }
        };

        view.findViewById(R.id.rootFragmentSecurity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        RippleView btnBack = (RippleView) view.findViewById(R.id.tsv_ripple_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentSecurity.this).commit();

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


        // check password for enter on page
        rippleOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                password = edtCheckPassword.getText().toString();




                if (password.length() > 1) {
                    new RequestUserTwoStepVerificationCheckPassword().checkPassword(password);
                } else {
                    Toast.makeText(getActivity(), "Password has to mor than 2 character", Toast.LENGTH_SHORT).show();
                }

                if (edtSetQuestionPassOne.length() > 0 && edtSetAnswerPassOne.length() > 0 && edtSetQuestionPassTwo.length() > 0 && edtSetAnswerPassTwo.length() > 0) {
                    new RequestUserTwoStepVerificationChangeRecoveryQuestion().changeRecoveryQuestion(password, edtSetQuestionPassOne.getText().toString(), edtSetAnswerPassOne.getText().toString(), edtSetQuestionPassTwo.getText().toString(), edtSetAnswerPassTwo.getText().toString());
                } else {
                    Toast.makeText(getActivity(), "Please complete all Item", Toast.LENGTH_SHORT).show();
                }

                if (edtSetEmail.length() > 0) {
                    new RequestUserTwoStepVerificationChangeRecoveryEmail().changeRecoveryEmail(password, edtSetEmail.getText().toString());
                } else {
                    Toast.makeText(getActivity(), "Please enter your email", Toast.LENGTH_SHORT).show();
                }

                if (edtChangeHint.length() > 0) {
                    if (password.contains(edtChangeHint.getText().toString())) {
                        new RequestUserTwoStepVerificationChangeHint().changeHint(password, edtChangeHint.getText().toString());
                    } else {
                        Toast.makeText(getActivity(), "hint can't same password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please enter your hint", Toast.LENGTH_SHORT).show();
                }

                if (edtConfirmedEmail.length() > 0) {
                    new RequestUserTwoStepVerificationVerifyRecoveryEmail().recoveryEmail(edtConfirmedEmail.getText().toString());
                } else {
                    Toast.makeText(getActivity(), "Please enter your code", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
