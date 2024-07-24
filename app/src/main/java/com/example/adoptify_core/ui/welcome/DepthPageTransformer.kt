package com.example.adoptify_core.ui.welcome

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class DepthPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        val pageWidth = view.width

        when {
            position < -1 -> { // Page is way off-screen to the left
                view.alpha = 0f
            }
            position <= 0 -> { // Page is moving out to the left
                view.alpha = 1f
                view.translationX = 0f
                view.translationZ = 0f
                view.scaleX = 1f
                view.scaleY = 1f
            }
            position <= 1 -> { // Page is moving in from the right
                view.alpha = 1 - position
                view.translationX = pageWidth * -position
                view.translationZ = -1f

                val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position))
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor

                // Smooth rotation effect
                view.rotationY = position * -10
            }
            else -> { // Page is way off-screen to the right
                view.alpha = 0f
            }
        }
    }

    companion object {
        private const val MIN_SCALE = 0.85f
    }
}