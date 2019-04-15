package net.iGap.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.iGap.R;

public class ReagentFragment extends BaseFragment {

    private View view;
    private TextView titleTv;
    private TextView detailTv;
    private EditText phoneNumberEt;
    private Button letsGoBtn;
    private Button skipBtn;
    private EditText countryCode;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null)
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_reagent, container, false);
        setUoViews();
        return view;
    }

    private void setUoViews() {
        titleTv = view.findViewById(R.id.tv_reagent_title);
        detailTv = view.findViewById(R.id.tv_reagent_detail);
        phoneNumberEt = view.findViewById(R.id.et_reagent_phoneNumber);
        letsGoBtn = view.findViewById(R.id.btn_reagent_start);
        skipBtn = view.findViewById(R.id.btn_reagent_skip);
        countryCode = view.findViewById(R.id.et_reagent_countryCode);

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    private void finalAction() {
//        G.onUserInfoResponse = null;
//        Intent intent = new Intent(context, ActivityMain.class);
//        intent.putExtra(ARG_USER_ID, user.getId());
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        G.context.startActivity(intent);
//        G.fragmentActivity.finish();
    }

}
