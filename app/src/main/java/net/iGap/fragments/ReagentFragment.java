package net.iGap.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vicmikhailau.maskededittext.MaskedEditText;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.AdapterDialog;
import net.iGap.interfaces.OnCountryCode;
import net.iGap.interfaces.OnInfoCountryResponse;
import net.iGap.module.CountryListComparator;
import net.iGap.module.CountryReader;
import net.iGap.module.SoftKeyboard;
import net.iGap.module.structs.StructCountry;
import net.iGap.request.RequestInfoCountry;

import java.util.ArrayList;
import java.util.Collections;

import static net.iGap.fragments.FragmentRegister.btnOk;
import static net.iGap.fragments.FragmentRegister.positionRadioButton;
import static net.iGap.viewmodel.FragmentRegisterViewModel.dialogChooseCountry;
import static net.iGap.viewmodel.FragmentRegisterViewModel.isoCode;

public class ReagentFragment extends BaseFragment implements OnCountryCode {

    private static final String ARG_USER_ID = "arg_user_id";
    public String regex;
    public long userId;
    private EditText countryCodeEt;
    private View view;
    private TextView titleTv;
    private TextView detailTv;
    private MaskedEditText phoneNumberEt;
    private Button letsGoBtn;
    private Button skipBtn;
    private Button selectCountryBtn;
    private SearchView edtSearchView;
    private AdapterDialog adapterDialog;
    private ArrayList<StructCountry> items = new ArrayList<>();
    private ArrayList<StructCountry> structCountryArrayList = new ArrayList();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_reagent, container, false);
        setUoViews();
        G.onCountryCode = this;
        return view;
    }

    private void setUoViews() {
        titleTv = view.findViewById(R.id.tv_reagent_title);
        detailTv = view.findViewById(R.id.tv_reagent_detail);
        phoneNumberEt = view.findViewById(R.id.et_reagent_phoneNumber);
        letsGoBtn = view.findViewById(R.id.btn_reagent_start);
        skipBtn = view.findViewById(R.id.btn_reagent_skip);
        countryCodeEt = view.findViewById(R.id.et_reagent_countryCode);
        selectCountryBtn = view.findViewById(R.id.btn_reagent_selectCountry);
    }

    @Override
    public void onStart() {
        super.onStart();
        addCountryList();
        selectCountryBtn.setOnClickListener(v -> selectCountry());

        phoneNumberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().startsWith("0")) {
                    phoneNumberEt.setText("");
                    Toast.makeText(G.fragmentActivity, G.fragmentActivity.getResources().getString(R.string.Toast_First_0), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (phoneNumberEt.getText().toString().isEmpty()) {
            letsGoBtn.setEnabled(true);
            letsGoBtn.setOnClickListener(v -> {
                finalAction();
            });

            skipBtn.setOnClickListener(v -> finalAction());
        }



    }

    private void selectCountry() {

        dialogChooseCountry = new Dialog(G.fragmentActivity);
        dialogChooseCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChooseCountry.setContentView(R.layout.rg_dialog);

        int setWidth = (int) (G.context.getResources().getDisplayMetrics().widthPixels * 0.9);
        int setHeight = (int) (G.context.getResources().getDisplayMetrics().heightPixels * 0.9);
        dialogChooseCountry.getWindow().setLayout(setWidth, setHeight);
        //
        final TextView txtTitle = dialogChooseCountry.findViewById(R.id.rg_txt_titleToolbar);
        edtSearchView = dialogChooseCountry.findViewById(R.id.rg_edtSearch_toolbar);

        txtTitle.setOnClickListener(view -> {

            edtSearchView.setIconified(false);
            edtSearchView.setIconifiedByDefault(true);
            txtTitle.setVisibility(View.GONE);
        });

        // close SearchView and show title again
        edtSearchView.setOnCloseListener(() -> {

            txtTitle.setVisibility(View.VISIBLE);

            return false;
        });

        final ViewGroup root = dialogChooseCountry.findViewById(android.R.id.content);
        InputMethodManager im = (InputMethodManager) G.context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        SoftKeyboard softKeyboard = new SoftKeyboard(root, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                G.handler.post(() -> {
                    if (edtSearchView.getQuery().toString().length() > 0) {
                        edtSearchView.setIconified(false);
                        edtSearchView.clearFocus();
                        txtTitle.setVisibility(View.GONE);
                    } else {
                        edtSearchView.setIconified(true);
                        txtTitle.setVisibility(View.VISIBLE);
                    }
                    adapterDialog.notifyDataSetChanged();
                });
            }

            @Override
            public void onSoftKeyboardShow() {

                G.handler.post(new Runnable() {

                    @Override
                    public void run() {

                        txtTitle.setVisibility(View.GONE);
                    }
                });
            }
        });

        final ListView listView = dialogChooseCountry.findViewById(R.id.lstContent);
        adapterDialog = new AdapterDialog(G.fragmentActivity, items);
        listView.setAdapter(adapterDialog);

        final View border = dialogChooseCountry.findViewById(R.id.rg_borderButton);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                if (i > 0) {
                    border.setVisibility(View.VISIBLE);
                } else {
                    border.setVisibility(View.GONE);
                }
            }
        });

        AdapterDialog.mSelectedVariation = positionRadioButton;

        adapterDialog.notifyDataSetChanged();

        edtSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                adapterDialog.getFilter().filter(s);
                return false;
            }
        });

        btnOk = dialogChooseCountry.findViewById(R.id.rg_txt_okDialog);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                G.onInfoCountryResponse = new OnInfoCountryResponse() {
                    @Override
                    public void onInfoCountryResponse(final int callingCode, final String name, final String pattern, final String regexR) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (pattern.equals("")) {
                                    phoneNumberEt.setMask("##################");
                                } else {
                                    phoneNumberEt.setMask(pattern.replace("X", "#").replace(" ", "-"));

                                }                            }
                        });
                    }

                    @Override
                    public void onError(int majorCode, int minorCode) {
                        //empty
                    }
                };

                new RequestInfoCountry().infoCountry(isoCode);

//                callBackEdtPhoneNumber.set("");
                dialogChooseCountry.dismiss();
            }
        });

        if (!(G.fragmentActivity).isFinishing()) {
            dialogChooseCountry.show();
        }
    }

    private void addCountryList() {
        CountryReader countryReade = new CountryReader();
        StringBuilder fileListBuilder = countryReade.readFromAssetsTextFile("country.txt", G.context);

        String list = fileListBuilder.toString();
        // Split line by line Into array
        String listArray[] = list.split("\\r?\\n");
        final String countryNameList[] = new String[listArray.length];
        //Convert array
        for (int i = 0; listArray.length > i; i++) {
            StructCountry structCountry = new StructCountry();

            String listItem[] = listArray[i].split(";");
            structCountry.setCountryCode(listItem[0]);
            structCountry.setAbbreviation(listItem[1]);
            structCountry.setName(listItem[2]);

            if (listItem.length > 3) {
                structCountry.setPhonePattern(listItem[3]);
            } else {
                structCountry.setPhonePattern(" ");
            }

            structCountryArrayList.add(structCountry);
        }

        Collections.sort(structCountryArrayList, new CountryListComparator());

        for (int i = 0; i < structCountryArrayList.size(); i++) {
            if (i < countryNameList.length) {
                countryNameList[i] = structCountryArrayList.get(i).getName();
                StructCountry item = new StructCountry();
                item.setId(i);
                item.setName(structCountryArrayList.get(i).getName());
                item.setCountryCode(structCountryArrayList.get(i).getCountryCode());
                item.setPhonePattern(structCountryArrayList.get(i).getPhonePattern());
                item.setAbbreviation(structCountryArrayList.get(i).getAbbreviation());
                items.add(item);
            }
        }
    }

    private void finalAction() {
        G.onUserInfoResponse = null;
        Intent intent = new Intent(getContext(), ActivityMain.class);
        intent.putExtra(ARG_USER_ID, userId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        G.context.startActivity(intent);
        G.fragmentActivity.finish();
    }


    @Override
    public void countryInfo(String countryName, String code, boolean performClick) {
        selectCountryBtn.setText(countryName);
        countryCodeEt.setText("+ " + String.valueOf(code));

        if (performClick) {
            dialogChooseCountry.dismiss();
            btnOk.performClick();
        }

    }

}
