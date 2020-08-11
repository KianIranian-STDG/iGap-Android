package net.iGap.fragments;

import android.app.ActivityManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;

import java.io.File;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class TabletEmptyChatFragment extends Fragment {

    private AppCompatImageView imgBackGround;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tablet_empty_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgBackGround = view.findViewById(R.id.backgroundView);

        getChatBackground();

        view.findViewById(R.id.goToSetting).setOnClickListener(v -> {
            if (getActivity() instanceof ActivityMain) {
                ((ActivityMain) getActivity()).goToUserProfile();
            }
        });


    }

    public void getChatBackground() {
        String backGroundPath = getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE).getString(SHP_SETTING.KEY_PATH_CHAT_BACKGROUND, "");
        if (backGroundPath.length() > 0) {
            File f = new File(backGroundPath);
            if (f.exists()) {
                try {
                    Drawable d = Drawable.createFromPath(f.getAbsolutePath());
                    imgBackGround.setImageDrawable(d);
                } catch (OutOfMemoryError e) {
                    ActivityManager activityManager = (ActivityManager) getContext().getSystemService(ACTIVITY_SERVICE);
                    ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                    activityManager.getMemoryInfo(memoryInfo);
                }
            } else {
                try {
                    imgBackGround.setBackgroundColor(Color.parseColor(backGroundPath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (G.themeColor == Theme.DARK) {
                imgBackGround.setImageResource(R.drawable.chat_bg_dark);
            } else {
                //todo: fixed load default background in light mode
                /*Log.wtf(this.getClass().getName(), "else else: ");*/
            }
        }
    }
}
