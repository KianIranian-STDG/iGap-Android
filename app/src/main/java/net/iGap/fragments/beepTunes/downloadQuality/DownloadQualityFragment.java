package net.iGap.fragments.beepTunes.downloadQuality;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.SHP_SETTING;

import static android.content.Context.MODE_PRIVATE;

public class DownloadQualityFragment extends DialogFragment {
    private RadioGroup radioGroup;
    private TextView confirmTv;
    private TextView cancelTv;
    private CheckBox checkBox;

    private SharedPreferences sharedPreferences;
    private OnDownloadDialog downloadDialog;

    private int quality;

    public void setDownloadDialog(OnDownloadDialog downloadDialog) {
        this.downloadDialog = downloadDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_beeptunes_download, null);
        builder.setView(rootView);

        radioGroup = rootView.findViewById(R.id.rg_downloadQuality);
        confirmTv = rootView.findViewById(R.id.tv_downloadQuality_confirm);
        cancelTv = rootView.findViewById(R.id.tv_downloadQuality_cancel);
        checkBox = rootView.findViewById(R.id.cb_downloadQuality_rememberSetting);
        sharedPreferences = G.currentActivity.getSharedPreferences(SHP_SETTING.KEY_BEEP_TUNES, MODE_PRIVATE);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_downloadQuality_128) {
                quality = 128;
            } else
                quality = 320;

            confirmTv.setTextColor(getContext().getResources().getColor(R.color.green));
            checkBox.setTextColor(getContext().getResources().getColor(R.color.black));
            checkBox.setEnabled(true);
        });

        confirmTv.setOnClickListener(v -> {
            if (quality != 0) {
                downloadDialog.downloadQuality(quality);

                if (checkBox.isChecked()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(SHP_SETTING.KEY_BBEP_TUNES_DOWNLOAD, true);
                    editor.putInt(SHP_SETTING.KEY_BBEP_TUNES_DOWNLOAD_QUALITY, quality);
                    editor.apply();
                }

                dismiss();
            } else
                confirmTv.setEnabled(false);
        });

        cancelTv.setOnClickListener(v -> dismiss());
    }

    public interface OnDownloadDialog {
        void downloadQuality(int quality);
    }
}
