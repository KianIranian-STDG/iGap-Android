package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.Model.Parsian.KuknosUserInfoResponse;
import net.iGap.kuknos.Model.Parsian.Owners;
import net.iGap.kuknos.viewmodel.KuknosEditInfoVM;
import net.iGap.libs.persianDatePicker.Listener;
import net.iGap.libs.persianDatePicker.PersianDatePickerDialog;
import net.iGap.libs.persianDatePicker.util.PersianCalendar;
import net.iGap.module.AndroidUtils;
import net.iGap.module.accountManager.DbManager;
import net.iGap.realm.RealmKuknos;

import java.security.acl.Owner;
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
    private Owners ibanInfoResponse;
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
        IBN.setText("IR");
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
        IBN.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && IBN.getText().length() == 2) {
                IBN.setSelection(IBN.getText().length());
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                closeKeyboard(v);
            }
            return false;
        });

        IBN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1 && s.toString().equalsIgnoreCase("i") || s.toString().equals("r")) {
                    IBN.setText("IR");
                    IBN.setSelection(IBN.getText().length());
                    return;
                } else if (s.toString().length() != 0 && s.toString().length() < 26) {
                    if (IBN.getText().toString().charAt(0) != 'I' || IBN.getText().toString().charAt(1) != 'R')
                        IBN.setText("IR" + IBN.getText().toString());
                } else if (s.toString().length() == 0) {
                    IBN.setText("IR");
                    IBN.setSelection(IBN.getText().length());
                }
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
                } else if (!IBN.getText().toString().isEmpty() && !birthDate.getText().toString().isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    submit.setText(getText(R.string.kuknos_edit_info_sending_info));
                    viewModel.getIbanInfo(IBN.getText().toString().trim());
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
                ibanInfoResponse = infoResponse;
                ibanOldValue = iban;
                if (firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty()) {
                    firstName.setText(ibanInfoResponse.getOwners().get(0).getFirst_name());
                    lastName.setText(ibanInfoResponse.getOwners().get(0).getLast_name());
                    IBN.setText(IBN.getText().toString().concat(iban.substring(2)));
                } else {
                    userInfo.setIban(IBN.getText().toString().trim());
                    userInfo.setFirstName(firstName.getText().toString().trim());
                    userInfo.setLastName(lastName.getText().toString().trim());
                    userInfo.setBirthDate(miladiDate);
                    RealmKuknos.updateIban(IBN.getText().toString().trim());
                    viewModel.sendUserInfo(userInfo);
                }
            } else {
                if (firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), getString(R.string.kuknos_editinfo_error_getinfo), Toast.LENGTH_SHORT).show();
                }
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
        Timestamp timestamp = new Timestamp(new Date(persianCalendar.getTimeInMillis()).getTime());
        miladiDate = timestamp.getTime();
    }

    private void onResponseState() {
        viewModel.getResponseState().observe(getViewLifecycleOwner(), state -> {
            progressBar.setVisibility(View.GONE);
            if (state.equals("true")) {
                Toast.makeText(getContext(), getText(R.string.kuknos_edit_info_saved_successfully), Toast.LENGTH_SHORT).show();
                if (ibanInfo) {
                    firstName.setText(ibanInfoResponse.getOwners().get(0).getFirst_name());
                    lastName.setText(ibanInfoResponse.getOwners().get(0).getLast_name());
                }
                iban = IBN.getText().toString().trim();
                ibanOldValue = IBN.getText().toString().trim();
            } else {
                if (state.equals("onFailed")) {
                    Toast.makeText(getContext(), getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                } else if (!firstName.getText().toString().isEmpty() || !lastName.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), state, Toast.LENGTH_SHORT).show();
                }
            }
            submit.setText(getText(R.string.save));
            if (ibanInfo && userInfoR) {
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