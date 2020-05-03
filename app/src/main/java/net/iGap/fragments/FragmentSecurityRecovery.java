package net.iGap.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityRegistration;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.repository.RegisterRepository;
import net.iGap.module.enums.Security;
import net.iGap.observers.interfaces.OnRecoveryEmailToken;
import net.iGap.observers.interfaces.OnRecoverySecurityPassword;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.observers.interfaces.TwoStepVerificationRecoverPasswordByAnswersCallback;
import net.iGap.observers.interfaces.TwoStepVerificationRecoverPasswordByToken;
import net.iGap.request.RequestUserTwoStepVerificationRecoverPasswordByAnswers;
import net.iGap.request.RequestUserTwoStepVerificationRecoverPasswordByToken;
import net.iGap.request.RequestUserTwoStepVerificationRequestRecoveryToken;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSecurityRecovery extends BaseFragment {

    private Security page;
    //private String page;
    private String questionOne = "";
    private String questionTwo = "";
    private String txtPaternEmail = "";
    private boolean isRecoveryByEmail;
    private ViewGroup rootRecoveryEmail;
    private ProgressBar pbLoading;
    private ViewGroup rootRecoveryQuestionPassword;
    private EditText edtSetRecoveryAnswerPassOne;
    private EditText edtSetRecoveryAnswerPassTwo;
    private EditText edtSetRecoveryEmail;
    private TextView txtSetRecoveryByQuestion;
    private TextView txtSetRecoveryByEmail;
    private TextView txtSetRecoveryQuestionPassOne;
    private TextView txtSetRecoveryQuestionPassTwo;
    private boolean isConfirmedRecoveryEmail;
    private HelperToolbar mHelperToolbar;

    public FragmentSecurityRecovery() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_security_recovery, container, false));
    }

    @Override
    public void onViewCreated(@NotNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setDefaultTitle(G.context.getResources().getString(R.string.recovery_Password))
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.check_icon)
                .setLogoShown(true)
                .setShowConnectionState(false)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {

                        closeKeyboard(view);
                        if (page == Security.SETTING) {
                            pageSetting();
                        } else {
                            pageRegister();
                        }

                    }

                    @Override
                    public void onRightIconClickListener(View v) {
                        closeKeyboard(v);
                        if (rootRecoveryEmail.getVisibility() == View.VISIBLE) {
                            if (edtSetRecoveryEmail.length() > 0) {
                                setLoaderState(true);
                                new RequestUserTwoStepVerificationRecoverPasswordByToken().recoveryPasswordByToken(edtSetRecoveryEmail.getText().toString(), new TwoStepVerificationRecoverPasswordByToken() {
                                    @Override
                                    public void recoveryByEmail(String tokenR) {
                                        setLoaderState(false);
                                        if (getActivity() instanceof ActivityRegistration) {
                                            openMainActivity(tokenR);
                                        } else if (getActivity() instanceof ActivityMain) {
                                            backToSettings();
                                        }
                                    }

                                    @Override
                                    public void errorRecoveryByEmail(int major, int minor) {
                                        setLoaderState(false);
                                    }
                                });
                            } else {
                                error(G.fragmentActivity.getResources().getString(R.string.please_enter_code));
                            }
                        } else {
                            if (edtSetRecoveryAnswerPassOne.length() > 0 && edtSetRecoveryAnswerPassTwo.length() > 0) {
                                setLoaderState(true);
                                new RequestUserTwoStepVerificationRecoverPasswordByAnswers().RecoveryPasswordByAnswer(edtSetRecoveryAnswerPassOne.getText().toString(), edtSetRecoveryAnswerPassTwo.getText().toString(), new TwoStepVerificationRecoverPasswordByAnswersCallback() {
                                    @Override
                                    public void recoveryByQuestion(String tokenR) {
                                        setLoaderState(false);
                                        //Todo:fixed it and move to repository
                                        if (getActivity() instanceof ActivityRegistration) {
                                            openMainActivity(tokenR);
                                        } else if (getActivity() instanceof ActivityMain) {
                                            backToSettings();
                                        }
                                    }

                                    @Override
                                    public void errorRecoveryByQuestion(int major, int minor) {
                                        setLoaderState(false);
                                    }
                                });

                            } else {
                                error(G.fragmentActivity.getResources().getString(R.string.please_complete_all_item));
                            }
                        }
                    }
                });

        ViewGroup toolbarLayout = view.findViewById(R.id.fsr_layout_toolbar);
        toolbarLayout.addView(mHelperToolbar.getView());

        new RequestUserTwoStepVerificationRequestRecoveryToken().requestRecoveryToken();

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            page = (Security) bundle.get("PAGE");
            questionOne = bundle.getString("QUESTION_ONE");
            questionTwo = bundle.getString("QUESTION_TWO");
            txtPaternEmail = bundle.getString("PATERN_EMAIL");
            isRecoveryByEmail = bundle.getBoolean("IS_EMAIL");
            isConfirmedRecoveryEmail = bundle.getBoolean("IS_CONFIRM_EMAIL");
        }

        view.findViewById(R.id.rootRecoveryPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        pbLoading = view.findViewById(R.id.pbLoading);
        rootRecoveryEmail = view.findViewById(R.id.rootRecoveryEmailPassword);
        rootRecoveryQuestionPassword = view.findViewById(R.id.rootRecoveryQuestionPassword);

        txtSetRecoveryByQuestion = view.findViewById(R.id.txtSetRecoveryByQuestion);
        txtSetRecoveryByEmail = view.findViewById(R.id.txtSetRecoveryByEmail);

        if (!isConfirmedRecoveryEmail) {
            txtSetRecoveryByEmail.setVisibility(View.GONE);
        }

        txtSetRecoveryQuestionPassOne = view.findViewById(R.id.txtSetRecoveryQuestionPassOne);
        txtSetRecoveryQuestionPassTwo = view.findViewById(R.id.txtSetRecoveryQuestionPassTwo);

        txtSetRecoveryQuestionPassOne.setText(questionOne);
        txtSetRecoveryQuestionPassTwo.setText(questionTwo);

        TextView txtResendConfirmEmail = view.findViewById(R.id.txtResendConfirmEmail);
        txtResendConfirmEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestUserTwoStepVerificationRequestRecoveryToken().requestRecoveryToken();
                closeKeyboard(v);

                HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.resend_verify_email_code), false);
            }
        });

        edtSetRecoveryAnswerPassOne = view.findViewById(R.id.edtSetRecoveryAnswerPassOne);
        edtSetRecoveryAnswerPassTwo = view.findViewById(R.id.edtSetRecoveryAnswerPassTwo);
        edtSetRecoveryEmail = view.findViewById(R.id.edtSetRecoveryEmail);
        edtSetRecoveryEmail.setHint("");

        if (page == Security.REGISTER) {
            G.onRecoveryEmailToken = new OnRecoveryEmailToken() {
                @Override
                public void getEmailPatern(final String patern) {

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            edtSetRecoveryEmail.setHint(patern);
                        }
                    });
                }
            };
        }

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
                public void getEmailPatern(final String patern) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            edtSetRecoveryEmail.setHint(patern);
                        }
                    });
                }
            };
        }

    }

    private void setLoaderState(boolean state) {
        G.runOnUiThread(() -> {
            if (state) {
                pbLoading.setVisibility(View.VISIBLE);
                mHelperToolbar.getRightButton().setEnabled(false);
            } else {
                pbLoading.setVisibility(View.GONE);
                mHelperToolbar.getRightButton().setEnabled(true);
            }
        });
    }

    private void backToSettings() {
        G.handler.post(() -> {
            popBackStackFragment();
            popBackStackFragment();
        });
    }

    private void openMainActivity(String tokenR) {
        RegisterRepository repository = RegisterRepository.getInstance();
        repository.setForgetTwoStepVerification(true);
        repository.setToken(tokenR);
        repository.userLogin(tokenR);
    }

    private void pageRegister() {

        G.fragmentActivity.getSupportFragmentManager().popBackStack();
    }

    private void pageSetting() {

        G.fragmentActivity.getSupportFragmentManager().popBackStack();
        //FragmentSecurity fragmentSecurity = new FragmentSecurity();
        //G.fragmentActivity
        //    .getSupportFragmentManager()
        //    .beginTransaction()
        //    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
        //    .replace(android.R.id.content, fragmentSecurity)
        //    .commit();

    }

    private void error(String error) {
        if (G.fragmentActivity != null) {
            try {

                HelperError.showSnackMessage(error, true);

            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }
}
