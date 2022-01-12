package com.example.bezpiecznik.ui.lockPatternView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.bezpiecznik.R
import com.example.bezpiecznik.ui.home.HomeViewModel

class LockPatternView(context: Context, attributeSet: AttributeSet) :
    GridLayout(context, attributeSet) {

    companion object{
        const val DEFAULT_RADIUS_RATIO = 0.25f
        const val DEFAULT_LINE_WIDTH = 2f // dp
        const val DEFAULT_SPACING = 6f // dp
        const val DEFAULT_ROW_AMOUNT = 3
        const val DEFAULT_COLUMN_AMOUNT = 3
        const val DEFAULT_HIT_AREA_PADDING_RATIO = 0.14f
    }

    private var normalDotColor: Int = 0
    private var normalDotRadiusRatio: Float = 0f

    private var selectedDotColor: Int = 0
    private var selectedDotRadiusRatio: Float = 0f

    private var emptyDotColor: Int = 0

    private var lineWidth: Int = 0
    private var normalLineColor: Int = 0

    private var spacing: Int = 0

    private var rowAmount: Int = 0
    private var colAmount: Int = 0

    private var hitAreaPaddingRatio: Float = 0f

    private var dots: ArrayList<Dot> = ArrayList()
    private var selectedDots: ArrayList<Dot> = ArrayList()

    private var linePaint: Paint = Paint()
    private var linePath: Path = Path()

    private var lastX: Float = 0f
    private var lastY: Float = 0f

    private var onPatternListener: OnPatternListener? = null

    var listOfPoints: List<List<Int>> = listOf(
        listOf(1,1,1),
        listOf(1,1,1),
        listOf(1,1,1)
    )
    private var allLines: MutableList<PatternLine> = mutableListOf()
    private var isFinished: Boolean = false

    init {
        normalDotColor = ContextCompat.getColor(context, R.color.normalColor)
        normalDotRadiusRatio = DEFAULT_RADIUS_RATIO
        selectedDotColor = ContextCompat.getColor(context, R.color.selectedColor)
        selectedDotRadiusRatio = DEFAULT_RADIUS_RATIO
        emptyDotColor = ContextCompat.getColor(context,R.color.emptyColor)
        lineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_LINE_WIDTH, context.resources.displayMetrics).toInt()
        normalLineColor = ContextCompat.getColor(context, R.color.selectedColor)
        spacing = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_SPACING, context.resources.displayMetrics).toInt()
        rowAmount =  DEFAULT_ROW_AMOUNT
        colAmount = DEFAULT_COLUMN_AMOUNT
        hitAreaPaddingRatio =  DEFAULT_HIT_AREA_PADDING_RATIO
        rowCount = rowAmount
        columnCount = colAmount
        placeDots()
        initPathPaint()
    }

    private fun placeDots(){
        for(i in 0 until rowCount) {
            for(j in 0 until columnCount){
                if(listOfPoints[i][j] == 0){
                    val dot = Dot(context, i * columnCount + j,
                        emptyDotColor, normalDotRadiusRatio,
                        selectedDotColor, selectedDotRadiusRatio, columnCount)
                    val padding = spacing / 2
                    dot.setPadding(padding, padding, padding, padding)
                    addView(dot)
                } else {
                    val dot = Dot(context, i * columnCount + j,
                        normalDotColor, normalDotRadiusRatio,
                        selectedDotColor, selectedDotRadiusRatio, columnCount)
                    val padding = spacing / 2
                    dot.setPadding(padding, padding, padding, padding)
                    addView(dot)
                    dots.add(dot)
                }
            }
        }
    }

    private fun initPathPaint() {
        linePaint.isAntiAlias = true
        linePaint.isDither = true
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeJoin = Paint.Join.ROUND
        linePaint.strokeCap = Paint.Cap.ROUND

        linePaint.strokeWidth = lineWidth.toFloat()
        linePaint.color = normalLineColor
    }

    private fun isSelected(view: View, x: Int, y: Int): Boolean{
        val innerPadding = view.width * hitAreaPaddingRatio
        return x >= view.left + innerPadding &&
                x <= view.right - innerPadding &&
                y >= view.top + innerPadding &&
                y <= view.bottom - innerPadding
    }

    private fun getHitDot(x: Int, y: Int): Dot? {
        for(dot in dots) {
            if (isSelected(dot, x, y)) {
                return dot
            }
        }
        return null
    }

    private fun notifyDotSelected(dot: Dot){
        selectedDots.add(dot)
        onPatternListener?.onProgress(generateSelectedIds())
        dot.setState(State.SELECTED)
        val center = dot.getCenter()
        if(selectedDots.count() == 1){
            linePath.moveTo(center.x.toFloat(), center.y.toFloat())
        } else {
            linePath.lineTo(center.x.toFloat(),center.y.toFloat())
            val startDot: Dot = selectedDots[selectedDots.count() - 2]
            val startPoint = MatrixPoint(startDot.index % columnCount, startDot.index / rowCount)
            val endPoint = MatrixPoint(dot.index % columnCount, dot.index / rowCount)
            val newLine = PatternLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y)
            allLines.add(newLine)
        }
    }

    private fun generateSelectedIds(): ArrayList<Int> {
        val ids = ArrayList<Int>()
        for(dot in selectedDots){
            ids.add(dot.index)
        }
        return ids
    }

    fun setOnPatternListener(listener: OnPatternListener){
        onPatternListener = listener
    }

    fun setSize(height: Int, width: Int){
        removeAllViews()
        rowCount = height
        columnCount = width
        placeDots()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.drawPath(linePath,linePaint)
        if(selectedDots.count() > 0 && lastX > 0f && lastY > 0f){
            val center = selectedDots[selectedDots.count() - 1].getCenter()
            canvas?.drawLine(center.x.toFloat(),center.y.toFloat(),lastX,lastY,linePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if(!isFinished){
                    val hitDot = getHitDot(event.x.toInt(), event.y.toInt())
                    if (hitDot == null) {
                        return false
                    } else {
                        onPatternListener?.onStarted()
                        notifyDotSelected(hitDot)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> handleActionMove(event)

            MotionEvent.ACTION_UP -> onFinish()

            MotionEvent.ACTION_CANCEL -> reset()

            else -> return false
        }
        return true
    }

    private fun onFinish() {
        lastX = 0f
        lastY = 0f
        isFinished = true
        invalidate()
        Toast.makeText(context, generateSelectedIds().toString(), Toast.LENGTH_SHORT).show()
    }

    fun reset() {
        for(dot in selectedDots){
            dot.reset()
        }
        selectedDots.clear()
        linePaint.color = normalLineColor
        linePath.reset()
        lastX = 0f
        lastY = 0f
        isFinished = false
        allLines.clear()
        invalidate()
    }

    private fun handleActionMove(event: MotionEvent) {
        if(!isFinished){
            val hitDot = getHitDot(event.x.toInt(),event.y.toInt())
            if(hitDot != null){
                if(!selectedDots.contains(hitDot)){
                    notifyDotSelected(hitDot)
                }
            }
            lastX = event.x
            lastY = event.y
            invalidate()
        }

    }

    interface OnPatternListener {
        fun onStarted(){}
        fun onProgress(ids: java.util.ArrayList<Int>){}
        fun onComplete(ids: java.util.ArrayList<Int>) : Boolean
    }
}