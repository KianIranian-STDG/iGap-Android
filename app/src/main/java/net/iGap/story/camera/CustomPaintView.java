package net.iGap.story.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.R;
import net.iGap.libs.photoEdit.BrushDrawingView;
import net.iGap.libs.photoEdit.FilterImageView;
import net.iGap.libs.photoEdit.OnSaveBitmap;

public class CustomPaintView extends RelativeLayout {
    private FilterImageView mImgSource;
    private BrushDrawingView mBrushDrawingView;
    private static final int imgSrcId = 1, brushSrcId = 2;


    public CustomPaintView(Context context) {
        super(context);
        init(null);
    }

    public CustomPaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomPaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @SuppressLint("Recycle")
    private void init(@Nullable AttributeSet attrs) {
        //Setup image attributes
        mImgSource = new FilterImageView(getContext());
        mImgSource.setId(imgSrcId);
        mImgSource.setAdjustViewBounds(true);
        RelativeLayout.LayoutParams imgSrcParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgSrcParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PhotoEditorView);
            Drawable imgSrcDrawable = a.getDrawable(R.styleable.PhotoEditorView_photo_src);
            if (imgSrcDrawable != null) {
                mImgSource.setImageDrawable(imgSrcDrawable);
            }
        }

        //Setup brush view
        mBrushDrawingView = new BrushDrawingView(getContext());
//        mBrushDrawingView.setVisibility(GONE);
        mBrushDrawingView.setId(brushSrcId);
        //Align brush to the size of image view
        RelativeLayout.LayoutParams brushParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        brushParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        brushParam.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
        brushParam.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);

        //Align brush to the size of image view
        RelativeLayout.LayoutParams imgFilterParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgFilterParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        imgFilterParam.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
        imgFilterParam.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);

        //Add image source
        addView(mImgSource, imgSrcParam);

        //Add brush view
        addView(mBrushDrawingView, brushParam);

    }


    void saveFilter(@NonNull final OnSaveBitmap onSaveBitmap) {
        onSaveBitmap.onBitmapReady(mImgSource.getBitmap());
    }

    /**
     * Source image which you want to edit
     *
     * @return source ImageView
     */
    public ImageView getSource() {
        return mImgSource;
    }

    public void setBrushSize(float size) {
        if (mBrushDrawingView != null)
            mBrushDrawingView.setBrushSize(size);
    }

    /**
     * set brush color which user want to paint
     *
     * @param color color value for paint
     */
    public void setBrushColor(@ColorInt int color) {
        if (mBrushDrawingView != null)
            mBrushDrawingView.setBrushColor(color);
    }

    public void setStrokeAlpha(float alpha) {
        if (mBrushDrawingView != null)
            mBrushDrawingView.setOpacity((int) alpha);
    }

    BrushDrawingView getBrushDrawingView() {
        return mBrushDrawingView;
    }
}