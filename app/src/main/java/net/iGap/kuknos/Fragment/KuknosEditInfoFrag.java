package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.kuknos.Model.Parsian.KuknosUserInfoResponse;
import net.iGap.kuknos.viewmodel.KuknosEditInfoVM;


public class KuknosEditInfoFrag extends BaseAPIViewFrag<KuknosEditInfoVM> {
    private Button submit;
    private ProgressBar progressBar;
    private EditText nationalId;
    private EditText IBN;
    private EditText birthDate;
    private EditText firstName;
    private EditText lastName;
    private KuknosUserInfoResponse userInfo;

    public static KuknosEditInfoFrag newInstance() {
        KuknosEditInfoFrag fragment = new KuknosEditInfoFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosEditInfoVM.class);
        viewModel.getInFoFromServerToCheckUserProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kuknos_edit_info, container, false);
        initView(view);
        firstName.setEnabled(false);
        lastName.setEnabled(false);
        nationalId.setEnabled(false);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfo = new KuknosUserInfoResponse();
                if (!IBN.getText().toString().isEmpty() && !birthDate.getText().toString().isEmpty()) {
                    userInfo.setIban(IBN.getText().toString().trim());
                    userInfo.setFirstName(firstName.getText().toString().trim());
                    userInfo.setLastName(lastName.getText().toString().trim());
                    userInfo.setBirthDate(birthDate.getText().toString().trim());
                    viewModel.sendUserInfo(userInfo);
                } else {
                    if (birthDate.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "فیلد تاریخ تولد نمی تواند خالی باشد", Toast.LENGTH_SHORT).show();
                    } else if (IBN.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "فیلد شبا نمی تواند خالی باشد", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        onProgressObserver();
        onUserInfoObserver();
        return view;

    }

    private void onProgressObserver() {
        viewModel.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                Toast.makeText(getContext(), "اطلاعات با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onUserInfoObserver() {
        viewModel.getUserInfo().observe(getViewLifecycleOwner(), userInfo -> {
            if (userInfo == null) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            } else {
                nationalId.setText(userInfo.getNationalCode());
                firstName.setText(userInfo.getFirstName());
                lastName.setText(userInfo.getLastName());
                if (userInfo.getIban() != null) {
                    IBN.setText(userInfo.getIban());
                }
                if (userInfo.getBirthDate() != null) {
                    birthDate.setText(userInfo.getBirthDate());
                }
            }
        });
    }

    private void initView(View view) {
        nationalId = view.findViewById(R.id.kuknos_editInfo_nationalCode);
        submit = view.findViewById(R.id.fragKuknosEditInfoSubmit);
        progressBar = view.findViewById(R.id.fragKuknosEditInfoProgressV);
        firstName = view.findViewById(R.id.kuknos_editInfo_name);
        lastName = view.findViewById(R.id.kuknos_editInfo_lastName);
        birthDate = view.findViewById(R.id.kuknos_editInfo_birthDate_editText);
        IBN = view.findViewById(R.id.kuknos_editInfo_shaba);
    }

}