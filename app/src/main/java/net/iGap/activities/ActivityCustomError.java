package net.iGap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import net.iGap.R;
import net.iGap.messenger.theme.Theme;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

public class ActivityCustomError extends ActivityEnhanced {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_error);

        AppCompatTextView title = (AppCompatTextView) findViewById(R.id.title);
        title.setTextColor(Theme.getColor(Theme.key_title_text));

        AppCompatTextView description = (AppCompatTextView) findViewById(R.id.description);
        description.setTextColor(Theme.getColor(Theme.key_title_text));

        Button restartButton = (Button) findViewById(R.id.restart_button);
        AppCompatImageView image = (AppCompatImageView) findViewById(R.id.image);
        image.setBackgroundResource(R.drawable.ic_crash);

        final Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomActivityOnCrash.restartApplicationWithIntent(ActivityCustomError.this, i, config);
            }
        });
    }
}
