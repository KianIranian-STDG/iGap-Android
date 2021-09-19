package net.iGap.story;

import android.graphics.PointF;

public class Vector2D extends PointF {

    void normalize() {
        float length = (float) Math.sqrt(x * x + y * y);
        x /= length;
        y /= length;
    }
}
