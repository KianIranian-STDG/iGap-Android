package net.iGap.fragments;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import net.iGap.R;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.request.RequestUserTwoStepVerificationSetPassword;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSetSecurityPassword extends Fragment {

    private int page = 1;
    private EditText edtSetPassword;
    private EditText edtSetRePassword;
    private EditText edtSetHintPassword;
    private EditText edtSetQuestionPassOne;
    private EditText edtSetAnswerPassOne;
    private EditText edtSetQuestionPassTwo;
    private EditText edtSetAnswerPassTwo;
    private EditText edtSetEmail;
    private String txtPassword;
    private String oldPassword = "";

    public FragmentSetSecurityPassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_security_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            oldPassword = bundle.getString("OLD_PASSWORD");
        }


        view.findViewById(R.id.rootSetPasswordSecurity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        RippleView btnBack = (RippleView) view.findViewById(R.id.setPassword_ripple_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        final TextView txtToolbar = (TextView) view.findViewById(R.id.setPassword_toolbar);

        final ViewGroup rootEnterPassword = (ViewGroup) view.findViewById(R.id.rootEnterPassword);
        final ViewGroup rootReEnterPassword = (ViewGroup) view.findViewById(R.id.rootReEnterPassword);
        final ViewGroup rootHintPassword = (ViewGroup) view.findViewById(R.id.rootHintPassword);
        final ViewGroup rootQuestionPassword = (ViewGroup) view.findViewById(R.id.rootQuestionPassword);
        final ViewGroup rootEmail = (ViewGroup) view.findViewById(R.id.rootEmail);


        RippleView btnOk = (RippleView) view.findViewById(R.id.setPassword_rippleOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtSetPassword.length() > 1 && page == 1) {
                    page = 2;
                    txtToolbar.setText(getResources().getString(R.string.your_password));
                    txtPassword = edtSetPassword.getText().toString();
                    rootEnterPassword.setVisibility(View.GONE);
                    rootReEnterPassword.setVisibility(View.VISIBLE);


                } else if (page == 2) {
                    if (edtSetRePassword.length() > 1 && txtPassword.contains(edtSetRePassword.getText().toString())) {
                        page = 3;
                        txtToolbar.setText(getResources().getString(R.string.password_hint));
                        rootReEnterPassword.setVisibility(View.GONE);
                        rootHintPassword.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getActivity(), "Password do not match", Toast.LENGTH_SHORT).show();
                    }

                } else if (page == 3) {

                    if (edtSetHintPassword.length() > 0 && !txtPassword.contains(edtSetHintPassword.getText().toString())) {
                        page = 4;
                        txtToolbar.setText(getResources().getString(R.string.recovery_question));
                        rootHintPassword.setVisibility(View.GONE);
                        rootQuestionPassword.setVisibility(View.VISIBLE);

                    } else {
                        Toast.makeText(getActivity(), "Hint cant the same password", Toast.LENGTH_SHORT).show();
                    }

                } else if (page == 4) {
                    if (edtSetQuestionPassOne.length() > 0 && edtSetQuestionPassTwo.length() > 0 && edtSetAnswerPassOne.length() > 0 && edtSetAnswerPassTwo.length() > 0) {
                        page = 5;
                        txtToolbar.setText(getResources().getString(R.string.recovery_email));
                        rootQuestionPassword.setVisibility(View.GONE);
                        rootEmail.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getActivity(), "Please complete all item ", Toast.LENGTH_SHORT).show();
                    }
                } else if (page == 5) {

                    new RequestUserTwoStepVerificationSetPassword().setPassword(oldPassword, txtPassword, edtSetEmail.getText().toString(), edtSetQuestionPassOne.getText().toString(), edtSetAnswerPassOne.getText().toString(), edtSetQuestionPassTwo.getText().toString(), edtSetAnswerPassTwo.getText().toString(), edtSetHintPassword.getText().toString());
                    getActivity().getSupportFragmentManager().popBackStack();
                }

            }
        });
        //
        edtSetPassword = (EditText) view.findViewById(R.id.setPassword_edtSetPassword);
        edtSetRePassword = (EditText) view.findViewById(R.id.setPassword_edtSetRePassword);
        edtSetHintPassword = (EditText) view.findViewById(R.id.edtSetHintPassword);
        edtSetQuestionPassOne = (EditText) view.findViewById(R.id.edtSetQuestionPassOne);
        edtSetAnswerPassOne = (EditText) view.findViewById(R.id.edtSetAnswerPassOne);
        edtSetQuestionPassTwo = (EditText) view.findViewById(R.id.edtSetQuestionPassTwo);
        edtSetAnswerPassTwo = (EditText) view.findViewById(R.id.edtSetAnswerPassTwo);
        edtSetEmail = (EditText) view.findViewById(R.id.edtSetEmail);



    }
}
