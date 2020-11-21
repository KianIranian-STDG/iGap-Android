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

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.Model.Parsian.KuknosUserInfoResponse;
import net.iGap.kuknos.viewmodel.KuknosEditInfoVM;
import net.iGap.libs.persianDatePicker.Listener;
import net.iGap.libs.persianDatePicker.PersianDatePickerDialog;
import net.iGap.libs.persianDatePicker.util.PersianCalendar;
import net.iGap.module.accountManager.DbManager;
import net.iGap.realm.RealmKuknos;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;


public class KuknosEditInfoFrag extends BaseAPIViewFrag<KuknosEditInfoVM> {
    private Button submit;
    private ProgressBar progressBar;
    private EditText nationalId;
    private EditText IBN;
    private EditText birthDate;
    private EditText firstName;
    private EditText lastName;
    private KuknosUserInfoResponse userInfo;
    private PersianDatePickerDialog datePickerDialog;
    private String iban;
    private long miladiDate;
    private String ibanOldValue;
    private long birthDateOldValue;
    private boolean ibanInfo = true;
    private boolean userInfoR = true;

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
        iban = DbManager.getInstance().doRealmTask(new DbManager.RealmTaskWithReturn<String>() {

            @Override
            public String doTask(Realm realm) {
                RealmKuknos realmKuknos = realm.where(RealmKuknos.class).findFirst();
                if (realmKuknos != null && realmKuknos.getIban() != null) {
                    return realmKuknos.getIban();
                } else {
                    return null;
                }
            }
        });
        viewModel.getInFoFromServerToCheckUserProfile();
        if (iban != null) {
            viewModel.getIbanInfo(iban);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kuknos_edit_info, container, false);
        initView(view);
        firstName.setEnabled(false);
        lastName.setEnabled(false);
        nationalId.setEnabled(false);
        birthDate.setEnabled(false);
        IBN.setEnabled(false);
        submit.setEnabled(false);
        birthDate.setFocusable(false);
        progressBar.setVisibility(View.VISIBLE);
        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        datePickerDialog = new PersianDatePickerDialog(getActivity())
                .setPositiveButtonString(getString(R.string.kuknos_SetPassConf_submit))
                .setNegativeButton(getString(R.string.your_confirm_email_skip))
                .setMaxYear(1399)
                .setMinYear(1300)
                .setListener(new Listener() {
                    @Override
                    public void onDateSelected(PersianCalendar persianCalendar) {
                        setMiladiDate(persianCalendar);
                        birthDate.setText(persianCalendar.getPersianShortDate().replace("/", "-"));
                    }

                    @Override
                    public void onDismissed() {

                    }

                });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit.setEnabled(false);
                birthDate.setEnabled(false);
                IBN.setEnabled(false);
                userInfo = new KuknosUserInfoResponse();
                if (ibanOldValue != null && ibanOldValue.equals(IBN.getText().toString().trim()) && birthDateOldValue == miladiDate) {
                    Toast.makeText(getContext(), getText(R.string.kuknos_edit_info_empty_sheba_and_birthdate), Toast.LENGTH_SHORT).show();
                    submit.setEnabled(true);
                    birthDate.setEnabled(true);
                    IBN.setEnabled(true);
                } else if (firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), getText(R.string.kuknos_edit_info_empty_first_or_last), Toast.LENGTH_SHORT).show();
                    submit.setEnabled(true);
                    birthDate.setEnabled(true);
                    IBN.setEnabled(true);
                } else if (!IBN.getText().toString().isEmpty() && !birthDate.getText().toString().isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    submit.setText(getText(R.string.kuknos_edit_info_sending_info));
                    userInfo.setIban(IBN.getText().toString().trim());
                    userInfo.setFirstName(firstName.getText().toString().trim());
                    userInfo.setLastName(lastName.getText().toString().trim());
                    userInfo.setBirthDate(miladiDate);
                    RealmKuknos.updateIban(IBN.getText().toString().trim());
                    viewModel.sendUserInfo(userInfo);
                } else {
                    if (birthDate.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), getText(R.string.kuknos_edit_info_empty_birthdate), Toast.LENGTH_SHORT).show();
                    } else if (IBN.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), getText(R.string.kuknos_edit_info_empty_sheba), Toast.LENGTH_SHORT).show();
                    }
                    submit.setEnabled(true);
                    birthDate.setEnabled(true);
                    IBN.setEnabled(true);
                }
            }
        });
        onIbanInfo();
        onResponseState();
        onUserInfoObserver();
        return view;

    }

    private void onIbanInfo() {
        viewModel.getIbanInfo().observe(getViewLifecycleOwner(), infoResponse -> {
            progressBar.setVisibility(View.GONE);
            if (infoResponse != null) {
                ibanInfo = true;
                firstName.setText(infoResponse.getOwners().get(0).getFirst_name());
                lastName.setText(infoResponse.getOwners().get(0).getLast_name());
                ibanOldValue = iban;
                IBN.setText(iban);
            }
            if (userInfoR && ibanInfo) {
                birthDate.setEnabled(true);
                IBN.setEnabled(true);
                submit.setEnabled(true);
            }
        });
    }

    private void setMiladiDate(PersianCalendar persianCalendar) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy/MM/dd", Locale.ENGLISH);
        String birthdate = simpleDateFormat.format(new Date(persianCalendar.getTimeInMillis())).replace("/", "-").trim();
        Timestamp timestamp = new Timestamp(new Date(persianCalendar.getTimeInMillis()).getTime());
        miladiDate = timestamp.getTime();
    }

    private void onResponseState() {
        viewModel.getResponseState().observe(getViewLifecycleOwner(), state -> {
            progressBar.setVisibility(View.GONE);
            if (state.equals("true")) {
                Toast.makeText(getContext(), getText(R.string.kuknos_edit_info_saved_successfully), Toast.LENGTH_SHORT).show();
                if (iban != null && !iban.equals(IBN.getText().toString().trim())) {
                    viewModel.getIbanInfo(IBN.getText().toString().trim());
                }
                iban = IBN.getText().toString().trim();
                ibanOldValue = IBN.getText().toString().trim();
            } else {
                Toast.makeText(getContext(), state, Toast.LENGTH_SHORT).show();
            }
            if (ibanInfo && userInfoR) {
                submit.setText(getText(R.string.save));
                submit.setEnabled(true);
                birthDate.setEnabled(true);
                IBN.setEnabled(true);
            }
        });
    }


    private void onUserInfoObserver() {
        viewModel.getUserInfo().observe(getViewLifecycleOwner(), userInfo -> {
            progressBar.setVisibility(View.GONE);
            if (userInfo != null) {
                userInfoR = true;
                nationalId.setText(userInfo.getNationalCode());
                if (iban == null) {
                    firstName.setText(userInfo.getFirstName());
                    lastName.setText(userInfo.getLastName());
                    if (userInfo.getIban() != null) {
                        ibanOldValue = userInfo.getIban();
                        IBN.setText(userInfo.getIban());
                    }
                }
                if (userInfo.getBirthDate() != 0) {
                    String zeroNumber;
                    if (G.selectedLanguage.equals("en")) {
                        zeroNumber = "-0";
                    } else {
                        zeroNumber = "-" + HelperCalander.convertToUnicodeFarsiNumber("0");

                    }
                    String birthDateTime;
                    miladiDate = userInfo.getBirthDate();
                    String[] finalResult = G.selectedLanguage.equals("en") ? HelperCalander.getPersianCalander(userInfo.getBirthDate()).replace("/", "-").split("-") : HelperCalander.convertToUnicodeFarsiNumber(HelperCalander.getPersianCalander(userInfo.getBirthDate())).replace("/", "-").split("-");
                    if (Integer.valueOf(finalResult[1]) < 10 && Integer.valueOf(finalResult[2]) < 10) {
                        birthDateTime = finalResult[0] + zeroNumber + finalResult[1] + zeroNumber + finalResult[2];
                    } else {
                        if (Integer.valueOf(finalResult[1]) < 10) {
                            birthDateTime = finalResult[0] + zeroNumber + finalResult[1] + "-" + finalResult[2];
                        } else {
                            birthDateTime = finalResult[0] + "-" + finalResult[1] + zeroNumber + finalResult[2];
                        }
                    }
                    birthDate.setText(birthDateTime);
                    birthDateOldValue = miladiDate;
                }
                if (userInfoR && ibanInfo) {
                    birthDate.setEnabled(true);
                    IBN.setEnabled(true);
                    submit.setEnabled(true);
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