package net.iGap.helper;

import android.os.Handler;
import android.view.View;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class HelperAnimation {

    public static void bigAndSmall(View view, float upScalePercent, float downScalePercent){
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", upScalePercent);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", upScalePercent);
        scaleUpX.setDuration(100);
        scaleUpY.setDuration(100);

        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.play(scaleUpX).with(scaleUpY);

        scaleUp.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", downScalePercent);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", downScalePercent);
                scaleDownX.setDuration(100);
                scaleDownY.setDuration(100);

                AnimatorSet scaleDown = new AnimatorSet();
                scaleDown.play(scaleDownX).with(scaleDownY);

                scaleDown.start();
            }
        }, 150);
    }
}
