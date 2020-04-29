package net.iGap.libs.photoEdit;

import android.graphics.Bitmap;

/**
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @since 8/8/2018
 * Builder Class to apply multiple save options
 */
public class SaveSettings {
    private boolean isTransparencyEnabled;
    private boolean isClearViewsEnabled;
    private Bitmap.CompressFormat compressFormat;
    private int compressQuality;

    boolean isTransparencyEnabled() {
        return isTransparencyEnabled;
    }

    boolean isClearViewsEnabled() {
        return isClearViewsEnabled;
    }

    Bitmap.CompressFormat getCompressFormat() {
        return compressFormat;
    }

    int getCompressQuality() {
        return compressQuality;
    }

    private SaveSettings(Builder builder) {
        this.isClearViewsEnabled = builder.isClearViewsEnabled;
        this.isTransparencyEnabled = builder.isTransparencyEnabled;
        this.compressFormat = builder.compressFormat;
        this.compressQuality = builder.compressQuality;
    }

    public static class Builder {
        private boolean isTransparencyEnabled = true;
        private boolean isClearViewsEnabled = true;
        private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
        private int compressQuality = 100;

        /**
         * Define a flag to enable transparency while saving image
         *
         * @param transparencyEnabled true if enabled
         * @return Builder
         * @see BitmapUtil#removeTransparency(Bitmap)
         */
        public Builder setTransparencyEnabled(boolean transparencyEnabled) {
            isTransparencyEnabled = transparencyEnabled;
            return this;
        }


        public SaveSettings build() {
            return new SaveSettings(this);
        }
    }
}
