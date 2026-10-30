package com.example.visionia.shriavis.efectos

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

class VerificationCircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    enum class Estado { ANALIZANDO, CORRECTO, INCORRECTO }

    private var estado = Estado.ANALIZANDO
    private var sweepAngle = 0f
    private var animator: ValueAnimator? = null
    private val rectF = RectF()

    private val paintCirculo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
    }

    private val paintFondo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        color = Color.parseColor("#33FFFFFF")
    }

    private val paintIcono = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        startAnalizando()
    }

    fun startAnalizando() {
        estado = Estado.ANALIZANDO
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 1200
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                sweepAngle = it.animatedValue as Float
                invalidate()
            }
        }
        animator?.start()
    }

    fun setCorrecto() {
        estado = Estado.CORRECTO
        animator?.cancel()
        invalidate()
    }

    fun setIncorrecto() {
        estado = Estado.INCORRECTO
        animator?.cancel()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = 16f
        rectF.set(padding, padding, w - padding, h - padding)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val radius = (width / 2f) - 20f

        when (estado) {
            Estado.ANALIZANDO -> {
                canvas.drawCircle(cx, cy, radius, paintFondo)
                paintCirculo.color = Color.parseColor("#00B4D8")
                canvas.drawArc(rectF, sweepAngle, 240f, false, paintCirculo)
            }

            Estado.CORRECTO -> {
                paintCirculo.style = Paint.Style.STROKE
                paintCirculo.color = Color.parseColor("#4CAF50")
                canvas.drawCircle(cx, cy, radius, paintCirculo)
                paintIcono.color = Color.parseColor("#4CAF50")
                val path = Path()
                path.moveTo(cx - radius * 0.4f, cy)
                path.lineTo(cx - radius * 0.1f, cy + radius * 0.35f)
                path.lineTo(cx + radius * 0.45f, cy - radius * 0.35f)
                canvas.drawPath(path, paintIcono)
            }

            Estado.INCORRECTO -> {
                paintCirculo.style = Paint.Style.STROKE
                paintCirculo.color = Color.parseColor("#F44336")
                canvas.drawCircle(cx, cy, radius, paintCirculo)
                paintIcono.color = Color.parseColor("#F44336")
                canvas.drawLine(cx - radius * 0.4f, cy - radius * 0.4f,
                    cx + radius * 0.4f, cy + radius * 0.4f, paintIcono)
                canvas.drawLine(cx + radius * 0.4f, cy - radius * 0.4f,
                    cx - radius * 0.4f, cy + radius * 0.4f, paintIcono)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}