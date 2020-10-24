package net.iGap.kuknos.Fragment;

import android.os.Bundle;
import android.util.Log;
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
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.JalaliCalendar;
import net.iGap.kuknos.Model.Parsian.KuknosUserInfoResponse;
import net.iGap.kuknos.viewmodel.KuknosEditInfoVM;
import net.iGap.libs.persianDatePicker.Listener;
import net.iGap.libs.persianDatePicker.PersianDatePickerDialog;
import net.iGap.libs.persianDatePicker.util.PersianCalendar;
import net.iGap.module.accountManager.DbManager;
import net.iGap.realm.RealmKuknos;

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
    private String miladiDate;
    private String ibanOldValue;
    private String birthDateOldValue;

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

        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        datePickerDialog = new PersianDatePickerDialog(getActivity())
                .setPositiveButtonString("حله")
                .setNegativeButton("بیخیال")
                .setMaxYear(1399)
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
                if (ibanOldValue.equals(IBN.getText().toString().trim()) && birthDateOldValue.equals(miladiDate)) {
                    Toast.makeText(getContext(), "فیلد تاریخ تولد و شبا تغییری نکرده اند!", Toast.LENGTH_SHORT).show();
                    submit.setEnabled(true);
                    birthDate.setEnabled(true);
                    IBN.setEnabled(true);
                } else if (!IBN.getText().toString().isEmpty() && !birthDate.getText().toString().isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    submit.setText("در حال ارسال اطلاعات...");
                    userInfo.setIban(IBN.getText().toString().trim());
                    userInfo.setFirstName(firstName.getText().toString().trim());
                    userInfo.setLastName(lastName.getText().toString().trim());
                    userInfo.setBirthDate(miladiDate.trim());
                    RealmKuknos.updateIban(IBN.getText().toString().trim());
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
        onIbanInfo();
        onResponseState();
        onUserInfoObserver();
        return view;

    }

    private void onIbanInfo() {
        viewModel.getIbanInfo().observe(getViewLifecycleOwner(), infoResponse -> {
            if (infoResponse != null) {
                firstName.setText(infoResponse.getOwners().get(0).getFirst_name());
                lastName.setText(infoResponse.getOwners().get(0).getLast_name());
                ibanOldValue = iban;
                IBN.setText(iban);
            }
            birthDate.setEnabled(true);
            IBN.setEnabled(true);
            submit.setEnabled(true);
        });
    }

    private void setMiladiDate(PersianCalendar persianCalendar) {

//        miladiDate = َHelperCalander.checkHijriAndReturnTime(persianCalendar.getTimeInMillis());
        Log.e("cvjkabijghsad", "" +miladiDate);


//        String[] finalResult = miladiDate.split("-");
//        if (Integer.valueOf(finalResult[1]) < 10) {
//            miladiDate = finalResult[0] + "-0" + finalResult[1] + "-" + finalResult[2];
//        }

    }

    private void onResponseState() {
        viewModel.getResponseState().observe(getViewLifecycleOwner(), state -> {
            if (state.equals("true")) {
                Toast.makeText(getContext(), "اطلاعات با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show();
                if (!iban.equals(IBN.getText().toString().trim())) {
                    iban = IBN.getText().toString().trim();
                    viewModel.getIbanInfo(IBN.getText().toString().trim());
                }
            } else {
                Toast.makeText(getContext(), state, Toast.LENGTH_SHORT).show();
            }
            submit.setText("دخیره");
            progressBar.setVisibility(View.GONE);
            submit.setEnabled(true);
            birthDate.setEnabled(true);
            IBN.setEnabled(true);
        });
    }


    private void onUserInfoObserver() {
        viewModel.getUserInfo().observe(getViewLifecycleOwner(), userInfo -> {
            if (userInfo != null) {
                nationalId.setText(userInfo.getNationalCode());
                if (iban == null) {
                    firstName.setText(userInfo.getFirstName());
                    lastName.setText(userInfo.getLastName());
                    if (userInfo.getIban() != null) {
                        ibanOldValue = userInfo.getIban();
                        IBN.setText(userInfo.getIban());
                    }
                }

                if (userInfo.getBirthDate() != null) {
                    Log.e("cvjkabijghsad", " "+userInfo.getBirthDate());
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy/MM/dd", Locale.ENGLISH);
//                    String birthdate = simpleDateFormat.format(new Date(Long.valueOf(userInfo.getBirthDate()))).replace("/", "-");
//                    Log.e("cvjkabijghsad", "" + birthdate);
//                    String day = "";
//                    String month = "";
//                    String year = "";
//                    int counter = 0;
//                    char[] date = birthdate.toCharArray();
//                    for (int i = 0; i < birthdate.length(); i++) {
//                        if (date[i] != '-') {
//                            if (counter == 0) {
//                                year += String.valueOf(date[i]);
//                            } else if (counter == 1) {
//                                month += String.valueOf(date[i]);
//                            } else {
//                                day += String.valueOf(date[i]);
//                            }
//                        } else {
//                            counter++;
//                        }
//                    }
//                    miladiDate = JalaliCalendar.gregorianToJalali(new JalaliCalendar.YearMonthDate(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day))).toString().replace("/", "-");
//                    Log.e("cvjkabijghsad", "" + miladiDate);
//                    String[] finalResult = miladiDate.split("-");
//                    if (Integer.valueOf(finalResult[1]) < 10 && Integer.valueOf(finalResult[2]) < 10) {
//                        miladiDate = finalResult[0] + "-0" + finalResult[1] + "-0" + finalResult[2];
//                    }else {
//                        if (Integer.valueOf(finalResult[1]) < 10){
//                            miladiDate = finalResult[0] + "-0" + finalResult[1] + "-" + finalResult[2];
//                        }else {
//                            miladiDate = finalResult[0] + "-" + finalResult[1] + "-0" + finalResult[2];
//                        }
//                    }
//                    Log.e("cvjkabijghsad", "" + miladiDate);
                    birthDate.setText(HelperCalander.getPersianCalander(Long.valueOf(userInfo.getBirthDate())));
                    birthDateOldValue = miladiDate;
                }
            }
            birthDate.setEnabled(true);
            IBN.setEnabled(true);
            submit.setEnabled(true);
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