package net.iGap.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import net.iGap.G;
import net.iGap.R;

public class ActivityCustomError extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_error);

        //TextView errorDetailsText = (TextView) findViewById(R.id.error_details);
        //errorDetailsText.setText(CustomActivityOnCrash.getStackTraceFromIntent(getIntent()));

        Button restartButton = (Button) findViewById(R.id.restart_button);
        restartButton.setBackgroundColor(Color.parseColor(G.appBarColor));

        //
        final CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());

        if (config.isShowRestartButton() && config.getRestartActivityClass() != null) {
            restartButton.setText(R.string.st_title_reset);
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomActivityOnCrash.restartApplication(ActivityCustomError.this, config);
                }
            });
        } else {
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomActivityOnCrash.closeApplication(ActivityCustomError.this, config);
                }
            });
        }
    }
}
