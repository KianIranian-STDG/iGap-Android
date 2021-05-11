package net.iGap.camera;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.libs.photoEdit.BrushDrawingView;
import net.iGap.libs.photoEdit.FilterImageView;
import net.iGap.module.AndroidUtils;

import java.io.File;

public class TextStickerView extends RelativeLayout {
    private FilterImageView bitmapHolderImageView;
    private BrushDrawingView mBrushDrawingView;
    private ImageFilterView mImageFilterView;
    private static final int imgSrcId = 1, brushSrcId = 2, glFilterId = 3;
    ;

    public TextStickerView(Context context, boolean isPaintMode) {
        super(context);
//        if (isPaintMode)
        init(null);
    }

    public TextStickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TextStickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        //Setup image attributes
        if (bitmapHolderImageView == null) {
            bitmapHolderImageView = new FilterImageView(getContext());
            RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageViewParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            bitmapHolderImageView.setLayoutParams(imageViewParams);
            bitmapHolderImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            bitmapHolderImageView.setAdjustViewBounds(true);

            bitmapHolderImageView.setDrawingCacheEnabled(true);
            addView(bitmapHolderImageView, imageViewParams);
        }
//        bitmapHolderImageView.setId(imgSrcId);
//        bitmapHolderImageView.setAdjustViewBounds(true);
//        bitmapHolderImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        bitmapHolderImageView.setDrawingCacheEnabled(true);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PhotoEditorView);
            Drawable imgSrcDrawable = a.getDrawable(R.styleable.PhotoEditorView_photo_src);
            if (imgSrcDrawable != null) {
                bitmapHolderImageView.setImageDrawable(imgSrcDrawable);
            }
        }

        //Setup brush view
        if (mBrushDrawingView == null) {
            mBrushDrawingView = new BrushDrawingView(getContext());
//        mBrushDrawingView.setVisibility(GONE);
            mBrushDrawingView.setId(brushSrcId);
            //Align brush to the size of image view
            RelativeLayout.LayoutParams brushParam = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            brushParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            brushParam.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
            brushParam.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);

            //Setup GLSurface attributes
            mImageFilterView = new ImageFilterView(getContext());
            mImageFilterView.setId(glFilterId);
            mImageFilterView.setVisibility(GONE);


            //Align brush to the size of image view
            RelativeLayout.LayoutParams imgFilterParam = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imgFilterParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            imgFilterParam.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
            imgFilterParam.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);

            addView(mImageFilterView, imgFilterParam);
            //Add brush view
            addView(mBrushDrawingView, brushParam);
        }
    }

    public ImageView getBitmapHolderImageView() {
        return bitmapHolderImageView;
    }

    public void updateImageBitmap(Bitmap bitmap) {
        if (bitmapHolderImageView != null) {
            removeView(bitmapHolderImageView);
        }

        bitmapHolderImageView = new FilterImageView(getContext());

        //Setup image attributes
        RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageViewParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        bitmapHolderImageView.setLayoutParams(imageViewParams);
        bitmapHolderImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        bitmapHolderImageView.setAdjustViewBounds(true);

        bitmapHolderImageView.setDrawingCacheEnabled(true);
        Glide.with(getContext()).asDrawable().load(bitmap).into(bitmapHolderImageView);

//        bitmapHolderImageView.setImageBitmap(bitmap);
        addView(bitmapHolderImageView, 0);
    }

    public void updateImageBitmap(String finalPath) {
        if (bitmapHolderImageView != null) {
            removeView(bitmapHolderImageView);
        }

        bitmapHolderImageView = new FilterImageView(getContext());

        //Setup image attributes
        RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageViewParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        bitmapHolderImageView.setLayoutParams(imageViewParams);
        bitmapHolderImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        bitmapHolderImageView.setAdjustViewBounds(true);

        bitmapHolderImageView.setDrawingCacheEnabled(true);
        G.imageLoader.displayImage(AndroidUtils.suitablePath(finalPath), bitmapHolderImageView);

//        bitmapHolderImageView.setImageBitmap(bitmap);
        addView(bitmapHolderImageView, 0);
    }

    void saveFilter(@NonNull final OnSaveBitmap onSaveBitmap) {
        if (mImageFilterView.getVisibility() == VISIBLE) {
            mImageFilterView.saveBitmap(new OnSaveBitmap() {
                @Override
                public void onBitmapReady(final Bitmap saveBitmap) {
                    bitmapHolderImageView.setImageBitmap(saveBitmap);
                    mImageFilterView.setVisibility(GONE);
                    onSaveBitmap.onBitmapReady(saveBitmap);
                }

                @Override
                public void onFailure(Exception e) {
                    onSaveBitmap.onFailure(e);
                }
            });
        } else {
            onSaveBitmap.onBitmapReady(bitmapHolderImageView.getBitmap());
        }


    }

    public void setPaintMode(boolean paintMode, Bitmap bitmap) {
        if (paintMode) {
            if (mBrushDrawingView != null) {
                mBrushDrawingView.setVisibility(VISIBLE);
                mBrushDrawingView.setBrushDrawingMode(true);
            }
            if (bitmap != null) {
                Glide.with(getContext()).asDrawable().load(bitmap).centerCrop().into(bitmapHolderImageView);
            }
        } else {
            if (bitmap != null) {

                Glide.with(getContext()).asDrawable().load(bitmap).centerCrop().into(bitmapHolderImageView);
            }
            if (mBrushDrawingView != null) {
                mBrushDrawingView.setBrushDrawingMode(false);
            }

        }
    }

    public void setPaintMode(boolean paintMode, String finalPath) {
        if (paintMode) {
            if (mBrushDrawingView != null) {
                mBrushDrawingView.setVisibility(VISIBLE);
                mBrushDrawingView.setBrushDrawingMode(true);
            }
            if (!finalPath.isEmpty()) {
                G.imageLoader.displayImage(AndroidUtils.suitablePath(finalPath), bitmapHolderImageView);
            }
        } else {
            if (!finalPath.isEmpty()) {
                G.imageLoader.displayImage(AndroidUtils.suitablePath(finalPath), bitmapHolderImageView);
            }
            if (mBrushDrawingView != null) {
                mBrushDrawingView.setBrushDrawingMode(false);
            }

        }
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

    public ImageFilterView getmImageFilterView() {
        return mImageFilterView;
    }

    public BrushDrawingView getmBrushDrawingView() {
        return mBrushDrawingView;
    }

    void setFilterEffect(PhotoFilter filterType) {
        mImageFilterView.setVisibility(VISIBLE);
        mImageFilterView.setSourceBitmap(bitmapHolderImageView.getBitmap());
        mImageFilterView.setFilterEffect(filterType);
    }

    void setFilterEffect(CustomEffect customEffect) {
        mImageFilterView.setVisibility(VISIBLE);
        mImageFilterView.setSourceBitmap(bitmapHolderImageView.getBitmap());
        mImageFilterView.setFilterEffect(customEffect);
    }
}
