package com.example.visionia.shriavis.efectos

import android.view.View

object EfectoNavCaptura {

    fun aplicar(
        view: View,
        accionFinal: (() -> Unit)? = null
    ) {
        view.animate()
            .scaleX(1.20f)
            .scaleY(1.20f)
            .setDuration(100)
            .withEndAction {
                view.postDelayed({
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction {
                            accionFinal?.invoke()
                        }
                        .start()
                }, 100)
            }
    }
}