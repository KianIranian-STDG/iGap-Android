package net.iGap.adapter.items.cells;

import android.content.Context;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import net.iGap.module.structs.StructMessageInfo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AnimatedStickerCell extends LottieAnimationView {
    private String TAG = "abbasiLottei";

    private InputStream inputStream;
    public boolean animatedLoaded;
    private String path;
    private boolean detached = false;

    public AnimatedStickerCell(Context context) {
        super(context);
        setRepeatMode(LottieDrawable.REVERSE);
    }

    public void setMessage(StructMessageInfo message) {
        if (message == null)
            return;

        if (message.getAttachment() != null && message.getAttachment().isFileExistsOnLocal()) {
            path = message.getAttachment().getLocalFilePath();
            loadAnimation(path);
        }

    }


    private void loadAnimation(String path) {
        if (path == null || path.isEmpty())
            return;

        try {
            inputStream = new BufferedInputStream(new FileInputStream(path));
            setAnimation(inputStream, null);
            animatedLoaded = true;

            playAnimation();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            animatedLoaded = false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        detached = true;
        try {
            if (inputStream != null)
                inputStream.close();
            animatedLoaded = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (detached)
            if (animatedLoaded)
                playAnimation();
            else if (path != null) {
                loadAnimation(path);
                playAnimation();
            }
        detached = false;
    }
}
