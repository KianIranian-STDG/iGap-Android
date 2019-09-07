package net.iGap.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;

public class FragmentCallAction extends BottomSheetDialogFragment {
    private View rootView;
    private String phoneNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_call_action, container);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        View callAction = rootView.findViewById(R.id.ll_callAction_call);
        callAction.setOnClickListener(v -> {
            if (phoneNumber != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                getContext().startActivity(intent);
                dismiss();
            }
        });

        View messageAction = rootView.findViewById(R.id.ll_callAction_textMessage);
        messageAction.setOnClickListener(v -> {
            if (phoneNumber != null) {
                dismiss();
                Uri uri = Uri.parse("smsto:"+phoneNumber);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.putExtra("sms_body", "");
                startActivity(intent);
            }
        });

        TextView call = rootView.findViewById(R.id.tv_callAction_call);
        TextView message = rootView.findViewById(R.id.tv_callAction_message);

        if (G.isDarkTheme) {
            call.setTextColor(getResources().getColor(R.color.white));
            message.setTextColor(getResources().getColor(R.color.white));
        } else {
            call.setTextColor(getResources().getColor(R.color.black));
            message.setTextColor(getResources().getColor(R.color.black));
        }
    }


    @Override
    public int getTheme() {
        if (G.isDarkTheme) {
            return R.style.BaseBottomSheetDialog;
        } else {
            return R.style.BaseBottomSheetDialogLight;
        }
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
