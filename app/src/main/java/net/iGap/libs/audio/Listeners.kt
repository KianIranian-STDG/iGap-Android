package net.iGap.libs.audio

/**
 * Created by alex
 */

interface OnSamplingListener {
    fun onComplete()
}

interface OnProgressListener {
    fun onStartTracking(progress: Float)
    fun onStopTracking(progress: Float)
    fun onProgressChanged(progress: Float, byUser: Boolean)
}