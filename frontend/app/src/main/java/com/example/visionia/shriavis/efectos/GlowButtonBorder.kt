package com.example.visionia.shriavis.efectos

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

class GlowButtonBorder @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val rect = RectF()
    private val matrix = Matrix()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private var angle = 0f
    private var shader: SweepGradient? = null

    private val animator = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = 2000
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            angle = it.animatedValue as Float
            invalidate()
        }
    }

    init {
        animator.start()
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(4f, 4f, w - 4f, h - 4f)
        shader = SweepGradient(
            w / 2f, h / 2f,
            intArrayOf(
                Color.TRANSPARENT,
                0xFF00B4D8.toInt(),
                Color.WHITE,
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 0.4f, 0.5f, 1f)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val radius = height / 2f
        matrix.setRotate(angle, width / 2f, height / 2f)
        shader?.setLocalMatrix(matrix)
        paint.shader = shader
        canvas.drawRoundRect(rect, radius, radius, paint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
    }
}