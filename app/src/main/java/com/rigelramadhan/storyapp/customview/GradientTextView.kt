package com.rigelramadhan.storyapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.rigelramadhan.storyapp.R

class GradientTextView : AppCompatTextView {

    private var colorStart: Int = 0
    private var colorEnd: Int = 0
    private var width: Float = 0f
    private lateinit var gradient: LinearGradient

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    private fun init() {
        colorStart = ContextCompat.getColor(context, R.color.blue)
        colorEnd = ContextCompat.getColor(context, R.color.turquoise)
        width = this.paint.measureText(text.toString())
        gradient = LinearGradient(
            0f,
            0f,
            width,
            0f,
            intArrayOf(colorStart, colorEnd),
            null,
            Shader.TileMode.CLAMP
        )
        this.paint.shader = gradient
    }
}