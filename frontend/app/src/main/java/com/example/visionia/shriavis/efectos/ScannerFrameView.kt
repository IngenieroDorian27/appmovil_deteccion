package com.example.visionia.shriavis.efectos
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

class ScannerFrameView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var scanLineY = 0f
    private var animator: ValueAnimator? = null
    private val shader get() = LinearGradient(
        0f, scanLineY, width.toFloat(), scanLineY,
        intArrayOf(Color.TRANSPARENT, 0xFF00B4D8.toInt(), Color.TRANSPARENT),
        floatArrayOf(0f, 0.5f, 1f),
        Shader.TileMode.CLAMP
    )

    private val paintEsquina = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF00B4D8.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 6f
        strokeCap = Paint.Cap.ROUND
    }

    private val paintLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scanLineY = 0f
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, h.toFloat()).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                scanLineY = it.animatedValue as Float
                invalidate()
            }
        }
        animator?.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val esquina = 40f

        // Esquina superior izquierda
        canvas.drawLine(0f, 0f, esquina, 0f, paintEsquina)
        canvas.drawLine(0f, 0f, 0f, esquina, paintEsquina)

        // Esquina superior derecha
        canvas.drawLine(w - esquina, 0f, w, 0f, paintEsquina)
        canvas.drawLine(w, 0f, w, esquina, paintEsquina)

        // Esquina inferior izquierda
        canvas.drawLine(0f, h - esquina, 0f, h, paintEsquina)
        canvas.drawLine(0f, h, esquina, h, paintEsquina)

        // Esquina inferior derecha
        canvas.drawLine(w - esquina, h, w, h, paintEsquina)
        canvas.drawLine(w, h - esquina, w, h, paintEsquina)

        // Línea de escaneo
        paintLinea.shader = shader
        canvas.drawLine(0f, scanLineY, w, scanLineY, paintLinea)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}