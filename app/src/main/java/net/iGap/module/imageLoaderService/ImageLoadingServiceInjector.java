package net.iGap.module.imageLoaderService;

public class ImageLoadingServiceInjector {
    private static ImageLoaderService imageLoadingService;

    public static ImageLoaderService inject() {
        if (imageLoadingService == null) {
            imageLoadingService = new GlideImageLoader();
        }
        return imageLoadingService;
    }
}
