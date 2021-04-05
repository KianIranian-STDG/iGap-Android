package net.iGap.libs.photoEdit;

/**
 * Created on 1/17/2018.
 *
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * <p></p>
 */

public interface BrushViewChangeListener {
    void onViewAdd(BrushDrawingView brushDrawingView);

    void onViewRemoved(BrushDrawingView brushDrawingView);

    void onStartDrawing();

    void onStopDrawing();
}
