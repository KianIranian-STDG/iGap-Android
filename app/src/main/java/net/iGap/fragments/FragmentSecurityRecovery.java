package net.iGap.fragments;



import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.OnRecoverySecurityPassword;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.enums.Security;
import net.iGap.request.RequestUserTwoStepVerificationRecoverPasswordByAnswers;
import net.iGap.request.RequestUserTwoStepVerificationRecoverPasswordByToken;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSecurityRecovery extends Fragment {

    private Security page;
    //private String page;
    private String questionOne = "";
    private String questionTwo = "";
    private boolean isRecoveryByEmail;
    private ViewGroup rootRecoveryEmail;
    private ViewGroup rootRecoveryQuestionPassword;
    private EditText edtSetRecoveryAnswerPassOne;
    private EditText edtSetRecoveryAnswerPassTwo;
    private EditText edtSetRecoveryEmail;
    private TextView txtSetRecoveryByQuestion;
    private TextView txtSetRecoveryByEmail;
    private TextView txtSetRecoveryQuestionPassOne;
    private TextView txtSetRecoveryQuestionPassTwo;


    public FragmentSecurityRecovery() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_security_recovery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.stps_backgroundToolbar).setBackgroundColor(Color.parseColor(G.appBarColor));
        view.findViewById(R.id.fpac_view_line).setBackgroundColor(Color.parseColor(G.appBarColor));

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            page = (Security) bundle.get("PAGE");
            questionOne = bundle.getString("QUESTION_ONE");
            questionTwo = bundle.getString("QUESTION_TWO");
            isRecoveryByEmail = bundle.getBoolean("IS_EMAIL");
        }

        RippleView ripple_back = (RippleView) view.findViewById(R.id.ripple_back);

        ripple_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeKeyboard(v);
                if (page == Security.SETTING) {
                    pageSetting();
                } else {
                    pageRegister();
                }

            }
        });

        view.findViewById(R.id.rootRecoveryPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        RippleView rippleOk = (RippleView) view.findViewById(R.id.verifyPassword_rippleOk);

        rootRecoveryEmail = (ViewGroup) view.findViewById(R.id.rootRecoveryEmailPassword);
        rootRecoveryQuestionPassword = (ViewGroup) view.findViewById(R.id.rootRecoveryQuestionPassword);

        txtSetRecoveryByQuestion = (TextView) view.findViewById(R.id.txtSetRecoveryByQuestion);
        txtSetRecoveryByEmail = (TextView) view.findViewById(R.id.txtSetRecoveryByEmail);
        txtSetRecoveryQuestionPassOne = (TextView) view.findViewById(R.id.txtSetRecoveryQuestionPassOne);
        txtSetRecoveryQuestionPassTwo = (TextView) view.findViewById(R.id.txtSetRecoveryQuestionPassTwo);

        txtSetRecoveryQuestionPassOne.setText(questionOne);
        txtSetRecoveryQuestionPassTwo.setText(questionTwo);

        edtSetRecoveryAnswerPassOne = (EditText) view.findViewById(R.id.edtSetRecoveryAnswerPassOne);
        edtSetRecoveryAnswerPassTwo = (EditText) view.findViewById(R.id.edtSetRecoveryAnswerPassTwo);
        edtSetRecoveryEmail = (EditText) view.findViewById(R.id.edtSetRecoveryEmail);

        if (isRecoveryByEmail) {
            rootRecoveryEmail.setVisibility(View.VISIBLE);
            rootRecoveryQuestionPassword.setVisibility(View.GONE);
        } else {
            rootRecoveryQuestionPassword.setVisibility(View.VISIBLE);
            rootRecoveryEmail.setVisibility(View.GONE);
        }

        txtSetRecoveryByEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootRecoveryQuestionPassword.setVisibility(View.GONE);
                rootRecoveryEmail.setVisibility(View.VISIBLE);
            }
        });

        txtSetRecoveryByQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootRecoveryEmail.setVisibility(View.GONE);
                rootRecoveryQuestionPassword.setVisibility(View.VISIBLE);
            }
        });

        if (page == Security.SETTING) {
            G.onRecoverySecurityPassword = new OnRecoverySecurityPassword() {
                @Override
                public void recoveryByEmail(String token) {


                    if (page == Security.SETTING) {
                        pageSetting();
                    } else {
                        pageRegister();
                    }

                }

                @Override
                public void recoveryByQuestion(String token) {
                    if (page == Security.SETTING) {
                        pageSetting();
                    } else {
                        pageRegister();
                    }
                }
            };
        }


        rippleOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rootRecoveryEmail.getVisibility() == View.VISIBLE) {
                    if (edtSetRecoveryEmail.length() > 0) {
                        new RequestUserTwoStepVerificationRecoverPasswordByToken().recoveryPasswordByToken(edtSetRecoveryEmail.getText().toString());
                        closeKeyboard(v);
                        edtSetRecoveryEmail.setText("");
                    } else {
                        error(getString(R.string.please_enter_code));
                    }
                } else {
                    if (edtSetRecoveryAnswerPassOne.length() > 0 && edtSetRecoveryAnswerPassTwo.length() > 0) {
                        new RequestUserTwoStepVerificationRecoverPasswordByAnswers().RecoveryPasswordByAnswer(edtSetRecoveryAnswerPassOne.getText().toString(), edtSetRecoveryAnswerPassTwo.getText().toString());
                        edtSetRecoveryAnswerPassOne.setText("");
                        edtSetRecoveryAnswerPassTwo.setText("");
                        closeKeyboard(v);

                    } else {

                        error(getString(R.string.please_complete_all_item));
                    }
                }
            }
        });
    }

    private void pageRegister() {

        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void pageSetting() {

        getActivity().getSupportFragmentManager().popBackStack();
        //FragmentSecurity fragmentSecurity = new FragmentSecurity();
        //getActivity()
        //    .getSupportFragmentManager()
        //    .beginTransaction()
        //    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
        //    .replace(android.R.id.content, fragmentSecurity)
        //    .commit();

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
