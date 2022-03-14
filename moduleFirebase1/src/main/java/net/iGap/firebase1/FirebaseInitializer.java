package net.iGap.firebase1;

import android.content.Context;

import com.google.firebase.FirebaseApp;

public class FirebaseInitializer {

    public static void initialize(Context context) {
        FirebaseApp.initializeApp(context);
    }
}
