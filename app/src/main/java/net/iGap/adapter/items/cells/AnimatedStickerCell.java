package net.iGap.adapter.items.cells;

import android.content.Context;
import android.util.AttributeSet;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import net.iGap.G;
import net.iGap.module.structs.StructMessageInfo;
import net.iGap.observers.eventbus.EventListener;
import net.iGap.observers.eventbus.EventManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AnimatedStickerCell extends LottieAnimationView implements EventListener {

    public boolean animatedLoaded;
    private String path;
    private boolean detached = false;
    private boolean playing;

    public boolean isPlaying() {
        return playing;
    }

    public AnimatedStickerCell(Context context) {
        super(context);
        init(context);
    }

    public AnimatedStickerCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setRepeatCount(LottieDrawable.INFINITE);
        setRepeatMode(LottieDrawable.RESTART);
    }

    public void setMessage(StructMessageInfo message) {
        if (message == null)
            return;

        if (message.getAttachment() != null && message.getAttachment().isFileExistsOnLocal()) {
            path = message.getAttachment().getLocalFilePath();
            loadAnimation(path);
        }

    }

    public void playAnimation(String path) {
        if (path != null) {
            this.path = path;
            loadAnimation(path);
        }
    }

    private void loadAnimation(String path) {
        if (path == null || path.isEmpty())
            return;

        try {
            InputStream inputStream = new FileInputStream(path);
            String loadingCashId = (String) getTag();
            setAnimation(inputStream, loadingCashId);
            animatedLoaded = true;
            playAnimation();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            animatedLoaded = false;
        } catch (ClassCastException e) {
            e.printStackTrace();
            animatedLoaded = false;
        }
    }

    @Override
    public void playAnimation() {
        super.playAnimation();
        playing = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        detached = true;
        EventManager.getInstance().removeEventListener(EventManager.STICKER_DOWNLOAD, this);
//        try {
//            if (inputStream != null)
//                inputStream.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            animatedLoaded = false;
//            playing = false;
//        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (detached)
            if (animatedLoaded)
                playAnimation();
            else if (path != null) {
                loadAnimation(path);
            }
        detached = false;

        EventManager.getInstance().addEventListener(EventManager.STICKER_DOWNLOAD, this);
    }

    @Override
    public void receivedMessage(int id, Object... message) {
        if (id == EventManager.STICKER_DOWNLOAD) {
            String filePath = (String) message[0];
            String fileToken = (String) message[1];

            if (getTag().equals(fileToken)) {
                G.handler.post(() -> {
                    playAnimation(filePath);
                });
            }
        }
    }
}
