package net.iGap.helper;

import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import net.iGap.controllers.ImageLoader;

public class ImageObserver {
    private View parentView;

    private Drawable currentImageDrawable;
    private BitmapShader imageShader;
    private String tag;
    private Object parentObject;
    private String imageKey;

    private Drawable currentThumbDrawable;
    private BitmapShader ThumbShader;
    private String thumbTag;
    private String thumbKey;

    private Paint roundPaint;
    private RectF roundRect = new RectF();
    private RectF bitmapRect = new RectF();
    private Matrix shaderMatrix = new Matrix();
    private Path roundPath = new Path();

    private float imageX, imageY, imageW, imageH;

    public ImageObserver(View parentView) {
        this.parentView = parentView;
    }

    public void setParentView(View parentView) {
        this.parentView = parentView;
    }

    public void setImage() {
        ImageLoader.getInstance().loadImageForImageObserver(this);
    }

    public void setDrawable(Drawable drawable) {
        ImageLoader.getInstance().cancelLoadImageForImageObserver(this);
    }

    public void onDraw(Canvas canvas, float x, float y, float width, float height) {
        imageX = x;
        imageY = y;
        imageW = width;
        imageH = height;

    }

    public void onAttachedToWindow() {

    }

    public void onDetachedFromWindow() {

    }

}
