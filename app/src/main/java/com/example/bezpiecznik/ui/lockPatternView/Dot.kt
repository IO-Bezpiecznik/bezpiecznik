package com.example.bezpiecznik.ui.lockPatternView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.view.View
import kotlin.math.min

class Dot(
    context: Context,
    var index: Int,
    private var normalDotColor: Int,
    private var normalDotRadiusRatio: Float,
    private var selectedDotColor: Int,
    private var selectedDotRadiusRatio: Float,
    private var columnCount: Int,
    private var rowCount: Int
) : View(context) {

    private var dotState: State = State.NORMAL
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var length = MeasureSpec.getSize(widthMeasureSpec) / columnCount
        if(length * rowCount > MeasureSpec.getSize(heightMeasureSpec)){
            length = MeasureSpec.getSize(heightMeasureSpec) / rowCount
        }
        setMeasuredDimension(length, length)
    }

    override fun onDraw(canvas: Canvas?) {
        when(dotState) {
            State.NORMAL -> drawDot(canvas,normalDotColor,normalDotRadiusRatio)
            State.SELECTED -> drawDot(canvas,selectedDotColor,selectedDotRadiusRatio)
        }
    }

    private fun drawDot(
        canvas: Canvas?,
        dotColor: Int,
        radiusRatio: Float
    ){
        val radius = getRadius()
        val centerX = width / 2
        val centerY = height / 2
        paint.color = dotColor
        paint.style = Paint.Style.FILL
        canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(),(radius * radiusRatio),paint)
    }

    private fun getRadius(): Int {
        return (min(width, height) - (paddingStart + paddingEnd)) / 2
    }

    fun getCenter() : Point {
        val point = Point()
        point.y = top + (bottom - top) / 2
        point.x = left + (right - left) / 2
        return point
    }

    fun setState(state: State){
        dotState = state
        invalidate()
    }

    fun reset(){
        setState(State.NORMAL)
    }

}