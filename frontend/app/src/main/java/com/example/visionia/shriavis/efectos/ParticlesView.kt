package com.example.visionia.shriavis.efectos

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import kotlin.math.*
import kotlin.random.Random

class ParticlesView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private data class Particle(
        var x: Float, var y: Float,
        var vx: Float, var vy: Float
    )

    private val particles = mutableListOf<Particle>()
    private val particleCount = 40
    private val maxDistance = 180f

    private val paintDot = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF00B4D8.toInt()
        style = Paint.Style.FILL
    }

    private val paintLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 16
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            updateParticles()
            invalidate()
        }
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        animator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        particles.clear()
        repeat(particleCount) {
            particles.add(
                Particle(
                    x = Random.nextFloat() * w,
                    y = Random.nextFloat() * h,
                    vx = (Random.nextFloat() - 0.5f) * 1.2f,
                    vy = (Random.nextFloat() - 0.5f) * 1.2f
                )
            )
        }
    }

    private fun updateParticles() {
        val w = width.toFloat()
        val h = height.toFloat()
        for (p in particles) {
            p.x += p.vx
            p.y += p.vy
            if (p.x < 0 || p.x > w) p.vx = -p.vx
            if (p.y < 0 || p.y > h) p.vy = -p.vy
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in particles.indices) {
            for (j in i + 1 until particles.size) {
                val dx = particles[i].x - particles[j].x
                val dy = particles[i].y - particles[j].y
                val dist = sqrt(dx * dx + dy * dy)
                if (dist < maxDistance) {
                    val alpha = ((1 - dist / maxDistance) * 180).toInt()
                    paintLine.color = Color.argb(alpha, 0, 180, 216)
                    canvas.drawLine(
                        particles[i].x, particles[i].y,
                        particles[j].x, particles[j].y,
                        paintLine
                    )
                }
            }
        }
        for (p in particles) {
            canvas.drawCircle(p.x, p.y, 4f, paintDot)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
    }
}