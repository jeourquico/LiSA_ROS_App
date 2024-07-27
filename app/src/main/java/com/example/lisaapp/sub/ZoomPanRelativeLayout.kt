package com.example.lisaapp.sub

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout

class ZoomPanRelativeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var scaleFactor = 1.0f
    private val scaleGestureDetector: ScaleGestureDetector
    private val gestureDetector: GestureDetector

    init {
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
        gestureDetector = GestureDetector(context, GestureListener())
        setLayerType(LAYER_TYPE_HARDWARE, null) // Ensure hardware acceleration
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f))
            scaleX = scaleFactor
            scaleY = scaleFactor
            return true
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            translationX -= distanceX
            translationY -= distanceY
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            smoothResetView()
            return true
        }
    }

    private fun smoothResetView() {
        // Use ViewPropertyAnimator for smooth reset
        animate().apply {
            scaleX(1.0f)
            scaleY(1.0f)
            translationX(0f)
            translationY(0f)
            interpolator = AccelerateDecelerateInterpolator() // Smooth transition
            duration = 300 // Duration of the animation
        }.start()
    }
}
