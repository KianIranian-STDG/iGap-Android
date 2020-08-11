package net.iGap.module

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import net.iGap.R

class WavesView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = R.attr.wavesViewStyle
) : View(context, attrs, defStyleAttr) {

    private val wavePaint: Paint
    private val waveGap: Float
    private val gradientPaint = Paint(ANTI_ALIAS_FLAG).apply {
        // Highlight only the areas already touched on the canvas
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    // gradient colors
    private val green = Color.GREEN

    // solid green in the center, transparent green at the edges
    private var gradientColors: IntArray

    private var maxRadius = 0f
    private var center = PointF(0f, 0f)
    private var initialRadius = 0f

    private var waveAnimator: ValueAnimator? = null
    private var waveRadiusOffset = 100f
        set(value) {
            field = value
            postInvalidateOnAnimation()
        }

    init {
        val attrs = context.obtainStyledAttributes(attrs, R.styleable.WavesView, defStyleAttr, 0)

        //init paint with custom attrs
        wavePaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = attrs.getColor(R.styleable.WavesView_wavesColor, green)
            strokeWidth = attrs.getDimension(R.styleable.WavesView_waveStrokeWidth, 0f)
            style = Paint.Style.STROKE
        }

        waveGap = attrs.getDimension(R.styleable.WavesView_waveGap, 50f)
        gradientColors = intArrayOf(wavePaint.color, modifyAlpha(wavePaint.color, 0.80f), modifyAlpha(wavePaint.color, 0.1f))
        attrs.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //set the center of all circles to be center of the view
        center.set(w / 2f, h / 2f)
        maxRadius = w / 2f
        initialRadius = /*(w / waveGap) +*/ w / 4f

        //Create gradient after getting sizing information
        gradientPaint.shader = RadialGradient(
                center.x, center.y, w / 2f,
                gradientColors, null, Shader.TileMode.CLAMP
        )
        waveAnimator = ValueAnimator.ofFloat(0f, maxRadius).apply {
            addUpdateListener {
                waveRadiusOffset = it.animatedValue as Float
            }
            duration = 2000L
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Log.wtf(this.javaClass.name, "onAttachedToWindow")
    }

    override fun onDetachedFromWindow() {
        waveAnimator?.cancel()
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var tmp = 0
        //draw circles separated by a space the size of waveGap
        var currentRadius = initialRadius + waveRadiusOffset
        while (currentRadius < maxRadius && tmp < 3) {
            canvas.drawCircle(center.x, center.y, currentRadius, wavePaint)
            currentRadius += waveGap
            tmp++
        }
        canvas.drawPaint(gradientPaint)
    }

    private fun modifyAlpha(color: Int, alpha: Float): Int {
        return color and 0x00ffffff or ((alpha * 255).toInt() shl 24)
    }

    public fun stopAnimation() {
        waveAnimator?.repeatCount = 0
    }
}