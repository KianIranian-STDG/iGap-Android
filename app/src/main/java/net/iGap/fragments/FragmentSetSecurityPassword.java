package net.iGap.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.RecoveryEmailCallback;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.request.RequestUserTwoStepVerificationResendVerifyEmail;
import net.iGap.request.RequestUserTwoStepVerificationSetPassword;
import net.iGap.request.RequestUserTwoStepVerificationVerifyRecoveryEmail;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSetSecurityPassword extends BaseFragment implements ToolbarListener {

    private static String txtPassword;
    private static String oldPassword = "";
    private int page = 1;
    private EditText edtSetPassword;
    private EditText edtSetRePassword;
    private EditText edtSetHintPassword;
    private EditText edtSetQuestionPassOne;
    private EditText edtSetAnswerPassOne;
    private EditText edtSetQuestionPassTwo;
    private EditText edtSetAnswerPassTwo;
    private EditText edtSetEmail;
    private EditText edtSetConfirmEmail;
    private HelperToolbar mHelperToolbar;

    private ViewGroup rootEnterPassword;
    private ViewGroup rootReEnterPassword;
    private ViewGroup rootHintPassword;
    private ViewGroup rootQuestionPassword;
    private ViewGroup rootEmail;
    private ViewGroup rootConfirmEmail;

    public FragmentSetSecurityPassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_set_security_password, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle bundle = this.getArguments();
        if (bundle != null) {

            oldPassword = bundle.getString("OLD_PASSWORD");
        }


        mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setDefaultTitle(G.context.getResources().getString(R.string.your_password))
                .setLeftIcon(R.string.icon_back)
                .setRightIcons(R.string.icon_sent)
                .setLogoShown(true)
                .setListener(this);

        ViewGroup layoutToolbar = view.findViewById(R.id.fssp_layout_toolbar);
        layoutToolbar.addView(mHelperToolbar.getView());


        view.findViewById(R.id.rootSetPasswordSecurity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rootEnterPassword = view.findViewById(R.id.rootEnterPassword);
        rootReEnterPassword = view.findViewById(R.id.rootReEnterPassword);
        rootHintPassword = view.findViewById(R.id.rootHintPassword);
        rootQuestionPassword = view.findViewById(R.id.rootQuestionPassword);
        rootEmail = view.findViewById(R.id.rootEmail);
        rootConfirmEmail = view.findViewById(R.id.rootConfirmEmail);

        TextView txtSkipConfirmEmail = view.findViewById(R.id.txtSkipConfirmEmail);
        TextView txtResendConfirmEmail = view.findViewById(R.id.txtResendConfirmEmail);
        TextView txtSkipSetEmail = view.findViewById(R.id.txtSkipSetEmail);
        txtResendConfirmEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestUserTwoStepVerificationResendVerifyEmail().ResendVerifyEmail();
                closeKeyboard(v);
                error(getString(R.string.resend_verify_email_code));
            }
        });


        txtSkipConfirmEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(v);
                //mActivity.getSupportFragmentManager().popBackStack();

                popBackStackFragment();

                edtSetRePassword.setText("");
                edtSetHintPassword.setText("");
                edtSetQuestionPassOne.setText("");
                edtSetQuestionPassTwo.setText("");
                edtSetAnswerPassOne.setText("");
                edtSetAnswerPassTwo.setText("");
                edtSetEmail.setText("");
            }
        });

        txtSkipSetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 5;
                FragmentSecurity.isSetRecoveryEmail = false;
                edtSetEmail.setText("");
                onRightIconClickListener(v);
            }
        });

        //
        edtSetPassword = view.findViewById(R.id.setPassword_edtSetPassword);
        edtSetPassword.requestFocus();
        openKeyboard(edtSetPassword);
        edtSetRePassword = view.findViewById(R.id.setPassword_edtSetRePassword);
        edtSetHintPassword = view.findViewById(R.id.edtSetHintPassword);
        edtSetQuestionPassOne = view.findViewById(R.id.edtSetQuestionPassOne);
        edtSetAnswerPassOne = view.findViewById(R.id.edtSetAnswerPassOne);
        edtSetQuestionPassTwo = view.findViewById(R.id.edtSetQuestionPassTwo);
        edtSetAnswerPassTwo = view.findViewById(R.id.edtSetAnswerPassTwo);
        edtSetEmail = view.findViewById(R.id.edtSetEmail);
        edtSetConfirmEmail = view.findViewById(R.id.edtSetConfirmEmail);

    }

    private Pattern patternEmail() {
        return Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{2,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{1,25}" + ")+");
    }

    private void openKeyboard(View v) {
        if (isAdded()) {
            try {
                InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    private void error(String error) {

        if (isAdded()) {
            try {

                HelperError.showSnackMessage(error, true);

            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    @Override
    public void onLeftIconClickListener(View view) {
        popBackStackFragment();
        closeKeyboard(view);
    }

    @Override
    public void onRightIconClickListener(View v) {

        if (page == 1) {
            if (edtSetPassword.length() >= 2) {

                page = 2;
                mHelperToolbar.setDefaultTitle(G.fragmentActivity.getResources().getString(R.string.your_password));
                txtPassword = edtSetPassword.getText().toString();
                rootEnterPassword.setVisibility(View.GONE);
                rootReEnterPassword.setVisibility(View.VISIBLE);
                edtSetRePassword.requestFocus();
            } else {
                closeKeyboard(v);
                error(getString(R.string.Password_has_to_mor_than_character));
            }


        } else if (page == 2) {

            if (edtSetRePassword.length() >= 2) {
                if (txtPassword.equals(edtSetRePassword.getText().toString())) {

                    page = 3;
                    mHelperToolbar.setDefaultTitle(G.fragmentActivity.getResources().getString(R.string.password_hint));
                    rootReEnterPassword.setVisibility(View.GONE);
                    rootHintPassword.setVisibility(View.VISIBLE);
                    edtSetHintPassword.requestFocus();
                } else {
                    closeKeyboard(v);
                    error(getString(R.string.Password_dose_not_match));
                }

            } else {
                closeKeyboard(v);
                error(getString(R.string.Password_has_to_mor_than_character));
            }

        } else if (page == 3) {

            if (edtSetHintPassword.length() > 0) {

                if (!txtPassword.equals(edtSetHintPassword.getText().toString())) {

                    page = 4;
                    mHelperToolbar.setDefaultTitle(G.fragmentActivity.getResources().getString(R.string.recovery_question));
                    rootHintPassword.setVisibility(View.GONE);
                    rootQuestionPassword.setVisibility(View.VISIBLE);
                    edtSetQuestionPassOne.requestFocus();

                } else {
                    closeKeyboard(v);
                    error(getString(R.string.Hint_cant_the_same_password));
                }
            } else {
                closeKeyboard(v);
                error(getString(R.string.please_set_hint));
            }

        } else if (page == 4) {
            if (edtSetQuestionPassOne.length() > 0 && edtSetQuestionPassTwo.length() > 0 && edtSetAnswerPassOne.length() > 0 && edtSetAnswerPassTwo.length() > 0) {
                page = 5;
                mHelperToolbar.setDefaultTitle(G.fragmentActivity.getResources().getString(R.string.recovery_email));
                rootQuestionPassword.setVisibility(View.GONE);
                rootEmail.setVisibility(View.VISIBLE);

            } else {
                closeKeyboard(v);
                error(getString(R.string.please_complete_all_item));
            }
        } else if (page == 5) {

            FragmentSecurity.isFirstSetPassword = false;
            if (edtSetEmail.getText() != null && edtSetEmail.getText().toString().length() > 0) {
                Pattern EMAIL_ADDRESS = patternEmail();

                if (EMAIL_ADDRESS.matcher(edtSetEmail.getText().toString()).matches()) {
                    page = 6;
                    new RequestUserTwoStepVerificationSetPassword().setPassword(oldPassword, txtPassword, edtSetEmail.getText().toString(), edtSetQuestionPassOne.getText().toString(), edtSetAnswerPassOne.getText().toString(), edtSetQuestionPassTwo.getText().toString(), edtSetAnswerPassTwo.getText().toString(), edtSetHintPassword.getText().toString());

                    mHelperToolbar.setDefaultTitle(G.fragmentActivity.getResources().getString(R.string.recovery_email));
                    rootEmail.setVisibility(View.GONE);
                    rootConfirmEmail.setVisibility(View.VISIBLE);
                } else {
                    closeKeyboard(v);
                    error(getString(R.string.invalid_email));
                }
            } else {
                closeKeyboard(v);
                error(getString(R.string.invalid_email));
            }


        } else if (page == 6) {

            if (edtSetConfirmEmail.length() > 0) {
                new RequestUserTwoStepVerificationVerifyRecoveryEmail().recoveryEmail(edtSetConfirmEmail.getText().toString(), new RecoveryEmailCallback() {
                    @Override
                    public void confirmEmail() {
                        G.handler.post(() -> {

                            page = 0;
                            edtSetRePassword.setText("");
                            edtSetHintPassword.setText("");
                            edtSetQuestionPassOne.setText("");
                            edtSetQuestionPassTwo.setText("");
                            edtSetAnswerPassOne.setText("");
                            edtSetAnswerPassTwo.setText("");
                            edtSetEmail.setText("");

                            popBackStackFragment();
                            popBackStackFragment();
                        });
                    }

                    @Override
                    public void errorEmail(int major, int minor) {

                    }
                });
            } else {
                error(getString(R.string.enter_verify_email_code));
            }
            closeKeyboard(v);
        }

    }
}
